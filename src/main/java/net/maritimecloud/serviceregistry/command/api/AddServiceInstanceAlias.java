// This code was generated by net.maritimecloud.cqrs.tool.SourceGenerator
// Generated Code is based on the contract defined in net.maritimecloud.serviceregistry.command.ServiceRegistryContract
// Please modify the contract instead of this file!
package net.maritimecloud.serviceregistry.command.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.serviceregistry.command.Command;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;

/**
 * GENERATED CLASS!
 * @see net.maritimecloud.serviceregistry.command.ServiceRegistryContract#addServiceInstanceAlias
 */
public class AddServiceInstanceAlias implements Command {

    @TargetAggregateIdentifier
    private final OrganizationId organizationId;
    private final ServiceInstanceId serviceInstanceId;
    private final String alias;

    @JsonCreator
    public AddServiceInstanceAlias(
            @JsonProperty("organizationId") OrganizationId organizationId,
            @JsonProperty("serviceInstanceId") ServiceInstanceId serviceInstanceId,
            @JsonProperty("alias") String alias
    ) {
        Assert.notNull(organizationId, "The organizationId must be provided");
        Assert.notNull(serviceInstanceId, "The serviceInstanceId must be provided");
        Assert.notNull(alias, "The alias must be provided");
        this.organizationId = organizationId;
        this.serviceInstanceId = serviceInstanceId;
        this.alias = alias;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public ServiceInstanceId getServiceInstanceId() {
        return serviceInstanceId;
    }

    public String getAlias() {
        return alias;
    }

}

