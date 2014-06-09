package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Transfer amount between two accounts
 * 
 * @author Mark Wigmans
 *
 */
public class Transfer {

    @Value
    public static class Request {
        String from;
        String to;
        int amount;
    }

    @Value
    public static class Response {
        boolean successful;
        String message;
    }
}
