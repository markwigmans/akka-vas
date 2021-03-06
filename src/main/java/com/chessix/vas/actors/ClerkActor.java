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
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chessix.vas.actors.messages.*;
import com.chessix.vas.actors.messages.Count.Request;
import com.chessix.vas.service.ISpeedStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Mark Wigmans
 */
public class ClerkActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final String clasId;
    private final int accountLength;
    private final ActorRef journalActor;
    private final ISpeedStorage storage;

    private ClerkActor(final String clasId, final int accountLength, final ActorRef journalActor,
                       final ISpeedStorage storage) {
        super();
        this.clasId = clasId;
        this.accountLength = accountLength;
        this.journalActor = journalActor;
        this.storage = storage;
    }

    public static Props props(final String clasId, final int accountLength, final ActorRef journalActor,
                              final ISpeedStorage storage) {
        return Props.create(ClerkActor.class, clasId, accountLength, journalActor, storage);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        log.debug("Received message: {}", message);
        if (message instanceof CreateClas.Request) {
            final CreateClas.Request request = (CreateClas.Request) message;
            createClas(request);
            getSender().tell(new CreateClas.ResponseBuilder(true).build(), getSender());
        } else if (message instanceof CreateAccount.Request) {
            final CreateAccount.Request request = (CreateAccount.Request) message;
            final String accountId = createAccount(request);
            if (StringUtils.isNoneBlank(accountId)) {
                getSender().tell(new CreateAccount.ResponseBuilder(true).clasId(request.getClasId()).accountId(accountId).build(),
                        getSelf());
                journalActor.tell(new JournalMessage.AccountCreatedBuilder(clasId, accountId).build(), getSelf());
            } else {
                getSender().tell(
                        new CreateAccount.ResponseBuilder(false).clasId(request.getClasId()).message("Account does already exist")
                                .build(), getSelf());
            }
        } else if (message instanceof Transfer.Request) {
            final Transfer.Request request = (Transfer.Request) message;
            if (transfer(request)) {
                getSender().tell(new Transfer.ResponseBuilder(true).message("Ok").build(), getSelf());
                journalActor.tell(new JournalMessage.TransferBuilder(clasId, request.getFrom(), request.getTo(), request.getAmount()).build(), getSelf());
            } else {
                getSender().tell(new Transfer.ResponseBuilder(false).message("Accounts do not exist").build(), getSelf());
            }
        } else if (message instanceof Balance.Request) {
            final Integer balance = balance((Balance.Request) message);
            getSender().tell(new Balance.ResponseBuilder(balance != null).amount(balance).build(), getSelf());
        } else if (message instanceof Clean.Request) {
            final Clean.Request request = (Clean.Request) message;
            clean(request);
            getSender().tell(new Clean.ResponseBuilder(true).clasId(request.getClasId()).message("Ok").build(), getSelf());
        } else if (message instanceof Count.Request) {
            final Request request = (Request) message;
            final Long count = count(request);
            getSender().tell(new Count.ResponseBuilder(count != null).clasId(request.getClasId()).count(count).build(), getSelf());
        } else if (message instanceof Validate.Request) {
            final Validate.Request request = (Validate.Request) message;
            getSender().tell(new Validate.ResponseBuilder(validate()).clasId(request.getClasId()).build(), getSelf());
        } else {
            unhandled(message);
        }
    }

    /**
     *
     */
    private boolean validate() {
        final List<Integer> values = storage.accountValues(clasId);
        int total = 0;
        for (final Integer value : values) {
            total += value;
        }
        // check if total is in balance
        return total == 0;
    }

    /**
     *
     */
    private void clean(final Clean.Request request) {
        log.debug("clean({})", request);
        Assert.isTrue(StringUtils.equals(clasId, request.getClasId()));
        storage.delete(clasId);
    }

    private void createClas(final CreateClas.Request request) {
        log.debug("createClas({})", request);
        storage.create(clasId);
    }

    private String createAccount(final CreateAccount.Request message) {
        log.debug("createAccount({})", message);
        final String accountId;
        if (StringUtils.isNoneBlank(message.getAccountId())) {
            accountId = message.getAccountId();
        } else {
            accountId = RandomStringUtils.randomNumeric(accountLength);
        }
        final Boolean inserted = storage.create(clasId, accountId);
        if (inserted) {
            return accountId;
        } else {
            return null;
        }
    }

    private Integer balance(final Balance.Request message) {
        log.debug("balance({})", message);
        return storage.get(clasId, message.getAccountId());
    }

    private Long count(final Request message) {
        log.debug("count({})", message);
        return storage.size(clasId);
    }

    private boolean transfer(final Transfer.Request message) {
        final String fromAccountId = message.getFrom();
        final String toAccountId = message.getTo();

        if ((storage.get(clasId, fromAccountId) != null) && (storage.get(clasId, toAccountId) != null)) {
            storage.transfer(clasId, fromAccountId, toAccountId, message.getAmount());
            return true;
        } else {
            return false;
        }
    }
}
