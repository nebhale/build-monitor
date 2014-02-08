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

import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.ProjectRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ProjectControllerTest extends AbstractControllerTest {

    private static final MediaType MEDIA_TYPE = MediaType.valueOf("application/vnd.nebhale.buildmonitor.project+json");

    @Autowired
    private volatile ProjectRepository projectRepository;

    @Test
    public void create() throws Exception {
        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:TEST_KEY", "name:Test Name")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/projects/TEST_KEY"));

        assertEquals(1, countRowsInTable("project"));
    }

    @Test
    public void createDuplicate() throws Exception {
        this.projectRepository.saveAndFlush(new Project("TEST_KEY", "Test Name"));

        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:TEST_KEY", "name:Test Name")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createMinKeySize() throws Exception {
        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:", "name:Test Name")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("key value '' size")));
    }

    @Test
    public void createMaxKeySize() throws Exception {
        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:012345678", "name:Test Name")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("key value '012345678' size")));
    }

    @Test
    public void createMinNameSize() throws Exception {
        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:TEST_KEY", "name:")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("name value '' size")));
    }

    @Test
    public void createMaxNameSize() throws Exception {
        this.mockMvc.perform(
                post("/projects")
                        .contentType(MEDIA_TYPE)
                        .content(toJson("key:TEST_KEY",
                                "name:012345678901234567890123456789012345678901234567890123456789012345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(startsWith("name value " +
                        "'012345678901234567890123456789012345678901234567890123456789012345' size")));
    }

    @Test
    public void readAll() throws Exception {
        this.projectRepository.saveAndFlush(new Project("BRAVO", "Test Name"));
        this.projectRepository.saveAndFlush(new Project("ALPHA", "Test Name"));
        this.projectRepository.saveAndFlush(new Project("CHARLIE", "Test Name"));

        this.mockMvc.perform(
                get("/projects")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("ALPHA"))
                .andExpect(jsonPath("$[1].key").value("BRAVO"))
                .andExpect(jsonPath("$[2].key").value("CHARLIE"));
    }

    @Test
    public void read() throws Exception {
        this.projectRepository.saveAndFlush(new Project("TEST_KEY", "Test Name"));

        this.mockMvc.perform(
                get("/projects/TEST_KEY")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("TEST_KEY"))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.links[?(@.rel==self)].href[0]").value("http://localhost/projects/TEST_KEY"))
                .andExpect(jsonPath("$.links[?(@.rel==webhook)].href[0]").value
                        ("http://localhost/projects/TEST_KEY/webhook"));
    }

    @Test
    public void readNotFound() throws Exception {
        this.mockMvc.perform(
                get("/projects/TEST_KEY")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void del() throws Exception {
        this.projectRepository.saveAndFlush(new Project("TEST_KEY", "Test Name"));

        this.mockMvc.perform(
                delete("/projects/TEST_KEY")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNotFound() throws Exception {
        this.mockMvc.perform(
                delete("/projects/TEST_KEY")
                        .accept(MEDIA_TYPE))
                .andExpect(status().isNotFound());
    }

}
