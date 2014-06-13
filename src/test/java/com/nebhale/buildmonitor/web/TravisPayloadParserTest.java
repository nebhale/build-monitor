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

public final class TravisPayloadParserTest extends AbstractPayloadParserTest {

    public TravisPayloadParserTest() {
        super(new TravisPayloadParser());
    }

    @Test
    public void getStatePending() throws IOException {
        assertState("travis-pending-webhook.json", Build.State.IN_PROGRESS);
    }

    @Test
    public void getStatePass() throws IOException {
        assertState("travis-fixed-webhook.json", Build.State.PASS);
        assertState("travis-pass-webhook.json", Build.State.PASS);
    }

    @Test
    public void getStateFail() throws Exception {
        assertState("travis-still-failing-webhook.json", Build.State.FAIL);
        assertState("travis-failed-webhook.json", Build.State.FAIL);
        assertState("travis-broken-webhook.json", Build.State.FAIL);
        assertState("travis-errored-webhook.json", Build.State.FAIL);
    }

    @Test
    public void getStateUnknown() throws Exception {
        assertState("travis-unknown-webhook.json", Build.State.UNKNOWN);
    }

    @Test
    public void getUri() throws IOException {
        assertUri("travis-uri-webhook.json", "https://host/org/repo/builds/11894745");
    }

    @Test
    public void shouldProcessMaster() throws IOException {
        assertShouldProcess("travis-master-webhook.json", true);
    }

    @Test
    public void shouldProcessNonMaster() throws IOException {
        assertShouldProcess("travis-non-master-webhook.json", false);
    }

    @Test
    public void shouldProcessPullRequest() throws Exception {
        assertShouldProcess("travis-pull-request-webhook.json", false);
    }

}
