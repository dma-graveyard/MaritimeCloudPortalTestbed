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
package net.maritimecloud.portal.domain.infrastructure.shiro.axon;

import net.maritimecloud.portal.domain.infrastructure.axon.ReplayableFileSystemEventStore;
import java.io.File;
import java.io.IOException;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventstore.EventVisitor;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christoffer Børrild
 */
public class ReplayableFileSystemEventStoreTest {

    public ReplayableFileSystemEventStoreTest() {
    }

    @Before
    public void setUp() {
    }

//    @Test
//    public void test1SomeMethod() throws IOException {
//        
//        File baseDir = new File("./target/events");
//        System.out.println(""+baseDir.getAbsolutePath());
//        SimpleEventFileResolver simpleEventFileResolver = new SimpleEventFileResolver(baseDir);
//        assertTrue("expected true ", simpleEventFileResolver.eventFileExists("Organization", "OrganizationId [id=ælkjælkjælkjælkj]"));
//    }

    @Test
    public void testReplay() {
        File baseDir = new File("./target/events");
        
        ReplayableFileSystemEventStore store = new ReplayableFileSystemEventStore(baseDir);
        
        store.visitEvents((DomainEventMessage domainEvent) -> {
            System.out.println(domainEvent.getTimestamp() + ", Type: " + domainEvent.getPayloadType() + " ID:" + domainEvent.getAggregateIdentifier() + ", Seq:" + domainEvent.getSequenceNumber());
        });
    }

}
