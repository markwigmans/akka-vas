package com.chessix.vas.service;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import scala.concurrent.Await;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.chessix.vas.actors.messages.Ready;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;

@Service
@Slf4j
public class ValidationService {

    private final static int PAGE_SIZE = 1000;

    private final StringRedisTemplate redisTemplate;
    private final DBService dbService;
    private final ActorRef journalActor;

    @Autowired
    public ValidationService(final ClasService clasService, final StringRedisTemplate redisTemplate, final DBService dbService) {
        super();
        this.redisTemplate = redisTemplate;
        this.dbService = dbService;
        this.journalActor = clasService.getJournal();
    }

    public Object prepare(final Timeout timeout) throws Exception {
        return Await.result(Patterns.ask(journalActor, new Ready.Request(), timeout), timeout.duration());
    }

    /**
     * Validate given clas, if all accounts have the same value in the different
     * layers.
     * 
     * @param clasId
     * @return
     */
    public boolean validate(final String clasId) {
        int page = 0;
        boolean result = true;
        Page<Account> accounts;
        do {
            log.debug("validate({}) : page: {}", clasId, page);
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            for (final Account account : accounts) {
                val fast = balance(clasId, account.getExternalId());
                final boolean compare = (fast != null) ? account.getBalance() == fast : false;
                if (!compare) {
                    log.warn("account {}/{} is our of sync", clasId, account.getExternalId());
                }
                result = result && compare;
            }
            page += 1;
        } while (result && accounts.hasNextPage());
        return result;
    }

    private Long balance(final String clasId, final String accountId) {
        val ops = redisTemplate.boundHashOps(clasId);
        val value = (String) ops.get(accountId);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

}