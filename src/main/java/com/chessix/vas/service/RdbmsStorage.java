package com.chessix.vas.service;

import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.Account;
import com.chessix.vas.db.DBService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * RDBMS/JPA storage version of the {@code ISpeedStorage} interface.
 *
 * @author Mark Wigmans
 */
public class RdbmsStorage implements ISpeedStorage {

    private final static int PAGE_SIZE = 1000;

    private final DBService dbService;

    public RdbmsStorage(final DBService dbService) {
        super();
        this.dbService = dbService;
    }

    @Override
    public Integer get(final String clasId, final String accountId) {
        final Account account = dbService.findAccount(clasId, accountId);
        if (account != null) {
            return account.getBalance();
        }
        return null;
    }

    @Override
    public List<Integer> accountValues(final String clasId) {
        final List<Integer> result = Lists.newLinkedList();
        int page = 0;
        Page<Account> accounts;
        do {
            accounts = dbService.findAccountsByClas(clasId, new PageRequest(page, PAGE_SIZE));
            result.addAll(Lists.transform(accounts.getContent(), new Function<Account, Integer>() {

                @Override
                public Integer apply(final Account input) {
                    return input.getBalance();
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
        dbService.createTransfer(new JournalMessage.TransferBuilder(clasId, fromAccountId, toAccountId, value).build());
    }

    @Override
    public boolean create(final String clasId) {
        dbService.createClas(new JournalMessage.ClasCreatedBuilder(clasId).build());
        return true;
    }

    @Override
    public boolean create(final String clasId, final String accountId) {
        dbService.createAccount(new JournalMessage.AccountCreatedBuilder(clasId, accountId).build());
        return true;
    }

    @Override
    public void delete(final String clasId) {
        dbService.clean(new JournalMessage.CleanBuilder(clasId).build());
    }
}
