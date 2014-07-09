package com.chessix.vas.dto;

import lombok.Value;

/**
 * @author Mark Wigmans
 */
@Value
public class SaldoDTO {

    String clasId;
    String accountId;
    Integer amount;
    boolean successful;
    String message;

}
