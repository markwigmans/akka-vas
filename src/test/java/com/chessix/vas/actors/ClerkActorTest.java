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
package com.chessix.vas.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.actors.messages.CreateAccount.Response;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.db.DBService;
import com.chessix.vas.service.ISpeedStorage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mockito;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class ClerkActorTest {

    private ActorSystem system;

    @Before
    public void init() {
        system = ActorSystem.create("VAS-Test-System");
    }

    @After
    public void clean() {
        if (system != null) {
            system.shutdown();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Ignore
    public void testCreateAccount() throws Exception {
        // Given
        final String clasId = "123";
        final String accountId = "456";
        final ISpeedStorage storage = Mockito.mock(ISpeedStorage.class);
        final DBService dbService = Mockito.mock(DBService.class);

        final Props journalProps = JournalActor.props(dbService);
        final TestActorRef<ClerkActor> journalRef = TestActorRef.create(system, journalProps, "Journal");

        final Props clerkProps = ClerkActor.props(clasId, 20, journalRef, storage);
        final TestActorRef<ClerkActor> clerkRef = TestActorRef.create(system, clerkProps, "clerk-1");

        // When
        Mockito.when(storage.create(Mockito.eq(clasId), Mockito.eq(accountId))).thenReturn(Boolean.TRUE);

        // Then
        final Future<Object> future = akka.pattern.Patterns.ask(clerkRef,
                new CreateAccount.RequestBuilder(clasId).accountId(accountId).build(), 1000);
        final CreateAccount.Response response = (Response) Await.result(future, Duration.Zero());
        Assert.assertEquals(accountId, response.getAccountId());
        Assert.assertTrue(response.isSuccessful());
        // check if journal is updated
        Mockito.verify(dbService).createAccount(Mockito.any(JournalMessage.AccountCreated.class));
    }

}
