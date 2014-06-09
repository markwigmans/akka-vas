package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Transfer amount between two accounts
 * 
 * @author Mark Wigmans
 *
 */
public class Balance {

    @Value
    public static class Request {
        String accountId;
    }

    @Value
    public static class Response {
        boolean successful;
        Integer amount;
        String message;
    }
}
