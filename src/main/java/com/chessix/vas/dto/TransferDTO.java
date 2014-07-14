package com.chessix.vas.dto;

import lombok.Value;

/**
 * @author Mark Wigmans
 */
@Value
public class TransferDTO {

    String clasId;
    String from;
    String to;
    boolean successful;
    String message;

}
