package com.chessix.vas.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public String getAccountId(final String accountRefId) {
        return StringUtils.lowerCase(StringUtils.trimToEmpty(accountRefId));
    }
}
