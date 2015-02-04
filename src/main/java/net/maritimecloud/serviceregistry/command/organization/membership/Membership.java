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
package net.maritimecloud.serviceregistry.command.organization.membership;

import net.maritimecloud.serviceregistry.command.api.AcceptUsersMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.AcceptMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.OrganizationAcceptedMembershipApplication;
import net.maritimecloud.serviceregistry.command.api.OrganizationMembershipAssignedToOwner;
import net.maritimecloud.serviceregistry.command.api.OrganizationRevokedUserMembership;
import net.maritimecloud.serviceregistry.command.api.RemoveUserFromOrganization;
import net.maritimecloud.serviceregistry.command.api.UserAcceptedMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserAppliedForMembershipToOrganization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.springframework.stereotype.Component;

/**
 * A Membership represents the association between an Organization and a User.
 * <p>
 * The Membership is intended to hold the current membership status ranging from "Invited/Requested" to "Accepted" as well as the users role
 * like being an "Owner" with admin privileges or e.g. a contributer with limited capacities.
 * <p>
 * In the future it is envisioned that this class should react to commands like:
 * <ul>
 * <li>ChangeMembershipRole</li>
 * </ul>
 * <p>
 * <p>
 * @author Christoffer Børrild
 */
@Component
public class Membership extends AbstractAnnotatedAggregateRoot<MembershipId> {

    @AggregateIdentifier
    private MembershipId membershipId;
    private OrganizationId organizationId;
    private String username;
    boolean acceptedByUser;
    boolean acceptedByOrganization;

    protected Membership() {
        acceptedByUser = false;
        acceptedByOrganization = false;
    }

    public static enum ApplicationType {

        APPLICATION, INVITE, CREATOR
    };

    public Membership(MembershipId membershipId, OrganizationId organizationId, String username, String applicationMessage, ApplicationType applicationType) {
        this.membershipId = membershipId;
        this.organizationId = organizationId;
        this.username = username;

        switch (applicationType) {
            case APPLICATION:
                apply(new UserAppliedForMembershipToOrganization(membershipId, organizationId, username, applicationMessage));
                break;
            case INVITE:
                apply(new UserInvitedToOrganization(membershipId, organizationId, username));
                break;
            case CREATOR:
                activate();
                break;
            default:
                throw new IllegalArgumentException("Membership must be startet by either an invite or an application");

        }
    }

    @CommandHandler
    public void handle(AcceptUsersMembershipApplication command) {
        if (!acceptedByOrganization) {
            apply(new OrganizationAcceptedMembershipApplication(membershipId, organizationId, username));

            if (acceptedByUser) {
                activate();
            }
        }
    }

    @CommandHandler
    public void handle(AcceptMembershipToOrganization command) {
        if (!acceptedByUser) {
            apply(new UserAcceptedMembershipToOrganization(membershipId, organizationId, username));

            if (acceptedByOrganization) {
                activate();
            }
        }
    }

    private void activate() {
        apply(new OrganizationMembershipAssignedToOwner(membershipId, organizationId, username));
    }

    @CommandHandler
    public void handle(RemoveUserFromOrganization command) {
        if (!isDeleted()) {
            markDeleted();
            apply(new OrganizationRevokedUserMembership(membershipId, organizationId, username));
        }
    }

    @EventSourcingHandler
    public void on(UserInvitedToOrganization event) {
        this.acceptedByOrganization = true;
        this.membershipId = new MembershipId(event.getMembershipId());
        this.organizationId = event.getOrganizationId();
        this.username = event.getUsername();
    }

    @EventSourcingHandler
    public void on(UserAppliedForMembershipToOrganization event) {
        this.acceptedByUser = true;
        this.membershipId = new MembershipId(event.getMembershipId());
        this.organizationId = event.getOrganizationId();
        this.username = event.getUsername();
    }

    @EventSourcingHandler
    public void on(OrganizationMembershipAssignedToOwner event) {
        this.acceptedByOrganization = true;
        this.acceptedByUser = true;
        this.membershipId = new MembershipId(event.getMembershipId());
        this.organizationId = event.getOrganizationId();
        this.username = event.getUsername();
    }

}
