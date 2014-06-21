package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class CleanTest {

    @Test
    public void equalsContractCleanRequest() {
        EqualsVerifier.forClass(Clean.Request.class).verify();
    }

    @Test
    public void equalsContractCleanResponset() {
        EqualsVerifier.forClass(Clean.Response.class).verify();
    }
}