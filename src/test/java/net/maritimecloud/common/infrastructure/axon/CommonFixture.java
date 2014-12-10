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

import java.net.URI;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import java.util.UUID;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.OrganizationCreated;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.ServiceInstanceCreated;
import net.maritimecloud.serviceregistry.command.api.ServiceSpecificationCreated;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;

/**
 * Helper class that provides useful constants and factories for instances of various test data.
 * <p>
 * @author Christoffer Børrild
 */
public class CommonFixture {

    public static final String AN_ORG_ID = "AN_ORG_ID";
    public static final String A_SPEC_ID = "A_SPEC_ID";
    public static final String AN_INSTANCE_ID = "AN_INSTANCE_ID";
    public static final String ANOTHER_INSTANCE_ID = "ANOTHER_INSTANCE_ID";
    public static final ServiceType A_SERVICE_TYPE = ServiceType.AISASM;
    public static final String A_NAME = "a name";
    public static final String A_SUMMARY = "a summary ...";
    public static final String AN_ALIAS = "AN_ALIAS";
    public static final String ANOTHER_ALIAS = "ANOTHER_ALIAS";
    public static final Coverage A_COVERAGE = new Coverage("[{\"type\":\"polygon\",\"points\":[[12.557373046874998,56.29215668507645],[11.656494140625,56.022948079627454],[12.381591796875,55.41030721005218],[13.568115234375,55.61558902526749],[13.90869140625,56.072035471800866],[13.0517578125,55.83214387781303],[13.128662109375,56.17613891766981],[12.513427734375,55.99838095535963]]},{\"type\":\"rectangle\",\"topLeftLatitude\":56.05976947910657,\"topLeftLongitude\":9.38232421875,\"buttomRightLatitude\":55.429013452407396,\"buttomRightLongitude\":11.1181640625},{\"type\":\"circle\",\"center-latitude\":55.29162848682989,\"center-longitude\":11.074218749999998,\"radius\":49552.58124628375}]");
    public static final ServiceEndpoint AN_ENDPOINT = new ServiceEndpoint(URI.create("http://some-url/an-endpoint"));
    public static final ServiceEndpoint ANOTHER_ENDPOINT = new ServiceEndpoint(URI.create("http://some-url/another-endpoint"));
    public static final String ANOTHER_NAME = "another name";
    public static final String ANOTHER_SUMMARY = "another summary ...";
    public static final Coverage ANOTHER_COVERAGE = new Coverage("[{\"type\":\"rectangle\",\"topLeftLatitude\":56.05976947910657,\"topLeftLongitude\":9.38232421875,\"buttomRightLatitude\":55.429013452407396,\"buttomRightLongitude\":11.1181640625},{\"type\":\"circle\",\"center-latitude\":55.29162848682989,\"center-longitude\":11.074218749999998,\"radius\":49552.58124628375}]");
    public static final String A_URL = "http://a-url.com";
    public static final String ANOTHER_URL = "http://another-url.com";
    
    public static final OrganizationId anOrganizationId = new OrganizationId(AN_ORG_ID);
    public static final ServiceSpecificationId aServiceSpecificationId = new ServiceSpecificationId(A_SPEC_ID);
    public static final ServiceInstanceId aServiceInstanceId = new ServiceInstanceId(AN_INSTANCE_ID);
    public static final ServiceInstanceId anotherServiceInstanceId = new ServiceInstanceId(ANOTHER_INSTANCE_ID);

    public static OrganizationCreated organizationCreatedEvent() {
        return new OrganizationCreated(anOrganizationId, A_NAME, A_SUMMARY, A_URL);
    }

    public static ServiceSpecificationCreated serviceSpecificationCreatedEvent() {
        return new ServiceSpecificationCreated(anOrganizationId, aServiceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY);
    }

    public static ServiceInstanceCreated serviceInstanceCreatedEvent() {
        return new ServiceInstanceCreated(anOrganizationId, aServiceSpecificationId, aServiceInstanceId, A_NAME, A_SUMMARY, A_COVERAGE, A_SERVICE_TYPE);
    }
    
    public static String generateIdentity() {
        return UUID.randomUUID().toString();
    }

    public static OrganizationId generateOrganizationId() {
        return generateOrganizationId(generateIdentity());
    }

    public static OrganizationId generateOrganizationId(String organizationIdentity) {
        return new OrganizationId(organizationIdentity);
    }

    public static ServiceSpecificationId generateServiceSpecificationId() {
        return generateServiceSpecificationId(generateIdentity());
    }

    public static ServiceSpecificationId generateServiceSpecificationId(String serviceSpecificationId) {
        return new ServiceSpecificationId(serviceSpecificationId);
    }

    public static ServiceInstanceId generateServiceInstanceId() {
        return new ServiceInstanceId(generateIdentity());
    }

    public static CreateOrganization generateCreateOrganizationCommand(String organizationIdentity) {
        return new CreateOrganization(generateOrganizationId(organizationIdentity), A_NAME, A_SUMMARY, A_URL);
    }

    public static PrepareServiceSpecification aPrepareServiceSpecificationCommand(
            OrganizationId organizationId,
            ServiceSpecificationId serviceSpecificationId
    ) {
        return new PrepareServiceSpecification(organizationId, serviceSpecificationId, A_SERVICE_TYPE, A_NAME, A_SUMMARY);
    }

}
