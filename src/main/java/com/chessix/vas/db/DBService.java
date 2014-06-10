package com.chessix.vas.db;

import org.springframework.transaction.annotation.Transactional;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.chessix.vas.actors.messages.JournalMessage.AccountCreated;
import com.chessix.vas.actors.messages.JournalMessage.ClasCreated;
import com.chessix.vas.actors.messages.JournalMessage.Clean;
import com.chessix.vas.actors.messages.JournalMessage.Transfer;

/**
 * Service to do the actual RDBMS actions.
 * 
 * @author Mark Wigmans
 *
 */
@Service
@Transactional
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
        clasRepository.save(new CLAS(message.getClasId()));
    }

    public void createAccount(final AccountCreated message) {
        val clas = clasRepository.findByExternalId(message.getClasId());
        accountRepository.save(new Account(clas, message.getAccountId()));
    }

    public void createTransfer(final Transfer message) {
        val clas = clasRepository.findByExternalId(message.getClasId());
        val from = accountRepository.findByClasAndExternalId(clas, message.getFromAccountId());
        val to = accountRepository.findByClasAndExternalId(clas, message.getToAccountId());
        transactionRepository.save(new Transaction(clas, from, to, message.getAmount(), message.getDate()));
        from.setBalance(from.getBalance() - message.getAmount());
        to.setBalance(to.getBalance() + message.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);
    }

    public void clean(final Clean message) {
        val clas = clasRepository.findByExternalId(message.getClasId());
        if (clas != null) {
            // there is data
            transactionRepository.deleteClasTransactions(clas);
            accountRepository.deleteClasAccounts(clas);
            clasRepository.delete(clas);
        }
    }

    @Transactional(readOnly = true)
    public Page<Account> findAccountsByClas(String clasId, PageRequest pageRequest) {
        val clas = clasRepository.findByExternalId(clasId);
        return accountRepository.findByClas(clas, pageRequest);
    }

}
