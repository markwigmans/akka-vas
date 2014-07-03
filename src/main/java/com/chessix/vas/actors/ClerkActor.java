package com.chessix.vas.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chessix.vas.actors.messages.*;
import com.chessix.vas.actors.messages.Count.Request;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Mark Wigmans
 *
 */
public class ClerkActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final String clasId;
    private final int accountLength;
    private final ActorRef journalActor;
    private final StringRedisTemplate redisTemplate;

    public static Props props(final String clasId, final int accountLength, final ActorRef journalActor,
            final StringRedisTemplate redisTemplate) {
        return Props.create(ClerkActor.class, clasId, accountLength, journalActor, redisTemplate);
    }

    private ClerkActor(final String clasId, final int accountLength, final ActorRef journalActor,
            final StringRedisTemplate redisTemplate) {
        super();
        this.clasId = clasId;
        this.accountLength = accountLength;
        this.journalActor = journalActor;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        log.debug("Received message: {}", message);
        if (message instanceof CreateAccount.Request) {
            final CreateAccount.Request request = (CreateAccount.Request) message;
            final String accountId = createAccount(request);
            if (StringUtils.isNoneBlank(accountId)) {
                getSender().tell(new CreateAccount.ResponseBuilder(true).clasId(request.getClasId()).accountId(accountId).build(),
                        getSelf());
                journalActor.tell(new JournalMessage.AccountCreated(clasId, accountId), getSelf());
            } else {
                getSender().tell(
                        new CreateAccount.ResponseBuilder(false).clasId(request.getClasId()).message("Account does already exist")
                                .build(), getSelf());
            }
        } else if (message instanceof Transfer.Request) {
            final Transfer.Request request = (Transfer.Request) message;
            if (transfer(request)) {
                getSender().tell(new Transfer.ResponseBuilder(true).message("Ok").build(), getSelf());
                journalActor.tell(new JournalMessage.Transfer(clasId, request.getFrom(), request.getTo(), request.getAmount(),
                        new Date()), getSelf());
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
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final List<Object> values = ops.values();
        int total = 0;
        for (Object value : values) {
            total += Integer.parseInt((String) value);
        }
        return total == 0;
    }

    /**
     * 
     */
    private void clean(final Clean.Request request) {
        Assert.isTrue(StringUtils.equals(clasId, request.getClasId()));
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final Set<Object> keys = ops.keys();

        redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public List<Object> execute(final RedisOperations operations) throws DataAccessException {
                final BoundHashOperations ops = operations.boundHashOps(clasId);
                for (Object key : keys) {
                    ops.delete((String) key);
                }
                return null;
            }
        });

    }

    private String createAccount(final CreateAccount.Request message) {
        log.debug("createAccount({})", message);
        final String accountId;
        if (StringUtils.isNoneBlank(message.getAccountId())) {
            accountId = message.getAccountId();
        } else {
            accountId = RandomStringUtils.randomNumeric(accountLength);
        }
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final Boolean inserted = ops.putIfAbsent(accountId, "0");
        if (inserted) {
            return accountId;
        } else {
            return null;
        }
    }

    private Integer balance(final Balance.Request message) {
        log.debug("balance({})", message);
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        final String value = (String) ops.get(message.getAccountId());
        if (value != null) {
            return Integer.parseInt(value);
        }
        return null;
    }

    private Long count(final Request message) {
        log.debug("balance({})", message);
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        return ops.size();
    }

    private boolean transfer(final Transfer.Request message) {
        final String fromAccountId = message.getFrom();
        final String toAccountId = message.getTo();

        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(clasId);
        if ((ops.get(fromAccountId) != null) && (ops.get(toAccountId) != null)) {
            ops.increment(fromAccountId, -message.getAmount());
            ops.increment(toAccountId, message.getAmount());
            return true;
        } else {
            return false;
        }
    }
}
