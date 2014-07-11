package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class TransferTest {

    @Test
    public void equalsContractTransferRequest() {
        EqualsVerifier.forClass(Transfer.Request.class).verify();
    }

    @Test
    public void equalsContractTransferResponse() {
        EqualsVerifier.forClass(Transfer.Response.class).verify();
    }
}
