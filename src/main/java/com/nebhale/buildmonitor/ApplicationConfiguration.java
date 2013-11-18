/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebhale.buildmonitor;

import com.googlecode.flyway.core.Flyway;
import com.jolbox.bonecp.BoneCPDataSource;
import org.postgresql.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.common.PostgresqlServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.messaging.simp.config.EnableWebSocketMessageBroker;
import org.springframework.messaging.simp.config.MessageBrokerConfigurer;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.messaging.simp.config.WebSocketMessageBrokerConfigurer;

import javax.sql.DataSource;

/**
 * Main configuration and application entry point
 */
@ComponentScan
@EnableAutoConfiguration
@EnableHypermediaSupport
@EnableSpringDataWebSupport
public class ApplicationConfiguration {

    /**
     * Start method
     *
     * @param args command line argument
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @Bean(destroyMethod = "close")
    @Profile("cloud")
    BoneCPDataSource cloudDataSource() {
        PostgresqlServiceInfo serviceInfo = (PostgresqlServiceInfo) cloud().getServiceInfo("build-monitor-db");

        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(Driver.class.getCanonicalName());
        dataSource.setJdbcUrl(serviceInfo.getJdbcUrl());
        dataSource.setMaxConnectionsPerPartition(2);

        return dataSource;
    }

    @Bean
    @Profile("cloud")
    Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean
    Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        return flyway;
    }

    @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/stomp").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerConfigurer configurer) {
            configurer.setAnnotationMethodDestinationPrefixes("/app");
        }
    }

}
