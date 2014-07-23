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

import akka.actor.*;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;
import com.chessix.vas.service.ISpeedStorage;
import scala.concurrent.duration.Duration;

/**
 * @author Mark Wigmans
 */
public class ClasActor extends UntypedActor {

    private static final SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
            new Function<Throwable, Directive>() {
                @Override
                public Directive apply(final Throwable t) throws Exception {
                    return SupervisorStrategy.restart();
                }
            });
    private final ActorRef router;

    private ClasActor(final String clasId, final int accountLength, final ISpeedStorage storage,
                      final ActorRef journalActor) {
        super();
        final DefaultResizer resizer = new DefaultResizer(2, 15);
        this.router = getContext().actorOf(
                new RoundRobinPool(5).withResizer(resizer).withSupervisorStrategy(strategy)
                        .props(ClerkActor.props(clasId, accountLength, journalActor, storage)), "router");
    }

    /**
     * Create Props for an actor of this type.
     */
    public static Props props(final String clasId, final int accountLength, final ActorRef journalActor,
                              final ISpeedStorage storage) {
        return Props.create(ClasActor.class, clasId, accountLength, storage, journalActor);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        router.tell(message, getSender());
    }
}
