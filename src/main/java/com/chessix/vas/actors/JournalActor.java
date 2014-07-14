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

/**
 * @author Mark Wigmans
 */
public class JournalActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final DBService service;

    private JournalActor(final DBService service) {
        super();
        this.service = service;
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
            getSender().tell(new Ready.ResponseBuilder(true).message("Ready").build(), getSelf());
        } else {
            unhandled(message);
        }
    }

    private void createClas(final ClasCreated message) {
        service.createClas(message);
    }

    private void createAccount(final AccountCreated message) {
        service.createAccount(message);
    }

    private void createTransfer(final Transfer message) {
        service.createTransfer(message);
    }

    private void clean(final JournalMessage.Clean message) {
        service.clean(message);
    }
}
