package com.chessix.vas.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chessix.vas.actors.ClasActor;
import com.chessix.vas.actors.JournalActor;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class ClasService {

    private final ActorSystem system;
    private final ISpeedStorage storage;
    private final DBService dbService;
    private final ActorRef journalActor;
    private final ConcurrentMap<String, ActorRef> clasManager;

    private final int accountLength = 20;
    private final static int PAGE_SIZE = 1000;

    public static final String NOSTRO = "nostro";
    public static final String OUTBOUND = "outbound";
    public static final String EXCEPTION = "exception";

    /**
     * Auto wired constructor
     */
    @Autowired
    public ClasService(final ActorSystem system, final ISpeedStorage storage, final DBService dbService) {
        super();
        this.system = system;
        this.storage = storage;
        this.dbService = dbService;
        this.journalActor = system.actorOf(JournalActor.props(dbService), "Journalizer");
        this.clasManager = Maps.newConcurrentMap();
    }

    /**
     * Create clas.
     *
     * @param clasId clas ID
     * @return {@code true} if clas is created, {@code false} otherwise.
     */
    public boolean create(final String clasId) {
        log.debug("create({})", clasId);
        final String clasName = getClasId(clasId);
        if (getClas(clasName) == null || storage.size(clasName) == 0) {
            log.debug("create({}) : newly", clasId);
            final ActorRef clas = getClas(clasName) != null ? getClas(clasName) : system.actorOf(
                    ClasActor.props(clasName, accountLength, journalActor, storage), clasActorName(clasName));
            journalActor.tell(new JournalMessage.ClasCreated(clasName), ActorRef.noSender());
            clas.tell(new CreateAccount.RequestBuilder(clasId).accountId(NOSTRO).build(), ActorRef.noSender());
            clas.tell(new CreateAccount.RequestBuilder(clasId).accountId(OUTBOUND).build(), ActorRef.noSender());
            clas.tell(new CreateAccount.RequestBuilder(clasId).accountId(EXCEPTION).build(), ActorRef.noSender());
            log.info("CLAS created: {}", clas);
            clasManager.putIfAbsent(clasName, clas);
            return true;
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
            for (final Account account : accounts) {
                total += account.getBalance();
            }
            page += 1;
        } while (accounts.hasNext());
        return total == 0;
    }

    public ActorRef getJournal() {
        return journalActor;
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
            ActorRef clas = null;
            // make sure that only 1 thread creates the clas actor.
            synchronized (this.clasManager) {
                if (!clasManager.containsKey(clasName)) {
                    log.debug("getClas({}) : in clas manager, size: {}", clasId, storage.size(clasId));
                    if (storage.size(clasName) > 0) {
                        log.debug("getClas({}) : create clas actor", clasId);
                        clas = system.actorOf(ClasActor.props(clasName, accountLength, journalActor, storage),
                                clasActorName(clasName));
                        clasManager.putIfAbsent(clasName, clas);
                    } else {
                        log.debug("getClas({}) : return null", clasId);
                    }
                }
            }
            return clas;
        } else {
            log.debug("getClas({}) : return class manager ID", clasId);
            return clasManager.get(clasName);
        }
    }
}
