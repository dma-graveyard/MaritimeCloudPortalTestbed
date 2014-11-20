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

import java.io.File;
import net.maritimecloud.portal.domain.infrastructure.axon.ReplayableFileSystemEventStore;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import net.maritimecloud.serviceregistry.command.organization.Organization;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.replay.BackloggingIncomingMessageHandler;
import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.axonframework.eventstore.management.EventStoreManagement;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Christoffer Børrild
 */
@Configuration
@ComponentScan(basePackages = "net.maritimecloud.serviceregistry")
@AnnotationDriven
public class AxonConfig {

    @Bean
    public CommandBus commandBus() {
        return new SimpleCommandBus();
    }

    @Bean
    public EventBus eventBus() {
//        return new SimpleEventBus();
        return new ClusteringEventBus(new DefaultClusterSelector(simpleCluster()));
    }

    @Bean
    public EventStore eventStore() {
//        return new FileSystemEventStore(new SimpleEventFileResolver(new File("./target/events")));
        return new ReplayableFileSystemEventStore(new File("./target/events"));
    }

    @Bean
    public SimpleCluster simpleCluster() {
        return new SimpleCluster("defaultCluster");
    }

    @Bean
    public SpringTransactionManager springTransactionManager(PlatformTransactionManager platformTransactionManager) {
        return new SpringTransactionManager(platformTransactionManager);
    }

    @Bean
    public ReplayingCluster replayingCluster(PlatformTransactionManager platformTransactionManager) {
        return new ReplayingCluster(simpleCluster(), (EventStoreManagement) eventStore(), springTransactionManager(platformTransactionManager), 10, backloggingIncomingMessageHandler());
    }

    @Bean
    public BackloggingIncomingMessageHandler backloggingIncomingMessageHandler() {
        return new BackloggingIncomingMessageHandler();
    }

    @Bean
    public CommandGatewayFactoryBean commandGateway() {
        CommandGatewayFactoryBean factory = new CommandGatewayFactoryBean();
        factory.setCommandBus(commandBus());
        return factory;
    }

//    @Bean
//    AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor() {
//        return new AnnotationCommandHandlerBeanPostProcessor();
//    }
//
//    @Bean
//    AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor() {
//        return new AnnotationEventListenerBeanPostProcessor();
//    }
    @Bean
    public Repository<Organization> organizationRepository() {
        EventSourcingRepository repository = new EventSourcingRepository<>(Organization.class, eventStore());
        repository.setEventBus(eventBus());
        return repository;
    }

    @Bean
    public Repository<ServiceSpecification> serviceSpecificationRepository() {
        EventSourcingRepository repository = new EventSourcingRepository<>(ServiceSpecification.class, eventStore());
        repository.setEventBus(eventBus());
        return repository;
    }

    @Bean
    public Repository<ServiceInstance> serviceInstanceRepository() {
        EventSourcingRepository repository = new EventSourcingRepository<>(ServiceInstance.class, eventStore());
        repository.setEventBus(eventBus());
        return repository;
    }

    // ------------------------------------------------------------------------
    // Currently Axon does not have spring support for discovering 
    // command handlers on Aggregates, so we have to subscribe them 
    // manually:
    // ------------------------------------------------------------------------ 
    @Bean
    public AggregateAnnotationCommandHandler<Organization> organizationAggregateCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(Organization.class, organizationRepository(), commandBus());
    }

    @Bean
    public AggregateAnnotationCommandHandler<ServiceSpecification> serviceSpecificationAggregateCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(ServiceSpecification.class, serviceSpecificationRepository(), commandBus());
    }

    @Bean
    public AggregateAnnotationCommandHandler<ServiceInstance> serviceInstanceAggregateCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(ServiceInstance.class, serviceInstanceRepository(), commandBus());
    }

}
