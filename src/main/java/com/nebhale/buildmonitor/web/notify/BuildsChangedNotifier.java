/*
 * Copyright 2013-2014 the original author or authors.
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

package com.nebhale.buildmonitor.web.notify;

import com.nebhale.buildmonitor.domain.Project;

/**
 * Used to notify listeners of changes to the collection of builds within a project
 */
public interface BuildsChangedNotifier {

    /**
     * Notifies listeners that the collection of builds within a project have changed
     *
     * @param project the project with changed builds
     */
    void buildsChanged(Project project);
}
