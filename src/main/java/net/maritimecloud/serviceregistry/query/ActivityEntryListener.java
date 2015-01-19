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

import javax.annotation.Resource;
import net.maritimecloud.portal.domain.infrastructure.axon.UserMetaData;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationAliasRemoved;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasAdded;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceAliasRemoved;
import org.axonframework.common.annotation.MetaData;
import org.axonframework.eventhandling.annotation.EventHandler;
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
    public void on(ServiceInstanceAliasAdded event, @MetaData(UserMetaData.USERNAME) String username) {
        save(new ActivityEntry(event.getOrganizationId().identifier(), username, event.getClass().getName(), "Service Alias Added", "User "+ username + " added alias '"+event.getAlias()+"' to service "+ event.getServiceInstanceId()));
    }

    @EventHandler
    public void on(ServiceInstanceAliasRemoved event) {
    }

    @EventHandler
    public void on(OrganizationAliasAdded event) {
    }

    @EventHandler
    public void on(OrganizationAliasRemoved event) {
    }

    private void save(ActivityEntry entry) {
        System.out.println("Activity: "+entry);
        activityEntryQueryRepository.save(entry);
    }

}
