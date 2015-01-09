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
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class OrganizationMemberListener {

    private final static Logger logger = LoggerFactory.getLogger(OrganizationMemberListener.class);

    @Resource
    private OrganizationMemberQueryRepository organizationMemberQueryRepository;

    public OrganizationMemberListener() {
    }

    public OrganizationMemberListener(OrganizationMemberQueryRepository organizationMemberQueryRepository) {
        this.organizationMemberQueryRepository = organizationMemberQueryRepository;
    }

    public void setOrganizationQueryRepository(OrganizationMemberQueryRepository organizationMemberQueryRepository) {
        this.organizationMemberQueryRepository = organizationMemberQueryRepository;
    }
    
    @EventHandler
    public void on(UserInvitedToOrganization event) {
        logger.debug("About to handle the UserInvitedToOrganization: {}", event);
        OrganizationMemberEntry organizationMemberEntry = new OrganizationMemberEntry();
        organizationMemberEntry.setOrganizationId(event.getOrganizationId().identifier());
        organizationMemberEntry.setUsername(event.getUsername());
        organizationMemberQueryRepository.save(organizationMemberEntry);
    }
    
}
