package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class BalanceTest {

    @Test
    public void equalsContractBalanceRequest() {
        EqualsVerifier.forClass(Balance.Request.class).verify();
    }

    @Test
    public void equalsContractBalanceResponset() {
        EqualsVerifier.forClass(Balance.Response.class).verify();
    }
}
