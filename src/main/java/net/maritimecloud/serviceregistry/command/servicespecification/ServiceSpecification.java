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
package net.maritimecloud.serviceregistry.command.servicespecification;

import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
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
public class ServiceSpecification extends AbstractAnnotatedAggregateRoot<ServiceSpecificationId> {

    @AggregateIdentifier
    private ServiceSpecificationId serviceSpecificationId;
    private OrganizationId organizationId;
    private ServiceType serviceType;
    private String name;
    private String summary;

    protected ServiceSpecification() {
    }

    public ServiceSpecification(
            OrganizationId organizationId,
            ServiceSpecificationId serviceSpecificationId,
            ServiceType serviceType,
            String name,
            String summary
    ) {
        this.organizationId = organizationId;
        this.serviceSpecificationId = serviceSpecificationId;
        this.serviceType = serviceType;
        this.name = name;
        this.summary = summary;
        apply(new ServiceSpecificationCreatedEvent(organizationId, serviceSpecificationId, serviceType, name, summary));
    }

    @CommandHandler
    public void handle(ChangeServiceSpecificationNameAndSummaryCommand command) {
        this.name = command.getName();
        this.summary = command.getSummary();
        apply(new ServiceSpecificationNameAndSummaryChangedEvent(command.getServiceSpecificationId(), command.getName(), command.getSummary()));
    }

    @EventSourcingHandler
    public void on(ServiceSpecificationCreatedEvent event) {
        this.serviceSpecificationId = event.getServiceSpecificationId();
        this.organizationId = event.getOwnerId();
        this.serviceType = event.getServiceType();
        this.name = event.getName();
        this.summary = event.getSummary();
    }

    @EventSourcingHandler
    public void on(ServiceSpecificationNameAndSummaryChangedEvent event) {
        this.name = event.getName();
        this.summary = event.getSummary();
    }

    /**
     * Factory method that will create (materialize) a ServiceInstance of this specification to be provisioned by an organization.
     * <p>
     * @param organizationId
     * @param serviceInstanceId
     * @param name
     * @param summary
     * @param coverage
     * @return
     */
    public ServiceInstance materialize(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String name, String summary, Coverage coverage) {
        return new ServiceInstance(organizationId, serviceSpecificationId, serviceInstanceId, name, summary, coverage, serviceType);
    }

}
