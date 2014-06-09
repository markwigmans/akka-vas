package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Create a new VAS account
 * 
 * @author Mark Wigmans
 *
 */
public class CreateAccount {

    @Value
    public static class Request {
        String accountId;
    }

    @Value
    public static class Response {
        boolean successful;
        String accountId;
        String message;
    }
}
