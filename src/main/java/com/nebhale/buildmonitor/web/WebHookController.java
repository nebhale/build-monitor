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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.BuildRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/projects/{project}/webhook")
final class WebHookController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;

    private final BuildRepository repository;

    @Autowired
    WebHookController(ObjectMapper objectMapper, BuildRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, value = "", params = "payload")
    ResponseEntity<Void> travisWebHook(@PathVariable Project project, @RequestParam("payload") String payloadString)
            throws IOException {
        return webHook(project, payloadString, new PayloadParser() {

            @Override
            public String getUri(Map<String, ?> payload) {
                return (String) payload.get("build_url");
            }

            @Override
            public Build.State getState(Map<String, ?> payload) {
                Object status = payload.get("status");

                if (Integer.valueOf(0).equals(status)) {
                    return Build.State.PASS;
                } else if (Integer.valueOf(1).equals(status)) {
                    return Build.State.FAIL;
                } else {
                    return Build.State.UNKNOWN;
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, value = "")
    ResponseEntity<Void> jenkinsWebHook(@PathVariable Project project, @RequestBody String payloadString) throws
            IOException {
        return webHook(project, payloadString, new PayloadParser() {

            @Override
            public String getUri(Map<String, ?> payload) {
                return getBuild(payload).get("full_url");
            }

            @Override
            public Build.State getState(Map<String, ?> payload) {
                Map<String, String> build = getBuild(payload);
                Object phase = build.get("phase");

                if ("STARTED".equals(phase)) {
                    return Build.State.IN_PROGRESS;
                } else if ("COMPLETED".equals(phase)) {
                    Object status = build.get("status");

                    if ("SUCCESS".equals(status)) {
                        return Build.State.PASS;
                    } else if ("FAILURE".equals(status)) {
                        return Build.State.FAIL;
                    } else {
                        return Build.State.UNKNOWN;
                    }
                } else {
                    return Build.State.UNKNOWN;
                }
            }

            private Map<String, String> getBuild(Map<String, ?> payload) {
                return (Map<String, String>) payload.get("build");
            }

        });
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Void> webHook(Project project, String payloadString, PayloadParser payloadParser) throws
            IOException {
        this.logger.debug(payloadString);

        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<String, ?> payload = this.objectMapper.readValue(payloadString, Map.class);
        String uri = payloadParser.getUri(payload);
        Build.State state = payloadParser.getState(payload);

        this.logger.info("Received {} webhook for {} with status {}", project.getKey(), uri, state);

        Build build = this.repository.findByUri(uri);
        if (build == null) {
            build = new Build(project, uri, state);
        } else {
            build.setState(state);
        }

        this.repository.saveAndFlush(build);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static interface PayloadParser {

        String getUri(Map<String, ?> payload);

        Build.State getState(Map<String, ?> payload);

    }

}
