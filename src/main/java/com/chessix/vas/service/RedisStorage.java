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
package com.chessix.vas.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * Redis storage version of the {@code ISpeedStorage} interface.
 *
 * @author Mark Wigmans
 */
public class RedisStorage implements ISpeedStorage {

    private final StringRedisTemplate redisTemplate;

    public RedisStorage(final StringRedisTemplate redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Integer get(final String clasId, final String accountId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final String value = (String) ops.get(accountId);

        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> accountValues(final String clasId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final List<Object> values = ops.values();
        return Lists.transform(values, new Function<Object, Integer>() {

            @Override
            public Integer apply(final Object input) {
                return Integer.parseInt((String) input);
            }
        });
    }

    @Override
    public Long size(final String clasId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.size();
    }

    @Override
    public void transfer(final String clasId, final String fromAccountId, final String toAccountId, final int value) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        ops.increment(fromAccountId, -value);
        ops.increment(toAccountId, value);
    }

    @Override
    public boolean create(final String clasId) {
        // CLAS is automatically created if account is added
        return true;
    }

    @Override
    public boolean create(final String clasId, final String accountId) {
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.putIfAbsent(accountId, "0");
    }

    @Override
    public void delete(final String clasId) {
        redisTemplate.delete(clasId);
    }
}
