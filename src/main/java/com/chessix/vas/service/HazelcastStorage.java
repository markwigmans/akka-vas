package com.chessix.vas.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Hazelcast storage version of the {@code ISpeedStorage} interface.
 *
 * @author Mark Wigmans
 */
public class HazelcastStorage implements ISpeedStorage {

    private final HazelcastInstance hazelcastInstance;

    private final MultiMap<String, String> accounts;

    public HazelcastStorage() {
        final Config config = new Config();
        config.setProperty("hazelcast.logging.type", "slf4j");
        config.getGroupConfig().setName("vas").setPassword("vas");
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        accounts = hazelcastInstance.getMultiMap("accounts");
    }

    @Override
    public Integer get(final String clasId, final String accountId) {
        final long value = hazelcastInstance.getAtomicLong(calcKey(clasId, accountId)).get();
        return new Integer((int)value);
    }

    private String calcKey(final String clasId, final String accountId) {
        return strip(clasId) + "::" + strip(accountId);
    }

    private static String strip(final String s) {
        return StringUtils.lowerCase(StringUtils.trimToEmpty(s));
    }

    @Override
    public List<Integer> accountValues(final String clasId) {
         return Lists.transform( new LinkedList(accounts.get(clasId)),  new Function<String,Integer>() {
            @Override
            public Integer apply(final String accountId) {
                return get(clasId, accountId);
            }
        });
    }

    @Override
    public Long size(final String clasId) {
        final Collection<String> ids = accounts.get(clasId);
        if (ids != null) {
            return new Long(ids.size());
        } else {
            return null;
        }
    }

    @Override
    public void transfer(final String clasId, final String fromAccountId, final String toAccountId, final int value) {
        hazelcastInstance.getAtomicLong(calcKey(clasId, fromAccountId)).addAndGet(-value);
        hazelcastInstance.getAtomicLong(calcKey(clasId, toAccountId)).addAndGet(value);
    }

    @Override
    public boolean create(final String clasId) {
        // CLAS is automatically created if account is added
        return true;
    }

    @Override
    public boolean create(final String clasId, final String accountId) {
        hazelcastInstance.getAtomicLong(calcKey(clasId,accountId)).set(0);
        accounts.put(strip(clasId), strip(accountId));
        return true;
    }

    @Override
    public void delete(final String clasId) {
        final Collection<String> accountIds =  accounts.remove(clasId);
        for ( final String accountId :  accountIds) {
            hazelcastInstance.getAtomicLong(calcKey(clasId,accountId)).destroy();
        }
    }
}
