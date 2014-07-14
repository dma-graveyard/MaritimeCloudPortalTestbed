'use strict';

/* Services */

var iamServices = angular.module('iamServices', ['ngResource']);

iamServices.factory('UserService', ['$resource',
  function($resource) {
    return $resource('/rest/users/:username', {}, {
      query: {method: 'GET', params: {username: ''}, isArray: true},
      signUp: {method: 'POST', params: {}, isArray: false}
    });
  }]);

iamServices.factory('OrganizationService', ['$resource',
  function($resource) {
//    return $resource('/rest/organizations/:organizationname', {}, {
//      query: {method: 'GET', params: {organizationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });

    console.log("TODO: using mocked organizations data");
    return {
      get: function(request) {
        console.log("organizationname: ", request.organizationname);
        var organizationname = request.organizationname;

        if (organizationname === 'dmi')
          return(
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
              });
        else if(organizationname === 'dma')
          return(
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
              });
        else /*if(organizationname === 'dma')*/
          return(
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
              });
              
      },
      query: function() {
        return (
            [
              {name: "dma", title: "Danish Maritime Authority"},
              {name: "dmi", title: "Danish Meteoroligical Institute"},
              {name: "dp", title: "DanPilot"}
            ]
            );
      }
    };
  }]);

