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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.ProvideServiceInstance;
import net.maritimecloud.serviceregistry.command.serviceinstance.Coverage;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecificationId;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceType;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporary hacker tools to for instance populate the model with randomized data
 * <p>
 * @author Christoffer Børrild
 */
@Path("/hack")
public class RandomizerResource {

    private static final Logger LOG = LoggerFactory.getLogger(RandomizerResource.class);

    // ------------------------------------------------------------------------
    // HACK - RANDOMIZER
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("randomizer")
    public void randomizer(
            @QueryParam("ss") @DefaultValue("0") int ssAmount,
            @QueryParam("si") @DefaultValue("1") int siAmount
    ) {
        randomizeServiceSpecification(ssAmount);
        randomizeServiceInstances(siAmount);
    }

    private void randomizeServiceSpecification(int amount) {
        Iterable<ServiceSpecificationEntry> serviceSpecificationEntrys = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        ArrayList<ServiceSpecificationEntry> l = new ArrayList<>();
        for (ServiceSpecificationEntry specificationEntry : serviceSpecificationEntrys) {
            l.add(specificationEntry);
        }

        if (l.isEmpty()) {
            return;
        }

        for (int i = 0; i < amount; i++) {
            Object command = new PrepareServiceSpecification(
                    new OrganizationId(random(l).getOwnerId()),
                    new ServiceSpecificationId("SS-" + i + "-" + UUID.randomUUID()),
                    random(Arrays.asList(ServiceType.values())),
                    random(l).getName() + "-" + i,
                    random(l).getSummary() + "-" + i);
            ApplicationServiceRegistry.commandGateway().sendAndWait(command);
        }
    }

    private void randomizeServiceInstances(int siAmount) {
        Iterable<ServiceInstanceEntry> serviceInstances = ApplicationServiceRegistry.serviceInstanceQueryRepository().findAll();
        ArrayList<ServiceInstanceEntry> l = new ArrayList<>();
        for (ServiceInstanceEntry serviceInstance : serviceInstances) {
            l.add(serviceInstance);
        }

        if (l.isEmpty()) {
            return;
        }

        for (int i = 0; i < siAmount; i++) {
            Object command = new ProvideServiceInstance(
                    new OrganizationId(random(l).getProviderId()),
                    new ServiceSpecificationId(random(l).getSpecificationId()),
                    new ServiceInstanceId("rand-" + i + "-" + UUID.randomUUID()),
                    random(l).getName() + "-" + i,
                    random(l).getSummary() + "-" + i,
                    randomCoverage());
            ApplicationServiceRegistry.commandGateway().sendAndWait(command);
        }
    }

    private <T> T random(List<T> l) {
        return l.get((int) (Math.random() * (l.size() - 1)));
    }

    private Coverage randomCoverage() {
        return new Coverage("[{"
                + "\"type\": \"circle\","
                + "\"center-latitude\": " + (80 - Math.random() * 160) + ","
                + "\"center-longitude\": " + (180 - Math.random() * 360) + ","
                + "\"radius\": " + (450000 - Math.random() * 22000) + "}]");
    }

}
