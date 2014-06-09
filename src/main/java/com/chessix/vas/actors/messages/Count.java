package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Count the number of records
 * 
 * @author Mark Wigmans
 *
 */
public class Count {

    @Value
    public static class Request {
    }

    @Value
    public static class Response {
        boolean successful;
        Long count;
        String message;
    }
}
