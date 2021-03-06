// This code was generated by net.maritimecloud.common.cqrs.contract.SourceGenerator
// Generated Code is based on the contract defined in net.maritimecloud.serviceregistry.command.ServiceRegistryContract
// Please modify the contract instead of this file!
package net.maritimecloud.serviceregistry.command.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.common.cqrs.Command;
import net.maritimecloud.serviceregistry.command.organization.OrganizationId;

/**
 * GENERATED CLASS!
 * @see net.maritimecloud.serviceregistry.command.ServiceRegistryContract#changeOrganizationNameAndSummary
 */
public class ChangeOrganizationNameAndSummary implements Command {

    @TargetAggregateIdentifier
    private final OrganizationId organizationId;
    private final String name;
    private final String summary;

    @JsonCreator
    public ChangeOrganizationNameAndSummary(
            @JsonProperty("organizationId") OrganizationId organizationId,
            @JsonProperty("name") String name,
            @JsonProperty("summary") String summary
    ) {
        Assert.notNull(organizationId, "The organizationId must be provided");
        Assert.notNull(name, "The name must be provided");
        Assert.notNull(summary, "The summary must be provided");
        this.organizationId = organizationId;
        this.name = name;
        this.summary = summary;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

}

