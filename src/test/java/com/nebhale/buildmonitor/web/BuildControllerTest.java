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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class BuildControllerTest extends AbstractControllerTest {

    private static final MediaType MEDIA_TYPE = MediaType.valueOf("application/vnd.nebhale.buildmonitor.build+json");

    @Autowired
    private volatile ProjectRepository projectRepository;

    @Autowired
    private volatile BuildRepository buildRepository;


    @Test
    public void readAll() throws Exception {
        Project project = this.projectRepository.saveAndFlush(new Project("TEST-KEY", "Test Name"));
        this.buildRepository.saveAndFlush(new Build(project, "test-uri-1", Build.State.PASS));
        this.buildRepository.saveAndFlush(new Build(project, "test-uri-2", Build.State.PASS));

        this.mockMvc.perform(
                get("/projects/TEST-KEY/builds")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$content[0].uri").value("test-uri-2"))
                .andExpect(jsonPath("$content[1].uri").value("test-uri-1"));
    }

    @Test
    public void readAllNotNull() throws Exception {
        this.mockMvc.perform(
                get("/projects/TEST_KEY/builds")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isNotFound());
    }
}
