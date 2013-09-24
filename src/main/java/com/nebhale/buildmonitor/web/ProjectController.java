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
import com.nebhale.buildmonitor.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

@Controller
@ExposesResourceFor(Project.class)
@RequestMapping("/projects")
final class ProjectController extends AbstractCrudController<Project> {

    private static final String MEDIA_TYPE = "application/vnd.nebhale.buildmonitor.project+json";

    @Autowired
    ProjectController(EntityLinks entityLinks, ProjectResourceAssembler resourceAssembler,
                      ProjectRepository repository) {
        super(entityLinks, resourceAssembler, repository);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "", produces = MEDIA_TYPE)
    ResponseEntity<Void> create(@RequestBody Project project) {
        return doCreate(project);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MEDIA_TYPE)
    ResponseEntity<Set<Resource<Project>>> readAll() {
        return doReadAll();
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MEDIA_TYPE)
    ResponseEntity<Resource<Project>> read(@PathVariable("id") Project project) {
        return doRead(project);
    }

    @Override
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") Project project) {
        return doDelete(project);
    }
}
