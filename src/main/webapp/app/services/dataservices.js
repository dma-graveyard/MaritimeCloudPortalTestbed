'use strict';

// ----------------------------------------------------------------------------
// TEST DATA - INSTANCE EXAMPLE
// ----------------------------------------------------------------------------
//var data = demoData();

// ----------------------------------------------------------------------------
// Remote API Commands
// ----------------------------------------------------------------------------
function CreateOrganizationCommand(organizationId, name, summary, url) {
  this.organizationId = {identifier: organizationId};
  this.name = name;
  this.summary = summary;
  this.url = url;
}

function ChangeOrganizationNameAndSummaryCommand(organizationId, name, summary) {
  this.organizationId = {identifier: organizationId};
  this.name = name;
  this.summary = summary;
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
          isUnique: {method: 'GET', url: '/rest/users/:username/exist', isArray: false}
        });
      }])

    .factory('OrganizationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {

        var resource = $resource(serviceBaseUrl + '/rest/api/org/:organizationId', {}, {
          post: {method: 'POST'},
          put: {method: 'PUT', params: {organizationId: '@organizationId.identifier'}},
        });

        resource.create = function (organization, succes, error) {
          return this.post(new CreateOrganizationCommand(organization.organizationId, organization.name, organization.summary, organization.url), succes, error);
        };
        
        resource.changeNameAndSummary = function (organization, succes, error) {
          return this.put(new ChangeOrganizationNameAndSummaryCommand(organization.organizationId, organization.name, organization.summary), succes, error);
        };

        return resource;
      }])

    .factory('AlmanacOrganizationService', ['$resource', 'serviceBaseUrl',
      function ($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/api/almanac/organization/:organizationId');
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
          return this.put({organizationId: serviceInstance.providerId, serviceInstanceId:serviceInstance.serviceInstanceId}, new RemoveServiceInstanceAliasCommand(serviceInstance.providerId, serviceInstance.serviceInstanceId, alias), succes, error);
        };

        resource.addEndpoint = function (serviceInstance, endpointUri, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new AddServiceInstanceEndpointCommand(serviceInstance.serviceInstanceId, endpointUri), succes, error);
        };

        resource.removeEndpoint = function (serviceInstance, endpointUri, succes, error) {
          return this.put({organizationId: serviceInstance.providerId}, new RemoveServiceInstanceEndpointCommand(serviceInstance.serviceInstanceId, endpointUri), succes, error);
        };

        return resource;
      }]);
