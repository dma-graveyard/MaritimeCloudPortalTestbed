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
package net.maritimecloud.serviceregistry.query;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import net.maritimecloud.portal.domain.infrastructure.axon.UserMetaData;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.OrganizationNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.OrganizationPrimaryAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationRevokedUserMembership;
import net.maritimecloud.serviceregistry.command.api.OrganizationWebsiteUrlChanged;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRegistrationDenied;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceEndpointRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.ServiceInstancePrimaryAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import net.maritimecloud.serviceregistry.command.api.UserLeftOrganization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.common.ddd.DomainIdentifier;
import org.axonframework.common.annotation.MetaData;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.annotation.Timestamp;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 *
 * @author Christoffer Børrild
 */
@Component
public class ActivityEntryListener {

    @Resource
    private ActivityEntryQueryRepository activityEntryQueryRepository;

    public ActivityEntryListener() {
    }

    public void setActivityEntryQueryRepository(ActivityEntryQueryRepository activityEntryQueryRepository) {
        this.activityEntryQueryRepository = activityEntryQueryRepository;
    }

    @EventHandler
    public void on(OrganizationCreated event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        registerPublic(username, dateTime, event, "New Organization Created", "created organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(ServiceSpecificationNameAndSummaryChanged event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Specification name and summary changed", "changed name and summary of service specification", event.getServiceSpecificationId());
    }

    @EventHandler
    public void on(OrganizationWebsiteUrlChanged event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization website URL changed", "changed website URL of organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationNameAndSummaryChanged event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization name and summary changed", "changed name and summary of organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization Alias Added", "added alias '" + event.getAlias() + "' to organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationPrimaryAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization Primary Alias Added", "added alias '" + event.getAlias() + "' to organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationAliasRegistrationDenied event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization alias registration denied", "denied to create organization alias '" + event.getAlias() + "' as it is already in use", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationAliasRemoved event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization Alias Removed", "removed alias '" + event.getAlias() + "' from ", event.getOrganizationId());
    }

    @EventHandler
    public void on(UserInvitedToOrganization event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "User invited to Organization", "invited '" + event.getUsername() + "' to organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(UserLeftOrganization event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "User left Organization", "left organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationRevokedUserMembership event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Membership revoked", "revoked '" + event.getUsername() + "s' membership to organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(ServiceSpecificationCreated event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "New Service Specification created", "created service specification", event.getServiceSpecificationId());
    }

    @EventHandler
    public void on(ServiceInstanceAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Alias Added", "added alias '" + event.getAlias() + "' to service", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstancePrimaryAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Primary Alias Added", "added primary alias '" + event.getAlias() + "' to service", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceAliasRegistrationDenied event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service alias registration denied", "denied to create service alias '" + event.getAlias() + "' as it is already in use", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceAliasRemoved event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Alias Removed", "removed alias '" + event.getAlias() + "'", null);
    }

    @EventHandler
    public void on(ServiceInstanceCreated event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        registerPublic(username, dateTime, event, "Service Created", "created service instance", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceEndpointAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service endpoint Added", "added service endpoint '" + event.getServiceEndpoint().getUri() + "' to service", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceEndpointRemoved event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service endpoint removed", "removed service endpoint '" + event.getServiceEndpoint().getUri() + "' from service", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceNameAndSummaryChanged event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service name and summary changed", "changed name and summary of service", event.getServiceInstanceId());
    }

    private void register(String username, DateTime dateTime, Object event, String title, String summary, DomainIdentifier target) {
        ActivityEntry entry = create(username, dateTime, event);
        describe(entry, title, summary, target);
        save(entry);
    }

    private void registerPublic(String username, DateTime dateTime, Object event, String title, String summary, DomainIdentifier target) {
        ActivityEntry entry = create(username, dateTime, event);
        describe(entry, title, summary, target);
        entry.setIsPublic(true);
        save(entry);
    }

    private ActivityEntry create(String username, DateTime dateTime, Object event) {
        return new ActivityEntry(
                username, getOrganizationIdentifier(event), false, dateTime.toDate(), event.getClass().getName(), event.getClass().getSimpleName(), "", "", null, null
        );
    }

    private void describe(ActivityEntry entry, String title, String summary, DomainIdentifier target) {
        entry.setTitle(title);
        entry.setSummary(summary);
        if (target != null) {
            entry.setTargetType(target.getClass().getSimpleName());
            entry.setTargetId(target.identifier());
        }
    }

    private String getOrganizationIdentifier(Object event) {
        if(event instanceof ServiceSpecificationCreated){
            return ((ServiceSpecificationCreated) event).getOwnerId().identifier();
        }
        if(event instanceof ServiceInstanceCreated){
            return ((ServiceInstanceCreated) event).getProviderId().identifier();
        }
        try {
            return getIdentifier("getOrganizationId", event);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private String getIdentifier(String getOrganizationIdMethodName, Object event) throws NoSuchMethodException {
        try {
            return ((DomainIdentifier) event.getClass().getMethod(getOrganizationIdMethodName).invoke(event)).identifier();
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void save(ActivityEntry entry) {
        //System.out.println("Activity: " + entry);
        activityEntryQueryRepository.save(entry);
    }

}
