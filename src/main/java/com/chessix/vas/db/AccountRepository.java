package com.chessix.vas.db;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    Page<Account> findByClas(CLAS clas, Pageable pageable);
    Account findByClasAndExternalId(final CLAS clas, final String externalId);

    @Modifying
    @Transactional
    @Query("delete from Account where clas = ?1")
    void deleteClasAccounts(final CLAS clas);
}
