/******************************************************************************
 Copyright 2014 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/
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
