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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * A build for a {@link Project}
 */
@Entity
public final class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private volatile Integer id;

    private volatile Date created;

    @ManyToOne
    @NotNull
    private volatile Project project;

    @Size(min = 1, max = 256)
    private volatile String uri;

    @NotNull
    private volatile State state;

    Build() {
    }

    /**
     * Creates a new instance
     *
     * @param project the project that the build belongs to
     * @param uri     the uri of the build
     * @param state   the state of the build
     */
    public Build(Project project, String uri, State state) {
        this.created = new Date();
        this.project = project;
        this.uri = uri;
        this.state = state;
    }

    /**
     * Returns the id of the build
     *
     * @return the id of the build
     */
    @JsonIgnore
    public Integer getId() {
        return this.id;
    }

    /**
     * Returns the date that the build was created
     *
     * @return the date that the build was created
     */
    public Date getCreated() {
        return this.created;
    }

    /**
     * Returns the project that the build belongs to
     *
     * @return the project that the build belongs to
     */
    @JsonIgnore
    public Project getProject() {
        return this.project;
    }

    /**
     * Returns the uri of the build
     *
     * @return the uri of the build
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Returns the state of the build
     *
     * @return the state of the build
     */
    public State getState() {
        return this.state;
    }

    /**
     * Set the state of the build
     *
     * @param state the state of the build
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * The state of a {@link Build}
     */
    public enum State {

        /**
         * The build has failed
         */
        FAIL,

        /**
         * The build is currently in progress
         */
        IN_PROGRESS,

        /**
         * The build has passed
         */
        PASS,

        /**
         * The build is in an unknown state
         */
        UNKNOWN
    }

}
