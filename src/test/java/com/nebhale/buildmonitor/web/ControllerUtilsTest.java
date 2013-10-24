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

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class ControllerUtilsTest {

    private final ConstraintViolationException exception;

    private final ControllerUtils controllerUtils = new ControllerUtils();

    public ControllerUtilsTest() {
        ConstraintViolation constraintViolation = mock(ConstraintViolation.class);
        when(constraintViolation.getPropertyPath()).thenReturn(mock(Path.class));
        when(constraintViolation.getInvalidValue()).thenReturn("test-invalid-value");
        when(constraintViolation.getMessage()).thenReturn("test-message");

        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);

        this.exception = new ConstraintViolationException(constraintViolations);
    }

    @Test
    public void handleConstraintViolation() {
        ResponseEntity<Set<String>> responseEntity = this.controllerUtils.handleConstraintViolation(this.exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
    }
}
