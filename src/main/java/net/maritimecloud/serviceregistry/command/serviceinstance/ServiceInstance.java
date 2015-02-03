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

import net.maritimecloud.serviceregistry.command.api.ServiceInstanceNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointRemoved;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Christoffer Børrild
 */
@Component
public class ServiceInstance extends AbstractAnnotatedAggregateRoot<ServiceInstanceId> {

    @AggregateIdentifier
    private ServiceInstanceId serviceInstanceId;
    private OrganizationId providerId;
    private ServiceSpecificationId specificationId;
    private String name;
    private String summary;
    private Coverage coverage;
    private Set<URI> endpoints;

    protected ServiceInstance() {
        endpoints = new HashSet<>();
    }

    public ServiceInstance(
            OrganizationId providerId,
            ServiceSpecificationId specificationId,
            ServiceInstanceId serviceInstanceId,
            String name,
            String summary,
            Coverage coverage,
            ServiceType serviceType
    ) {
        this();
        this.providerId = providerId;
        this.specificationId = specificationId;
        this.serviceInstanceId = serviceInstanceId;
        this.name = name;
        this.summary = summary;
        this.coverage = coverage;
        apply(new ServiceInstanceCreated(providerId, specificationId, serviceInstanceId, name, summary, coverage, serviceType));
    }

    @CommandHandler
    public void handle(ChangeServiceInstanceNameAndSummary command) {
        apply(new ServiceInstanceNameAndSummaryChanged(command.getServiceInstanceId(), command.getName(), command.getSummary()));
    }

    @CommandHandler
    public void handle(ChangeServiceInstanceCoverage command) {
        apply(new ServiceInstanceCoverageChanged(command.getServiceInstanceId(), command.getCoverage()));
    }

    @CommandHandler
    public void handle(AddServiceInstanceEndpoint command) {
        apply(new ServiceInstanceEndpointAdded(command.getServiceInstanceId(), command.getServiceEndpoint()));
    }

    @CommandHandler
    public void handle(RemoveServiceInstanceEndpoint command) {
        apply(new ServiceInstanceEndpointRemoved(command.getServiceInstanceId(), command.getServiceEndpoint()));
    }

    @EventSourcingHandler
    public void on(ServiceInstanceCreated event) {
        this.serviceInstanceId = event.getServiceInstanceId();
        this.providerId = event.getProviderId();
        this.specificationId = event.getSpecificationId();
        this.name = event.getName();
        this.summary = event.getSummary();
        this.coverage = event.getCoverage();
    }

    @EventSourcingHandler
    public void on(ServiceInstanceEndpointAdded event) {
        endpoints.add(event.getServiceEndpoint().getUri());
    }

    @EventSourcingHandler
    public void on(ServiceInstanceEndpointRemoved event) {
        endpoints.remove(event.getServiceEndpoint().getUri());
    }

}
