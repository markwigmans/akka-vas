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
package com.chessix.vas;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;
import akka.routing.SmallestMailboxPool;
import backtype.storm.LocalCluster;
import com.chessix.vas.actors.JournalActor;
import com.chessix.vas.actors.NullJournalActor;
import com.chessix.vas.actors.StormHazelcastActor;
import com.chessix.vas.actors.StormZMQActor;
import com.chessix.vas.db.DBService;
import com.chessix.vas.service.HazelcastStorage;
import com.chessix.vas.service.ISpeedStorage;
import com.chessix.vas.service.RdbmsStorage;
import com.chessix.vas.service.RedisStorage;
import com.chessix.vas.web.RequestProcessingTimeInterceptor;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@ComponentScan
@Component
@Slf4j
public class Application {

    @Autowired
    private HazelcastInstance hzInstance;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DBService dbService;
    @Autowired
    private ActorSystem actorSystem;

    @Value("${vas.async:true}")
    private boolean async;

    @Value("${vas.async.strategy:H}")
    private String asyncStrategy;

    @Value("${storm.connector.strategy:hzc}")
    private String stormConnector;

    @Value("${storm.connector.pool.lower:2}")
    private int connectorPoolLowerSize;
    @Value("${storm.connector.pool.upper:15}")
    private int connectorPoolUpperSize;

    @Value("${storm.zmq.address:tcp://127.0.0.1:1237}")
    private String stormBindAddress;


    /**
     * Start the whole application
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    WebMvcConfigurerAdapter mvcConfigurerAdapter() {
        return new MvcConfig();
    }

    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("VAS-System");
    }

    @Bean
    HazelcastInstance hzInstance() {
        final Config cfg = new Config();
        return Hazelcast.newHazelcastInstance(cfg);
    }

    @Bean
    LocalCluster cluster() {
        return new LocalCluster();
    }

    @Bean
    ISpeedStorage speedStorage() {
        if (async) {
            switch (asyncStrategy) {
                case "R":
                    log.info("Redis speed storage");
                    return new RedisStorage(redisTemplate);
                case "H":
                default:
                    log.info("Hazelcast speed storage");
                    return new HazelcastStorage(hzInstance);
            }
        } else {
            log.debug("RDBMS speed storage");
            return new RdbmsStorage(dbService);
        }
    }

    @Bean
    ActorRef journalActor() {
        if (async) {
            log.info("RDBMS batch storage");
            return actorSystem.actorOf(JournalActor.props(dbService), "journalizer");
        } else {
            log.info("Dummy batch storage");
            return actorSystem.actorOf(NullJournalActor.props(), "dummy-journalizer");
        }
    }

    @Bean
    ActorRef stormActor() {
        final Props props;
        switch (stormConnector) {
            case "zmq":
                props = StormZMQActor.props(stormBindAddress);
                break;
            case "hzc":
            default:
                props = StormHazelcastActor.props(hzInstance);
        }

        final DefaultResizer resizer = new DefaultResizer(connectorPoolLowerSize, connectorPoolUpperSize);
        return actorSystem.actorOf(
                new SmallestMailboxPool(connectorPoolLowerSize).withResizer(resizer).props(props), "storm-router");
    }


    private static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(new RequestProcessingTimeInterceptor()).addPathPatterns("/**");
            super.addInterceptors(registry);
        }
    }
}
