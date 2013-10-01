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

package com.nebhale.buildmonitor.web.resource;

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.web.WebHookController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
final class EntityLinksProjectResourceAssembler implements ProjectResourceAssembler {

    private final EntityLinks entityLinks;

    /**
     * Creates an instance
     *
     * @param entityLinks used to build links
     */
    @Autowired
    EntityLinksProjectResourceAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Project> toResource(Project project) {
        Resource<Project> resource = new Resource<>(project);

        resource.add(this.entityLinks.linkToSingleResource(Project.class, project.getKey()));
        resource.add(this.entityLinks.linkFor(Build.class, project.getKey()).withRel("builds"));
        resource.add(linkTo(WebHookController.class, project.getKey()).withRel("webhook"));

        return resource;
    }
}
