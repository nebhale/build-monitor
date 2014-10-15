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

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.BuildRepository;
import com.nebhale.buildmonitor.web.resource.BuildResourceAssembler;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Resource;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MessagingBuildsChangedNotifierTest {

    @SuppressWarnings("unchecked")
    private final MessageSendingOperations<String> messageTemplate = mock(MessageSendingOperations.class);

    private final BuildRepository repository = mock(BuildRepository.class);

    private final BuildResourceAssembler resourceAssembler = mock(BuildResourceAssembler.class);

    private final MessagingBuildsChangedNotifier notifier = new MessagingBuildsChangedNotifier(this
            .messageTemplate, this.repository, this.resourceAssembler);

    @Test
    @SuppressWarnings("unchecked")
    public void BuildsChanged() {
        Project project = new Project("TEST-KEY", "Test Name");
        Build build1 = new Build(project, "test-uri-1", Build.State.FAIL);
        Build build2 = new Build(project, "test-uri-2", Build.State.FAIL);
        Resource<Build> resource1 = new Resource<>(build1);
        Resource<Build> resource2 = new Resource<>(build2);
        when(this.repository.findAllByProjectOrderByCreatedDesc(project, new PageRequest(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(build1, build2)));
        when(this.resourceAssembler.toResource(build1)).thenReturn(resource1);
        when(this.resourceAssembler.toResource(build2)).thenReturn(resource2);

        this.notifier.buildsChanged(project);

        verify(this.messageTemplate).convertAndSend("/app/projects/TEST-KEY/builds", Arrays.asList(resource1,
                resource2));
    }

}
