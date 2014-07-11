package com.chessix.vas;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chessix.vas.actors.JournalActor;
import com.chessix.vas.actors.NullJournalActor;
import com.chessix.vas.db.DBService;
import com.chessix.vas.service.ISpeedStorage;
import com.chessix.vas.service.RdbmsStorage;
import com.chessix.vas.service.RedisStorage;
import com.chessix.vas.web.RequestProcessingTimeInterceptor;
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
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DBService dbService;
    @Autowired
    private ActorSystem actorSystem;

    @Value("${vas.async:true}")
    private boolean async;

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
    ISpeedStorage speedStorage() {
        if (async) {
            log.debug("Redis storage");
            return new RedisStorage(redisTemplate);
        } else {
            log.debug("RDBMS storage");
            return new RdbmsStorage(dbService);
        }
    }

    @Bean
    ActorRef batchStorage() {
        if (async) {
            log.debug("RDBMS journaling");
            return actorSystem.actorOf(JournalActor.props(dbService), "Journalizer");
        } else {
            log.debug("Dummy journaling");
            return actorSystem.actorOf(NullJournalActor.props(), "Dummy-Journalizer");
        }
    }

    private static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(new RequestProcessingTimeInterceptor()).addPathPatterns("/**");
            super.addInterceptors(registry);
        }
    }
}
