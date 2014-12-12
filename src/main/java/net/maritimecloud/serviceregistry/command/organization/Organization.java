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

import java.util.HashMap;
import java.util.Map;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.OrganizationNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.ServiceInstancePrimaryAliasAdded;
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
    private Map<String, ServiceInstanceId> aliases;

    protected Organization() {
        aliases = new HashMap<>();
    }

    @CommandHandler
    public Organization(CreateOrganization command) {
        this();
        apply(new OrganizationCreated(command.getOrganizationId(), command.getName(), command.getSummary(), command.getUrl()));
    }

    @CommandHandler
    public void handle(ChangeOrganizationNameAndSummary command) {
        apply(new OrganizationNameAndSummaryChanged(command.getOrganizationId(), command.getName(), command.getSummary()));
    }

    @CommandHandler
    public void handle(AddServiceInstanceAlias command) {
        ServiceInstanceId serviceInstanceId = aliases.get(command.getAlias());
        if (serviceInstanceId != null) {
            if (serviceInstanceId != command.getServiceInstanceId()) {
                // alias already in use by other target -> action denied
                apply(new ServiceInstanceAliasRegistrationDenied(command.getOrganizationId(), command.getServiceInstanceId(), command.getAlias()));
            }
            // idempotent, ok to register same alias and instance twice
            return;
        }
        if (aliases.containsValue(command.getServiceInstanceId())) {
            apply(new ServiceInstanceAliasAdded(command.getOrganizationId(), command.getServiceInstanceId(), command.getAlias()));
        } else {
            // there's a first time for everything
            apply(new ServiceInstancePrimaryAliasAdded(command.getOrganizationId(), command.getServiceInstanceId(), command.getAlias()));
        }
    }

    // Remove alias should ignore/fail on remove of last alias, as this one is locked for removal (primary alias)!
    @EventSourcingHandler
    public void on(OrganizationCreated event) {
        this.organizationId = event.getOrganizationId();
    }

    @EventSourcingHandler
    public void on(ServiceInstanceAliasAdded event) {
        aliases.put(event.getAlias(), event.getServiceInstanceId());
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
