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
package net.maritimecloud.portal.config;

import net.maritimecloud.portal.application.SpringContextBasedRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Christoffer Børrild
 */
public class ApplicationContextSetup implements ApplicationContextAware {

    boolean runnedOnce = false;

    @Override
    public synchronized void setApplicationContext(ApplicationContext anApplicationContext) throws BeansException {

        if (!runnedOnce) {
            System.out.println("setApplicationContext: " + anApplicationContext);

            // Set application context on all our registries once and for all
            new SpringContextBasedRegistry().setApplicationContext(anApplicationContext);
        }
    }

}
