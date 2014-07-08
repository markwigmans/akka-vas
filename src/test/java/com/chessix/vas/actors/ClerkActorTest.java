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
import org.junit.Test;
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCreateAccount() throws Exception {
        // Given
        final String clasId = "123";
        final String accountId = "456";
        final ISpeedStorage storage = Mockito.mock(ISpeedStorage.class);
        final DBService dbService = Mockito.mock(DBService.class);

        final Props journalProps = JournalActor.props(dbService);
        final TestActorRef<ClerkActor> journalRef = TestActorRef.create(system, journalProps, "Journal");

        final Props clerkProps = ClerkActor.props(clasId, 20, journalRef, storage);
        final TestActorRef<ClerkActor> cleckRef = TestActorRef.create(system, clerkProps, "clerk-1");

        // When
        Mockito.when(storage.putIfAbsent(Mockito.eq(clasId),Mockito.eq(accountId), Mockito.anyString())).thenReturn(Boolean.TRUE);

        // Then
        final Future<Object> future = akka.pattern.Patterns.ask(cleckRef,
                new CreateAccount.RequestBuilder(clasId).accountId(accountId).build(), 1000);
        final CreateAccount.Response response = (Response) Await.result(future, Duration.Zero());
        Assert.assertEquals(accountId, response.getAccountId());
        Assert.assertTrue(response.isSuccessful());
        // check if journal is updated
        Mockito.verify(dbService).createAccount(Mockito.eq(new JournalMessage.AccountCreated(clasId, accountId)));
    }

}
