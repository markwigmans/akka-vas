package com.chessix.vas.dto;

import lombok.Value;

/**
 * 
 * @author Mark Wigmans
 *
 */
@Value
public class ClasCreated {

    String clasId;
    boolean successful;
    String message;

}
