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
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointAdded;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import net.maritimecloud.common.infrastructure.axon.RepositoryMock;
import net.maritimecloud.serviceregistry.command.organization.Organization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationCommandHandler;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationCreated;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christoffer Børrild
 */
public class ServiceInstanceTest extends CommonFixture {

    private FixtureConfiguration<ServiceInstance> fixture;

    private final ServiceSpecificationId serviceSpecificationId = new ServiceSpecificationId(A_SPEC_ID);
    private final ServiceInstanceId serviceInstanceId = new ServiceInstanceId(AN_INSTANCE_ID);

    private ServiceInstanceCreated serviceInstanceCreatedEvent;
    private ServiceSpecificationCreated serviceSpecificationCreatedEvent;
    private OrganizationCreated organizationCreatedEvent;

    @Before
    public void setUp() throws Exception {

        // setup predefined events
        serviceInstanceCreatedEvent = new ServiceInstanceCreated(anOrganizationId, serviceSpecificationId, serviceInstanceId, A_NAME, A_SUMMARY, A_COVERAGE, A_SERVICE_TYPE);
        serviceSpecificationCreatedEvent = new ServiceSpecificationCreated(anOrganizationId, serviceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY);
        organizationCreatedEvent = new OrganizationCreated(anOrganizationId, A_NAME, A_SUMMARY, A_URL);

        // Setup a fixture with an OrganizationCommandHandler with mocked 
        // organization- and serviceSpecification repositories and aggregates
        fixture = Fixtures.newGivenWhenThenFixture(ServiceInstance.class);
        Organization anOrganization = new Organization(generateCreateOrganizationCommand(AN_ORG_ID));
        ServiceSpecification serviceSpecification = new ServiceSpecification(anOrganizationId, serviceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY);
        OrganizationCommandHandler commandHandler = new OrganizationCommandHandler();
        commandHandler.setOrganizationRepository(new RepositoryMock(anOrganization));
        commandHandler.setServiceSpecificationRepository(new RepositoryMock(serviceSpecification));
        commandHandler.setServiceInstanceRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);

    }

    @Test
    public void changeServiceInstanceNameAndSummary() {

        fixture.given(
                organizationCreatedEvent,
                serviceSpecificationCreatedEvent,
                serviceInstanceCreatedEvent
        )
                .when(new ChangeServiceInstanceNameAndSummary(serviceInstanceId, ANOTHER_NAME, ANOTHER_SUMMARY))
                .expectEvents(new ServiceInstanceNameAndSummaryChanged(serviceInstanceId, ANOTHER_NAME, ANOTHER_SUMMARY));
    }

    @Test
    public void changeServiceInstanceCoverage() {

        fixture.given(
                organizationCreatedEvent,
                serviceSpecificationCreatedEvent,
                serviceInstanceCreatedEvent
        )
                .when(new ChangeServiceInstanceCoverage(serviceInstanceId, ANOTHER_COVERAGE))
                .expectEvents(new ServiceInstanceCoverageChanged(serviceInstanceId, ANOTHER_COVERAGE));
    }

    @Test
    public void addEndpoint() {
        fixture.given(
                organizationCreatedEvent,
                serviceSpecificationCreatedEvent,
                serviceInstanceCreatedEvent
        )
                .when(new AddServiceInstanceEndpoint(serviceInstanceId, AN_ENDPOINT))
                .expectEvents(new ServiceInstanceEndpointAdded(serviceInstanceId, AN_ENDPOINT));
    }

    @Test
    public void removeEndpoint() {
        fixture.given(organizationCreatedEvent,
                serviceSpecificationCreatedEvent,
                serviceInstanceCreatedEvent,
                new ServiceInstanceEndpointAdded(serviceInstanceId, AN_ENDPOINT)
        )
                .when(new RemoveServiceInstanceEndpoint(serviceInstanceId, AN_ENDPOINT))
                .expectEvents(new ServiceInstanceEndpointRemoved(serviceInstanceId, AN_ENDPOINT));
    }

}
