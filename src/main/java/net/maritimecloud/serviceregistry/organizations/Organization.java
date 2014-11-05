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
package net.maritimecloud.serviceregistry.organizations;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

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
public class Organization extends AbstractAnnotatedAggregateRoot {

    @AggregateIdentifier
    private OrganizationId organizationId;
    private String name;
    private String summary;

    public Organization() {
    }

    @CommandHandler
    public Organization(CreateOrganizationCommand command) {
        apply(new OrganizationCreatedEvent(command.getOrganizationId(), command.getName(), command.getSummary()));
    }

    @CommandHandler
    public void markCompleted(ChangeOrganizationNameAndSummaryCommand command) {
        apply(new OrganizationNameAndSummaryChangedEvent(command.getOrganizationId(), command.getName(), command.getSummary()));
    }

    @EventSourcingHandler
    public void on(OrganizationCreatedEvent event) {
        this.organizationId = event.getOrganizationId();
        this.name = event.getName();
        this.summary = event.getSummary();
    }

    @EventSourcingHandler
    public void on(OrganizationNameAndSummaryChangedEvent event) {
        this.organizationId = event.getOrganizationId();
        this.name = event.getName();
        this.summary = event.getSummary();
    }

//    private OrganizationId organizationId;
//    private String name;
//    private String summary;
//    private HashSet<ServiceSpecification> serviceSpecifications;
//
//    public Organization(OrganizationId anOrganizationId, String aName, String aSummary) {
//        this();
//
//        setOrganizationId(anOrganizationId);
//        setName(aName);
//        setSummary(aSummary);
//    }
//
//    private Organization() {
//        super();
//
//        this.setServiceSpecifications(new HashSet<>(0));
//    }
//
//    public OrganizationId organizationId() {
//        return organizationId;
//    }
//
//    public String name() {
//        return name;
//    }
//
//    public String summary() {
//        return summary;
//    }
//
//    public Set<ServiceSpecification> allServiceSpecifications() {
//        return Collections.unmodifiableSet(this.serviceSpecifications());
//    }
//
//    public void changeName(String aNewName) {
//        setName(aNewName);
//    }
//
//    void changeSummary(String aNewSummary) {
//        setSummary(aNewSummary);
//    }
//
//    public ServiceSpecification createDraftServiceSpecification(
//            ServiceSpecificationId aNewServiceSpecificationId,
//            String aName,
//            String aSummary) {
//
//        ServiceSpecification serviceSpecification = new ServiceSpecification(
//                this.organizationId,
//                aNewServiceSpecificationId,
//                aName,
//                aSummary,
//                ServiceSpecificationStatus.DRAFT
//        );
//
//        DomainEventPublisher.instance().publish(
//                new DraftServiceSpecificationCreated(
//                        serviceSpecification.organizationId(),
//                        serviceSpecification.serviceSpecificationId(),
//                        serviceSpecification.name(),
//                        serviceSpecification.summary()
//                )
//        );
//
//        return serviceSpecification;
//    }
//
//    public void publishServiceSpecification(ServiceSpecification serviceSpecification, String aVersionId) {
//        // assert that everything is set
//        // ...todo
//
//        serviceSpecifications().add(serviceSpecification);
//    }
//
//    private Set<ServiceSpecification> serviceSpecifications() {
//        return serviceSpecifications;
//    }
//
//    private void setOrganizationId(OrganizationId anOrganizationId) {
//        this.assertArgumentNotNull(anOrganizationId, "The organizationId must be provided.");
//
//        this.organizationId = anOrganizationId;
//    }
//
//    private void setName(String aName) {
//        this.assertArgumentNotEmpty(aName, "The name must be provided.");
//        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");
//
//        this.name = aName;
//    }
//
//    private void setSummary(String aSummary) {
//        this.assertArgumentNotEmpty(aSummary, "The summary must be provided.");
//        this.assertArgumentLength(aSummary, 500, "Summary must be 500 characters or less.");
//
//        this.summary = aSummary;
//    }
//
//    private void setServiceSpecifications(HashSet<ServiceSpecification> serviceSpecifications) {
//        this.serviceSpecifications = serviceSpecifications;
//    }
}
