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
import com.nebhale.buildmonitor.web.notify.ProjectsChangedNotifier;
import com.nebhale.buildmonitor.web.resource.ProjectResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for accessing {@link Project}s
 */
@Controller
@ExposesResourceFor(Project.class)
@RequestMapping("/projects")
public final class ProjectController {

    private static final String MEDIA_TYPE = "application/vnd.nebhale.buildmonitor.project+json";

    private final EntityLinks entityLinks;

    private final ProjectsChangedNotifier projectsChangedNotifier;

    private final ProjectRepository repository;

    private final ProjectResourceAssembler resourceAssembler;

    @Autowired
    ProjectController(EntityLinks entityLinks, ProjectsChangedNotifier projectsChangedNotifier, ProjectRepository repository,
                      ProjectResourceAssembler resourceAssembler) {
        this.entityLinks = entityLinks;
        this.projectsChangedNotifier = projectsChangedNotifier;
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "", produces = MEDIA_TYPE)
    ResponseEntity<Void> create(@RequestBody Project project) {
        if (this.repository.exists(project.getKey())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.repository.saveAndFlush(project);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(this.entityLinks.linkForSingleResource(Project.class, project.getKey()).toUri());

        this.projectsChangedNotifier.projectsChanged();
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.GET, value = "", produces = MEDIA_TYPE)
    ResponseEntity<List<Resource<Project>>> readAll() {
        List<Resource<Project>> resources = new ArrayList<>();
        for (Project project : this.repository.findAllByOrderByKeyAsc()) {
            resources.add(this.resourceAssembler.toResource(project));
        }

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.GET, value = "/{project}", produces = MEDIA_TYPE)
    ResponseEntity<Resource<Project>> read(@PathVariable Project project) {
        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(this.resourceAssembler.toResource(project), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{project}")
    ResponseEntity<Void> delete(@PathVariable Project project) {
        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.repository.delete(project);
        this.projectsChangedNotifier.projectsChanged();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
