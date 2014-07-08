package com.chessix.vas;

import akka.actor.ActorSystem;
import com.chessix.vas.service.ISpeedStorage;
import com.chessix.vas.service.RedisStorage;
import com.chessix.vas.web.RequestProcessingTimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@ComponentScan
@Component
public class Application {

    @Autowired
    private RedisStorage redisStorage;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static class MvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(new RequestProcessingTimeInterceptor()).addPathPatterns("/**");
            super.addInterceptors(registry);
        }
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
    ISpeedStorage storage() {
        return redisStorage;
    }
}
