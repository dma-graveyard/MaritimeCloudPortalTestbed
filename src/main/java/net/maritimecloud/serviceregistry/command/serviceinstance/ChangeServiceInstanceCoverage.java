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
package net.maritimecloud.serviceregistry.command.serviceinstance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.maritimecloud.serviceregistry.infrastructure.jackson.CoverageDeserializer;
import net.maritimecloud.serviceregistry.infrastructure.jackson.CoverageSerializer;
import net.maritimecloud.common.cqrs.Command;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;

/**
 *
 * @author Christoffer Børrild
 */
public class ChangeServiceInstanceCoverage implements Command {

    @TargetAggregateIdentifier
    private final ServiceInstanceId serviceInstanceId;
    private final Coverage coverage;

    @JsonCreator
    public ChangeServiceInstanceCoverage(
            @JsonProperty("serviceInstanceId") ServiceInstanceId serviceInstanceId,
            @JsonProperty("coverage") @JsonSerialize(using = CoverageSerializer.class) @JsonDeserialize(using = CoverageDeserializer.class) Coverage coverage
    ) {
        Assert.notNull(serviceInstanceId, "The serviceInstanceId must be provided");
        this.serviceInstanceId = serviceInstanceId;
        this.coverage = coverage;
    }

    public ServiceInstanceId getServiceInstanceId() {
        return serviceInstanceId;
    }

    public Coverage getCoverage() {
        return coverage;
    }
}
