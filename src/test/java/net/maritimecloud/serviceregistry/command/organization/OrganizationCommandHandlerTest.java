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

import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import net.maritimecloud.common.infrastructure.axon.RepositoryMock;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceCreated;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationCreatedEvent;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 * The test of OrganizationCommandHandler is a bit more involved that the average AxonTest. The reason is that the fixture is only
 * supporting a single repository and we need three different Aggregates to perform all tests (Organization need a ServiceSpecification to
 * create a ServiceInstance. Hence the setup of to "Mocked" repositories that are used for handing us the prerequisites. 
 * <p>
 * @author Christoffer Børrild
 */
public class OrganizationCommandHandlerTest extends CommonFixture {

    private FixtureConfiguration fixtureServiceSpecification;
    private FixtureConfiguration fixtureServiceInstance;
    private Organization anOrganization;
    private final OrganizationId anOrganizationId = new OrganizationId(AN_ORG_ID);
    private final ServiceSpecificationId serviceSpecificationId = new ServiceSpecificationId(A_SPEC_ID);
    private ServiceSpecification serviceSpecification;
    private OrganizationCommandHandler commandHandler;
    private boolean mockDeleted = false;

    @Before
    public void setUp() {
        anOrganization = new Organization(generateCreateOrganizationCommand(AN_ORG_ID)) {

            @Override
            public boolean isDeleted() {
                return mockDeleted ? true : super.isDeleted(); //To change body of generated methods, choose Tools | Templates.
            }

        };
        serviceSpecification = new ServiceSpecification(anOrganizationId, serviceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY);

        // Setup a fixture with an OrganizationCommandHandler with mocked 
        // organization repository and aggregate
        fixtureServiceSpecification = Fixtures.newGivenWhenThenFixture(ServiceSpecification.class);
        commandHandler = new OrganizationCommandHandler();
        commandHandler.setOrganizationRepository(new RepositoryMock(anOrganization));
        commandHandler.setServiceSpecificationRepository(fixtureServiceSpecification.getRepository());
        commandHandler.setServiceInstanceRepository(null);
        fixtureServiceSpecification.registerAnnotatedCommandHandler(commandHandler);

        // Setup a fixture with an OrganizationCommandHandler with mocked 
        // organization- and serviceSpecification repositories and aggregates
        fixtureServiceInstance = Fixtures.newGivenWhenThenFixture(ServiceInstance.class);
        commandHandler = new OrganizationCommandHandler();
        commandHandler.setOrganizationRepository(new RepositoryMock(anOrganization));
        commandHandler.setServiceSpecificationRepository(new RepositoryMock(serviceSpecification));
        commandHandler.setServiceInstanceRepository(fixtureServiceInstance.getRepository());
        fixtureServiceInstance.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void prepareServiceSpecification() {
        fixtureServiceSpecification.given(new OrganizationCreatedEvent(anOrganizationId, A_NAME, A_SUMMARY, A_URL))
                .when(aPrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId))
                .expectEvents(new ServiceSpecificationCreatedEvent(
                                anOrganizationId,
                                new ServiceSpecificationId(A_SPEC_ID),
                                A_SERVICE_TYPE, A_NAME, A_SUMMARY)
                );
    }

    @Test
    public void prepareServiceSpecificationOnDeletedOrganization() {
        mockDeleted = true;
        fixtureServiceSpecification.given(new OrganizationCreatedEvent(anOrganizationId, A_NAME, A_SUMMARY, A_URL))
                .when(aPrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId))
                .expectException(IllegalArgumentException.class);
    }

// Duplicate test not currently possible !?!    
//    @Test
//    public void dublicatePrepareServiceSpecification() {
//        PrepareServiceSpecificationCommand prepareServiceSpecificationCommand
//                = aPrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId);
//
//        fixtureServiceSpecification.givenCommands(prepareServiceSpecificationCommand)
//                .when(prepareServiceSpecificationCommand)
//                .expectException(IllegalArgumentException.class);
//    }
//    
    @Test
    public void provideServiceInstance() {

        final ServiceInstanceId serviceInstanceId = new ServiceInstanceId(AN_INSTANCE_ID);

        fixtureServiceInstance.given(
                new OrganizationCreatedEvent(anOrganizationId, A_NAME, A_SUMMARY, A_URL),
                new ServiceSpecificationCreatedEvent(anOrganizationId, serviceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY)
        )
                .when(new ProvideServiceInstanceCommand(anOrganizationId, serviceSpecificationId, serviceInstanceId, A_NAME, A_SUMMARY, A_COVERAGE))
                .expectEvents(new ServiceInstanceCreated(
                                anOrganizationId,
                                serviceSpecificationId,
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                A_NAME, A_SUMMARY, A_COVERAGE, A_SERVICE_TYPE));
    }

}
