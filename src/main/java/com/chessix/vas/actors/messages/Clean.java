package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Clean
 * 
 * @author Mark Wigmans
 *
 */
public class Clean {

    @Value
    public static class Request {
    }

    @Value
    public static class Response {
        boolean successful;
        String message;
    }
}
