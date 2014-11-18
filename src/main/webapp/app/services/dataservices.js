'use strict';

// ----------------------------------------------------------------------------
// TEST DATA - INSTANCE EXAMPLE
// ----------------------------------------------------------------------------
var data = demoData();

// --------------------------------------------------
/* Services */
// --------------------------------------------------

var mcpServices = angular.module('mcp.dataservices', ['ngResource'])

    .constant("servicePort", /*"8080"*/ null)
    .factory('serviceBaseUrl', ['$location', 'servicePort',
      function ($location, servicePort) {
        var protocol = $location.protocol();
        var host = $location.host();
        var port = servicePort ? servicePort : $location.port();
        return protocol + "://" + host + ":" + port;
      }])

    .factory('UserService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/users/:username', {}, {
          query: {method: 'GET', params: {username: ''}, isArray: true},
          signUp: {method: 'POST', params: {}, isArray: false},
          activateAccount: {method: 'POST', url: '/rest/users/:username/activate/:activationId', isArray: false},
          isUnique: {method: 'GET', url: '/rest/users/:username/exist', isArray: false}
        });
      }])

    .factory('OrganizationService', ['$resource',
      function ($resource) {
//    return $resource('/rest/organizations/:organizationId', {}, {
//      query: {method: 'GET', params: {organizationId: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });

        console.log("TODO: using mocked organizations data");
        var organizations = data.organizationList;
        /**
         * Helper function to find organization by id
         * @param {type} organizationId
         * @returns the organization or null
         */
        var findOrganization = function (organizationId) {
          for (var i = 0; i < organizations.length; i++) {
            if (organizationId === organizations[i].organizationId)
              return organizations[i];
          }
          console.log("Error. OrganizationId not found! ", organizationId);
          return null;
        };
        return {
          get: function (request) {
            var organizationId = request.organizationId;
            var result = findOrganization(organizationId);
            if (result)
              return result;
          },
          query: function (user) {
            if (user && user.name) {
              var organizationsOfMember = [];
              organizations.forEach(function (organization) {

                if (arrayIndexOf(user.name, organization.members))
                  organizationsOfMember.push(organization);
              });
              return organizationsOfMember;
            }
            return organizations;
          },
          //create: {method: 'POST', params: {}, isArray: false}
          create: function (newOrganizationRequest, success, failure) {

            if (findOrganization(newOrganizationRequest.organizationId)) {
              console.log("An organization with that id already exists");
              failure("An organization with that id already exists");
              return;
            }

            var newOrganization =
                {
                  organizationId: newOrganizationRequest.organizationId,
                  name: newOrganizationRequest.name,
                  summary: newOrganizationRequest.summary,
                  members: ["admin"],
                  teams: [
                    {
                      name: "Owners",
                      description: "Special team of owners. Owners can do just about anything.",
                      isOwner: true,
                      members: ["admin"],
                      accessLevel: "admin"
                    },
                    {
                      name: "Members",
                      description: "Members of the organization with read access",
                      isAdmin: false,
                      members: ["admin"],
                      accessLevel: "read"
                    }
                  ]
                };
            organizations.push(newOrganization);
            console.log("organizations: ", organizations);
            success(newOrganization);
            //return
          }
        };
      }])

    .factory('ServiceInstanceService', ['$resource',
      function ($resource) {
//    return $resource('/rest/serviceInstance/:specificationname', {}, {
//      query: {method: 'GET', params: {specificationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });
        console.log("TODO: using mocked service instance data");
        var serviceInstances = data.serviceInstanceList;

        /**
         * Helper function to find Service Instance by name (to avoid duplicates)
         * @param {type} serviceInstanceName
         */
        var findServiceInstance = function (organizationId, serviceInstanceName) {
          for (var i = 0; i < serviceInstances.length; i++) {
            // FIXME: We should also check that the ID is unique within the organization!!! (defer this to the serverside implementation!)
            if (organizationId === serviceInstances[i].provider.organizationId && serviceInstanceName === serviceInstances[i].name)
              return serviceInstances[i];
          }
          return null;
        };

        var getServiceInstance = function (organizationId, serviceInstanceId) {
          for (var i = 0; i < serviceInstances.length; i++) {
            if (organizationId === serviceInstances[i].provider.organizationId && serviceInstanceId === serviceInstances[i].id) {
              return serviceInstances[i];
            }
          }
          return null;
        };
        return {
          get: function (request) {
            console.log("request: ", request);
            var organizationId = request.organizationId;
            var serviceInstanceId = request.serviceInstanceId;
            var result = getServiceInstance(organizationId, serviceInstanceId);
            if (result) {
              result.$save = function (success, failure) {
                success(result);
              };
              return result;
            }
            else
              console.log("Error. Service Instance with id not found! ", serviceInstanceId);
          },
          query: function (request) {
            if (request && request.organizationId) {
              var specs = [];
              console.log('serviceInstances:', serviceInstances);

              serviceInstances.forEach(function (serviceInstance) {
                if (serviceInstance.provider.organizationId === request.organizationId)
                  specs.push(serviceInstance);
              });
              return specs;
            }
            return serviceInstances;
          },
          //create: {method: 'POST', params: {}, isArray: false}
          create: function (newServiceInstance, success, failure) {

            if (findServiceInstance(newServiceInstance.provider.organizationId, newServiceInstance.name)) {
              console.log("A service instance with that name already exists");
              failure("A service instance with that name already exists");
              return;
            }

            serviceInstances.push(newServiceInstance);
            success(newServiceInstance);
          }

        };
      }])

    .factory('OperationalServiceService', ['$resource',
      function ($resource) {
//    return $resource('/rest/serviceInstance/:specificationname', {}, {
//      query: {method: 'GET', params: {specificationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });
        console.log("TODO: using mocked operational service data");
        return {
          query: function (request) {
            return data.operationalServicesList;
          }
        };
      }])

    .factory('ServiceSpecificationService', ['$resource',
      function ($resource) {
        //    return $resource('/rest/api/specifications/:specificationname', {}, {
        //      query: {method: 'GET', params: {specificationname: ''}, isArray: true},
        //      signUp: {method: 'POST', params: {}, isArray: false}
        //    });

        // mimics GET /rest/api/specifications?serviceSpecificationId='some ssid'

        console.log("TODO: using mocked service specification data");
        return {
          query: function (parameters) {

            var matchingSpecifications = [];

            if (parameters) {

              for (var key in data.serviceSpecifications) {

                if (
                    // no filter?
                    !parameters

                    // filter by organizationId
                    || parameters.organizationId && data.serviceSpecifications[key].ownerId === parameters.organizationId

                    // filter by operational service
                    // does this specification link to the desired operational service?
                    || parameters.operationalServiceId && data.serviceSpecifications[key].operationalServices.indexOf(parameters.operationalServiceId) !== -1

                    )
                {
                  matchingSpecifications.push(data.serviceSpecifications[key]);
                }
              }
            }
            console.log("matchingSpecifications", matchingSpecifications);
            return matchingSpecifications;
          }
        };
      }])
    ;

// ----------------------------------------------------------------------------
// HELPER FUNCTIONS
// ----------------------------------------------------------------------------

var arrayIndexOf = function (value, array) {
  var i = 0;
  array.forEach(function (element) {
    if (element === value)
      i++;
  });
  return i;
};
