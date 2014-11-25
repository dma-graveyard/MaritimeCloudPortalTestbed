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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import net.maritimecloud.serviceregistry.query.OrganizationEntry;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AlmanacResource resembles the publicly available API part of the RegistryService.
 * 
 * This resource should be accessible by all users. 
 *
 * @author Christoffer Børrild
 */
@Path("/api/almanac")
public class AlmanacResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlmanacResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public Iterable<OrganizationEntry> organizations(
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        return ApplicationServiceRegistry.organizationQueryRepository().findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization/{organizationId}")
    public OrganizationEntry organization(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        simulateLack(50);
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-specification")
    public Iterable<ServiceSpecificationEntry> serviceSpecifications(
            @QueryParam("operationalServiceId") @DefaultValue("") String operationalServiceId,
            @QueryParam("providerId") @DefaultValue("") String providerId,
            @QueryParam("serviceType") @DefaultValue("") String serviceType,
            @QueryParam("anyTextPattern") @DefaultValue("") String anyTextPattern
    ) {
        simulateLack(86);
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-specification/{serviceSpecificationId}")
    public ServiceSpecificationEntry serviceSpecification(
            @PathParam("serviceSpecificationId") String serviceSpecificationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        simulateLack(120);
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findOne(serviceSpecificationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("service-instance")
    public Iterable<ServiceInstanceEntry> getInstances(
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
    public ServiceInstanceEntry getInstances(
            @PathParam("serviceInstanceId") String serviceInstanceId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
    }

    private void simulateLack(long millis) {
        try {
            System.out.println("Simulating "+ millis +" milliseconds lack in class "+getClass());
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
    
}
