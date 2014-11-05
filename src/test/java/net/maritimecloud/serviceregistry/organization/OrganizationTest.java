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

import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Christoffer Børrild
 */
public class OrganizationTest {

    private FixtureConfiguration<Organization> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Organization.class);
    }

    @Test
    public void createOrganization() throws Exception {
        fixture.given()
                .when(new CreateOrganizationCommand(new OrganizationId("an organization id"), "a name", "a summary ..."))
                .expectEvents(new OrganizationCreatedEvent(new OrganizationId("an organization id"), "a name", "a summary ..."));
    }

    @Test
    public void changeOrganizationNameAndSummary() throws Exception {
        fixture.given(new OrganizationCreatedEvent(new OrganizationId("an organization id"), "a name", "a summary ..."))
                .when(new ChangeOrganizationNameAndSummaryCommand(new OrganizationId("an organization id"), "a new name", "a new summary ..."))
                .expectEvents(new OrganizationNameAndSummaryChangedEvent(new OrganizationId("an organization id"), "a new name", "a new summary ..."));
    }

}
