package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ValidateTest {
    @Test
    public void equalsContractValidateRequest() {
        EqualsVerifier.forClass(Validate.Request.class).verify();
    }

    @Test
    public void equalsContractValidateResponset() {
        EqualsVerifier.forClass(Validate.Response.class).verify();
    }
}
