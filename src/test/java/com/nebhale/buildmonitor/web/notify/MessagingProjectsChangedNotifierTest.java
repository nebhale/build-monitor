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

package com.nebhale.buildmonitor.web.notify;

import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.ProjectRepository;
import com.nebhale.buildmonitor.web.resource.ProjectResourceAssembler;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MessagingProjectsChangedNotifierTest {

    @SuppressWarnings("unchecked")
    private final MessageSendingOperations<String> messageTemplate = mock(MessageSendingOperations.class);

    private final ProjectRepository repository = mock(ProjectRepository.class);

    private final ProjectResourceAssembler resourceAssembler = mock(ProjectResourceAssembler.class);

    private final MessagingProjectsChangedNotifier notifier = new MessagingProjectsChangedNotifier(this
            .messageTemplate, this.repository, this.resourceAssembler);

    @Test
    @SuppressWarnings("unchecked")
    public void projectsChanged() {
        Project project1 = new Project("TEST-KEY-1", "Test Name 1");
        Project project2 = new Project("TEST-KEY-2", "Test Name 2");
        Resource<Project> resource1 = new Resource<>(project1);
        Resource<Project> resource2 = new Resource<>(project2);
        when(this.repository.findAll(new Sort("key"))).thenReturn(Arrays.asList(project1, project2));
        when(this.resourceAssembler.toResource(project1)).thenReturn(resource1);
        when(this.resourceAssembler.toResource(project2)).thenReturn(resource2);

        this.notifier.projectsChanged();

        verify(this.messageTemplate).convertAndSend("/app/projects", Arrays.asList(resource1, resource2));
    }

}
