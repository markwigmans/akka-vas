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

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chessix.vas.actors.messages.*;
import com.chessix.vas.db.DBService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 */
public class JournalActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final DBService service;
    private Optional<Duration> delay;

    /**
     * Constructor
     */
    private JournalActor(final DBService service) {
        this.service = service;
        this.delay = Optional.empty();
    }

    public static Props props(final DBService service) {
        return Props.create(JournalActor.class, service);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        log.debug("Received message: {}", message);
        if (message instanceof CreateClas.Request) {
            createClas((CreateClas.Request) message);
        } else if (message instanceof CreateAccount.Request) {
            createAccount((CreateAccount.Request) message);
        } else if (message instanceof Transfer.Request) {
            createTransfer((Transfer.Request) message);
        } else if (message instanceof Clean.Request) {
            clean((Clean.Request) message);
        } else if (message instanceof Ready.Request) {
            log.info("Max delay: {}", delay);
            getSender().tell(new Ready.ResponseBuilder(true).message("Ready").build(), getSelf());
        } else {
            unhandled(message);
        }
    }

    private void createClas(final CreateClas.Request message) {
        updateTimeDelay(message.getTimestamp());
        service.createClas(message);
    }

    private void createAccount(final CreateAccount.Request message) {
        updateTimeDelay(message.getTimestamp());
        service.createAccount(message);
    }

    private void createTransfer(final Transfer.Request message) {
        updateTimeDelay(message.getTimestamp());
        service.createTransfer(message);
    }

    private void clean(final Clean.Request message) {
        service.clean(message);
    }

    /**
     * Update the time delay between creation of the message and processing it.
     */
    private void updateTimeDelay(final LocalDateTime timestamp) {
        final Duration d = Duration.between(timestamp, LocalDateTime.now());
        delay = Optional.of(delay.map(i -> d.compareTo(i) > 0 ? d : i).orElse(d));
    }
}
