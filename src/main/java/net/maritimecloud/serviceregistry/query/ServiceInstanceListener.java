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
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceCoverageChangedEvent;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceCreatedEvent;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceEndpointAddedEvent;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceEndpointRemovedEvent;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceNameAndSummaryChangedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoffer Børrild
 */
@Component
public class ServiceInstanceListener {

    private final static Logger logger = LoggerFactory.getLogger(ServiceInstanceQueryRepository.class);

    @Resource
    private ServiceInstanceQueryRepository serviceInstanceQueryRepository;

    public ServiceInstanceListener() {
    }

    public ServiceInstanceListener(ServiceInstanceQueryRepository serviceinstanceQueryRepository) {
        this.serviceInstanceQueryRepository = serviceinstanceQueryRepository;
    }

    @EventHandler
    public void on(ServiceInstanceCreatedEvent event) {
        ServiceInstanceEntry entry = new ServiceInstanceEntry();
        entry.setServiceInstanceId(event.getServiceInstanceId().identifier());
        entry.setProviderId(event.getProviderId().identifier());
        entry.setSpecificationId(event.getSpecificationId().identifier());
        entry.setName(event.getName());
        entry.setSummary(event.getSummary());
        entry.setCoverage(event.getCoverage());
        entry.setSpecificationServiceType(event.getServiceType());
        save(entry);
    }

    @EventHandler
    public void on(ServiceInstanceNameAndSummaryChangedEvent event) {
        ServiceInstanceEntry instance = getInstanceWith(event.getServiceInstanceId());
        instance.setName(event.getName());
        instance.setSummary(event.getSummary());
        save(instance);
    }

    @EventHandler
    public void on(ServiceInstanceCoverageChangedEvent event) {
        ServiceInstanceEntry instance = getInstanceWith(event.getServiceInstanceId());
        instance.setCoverage(event.getCoverage());
        save(instance);
    }

    @EventHandler
    public void on(ServiceInstanceEndpointAddedEvent event) {
        ServiceInstanceEntry instance = getInstanceWith(event.getServiceInstanceId());
        instance.addEndpoint(event.getServiceEndpoint());
        save(instance);
    }

    @EventHandler
    public void on(ServiceInstanceEndpointRemovedEvent event) {
        ServiceInstanceEntry instance = getInstanceWith(event.getServiceInstanceId());
        instance.removeEndpoint(event.getServiceEndpoint());
        save(instance);
    }

    private void save(ServiceInstanceEntry entry) {
        serviceInstanceQueryRepository.save(entry);
    }

    private ServiceInstanceEntry getInstanceWith(ServiceInstanceId id) {
        return serviceInstanceQueryRepository.findOne(id.identifier());
    }
}
