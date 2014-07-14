package com.chessix.vas.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    Long countByClas(CLAS clas);

    Page<Account> findByClas(CLAS clas, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account findByClasAndExternalId(final CLAS clas, final String externalId);

    @Modifying
    @Transactional
    @Query(value = "delete from com.chessix.vas.db.Account where clas = ?1")
    void deleteClasAccounts(final CLAS clas);
}
