package com.chessix.vas.db;

import com.chessix.vas.actors.messages.JournalMessage.AccountCreated;
import com.chessix.vas.actors.messages.JournalMessage.ClasCreated;
import com.chessix.vas.actors.messages.JournalMessage.Clean;
import com.chessix.vas.actors.messages.JournalMessage.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to do the actual RDBMS actions.
 *
 * @author Mark Wigmans
 */
@Service
@Transactional
@Slf4j
public class DBService {

    private final CLASRepository clasRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DBService(final CLASRepository clasRepository, final AccountRepository accountRepository,
                     final TransactionRepository transactionRepository) {
        super();
        this.clasRepository = clasRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void createClas(final ClasCreated message) {
        log.debug("createClas({})", message);
        clasRepository.save(new CLAS(message.getClasId()));
    }

    public void createAccount(final AccountCreated message) {
        log.debug("createAccount({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        accountRepository.save(new Account(clas, message.getAccountId(), 0));
    }

    public void createTransfer(final Transfer message) {
        log.debug("createTransfer({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        final Account from = accountRepository.findByClasAndExternalId(clas, message.getFromAccountId());
        final Account to = accountRepository.findByClasAndExternalId(clas, message.getToAccountId());
        transactionRepository.save(new Transaction(clas, from, to, message.getAmount(), message.getTimestamp()));
        from.setBalance(from.getBalance() - message.getAmount());
        to.setBalance(to.getBalance() + message.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);
    }

    public void clean(final Clean message) {
        log.debug("clean({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        if (clas != null) {
            // there is data
            transactionRepository.deleteClasTransactions(clas);
            accountRepository.deleteClasAccounts(clas);
            clasRepository.delete(clas);
        }
    }

    @Transactional(readOnly = true)
    public Page<Account> findAccountsByClas(final String clasId, final PageRequest pageRequest) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return accountRepository.findByClas(clas, pageRequest);
    }

    @Transactional(readOnly = true)
    public Account findAccount(final String clasId, final String accountId) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return accountRepository.findByClasAndExternalId(clas, accountId);
    }

    @Transactional(readOnly = true)
    public Long count(final String clasId) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return accountRepository.countByClas(clas);
    }
}
