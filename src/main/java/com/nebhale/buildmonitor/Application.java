/*
 * Copyright 2013-2014 the original author or authors.
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.cloud.security.sso.EnableOAuth2Sso;
import org.springframework.cloud.security.sso.OAuth2SsoConfigurer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

/**
 * Main configuration and application entry point
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableSpringDataWebSupport
public class Application {

    /**
     * Start method
     *
     * @param args command line argument
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @EnableOAuth2Sso
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 1)
    @Profile("oauth")
    static class OAuthConfiguration implements OAuth2SsoConfigurer {

        @Override
        public void configure(HttpSecurity http) {
            try {
                http
                    .authorizeRequests()
//                    .antMatchers("/management.html")
//                    .access("isAuthenticated() && authentication.userAuthentication.details['login'] == 'nebhale'");
                    .anyRequest()
                    .access("isAuthenticated() && authentication.userAuthentication.details['login'] == 'nebhale'");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Configuration
    @ServiceScan
    @Profile("cloud")
    static class CloudConfiguration {

    }

    @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/stomp").withSockJS();
        }

        @Override
        public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        }

        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
        }

        @Override
        public void configureClientOutboundChannel(ChannelRegistration registration) {
        }

        @Override
        public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
            return true;
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setApplicationDestinationPrefixes("/app");
        }

    }

}
