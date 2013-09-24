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

package com.nebhale.buildmonitor.domain;

import org.springframework.hateoas.Identifiable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * A project who's build should be monitored
 */
@Entity
public final class Project implements Identifiable<String> {

    @Id
    @Size(min = 1, max = 8)
    private volatile String id;

    @Size(min = 1, max = 64)
    private volatile String name;

    Project() {
    }

    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the id of the project
     *
     * @return the id of the project
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the name of the project
     *
     * @return the name of the project
     */
    public String getName() {
        return this.name;
    }

}
