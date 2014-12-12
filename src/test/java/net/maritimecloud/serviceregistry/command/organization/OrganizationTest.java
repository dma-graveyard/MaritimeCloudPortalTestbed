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

import net.maritimecloud.serviceregistry.command.api.OrganizationNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.AN_ALIAS;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstancePrimaryAliasAdded;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationTest extends CommonFixture {

    private FixtureConfiguration<Organization> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Organization.class);
    }

    @Test
    public void createOrganization() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new CreateOrganization(new OrganizationId("an organization id"), "a name", "a summary ...", A_URL))
                .expectEvents(new OrganizationCreated(new OrganizationId("an organization id"), "a name", "a summary ...", A_URL));
    }

    @Test
    public void changeOrganizationNameAndSummary() throws Exception {
        fixture.given(new OrganizationCreated(new OrganizationId("an organization id"), "a name", "a summary ...", A_URL))
                .when(new ChangeOrganizationNameAndSummary(new OrganizationId("an organization id"), "a new name", "a new summary ..."))
                .expectEvents(new OrganizationNameAndSummaryChanged(new OrganizationId("an organization id"), "a new name", "a new summary ..."));
    }

    @Test
    public void firstAddServiceInstanceAlias() {
        fixture.given(
                organizationCreatedEvent(),
                serviceSpecificationCreatedEvent(),
                serviceInstanceCreatedEvent()
        )
                .when(new AddServiceInstanceAlias(anOrganizationId, aServiceInstanceId, AN_ALIAS))
                .expectEvents(new ServiceInstancePrimaryAliasAdded(anOrganizationId, aServiceInstanceId, AN_ALIAS));
    }

    @Test
    public void subsequentAddServiceInstanceAlias() {
        fixture.given(
                organizationCreatedEvent(),
                serviceSpecificationCreatedEvent(),
                serviceInstanceCreatedEvent(),
                new ServiceInstancePrimaryAliasAdded(anOrganizationId, aServiceInstanceId, AN_ALIAS)
        )
                .when(new AddServiceInstanceAlias(anOrganizationId, aServiceInstanceId, ANOTHER_ALIAS))
                .expectEvents(new ServiceInstanceAliasAdded(anOrganizationId, aServiceInstanceId, ANOTHER_ALIAS));
    }

    @Test
    public void removeServiceInstanceAlias() {
        fixture.given(
                organizationCreatedEvent(),
                serviceSpecificationCreatedEvent(),
                serviceInstanceCreatedEvent(),
                new ServiceInstancePrimaryAliasAdded(anOrganizationId, aServiceInstanceId, AN_ALIAS),
                new ServiceInstanceAliasAdded(anOrganizationId, aServiceInstanceId, ANOTHER_ALIAS)
        )
                .when(new RemoveServiceInstanceAlias(anOrganizationId, aServiceInstanceId, ANOTHER_ALIAS))
                .expectEvents(new ServiceInstanceAliasRemoved(anOrganizationId, ANOTHER_ALIAS));
    }

    @Test
    public void duplicateAddServiceInstanceAliasShouldBeDenied() {
        fixture.given(
                organizationCreatedEvent(),
                serviceSpecificationCreatedEvent(),
                serviceInstanceCreatedEvent(),
                new ServiceInstanceAliasAdded(anOrganizationId, aServiceInstanceId, AN_ALIAS)
        )
                .when(new AddServiceInstanceAlias(anOrganizationId, anotherServiceInstanceId, AN_ALIAS))
                .expectEvents(new ServiceInstanceAliasRegistrationDenied(anOrganizationId, anotherServiceInstanceId, AN_ALIAS));
    }

    @Test
    public void ResubmittedAddDuplicateServiceInstanceAliasShouldBeIgnored() {
        fixture.given(
                organizationCreatedEvent(),
                serviceSpecificationCreatedEvent(),
                serviceInstanceCreatedEvent(),
                new ServiceInstanceAliasAdded(anOrganizationId, aServiceInstanceId, AN_ALIAS)
        )
                .when(new AddServiceInstanceAlias(anOrganizationId, aServiceInstanceId, AN_ALIAS))
                .expectEvents();
    }

}
