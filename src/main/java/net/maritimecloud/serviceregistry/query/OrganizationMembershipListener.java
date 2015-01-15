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
import javax.persistence.EntityManager;
import net.maritimecloud.serviceregistry.command.api.OrganizationRevokedUserMembership;
import net.maritimecloud.serviceregistry.command.api.UserInvitedToOrganization;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class OrganizationMembershipListener {

    private final static Logger logger = LoggerFactory.getLogger(OrganizationMembershipListener.class);

    @Resource
    private OrganizationMembershipQueryRepository organizationMemberQueryRepository;

    @Resource
    private EntityManager entityManager;

    public OrganizationMembershipListener() {
    }

    public OrganizationMembershipListener(OrganizationMembershipQueryRepository organizationMemberQueryRepository) {
        this.organizationMemberQueryRepository = organizationMemberQueryRepository;
    }

    public void setOrganizationQueryRepository(OrganizationMembershipQueryRepository organizationMemberQueryRepository) {
        this.organizationMemberQueryRepository = organizationMemberQueryRepository;
    }

    @EventHandler
    public void on(UserInvitedToOrganization event) {
        logger.debug("About to handle the UserInvitedToOrganization: {}", event);
        OrganizationMembershipEntry organizationMemberEntry = new OrganizationMembershipEntry();
        organizationMemberEntry.setMembershipId(event.getMembershipId().identifier());
        organizationMemberEntry.setOrganizationId(event.getOrganizationId().identifier());
        organizationMemberEntry.setUsername(event.getUsername());
        organizationMemberQueryRepository.save(organizationMemberEntry);
    }

    @EventHandler
    public void on(OrganizationRevokedUserMembership event) {
        logger.debug("About to handle the OrganizationRevokedUserMembership: {}", event);
        OrganizationMembershipEntry entry = organizationMemberQueryRepository.findOne(event.getMembershipId().identifier());
        if (entry != null) {
            organizationMemberQueryRepository.delete(entry);

            // GOTCHA: one of the painful experiences that may take you a while to realize (as well as debug) is:
            // Since this view has a unique key constraint, we need to flush now in order to avoid
            // constraints violations later on in event-replaying scenarios where a similar entry
            // is re-inserted before commit!!! If we do not flush the delete, the constraint may 
            // still see the existing row and complaint.  
            entityManager.flush();
        }
    }

}
