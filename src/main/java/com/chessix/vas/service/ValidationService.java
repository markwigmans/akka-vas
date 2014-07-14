package com.chessix.vas.service;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.chessix.vas.actors.messages.Ready;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;

@Service
@Slf4j
public class ValidationService {

    private final static int PAGE_SIZE = 1000;

    private final ISpeedStorage speedStorage;
    private final DBService dbService;
    private final ActorRef batchStorage;

    @Autowired
    public ValidationService(final ClasService clasService, final ISpeedStorage speedStorage, final ActorRef batchStorage, final DBService dbService) {
        super();
        this.speedStorage = speedStorage;
        this.dbService = dbService;
        this.batchStorage = batchStorage;
    }

    public Object prepare(final Timeout timeout) throws Exception {
        return Await.result(Patterns.ask(batchStorage, new Ready.RequestBuilder(true).build(), timeout), timeout.duration());
    }

    /**
     * Validate given clas, if all accounts have the same value in the different
     * layers.
     */
    public boolean validate(final String clasId) {
        int page = 0;
        boolean result = true;
        Page<Account> accounts;
        do {
            log.debug("validate({}) : page: {}", clasId, page);
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            for (final Account account : accounts) {
                final Integer speed = balance(clasId, account.getExternalId());
                final boolean compare = ObjectUtils.compare(speed, account.getBalance()) == 0;
                if (!compare) {
                    log.warn("account {}/{} is out of sync. speed/batch = {}/{}", clasId, account.getExternalId(), speed, account.getBalance());
                }
                result = result && compare;
            }
            page += 1;
        } while (result && accounts.hasNext());
        return result;
    }

    private Integer balance(final String clasId, final String accountId) {
        return speedStorage.get(clasId, accountId);
    }
}
