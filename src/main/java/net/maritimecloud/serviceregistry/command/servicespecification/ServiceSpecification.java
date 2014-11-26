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
        apply(new ServiceSpecificationCreatedEvent(organizationId, serviceSpecificationId, serviceType, name, summary));
    }

    @CommandHandler
    public void handle(ChangeServiceSpecificationNameAndSummaryCommand command) {
        apply(new ServiceSpecificationNameAndSummaryChangedEvent(command.getServiceSpecificationId(), command.getName(), command.getSummary()));
    }

    @EventSourcingHandler
    public void on(ServiceSpecificationCreatedEvent event) {
        this.serviceSpecificationId = event.getServiceSpecificationId();
        this.organizationId = event.getOwnerId();
    }

}
