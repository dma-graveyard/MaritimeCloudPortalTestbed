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
        description: "Lorem ipsum dolor sit amet, ex quo sint aeque. Regione scribentur dissentiet eum ea, no atqui audiam ius, diam omittam efficiendi te usu.",
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
          },
        ]
      },
      {// details: dmi
        name: "dma",
        title: "Danish Maritime Authority",
        description: "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.",
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
          },
        ]
      },
      {// details: dp
        name: "dp",
        title: "DanPilot",
        description: "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum.",
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
          },
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
      query: function() {
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
        
        if(findOrganization(newOrganizationRequest.name)){
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
  }]);

