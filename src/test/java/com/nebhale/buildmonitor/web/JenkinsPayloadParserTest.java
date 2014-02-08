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
import org.junit.Test;

import java.io.IOException;

public final class JenkinsPayloadParserTest extends AbstractPayloadParserTest {

    public JenkinsPayloadParserTest() {
        super(new JenkinsPayloadParser());
    }

    @Test
    public void getStateStart() throws Exception {
        assertState("jenkins-in-progress-webhook.json", Build.State.IN_PROGRESS);
    }

    @Test
    public void getStatePassCompleted() throws Exception {
        assertState("jenkins-pass-completed-webhook.json", Build.State.PASS);
    }

    @Test
    public void getStateFinishedCompleted() throws Exception {
        assertState("jenkins-pass-finished-webhook.json", Build.State.PASS);
    }

    @Test
    public void getStateFail() throws Exception {
        assertState("jenkins-fail-webhook.json", Build.State.FAIL);
    }

    @Test
    public void getStateUnknownPhase() throws Exception {
        assertState("jenkins-unknown-phase-webhook.json", Build.State.UNKNOWN);
    }

    @Test
    public void getStateUnknownStatus() throws Exception {
        assertState("jenkins-unknown-status-webhook.json", Build.State.UNKNOWN);
    }

    @Test
    public void getUri() throws IOException {
        assertUri("jenkins-uri-webhook.json", "http://host/job/job-name/211/");
    }

    @Test
    public void shouldProcessParametersNotSpecified() throws IOException {
        assertShouldProcess("jenkins-parameters-not-specified-webhook.json", true);
    }

    @Test
    public void shouldProcessBranchNotSpecified() throws IOException {
        assertShouldProcess("jenkins-branch-not-specified-webhook.json", true);
    }

    @Test
    public void shouldProcessMaster() throws IOException {
        assertShouldProcess("jenkins-master-webhook.json", true);
    }

    @Test
    public void shouldProcessNonMaster() throws IOException {
        assertShouldProcess("jenkins-non-master-webhook.json", false);
    }
}
