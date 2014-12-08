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
package net.maritimecloud.serviceregistry;

import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.servicespecification.*;
import net.maritimecloud.serviceregistry.command.organization.*;
import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.common.infrastructure.axon.AbstractManuallyComnfiguredAxonCqrsIT;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationListener;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationQueryRepository;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Integration test not using spring for wiring Axon components. Kept for reference in case we need to opt out of spring.
 * <p>
 * (run with 'mvn failsafe:integration-test')
 * <p>
 * @author Christoffer Børrild
 */
public class ManuallyConfiguredAxonIT extends AbstractManuallyComnfiguredAxonCqrsIT {

    @Resource
    protected ServiceSpecificationQueryRepository serviceSpecificationQueryRepository;

    private final String itemId = UUID.randomUUID().toString();
    private final OrganizationId organizationId = new OrganizationId(itemId);
    private final ServiceSpecificationId serviceSpecificationId1 = new ServiceSpecificationId(UUID.randomUUID().toString());
    private final ServiceSpecificationId serviceSpecificationId2 = new ServiceSpecificationId(UUID.randomUUID().toString());
    private final ServiceSpecificationId serviceSpecificationId3 = new ServiceSpecificationId(UUID.randomUUID().toString());
    private final CreateOrganization CREATE_ORGANIZATION_COMMAND = new CreateOrganization(organizationId, A_NAME, A_SUMMARY, A_URL);

    @BeforeClass
    public static void setUpClass() {
        EventSourcingRepository<Organization> organizationRepository = subscribe(Organization.class);
        EventSourcingRepository<ServiceSpecification> serviceSpecificationRepository = subscribe(ServiceSpecification.class);
        EventSourcingRepository<ServiceInstance> serviceInstanceRepository = subscribe(ServiceInstance.class);
        OrganizationCommandHandler organizationCommandHandler = new OrganizationCommandHandler();
        organizationCommandHandler.setOrganizationRepository(organizationRepository);
        organizationCommandHandler.setServiceSpecificationRepository(serviceSpecificationRepository);
        organizationCommandHandler.setServiceInstanceRepository(serviceInstanceRepository);
        subscribeHandler(organizationCommandHandler);
    }

    @Before
    public void setUp() {
        subscribeListener(new ServiceSpecificationListener(serviceSpecificationQueryRepository));
    }

    @Test
    public void testPrepareServiceSpecification() {

        serviceSpecificationQueryRepository.deleteAll();
        commandGateway.sendAndWait(CREATE_ORGANIZATION_COMMAND);
        commandGateway.sendAndWait(aPrepareServiceSpecificationCommand(organizationId, serviceSpecificationId1));
        assertEquals(1, serviceSpecificationQueryRepository.count());
        assertEquals(A_NAME, serviceSpecificationQueryRepository.findOne(serviceSpecificationId1.identifier()).getName());

        commandGateway.sendAndWait(aPrepareServiceSpecificationCommand(organizationId, serviceSpecificationId2));
        commandGateway.sendAndWait(aPrepareServiceSpecificationCommand(organizationId, serviceSpecificationId3));
        assertEquals(3, serviceSpecificationQueryRepository.count());

        try {
            commandGateway.sendAndWait(aPrepareServiceSpecificationCommand(organizationId, serviceSpecificationId1));
            fail("Should fail as item already exist");
        } catch (Exception e) {
        }

        assertEquals(3, serviceSpecificationQueryRepository.count());
    }

}
