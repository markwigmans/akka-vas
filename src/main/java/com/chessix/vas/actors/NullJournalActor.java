package com.chessix.vas.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chessix.vas.actors.messages.Ready;

/**
 * A journal
 */
public class NullJournalActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private NullJournalActor() {
        super();
    }

    public static Props props() {
        return Props.create(NullJournalActor.class);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        log.debug("Received message: {}", message);
        if (message instanceof Ready.Request) {
            getSender().tell(new Ready.ResponseBuilder(true).build(), getSelf());
        }
    }
}
