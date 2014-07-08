package com.chessix.vas.service;

import java.util.List;
import java.util.Set;

/**
 * @author Mark Wigmans
 */
public interface ISpeedStorage {
    /**
     * Get the value for the given {@code classId} and {@code accountId}.
     */
    String get(String clasId, String accountId);

    List<String> values(String clasId);

    Set<String> keys(String clasId);

    /**
     * Number of records of given {@code classId}.
     */
    Long size(String clasId);

    Long increment(String clasId, String accountId, long value);

    Boolean putIfAbsent(String clasId, String accountId, String value);
    void delete(String clasId, String... accountIds);
}
