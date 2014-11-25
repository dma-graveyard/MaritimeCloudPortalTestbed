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
package net.maritimecloud.serviceregistry.command.organization;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationCreatedEvent {

    @TargetAggregateIdentifier
    private final OrganizationId organizationId;
    private final String name;
    private final String summary;
    private final String url;

    public OrganizationCreatedEvent(OrganizationId organizationId, String name, String summary, String url) {
        this.organizationId = organizationId;
        this.name = name;
        this.summary = summary;
        this.url = url;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getUrl() {
        return url;
    }

}
