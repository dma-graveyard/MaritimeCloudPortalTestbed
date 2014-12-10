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
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import static net.maritimecloud.portal.resource.ResourceResolver.resolveOrganizationIdOrFail;
import static net.maritimecloud.portal.resource.ResourceResolver.resolveServiceInstance;
import static net.maritimecloud.portal.resource.ResourceResolver.resolveServiceSpecification;
import static net.maritimecloud.portal.resource.GenericCommandResource.APPLICATION_JSON_CQRS_COMMAND;
import net.maritimecloud.serviceregistry.command.CommandRegistry;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.ProvideServiceInstance;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverage;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
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
@Path("/api")
public class OrganizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;

    @Resource
    protected CommandGateway commandGateway;

    // -------------------------------------------------------
    // Commands
    // -------------------------------------------------------
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public void organizationPostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Organization POST command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                CreateOrganization.class
        );
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
    public void organizationPutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Organization PUT command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeOrganizationNameAndSummary.class
        );
    }

    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationId}/si")
    public void serviceInstancePostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance POST command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                ProvideServiceInstance.class
        );
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationId}/si/{serviceInstanceId}")
    public void serviceInstancePutCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance PUT command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeServiceInstanceNameAndSummary.class,
                ChangeServiceInstanceCoverage.class,
                AddServiceInstanceEndpoint.class,
                AddServiceInstanceAlias.class,
                RemoveServiceInstanceEndpoint.class
        );
    }

    @DELETE
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationId}/si/{serviceInstanceId}")
    public void serviceInstanceDeleteCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Service Instance DELETE command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                RemoveServiceInstanceEndpoint.class
        );
    }

    private void sendAndWait(String contentType, String queryCommandName, String commandJSON, Class... classes) {
        GenericCommandResource.sendAndWait(contentType, queryCommandName, new CommandRegistry(classes), commandJSON);
    }

    // -------------------------------------------------------
    // Queries
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("organization")
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
    @Path("organization/{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    // ------------------------------------------------------------------------
    // SERVICE SPECIFICATIONS
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/ss")
    public List<ServiceSpecificationEntry> queryServiceSpecificationsByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findByOwnerId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/ss/{serviceSpecificationAliasOrId}")
    public ServiceSpecificationEntry getServiceSpecificationByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @PathParam("serviceSpecificationAliasOrId") String serviceSpecificationAliasOrId
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return resolveServiceSpecification(organizationId, serviceSpecificationAliasOrId);
    }

    // ------------------------------------------------------------------------
    // SERVICE INSTANCES
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/si")
    public List<ServiceInstanceEntry> queryServiceInstancesByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findByProviderId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationAliasOrId}/si/{serviceInstanceAliasOrId}")
    public ServiceInstanceEntry getServiceInstanceByAlias(
            @PathParam("organizationAliasOrId") String organizationAliasOrId,
            @PathParam("serviceInstanceAliasOrId") String serviceInstanceAliasOrId
    ) {
        // TODO: move to almanac api as list aliases ...or something
        System.out.println("ALL: " + ApplicationServiceRegistry.aliasRegistryQueryRepository().findAll());

        String organizationId = resolveOrganizationIdOrFail(organizationAliasOrId);
        return resolveServiceInstance(organizationId, serviceInstanceAliasOrId);
    }

}
