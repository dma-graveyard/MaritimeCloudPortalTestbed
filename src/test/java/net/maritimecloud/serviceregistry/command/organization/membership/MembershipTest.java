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
import net.maritimecloud.serviceregistry.command.api.AcceptUsersMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.AcceptMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.OrganizationAcceptedMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.OrganizationMembershipAssignedToOwner;
import net.maritimecloud.serviceregistry.command.api.UserAcceptedMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserAppliedForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Christoffer Børrild
 */
public class MembershipTest extends CommonFixture {

    private FixtureConfiguration<Membership> fixture;
    private static final MembershipId aMembershipId = new MembershipId("A_MEMBERSHIP_ID");

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Membership.class);
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
