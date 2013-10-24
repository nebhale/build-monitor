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

package com.nebhale.buildmonitor.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.BuildRepository;
import com.nebhale.buildmonitor.web.notify.BuildsChangedNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

/**
 * Controller for accessing WebHooks
 */
@Controller
@RequestMapping("/projects/{project}/webhook")
public final class WebHookController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BuildsChangedNotifier buildsChangedNotifier;

    private final BuildRepository repository;

    private final JenkinsPayloadParser jenkinsPayloadParser;

    private final TravisPayloadParser travisPayloadParser;

    @Autowired
    WebHookController(BuildsChangedNotifier buildsChangedNotifier, BuildRepository repository,
                      JenkinsPayloadParser jenkinsPayloadParser, TravisPayloadParser travisPayloadParser) {
        this.buildsChangedNotifier = buildsChangedNotifier;
        this.repository = repository;
        this.jenkinsPayloadParser = jenkinsPayloadParser;
        this.travisPayloadParser = travisPayloadParser;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "", params = "payload")
    ResponseEntity<Void> travisWebHook(@PathVariable Project project, @RequestParam("payload") Map<String, ?> payload)
            throws IOException {
        return webHook(project, payload, this.travisPayloadParser);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "")
    ResponseEntity<Void> jenkinsWebHook(@PathVariable Project project, @RequestBody Map<String, ?> payload) throws
            IOException {
        return webHook(project, payload, this.jenkinsPayloadParser);
    }

    private ResponseEntity<Void> webHook(Project project, Map<String, ?> payload, PayloadParser payloadParser) throws
            JsonProcessingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(new ObjectMapper().writeValueAsString(payload));
        }

        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String uri = payloadParser.getUri(payload);
        Build.State state = payloadParser.getState(payload);

        this.logger.info("Received {} webhook for {} with status {}", project.getKey(), uri, state);

        if (payloadParser.shouldProcess(payload)) {
            Build build = this.repository.findByUri(uri);
            if (build == null) {
                build = new Build(project, uri, state);
            } else {
                build.setState(state);
            }

            this.repository.saveAndFlush(build);
            this.buildsChangedNotifier.buildsChanged(project);
        } else {
            this.logger.info("Payload ignored");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    static interface PayloadParser {

        /**
         * Returns the state of the build represented by the payload
         *
         * @param payload the payload
         *
         * @return the state of the build represented by the payload
         */
        Build.State getState(Map<String, ?> payload);

        /**
         * Returns the uri of the build represented by the payload
         *
         * @param payload the payload
         * @return the uri of the build represented by the payload
         */
        String getUri(Map<String, ?> payload);

        /**
         * Whether or not the payload should be processed
         * @param payload the payload
         * @return {@code true} if the payload should be processed, {@code false} otherwise
         */
        Boolean shouldProcess(Map<String, ?> payload);

    }

}
