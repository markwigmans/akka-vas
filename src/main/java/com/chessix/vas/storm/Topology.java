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

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *
 */
@Component
public class Topology {

    private final LocalCluster cluster;
    private final String name;

    @Value("${storm.spout.transfer:2}")
    private int transferSpount;

    @Value("${storm.spout.account:2}")
    private int accountsSpount;

    @Value("${storm.workers:2}")
    private int workers;

    @Autowired
    public Topology(final LocalCluster cluster) {
        this.cluster = cluster;
        this.name = "vas";
    }

    @PostConstruct
    void init() {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("transfers", new TransferSpout(), transferSpount);
        builder.setSpout("accounts", new AccountSpout(), accountsSpount);
        builder.setBolt("statisics", new StatisticsBolt()).shuffleGrouping("transfers");

        final Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(workers);

        cluster.submitTopology(name, conf, builder.createTopology());
    }

    @PreDestroy
    void destroy() {
        cluster.killTopology(name);
    }

}
