package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Validate the clas.
 * 
 * @author Mark Wigmans
 *
 */
public class Validate {

    @Value
    public static class Request {
    }

    @Value
    public static class Response {
        boolean successful;
        String message;
    }
}
