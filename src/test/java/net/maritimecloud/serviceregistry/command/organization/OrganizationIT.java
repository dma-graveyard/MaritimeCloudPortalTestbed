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

import net.maritimecloud.common.infrastructure.axon.AbstractAxonCqrsIT;
import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import net.maritimecloud.serviceregistry.query.OrganizationListener;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.repository.AggregateNotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Integration test for Organization commands
 * (run with 'mvn failsafe:integration-test')
 * @author Christoffer Børrild
 */
public class OrganizationIT extends AbstractAxonCqrsIT {
    
    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;
    

    private final String itemId = UUID.randomUUID().toString();
    private final OrganizationId organizationId = new OrganizationId(itemId);
    private final OrganizationId organizationId2 = new OrganizationId(itemId + "2");
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
        subscribeListener(new OrganizationListener(organizationQueryRepository));
    }

    @Test
    public void testCqrs() {

        commandGateway.sendAndWait(CREATE_ORGANIZATION_COMMAND);
        commandGateway.sendAndWait(new ChangeOrganizationNameAndSummaryCommand(organizationId, "a new name", "a new summary ..."));

        try {
            commandGateway.sendAndWait(CREATE_ORGANIZATION_COMMAND);
            fail("Should fail as item already exist");
        } catch (Exception e) {
        }

        assertEquals(1, organizationQueryRepository.count());
        assertEquals("a new name", organizationQueryRepository.findOne(organizationId.identifier()).getName());

        commandGateway.send(new CreateOrganizationCommand(organizationId2, A_NAME, A_SUMMARY_));
        assertEquals(2, organizationQueryRepository.count());
    }

    @Test(expected = AggregateNotFoundException.class)
    public void cannotChangeNonExistingOrganization() {
        commandGateway.sendAndWait(new ChangeOrganizationNameAndSummaryCommand(new OrganizationId("notCreated"), A_NAME, A_SUMMARY_));
    }

}
