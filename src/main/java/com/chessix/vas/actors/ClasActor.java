/******************************************************************************
 Copyright 2014,2015 Mark Wigmans

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
import akka.routing.SmallestMailboxPool;
import com.chessix.vas.service.ISpeedStorage;
import scala.concurrent.duration.Duration;

/**
 *
 */
public class ClasActor extends UntypedActor {

    private static int MAX_RETRIES = 10;

    private static final SupervisorStrategy strategy = new OneForOneStrategy(MAX_RETRIES, Duration.create("1 minute"),
            new Function<Throwable, Directive>() {
                @Override
                public Directive apply(final Throwable t) throws Exception {
                    return SupervisorStrategy.restart();
                }
            });

    private final ActorRef router;
    private final ActorRef stormActor;

    /**
     *
     */
    private ClasActor(final String clasId, final int accountLength, final ISpeedStorage storage,
                      final ActorRef journalActor, final ActorRef stormActor,
                      final int poolLowerSize, final int poolUpperSize) {
        final DefaultResizer resizer = new DefaultResizer(poolLowerSize, poolUpperSize);
        this.router = getContext().actorOf(
                new SmallestMailboxPool(poolLowerSize).withResizer(resizer).withSupervisorStrategy(strategy)
                        .props(ClerkActor.props(clasId, accountLength, journalActor, storage)), "clas-router");
        this.stormActor = stormActor;
    }

    /**
     * Create Props for an actor of this type.
     */
    public static Props props(final ClasActorBuilder builder) {
        return Props.create(ClasActor.class,
                builder.clasId,
                builder.accountLength,
                builder.storage,
                builder.journalActor,
                builder.stormActor,
                builder.poolLowerSize,
                builder.poolUpperSize);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        router.tell(message, getSender());
        stormActor.tell(message, getSelf());
    }


    static public class ClasActorBuilder {
        private final String clasId;
        private final int accountLength;
        private ISpeedStorage storage;
        private ActorRef journalActor;
        private ActorRef stormActor;
        private int poolLowerSize = 2;
        private int poolUpperSize = 15;

        public ClasActorBuilder(final String clasId, final int accountLength) {
            this.clasId = clasId;
            this.accountLength = accountLength;
        }

        public ClasActorBuilder setStorage(ISpeedStorage storage) {
            this.storage = storage;
            return this;
        }

        public ClasActorBuilder setJournalActor(ActorRef journalActor) {
            this.journalActor = journalActor;
            return this;
        }

        public ClasActorBuilder setStormActor(ActorRef stormActor) {
            this.stormActor = stormActor;
            return this;
        }

        public ClasActorBuilder setPoolLowerSize(int poolLowerSize) {
            this.poolLowerSize = poolLowerSize;
            return this;
        }

        public ClasActorBuilder setPoolUpperSize(int poolUpperSize) {
            this.poolUpperSize = poolUpperSize;
            return this;
        }
    }
}
