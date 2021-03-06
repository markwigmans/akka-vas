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
