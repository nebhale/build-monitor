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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfiguration.class)
@Transactional
@WebAppConfiguration
public abstract class AbstractControllerTest {

    private volatile JdbcTemplate jdbcTemplate;

    volatile MockMvc mockMvc;

    @Autowired
    final void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    final void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    final int countRowsInTable(String tableName) {
        return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, tableName);
    }

    final String toJson(String... pairs) {
        StringBuilder sb = new StringBuilder("{ ");

        Set<String> entries = Arrays.stream(pairs).map(pair -> {
            String[] parts = StringUtils.split(pair, ":");
            return String.format("\"%s\" : \"%s\"", parts[0], parts[1]);
        }).collect(Collectors.toSet());

        sb.append(StringUtils.collectionToDelimitedString(entries, ", "));

        return sb.append(" }").toString();
    }

}
