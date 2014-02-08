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

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.web.BuildController;
import com.nebhale.buildmonitor.web.ProjectController;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
final class StandardBuildResourceAssembler implements BuildResourceAssembler {

    @Override
    public Resource<Build> toResource(Build build) {
        Resource<Build> resource = new Resource<>(build);

        Project project = build.getProject();
        resource.add(linkTo(BuildController.class, project.getKey()).slash(build.getId()).withSelfRel());
        resource.add(linkTo(ProjectController.class).slash(project.getKey()).withRel("project"));

        return resource;
    }
}
