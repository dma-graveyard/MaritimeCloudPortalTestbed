'use strict';

// ----------------------------------------------------------------------------
// TEST DATA - INSTANCE EXAMPLE
// ----------------------------------------------------------------------------
//var data = demoData();

// ----------------------------------------------------------------------------
// Remote API Commands
// ----------------------------------------------------------------------------
function CreateOrganizationCommand(organizationId, primaryAlias, name, summary, url) {
  this.organizationId = {identifier: organizationId};
  this.primaryAlias = primaryAlias;
  this.name = name;
  this.summary = summary;
  this.url = url;
}

function ChangeOrganizationNameAndSummaryCommand(organizationId, name, summary) {
  this.organizationId = {identifier: organizationId};
  this.name = name;
  this.summary = summary;
}

function ChangeOrganizationWebsiteUrlCommand(organizationId, url) {
  this.organizationId = {identifier: organizationId};
  this.url = url;
}

function InviteUserToOrganization(organizationId, username) {
  this.organizationId = {identifier: organizationId};
  this.username = username;
}

function AddOrganizationAliasCommand(organizationId, alias) {
  this.organizationId = {identifier: organizationId};
  this.alias = alias;
}

function RemoveOrganizationAliasCommand(organizationId, alias) {
  this.organizationId = {identifier: organizationId};
  this.alias = alias;
}

function ProvideServiceInstanceCommand(providerId, specificationId, serviceInstanceId, name, summary, coverage) {
  this.providerId = {identifier: providerId};
  this.specificationId = {identifier: specificationId};
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.name = name;
  this.summary = summary;
  this.coverage = coverage;
}

function ChangeServiceInstanceNameAndSummaryCommand(serviceInstanceId, name, summary) {
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.name = name;
  this.summary = summary;
}

function ChangeServiceInstanceCoverageCommand(serviceInstanceId, coverage) {
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.coverage = coverage;
}

function AddServiceInstanceAliasCommand(organizationId, serviceInstanceId, alias) {
  this.organizationId = {identifier: organizationId};
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.alias = alias;
}

function RemoveServiceInstanceAliasCommand(organizationId, serviceInstanceId, alias) {
  this.organizationId = {identifier: organizationId};
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.alias = alias;
}

function AddServiceInstanceEndpointCommand(serviceInstanceId, endpointUri) {
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.serviceEndpoint = {uri: endpointUri};
}

