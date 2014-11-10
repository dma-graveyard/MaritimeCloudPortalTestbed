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

import java.util.UUID;
import javax.annotation.Resource;
import net.maritimecloud.portal.config.ApplicationTestConfig;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.repository.AggregateNotFoundException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for Organization commands (run with 'mvn failsafe:integration-test')
 * <p>
 * @author Christoffer Børrild
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
public class OrganizationIT /*extends AbstractAxonCqrsIT*/ {

    @Resource
    protected CommandGateway commandGateway;

    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;

    private final String itemId = UUID.randomUUID().toString();
    private final OrganizationId organizationId = new OrganizationId(itemId);
    private final OrganizationId organizationId2 = new OrganizationId(itemId + "2");
    private static final String A_NAME = "a name";
    private static final String A_SUMMARY_ = "a summary ...";
    private final CreateOrganizationCommand CREATE_ORGANIZATION_COMMAND = new CreateOrganizationCommand(organizationId, A_NAME, A_SUMMARY_);

    @Before
    public void setUp() {
        organizationQueryRepository.deleteAll();
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
    public void cannotChangeNonExistingOrganization() throws Throwable {
        try {
            commandGateway.sendAndWait(new ChangeOrganizationNameAndSummaryCommand(new OrganizationId("notCreated"), A_NAME, A_SUMMARY_));
        } catch (CommandExecutionException e) {
            throw e.getCause();
        }
    }

}
