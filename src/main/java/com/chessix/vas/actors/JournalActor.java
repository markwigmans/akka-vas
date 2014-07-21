package com.chessix.vas.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.actors.messages.JournalMessage.AccountCreated;
import com.chessix.vas.actors.messages.JournalMessage.ClasCreated;
import com.chessix.vas.actors.messages.JournalMessage.Transfer;
import com.chessix.vas.actors.messages.Ready;
import com.chessix.vas.db.DBService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Date;

/**
 * @author Mark Wigmans
 */
public class JournalActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final DBService service;

    private Duration delay;

    /**
     * Constructor
     */
    private JournalActor(final DBService service) {
        super();
        this.service = service;
        this.delay = null;
    }

    public static Props props(final DBService service) {
        return Props.create(JournalActor.class, service);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        log.debug("Received message: {}", message);
        if (message instanceof JournalMessage.ClasCreated) {
            createClas((JournalMessage.ClasCreated) message);
        } else if (message instanceof JournalMessage.AccountCreated) {
            createAccount((JournalMessage.AccountCreated) message);
        } else if (message instanceof JournalMessage.Transfer) {
            createTransfer((JournalMessage.Transfer) message);
        } else if (message instanceof JournalMessage.Clean) {
            clean((JournalMessage.Clean) message);
        } else if (message instanceof Ready.Request) {
            log.info("Max delay: {}", delay);
            getSender().tell(new Ready.ResponseBuilder(true).message("Ready").build(), getSelf());
        } else {
            unhandled(message);
        }
    }

    private void createClas(final ClasCreated message) {
        updateTimeDelay(message.getTimestamp());
        service.createClas(message);
    }

    private void createAccount(final AccountCreated message) {
        updateTimeDelay(message.getTimestamp());
        service.createAccount(message);
    }

    private void createTransfer(final Transfer message) {
        updateTimeDelay(message.getTimestamp());
        service.createTransfer(message);
    }

    private void clean(final JournalMessage.Clean message) {
        service.clean(message);
    }

    /**
     * Update the time delay between creation of the message and processing it.
     */
    private void updateTimeDelay(final Date timestamp) {
        final Duration d = new Interval(new DateTime(timestamp), DateTime.now()).toDuration();
        if ((delay == null) || (d.compareTo(delay) > 0)) {
            delay = d;
        }
    }
}
