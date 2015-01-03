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
package com.chessix.vas.db;

import com.chessix.vas.actors.messages.Clean;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.actors.messages.CreateClas;
import com.chessix.vas.actors.messages.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service to do the actual RDBMS actions.
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

    public void createClas(final CreateClas.Request message) {
        log.debug("createClas({})", message);
        clasRepository.save(new CLAS(message.getClasId()));
    }

    public void createAccount(final CreateAccount.Request message) {
        log.debug("createAccount({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        accountRepository.save(new Account(clas, message.getAccountId(), 0));
    }

    public void createTransfer(final Transfer.Request message) {
        log.debug("createTransfer({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        final Account from = accountRepository.findByClasAndExternalId(clas, message.getFrom());
        final Account to = accountRepository.findByClasAndExternalId(clas, message.getTo());
        transactionRepository.save(new Transaction(clas, from, to, message.getAmount(), BaseModel.fromLocalDateTime(message.getTimestamp())));
        from.setBalance(from.getBalance() - message.getAmount());
        to.setBalance(to.getBalance() + message.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);
    }

    public void clean(final Clean.Request message) {
        log.debug("starting clean({})", message);
        final CLAS clas = clasRepository.findByExternalId(message.getClasId());
        if (clas != null) {
            // there is data
            log.debug("clean({}) : transactions", message);
            transactionRepository.deleteByClas(clas);
            log.debug("clean({}) : accounts", message);
            accountRepository.deleteByClas(clas);
            log.debug("clean({}) : clas", message);
            clasRepository.delete(clas);
        }
        log.debug("stop clean({})", message);
    }

    @Transactional(readOnly = true)
    public Page<Account> findAccountsByClas(final String clasId, final PageRequest pageRequest) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return accountRepository.findByClasOrderByExternalIdAsc(clas, pageRequest);
    }

    @Transactional(readOnly = true)
    public Optional<Account> findAccount(final String clasId, final String accountId) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return Optional.ofNullable(accountRepository.findByClasAndExternalId(clas, accountId));
    }

    @Transactional(readOnly = true)
    public long count(final String clasId) {
        final CLAS clas = clasRepository.findByExternalId(clasId);
        return accountRepository.countByClas(clas);
    }
}
