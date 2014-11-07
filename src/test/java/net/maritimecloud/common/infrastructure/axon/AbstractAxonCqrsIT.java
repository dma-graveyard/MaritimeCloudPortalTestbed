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

import java.io.File;
import net.maritimecloud.portal.config.ApplicationTestConfig;
import net.maritimecloud.serviceregistry.command.organization.OrganizationCommandHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerAdapter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Abstract class to extend from to setup Axon integration tests with gateway, busses, repositories and (JPA?) backed persistence  
 * @author Christoffer Børrild
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
public abstract class AbstractAxonCqrsIT {

    static protected EventStore eventStore;
    static protected EventBus eventBus;
    static protected CommandGateway commandGateway;
    static protected CommandBus commandBus;

    @BeforeClass
    public static void setUpSuperClass() {
        // let's start with the Command Bus
        commandBus = new SimpleCommandBus();

        // the CommandGateway provides a friendlier API
        commandGateway = new DefaultCommandGateway(commandBus);

        // we'll store Events on the FileSystem, in the "events/" folder
        //EventStore eventStore = new FileSystemEventStore(new JacksonSerializer(), new SimpleEventFileResolver(new File("./events")));
        eventStore = new FileSystemEventStore(new SimpleEventFileResolver(new File("./target/events")));

        // a Simple Event Bus will do
        eventBus = new SimpleEventBus();
    }
    
    /**
     *  Subclasses should call this method in order to setup repositories and command handlers etc...
     * @param <T>
     * @param aggregateType The aggregate type to prepare for
     */
    protected static <T extends EventSourcedAggregateRoot> EventSourcingRepository<T> subscribe(Class<T> aggregateType){
        // we need to configure the repository
        EventSourcingRepository<T> eventSourcingRepository = new EventSourcingRepository<>(aggregateType, eventStore);
        eventSourcingRepository.setEventBus(eventBus);

        // Axon needs to know that our aggregate can handle commands
        AggregateAnnotationCommandHandler.subscribe(aggregateType, eventSourcingRepository, commandBus);
        return eventSourcingRepository;
    }

    /**
     *  Subclasses should call this method in order to setup command handlers (except aggregates)
     * @param commandHandler The command handler to subscribe to the commandBus. Must be initialized with repositories etc!
     */
    protected static void subscribeHandler(Object commandHandler){
        AnnotationCommandHandlerAdapter.subscribe(commandHandler, commandBus);
    }

    /**
     *  Subclasses should call this method in order to setup event listeners
     * @param eventListener The Listener type to subscribe
     */
    protected static void subscribeListener(Object eventListener){
        AnnotationEventListenerAdapter.subscribe(eventListener, eventBus);
    }

    @AfterClass
    public static void tearDownClass() {
        // TODO:
        // cleanup files
    }

}