function RemoveServiceInstanceEndpointCommand(serviceInstanceId, endpointUri) {
  this.serviceInstanceId = {identifier: serviceInstanceId};
  this.serviceEndpoint = {uri: endpointUri};
}

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

    .factory('UUID', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/uuid');
      }])

    .factory('UserService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/users/:username', {}, {
          query: {method: 'GET', params: {username: ''}, isArray: true},
          signUp: {method: 'POST', params: {}, isArray: false},
          activateAccount: {method: 'POST', url: '/rest/users/:username/activate/:activationId', isArray: false},
          queryOrganizationMeberships: {method: 'GET', url: '/rest/users/:username/orgs', isArray: true},
          isUnique: {method: 'GET', url: '/rest/users/:username/exist', isArray: false}
        });
      }])

    .factory('OrganizationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {

        var resource = $resource(serviceBaseUrl + '/rest/api/org/:organizationId', {}, {
          post: {method: 'POST'},
          put: {method: 'PUT', params: {organizationId: '@organizationId.identifier'}},
          invite: {method: 'POST', params: {},
            url: serviceBaseUrl + '/rest/api/org/:organizationId/member'
          },
          aliases: {method: 'GET', params: {serviceInstanceId: '@organizationId.identifier'},
            url: serviceBaseUrl + '/rest/api/org/:organizationId/alias',
            isArray: true
          },
          alias: {method: 'GET', params: {},
            url: serviceBaseUrl + '/rest/api/org/exist/alias/:alias'
          }          
        });

        resource.create = function (organization, succes, error) {
          return this.post(new CreateOrganizationCommand(organization.organizationId, organization.primaryAlias, organization.name, organization.summary, organization.url), succes, error);
        };

        resource.changeNameAndSummary = function (organization, succes, error) {
          return this.put(new ChangeOrganizationNameAndSummaryCommand(organization.organizationId, organization.name, organization.summary), succes, error);
        };

        resource.changeWebsiteUrl = function (organization, succes, error) {
          return this.put(new ChangeOrganizationWebsiteUrlCommand(organization.organizationId, organization.url), succes, error);
        };

        resource.InviteUserToOrganization = function (organization, username, succes, error) {
          return this.invite({organizationId: organization.organizationId}, new InviteUserToOrganization(organization.organizationId, username), succes, error);
        };

        resource.addAlias = function (organization, alias, succes, error) {
          return this.put(new AddOrganizationAliasCommand(organization.organizationId, alias), succes, error);
        };

        resource.removeAlias = function (organization, alias, succes, error) {
          return this.put(new RemoveOrganizationAliasCommand(organization.organizationId, alias), succes, error);
        };

        return resource;
      }])

    .factory('AlmanacOrganizationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/organization/:organizationId');
      }])

    .factory('AlmanacOrganizationMemberService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/organization/:organizationId/member');
      }])

    .factory('AlmanacOperationalServiceService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/operational-service/:operationalServiceId');
      }])

    .factory('AlmanacServiceSpecificationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/service-specification/:serviceSpecificationId');
      }])

    .factory('AlmanacServiceInstanceService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/service-instance/:serviceInstanceId', {}, {});
      }])

    .factory('OperationalServiceService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/organization/:organizationId/operational-service/:operationalServiceId');
      }])

    .factory('ServiceSpecificationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/org/:organizationId/ss/:serviceSpecificationId');
      }])

    .factory('ServiceInstanceService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {

        var resource = $resource(serviceBaseUrl + '/rest/api/org/:organizationId/si/:serviceInstanceId', {},
            {
              post: {method: 'POST', params: {organizationId: '@providerId.identifier'}},
              put: {method: 'PUT', params: {serviceInstanceId: '@serviceInstanceId.identifier'}},
              aliases: {method: 'GET', params: {serviceInstanceId: '@serviceInstanceId.identifier'},
                url: serviceBaseUrl + '/rest/api/org/:organizationId/si/:serviceInstanceId/alias',
                isArray: true
              },
              alias: {method: 'GET', params: {},
                url: serviceBaseUrl + '/rest/api/org/:organizationId/si/exist/alias/:alias'
              }
            });

        resource.create = function (serviceInstance, succes, error) {
          return this.post(new ProvideServiceInstanceCommand(serviceInstance.providerId, serviceInstance.specificationId, serviceInstance.serviceInstanceId, serviceInstance.name, serviceInstance.summary, serviceInstance.coverage), succes, error);
        };

        resource.changeNameAndSummary = function (serviceInstance, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new ChangeServiceInstanceNameAndSummaryCommand(serviceInstance.serviceInstanceId, serviceInstance.name, serviceInstance.summary), succes, error);
        };

        resource.changeCoverage = function (serviceInstance, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new ChangeServiceInstanceCoverageCommand(serviceInstance.serviceInstanceId, serviceInstance.coverage), succes, error);
        };

        resource.addAlias = function (serviceInstance, alias, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new AddServiceInstanceAliasCommand(serviceInstance.providerId, serviceInstance.serviceInstanceId, alias), succes, error);
        };

        resource.removeAlias = function (serviceInstance, alias, succes, error) {
          return this.put({organizationId: serviceInstance.providerId, serviceInstanceId: serviceInstance.serviceInstanceId}, new RemoveServiceInstanceAliasCommand(serviceInstance.providerId, serviceInstance.serviceInstanceId, alias), succes, error);
        };

        resource.addEndpoint = function (serviceInstance, endpointUri, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new AddServiceInstanceEndpointCommand(serviceInstance.serviceInstanceId, endpointUri), succes, error);
        };

        resource.removeEndpoint = function (serviceInstance, endpointUri, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new RemoveServiceInstanceEndpointCommand(serviceInstance.serviceInstanceId, endpointUri), succes, error);
        };

        return resource;
      }]);
