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
package net.maritimecloud.portal.application;

import net.maritimecloud.portal.config.ApplicationTestConfig;
import net.maritimecloud.portal.infrastructure.persistence.CleanableStore;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Base class for sharing of convenient setup and teardown as well as builders for commonly used scenarios.
 * <p>
 * Use this class as base class for your tests of e.g. resources and services.
 * <p>
 * @author Christoffer Børrild
 */
public class ApplicationServiceTest {

    protected ApplicationContext applicationContext;

    /**
     * Factory method that creates a new Application Context. Override this method to provide your own application context
     * <p>
     * @return the created application context
     */
    protected ApplicationContext createApplicationContext() {
        return new AnnotationConfigApplicationContext(ApplicationTestConfig.class);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Before
    public void setUp() {
        System.out.println(">>>>>>>>>>>>>>>>>>>> application service " + getClass().getTypeName());
        setApplicationContext(createApplicationContext());
        cleanAllRepositories();
    }

    private void cleanAllRepositories() {
        //eventStore = (EventStore) applicationContext.getBean("eventStore");
        //clean(eventStore);
        //clean(DomainRegistry.groupRepository());
    }

    private void clean(Object cleanableStore) {
        ((CleanableStore) cleanableStore).clean();
    }

    @After
    public void tearDown() {
        cleanAllRepositories();
    }

}
