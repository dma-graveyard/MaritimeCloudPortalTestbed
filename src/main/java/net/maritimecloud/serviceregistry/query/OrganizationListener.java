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

import net.maritimecloud.serviceregistry.command.organization.OrganizationCreatedEvent;
import net.maritimecloud.serviceregistry.command.organization.OrganizationNameAndSummaryChangedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoffer Børrild
 */
public class OrganizationListener {

    private final static Logger logger = LoggerFactory.getLogger(OrganizationQueryRepository.class);

    private OrganizationQueryRepository organizationQueryRepository;

    public OrganizationListener(OrganizationQueryRepository organizationQueryRepository) {
        this.organizationQueryRepository = organizationQueryRepository;
    }

    @EventHandler
    public void on(OrganizationCreatedEvent event) {
        logger.debug("About to handle the OrganizationCreatedEvent: {}", event);
        OrganizationEntry organizationEntry = new OrganizationEntry();
        organizationEntry.setOrganizationIdentifier(event.getOrganizationId().identifier());
        organizationEntry.setName(event.getName());
        organizationEntry.setSummary(event.getSummary());
        organizationQueryRepository.save(organizationEntry);
    }

    @EventHandler
    public void on(OrganizationNameAndSummaryChangedEvent event) {
        OrganizationEntry organizationEntry = organizationQueryRepository.findOne(event.getOrganizationId().identifier());
        organizationEntry.setName(event.getName());
        organizationEntry.setSummary(event.getSummary());
        organizationQueryRepository.save(organizationEntry);
    }
}
