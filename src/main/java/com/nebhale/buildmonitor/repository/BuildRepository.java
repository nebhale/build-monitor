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

package com.nebhale.buildmonitor.repository;

import com.nebhale.buildmonitor.domain.Build;
import com.nebhale.buildmonitor.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for working with {@link Build}s
 */
public interface BuildRepository extends JpaRepository<Build, Integer> {

    /**
     * Returns all builds belonging to a project sorted by their creation date, descending
     *
     * @param project  the project that the builds belong to
     * @param pageable the paging configuration
     *
     * @return all builds belonging to a project sorted by their creation date, descending
     */
    Page<Build> findAllByProjectOrderByCreatedDesc(Project project, Pageable pageable);

    /**
     * Returns a build uniquely identified by it's URI, otherwise {@literal null}
     *
     * @param uri the URI that uniquely identifies a build
     *
     * @return a build uniquely identified by it's URI, otherwise {@literal null}
     */
    Build findByUri(String uri);

}
