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

import com.nebhale.buildmonitor.ApplicationConfiguration;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = SpringApplicationContextLoader.class)
public abstract class AbstractControllerTest extends AbstractTransactionalJUnit4SpringContextTests {

    protected volatile MockMvc mockMvc;

    @Autowired
    private volatile WebApplicationContext webApplicationContext;

    @Before
    public final void mockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    protected final String toJson(String... pairs) {
        StringBuilder sb = new StringBuilder("{ ");

        Set<String> entries = new HashSet<>(pairs.length);
        for (String pair : pairs) {
            String[] parts = StringUtils.split(pair, ":");
            entries.add(String.format("\"%s\" : \"%s\"", parts[0], parts[1]));
        }

        sb.append(StringUtils.collectionToDelimitedString(entries, ", "));

        return sb.append(" }").toString();
    }
}
