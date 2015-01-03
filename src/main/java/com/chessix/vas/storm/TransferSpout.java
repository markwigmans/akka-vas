package com.chessix.vas.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.chessix.vas.actors.messages.Transfer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mawi on 31-12-2014.
 */
@Slf4j
public class TransferSpout extends BaseHazelcastSpout {
    private static final long serialVersionUID = 5377898051115225228L;

    private BlockingQueue<Transfer.Request> queue;

    /**
     * Constructor
     */
    public TransferSpout() {
        super(Transfer.Request.class.getName());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Constants.CLAS_ID, Constants.FROM, Constants.TO, Constants.AMOUNT));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        super.open(conf, context, collector);
        queue = hzInstance.getQueue(topicName);
    }

    @Override
    public void nextTuple() {
        final Transfer.Request message = queue.poll();
        if (message != null) {
            collector.emit(new Values(message.getClasId(), message.getFrom(), message.getTo(), message.getAmount()));
        }
    }
}
