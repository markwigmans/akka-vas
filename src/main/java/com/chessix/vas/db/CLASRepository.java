package com.chessix.vas.db;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CLASRepository extends PagingAndSortingRepository<CLAS, Long> {

    CLAS findByExternalId(final String externalId);
}
