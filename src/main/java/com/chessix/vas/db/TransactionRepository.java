package com.chessix.vas.db;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    @Modifying
    @Transactional
    @Query("delete from Transaction where clas = ?1")
    void deleteClasTransactions(final CLAS clas);
}
