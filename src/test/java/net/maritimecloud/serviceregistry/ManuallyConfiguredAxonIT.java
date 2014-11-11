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

import net.maritimecloud.serviceregistry.command.servicespecification.*;
import net.maritimecloud.serviceregistry.command.organization.*;
import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.common.infrastructure.axon.AbstractManuallyComnfiguredAxonCqrsIT;
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
    private static final String A_NAME = "a name";
    private static final String A_SUMMARY_ = "a summary ...";
    private final CreateOrganizationCommand CREATE_ORGANIZATION_COMMAND = new CreateOrganizationCommand(organizationId, A_NAME, A_SUMMARY_);

    @BeforeClass
    public static void setUpClass() {
        EventSourcingRepository<Organization> organizationRepository = subscribe(Organization.class);
        EventSourcingRepository<ServiceSpecification> serviceSpecificationRepository = subscribe(ServiceSpecification.class);
        OrganizationCommandHandler organizationCommandHandler = new OrganizationCommandHandler();
        organizationCommandHandler.setRepository(organizationRepository);
        organizationCommandHandler.setServiceSpecificationRepository(serviceSpecificationRepository);
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
        commandGateway.sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId1, A_NAME, A_SUMMARY_));
        assertEquals(1, serviceSpecificationQueryRepository.count());
        assertEquals("a name", serviceSpecificationQueryRepository.findOne(serviceSpecificationId1.identifier()).getName());

        commandGateway.sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId2, A_NAME, A_SUMMARY_));
        commandGateway.sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId3, A_NAME, A_SUMMARY_));

        assertEquals(3, serviceSpecificationQueryRepository.count());

        try {
            commandGateway.sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId1, A_NAME, A_SUMMARY_));
            fail("Should fail as item already exist");
        } catch (Exception e) {
        }

        assertEquals(3, serviceSpecificationQueryRepository.count());
    }

}
