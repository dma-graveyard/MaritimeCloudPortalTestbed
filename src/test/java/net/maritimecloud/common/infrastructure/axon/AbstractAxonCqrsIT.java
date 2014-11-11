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
package net.maritimecloud.common.infrastructure.axon;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import net.maritimecloud.portal.config.ApplicationTestConfig;
import net.maritimecloud.serviceregistry.query.OrganizationQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceInstanceQueryRepository;
import net.maritimecloud.serviceregistry.query.ServiceSpecificationQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Abstract class to extend from to setup Axon integration tests with gateway, busses, repositories and backend persistence
 * <p>
 * @author Christoffer Børrild
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
public abstract class AbstractAxonCqrsIT extends CommonFixture {

    @Resource
    protected CommandGateway commandGateway;
    
    @Resource
    protected OrganizationQueryRepository organizationQueryRepository;

    @Resource
    protected ServiceSpecificationQueryRepository serviceSpecificationQueryRepository;

    @Resource
    protected ServiceInstanceQueryRepository serviceInstanceQueryRepository;

    @Resource
    protected EntityManager entityManager;


    protected CommandGateway commandGateway() {
        return commandGateway;
    }
    
    @Before
    public void setUpParent() {
        organizationQueryRepository.deleteAll();
        serviceSpecificationQueryRepository.deleteAll();
        serviceInstanceQueryRepository.deleteAll();
    }

}
