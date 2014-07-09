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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Christoffer Børrild
 */
public class ApplicationServiceRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static IdentityApplicationService identityApplicationService() {
        return (IdentityApplicationService) getResource("identityApplicationService");
    }

    public static AuthenticationUtil authenticationUtil() {
        return (AuthenticationUtil) getResource("authenticationUtil");
    }

    public static LogService logService() {
        return (LogService) getResource("logService");
    }

    private static Object getResource(String resourceName) throws BeansException {
        return getApplicationContext().getBean(resourceName);
    }

    private static ApplicationContext getApplicationContext() {
        if (ApplicationServiceRegistry.applicationContext == null) {
            throw new IllegalStateException("No applicationContext has been injected!?!");
        }
        return applicationContext;
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext anApplicationContext) throws BeansException {
        if (ApplicationServiceRegistry.applicationContext == null) {
            ApplicationServiceRegistry.applicationContext = anApplicationContext;
        }
    }

}
