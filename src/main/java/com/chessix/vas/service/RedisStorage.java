package com.chessix.vas.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Redis storage version of the {@code ISpeedStorage} interface.
 *
 * @author Mark Wigmans
 */
@Component
public class RedisStorage implements ISpeedStorage {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisStorage(final StringRedisTemplate redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(final String clasId, final String accountId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return (String) ops.get(accountId);
    }

    @Override
    public List<String> values(final String clasId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final List<Object> values = ops.values();
        return Lists.transform(values, new Function<Object, String>() {

            @Override
            public String apply(Object input) {
                return (String) input;
            }
        });
    }

    @Override
    public Set<String> keys(final String clasId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final List<Object> values = Lists.newArrayList(ops.keys());
        final List<String> transform = Lists.transform(values, new Function<Object, String>() {

            @Override
            public String apply(Object input) {
                return (String) input;
            }
        });
        return Sets.newHashSet(transform);
    }

    @Override
    public Long size(final String clasId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.size();
    }

    @Override
    public Long increment(final String clasId, final String accountId, final long value) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.increment(accountId, value);
    }

    @Override
    public Boolean putIfAbsent(final String clasId, final String accountId, final String value) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.putIfAbsent(accountId, value);
    }

    @Override
    public void delete(final String clasId, final String... accountIds) {
        redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(final RedisOperations operations) throws DataAccessException {
                final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
                for(final String accountId : accountIds) {
                    ops.delete(accountId);
                }
                return null;
            }
        });
    }
}
