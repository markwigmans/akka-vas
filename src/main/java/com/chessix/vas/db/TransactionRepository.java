package com.chessix.vas.db;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from  com.chessix.vas.db.Transaction where clas = ?1")
    void deleteClasTransactions(final CLAS clas);
}
