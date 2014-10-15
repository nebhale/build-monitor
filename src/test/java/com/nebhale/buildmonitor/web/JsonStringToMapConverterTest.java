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

package com.nebhale.buildmonitor.web;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public final class JsonStringToMapConverterTest {

    private final JsonStringToMapConverter converter = new JsonStringToMapConverter();

    @Test
    public void convert() {
        Map<?, ?> result = this.converter.convert("{ \"test-key\": true }");
        assertTrue((Boolean) result.get("test-key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertFailure() {
        this.converter.convert("{");
    }

}
