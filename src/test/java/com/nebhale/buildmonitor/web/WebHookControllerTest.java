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

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.BuildRepository;
import com.nebhale.buildmonitor.repository.ProjectRepository;
import com.nebhale.buildmonitor.utils.IoUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class WebHookControllerTest extends AbstractControllerTest {

    private static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final Project project = new Project("TEST-KEY", "Test Name");

    private final Build build = new Build(this.project, "http://host/job/job-name/211/", Build.State.UNKNOWN);

    @Autowired
    private volatile BuildRepository buildRepository;

    @Autowired
    private volatile ProjectRepository projectRepository;

    @Test
    public void webHookNotFound() throws Exception {
        this.mockMvc.perform(
                post("/projects/TEST-KEY/webhook")
                        .contentType(MEDIA_TYPE)
                        .content(read("webhook.json")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void webHookNewBuild() throws Exception {
        this.projectRepository.saveAndFlush(this.project);

        this.mockMvc.perform(
                post("/projects/TEST-KEY/webhook")
                        .contentType(MEDIA_TYPE)
                        .content(read("webhook.json")))
                .andExpect(status().isOk());

        assertEquals(1, countRowsInTable("build"));
    }

    @Test
    public void webHookExistingBuild() throws Exception {
        this.projectRepository.saveAndFlush(this.project);
        this.buildRepository.saveAndFlush(this.build);

        this.mockMvc.perform(
                post("/projects/TEST-KEY/webhook")
                        .contentType(MEDIA_TYPE)
                        .content(read("webhook.json")))
                .andExpect(status().isOk());

        assertEquals(1, countRowsInTable("build"));
    }

    @Test
    public void webHookMaxUriSize() throws Exception {
        this.projectRepository.saveAndFlush(this.project);

        this.mockMvc.perform(
                post("/projects/TEST-KEY/webhook")
                        .contentType(MEDIA_TYPE)
                        .content(read("uri-too-long-webhook.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("uri value " +
                        "'http://host/job/job-name/211/111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111' size")));
    }

    @Test
    public void webHookMinUriSize() throws Exception {
        this.projectRepository.saveAndFlush(this.project);

        this.mockMvc.perform(
                post("/projects/TEST-KEY/webhook")
                        .contentType(MEDIA_TYPE)
                        .content(read("uri-too-short-webhook.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("uri value '' size")));
    }

    @Test
    public void travisWebHookPass() throws Exception {
        assertState("travis-pass-webhook.json", Build.State.PASS);
    }

    @Test
    public void travisWebHookFail() throws Exception {
        assertState("travis-fail-webhook.json", Build.State.FAIL);
    }

    @Test
    public void travisWebHookUnknown() throws Exception {
        assertState("travis-unknown-webhook.json", Build.State.UNKNOWN);
    }

    @Test
    public void jenkinsWebHookStart() throws Exception {
        assertState("jenkins-in-progress-webhook.json", Build.State.IN_PROGRESS);
    }

    @Test
    public void jenkinsWebHookPassCompleted() throws Exception {
        assertState("jenkins-pass-completed-webhook.json", Build.State.PASS);
    }

    @Test
    public void jenkinsWebHookFinishedCompleted() throws Exception {
        assertState("jenkins-pass-finished-webhook.json", Build.State.PASS);
    }

    @Test
    public void jenkinsWebHookFail() throws Exception {
        assertState("jenkins-fail-webhook.json", Build.State.FAIL);
    }

    @Test
    public void jenkinsWebHookUnknownPhase() throws Exception {
        assertState("jenkins-unknown-phase-webhook.json", Build.State.UNKNOWN);
    }

    @Test
    public void jenkinsWebHookUnknownStatus() throws Exception {
        assertState("jenkins-unknown-status-webhook.json", Build.State.UNKNOWN);
    }

    private void assertState(String filename, Build.State state) throws Exception {
        this.projectRepository.saveAndFlush(this.project);

        MockHttpServletRequestBuilder requestBuilder = post("/projects/TEST-KEY/webhook").contentType(MEDIA_TYPE);

        if (filename.startsWith("travis")) {
            requestBuilder.param("payload", read(filename));
        } else {
            requestBuilder.content(read(filename));
        }

        this.mockMvc.perform(requestBuilder).andExpect(status().isOk());
        assertEquals(state, this.buildRepository.findAll().get(0).getState());
    }

    private String read(String filename) throws IOException {
        Reader in = null;
        StringWriter out = null;

        try {
            in = new FileReader("src/test/resources/" + filename);
            out = new StringWriter();

            IoUtils.copy(in, out);

            return out.toString();
        } finally {
            IoUtils.closeQuietly(in, out);
        }
    }

}
