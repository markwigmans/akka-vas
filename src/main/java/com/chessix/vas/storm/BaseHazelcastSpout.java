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
package com.chessix.vas.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Default BaseRichSpout using Hazelcast to publish/subscribe on specific topic topicName.
 */
@Slf4j
public abstract class BaseHazelcastSpout extends BaseRichSpout {

    private static final long serialVersionUID = -6207675687227794104L;

    protected final String topicName;
    protected SpoutOutputCollector collector;
    protected HazelcastInstance hzInstance;
    private String hzInstanceName;


    /**
     * Creates a new {@link BaseHazelcastSpout} instance.
     *
     * @param name topicName of the topic on which this bolt have to listen.
     */
    public BaseHazelcastSpout(final String topicName) {
        this.topicName = topicName;
    }

    private HazelcastInstance hazelcastProvider(final TopologyContext context) {
        final Config cfg = new Config();
        hzInstanceName = String.format("storm-%s-%d-%d", topicName, context.getThisTaskId(), context.getThisTaskIndex());
        log.info("create hazelcast instance: '{}'", hzInstanceName);
        cfg.setInstanceName(hzInstanceName);
        return Hazelcast.newHazelcastInstance(cfg);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.hzInstance = hazelcastProvider(context);
        this.collector = collector;
    }

    @Override
    public void close() {
        log.info("hazelcast instance: '{}' stopped", hzInstanceName);
        hzInstance.shutdown();
    }
}