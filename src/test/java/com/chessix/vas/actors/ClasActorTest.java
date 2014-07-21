package com.chessix.vas.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ClasActorTest {

    private final ActorSystem system = ActorSystem.create("VAS-Test-System");

    @Test
    public void testProps() {
        final ActorRef journalActor = Mockito.mock(ActorRef.class);
        final Props props = ClasActor.props("123", 20, journalActor, null);
        final TestActorRef<ClasActor> ref = TestActorRef.create(system, props, "Clas-1");
        Assert.assertNotNull(ref);
    }

}
