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
package com.chessix.vas.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    Long countByClas(CLAS clas);

    Page<Account> findByClas(CLAS clas, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account findByClasAndExternalId(final CLAS clas, final String externalId);

    @Modifying
    @Transactional
    @Query(value = "delete from com.chessix.vas.db.Account where clas = ?1")
    void deleteClasAccounts(final CLAS clas);
}
