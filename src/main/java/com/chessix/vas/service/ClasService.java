/******************************************************************************
 Copyright 2014 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/
package com.chessix.vas.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chessix.vas.actors.ClasActor;
import com.chessix.vas.actors.messages.CreateClas;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;

@Service
@Slf4j
public class ClasService {

    private final static int PAGE_SIZE = 1000;
    private final ActorSystem system;
    private final ISpeedStorage storage;
    private final DBService dbService;
    private final ActorRef journalActor;
    private final ActorRef stormActor;
    private final ConcurrentMap<String, ActorRef> clasManager;
    private final int accountLength = 20;

    /**
     * Auto wired constructor
     */
    @Autowired
    public ClasService(final ActorSystem system, final ISpeedStorage storage, final DBService dbService,
                       final ActorRef journalActor,
                        final ActorRef stormActor) {
        super();
        this.system = system;
        this.storage = storage;
        this.dbService = dbService;
        this.journalActor = journalActor;
        this.stormActor = stormActor;
        this.clasManager = new ConcurrentHashMap<>();
    }

    /**
     * Create clas.
     *
     * @param clasId clas ID
     * @return {@code true} if clas is created, {@code false} otherwise.
     */
    public synchronized boolean create(final String clasId) {
        log.debug("create({})", clasId);
        final String clasName = getClasId(clasId);
        if (storage.isEmpty(clasName)) {
            log.debug("create({}) : newly", clasId);
            journalActor.tell(new JournalMessage.ClasCreatedBuilder(clasName).build(), ActorRef.noSender());

            final ActorRef clas = getClas(clasName);
            final Duration timeout = Duration.create(1, TimeUnit.SECONDS);
            try {
                Await.result(ask(clas, new CreateClas.RequestBuilder(clasId).build(), timeout.toMillis()), timeout);
                log.info("CLAS created: {}", clas);
                clasManager.putIfAbsent(clasName, clas);
                return true;
            } catch (final Exception e) {
                log.error("Exception", e);
                return false;
            }
        }
        // clas is already created
        log.debug("create({}) : already there", clasId);
        return false;
    }

    /**
     * Validate given clas, if all accounts count to 0.
     */
    public boolean validate(final String clasId) {
        int page = 0;
        long total = 0;
        Page<Account> accounts;
        do {
            log.debug("validate({}) : page: {}", clasId, page);
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            total += accounts.getContent().stream().collect(Collectors.summingInt(Account::getBalance));
            page++;
        } while (accounts.hasNext());
        final boolean insync = total == 0;
        if (!insync) {
            log.error("CLAS {} not in sync. total is: {}", clasId, total);
        } else {
            log.debug("CLAS {} in sync.", clasId);
        }
        return insync;
    }

    String getClasId(final String clasId) {
        return StringUtils.lowerCase(StringUtils.trimToEmpty(clasId));
    }

    String clasActorName(final String clasId) {
        return "clas-" + getClasId(clasId);
    }

    public ActorRef getClas(final String clasId) {
        log.debug("getClas({})", clasId);
        final String clasName = getClasId(clasId);
        if (!clasManager.containsKey(clasName)) {
            final ActorRef clas;
            // make sure that only 1 thread creates the clas actor.
            synchronized (this.clasManager) {
                // check again, it might be changed after we get the lock
                if (!clasManager.containsKey(clasName)) {
                    log.debug("getClas({}) : create clas actor", clasId);
                    clas = system.actorOf(ClasActor.props(new ClasActor.ClasActorBuilder(clasName, accountLength)
                                    .setJournalActor(journalActor)
                                    .setStormActor(stormActor)
                                    .setStorage(storage)),
                            clasActorName(clasName));
                    clasManager.putIfAbsent(clasName, clas);
                } else {
                    return clasManager.get(clasName);
                }
            }
            return clas;
        } else {
            log.debug("getClas({}) : return class manager ID", clasId);
            return clasManager.get(clasName);
        }
    }
}
