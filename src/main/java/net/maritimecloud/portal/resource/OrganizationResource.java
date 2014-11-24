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
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.maritimecloud.portal.application.ApplicationServiceRegistry;
import static net.maritimecloud.portal.resource.GenericCommandResource.APPLICATION_JSON_CQRS_COMMAND;
import static net.maritimecloud.portal.resource.GenericCommandResource.sendAndWait;
import net.maritimecloud.serviceregistry.command.CommandRegistry;
import net.maritimecloud.serviceregistry.command.organization.ChangeOrganizationNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.command.organization.CreateOrganizationCommand;
import net.maritimecloud.serviceregistry.command.organization.PrepareServiceSpecificationCommand;
import net.maritimecloud.serviceregistry.command.organization.ProvideServiceInstanceCommand;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.command.servicespecification.ChangeServiceSpecificationNameAndSummaryCommand;
import net.maritimecloud.serviceregistry.query.OrganizationEntry;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceInstanceEntry;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationEntry;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Christoffer Børrild
 */
@Path("/api/organization")
public class OrganizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;

    @Resource
    protected CommandGateway commandGateway;
    
    // -------------------------------------------------------
    // Commands
    // -------------------------------------------------------
    
    private static final CommandRegistry postCommandsRegistry = new CommandRegistry(
            CreateOrganizationCommand.class,
            PrepareServiceSpecificationCommand.class,
            ProvideServiceInstanceCommand.class
    );
    private static final CommandRegistry putCommandsRegistry = new CommandRegistry(
            ChangeOrganizationNameAndSummaryCommand.class,
            ChangeServiceSpecificationNameAndSummaryCommand.class,
            ChangeServiceInstanceNameAndSummaryCommand.class
    );
    private static final CommandRegistry deleteCommandsRegistry = new CommandRegistry();
    private static final CommandRegistry patchCommandsRegistry = new CommandRegistry();

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    public void mappedPostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(OrganizationResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }
    
    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    public void mappedPutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("PUT command: " + commandJSON);
        sendAndWait(contentType, queryCommandName, putCommandsRegistry, commandJSON);
    }
    
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance")
    public void serviceInstancePostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("POST command: " + commandJSON);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(OrganizationResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        GenericCommandResource.sendAndWait(contentType, queryCommandName, postCommandsRegistry, commandJSON);
    }
    
    // -------------------------------------------------------
    // Queries
    // -------------------------------------------------------

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrganizationEntry> getOrganizations(
            @QueryParam("member") @DefaultValue("") String memberUID,
            @QueryParam("namePattern") @DefaultValue("") String organizationNamePattern
    ) {

        LOG.info("getOrganizations");

        Iterable<OrganizationEntry> all = ApplicationServiceRegistry.organizationQueryRepository().findAll();
        List<OrganizationEntry> organizationEntries = new ArrayList<>();

        for (OrganizationEntry organizationEntry : all) {

            // TODO: create a mapping between organizations and members
            if (memberUID.isEmpty() || organizationEntry.getSummary().contains(memberUID)) {
                organizationEntries.add(organizationEntry);
            }

        }

        return organizationEntries;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("specification")
    public List<ServiceSpecificationEntry> getSpecifications(@QueryParam("namePattern") @DefaultValue("") String usernamePattern) {

        Iterable<ServiceSpecificationEntry> all = ApplicationServiceRegistry.serviceSpecificationQueryRepository().findAll();
        List<ServiceSpecificationEntry> serviceSpecificationEntries = new ArrayList<>();

        for (ServiceSpecificationEntry organizationEntry : all) {
            serviceSpecificationEntries.add(organizationEntry);
        }

        return serviceSpecificationEntries;
    }

    // SERVICE INSTANCE ------------------------------------------------
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance")
    public List<ServiceInstanceEntry> getInstances(
            @PathParam("organizationId") String organizationId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {

        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findByProviderId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{organizationId}/service-instance/{serviceInstanceId}")
    public ServiceInstanceEntry getInstances(
            @PathParam("organizationId") String organizationId,
            @PathParam("serviceInstanceId") String serviceInstanceId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findOne(serviceInstanceId);
    }
    
}
