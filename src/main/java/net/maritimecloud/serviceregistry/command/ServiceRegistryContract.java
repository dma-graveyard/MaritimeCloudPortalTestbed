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
package net.maritimecloud.serviceregistry.command;

import net.maritimecloud.cqrs.tool.CqrsContract;
import net.maritimecloud.cqrs.tool.Event;
import net.maritimecloud.cqrs.tool.Command;
import net.maritimecloud.cqrs.tool.TargetAggregateIdentifier;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.organization.membership.MembershipId;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;

/**
 * This interface describes the interface to the ServiceRegitry in terms of Commands and Events. Whereas the commands are explicitly being
 * the published interface, the description of the events are more like a convenience as they in most cases share the properties of the
 * commands.
 * <p>
 * Events may inherit their properties from corresponding commands, and may extend those properties, as would be needed to make room for
 * enrichment.
 * <p>
 * <p>
 * @see CommandEventSourceGenerator
 * @author Christoffer Børrild
 */
public interface ServiceRegistryContract extends CqrsContract {

    @Command
    void createOrganization(OrganizationId organizationId, String primaryAlias, String name, String summary, String url);

    @Command
    void changeOrganizationNameAndSummary(OrganizationId organizationId, String name, String summary);

    @Command
    void changeOrganizationWebsiteUrl(OrganizationId organizationId, String url);

    @Command
    void addOrganizationAlias(OrganizationId organizationId, String alias);

    @Command
    void removeOrganizationAlias(OrganizationId organizationId, String alias);

    @Command
    void authorizeMembershipToOrganizationCreator(OrganizationId organizationId, MembershipId membershipId, String username);

    @Command
    void inviteUserToOrganization(OrganizationId organizationId, MembershipId membershipId, String username);

    @Command
    void removeUserFromOrganization(MembershipId membershipId);

    @Command
    void prepareServiceSpecification(
            OrganizationId ownerId,
            @TargetAggregateIdentifier ServiceSpecificationId serviceSpecificationId,
            ServiceType serviceType,
            String name,
            String summary
    );

//    @Command
//    void provideServiceInstance(
//            OrganizationId providerId,
//            ServiceSpecificationId specificationId,
//            ServiceInstanceId serviceInstanceId,
//            String name,
//            String summary,
//            Coverage coverage);
    @Command
    void addServiceInstanceAlias(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String alias);

    @Command
    void removeServiceInstanceAlias(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String alias);

    @Command
    void changeServiceSpecificationNameAndSummary(ServiceSpecificationId serviceSpecificationId, String name, String summary);

    @Command
    void addServiceInstanceEndpoint(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);

    @Command
    void changeServiceInstanceNameAndSummary(ServiceInstanceId serviceInstanceId, String name, String summary);

    @Command
    void removeServiceInstanceEndpoint(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);

    // ------------------------------------------------------------------------
    // EVENTS
    // ------------------------------------------------------------------------

    @Event
    void organizationCreated(OrganizationId organizationId, String primaryAlias, String name, String summary, String url);

    @Event
    void serviceSpecificationNameAndSummaryChanged(ServiceSpecificationId serviceSpecificationId, String name, String summary);

    @Event
    void organizationWebsiteUrlChanged(OrganizationId organizationId, String url);

    @Event
    void organizationNameAndSummaryChanged(OrganizationId organizationId, String name, String summary);

    @Event
    void organizationAliasAdded(OrganizationId organizationId, String alias);

    @Event(extend = "OrganizationAliasAdded")
    void organizationPrimaryAliasAdded(OrganizationId organizationId, String alias);

    @Event
    void organizationAliasRegistrationDenied(OrganizationId organizationId, String alias);

    @Event
    void organizationAliasRemoved(OrganizationId organizationId, String alias);
    
    @Event
    void UserInvitedToOrganization(MembershipId membershipId, OrganizationId organizationId, String username);    
    
    @Event
    void UserLeftOrganization(MembershipId membershipId, OrganizationId organizationId, String username);    
    
    @Event
    void OrganizationRevokedUserMembership(MembershipId membershipId, OrganizationId organizationId, String username);    
    
    @Event
    void serviceSpecificationCreated(
            OrganizationId ownerId,
            @TargetAggregateIdentifier ServiceSpecificationId serviceSpecificationId,
            ServiceType serviceType,
            String name,
            String summary
    );

    @Event
    void serviceInstanceAliasAdded(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String alias);

    /**
     * this one is emitted only on the first creation of an alias on a target
     */
    @Event(extend = "ServiceInstanceAliasAdded")
    void serviceInstancePrimaryAliasAdded(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String alias);

    @Event
    void serviceInstanceAliasRegistrationDenied(OrganizationId organizationId, ServiceInstanceId serviceInstanceId, String alias);

    @Event
    void serviceInstanceAliasRemoved(OrganizationId organizationId, String alias);

    @Event
    void serviceInstanceCreated(
            OrganizationId providerId,
            ServiceSpecificationId specificationId,
            ServiceInstanceId serviceInstanceId,
            String name,
            String summary,
            Coverage coverage,
            // enrich with redundant data
            ServiceType serviceType);

    @Event
    void serviceInstanceEndpointAdded(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);

    @Event
    void serviceInstanceEndpointRemoved(ServiceInstanceId serviceInstanceId, ServiceEndpoint serviceEndpoint);

    @Event
    void serviceInstanceNameAndSummaryChanged(ServiceInstanceId serviceInstanceId, String name, String summary);

}
