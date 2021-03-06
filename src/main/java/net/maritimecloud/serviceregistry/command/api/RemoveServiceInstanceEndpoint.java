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
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceEndpoint;

/**
 * GENERATED CLASS!
 * @see net.maritimecloud.serviceregistry.command.ServiceRegistryContract#removeServiceInstanceEndpoint
 */
public class RemoveServiceInstanceEndpoint implements Command {

    @TargetAggregateIdentifier
    private final ServiceInstanceId serviceInstanceId;
    private final ServiceEndpoint serviceEndpoint;

    @JsonCreator
    public RemoveServiceInstanceEndpoint(
            @JsonProperty("serviceInstanceId") ServiceInstanceId serviceInstanceId,
            @JsonProperty("serviceEndpoint") ServiceEndpoint serviceEndpoint
    ) {
        Assert.notNull(serviceInstanceId, "The serviceInstanceId must be provided");
        Assert.notNull(serviceEndpoint, "The serviceEndpoint must be provided");
        this.serviceInstanceId = serviceInstanceId;
        this.serviceEndpoint = serviceEndpoint;
    }

    public ServiceInstanceId getServiceInstanceId() {
        return serviceInstanceId;
    }

    public ServiceEndpoint getServiceEndpoint() {
        return serviceEndpoint;
    }

}

