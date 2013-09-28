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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * A project who's build should be monitored
 */
@Entity
public final class Project {

    @Id
    @Size(min = 1, max = 8)
    private volatile String key;

    @Size(min = 1, max = 64)
    private volatile String name;

    Project() {
    }

    /**
     * Creates a new instance
     *
     * @param key  the key of the project
     * @param name the name of the project
     */
    public Project(String key, String name) {
        this.key = key.toUpperCase();
        this.name = name;
    }

    /**
     * Returns the key of the project
     *
     * @return the key of the project
     */
    public String getKey() {
        return this.key;
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
