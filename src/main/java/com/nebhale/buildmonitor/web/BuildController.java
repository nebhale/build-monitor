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

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import com.nebhale.buildmonitor.repository.BuildRepository;
import com.nebhale.buildmonitor.web.resource.BuildResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for accessing {@link Build}s
 */
@Controller
@ExposesResourceFor(Build.class)
@RequestMapping("/projects/{project}/builds")
public final class BuildController {

    private static final String MEDIA_TYPE = "application/vnd.nebhale.buildmonitor.build+json";

    private final PagedResourcesAssembler<Build> pagedResourcesAssembler;

    private final BuildRepository repository;

    private final BuildResourceAssembler resourceAssembler;

    @Autowired
    BuildController(PagedResourcesAssembler<Build> pagedResourcesAssembler, BuildRepository repository,
                    BuildResourceAssembler resourceAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MEDIA_TYPE)
    ResponseEntity<PagedResources<Resource<Build>>> readAll(@PathVariable Project project,
                                                            @PageableDefault(size = 10) Pageable pageable) {
        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Page<Build> builds = this.repository.findAllByProjectOrderByCreatedDesc(project, pageable);
        PagedResources<Resource<Build>> resources = this.pagedResourcesAssembler.toResource(builds,
                this.resourceAssembler);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

}
