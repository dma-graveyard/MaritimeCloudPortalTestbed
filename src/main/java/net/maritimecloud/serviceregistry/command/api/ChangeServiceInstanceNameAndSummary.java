// This code was generated by net.maritimecloud.common.cqrs.contract.SourceGenerator
// Generated Code is based on the contract defined in net.maritimecloud.serviceregistry.command.ServiceRegistryContract
// Please modify the contract instead of this file!
package net.maritimecloud.serviceregistry.command.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.common.cqrs.Command;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstanceId;

/**
 * GENERATED CLASS!
 * @see net.maritimecloud.serviceregistry.command.ServiceRegistryContract#changeServiceInstanceNameAndSummary
 */
public class ChangeServiceInstanceNameAndSummary implements Command {

    @TargetAggregateIdentifier
    private final ServiceInstanceId serviceInstanceId;
    private final String name;
    private final String summary;

    @JsonCreator
    public ChangeServiceInstanceNameAndSummary(
            @JsonProperty("serviceInstanceId") ServiceInstanceId serviceInstanceId,
            @JsonProperty("name") String name,
            @JsonProperty("summary") String summary
    ) {
        Assert.notNull(serviceInstanceId, "The serviceInstanceId must be provided");
        Assert.notNull(name, "The name must be provided");
        Assert.notNull(summary, "The summary must be provided");
        this.serviceInstanceId = serviceInstanceId;
        this.name = name;
        this.summary = summary;
    }

    public ServiceInstanceId getServiceInstanceId() {
        return serviceInstanceId;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

}

