/* Copyright (c) 2011 Danish Maritime Authority.
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
package net.maritimecloud.portal.application;

import net.maritimecloud.portal.domain.model.security.AuthenticationUtil;
import net.maritimecloud.portal.infrastructure.mail.MailService;
import net.maritimecloud.portal.resource.LogService;
import net.maritimecloud.serviceregistry.domain.service.AliasService;
import net.maritimecloud.serviceregistry.query.AliasRegistryQueryRepository;
import net.maritimecloud.serviceregistry.query.OperationalServiceQueryRepository;
import net.maritimecloud.serviceregistry.query.OrganizationMembershipQueryRepository;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceInstanceQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;

/**
 * @author Christoffer Børrild
 */
public class ApplicationServiceRegistry extends SpringContextBasedRegistry {

    public static IdentityApplicationService identityApplicationService() {
        return (IdentityApplicationService) get("identityApplicationService");
    }

    public static AliasService aliasService() {
        return (AliasService) get("aliasService");
    }

    public static AuthenticationUtil authenticationUtil() {
        return (AuthenticationUtil) get("authenticationUtil");
    }

    public static LogService logService() {
        return (LogService) get("logService");
    }

    public static MailService mailService() {
        return (MailService) get("mailService");
    }

    // ------------------------------------------------------------------------------------------
    // FIXME: should not live in a service registry - move to somewhere else!!!
    // ------------------------------------------------------------------------------------------
    public static CommandGateway commandGateway() {
        return (CommandGateway) get("commandGateway");
    }

    public static OrganizationQueryRepository organizationQueryRepository() {
        return (OrganizationQueryRepository) get("organizationQueryRepository");
    }

    public static OrganizationMembershipQueryRepository organizationMembershipQueryRepository() {
        return (OrganizationMembershipQueryRepository) get("organizationMembershipQueryRepository");
    }

    public static OperationalServiceQueryRepository operationalServiceQueryRepository() {
        return (OperationalServiceQueryRepository) get("operationalServiceQueryRepository");
    }

    public static ServiceSpecificationQueryRepository serviceSpecificationQueryRepository() {
        return (ServiceSpecificationQueryRepository) get("serviceSpecificationQueryRepository");
    }

    public static ServiceInstanceQueryRepository serviceInstanceQueryRepository() {
        return (ServiceInstanceQueryRepository) get("serviceInstanceQueryRepository");
    }

    public static AliasRegistryQueryRepository aliasRegistryQueryRepository() {
        return (AliasRegistryQueryRepository) get("aliasRegistryQueryRepository");
    }

}
