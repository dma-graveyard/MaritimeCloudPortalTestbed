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
import javax.annotation.Resource;
import net.maritimecloud.portal.domain.infrastructure.axon.UserMetaData;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.domain.DomainIdentifier;
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
    public void on(ServiceInstanceAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Alias Added", "added alias '" + event.getAlias() + "' to service", event.getServiceInstanceId());
    }

    @EventHandler
    public void on(ServiceInstanceAliasRemoved event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Service Alias Removed", "removed alias '" + event.getAlias() + "'", null);
    }

    @EventHandler
    public void on(OrganizationAliasAdded event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization Alias Added", "added alias '" + event.getAlias() + "' to organization", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationAliasRemoved event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        register(username, dateTime, event, "Organization Alias Removed", "removed alias '" + event.getAlias() + "' from ", event.getOrganizationId());
    }

    @EventHandler
    public void on(OrganizationCreated event, @MetaData(UserMetaData.USERNAME) String username, @Timestamp DateTime dateTime) {
        registerPublic(username, dateTime, event, "New Organization Created", "created organization", event.getOrganizationId());
    }

    private void register(String username, DateTime dateTime, Object event, String title, String summary, DomainIdentifier target) {
        ActivityEntry entry = build(username, dateTime, event);
        describe(entry, title, summary, target);
        save(entry);
    }

    private void registerPublic(String username, DateTime dateTime, Object event, String title, String summary, DomainIdentifier target) {
        ActivityEntry entry = build(username, dateTime, event);
        describe(entry, title, summary, target);
        entry.setIsPublic(true);
        save(entry);
    }

    private ActivityEntry build(String username, DateTime dateTime, Object event) {
        return new ActivityEntry(
                username, getIdentifier(OrganizationId.class, event), false, dateTime.toDate(), event.getClass().getName(), event.getClass().getSimpleName(), "", "", null, null
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

    private String getIdentifier(Class identifierClass, Object event) {
        return getIdentifier("get" + identifierClass.getSimpleName(), event);
    }

    private String getIdentifier(String getOrganizationIdMethodName, Object event) {
        try {
            return ((DomainIdentifier) event.getClass().getMethod(getOrganizationIdMethodName).invoke(event)).identifier();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException();
        }
    }

    private void save(ActivityEntry entry) {
        //System.out.println("Activity: " + entry);
        activityEntryQueryRepository.save(entry);
    }

}
