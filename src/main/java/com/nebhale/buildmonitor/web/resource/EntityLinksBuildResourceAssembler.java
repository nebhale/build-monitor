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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;


@Component
final class EntityLinksBuildResourceAssembler implements BuildResourceAssembler {

    private final EntityLinks entityLinks;

    @Autowired
    EntityLinksBuildResourceAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Build> toResource(Build build) {
        Resource<Build> resource = new Resource<>(build);

        resource.add(this.entityLinks.linkFor(Build.class, build.getProject().getKey()).slash(build.getId()).withSelfRel
                ());
        resource.add(this.entityLinks.linkToSingleResource(Project.class, build.getProject().getKey()).withRel
                ("project"));

        return resource;
    }
}
