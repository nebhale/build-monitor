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

package com.nebhale.buildmonitor.web.resource;

import com.nebhale.buildmonitor.domain.Project;
import org.junit.Test;
import org.springframework.hateoas.Resource;

import static org.junit.Assert.assertEquals;

public final class StandardProjectResourceAssemblerTest extends AbstractResourceAssemblerTest {

    private final Project project = new Project("test-key", "test-name");

    private final StandardProjectResourceAssembler resourceAssembler = new StandardProjectResourceAssembler();

    @Test
    public void toResource() throws Exception {
        Resource<Project> resource = this.resourceAssembler.toResource(this.project);

        assertEquals(this.project, resource.getContent());
        assertEquals("http://localhost/projects/TEST-KEY", resource.getLink("self").getHref());
        assertEquals("http://localhost/projects/TEST-KEY/webhook", resource.getLink("webhook").getHref());
    }

}
