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
package net.maritimecloud.portal.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.maritimecloud.common.infrastructure.axon.CommonFixture;
import net.maritimecloud.serviceregistry.command.Command;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.ProvideServiceInstance;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverage;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceSpecificationNameAndSummary;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests JSON serilization and deserialization of command objects
 * <p>
 * @author Christoffer Børrild
 */
public class GenericCommandResourceTest extends CommonFixture {

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void deserialize() throws JsonProcessingException, IOException {

        String commandAsJSON = "{\"organizationId\":{\"identifier\":\"AN_ORG_ID\"},\"name\":\"A_NAME\",\"summary\":\"A_SUMMARY\",\"url\":\"http://a.url\"}";
        Map<String, Class> commandRegistry = new HashMap<>();
        commandRegistry.put(CreateOrganization.class.getCanonicalName(), CreateOrganization.class);
        commandRegistry.put(CreateOrganization.class.getSimpleName(), CreateOrganization.class);
        Object command = mapper.readValue(commandAsJSON, commandRegistry.get("CreateOrganization"));
        //System.out.println("Command: " + mapper.writeValueAsString(command));
    }

    @Test
    public void serialize() throws JsonProcessingException, IOException {
        CreateOrganization createOrganizationCommand
                = new CreateOrganization(new OrganizationId(AN_ORG_ID), A_NAME, A_SUMMARY, A_URL);
        String commandAsJSON = mapper.writeValueAsString(createOrganizationCommand);
        //System.out.println(commandAsJSON);
    }

    @Test
    public void JsonCreateOrganizationCommand() throws Exception {
        CreateOrganization command
                = serializeAndDeserializeCommand(new CreateOrganization(
                                new OrganizationId(AN_ORG_ID),
                                A_NAME,
                                A_SUMMARY,
                                A_URL
                        )
                );

        assertEquals(AN_ORG_ID, command.getOrganizationId().identifier());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
        assertEquals(A_URL, command.getUrl());
    }

    @Test
    public void jsonChangeOrganizationNameAndSummaryCommand() throws Exception {
        ChangeOrganizationNameAndSummary command
                = serializeAndDeserializeCommand(new ChangeOrganizationNameAndSummary(
                                new OrganizationId(AN_ORG_ID),
                                A_NAME,
                                A_SUMMARY)
                );

        assertEquals(AN_ORG_ID, command.getOrganizationId().identifier());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
    }

    @Test
    public void jsonPrepareServiceSpecificationCommand() throws Exception {

        PrepareServiceSpecification command
                = serializeAndDeserializeCommand(new PrepareServiceSpecification(
                                new OrganizationId(AN_ORG_ID),
                                new ServiceSpecificationId(A_SPEC_ID),
                                A_SERVICE_TYPE,
                                A_NAME,
                                A_SUMMARY)
                );

        assertEquals(AN_ORG_ID, command.getOwnerId().identifier());
        assertEquals(A_SPEC_ID, command.getServiceSpecificationId().identifier());
        assertEquals(A_SERVICE_TYPE, command.getServiceType());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
    }

    @Test
    public void jsonChangeServiceSpecificationNameAndSummaryCommand() throws Exception {

        ChangeServiceSpecificationNameAndSummary command
                = serializeAndDeserializeCommand(new ChangeServiceSpecificationNameAndSummary(
                                new ServiceSpecificationId(A_SPEC_ID),
                                A_NAME,
                                A_SUMMARY)
                );

        assertEquals(A_SPEC_ID, command.getServiceSpecificationId().identifier());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
    }

