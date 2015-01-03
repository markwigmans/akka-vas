package com.chessix.vas.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

/**
 * Connector to the Storm big-data processing system via hazelcast
 *
 * @see <a href="https://storm.apache.org/">Apache Storm</a>
 */
public class StormHazelcastActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final HazelcastInstance hzInstance;

    public StormHazelcastActor(final HazelcastInstance hzInstance) {
        this.hzInstance = hzInstance;
    }

    public static Props props(final HazelcastInstance hzInstance) {
        return Props.create(StormHazelcastActor.class, hzInstance);
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        final IQueue<Object> queue = hzInstance.getQueue(message.getClass().getName());
        queue.add(message);
    }
}
