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

    private final ActorRef router;

    private static final SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
            new Function<Throwable, Directive>() {
                @Override
                public Directive apply(final Throwable t) throws Exception {
                    return SupervisorStrategy.restart();
                }
            });

    /**
     * Create Props for an actor of this type.
     */
    public static Props props(final String clasId, final int accountLength, final ActorRef journalActor,
                              final ISpeedStorage storage) {
        return Props.create(ClasActor.class, clasId, accountLength, storage, journalActor);
    }

    private ClasActor(final String clasId, final int accountLength, final ISpeedStorage storage,
                      final ActorRef journalActor) {
        super();
        final DefaultResizer resizer = new DefaultResizer(2, 15);
        this.router = getContext().actorOf(
                new RoundRobinPool(5).withResizer(resizer).withSupervisorStrategy(strategy)
                        .props(ClerkActor.props(clasId, accountLength, journalActor, storage)), "router");
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        router.tell(message, getSender());
    }
}
