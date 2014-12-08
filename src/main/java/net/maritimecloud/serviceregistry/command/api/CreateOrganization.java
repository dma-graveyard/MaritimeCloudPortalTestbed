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
package net.maritimecloud.serviceregistry.command.api;

import net.maritimecloud.serviceregistry.command.Command;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;

/**
 *
 * @author Christoffer Børrild
 */
public class CreateOrganization implements Command {

    @TargetAggregateIdentifier
    private final OrganizationId organizationId;
    private final String name;
    private final String summary;
    private final String url;

    @JsonCreator
    public CreateOrganization(
            @JsonProperty("organizationId") OrganizationId organizationId,
            @JsonProperty("name") String name,
            @JsonProperty("summary") String summary,
            @JsonProperty("url") String url
    ) {
        Assert.notNull(organizationId, "The organizationId must be provided");
        Assert.notNull(name, "The provided name cannot be null");
        Assert.notNull(summary, "The provided summary cannot be null");

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
