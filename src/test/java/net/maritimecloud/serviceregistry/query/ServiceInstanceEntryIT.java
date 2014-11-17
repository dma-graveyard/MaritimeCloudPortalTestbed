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
package net.maritimecloud.serviceregistry.query;

import javax.transaction.Transactional;
import net.maritimecloud.common.infrastructure.axon.AbstractAxonCqrsIT;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christoffer Børrild
 */
public class ServiceInstanceEntryIT extends AbstractAxonCqrsIT {
    
    @Test
    @Transactional
    public void changeNameAndSummaryEntry() {
        
        ServiceInstanceEntry entry = new ServiceInstanceEntry();
        
        entry.setServiceInstanceIdentifier(AN_INSTANCE_ID);
        entry.setName(A_NAME);
        entry.setSummary(A_SUMMARY);
        serviceInstanceQueryRepository.save(entry);
        entityManager.flush();

        final ServiceInstanceEntry instance = serviceInstanceQueryRepository.findOne(AN_INSTANCE_ID);
        instance.setName(ANOTHER_NAME);
        instance.setSummary(ANOTHER_SUMMARY);
        serviceInstanceQueryRepository.save(instance);
        entityManager.flush();
        
        final ServiceInstanceEntry instance2 = serviceInstanceQueryRepository.findOne(AN_INSTANCE_ID);
        assertEquals(ANOTHER_NAME, instance2.getName());
        assertEquals(ANOTHER_SUMMARY, instance2.getSummary());
    }
    
}
