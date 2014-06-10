package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * 
 * 
 * @author Mark Wigmans
 *
 */
public class Ready {

    @Value
    public static class Request {
    }

    @Value
    public static class Response {
        boolean successful;
        String message;
    }
}
