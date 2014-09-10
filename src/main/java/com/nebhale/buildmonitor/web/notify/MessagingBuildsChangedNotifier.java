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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Resource;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
final class MessagingBuildsChangedNotifier implements BuildsChangedNotifier {

    private static final Pageable PAGE = new PageRequest(0, 10);

    private final MessageSendingOperations<String> messageTemplate;

    private final BuildRepository repository;

    private final BuildResourceAssembler resourceAssembler;

    @Autowired
    MessagingBuildsChangedNotifier(MessageSendingOperations<String> messageTemplate, BuildRepository repository,
                                   BuildResourceAssembler resourceAssembler) {
        this.messageTemplate = messageTemplate;
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @Override
    public void buildsChanged(Project project) {
        List<Resource<Build>> resources = this.repository.findAllByProjectOrderByCreatedDesc(project,
                PAGE).getContent().stream().map(this.resourceAssembler::toResource).collect(Collectors.toList());

        this.messageTemplate.convertAndSend(getDestination(project), resources);
    }

    private String getDestination(Project project) {
        return String.format("/app/projects/%s/builds", project.getKey());
    }

}
