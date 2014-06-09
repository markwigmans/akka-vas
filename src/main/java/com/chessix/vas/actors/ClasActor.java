package com.chessix.vas.actors;

import org.springframework.data.redis.core.StringRedisTemplate;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.japi.Function;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;

/**
 * 
 * @author Mark Wigmans
 *
 */
public class ClasActor extends UntypedActor {

    @SuppressWarnings("unused")
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static final String NOSTRO = "nostro";
    public static final String OUTBOUND = "outbound";
    public static final String EXCEPTION = "exception";

    @SuppressWarnings("unused")
    private final String clasId;
    private final ActorRef router;

    private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
            new Function<Throwable, Directive>() {

                @Override
                public Directive apply(final Throwable t) throws Exception {
                    return SupervisorStrategy.restart();
                }

            });

    /**
     * Create Props for an actor of this type.
     */
    @SuppressWarnings("serial")
    public static Props props(final String clasId, final int accountLength, final ActorRef journalActor,
            final StringRedisTemplate redisTemplate) {
        return Props.create(new Creator<ClasActor>() {

            @Override
            public ClasActor create() throws Exception {
                return new ClasActor(clasId, accountLength, redisTemplate, journalActor);
            }
        });
    }

    public ClasActor(final String clasId, final int accountLength, final StringRedisTemplate redisTemplate,
            final ActorRef journalActor) {
        super();
        this.clasId = clasId;
        final DefaultResizer resizer = new DefaultResizer(2, 15);
        this.router = getContext().actorOf(
                new RoundRobinPool(5).withResizer(resizer).withSupervisorStrategy(strategy)
                        .props(ClerkActor.props(clasId, accountLength, journalActor, redisTemplate)), "router");
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        router.tell(message, getSender());
    }
}
