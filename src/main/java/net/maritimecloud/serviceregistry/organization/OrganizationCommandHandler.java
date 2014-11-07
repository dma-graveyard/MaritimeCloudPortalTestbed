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
package net.maritimecloud.serviceregistry.organization;

import net.maritimecloud.serviceregistry.servicespecification.ServiceSpecification;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;

/**
 * Responsibilities (in the ServiceRegistry context):
 * 
 * <p>
 * Creates ServiceSpecifications and provides ServiceInstances
 * (Provides factories for ServiceSpecifications and ServiceInstances, this protects the invariant that )
 * <p>
 * Makes sure that ServiceSpecifications and ServiceInstances has a unique identity within the owning organization
 * <p>
 * Maintains the lists of ServiceSpecifications and ServiceInstances held by an organization
 * <p>
 * 
 * @author Christoffer Børrild
 */
public class OrganizationCommandHandler {

    private Repository<Organization> repository;
    private Repository<ServiceSpecification> serviceSpecificationRepository;

    public void setRepository(Repository<Organization> organizationRepository) {
        this.repository = organizationRepository;
    }

    public void setServiceSpecificationRepository(Repository<ServiceSpecification> serviceSpecificationRepository) {
        this.serviceSpecificationRepository = serviceSpecificationRepository;
    }

    @CommandHandler
    public void handle(PrepareServiceSpecificationCommand command) {
        
        Organization organization = repository.load(command.getOrganizationId());
        
        if(organization.isDeleted())
            throw new IllegalArgumentException("Organization exists no more. " + command.getOrganizationId());
        
        ServiceSpecification serviceSpecification = 
                organization.prepareServiceSpecification(command.getServiceSpecificationId(), command.getName(), command.getSummary());

        serviceSpecificationRepository.add(serviceSpecification);
        
    }

}
