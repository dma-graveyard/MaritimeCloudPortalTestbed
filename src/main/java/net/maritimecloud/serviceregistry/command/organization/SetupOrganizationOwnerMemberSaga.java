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
import net.maritimecloud.common.eventsourcing.axon.NoReplayedEvents;
import net.maritimecloud.portal.audit.axon.UserMetaData;
import net.maritimecloud.serviceregistry.command.api.AuthorizeMembershipToOrganizationCreator;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.organization.membership.MembershipId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.annotation.MetaData;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;

/**
 * This saga adds the creating user as owner of the newly created organization. This is a special purpose process, as usually all potential
 * members need to go through an invite- or apply-process in order to achieve membership. The creator of the organization should of
 * course be granted membership as owner right away.
 * <p>
 * @author Christoffer Børrild
 */
@NoReplayedEvents
public class SetupOrganizationOwnerMemberSaga extends AbstractAnnotatedSaga {

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
    public void handle(OrganizationCreated event, @MetaData(UserMetaData.USERNAME) String username) {
        commandGateway.send(new AuthorizeMembershipToOrganizationCreator(event.getOrganizationId(), new MembershipId(event.getOrganizationId().identifier()), username));
        end();
    }

}
