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
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationNameAndSummaryChanged;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class ServiceSpecificationListener {

    private final static Logger logger = LoggerFactory.getLogger(ServiceSpecificationQueryRepository.class);

    @Resource
    private ServiceSpecificationQueryRepository serviceSpecificationQueryRepository;

    public ServiceSpecificationListener() {
    }

    public ServiceSpecificationListener(ServiceSpecificationQueryRepository servicespecificationQueryRepository) {
        this.serviceSpecificationQueryRepository = servicespecificationQueryRepository;
    }

    @EventHandler
    public void on(ServiceSpecificationCreated event) {
        logger.debug("About to handle the ServiceSpecificationCreatedEvent: {}", event);
        ServiceSpecificationEntry entry = new ServiceSpecificationEntry();
        entry.setServiceSpecificationId(event.getServiceSpecificationId().identifier());
        entry.setOwnerId(event.getOwnerId().identifier());
        entry.setServiceType(event.getServiceType());
        entry.setName(event.getName());
        entry.setSummary(event.getSummary());
        serviceSpecificationQueryRepository.save(entry);
    }

    @EventHandler
    public void on(ServiceSpecificationNameAndSummaryChanged event) {
        ServiceSpecificationEntry servicespecificationEntry = serviceSpecificationQueryRepository.findOne(event.getServiceSpecificationId().identifier());
        servicespecificationEntry.setName(event.getName());
        servicespecificationEntry.setSummary(event.getSummary());
        serviceSpecificationQueryRepository.save(servicespecificationEntry);
    }
}
