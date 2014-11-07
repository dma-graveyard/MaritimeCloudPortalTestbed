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

import net.maritimecloud.common.infrastructure.axon.RepositoryMock;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationCreatedEvent;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import org.axonframework.repository.Repository;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationCommandHandlerTest {

    private FixtureConfiguration fixture;
    private Repository<Organization> organizationRepository;
    private Organization anOrganization;
    private final OrganizationId anOrganizationId = new OrganizationId("an organization id");
    private final ServiceSpecificationId serviceSpecificationId = new ServiceSpecificationId("a ServiceSpecification id");

    @Before
    public void setUp() {
        anOrganization = Mockito.mock(Organization.class);
        organizationRepository = new RepositoryMock(anOrganization);

        fixture = Fixtures.newGivenWhenThenFixture(ServiceSpecification.class);
        OrganizationCommandHandler commandHandler = new OrganizationCommandHandler();
        commandHandler.setRepository(organizationRepository);
        commandHandler.setServiceSpecificationRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void prepareServiceSpecification() {
        Mockito.when(anOrganization.isDeleted()).thenReturn(false);
        Mockito.when(anOrganization.prepareServiceSpecification(serviceSpecificationId, "a name", "a summary ..."))
                .thenReturn(new ServiceSpecification(anOrganizationId, serviceSpecificationId, "a name", "a summary ..."));
        Mockito.when(anOrganization.getIdentifier()).thenReturn(anOrganizationId);

        fixture.given(new OrganizationCreatedEvent(anOrganizationId, "a name", "a summary ..."))
                .when(new PrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId, "a name", "a summary ..."))
                .expectEvents(new ServiceSpecificationCreatedEvent(anOrganizationId, new ServiceSpecificationId("a ServiceSpecification id"), "a name", "a summary ..."));
    }

    @Test
    public void prepareServiceSpecificationOnDeletedOrganization() {
        Mockito.when(anOrganization.isDeleted()).thenReturn(true);

        fixture.given(new OrganizationCreatedEvent(anOrganizationId, "a name", "a summary ..."))
                .when(new PrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId, "a name", "a summary ..."))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void dublicatePrepareServiceSpecification() {
        Mockito.when(anOrganization.isDeleted()).thenReturn(false);
        Mockito.when(anOrganization.prepareServiceSpecification(serviceSpecificationId, "a name", "a summary ..."))
                .thenReturn(new ServiceSpecification(anOrganizationId, serviceSpecificationId, "a name", "a summary ..."));
        Mockito.when(anOrganization.getIdentifier()).thenReturn(anOrganizationId);

        PrepareServiceSpecificationCommand prepareServiceSpecificationCommand
                = new PrepareServiceSpecificationCommand(anOrganizationId, serviceSpecificationId, "a name", "a summary ...");

        fixture.givenCommands(prepareServiceSpecificationCommand)
                .when(prepareServiceSpecificationCommand)
                .expectException(IllegalArgumentException.class);
    }

}
