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
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.api.OrganizationNameAndSummaryChanged;
import net.maritimecloud.serviceregistry.command.api.OrganizationPrimaryAliasAdded;
import net.maritimecloud.serviceregistry.command.api.OrganizationWebsiteUrlChanged;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class OrganizationListener {

    private final static Logger logger = LoggerFactory.getLogger(OrganizationQueryRepository.class);

    @Resource
    private OrganizationQueryRepository organizationQueryRepository;

    public OrganizationListener() {
    }

    public OrganizationListener(OrganizationQueryRepository organizationQueryRepository) {
        this.organizationQueryRepository = organizationQueryRepository;
    }

    public void setOrganizationQueryRepository(OrganizationQueryRepository organizationQueryRepository) {
        this.organizationQueryRepository = organizationQueryRepository;
    }
    
    @EventHandler
    public void on(OrganizationCreated event) {
        logger.debug("About to handle the OrganizationCreatedEvent: {}", event);
        OrganizationEntry organizationEntry = new OrganizationEntry();
        organizationEntry.setOrganizationId(event.getOrganizationId().identifier());
        organizationEntry.setName(event.getName());
        organizationEntry.setSummary(event.getSummary());
        organizationEntry.setUrl(event.getUrl());
        organizationQueryRepository.save(organizationEntry);
    }

    @EventHandler
    public void on(OrganizationNameAndSummaryChanged event) {
        OrganizationEntry organizationEntry = organizationQueryRepository.findOne(event.getOrganizationId().identifier());
        organizationEntry.setName(event.getName());
        organizationEntry.setSummary(event.getSummary());
        organizationQueryRepository.save(organizationEntry);
    }

    @EventHandler
    public void on(OrganizationWebsiteUrlChanged event) {
        OrganizationEntry organizationEntry = organizationQueryRepository.findOne(event.getOrganizationId().identifier());
        organizationEntry.setUrl(event.getUrl());
        organizationQueryRepository.save(organizationEntry);
    }
    
    @EventHandler
    public void on(OrganizationPrimaryAliasAdded event) {
        OrganizationEntry organizationEntry = organizationQueryRepository.findOne(event.getOrganizationId().identifier());
        organizationEntry.setPrimaryAlias(event.getAlias());
        organizationQueryRepository.save(organizationEntry);
    }

    
}
