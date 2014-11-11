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
package net.maritimecloud.serviceregistry.command.servicespecification;

import net.maritimecloud.serviceregistry.command.organization.*;
import net.maritimecloud.common.infrastructure.axon.AbstractAxonCqrsIT;
import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationQueryRepository;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Integration test for Organization commands (run with 'mvn failsafe:integration-test')
 * <p>
 * @author Christoffer Børrild
 */
public class ServiceSpecificationIT extends AbstractAxonCqrsIT {

    @Resource
    protected ServiceSpecificationQueryRepository serviceSpecificationQueryRepository;

    private final ServiceSpecificationId serviceSpecificationId1 = generateServiceSpecificationId();
    private final ServiceSpecificationId serviceSpecificationId2 = generateServiceSpecificationId();
    private final ServiceSpecificationId serviceSpecificationId3 = generateServiceSpecificationId();

    private CreateOrganizationCommand createOrganizationCommand;
    private OrganizationId organizationId;
    private ServiceSpecificationId serviceSpecificationId;
    private PrepareServiceSpecificationCommand prepareServiceSpecificationCommand;

    @Before
    public void setUp() {
        // prepare an organization 
        createOrganizationCommand = generateCreateOrganizationCommand(generateIdentity());
        organizationId = createOrganizationCommand.getOrganizationId();
        // prepare a service specification
        serviceSpecificationId = generateServiceSpecificationId();
        prepareServiceSpecificationCommand
                = new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId, A_NAME, A_SUMMARY);
    }

    @Test
    public void prepareServiceSpecification() {

        // Given an organization
        commandGateway().sendAndWait(createOrganizationCommand);
        
        // When the Organization prepares anew Service Specification
        commandGateway().sendAndWait(prepareServiceSpecificationCommand);
        
        // The the views are updated
        assertEquals(1, serviceSpecificationQueryRepository.count());
        ServiceSpecificationEntry entry = serviceSpecificationQueryRepository.findOne(serviceSpecificationId.identifier());
        assertEquals(A_NAME, entry.getName());
        assertEquals(A_SUMMARY, entry.getSummary());

        // When we add some more
        commandGateway().sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId2, A_NAME, A_SUMMARY));
        commandGateway().sendAndWait(new PrepareServiceSpecificationCommand(organizationId, serviceSpecificationId3, A_NAME, A_SUMMARY));

        // The views grow
        assertEquals(3, serviceSpecificationQueryRepository.count());

        // When we try to add a duplicate
        try {
            commandGateway().sendAndWait(prepareServiceSpecificationCommand);
            fail("Should fail as item already exist");
        } catch (Exception e) {
            // Then it fails
        }

        // ...and nothing grows - still three specifications
        assertEquals(3, serviceSpecificationQueryRepository.count());
    }

    @Test
    public void changeNameAndSummary() {

        // Given an organization with a Service Specification
        commandGateway().sendAndWait(createOrganizationCommand);
        commandGateway().sendAndWait(prepareServiceSpecificationCommand);

        // When the description is changed
        commandGateway().sendAndWait(
                new ChangeServiceSpecificationNameAndSummaryCommand(serviceSpecificationId, ANOTHER_NAME, ANOTHER_SUMMARY));
        
        // Then the name and summary has changed in the view
        ServiceSpecificationEntry entry = serviceSpecificationQueryRepository.findOne(serviceSpecificationId.identifier());
        assertEquals(ANOTHER_NAME, entry.getName());
        assertEquals(ANOTHER_SUMMARY, entry.getSummary());

    }

    // Next up: 
    // Wire up main (see https://github.com/MagnusSmith/axon-orders/tree/master/web-core/src/main/java/com/example/config )
    // Add REST interface
    // introduce ServiceInstances
}
