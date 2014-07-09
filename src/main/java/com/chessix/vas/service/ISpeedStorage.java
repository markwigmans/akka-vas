package com.chessix.vas.service;

import java.util.List;

/**
 * @author Mark Wigmans
 */
public interface ISpeedStorage {
    /**
     * Get the value for the given {@code classId} and {@code accountId}.
     */
    String get(String clasId, String accountId);

    List<String> accountValues(String clasId);

    List<String> accountIds(String clasId);

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
     * Create account
     *
     * @param clasId    ID of CLAS
     * @param accountId external account ID
     * @return {@code true} if account is created
     */
    boolean create(String clasId, String accountId);

    /**
     * Delete 1 of more accounts.
     *
     * @param clasId     ID of CLAS
     * @param accountIds list of accounts to be removed.
     */
    void delete(String clasId, String... accountIds);
}
