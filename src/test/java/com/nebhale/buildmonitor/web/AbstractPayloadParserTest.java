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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebhale.buildmonitor.domain.Build;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractPayloadParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebHookController.PayloadParser payloadParser;

    protected AbstractPayloadParserTest(WebHookController.PayloadParser payloadParser) {
        this.payloadParser = payloadParser;
    }

    protected final void assertShouldProcess(String filename, Boolean shouldProcess) throws IOException {
        Assert.assertEquals(shouldProcess, this.payloadParser.shouldProcess(getPayload(filename)));
    }

    protected final void assertState(String filename, Build.State state) throws IOException {
        Assert.assertEquals(state, this.payloadParser.getState(getPayload(filename)));
    }

    protected final void assertUri(String filename, String uri) throws IOException {
        Assert.assertEquals(uri, this.payloadParser.getUri(getPayload(filename)));
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> getPayload(String filename) throws IOException {
        return this.objectMapper.readValue(new File("src/test/resources/" + filename), Map.class);
    }
}
