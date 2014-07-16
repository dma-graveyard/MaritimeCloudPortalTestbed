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
import net.maritimecloud.portal.resource.LogService;

/**
 * @author Christoffer Børrild
 */
public class ApplicationServiceRegistry extends SpringContextBasedRegistry {


    public static IdentityApplicationService identityApplicationService() {
        return (IdentityApplicationService) get("identityApplicationService");
    }

    public static AuthenticationUtil authenticationUtil() {
        return (AuthenticationUtil) get("authenticationUtil");
    }

    public static LogService logService() {
        return (LogService) get("logService");
    }

//    private static ApplicationContext applicationContext;
//    protected static Object get(String resourceName) throws BeansException {
//        return getApplicationContext().getBean(resourceName);
//    }
//    private static ApplicationContext getApplicationContext() {
//        if (ApplicationServiceRegistry.applicationContext == null) {
//            throw new IllegalStateException("No applicationContext has been injected!?!");
//        }
//        return applicationContext;
//    }


}
