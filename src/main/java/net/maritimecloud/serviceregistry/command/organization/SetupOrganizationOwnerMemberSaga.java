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

import javax.annotation.Resource;
import net.maritimecloud.portal.domain.infrastructure.axon.NoReplayedEvents;
import net.maritimecloud.serviceregistry.command.api.AuthorizeMembershipToOrganizationCreator;
import net.maritimecloud.serviceregistry.command.api.InviteUserToOrganization;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import net.maritimecloud.serviceregistry.command.organization.membership.MembershipId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;

/**
 * This saga adds the creating user as owner of the newly created organization. This is a special purpose process, as usually all potential
 * members need to go through an invite- or join-request process in order to achieve membership. The creator of the organization should of
 * course be granted membership right away.
 * <p>
 * @author Christoffer Børrild
 */
@NoReplayedEvents
public class SetupOrganizationOwnerMemberSaga extends AbstractAnnotatedSaga {

    private String alias;

    @Resource
    private transient CommandGateway commandGateway;

    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(OrganizationCreated event) {
        alias = event.getPrimaryAlias();

        // FIXME: figure out how to get hand on the username of the creating user !!!
        System.out.println("SetupOrganizationSaga: FIXME: Hardcoding user 'admin' as admin-member for the organization " + event.getOrganizationId().identifier());
        commandGateway.send(new AuthorizeMembershipToOrganizationCreator(event.getOrganizationId(), new MembershipId(event.getOrganizationId().identifier()), "admin"));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(UserInvitedToOrganization event) {
        System.out.println("Saga added the primary alias " + alias + " to the organization " + event.getOrganizationId().identifier());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(OrganizationAliasAdded event) {
        System.out.println("SetupOrganizationSaga added a member invitation for organization " + event.getOrganizationId().identifier());
    }

}
