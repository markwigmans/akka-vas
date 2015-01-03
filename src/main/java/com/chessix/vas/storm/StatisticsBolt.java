package com.chessix.vas.storm;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class StatisticsBolt extends BaseRichBolt {
    private static final long serialVersionUID = -7552082412071515007L;

    private SpoutOutputCollector collector;
    private Map<String, Statistics> statistics = new HashMap<>();


    @Override
    public Map<String, Object> getComponentConfiguration() {
        final Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
        return conf;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = collector;
    }

    @Override
    public void execute(final Tuple tuple) {
        if (isTickTuple(tuple)) {
            log.info("statistics tick");
            statistics.forEach((k, v) -> log.info("clas ID[{}] : {}", k, v));
        } else {
            final Integer amount = tuple.getIntegerByField(com.chessix.vas.storm.Constants.AMOUNT);
            final String clasId = tuple.getStringByField(com.chessix.vas.storm.Constants.CLAS_ID);
            updateStatistics(clasId, amount);
        }
    }

    private boolean isTickTuple(final Tuple tuple) {
        final String sourceComponent = tuple.getSourceComponent();
        final String sourceStreamId = tuple.getSourceStreamId();
        return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID) && sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
    }


    private void updateStatistics(final String clasId, final Integer amount) {
        log.info("updateStatistics({},{})", clasId, amount);
        Assert.notNull(clasId);
        Assert.notNull(amount);
        final Statistics statisic = statistics.getOrDefault(clasId, new Statistics());
        final Statistics update = statisic.update(amount);
        statistics.put(clasId, update);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("min", "max"));
    }
}