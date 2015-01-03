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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Hazelcast storage version of the {@code ISpeedStorage} interface.
 */
@Slf4j
public class HazelcastStorage implements ISpeedStorage {

    private final HazelcastInstance instance;

    /**
     *
     */
    public HazelcastStorage(HazelcastInstance hzInstance) {
        this.instance = hzInstance;
    }

    private IMap<String, Integer> getClas(final String clasId) {
        return instance.getMap(clasId);
    }

    @Override
    public Optional<Integer> get(final String clasId, final String accountId) {
        final Map<String, Integer> clas = getClas(clasId);
        final Integer value = clas.get(accountId);
        return Optional.ofNullable(value);
    }

    @Override
    public Collection<Integer> accountValues(final String clasId) {
        final Map<String, Integer> clas = getClas(clasId);
        return clas.values();
    }

    @Override
    public long size(final String clasId) {
        final Map<String, Integer> clas = getClas(clasId);
        return clas.values().size();
    }

    @Override
    public boolean isEmpty(final String clasId) {
        final Map<String, Integer> clas = getClas(clasId);
        return clas.values().isEmpty();
    }

    @Override
    public void transfer(final String clasId, final String fromAccountId, final String toAccountId, final int value) {
        final Lock lock = instance.getLock(clasId);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    final IMap<String, Integer> clas = getClas(clasId);
                    clas.compute(fromAccountId, (k, v) -> v - value);
                    clas.compute(toAccountId, (k, v) -> v + value);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("Lock couldn't acquired");
            }
        } catch (InterruptedException e) {
            log.info("Interrupted: {}", e.toString());
        }
    }

    @Override
    public boolean create(final String clasId) {
        // access the clas will create it as well.
        final Map<String, Integer> clas = getClas(clasId);
        return true;
    }

    @Override
    public boolean create(final String clasId, final String accountId) {
        final Map<String, Integer> clas = getClas(clasId);
        clas.put(accountId, 0);
        return true;
    }

    @Override
    public void delete(final String clasId) {
        final IMap<String, Integer> clas = getClas(clasId);
        clas.clear();
    }
}
