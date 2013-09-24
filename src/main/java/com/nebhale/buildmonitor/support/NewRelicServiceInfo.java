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

package com.nebhale.buildmonitor.support;

import org.springframework.cloud.service.ServiceInfo;

final class NewRelicServiceInfo implements ServiceInfo {

    private final String id;

    private final String licenseKey;

    NewRelicServiceInfo(String id, String licenseKey) {
        this.id = id;
        this.licenseKey = licenseKey;
    }

    @ServiceProperty
    public String getId() {
        return id;
    }

    @ServiceProperty
    String getLicenseKey() {
        return licenseKey;
    }
}
