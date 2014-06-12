package com.chessix.vas.actors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

public class ClasActorTest {

    private final ActorSystem system = ActorSystem.create("VAS-Test-System");

    @Test
    public void testProps() {
        ActorRef journalActor = Mockito.mock(ActorRef.class);
        final Props props = ClasActor.props("123", 20, journalActor, null);
        final TestActorRef<ClasActor> ref = TestActorRef.create(system, props, "Clas-1");
        Assert.assertNotNull(ref);
    }

}
