    # List all supported commands
    curl http://localhost:8080/rest/api/command

    # Create an Organization
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=CreateOrganizationCommand" -d '{"organizationId":{"identifier":"AN_ORG_ID"},"name":"A_NAME","summary":"A_SUMMARY"}' -X POST

    # Rename organization and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeOrganizationNameAndSummaryCommand" -d '{"organizationId":{"identifier":"AN_ORG_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT

    # Prepare a service specification 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=PrepareServiceSpecificationCommand" -d '{"ownerId":{"identifier":"AN_ORG_ID"}, "serviceSpecificationId":{"identifier":"A_SPEC_ID"}, "name":"A_NAME","summary":"A_SUMMARY fail"}' -X POST

    # Rename service specification and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeServiceSpecificationNameAndSummaryCommand" -d '{"serviceSpecificationId":{"identifier":"A_SPEC_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT

    # Provide a service instance (with no coverage!!!)
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ProvideServiceInstanceCommand" -d '{"providerId":{"identifier":"AN_ORG_ID"},"specificationId":{"identifier":"A_SPEC_ID"},"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"name":"A_NAME","summary":"A_SUMMARY","coverage":null}' -X POST

    # Rename service instance and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeServiceInstanceNameAndSummaryCommand" -d '{"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT



    # dma with admin and Tintin in comment
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=CreateOrganizationCommand" -d '{"organizationId":{"identifier":"dma"},"name":"Danish Maritime Authority","summary":"The Danish Maritime Authority is a government agency of Denmark that regulates maritime affairs. The field of responsibility is based on the shipping industry and its framework conditions, the ship and its crew. In addition, it is responsible for aids to navigation in the waters surrounding Denmark and ashore. admin. Tintin"}' -X POST

    # Provide a service instance (with no coverage!!!)
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ProvideServiceInstanceCommand" -d '{"providerId":{"identifier":"dma"},"specificationId":{"identifier":"A_SPEC_ID"},"serviceInstanceId":{"identifier":"dmaAN_INSTANCE_ID"},"name":"dmaA_NAME","summary":"dmaA_SUMMARY","coverage":null}' -X POST

    # Prepare a service specification 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=PrepareServiceSpecificationCommand" -d '{"ownerId":{"identifier":"dma"}, "serviceSpecificationId":{"identifier":"imo-mis-rest"}, "name":"IMO MIS REST","summary":"A_SUMMARY"}' -X POST
