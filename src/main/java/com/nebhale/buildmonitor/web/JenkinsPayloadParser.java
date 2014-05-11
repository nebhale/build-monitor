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

package com.nebhale.buildmonitor.web;

import com.nebhale.buildmonitor.domain.Build;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
final class JenkinsPayloadParser implements WebHookController.PayloadParser {

    @Override
    public Build.State getState(Map<String, ?> payload) {
        Map<String, String> build = getBuild(payload);
        Object phase = build.get("phase");

        if ("STARTED".equals(phase)) {
            return Build.State.IN_PROGRESS;
        } else if ("COMPLETED".equals(phase) || "FINISHED".equals(phase)) {
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

    @Override
    public String getUri(Map<String, ?> payload) {
        return getBuild(payload).get("full_url");
    }

    @Override
    public Boolean shouldProcess(Map<String, ?> payload) {
        return isMaster(payload);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getBuild(Map<String, ?> payload) {
        return (Map<String, String>) payload.get("build");
    }

    @SuppressWarnings("unchecked")
    private Boolean isMaster(Map<String, ?> payload) {
        Map<String, Map<String, String>> build = (Map<String, Map<String, String>>) payload.get("build");
        Map<String, String> parameters = build.get("parameters");

        if (parameters != null) {
            String branch = parameters.get("branch");

            if (branch != null) {
                return "master".equals(branch);
            }
        }

        return true;
    }

}
