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

import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.springframework.stereotype.Component;

/**
 * Responsibilities (in the ServiceRegistry context):
 * <p>
 * Owns ServiceSpecifications and provides ServiceInstances
 * <p>
 * Makes sure that ServiceSpecifications and ServiceInstances has a unique identity within the owning organization
 * <p>
 * Maintains the lists of ServiceSpecifications and ServiceInstances held by an organization
 * <p>
 * @author Christoffer Børrild
 */
@Component
public class Organization extends AbstractAnnotatedAggregateRoot<OrganizationId> {

    @AggregateIdentifier
    private OrganizationId organizationId;
    private String name;
    private String summary;

    protected Organization() {
    }

    @CommandHandler
    public Organization(CreateOrganizationCommand command) {
        apply(new OrganizationCreatedEvent(command.getOrganizationId(), command.getName(), command.getSummary(), command.getUrl()));
    }

    @CommandHandler
    public void handle(ChangeOrganizationNameAndSummaryCommand command) {
        apply(new OrganizationNameAndSummaryChangedEvent(command.getOrganizationId(), command.getName(), command.getSummary()));
    }

    @EventSourcingHandler
    public void on(OrganizationCreatedEvent event) {
        this.organizationId = event.getOrganizationId();
    }

    /**
     * Factory for creating a ServiceSpecification in "prepare"/"draft" mode.
     * <p>
     * ( Even thought this factory makes the CommandHandler for this use case somewhat cumbersome to test it serves the DDD valid purpose of
     * guarding the invariant that a ServiceSpecification cannot be created for a non-existing or deleted Organization )
     */
    public ServiceSpecification prepareServiceSpecification(ServiceSpecificationId serviceSpecificationId, ServiceType serviceType, String name, String summary) {
        return new ServiceSpecification(organizationId, serviceSpecificationId, serviceType, name, summary);
    }

    /**
     * Factory for creating a ServiceInstance to be published by the Organization.
     * <p>
     * ( Even thought this factory makes the CommandHandler for this use case somewhat cumbersome to test it serves the DDD valid purpose of
     * guarding the invariant that a ServiceInstance cannot be created for a non-existing or deleted Organization )
     */
    public ServiceInstance provideServiceInstance(
            ServiceSpecification specification, ServiceInstanceId serviceInstanceId, String name, String summary, Coverage coverage) {
        return specification.materialize(organizationId, serviceInstanceId, name, summary, coverage);
    }
}
