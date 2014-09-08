'use strict';

/* Services */

var mcpServices = angular.module('mcp.dataservices', ['ngResource'])

    .constant("servicePort", /*"8080"*/ null)
    .factory('serviceBaseUrl', ['$location', 'servicePort',
      function($location, servicePort) {
        var protocol = $location.protocol();
        var host = $location.host();
        var port = servicePort ? servicePort : $location.port();
        return protocol + "://" + host + ":" + port;
      }])

    .factory('UserService', ['$resource', 'serviceBaseUrl',
      function($resource, serviceBaseUrl) {
        return $resource(serviceBaseUrl + '/rest/users/:username', {}, {
          query: {method: 'GET', params: {username: ''}, isArray: true},
          signUp: {method: 'POST', params: {}, isArray: false},
          activateAccount: {method: 'POST', url: '/rest/users/:username/activate/:activationId', isArray: false},
          isUnique: {method: 'GET', url: '/rest/users/:username/exist', isArray: false}
        });
      }])

    .factory('OrganizationService', ['$resource',
      function($resource) {
//    return $resource('/rest/organizations/:organizationname', {}, {
//      query: {method: 'GET', params: {organizationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });

        console.log("TODO: using mocked organizations data");

        var organizations = [
          {// details: dmi
            name: "dmi",
            title: "Danish Meteoroligical Institute",
            description: "DMI provides meteorological services in the Commonwealth of the Realm of Denmark, the Faroe Islands, Greenland, and surrounding waters and airspace. Meteorological services include forecasting and warnings and monitoring of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
            members: ["admin", "Tintin", "Haddock"],
            teams: [
              {
                name: "Owners",
                description: "Special team of owners. Owners can do just about anything.",
                isOwner: true,
                members: ["admin"],
                accessLevel: "admin"
              },
              {
                name: "DMI service producers",
                description: "DMI personel that produces info for our various services",
                isAdmin: false,
                members: ["admin", "Tintin"],
                accessLevel: "write"
              }
            ]
          },
          {// details: dmi
            name: "dma",
            title: "Danish Maritime Authority",
            description: "The Danish Maritime Authority is a government agency of Denmark that regulates maritime affairs. The field of responsibility is based on the shipping industry and its framework conditions, the ship and its crew. In addition, it is responsible for aids to navigation in the waters surrounding Denmark and ashore.",
            members: ["admin", "Haddock"],
            teams: [
              {
                name: "Owners",
                description: "Special team of owners. Owners can do just about anything.",
                isOwner: true,
                members: ["admin"],
                accessLevel: "admin"
              },
              {
                name: "Captains",
                description: "Captains of the royal danish fleet with priviledge of editing stuff.",
                isAdmin: false,
                members: ["Hadock"],
                accessLevel: "write"
              }
            ]
          },
          {// details: dp
            name: "dp",
            title: "DanPilot",
            description: "DanPilot handles the public pilotage through Danish territorial waters from any destination in Denmark to all ports in the Baltic Sea. As the unique full-service provider in Denmark DanPilot offers pilotage to all Danish ports as well. DanPilot is obliged to deliver pilotage in Denmark and handles all transit pilotage.",
            members: ["admin", "Haddock", "Tintin"],
            teams: [
              {
                name: "Owners",
                description: "Special team of owners. Owners can do just about anything.",
                isOwner: true,
                members: ["admin"],
                accessLevel: "admin"
              },
              {
                name: "Captains",
                description: "Captains of the royal danish pilot fleet with priviledge of reading stuff.",
                isAdmin: false,
                members: ["Hadock"],
                accessLevel: "read"
              }
            ]
          }
        ];

        /**
         * Helper function to find organization by name
         * @param {type} organizationname
         * @returns {_L16.Anonym$11|_L16.Anonym$8|_L16.Anonym$5}
         */
        var findOrganization = function(organizationname) {
          for (var i = 0; i < organizations.length; i++) {
            if (organizationname === organizations[i].name)
              return organizations[i];
          }
          console.log("Error. Organizationname not found! ", organizationname);
          return null;
        };


        return {
          get: function(request) {
            console.log("organizationname: ", request.organizationname);
            var organizationname = request.organizationname;
            var result = findOrganization(organizationname);
            if (result)
              return result;
          },
          query: function(user) {
            console.log("ORGS U ", organizationsOfMember);

            if (user && user.name) {
              var organizationsOfMember = [];
              organizations.forEach(function(organization) {

                if (arrayIndexOf(user.name, organization.members))
                  organizationsOfMember.push(organization);
              });
              console.log("ORGS ", organizationsOfMember);
              return organizationsOfMember;
            }

            return organizations;
//        return (
//            [
//              {name: "dma", title: "Danish Maritime Authority"},
//              {name: "dmi", title: "Danish Meteoroligical Institute"},
//              {name: "dp", title: "DanPilot"}
//            ]
//            );
          },
          //create: {method: 'POST', params: {}, isArray: false}
          create: function(newOrganizationRequest, success, failure) {

            if (findOrganization(newOrganizationRequest.name)) {
              console.log("An organization with that name already exists");
              failure("An organization with that name already exists");
              return;
            }

            var newOrganization =
                {
                  name: newOrganizationRequest.name,
                  title: newOrganizationRequest.title,
                  description: newOrganizationRequest.description,
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

    .factory('SpecificationService', ['$resource',
      function($resource) {
//    return $resource('/rest/specifications/:specificationname', {}, {
//      query: {method: 'GET', params: {specificationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });
        console.log("TODO: using mocked specifications data");
        var specifications = [
          {
            ownerOrganization: "dmi",
            id: "wfss",
            version: 1,
            type: "weather",
            title: "Weather Forecast Service Specification",
            description: "Regione scribentur dissentiet eum ea, no atqui audiam ius, diam omittam efficiendi te usu.",
            instances: ["dmi-wfss-dk", "dmi-wfss-eu", "dmi-wfss-wrld"]
          },
          {
            ownerOrganization: "dp",
            id: "tpss",
            version: 1,
            type: "naval",
            title: "Transit Pilotage Service Specification",
            description: "Public pilotage through Danish territorial waters from any destination in Denmark to all ports in the Baltic Sea. As the unique full-service provider in Denmark DanPilot offers pilotage to all Danish ports as well.",
            instances: ["dp-tpss-rt", "dp-tpss-so"]
          },
          {
            ownerOrganization: "dma",
            id: "ntmss",
            version: 1,
            type: "naval",
            title: "Notice To Mariners Service Specification",
            description: "A notice to mariners advises mariners of important matters affecting navigational safety, including new hydrographic information, changes in channels and aids to navigation, and other important data.",
            instances: ["dma-ntmss-dk"]
          }
        ];

        /**
         * Helper function to find specification by name
         * @param {type} specificationname
         * @returns {_L16.Anonym$11|_L16.Anonym$8|_L16.Anonym$5}
         */
        var findSpecification = function(specificationname) {
          for (var i = 0; i < specifications.length; i++) {
            if (specificationname === specifications[i].name)
              return specifications[i];
          }
          console.log("Error. Specificationname not found! ", specificationname);
          return null;
        };


        return {
          get: function(request) {
            console.log("specificationname: ", request.specificationname);
            var specificationname = request.specificationname;
            var result = findSpecification(specificationname);
            if (result)
              return result;
          },
          query: function(request) {
            if (request && request.organizationname) {
              var specs = [];
              specifications.forEach(function(specification) {
                if (specification.ownerOrganization === request.organizationname)
                  specs.push(specification);
              });
              return specs;
            }
            return specifications;
          },
          //create: {method: 'POST', params: {}, isArray: false}
          create: function(newSpecificationRequest, success, failure) {

            if (findSpecification(newSpecificationRequest.name)) {
              console.log("An specification with that name already exists");
              failure("An specification with that name already exists");
              return;
            }

            var newSpecification =
                {
                  name: newSpecificationRequest.name,
                  title: newSpecificationRequest.title,
                  description: newSpecificationRequest.description,
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
                      description: "Members of the specification with read access",
                      isAdmin: false,
                      members: ["admin"],
                      accessLevel: "read"
                    }
                  ]
                };
            specifications.push(newSpecification);
            console.log("specifications: ", specifications);
            success(newSpecification);
            //return
          }

        };
      }]);



// ----------------------------------------------------------------------------
// HELPER FUNCTIONS
// ----------------------------------------------------------------------------

var arrayIndexOf = function(value, array) {
  var i = 0;
  array.forEach(function(element) {
    if (element === value)
      i++;
  });
  return i;
};

