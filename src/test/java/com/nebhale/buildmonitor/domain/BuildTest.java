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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public final class BuildTest {

    private final Project project = new Project("test-key", "test-name");

    @Test
    public void test() {
        Build build = new Build(this.project, "test-uri", Build.State.PASS);

        assertNull(build.getId());
        assertNotNull(build.getCreated());
        assertEquals(this.project, build.getProject());
        assertEquals("test-uri", build.getUri());
        assertEquals(Build.State.PASS, build.getState());

        build.setState(Build.State.FAIL);

        assertEquals(Build.State.FAIL, build.getState());
    }
}
