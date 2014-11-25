Technical notes
===============

# Command Query Responsibility Segregation (CQRS)

## Extending CQRS with a new Command

1. Create a Command value object class with JSON support and getters
2. Add a @CommandHandler to the domain object or a handler object:

    @CommandHandler
    public void handle(ChangeServiceInstanceCoverageCommand command) {
        apply(new ServiceInstanceCoverageChangedEvent(command.getServiceInstanceId(), command.getCoverage()));
    }

3. Create a corresponding event:

    public class ServiceInstanceCoverageChangedEvent {

        @TargetAggregateIdentifier
        private final ServiceInstanceId serviceInstanceId;
        private final Coverage coverage;

        public ServiceInstanceCoverageChangedEvent(ServiceInstanceId serviceInstanceId, Coverage coverage) {
            this.serviceInstanceId = serviceInstanceId;
            this.coverage = coverage;
        }

        public ServiceInstanceId getServiceInstanceId() { ... }
        public Coverage getCoverage() { ... }

    }

4. Add an event handler to the query event listener

    @EventHandler
    public void on(ServiceInstanceCoverageChangedEvent event) {
        ServiceInstanceEntry entry = getInstanceWith(event.getServiceInstanceId());
        entry.setCoverage(event.getCoverage());
        save(entry);
    }

5. Expose the command in various REST Resources. E.g. add it to the list of POST or PUT commands of the appropriate Resource.

6. Add CURL example to the readme.md of using the command:

    

7. TEST: (optional) Check that the command is serializable to JSON format by adding a test in GenericCommandResourceTest:

    @Test
    public void jsonChangeServiceInstanceCoverageCommand() throws Exception {

        ChangeServiceInstanceCoverageCommand command
                = serializeAndDeserializeCommand(
                        new ChangeServiceInstanceCoverageCommand(
                                new ServiceInstanceId(AN_INSTANCE_ID),
                                A_COVERAGE
                        )
                );

        assertEquals(AN_INSTANCE_ID, command.getServiceInstanceId().identifier());
        assertEquals(A_COVERAGE, command.getCoverage());
    }

 
