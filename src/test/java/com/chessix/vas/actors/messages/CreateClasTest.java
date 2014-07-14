package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class CreateClasTest {

    @Test
    public void equalsContractCreateClasRequest() {
        EqualsVerifier.forClass(CreateClas.Request.class).verify();
    }

    @Test
    public void equalsContractCreateClasResponse() {
        EqualsVerifier.forClass(CreateClas.Response.class).verify();
    }
}

