package com.chessix.vas.actors.messages;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class JournalMessageTest {

    @Test
    public void equalsContractJournalMessageClasCreated() {
        EqualsVerifier.forClass(JournalMessage.ClasCreated.class).verify();
    }

    @Test
    public void equalsContractJournalMessageAccountCreated() {
        EqualsVerifier.forClass(JournalMessage.AccountCreated.class).verify();
    }

    @Test
    public void equalsContractJournalMessageTransfer() {
        EqualsVerifier.forClass(JournalMessage.Transfer.class).verify();
    }

    @Test
    public void equalsContractJournalMessageClean() {
        EqualsVerifier.forClass(JournalMessage.Clean.class).verify();
    }
}
