package com.chessix.vas.service;

import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by Mark Wigmans on 9-7-2014.
 */
@Component
public class RdbmsStorage implements ISpeedStorage {

    private final static int PAGE_SIZE = 1000;

    private final DBService dbService;

    @Autowired
    public RdbmsStorage(final DBService dbService) {
        super();
        this.dbService = dbService;
    }

    @Override
    public String get(final String clasId, final String accountId) {
        final Account account = dbService.findAccount(clasId, accountId);
        if (account != null) {
            return Long.toString(account.getBalance());
        }
        return null;
    }

    @Override
    public List<String> accountValues(final String clasId) {
        final List<String> result = Lists.newLinkedList();
        int page = 0;
        Page<Account> accounts;
        do {
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            result.addAll(Lists.transform(accounts.getContent(), new Function<Account, String>() {

                @Override
                public String apply(Account input) {
                    return Long.toString(input.getBalance());
                }
            }));

            page += 1;
        } while (accounts.hasNext());

        return result;
    }

    @Override
    public List<String> accountIds(final String clasId) {
        final List<String> result = Lists.newLinkedList();
        int page = 0;
        Page<Account> accounts;
        do {
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            result.addAll(Lists.transform(accounts.getContent(), new Function<Account, String>() {

                @Override
                public String apply(Account input) {
                    return input.getExternalId();
                }
            }));

            page += 1;
        } while (accounts.hasNext());

        return result;
    }

    @Override
    public Long size(final String clasId) {
        return dbService.count(clasId);
    }

    @Override
    public void transfer(final String clasId, final String fromAccountId, final String toAccountId, final int value) {
        dbService.createTransfer(new JournalMessage.Transfer(clasId, fromAccountId, toAccountId, value, new Date()));
    }

    @Override
    public boolean create(final String clasId, final String accountId) {
        dbService.createAccount(new JournalMessage.AccountCreated(clasId, accountId));
        return true;
    }

    @Override
    public void delete(final String clasId, final String... accountIds) {
        // ignore the account ID's classes are always cleaned entirely.
        dbService.clean(new JournalMessage.Clean(clasId));
    }
}
