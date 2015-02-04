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
import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.AN_ALIAS;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.A_NAME;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.A_SUMMARY;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.A_URL;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.anOrganizationId;
import net.maritimecloud.common.spring.ApplicationContextProvider;
import net.maritimecloud.serviceregistry.command.api.AddOrganizationAlias;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ApplyForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationWebsiteUrl;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationPrimaryAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationWebsiteUrlChanged;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstancePrimaryAliasAdded;
import net.maritimecloud.serviceregistry.command.api.UserAppliedForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.organization.membership.MembershipId;
import net.maritimecloud.serviceregistry.domain.service.AliasGroups;
import net.maritimecloud.serviceregistry.domain.service.AliasService;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationTest extends CommonFixture {

    private FixtureConfiguration<Organization> fixture;
    private AliasService mockedAliasService;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Organization.class);

        // just some ugly mocking for the organization alias test : (
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        mockedAliasService = Mockito.mock(AliasService.class);
        new ApplicationContextProvider().setApplicationContext(applicationContext);
        when(applicationContext.getBean("aliasService")).thenReturn(mockedAliasService);

    }

    private static final MembershipId aMembershipId = new MembershipId("A_MEMBERSHIP_ID");

    @Test
    public void requestMembershipToOrganization() throws Exception {
        fixture.given(new OrganizationCreated(anOrganizationId, AN_ALIAS, A_NAME, A_SUMMARY, A_URL))
                .when(new ApplyForMembershipToOrganization(anOrganizationId, aMembershipId, A_NAME, "Let me in"))
                .expectEvents(new UserAppliedForMembershipToOrganization(aMembershipId, anOrganizationId, A_NAME, "Let me in"));
    }

    @Test
    public void createOrganization() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new CreateOrganization(anOrganizationId, AN_ALIAS, A_NAME, A_SUMMARY, A_URL))
                .expectEvents(new OrganizationCreated(anOrganizationId, AN_ALIAS, A_NAME, A_SUMMARY, A_URL));
    }

    @Test
    public void changeOrganizationNameAndSummary() throws Exception {
        fixture.given(new OrganizationCreated(anOrganizationId, AN_ALIAS, A_NAME, A_SUMMARY, A_URL))
                .when(new ChangeOrganizationNameAndSummary(anOrganizationId, A_NAME, A_SUMMARY))
                .expectEvents(new OrganizationNameAndSummaryChanged(anOrganizationId, A_NAME, A_SUMMARY));
    }

    @Test
    public void changeOrganizationWebsiteUrl() throws Exception {
        fixture.given(new OrganizationCreated(anOrganizationId, AN_ALIAS, A_NAME, A_SUMMARY, A_URL))
                .when(new ChangeOrganizationWebsiteUrl(anOrganizationId, A_URL))
                .expectEvents(new OrganizationWebsiteUrlChanged(anOrganizationId, A_URL));
    }

    @Test
    public void firstAddOrganizationAlias() {

        // Given no existing aliases on target
        when(mockedAliasService.isDefined(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS)).thenReturn(false);
        when(mockedAliasService.hasTarget(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ORG_ID)).thenReturn(false);

        fixture.given(
                organizationCreatedEvent()
        )
                .when(new AddOrganizationAlias(anOrganizationId, AN_ALIAS))
                .expectEvents(new OrganizationPrimaryAliasAdded(anOrganizationId, AN_ALIAS));
    }

    @Test
    public void secondAddOrganizationAlias() {

        // Given existing aliases on target
        when(mockedAliasService.isDefined(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS)).thenReturn(false);
        when(mockedAliasService.hasTarget(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ORG_ID)).thenReturn(true);

        fixture.given(
                organizationCreatedEvent()
        )
                .when(new AddOrganizationAlias(anOrganizationId, AN_ALIAS))
                .expectEvents(new OrganizationAliasAdded(anOrganizationId, AN_ALIAS));
    }

    @Test
    public void addOrganizationAliasIsIdempotent() {

        // Given alias already defined on same target 
        when(mockedAliasService.isDefined(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS)).thenReturn(true);
        when(mockedAliasService.isIdentical(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS, AN_ORG_ID)).thenReturn(true);

        fixture.given(
                organizationCreatedEvent()
        )
                .when(new AddOrganizationAlias(anOrganizationId, AN_ALIAS))
                .expectEvents();
    }

    @Test
    public void addOrganizationAliasAlreadyUsedShouldBeDenied() {

        // Given alias already defined on other target 
        when(mockedAliasService.isDefined(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS)).thenReturn(true);
        when(mockedAliasService.isIdentical(AliasGroups.USERS_AND_ORGANIZATIONS.name(), AN_ALIAS, AN_ORG_ID)).thenReturn(false);

        fixture.given(
                organizationCreatedEvent()
        )
                .when(new AddOrganizationAlias(anOrganizationId, AN_ALIAS))
                .expectEvents(/*new OrganizationAliasRegistrationDenied(anOrganizationId, AN_ALIAS)*/);
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
