package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ReadyTest {
    @Test
    public void equalsContractReadyRequest() {
        EqualsVerifier.forClass(Ready.Request.class).verify();
    }

    @Test
    public void equalsContractReadyResponse() {
        EqualsVerifier.forClass(Ready.Response.class).verify();
    }
}
