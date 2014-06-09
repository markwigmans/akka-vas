package com.chessix.vas.actors.messages;

import lombok.Value;

/**
 * Create a new VAS CLAS
 * 
 * @author Mark Wigmans
 *
 */
public class CreateClas {

    @Value
    public static class Request {
        String clasId;
    }

    @Value
    public static class Response {
    }
}
