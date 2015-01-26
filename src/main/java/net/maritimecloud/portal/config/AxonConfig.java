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

import net.maritimecloud.portal.domain.infrastructure.axon.ShiroAuditDataProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.maritimecloud.identityregistry.command.user.User;
import net.maritimecloud.portal.domain.infrastructure.axon.NoReplayedEvents;
import net.maritimecloud.portal.domain.infrastructure.axon.ReplayableFileSystemEventStore;
import net.maritimecloud.serviceregistry.command.organization.AttachOrganizationAliasSaga;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import net.maritimecloud.serviceregistry.command.organization.Organization;
import net.maritimecloud.serviceregistry.command.organization.SetupOrganizationOwnerMemberSaga;
import net.maritimecloud.serviceregistry.command.organization.membership.Membership;
import net.maritimecloud.serviceregistry.command.serviceinstance.ServiceInstance;
import net.maritimecloud.serviceregistry.command.servicespecification.ServiceSpecification;
import org.axonframework.auditing.AuditDataProvider;
import org.axonframework.auditing.AuditingInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.eventhandling.AnnotationClusterSelector;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.CompositeClusterSelector;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.replay.BackloggingIncomingMessageHandler;
import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.axonframework.eventstore.management.EventStoreManagement;
import org.axonframework.saga.GenericSagaFactory;
import org.axonframework.saga.ResourceInjector;
import org.axonframework.saga.SagaFactory;
import org.axonframework.saga.SagaManager;
import org.axonframework.saga.SagaRepository;
import org.axonframework.saga.annotation.AnnotatedSagaManager;
import org.axonframework.saga.repository.inmemory.InMemorySagaRepository;
import org.axonframework.saga.spring.SpringResourceInjector;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Christoffer Børrild
 */
@Configuration
@ComponentScan(basePackages = {"net.maritimecloud.serviceregistry", "net.maritimecloud.identityregistry"})
@AnnotationDriven
public class AxonConfig {

    @Bean
    public CommandBus commandBus() {
        final SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setHandlerInterceptors(commandHandlerInterceptors());
        return commandBus;
    }

    private List<CommandHandlerInterceptor> commandHandlerInterceptors() {
        List<CommandHandlerInterceptor> commandHandlerInterceptors = new ArrayList<>();
        commandHandlerInterceptors.add(auditingInterceptor());
        return commandHandlerInterceptors;
    }

    @Bean
    public AuditingInterceptor auditingInterceptor() {
        final AuditingInterceptor auditingInterceptor = new AuditingInterceptor();

        // Attach user info to all events:
        auditingInterceptor.setAuditDataProvider(auditDataProvider());

        return auditingInterceptor;
    }
    
    @Bean
    public AuditDataProvider auditDataProvider() {
        return new ShiroAuditDataProvider();
    }

    @Bean
    public EventBus eventBus() {
//        return new SimpleEventBus();
        List<ClusterSelector> clusterSelectors = new ArrayList<>();
        clusterSelectors.add(new AnnotationClusterSelector(NoReplayedEvents.class, sagaCluster()));
        clusterSelectors.add(new DefaultClusterSelector(simpleCluster()));

        final ClusteringEventBus eventBus = new ClusteringEventBus(new CompositeClusterSelector(clusterSelectors));

        eventBus.subscribe(sagaManager());

        return eventBus;
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
    public SimpleCluster sagaCluster() {
        return new SimpleCluster("sagaCluster");
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

    @Bean
    public SagaRepository sagaRepository() {
        return new InMemorySagaRepository();
    }

    @Bean
    public ResourceInjector resourceInjector() {
        return new SpringResourceInjector();
    }

    @Bean
    public SagaFactory sagaFactory() {
        GenericSagaFactory sagaFactory = new GenericSagaFactory();
        sagaFactory.setResourceInjector(resourceInjector());
        return sagaFactory;
    }

    @Bean
    public SagaManager sagaManager() {
        AnnotatedSagaManager sagaManager = new AnnotatedSagaManager(sagaRepository(), sagaFactory(),
                AttachOrganizationAliasSaga.class,
                SetupOrganizationOwnerMemberSaga.class
        );
        return sagaManager;
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
    public Repository<User> userAggregateRepository() {
        EventSourcingRepository repository = new EventSourcingRepository<>(User.class, eventStore());
        repository.setEventBus(eventBus());
        return repository;
    }

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

    @Bean
    public Repository<Membership> organizationMembershipRepository() {
        EventSourcingRepository repository = new EventSourcingRepository<>(Membership.class, eventStore());
        repository.setEventBus(eventBus());
        return repository;
    }

    // ------------------------------------------------------------------------
    // Currently Axon does not have spring support for discovering 
    // command handlers on Aggregates, so we have to subscribe them 
    // manually:
    // ------------------------------------------------------------------------ 
    @Bean
    public AggregateAnnotationCommandHandler<User> userAggregateCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(User.class, userAggregateRepository(), commandBus());
    }

    @Bean
    public AggregateAnnotationCommandHandler<Organization> organizationAggregateCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(Organization.class, organizationRepository(), commandBus());
    }

    @Bean
    public AggregateAnnotationCommandHandler<Membership> organizationMembershipCommandHandler() {
        return AggregateAnnotationCommandHandler.subscribe(Membership.class, organizationMembershipRepository(), commandBus());
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
