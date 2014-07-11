package com.chessix.vas.service;

import java.util.List;

/**
 * Interface to the speed layer of the lambda architecture
 */
public interface ISpeedStorage {
    /**
     * Get the value for the given {@code classId} and {@code accountId}.
     */
    Integer get(String clasId, String accountId);

    /**
     * Get all the balance values for the given {@code classId}.
     */
    List<Integer> accountValues(String clasId);

    /**
     * Number of records of given {@code classId}.
     */
    Long size(String clasId);

    /**
     * Transfer {@code value} amount between given accounts.
     *
     * @param clasId        ID of CLAS
     * @param fromAccountId account that delivers given value
     * @param toAccountId   account that receives given value
     * @param value         value to be transferred.
     */
    void transfer(String clasId, String fromAccountId, String toAccountId, int value);

    /**
     * Create CLAS
     *
     * @param clasId ID of CLAS
     */
    boolean create(String clasId);

    /**
     * Create account
     *
     * @param clasId    ID of CLAS
     * @param accountId external account ID
     * @return {@code true} if account is created
     */
    boolean create(String clasId, String accountId);

    /**
     * Delete CLAS and all related accounts and transactions.
     *
     * @param clasId ID of CLAS
     */
    void delete(String clasId);
}
