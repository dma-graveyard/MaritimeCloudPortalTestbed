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
import javax.ws.rs.WebApplicationException;
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
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationWebsiteUrl;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverage;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceSpecificationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.query.AliasRegistryEntry;
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
    // -------------------------------------------------------
    // Commands
    // -------------------------------------------------------
    // -------------------------------------------------------
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org")
    public void organizationPostCommand(@HeaderParam("Content-type") String contentType, @QueryParam("command") @DefaultValue("") String queryCommandName, String commandJSON) {
        LOG.info("Organization POST command");
        sendAndWait(contentType, queryCommandName, commandJSON,
                CreateOrganization.class
        );
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}")
    public void organizationPutCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Organization PUT command");
        assertCommandContext(commandJSON, organizationIdOrAlias);
        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeOrganizationNameAndSummary.class,
                ChangeOrganizationWebsiteUrl.class
        );
    }

    // ------------------------------------------------------------------------
    // SERVICE SPECIFICATIONS
    // ------------------------------------------------------------------------
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/ss")
    public void serviceSpecificationPostCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance POST command");
        assertCommandContext(commandJSON, organizationIdOrAlias);
        sendAndWait(contentType, queryCommandName, commandJSON,
                PrepareServiceSpecification.class
        );
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/ss/{serviceSpecificationIdOrAlias}")
    public void serviceSpecificationPutCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("serviceSpecificationIdOrAlias") String serviceSpecificationIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance PUT command");

        // assert that specification exists and belongs to organization
        ServiceSpecificationEntry serviceSpecification = getServiceSpecificationByAlias(organizationIdOrAlias, serviceSpecificationIdOrAlias);
        assertCommandContext(commandJSON, serviceSpecification.getServiceSpecificationId());
        
        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeServiceSpecificationNameAndSummary.class
        );
    }

    // ------------------------------------------------------------------------
    // SERVICE INSTANCES
    // ------------------------------------------------------------------------
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si")
    public void serviceInstancePostCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance POST command");
        assertCommandContext(commandJSON, organizationIdOrAlias);
        sendAndWait(contentType, queryCommandName, commandJSON,
                ProvideServiceInstance.class
        );
    }

    @PUT
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si/{serviceInstanceIdOrAlias}")
    public void serviceInstancePutCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("serviceInstanceIdOrAlias") String serviceInstanceIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance PUT command");

        // assert that instance exists and belongs to organization
        ServiceInstanceEntry serviceInstance = getServiceInstanceByAlias(organizationIdOrAlias, serviceInstanceIdOrAlias);
        assertCommandContext(commandJSON, serviceInstance.getServiceInstanceId());

        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeServiceInstanceNameAndSummary.class,
                ChangeServiceInstanceCoverage.class,
                AddServiceInstanceEndpoint.class,
                AddServiceInstanceAlias.class,
                RemoveServiceInstanceAlias.class,
                RemoveServiceInstanceEndpoint.class
        );
    }

    @DELETE
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si/{serviceInstanceIdOrAlias}")
    public void serviceInstanceDeleteCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance DELETE command");

        // TODO: we do not test that instance is owned by org!
        assertCommandContext(commandJSON, organizationIdOrAlias);
        sendAndWait(contentType, queryCommandName, commandJSON,
                RemoveServiceInstanceEndpoint.class
        );
    }

    /**
     * Will resolve the organizationId and check that it is mentioned in the command.
     * <p>
     * Security test to make sure that the command is in context of the current organization. It is presumed that only legal users are
     * capable of submitting (POST) to the organization (a concern managed elsewhere)
     * <p>
     * @param commandJSON
     * @param organizationIdOrAlias
     */
    private void assertCommandContext(String commandJSON, String organizationIdOrAlias) {
        
        // FIXME: testing with "string contains" is really not a sufficient test - we MUST check that the command object targets the right
        // object. (in many commands it would be easy to mention the "thingsToMention" in the summary for instance, hence allowing a completely 
        // other target to be affected!!!!

        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        assertCommandMentions(commandJSON, organizationId);
    }

    private void assertCommandMentions(String commandJSON, String... thingsToMention) {
        for (String thingToMention : thingsToMention) {
            if (!commandJSON.contains(thingToMention)) {
                LOG.warn("Command does not contain expected strings {} {}", commandJSON, thingsToMention);
                throw new WebApplicationException("Invalid context", 403);
            }
        }
    }

    private void sendAndWait(String contentType, String queryCommandName, String commandJSON, Class... classes) {
        GenericCommandResource.sendAndWait(contentType, queryCommandName, new CommandRegistry(classes), commandJSON);
    }

    // -------------------------------------------------------
    // -------------------------------------------------------
    // Queries
    // -------------------------------------------------------
    // -------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org")
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
    @Path("org/{organizationId}")
    public OrganizationEntry getOrganization(@PathParam("organizationId") String organizationId) {
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    // ------------------------------------------------------------------------
    // SERVICE SPECIFICATIONS
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/ss")
    public List<ServiceSpecificationEntry> queryServiceSpecificationsByAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return ApplicationServiceRegistry.serviceSpecificationQueryRepository().findByOwnerId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/ss/{serviceSpecificationIdOrAlias}")
    public ServiceSpecificationEntry getServiceSpecificationByAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("serviceSpecificationIdOrAlias") String serviceSpecificationIdOrAlias
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return resolveServiceSpecification(organizationId, serviceSpecificationIdOrAlias);
    }

    // ------------------------------------------------------------------------
    // SERVICE INSTANCES
    // ------------------------------------------------------------------------
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si")
    public List<ServiceInstanceEntry> queryServiceInstancesByAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @QueryParam("namePattern") @DefaultValue("") String usernamePattern
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return ApplicationServiceRegistry.serviceInstanceQueryRepository().findByProviderId(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si/{serviceInstanceIdOrAlias}")
    public ServiceInstanceEntry getServiceInstanceByAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("serviceInstanceIdOrAlias") String serviceInstanceIdOrAlias
    ) {
        // TODO: move to almanac api as list aliases ...or something
        System.out.println("ALL: " + ApplicationServiceRegistry.aliasRegistryQueryRepository().findAll());

        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return resolveServiceInstance(organizationId, serviceInstanceIdOrAlias);
    }

    private static AliasRegistryEntry lookupAlias(String groupId, Class type, String alias) {
        return ApplicationServiceRegistry.aliasRegistryQueryRepository().findByGroupIdAndTypeNameAndAlias(groupId, type.getName(), alias);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si/{serviceInstanceIdOrAlias}/alias")
    public List<AliasRegistryEntry> queryServiceInstanceAliases(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("serviceInstanceIdOrAlias") String serviceInstanceIdOrAlias
    ) {
        return ResourceResolver.queryServiceinstanceAliases(getServiceInstanceByAlias(organizationIdOrAlias, serviceInstanceIdOrAlias));
    }     

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/si/{serviceInstanceIdOrAlias}/alias/{alias}")
    public AliasRegistryEntry getAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("alias") String alias
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return lookupAlias(organizationId, ServiceInstanceId.class, alias);
    }     
    
}
