package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class CreateAccountTest {

    @Test
    public void equalsContractCreateAccountRequest() {
        EqualsVerifier.forClass(CreateAccount.Request.class).verify();
    }

    @Test
    public void equalsContractCreateAccountResponse() {
        EqualsVerifier.forClass(CreateAccount.Response.class).verify();
    }
}
