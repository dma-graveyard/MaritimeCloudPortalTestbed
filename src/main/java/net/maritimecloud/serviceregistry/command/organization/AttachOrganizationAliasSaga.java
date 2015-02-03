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
import net.maritimecloud.serviceregistry.command.api.AddOrganizationAlias;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.OrganizationPrimaryAliasAdded;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;

/**
 *
 * @author Christoffer Børrild
 */
@NoReplayedEvents
public class AttachOrganizationAliasSaga extends AbstractAnnotatedSaga {

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
        System.out.println("AttachOrganizationAliasSaga: Emitted AddOrganizationAlias with alias " + alias + " for the organization " + event.getOrganizationId().identifier());
        commandGateway.send(new AddOrganizationAlias(event.getOrganizationId(), event.getPrimaryAlias()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(OrganizationPrimaryAliasAdded event) {
        System.out.println("Saga added the primary alias " + alias + " to the organization " + event.getOrganizationId().identifier());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(OrganizationAliasAdded event) {
        System.out.println("Saga added the alias " + alias + " to the organization " + event.getOrganizationId().identifier()
                + ", but not as the primary alias (... and I wonder why?)");
    }

    @SagaEventHandler(associationProperty = "organizationId")
    public void handle(OrganizationAliasRegistrationDenied event) {
        if (alias.equals(event.getAlias())) {
            System.out.println("Whoops - something went wrong when adding the alias " + alias
                    + " to the organization " + event.getOrganizationId().identifier());
            System.out.println("The alias was probably not unique. ");
            end();
        }
    }

}
