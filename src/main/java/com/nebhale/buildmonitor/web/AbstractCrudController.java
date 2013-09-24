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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractCrudController<T extends Identifiable<String>> {

    private final EntityLinks entityLinks;

    private final ResourceAssembler<T, Resource<T>> resourceAssembler;

    private final JpaRepository<T, String> repository;

    AbstractCrudController(EntityLinks entityLinks, ResourceAssembler<T, Resource<T>> resourceAssembler,
                                     JpaRepository<T, String> repository) {
        this.entityLinks = entityLinks;
        this.resourceAssembler = resourceAssembler;
        this.repository = repository;
    }

    abstract ResponseEntity<Void> create(@RequestBody T entity);

    @Transactional
    final ResponseEntity<Void> doCreate(T entity) {
        if (this.repository.exists(entity.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.repository.saveAndFlush(entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(this.entityLinks.linkForSingleResource(entity).toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    abstract ResponseEntity<Set<Resource<T>>> readAll();

    @Transactional(readOnly = true)
    final ResponseEntity<Set<Resource<T>>> doReadAll() {
        List<T> entities = this.repository.findAll();

        Set<Resource<T>> resources = new HashSet<>(entities.size());
        for (T entity : entities) {
            resources.add(this.resourceAssembler.toResource(entity));
        }

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    abstract ResponseEntity<Resource<T>> read(@PathVariable("id") T entity);

    @Transactional(readOnly = true)
    final ResponseEntity<Resource<T>> doRead(T entity) {
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(this.resourceAssembler.toResource(entity), HttpStatus.OK);
    }

    abstract ResponseEntity<Void> delete(@PathVariable("id") T entity);

    @Transactional
    final ResponseEntity<Void> doDelete(T entity) {
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.repository.delete(entity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    final ResponseEntity<Set<String>> handleConstraintViolation(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        Set<String> messages = new HashSet<>(constraintViolations.size());
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            messages.add(String.format("%s value '%s' %s", constraintViolation.getPropertyPath(),
                    constraintViolation.getInvalidValue(), constraintViolation.getMessage()));
        }

        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }

}
