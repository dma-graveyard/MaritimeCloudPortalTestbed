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
import net.maritimecloud.serviceregistry.query.OrganizationEntry;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.repository.AggregateNotFoundException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for Organization commands (run with 'mvn failsafe:integration-test')
 * <p>
 * @author Christoffer Børrild
 */
public class OrganizationIT extends AbstractAxonCqrsIT {

    private CreateOrganizationCommand createOrganizationCommand;
    private OrganizationId organizationId;
    private OrganizationId organizationId2;

    @Before
    public void setUp() {
        // prepare an organization 
        createOrganizationCommand = generateCreateOrganizationCommand(generateIdentity());
        organizationId = createOrganizationCommand.getOrganizationId();
        organizationId2 = generateOrganizationId();
    }

    @Test
    public void testOrganization() {

        commandGateway().sendAndWait(createOrganizationCommand);
        commandGateway().sendAndWait(new ChangeOrganizationNameAndSummaryCommand(organizationId, ANOTHER_NAME, ANOTHER_SUMMARY));

        try {
            commandGateway().sendAndWait(createOrganizationCommand);
            fail("Should fail as item already exist");
        } catch (Exception e) {
        }

        assertEquals(1, organizationQueryRepository.count());
        OrganizationEntry entry = organizationQueryRepository.findOne(organizationId.identifier());
        assertEquals(ANOTHER_NAME, entry.getName());
        assertEquals(ANOTHER_SUMMARY, entry.getSummary());

        commandGateway().send(new CreateOrganizationCommand(organizationId2, A_NAME, A_SUMMARY));
        assertEquals(2, organizationQueryRepository.count());
    }

    @Test(expected = AggregateNotFoundException.class)
    public void cannotChangeNonExistingOrganization() throws Throwable {
        try {
            commandGateway().sendAndWait(new ChangeOrganizationNameAndSummaryCommand(new OrganizationId("notCreated"), A_NAME, A_SUMMARY));
        } catch (CommandExecutionException e) {
            throw e.getCause();
        }
    }

}
