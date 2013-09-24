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

import com.nebhale.buildmonitor.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
final class ProjectResourceAssembler implements ResourceAssembler<Project, Resource<Project>> {

    private final EntityLinks entityLinks;

    @Autowired
    ProjectResourceAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Project> toResource(Project project) {
        Resource<Project> resource = new Resource<>(project);

        resource.add(this.entityLinks.linkToSingleResource(project).withSelfRel());
        resource.add(linkTo(WebHookController.class, project).withRel("webhook"));

        return resource;
    }
}
