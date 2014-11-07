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

import net.maritimecloud.common.infrastructure.axon.AbstractAxonCqrsIT;
import java.util.UUID;
import org.axonframework.repository.AggregateNotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration test for Organization commands
 * (run with 'mvn failsafe:integration-test')
 * @author Christoffer Børrild
 */
public class OrganizationIT extends AbstractAxonCqrsIT {

    final String itemId = UUID.randomUUID().toString();
    final OrganizationId organizationId = new OrganizationId(itemId);
    final OrganizationId organizationId2 = new OrganizationId(itemId + "2");

    @Test
    public void testCqrs() {

        commandGateway.sendAndWait(new CreateOrganizationCommand(organizationId, "a name", "a summary ..."));
        commandGateway.sendAndWait(new ChangeOrganizationNameAndSummaryCommand(organizationId, "a new name", "a new summary ..."));

        try {
            commandGateway.sendAndWait(new CreateOrganizationCommand(organizationId, "a name", "a summary ..."));
            fail("Should fail as item already exist");
        } catch (Exception e) {
        }

        assertEquals(1, organizationQueryRepository.count());
        assertEquals("a new name", organizationQueryRepository.findOne(organizationId.identifier()).getName());

        commandGateway.send(new CreateOrganizationCommand(organizationId2, "a name", "a summary ..."));
        assertEquals(2, organizationQueryRepository.count());
    }

    @Test(expected = AggregateNotFoundException.class)
    public void cannotChangeNonExistingOrganization() {
        commandGateway.sendAndWait(new ChangeOrganizationNameAndSummaryCommand(new OrganizationId("notCreated"), "a name", "a summary ..."));
    }

}
