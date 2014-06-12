package com.chessix.vas.service;

import java.util.concurrent.ConcurrentMap;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.chessix.vas.actors.ClasActor;
import com.chessix.vas.actors.JournalActor;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import com.google.common.collect.Maps;

@Service
@Slf4j
public class ClasService {

    private final ActorSystem system;
    private final StringRedisTemplate redisTemplate;
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
    public ClasService(final ActorSystem system, final StringRedisTemplate redisTemplate, final DBService dbService) {
        super();
        this.system = system;
        this.redisTemplate = redisTemplate;
        this.dbService = dbService;
        this.journalActor = system.actorOf(JournalActor.props(dbService), "Journalizer");
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
            val clas = getClas(clasName) != null ? getClas(clasName) : system.actorOf(
                    ClasActor.props(clasName, accountLength, journalActor, redisTemplate), clasActorName(clasName));
            journalActor.tell(new JournalMessage.ClasCreated(clasName), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(NOSTRO), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(OUTBOUND), ActorRef.noSender());
            clas.tell(new CreateAccount.Request(EXCEPTION), ActorRef.noSender());
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
     * 
     * @param clasId
     * @return
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
                        clas = system.actorOf(ClasActor.props(clasName, accountLength, journalActor, redisTemplate),
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
