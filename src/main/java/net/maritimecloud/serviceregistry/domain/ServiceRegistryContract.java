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
package net.maritimecloud.serviceregistry.domain;

import net.maritimecloud.cqrs.tool.CqrsContract;
import net.maritimecloud.cqrs.tool.Event;
import net.maritimecloud.cqrs.tool.Command;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;

/**
 * This interface describes the interface to the ServiceRegitry in terms of Commands and Events. Whereas the commands are explicitly being
 * the published interface, the description of the events are more like a convenience as they in most cases share the properties of the
 * commands.
 * <p>
 * Events may inherit their properties from corresponding commands, and may extend those properties, as would be needed to make room for
 * enrichment.
 * <p>
 * <p>
 * @author Christoffer Børrild
 */
public interface ServiceRegistryContract extends CqrsContract {

    @Command
    void addServiceInstanceAlias(ServiceInstanceId serviceInstanceId, String alias);

    @Command
    void addServiceInstanceEndpoint(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);

    @Command
    void changeServiceInstanceNameAndSummaryCommand(ServiceInstanceId serviceInstanceId, String name, String summary);

    @Command
    void RemoveServiceInstanceEndpointCommand(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);
    
    @Event
    void serviceInstanceAliasAdded(ServiceInstanceId serviceInstanceId, String alias);

    @Event
    void serviceInstanceCreatedEvent(
            OrganizationId providerId,
            ServiceSpecificationId specificationId,
            ServiceInstanceId serviceInstanceId,
            String name,
            String summary,
            Coverage coverage,
            // enrich with redundant data
            ServiceType serviceType);
    
    @Event
    void serviceInstanceEndpointAddedEvent(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);
    
    @Event
    void serviceInstanceEndpointRemovedEvent(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);
    
    @Event
    void serviceInstanceNameAndSummaryChangedEvent(ServiceInstanceId serviceInstanceId, String name, String summary);
    
    
}
