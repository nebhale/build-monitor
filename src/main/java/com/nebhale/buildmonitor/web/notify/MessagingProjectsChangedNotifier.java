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

package com.nebhale.buildmonitor.web.notify;

import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.ProjectRepository;
import com.nebhale.buildmonitor.web.resource.ProjectResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
final class MessagingProjectsChangedNotifier implements ProjectsChangedNotifier {

    private final MessageSendingOperations<String> messageTemplate;

    private final ProjectRepository repository;

    private final ProjectResourceAssembler resourceAssembler;

    @Autowired
    MessagingProjectsChangedNotifier(MessageSendingOperations<String> messageTemplate, ProjectRepository repository,
                                     ProjectResourceAssembler resourceAssembler) {
        this.messageTemplate = messageTemplate;
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @Override
    public void projectsChanged() {
        List<Resource<Project>> resources = new ArrayList<>();
        for (Project project : this.repository.findAll(new Sort("key"))) {
            resources.add(this.resourceAssembler.toResource(project));
        }

        this.messageTemplate.convertAndSend("/app/projects", resources);
    }
}