    @Test
    public void jsonProvideServiceInstanceCommand() throws Exception {

        String commandAsJSON = "{\"providerId\":{\"identifier\":\"dma\"},\"specificationId\":{\"identifier\":\"imo-msi-soap\"},\"serviceInstanceId\":{\"identifier\":\"vcxzvzvcxz\"},\"name\":\"vcxvcxzvxz\",\"summary\":\"vcxzvcxzvx\",\"coverage\":[{\"type\":\"polygon\",\"points\":[[12.557373046874998,56.29215668507645],[11.656494140625,56.022948079627454],[12.381591796875,55.41030721005218],[13.568115234375,55.61558902526749],[13.90869140625,56.072035471800866],[13.0517578125,55.83214387781303],[13.128662109375,56.17613891766981],[12.513427734375,55.99838095535963]]},{\"type\":\"rectangle\",\"topLeftLatitude\":56.05976947910657,\"topLeftLongitude\":9.38232421875,\"buttomRightLatitude\":55.429013452407396,\"buttomRightLongitude\":11.1181640625},{\"type\":\"circle\",\"center-latitude\":55.29162848682989,\"center-longitude\":11.074218749999998,\"radius\":49552.58124628375}]}";
        //System.out.println("mapper.readValue: " + mapper.readValue(commandAsJSON, ProvideServiceInstance.class));

        ProvideServiceInstance command
                = serializeAndDeserializeCommand(new ProvideServiceInstance(
                                new OrganizationId(AN_ORG_ID),
                                new ServiceSpecificationId(A_SPEC_ID),
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                A_NAME,
                                A_SUMMARY,
                                new Coverage("[]")
                        )
                );

        assertEquals(AN_ORG_ID, command.getProviderId().identifier());
        assertEquals(A_SPEC_ID, command.getSpecificationId().identifier());
        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
    }

    @Test
    public void jsonProvideServiceInstanceCommand2() throws Exception {
        String commandAsJSON = "{\"providerId\":{\"identifier\":\"dma\"},\"specificationId\":{\"identifier\":\"imo-msi-soap\"},\"serviceInstanceId\":{\"identifier\":\"vcxzvzvcxz\"},\"name\":\"vcxvcxzvxz\",\"summary\":\"vcxzvcxzvx\",\"coverage\":[{\"type\":\"polygon\",\"points\":[[12.557373046874998,56.29215668507645],[11.656494140625,56.022948079627454],[12.381591796875,55.41030721005218],[13.568115234375,55.61558902526749],[13.90869140625,56.072035471800866],[13.0517578125,55.83214387781303],[13.128662109375,56.17613891766981],[12.513427734375,55.99838095535963]]},{\"type\":\"rectangle\",\"topLeftLatitude\":56.05976947910657,\"topLeftLongitude\":9.38232421875,\"buttomRightLatitude\":55.429013452407396,\"buttomRightLongitude\":11.1181640625},{\"type\":\"circle\",\"center-latitude\":55.29162848682989,\"center-longitude\":11.074218749999998,\"radius\":49552.58124628375}]}";
        assertEquals(commandAsJSON, deserializeAndSerializeCommand(commandAsJSON, ProvideServiceInstance.class));
    }

    @Test
    public void jsonChangeServiceInstanceNameAndSummaryCommand() throws Exception {

        ChangeServiceInstanceNameAndSummary command
                = serializeAndDeserializeCommand(new ChangeServiceInstanceNameAndSummary(
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                A_NAME,
                                A_SUMMARY
                        )
                );

        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(A_NAME, command.getName());
        assertEquals(A_SUMMARY, command.getSummary());
    }

    @Test
    public void jsonChangeServiceInstanceCoverageCommand() throws Exception {

        ChangeServiceInstanceCoverage command
                = serializeAndDeserializeCommand(new ChangeServiceInstanceCoverage(
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                A_COVERAGE
                        )
                );

        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(A_COVERAGE, command.getCoverage());
    }

    @Test
    public void jsonAddServiceInstanceEndpointCommand() throws Exception {
        AddServiceInstanceEndpoint command
                = serializeAndDeserializeCommand(new AddServiceInstanceEndpoint(
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                AN_ENDPOINT
                        )
                );

        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(AN_ENDPOINT, command.getServiceEndpoint());
    }

    @Test
    public void jsonRemoveServiceInstanceEndpointCommand() throws Exception {
        RemoveServiceInstanceEndpoint command
                = serializeAndDeserializeCommand(new RemoveServiceInstanceEndpoint(
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                AN_ENDPOINT
                        )
                );

        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(AN_ENDPOINT, command.getServiceEndpoint());
    }

    private <T extends Command> T serializeAndDeserializeCommand(T command)
            throws JsonProcessingException, IOException {
        String commandAsJSON = mapper.writeValueAsString(command);
        System.out.println(String.format("[JSON] %0$50s: %1s", command.getClass().getSimpleName(), commandAsJSON));
        return (T) mapper.readValue(commandAsJSON, command.getClass());
    }

    private String deserializeAndSerializeCommand(String commandAsJSON, Class commandClass)
            throws JsonProcessingException, IOException {
        Object command = mapper.readValue(commandAsJSON, commandClass);
        return mapper.writeValueAsString(command);
    }

}
