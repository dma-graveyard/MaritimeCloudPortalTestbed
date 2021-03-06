// This code was generated by net.maritimecloud.common.cqrs.contract.SourceGenerator
// Generated Code is based on the contract defined in net.maritimecloud.identityregistry.command.IdentityRegistryContract
// Please modify the contract instead of this file!
package net.maritimecloud.identityregistry.command.api;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.axonframework.common.Assert;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.maritimecloud.common.cqrs.Command;
import net.maritimecloud.identityregistry.command.user.UserId;

/**
 * GENERATED CLASS!
 * @see net.maritimecloud.identityregistry.command.IdentityRegistryContract#ChangeUserEmailAddress
 */
public class ChangeUserEmailAddress implements Command {

    @TargetAggregateIdentifier
    private final UserId userId;
    private final String emailAddress;

    @JsonCreator
    public ChangeUserEmailAddress(
            @JsonProperty("userId") UserId userId,
            @JsonProperty("emailAddress") String emailAddress
    ) {
        Assert.notNull(userId, "The userId must be provided");
        Assert.notNull(emailAddress, "The emailAddress must be provided");
        this.userId = userId;
        this.emailAddress = emailAddress;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

}

