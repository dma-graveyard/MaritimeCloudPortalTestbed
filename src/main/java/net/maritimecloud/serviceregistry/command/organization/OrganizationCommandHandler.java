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
package net.maritimecloud.serviceregistry.command.organization;

import javax.annotation.Resource;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

/**
 * Responsibilities (in the ServiceRegistry context):
 * <p>
 * <p>
 * Creates ServiceSpecifications and provides ServiceInstances (Provides factories for ServiceSpecifications and ServiceInstances, this
 * protects the invariant that )
 * <p>
 * Makes sure that ServiceSpecifications and ServiceInstances has a unique identity within the owning organization
 * <p>
 * Maintains the lists of ServiceSpecifications and ServiceInstances held by an organization
 * <p>
 * <p>
 * @author Christoffer Børrild
 */
@Component
public class OrganizationCommandHandler {

    @Resource
    private Repository<Organization> repository;
    @Resource
    private Repository<ServiceSpecification> serviceSpecificationRepository;
    @Resource
    private Repository<ServiceInstance> serviceInstanceRepository;

    public void setRepository(Repository<Organization> organizationRepository) {
        this.repository = organizationRepository;
    }

    public void setServiceSpecificationRepository(Repository<ServiceSpecification> serviceSpecificationRepository) {
        this.serviceSpecificationRepository = serviceSpecificationRepository;
    }

    @CommandHandler
    public void handle(PrepareServiceSpecificationCommand command) {

        Organization organization = repository.load(command.getOrganizationId());

        if (organization.isDeleted()) {
            throw new IllegalArgumentException("Organization exists no more. " + command.getOrganizationId());
        }

        ServiceSpecification serviceSpecification
                = organization.prepareServiceSpecification(command.getServiceSpecificationId(), command.getName(), command.getSummary());

        serviceSpecificationRepository.add(serviceSpecification);

    }

    @CommandHandler
    public void handle(ProvideServiceInstanceCommand command) {

        Organization organization = repository.load(command.getProviderId());

        if (organization.isDeleted()) {
            throw new IllegalArgumentException("Organization exists no more. " + command.getProviderId());
        }

        ServiceSpecification serviceSpecification
                = serviceSpecificationRepository.load(command.getSpecificationId());

        if (serviceSpecification.isDeleted()) {
            throw new IllegalArgumentException("Service specification exists no more. " + command.getProviderId());
        }

        ServiceInstance serviceInstance
                = organization.provideServiceInstance(
                        command.getSpecificationId(),
                        command.getServiceInstanceId(),
                        command.getName(),
                        command.getSummary(),
                        command.getCoverage());

        serviceInstanceRepository.add(serviceInstance);

    }

}
