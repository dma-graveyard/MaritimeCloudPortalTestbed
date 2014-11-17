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
package net.maritimecloud.common.infrastructure.axon;

import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import java.util.UUID;
import net.maritimecloud.serviceregistry.command.organization.CreateOrganizationCommand;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;

/**
 * Helper class that provides useful constants and factories for instances of various test data.
 * <p>
 * @author Christoffer Børrild
 */
public class CommonFixture {
    
    public static final String AN_ORG_ID = "AN_ORG_ID";
    public static final String A_SPEC_ID = "A_SPEC_ID";
    public static final String AN_INSTANCE_ID = "AN_INSTANCE_ID";
    public static final String A_NAME = "a name";
    public static final String A_SUMMARY = "a summary ...";
    public static final Coverage A_COVERAGE = new Coverage() {
    };
    public static final String ANOTHER_NAME = "another name";
    public static final String ANOTHER_SUMMARY = "another summary ...";
    public static final Coverage ANOTHER_COVERAGE = new Coverage() {
    };

    public static String generateIdentity() {
        return UUID.randomUUID().toString();
    }

    public static OrganizationId generateOrganizationId() {
        return new OrganizationId(generateIdentity());
    }

    public static OrganizationId generateOrganizationId(String organizationIdentity) {
        return new OrganizationId(organizationIdentity);
    }

    public static ServiceSpecificationId generateServiceSpecificationId() {
        return new ServiceSpecificationId(generateIdentity());
    }

    public static ServiceInstanceId generateServiceInstanceId() {
        return new ServiceInstanceId(generateIdentity());
    }

    public static CreateOrganizationCommand generateCreateOrganizationCommand(String organizationIdentity) {
        return new CreateOrganizationCommand(generateOrganizationId(organizationIdentity), A_NAME, A_SUMMARY);
    }

}
