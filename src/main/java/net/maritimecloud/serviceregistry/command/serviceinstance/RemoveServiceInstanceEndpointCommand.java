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
import net.maritimecloud.serviceregistry.command.Command;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;

/**
 * @author Christoffer Børrild
 */
public class RemoveServiceInstanceEndpointCommand implements Command {

    @TargetAggregateIdentifier
    private final ServiceInstanceId serviceInstanceId;
    private final ServiceEndpoint serviceEndpoint;

    @JsonCreator
    public RemoveServiceInstanceEndpointCommand(
            @JsonProperty("serviceInstanceId") ServiceInstanceId serviceInstanceId,
            @JsonProperty("serviceEndpoint") ServiceEndpoint serviceEndpoint
    ) {
        Assert.notNull(serviceInstanceId, "The serviceInstanceId must be provided");
        Assert.notNull(serviceEndpoint, "The serviceEndpoint must be provided");
        this.serviceInstanceId = serviceInstanceId;
        this.serviceEndpoint = serviceEndpoint;
    }

    public ServiceInstanceId getServiceInstanceId() {
        return serviceInstanceId;
    }

    public ServiceEndpoint getServiceEndpoint() {
        return serviceEndpoint;
    }
    
}