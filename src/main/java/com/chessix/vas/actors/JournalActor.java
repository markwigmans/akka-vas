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
import java.util.Optional;

/**
 * @author Mark Wigmans
 */
public class JournalActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final DBService service;
    private Optional<Duration> delay;

    /**
     * Constructor
     */
    private JournalActor(final DBService service) {
        super();
        this.service = service;
        this.delay = Optional.empty();
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
        if ((!delay.isPresent()) || (d.compareTo(delay.get()) > 0)) {
            delay = Optional.of(d);
        }
    }
}
