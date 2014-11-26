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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.serviceregistry.command.Command;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;

/**
 *
 * @author Christoffer Børrild
 */
public class PrepareServiceSpecificationCommand implements Command {

    @TargetAggregateIdentifier
    private final OrganizationId ownerId;
    private final ServiceSpecificationId serviceSpecificationId;
    private final ServiceType serviceType;
    private final String name;
    private final String summary;

    @JsonCreator
    public PrepareServiceSpecificationCommand(
            @JsonProperty("ownerId") OrganizationId ownerId,
            @JsonProperty("serviceSpecificationId") ServiceSpecificationId serviceSpecificationId,
            @JsonProperty("serviceType") ServiceType serviceType,
            @JsonProperty("name") String name,
            @JsonProperty("summary") String summary
    ) {
        Assert.notNull(ownerId, "The organizationId of the owning organization must be provided");
        Assert.notNull(serviceSpecificationId, "The serviceSpecificationId must be provided");
        Assert.notNull(name, "The provided name cannot be null");
        Assert.notNull(summary, "The provided summary cannot be null");
        Assert.notNull(serviceType, "The provided serviceType cannot be null");

        this.ownerId = ownerId;
        this.serviceSpecificationId = serviceSpecificationId;
        this.serviceType = serviceType;
        this.name = name;
        this.summary = summary;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public ServiceSpecificationId getServiceSpecificationId() {
        return serviceSpecificationId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

}
