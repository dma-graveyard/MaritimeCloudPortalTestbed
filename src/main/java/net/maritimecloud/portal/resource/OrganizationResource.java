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
import net.maritimecloud.serviceregistry.command.api.AddOrganizationAlias;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.CreateOrganization;
import net.maritimecloud.serviceregistry.command.api.ProvideServiceInstance;
import net.maritimecloud.serviceregistry.command.api.AddServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.api.ChangeOrganizationWebsiteUrl;
import net.maritimecloud.serviceregistry.command.serviceinstance.ChangeServiceInstanceCoverage;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceInstanceNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.ChangeServiceSpecificationNameAndSummary;
import net.maritimecloud.serviceregistry.command.api.InviteUserToOrganization;
import net.maritimecloud.serviceregistry.command.api.PrepareServiceSpecification;
import net.maritimecloud.serviceregistry.command.api.RemoveOrganizationAlias;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceAlias;
import net.maritimecloud.serviceregistry.command.api.RemoveServiceInstanceEndpoint;
import net.maritimecloud.serviceregistry.command.api.RemoveUserFromOrganization;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;
import net.maritimecloud.serviceregistry.domain.service.AliasGroups;
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
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "organizationId", organizationId);
        sendAndWait(contentType, queryCommandName, commandJSON,
                ChangeOrganizationNameAndSummary.class,
                ChangeOrganizationWebsiteUrl.class,
                AddOrganizationAlias.class,
                RemoveOrganizationAlias.class
        );
    }

    // ------------------------------------------------------------------------
    // SERVICE SPECIFICATIONS
    // ------------------------------------------------------------------------
    @POST
    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/member")
    public void memberPostCommand(
            @HeaderParam("Content-type") String contentType,
            @QueryParam("command") @DefaultValue("") String queryCommandName,
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            String commandJSON
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "organizationId", organizationId);
        sendAndWait(contentType, queryCommandName, commandJSON,
                InviteUserToOrganization.class,
                RemoveUserFromOrganization.class
        );
    }

//    @PUT
//    @Consumes(APPLICATION_JSON_CQRS_COMMAND)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("org/{organizationIdOrAlias}/member/{serviceSpecificationIdOrAlias}")
//    public void memberPutCommand(
//            @HeaderParam("Content-type") String contentType,
//            @QueryParam("command") @DefaultValue("") String queryCommandName,
//            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
//            @PathParam("serviceSpecificationIdOrAlias") String serviceSpecificationIdOrAlias,
//            String commandJSON
//    ) {
//        ServiceSpecificationEntry serviceSpecification = getServiceSpecificationByAlias(organizationIdOrAlias, serviceSpecificationIdOrAlias);
//        commandJSON = overwriteIdentity(commandJSON, "serviceSpecificationId", serviceSpecification.getServiceSpecificationId());
//        sendAndWait(contentType, queryCommandName, commandJSON,
//                ChangeServiceSpecificationNameAndSummary.class
//        );
//    }
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
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "ownerId", organizationId);
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
        ServiceSpecificationEntry serviceSpecification = getServiceSpecificationByAlias(organizationIdOrAlias, serviceSpecificationIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "serviceSpecificationId", serviceSpecification.getServiceSpecificationId());
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
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "providerId", organizationId);
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

        // enrich command with mandatory identifiers (those of the path)
        // ...
        // ( todo: add any missing identifier properties
        //   need to know which based on commandName !?! )
        // rewrite command to contain the identifiers of the path
        //System.out.println("commandJSON: "+commandJSON);
        commandJSON = overwriteIdentity(commandJSON, "serviceInstanceId", serviceInstance.getServiceInstanceId());
        commandJSON = overwriteIdentity(commandJSON, "organizationId", serviceInstance.getProviderId());
        commandJSON = overwriteIdentity(commandJSON, "providerId", serviceInstance.getProviderId());

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
            @PathParam("serviceInstanceIdOrAlias") String serviceInstanceIdOrAlias,
            String commandJSON
    ) {
        LOG.info("Service Instance DELETE command");
        ServiceInstanceEntry serviceInstance = getServiceInstanceByAlias(organizationIdOrAlias, serviceInstanceIdOrAlias);
        commandJSON = overwriteIdentity(commandJSON, "serviceInstanceId", serviceInstance.getServiceInstanceId());
        sendAndWait(contentType, queryCommandName, commandJSON,
                RemoveServiceInstanceEndpoint.class
        );
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
    @Path("org/{organizationIdOrAlias}")
    public OrganizationEntry getOrganization(@PathParam("organizationIdOrAlias") String organizationIdOrAlias) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return ApplicationServiceRegistry.organizationQueryRepository().findOne(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/alias")
    public List<AliasRegistryEntry> queryOrganizationAliases(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return ResourceResolver.queryOrganizationAliases(organizationId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("org/{organizationIdOrAlias}/alias/{alias}")
    public AliasRegistryEntry getOrganizationAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("alias") String alias
    ) {
        final AliasRegistryEntry lookupAlias = lookupAlias(AliasGroups.USERS_AND_ORGANIZATIONS.name(), OrganizationId.class, alias);
        return lookupAlias;
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
    public AliasRegistryEntry getServiceInstanceAlias(
            @PathParam("organizationIdOrAlias") String organizationIdOrAlias,
            @PathParam("alias") String alias
    ) {
        String organizationId = resolveOrganizationIdOrFail(organizationIdOrAlias);
        return lookupAlias(organizationId, ServiceInstanceId.class, alias);
    }

    private String overwriteIdentity(String commandJSON, String propertyName, String value) {
        return JsonCommandHelper.overwriteIdentity(commandJSON, propertyName, value);
    }

}
