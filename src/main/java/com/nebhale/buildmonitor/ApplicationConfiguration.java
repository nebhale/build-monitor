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
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import javax.sql.DataSource;

/**
 * Main configuration and application entry point
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableHypermediaSupport
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
    DataSource cloudDataSource() {
        PostgresqlServiceInfo serviceInfo = (PostgresqlServiceInfo) cloud().getServiceInfo("build-monitor-db");

        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(Driver.class.getCanonicalName());
        dataSource.setJdbcUrl(jdbcUrl(serviceInfo));
        dataSource.setUsername(serviceInfo.getUserName());
        dataSource.setPassword(serviceInfo.getPassword());
        dataSource.setMaxConnectionsPerPartition(2);

        return dataSource;
    }

    @Bean
    @Profile("cloud")
    Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean(destroyMethod = "close")
    @Profile("default")
    DataSource defaultDataSource() {

        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(Driver.class.getCanonicalName());
        dataSource.setJdbcUrl("jdbc:postgresql://localhost/build_monitor");

        return dataSource;
    }

    @Bean
    DomainClassConverter<ConfigurableConversionService> domainClassConverter(ConfigurableConversionService conversionService) {
        return new DomainClassConverter<>(conversionService);
    }

    @Bean(initMethod = "migrate")
    Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("META-INF/db/migration");

        return flyway;
    }

    private String jdbcUrl(PostgresqlServiceInfo serviceInfo) {
        return String.format("jdbc:postgresql://%s:%d/%s", serviceInfo.getHost(), serviceInfo.getPort(),
                serviceInfo.getUserName());
    }

}
