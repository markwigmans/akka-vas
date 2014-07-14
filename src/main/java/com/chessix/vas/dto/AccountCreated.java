package com.chessix.vas.dto;

import lombok.Value;

/**
 * @author Mark Wigmans
 */
@Value
public class AccountCreated {
    String clasId;
    String accountIds;
    boolean successful;
    String message;

}
