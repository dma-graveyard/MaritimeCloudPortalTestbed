/* Copyright 2014 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.maritimecloud.serviceregistry.servicespecification;

import net.maritimecloud.serviceregistry.organization.*;

/**
 * @author Christoffer Børrild
 */
public class ServiceSpecificationNameAndSummaryChangedEvent {

    private final ServiceSpecificationId serviceSpecificationId;
    private final String name;
    private final String summary;

    public ServiceSpecificationNameAndSummaryChangedEvent(ServiceSpecificationId serviceSpecificationId, String name, String summary) {
        this.serviceSpecificationId = serviceSpecificationId;
        this.name = name;
        this.summary = summary;
    }

    public ServiceSpecificationId getServiceSpecificationId() {
        return serviceSpecificationId;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

}
