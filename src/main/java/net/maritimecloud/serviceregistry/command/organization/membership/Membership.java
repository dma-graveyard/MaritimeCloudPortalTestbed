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

import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Christoffer Børrild
 */
@Component
public class Membership extends AbstractAnnotatedAggregateRoot<MembershipId> {

    @AggregateIdentifier
    private MembershipId membershipId;
    private OrganizationId organizationId;
    private String username;

    protected Membership() {
    }
    
    public Membership(OrganizationId organizationId, String username) {
        this.membershipId = new MembershipId(organizationId, username); 
        this.organizationId = organizationId;
        this.username = username;
        apply(new UserInvitedToOrganization(organizationId, username));
    }
//
//    @CommandHandler
//    public void handle(InviteUserToOrganization command) {
//        apply(new UserInvitedToOrganization(command.getOrganizationId(), command.getUsername()));
//    }

    @EventSourcingHandler
    public void on(UserInvitedToOrganization event) {
    }

}