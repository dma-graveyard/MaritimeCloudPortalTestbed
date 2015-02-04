/* Copyright 2015 Danish Maritime Authority.
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
package net.maritimecloud.serviceregistry.command.organization.membership;

import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.A_NAME;
import static net.maritimecloud.common.infrastructure.axon.CommonFixture.anOrganizationId;
import net.maritimecloud.common.infrastructure.axon.RepositoryMock;
import net.maritimecloud.serviceregistry.command.api.AcceptUsersMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.AcceptMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.ApplyForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.InviteUserToOrganization;
import net.maritimecloud.serviceregistry.command.api.OrganizationAcceptedMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.OrganizationMembershipAssignedToOwner;
import net.maritimecloud.serviceregistry.command.api.UserAcceptedMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserAppliedForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import net.maritimecloud.serviceregistry.command.organization.Organization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationCommandHandler;
import net.maritimecloud.serviceregistry.query.OrganizationMembershipQueryRepository;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 *
 * @author Christoffer Børrild
 */
public class MembershipTest extends CommonFixture {

    private FixtureConfiguration<Membership> fixture;
    private static final MembershipId aMembershipId = new MembershipId("A_MEMBERSHIP_ID");
    private final OrganizationCommandHandler organizationCommandHandler = new OrganizationCommandHandler();
 
    @Mock
    private OrganizationMembershipQueryRepository membershipQueryRepository;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Membership.class);
        Organization anOrganization = new Organization(generateCreateOrganizationCommand(AN_ORG_ID)) {
            @Override
            public boolean isDeleted() {
                return false;
            }
        };
        organizationCommandHandler.setOrganizationRepository(new RepositoryMock(anOrganization));
        organizationCommandHandler.setMembershipRepository(fixture.getRepository());
        membershipQueryRepository = Mockito.mock(OrganizationMembershipQueryRepository.class);
        organizationCommandHandler.setMembershipQueryRepository(membershipQueryRepository);
                fixture.registerAnnotatedCommandHandler(organizationCommandHandler);
    }
    
    @Test
    public void requestMembershipToOrganization() throws Exception {
        // Given an organization and a user 
        // and no existing membership
        Mockito.when(membershipQueryRepository.findByOrganizationIdAndUsername(AN_ORG_ID, A_NAME)).thenReturn(null);
        
        fixture.givenNoPriorActivity()
                .when(new ApplyForMembershipToOrganization(anOrganizationId, aMembershipId, A_NAME, "Let me in"))
                .expectEvents(new UserAppliedForMembershipToOrganization(aMembershipId, anOrganizationId, A_NAME, "Let me in"));
    }
    
    @Test
    public void inviteUserToOrganization() throws Exception {
        // Given an organization and a user 
        // and no existing membership
        Mockito.when(membershipQueryRepository.findByOrganizationIdAndUsername(AN_ORG_ID, A_NAME)).thenReturn(null);
        
        fixture.givenNoPriorActivity()
                .when(new InviteUserToOrganization(anOrganizationId, aMembershipId, A_NAME))
                .expectEvents(new UserInvitedToOrganization(aMembershipId, anOrganizationId, A_NAME));
    }
    
    @Test
    public void acceptUsersMembershipApplication() throws Exception {
        fixture.given(
                new UserAppliedForMembershipToOrganization(aMembershipId, anOrganizationId, A_NAME, "Let me in")
        ).when(new AcceptUsersMembershipApplication(aMembershipId)
        ).expectEvents(
                new OrganizationAcceptedMembershipApplication(aMembershipId, anOrganizationId, A_NAME),
                new OrganizationMembershipAssignedToOwner(aMembershipId, anOrganizationId, A_NAME)
        );
    }

    @Test
    public void acceptMembershipToOrganization() throws Exception {
        fixture.given(
                new UserInvitedToOrganization(aMembershipId, anOrganizationId, A_NAME)
        ).when(
                new AcceptMembershipToOrganization(aMembershipId)
        ).expectEvents(
                new UserAcceptedMembershipToOrganization(aMembershipId, anOrganizationId, A_NAME),
                new OrganizationMembershipAssignedToOwner(aMembershipId, anOrganizationId, A_NAME)
        );
    }
    
}
