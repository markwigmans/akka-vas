package com.chessix.vas.service;

import java.util.concurrent.ConcurrentMap;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.chessix.vas.actors.ClasActor;
import com.chessix.vas.actors.JournalActor;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.DBService;
import com.google.common.collect.Maps;

@Service
@Slf4j
public class ClasService {

    private final ActorSystem actorSystem;
    private final StringRedisTemplate redisTemplate;
    private final ActorRef journalActor;
    private final ConcurrentMap<String, ActorRef> clasManager;

    private final int accountLength = 20;

    /**
     * Auto wired constructor
     */
    @Autowired
    public ClasService(final ActorSystem actorSystem, final StringRedisTemplate redisTemplate, final DBService dbService) {
        super();
        this.actorSystem = actorSystem;
        this.redisTemplate = redisTemplate;
        this.journalActor = actorSystem.actorOf(JournalActor.props(dbService), "Journalizer");
        this.clasManager = Maps.newConcurrentMap();
    }

    /**
     * Create clas.
     * 
     * @param clasId
     *            clas ID
     * @return {@code true} if clas is created, {@code false} otherwise.
     */
    public boolean create(final String clasId) {
        log.debug("create({})", clasId);
        val clasName = getClasId(clasId);
        val ops = redisTemplate.boundHashOps(clasName);
        if (getClas(clasName) == null || ops.size() == 0) {
            log.debug("create({}) : newly", clasId);
            val clas = getClas(clasName) != null ? getClas(clasName) : actorSystem.actorOf(
                    ClasActor.props(clasName, accountLength, journalActor, redisTemplate), clasActorName(clasName));
            journalActor.tell(new JournalMessage.ClasCreated(clasName), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(ClasActor.NOSTRO), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(ClasActor.OUTBOUND), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(ClasActor.EXCEPTION), ActorRef.noSender());
            log.info("CLAS created: {}", clas);
            clasManager.putIfAbsent(clasName, clas);
            return true;
        }
        // clas is already created
        log.debug("create({}) : already there", clasId);
        return false;
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
        val clasName = getClasId(clasId);
        if (!clasManager.containsKey(clasName)) {
            ActorRef clas = null;
            // make sure that only 1 thread creates the clas actor.
            synchronized (this.clasManager) {
                if (!clasManager.containsKey(clasName)) {
                    val ops = redisTemplate.boundHashOps(clasName);
                    log.debug("getClas({}) : in clas manager, size: {}", clasId, ops.size());
                    if (ops.size() > 0) {
                        log.debug("getClas({}) : create clas actor", clasId);
                        clas = actorSystem.actorOf(ClasActor.props(clasName, accountLength, journalActor, redisTemplate),
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
