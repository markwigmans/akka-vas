package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class CountTest {

    @Test
    public void equalsContractCountRequest() {
        EqualsVerifier.forClass(Count.Request.class).verify();
    }

    @Test
    public void equalsContractCountResponse() {
        EqualsVerifier.forClass(Count.Response.class).verify();
    }
}
