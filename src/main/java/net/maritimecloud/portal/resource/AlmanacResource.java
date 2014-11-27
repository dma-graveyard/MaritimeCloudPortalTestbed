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
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.query.OperationalServiceEntry;
import net.maritimecloud.serviceregistry.query.OrganizationEntry;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AlmanacResource resembles the publicly available API part of the RegistryService.
 * <p>
 * This resource should be accessible by all users.
 * <p>
 * @author Christoffer Børrild
 */
@Path("/api/almanac")
public class AlmanacResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlmanacResource.class);

    private static final List<OperationalServiceEntry> operationalServices = new ArrayList<>();

    static {
        System.out.println("FIXME: Hardcoding Operational Services!");
        operationalServices.add(new OperationalServiceEntry("lps", "imo", "Local Port Services", "Summary of Local Port Services"));
        operationalServices.add(new OperationalServiceEntry("mis", "imo", "Meteorological Information Services", "Summary of Meteorological Information Services"));
        operationalServices.add(new OperationalServiceEntry("msi", "imo", "Maritime Safety Information", "Summary of Maritime Safety Information"));
        operationalServices.add(new OperationalServiceEntry("msinm", "imo", "Maritime Safety Information & Notices to Mariners", "Summary of Maritime Safety Information & Notices to Mariners"));
        operationalServices.add(new OperationalServiceEntry("nas", "imo", "Navigational Assistance Service", "Summary of Navigational Assistance Service"));
        operationalServices.add(new OperationalServiceEntry("nga", "imo", "No-Go Area", "Summary of No-Go Area"));
        operationalServices.add(new OperationalServiceEntry("rme", "imo", "Route METOC", "Summary of Route METOC"));
        operationalServices.add(new OperationalServiceEntry("sre", "imo", "Strategical Route Exchange", "Summary of Strategical Route Exchange"));
        operationalServices.add(new OperationalServiceEntry("tos", "imo", "Traffic Organization Service", "Summary of Traffic Organization Service"));
        operationalServices.add(new OperationalServiceEntry("vsr", "imo", "Vessel Shore Reporting", "Summary of Vessel Shore Reporting"));
        operationalServices.add(new OperationalServiceEntry("wvtsg", "imo", "World Vessel Traffic Services Guide", "Summary of World Vessel Traffic Services Guide"));
        operationalServices.add(new OperationalServiceEntry("tre", "imo", "Tactical Route Exchange", "Summary of Tactical Route Exchange"));
        operationalServices.add(new OperationalServiceEntry("tus", "imo", "Tugs Services", "Summary of Tugs Services"));
    }

    private void addHardcodedOperationalServicesHACK() {
        // HACK HACK HACK
        while (!operationalServices.isEmpty()) {
            OperationalServiceEntry os = operationalServices.remove(0);
            ApplicationServiceRegistry.operationalServiceQueryRepository().save(os);
            System.out.println("Added hardcoded Operational Service to repository: " + os);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("operational-service")
    public Iterable<OperationalServiceEntry> queryOperationalServices(
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        addHardcodedOperationalServicesHACK();
        return ApplicationServiceRegistry.operationalServiceQueryRepository().findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("operational-service/{operationalServiceId}")
    public OperationalServiceEntry getOperationalService(@PathParam("operationalServiceId") String operationalServiceId) {
        addHardcodedOperationalServicesHACK();
        return ApplicationServiceRegistry.operationalServiceQueryRepository().findOne(operationalServiceId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public Iterable<OrganizationEntry> queryOrganizations(
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        return ApplicationServiceRegistry.organizationQueryRepository().findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        simulateLack(50);
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-specification")
    public Iterable<ServiceSpecificationEntry> queryServiceSpecifications(
            @QueryParam("operationalServiceId") @DefaultValue("") String operationalServiceId,
            @QueryParam("providerId") @DefaultValue("") String providerId,
            @QueryParam("serviceType") @DefaultValue("") String serviceType,
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        simulateLack(86);

        if (operationalServiceId.isEmpty()) {
            return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        } else {
            return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findByOperationalServiceId(operationalServiceId);
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-specification/{serviceSpecificationId}")
    public ServiceSpecificationEntry getServiceSpecification(@PathParam("serviceSpecificationId") String serviceSpecificationId) {
        simulateLack(120);
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-instance")
    public Iterable<ServiceInstanceEntry> queryInstances(
            @QueryParam("operationalServiceId") @DefaultValue("") String operationalServiceId,
            @QueryParam("serviceSpecificationId") @DefaultValue("") String serviceSpecificationId,
            @QueryParam("providerId") @DefaultValue("") String providerId,
            @QueryParam("serviceType") @DefaultValue("") String serviceType,
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        simulateLack(143);
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-instance/{serviceInstanceId}")
    public ServiceInstanceEntry getInstance(
            @PathParam("serviceInstanceId") String serviceInstanceId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
    }

    private void simulateLack(long millis) {
        try {
            System.out.println("Simulating " + millis + " milliseconds lack in class " + getClass());
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }

}
