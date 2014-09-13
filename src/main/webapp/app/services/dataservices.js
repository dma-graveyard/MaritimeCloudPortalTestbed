'use strict';

// ----------------------------------------------------------------------------
// TEST DATA - INSTANCE EXAMPLE
// ----------------------------------------------------------------------------

var organization = {
  dmi:
      {
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
  dma: {
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
  dp: {
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
  },
  imo: {
    name: 'imo',
    title: 'IMO - International Maritime Organization',
    description: 'the United Nations specialized agency with responsibility for the safety and security of shipping and the prevention of marine pollution by ships.',
    members: ["Haddock"],
    teams: [
      {
        name: "Owners",
        description: "Special team of owners. Owners can do just about anything.",
        isOwner: true,
        members: ["Hadock"],
        accessLevel: "admin"
      }
    ]
  }
};
var operationalServices = {
  lps: {
    id: 'lps',
    name: 'Local Port Services'
  },
  mis: {
    id: 'mis',
    name: 'Meteorological Information Services',
    description: 'Meteorological services include forecasting and warnings and monitoring of weather, climate and related environmental conditions in the atmosphere, on land and at sea.'
  },
  msi: {
    id: 'msi',
    name: 'Maritime Safety Information'
  },
  nas: {
    id: 'nas',
    name: 'Navigational Assistance Service'
  },
  nga: {
    id: 'nga',
    name: 'No-Go Area'
  },
  rme: {
    id: 'rme',
    name: 'Route METOC'
  },
  sre: {
    id: 'sre',
    name: 'Strategical Route Exchange'
  },
  tos: {
    id: 'tos',
    name: 'Traffic Organization Service'
  },
  vsr: {
    id: 'vsr',
    name: 'Vessel Shore Reporting'
  },
  wvtsg: {
    id: 'wvtsg',
    name: 'World Vessel Traffic Services Guide'
  },
  tre: {
    id: 'tre',
    name: 'Tactical Route Exchange'
  },
  tus: {
    id: 'tus',
    name: 'Tugs Services'
  }
};

var transportTypes = {
  mms: 'MMS',
  rest: 'REST',
  soap: 'SOAP',
  www: 'WWW',
  tcp: 'TCP',
  udp: 'UDP',
  aisasm: 'AISASM',
  tel: 'TEL',
  vhf: 'VHF',
  dgnss: 'DGNSS',
  other: 'OTHER'
};

var technicalServices = {
  imoMisRest: {
    id: 'imo-mis-rest',
    owner: organization.imo,
    operationalService: operationalServices.mis,
    transportType: transportTypes.rest,
    name: 'METOC en route (rest)',
    description: 'Meteorological services provided as a REST api'
  },
  imoMisWww: {
    id: 'imo-mis-www',
    owner: organization.imo,
    operationalService: operationalServices.mis,
    transportType: transportTypes.www,
    name: 'METOC en route (www)',
    description: 'Meteorological services provided on the internet'
  },
  imoMsiSoap: {
    id: 'imo-msi-soap',
    owner: organization.imo,
    operationalService: operationalServices.msi,
    transportType: transportTypes.soap,
    name: 'MSI (soap)',
    description: 'Maritime Safety Information provided as a SOAP-service'
  },
  imoMsiVhf: {
    id: 'imo-msi-vhf',
    owner: organization.imo,
    operationalService: operationalServices.msi,
    transportType: transportTypes.vhf,
    name: 'MSI (vhf)',
    description: 'Maritime Safety Information broadcasted on VHF'
  },
  imoMsiWww: {
    id: 'imo-msi-www',
    owner: organization.imo,
    operationalService: operationalServices.msi,
    transportType: transportTypes.www,
    name: 'MSI (www)',
    description: 'Maritime Safety Information provided on the internet'
  }
};

var area = {
  dk: [// array of shapes (polygon/rectangle/circle/multipolygon)
    {
      type: "polygon",
      points: [// [longitude,lattitude]-pairs
        [8.173828125, 54.84973402078036], [7.492675781249999, 56.29063241616282], [8.06396484375, 57.32503845095438],
        [9.140625, 57.44347144354234], [9.73388671875, 57.80818813313426], [10.08544921875, 57.80818813313426],
        [10.56884765625, 57.936725003674646], [11.00830078125, 57.71441809916714], [10.986328125, 57.58508660014084],
        [11.35986328125, 57.44347144354234], [11.53564453125, 57.23001638509267], [11.162109375, 57.02727908263874],
        [10.7666015625, 57.01531876758453], [10.65673828125, 56.82342990779178], [10.96435546875, 56.73916801839526],
        [11.271972656249998, 56.84746998772644], [11.66748046875, 56.93148877710671], [11.953125, 56.59427839029623],
        [11.53564453125, 56.473111073472246], [11.14013671875, 56.315013425566924], [10.72265625, 55.78738467626539],
        [11.71142578125, 56.412381965477785], [12.32666015625, 56.25403172382012], [12.722167968749998, 56.02141309205163],
        [12.76611328125, 55.67603572236134], [12.722167968749998, 55.40251032740405], [12.89794921875, 55.11451369585085],
        [12.94189453125, 54.90030293114211], [12.15087890625, 54.51948733886334], [11.93115234375, 54.44289461838544],
        [11.25, 54.51948733886334], [10.52490234375, 54.532238849162084], [9.95361328125, 54.81176569069303],
        [9.68994140625, 54.87502640669144], [8.173828125, 54.84973402078036]
      ]
    }
  ],
  gl: [
    {
      type: "polygon",
      points: [[-73.47656249999999, 59.33318942659219], [-73.47656249999999, 84.36725432248352], [-8.4375, 84.36725432248352], [-8.4375, 59.33318942659219]]
    },
    {
      "type": "circle",
      "center-latitude": 75.30888448476105,
      "center-longitude": -73.828125,
      "radius": 456789
    }

  ],
  fo: [
    {
      "type": "circle",
      "center-latitude": 62.00992920374125,
      "center-longitude": -6.96533203125,
      "radius": 234567
    }
  ]
};

var serviceInstance = {
  dmiImoMisDkRest: {
    provider: organization.dmi,
    specification: technicalServices.imoMisRest,
    key: {
      specificationId: "imo-mis-rest", // [TechnicalServiceId]
      providerId: "dmi", // [MaritimeId (=OrganizationId/UserId)]
      instanceId: "dk"   // [ServiceInstanceId]
    },
    id: "dk", // [TechnicalServiceId].[OrganizationId].[ServiceInstanceId]
    // or id: "fo.imo-met-metocroute.dmi.dk", // [ServiceInstanceId].[TechnicalServiceId].[OrganizationId].[OrganizationType]
    name: "DMI METOC on route (Denmark)",
    description: "Route based Meteorological Services for the waters surrounding Denmark including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
    coverage: area.dk
  },
  dmiImoMisDkWww: {
    provider: organization.dmi,
    specification: technicalServices.imoMisWww,
    key: {
      specificationId: "imo-mis-www",
      providerId: "dmi",
      instanceId: "dk"
    },
    id: "dk",
    name: "DMI METOC on route (Denmark)",
    description: "Route based Meteorological Services for the waters surrounding Denmark including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
    coverage: area.dk,
  },
  dmiImoMisGlWww: {
    provider: organization.dmi,
    specification: technicalServices.imoMisWww,
    key: {
      specificationId: "imo-mis-www",
      providerId: "dmi",
      instanceId: "gl"
    },
    id: "gl",
    name: "DMI METOC on route (Greenland)",
    description: "Route based Meteorological Services for the waters surrounding Greenland including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
    coverage: area.gl
  },
  dmiImoMisFoRest: {
    provider: organization.dmi,
    specification: technicalServices.imoMisRest,
    key: {
      specificationId: "imo-mis-rest",
      providerId: "dmi",
      instanceId: "fo"
    },
    id: "fo",
    name: "DMI METOC on route (Faroe Islands)",
    description: "Route based Meteorological Services for the waters surrounding the Faroe Islands including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
    coverage: area.fo
  },
  dmiImoMisFoWww: {
    provider: organization.dmi,
    specification: technicalServices.imoMisWww,
    key: {
      specificationId: "imo-mis-www",
      providerId: "dmi",
      instanceId: "fo"
    },
    id: "fo",
    name: "DMI METOC on route (Faroe Islands)",
    description: "Route based Meteorological Services for the waters surrounding the Faroe Islands including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
    coverage: area.fo
  }
};

// --------------------------------------------------
/* Services */
// --------------------------------------------------

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
          organization.dma,
          organization.dmi,
          organization.dp,
          organization.imo
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
            instances: ["dmi-wfss-dk", "dmi-wfss-fo", "dmi-wfss-gl"]
          },
          {
            ownerOrganization: "dp",
            id: "tpss",
            version: 1,
            type: "naval",
            title: "Transit Pilotage Service Specification",
            description: "Public pilotage through Danish territorial waters from any destination in Denmark to all ports in the Baltic Sea. As the unique full-service provider in Denmark DanPilot offers pilotage to all Danish ports as well.",
            instances: ["dp-tpss-dtw"]
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
      }])


    .factory('ServiceInstanceService', ['$resource',
      function($resource) {
//    return $resource('/rest/serviceInstance/:specificationname', {}, {
//      query: {method: 'GET', params: {specificationname: ''}, isArray: true},
//      signUp: {method: 'POST', params: {}, isArray: false}
//    });
        console.log("TODO: using mocked service instance data");
        var geom =
            {
              "type": "Feature",
              "id": "DNK",
              "properties": {
                "name": "Denmark"
              },
              "geometry": {
                "type": "MultiPolygon",
                "coordinates":
                    [
                      [[[12.690006, 55.609991], [12.089991, 54.800015], [11.043543, 55.364864], [10.903914, 55.779955], [12.370904, 56.111407]]]
                    ]
              }
            };
        var serviceInstances2 = [
          {
            ownerOrganization: "dmi",
            id: "dmi-wfss-dk",
            version: 1,
            type: "weather",
            title: "Meteorological Services in Denmark and surrounding waters",
            description: "Meteorological services include forecasting and warnings and monitoring of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
            coverage: [geom]
          },
          {
            ownerOrganization: "dmi",
            id: "dmi-wfss-fo",
            version: 1,
            type: "weather",
            title: "Meteorological Services in Faroe Islands and surrounding waters",
            description: "Meteorological services include forecasting and warnings and monitoring of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
            coverage: [
              {
                "type": "Feature",
                "id": "DNK",
                "properties": {
                  "name": "Denmark"
                },
                "geometry": {
                  "type": "MultiPolygon",
                  "coordinates": [
                    [[[8.173828125, 54.84973402078036], [7.492675781249999, 56.29063241616282], [8.06396484375, 57.32503845095438],
                        [9.140625, 57.44347144354234], [9.73388671875, 57.80818813313426], [10.08544921875, 57.80818813313426],
                        [10.56884765625, 57.936725003674646], [11.00830078125, 57.71441809916714], [10.986328125, 57.58508660014084],
                        [11.35986328125, 57.44347144354234], [11.53564453125, 57.23001638509267], [11.162109375, 57.02727908263874],
                        [10.7666015625, 57.01531876758453], [10.65673828125, 56.82342990779178], [10.96435546875, 56.73916801839526],
                        [11.271972656249998, 56.84746998772644], [11.66748046875, 56.93148877710671], [11.953125, 56.59427839029623],
                        [11.53564453125, 56.473111073472246], [11.14013671875, 56.315013425566924], [10.72265625, 55.78738467626539],
                        [11.71142578125, 56.412381965477785], [12.32666015625, 56.25403172382012], [12.722167968749998, 56.02141309205163],
                        [12.76611328125, 55.67603572236134], [12.722167968749998, 55.40251032740405], [12.89794921875, 55.11451369585085],
                        [12.94189453125, 54.90030293114211], [12.15087890625, 54.51948733886334], [11.93115234375, 54.44289461838544],
                        [11.25, 54.51948733886334], [10.52490234375, 54.532238849162084], [9.95361328125, 54.81176569069303],
                        [9.68994140625, 54.87502640669144], [8.173828125, 54.84973402078036]]]
                  ]
                }
              }
            ]
          },
          {
            ownerOrganization: "dmi",
            id: "dmi-wfss-gl",
            version: 1,
            type: "weather",
            title: "Meteorological Services in Greenland and surrounding waters",
            description: "Meteorological services include forecasting and warnings and monitoring of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
            coverage: [geom]
          },
          {
            ownerOrganization: "dp",
            id: "dp-tpss-dtw",
            version: 1,
            type: "naval",
            title: "Transit Pilotage Service through Danish territorial waters",
            description: "Public pilotage through Danish territorial waters from any destination in Denmark to all ports in the Baltic Sea. As the unique full-service provider in Denmark DanPilot offers pilotage to all Danish ports as well. The regulation regarding mandatory use of pilots is found in the Pilotage Act. In order to ensure the safety at sea and to protect the environment the Pilotage Act and the corresponding Order on the use of a pilot make the use of a pilot mandatory for certain vessels for a number of specified geographical areas.",
            coverage: [geom]
          },
          {
            ownerOrganization: "dma",
            id: "ntmss",
            version: 1,
            type: "naval",
            title: "Notice To Mariners Service Instance",
            description: "A notice to mariners advises mariners of important matters affecting navigational safety, including new hydrographic information, changes in channels and aids to navigation, and other important data.",
            coverage: [geom]
//            instances: ["dma-ntmss-dk"]
          }
        ];

        var serviceInstances = [
          serviceInstance.dmiImoMisDkRest,
          serviceInstance.dmiImoMisDkWww,
          serviceInstance.dmiImoMisFoRest,
          serviceInstance.dmiImoMisFoWww,
          serviceInstance.dmiImoMisGlWww
        ];


        /**
         * Helper function to find Service Instance by name
         * @param {type} serviceInstanceName
         */
        var findServiceInstance = function(serviceInstanceName) {
          for (var i = 0; i < serviceInstances.length; i++) {
            if (serviceInstanceName === serviceInstances[i].name)
              return serviceInstances[i];
          }
          console.log("Error. ServiceInstancename not found! ", serviceInstanceName);
          return null;
        };
        return {
          get: function(request) {
            console.log("serviceInstanceName: ", request.serviceInstanceName);
            var serviceInstanceName = request.serviceInstanceName;
            var result = findServiceInstance(serviceInstanceName);
            if (result)
              return result;
          },
          query: function(request) {
            if (request && request.organizationname) {
              var specs = [];
              serviceInstances.forEach(function(serviceInstance) {
                if (serviceInstance.provider.name === request.organizationname)
                  specs.push(serviceInstance);
              });
              return specs;
            }
            return serviceInstances;
          },
          //create: {method: 'POST', params: {}, isArray: false}
          create: function(newServiceInstanceRequest, success, failure) {

            if (findServiceInstance(newServiceInstanceRequest.name)) {
              console.log("An service instance with that name already exists");
              failure("An service instance with that name already exists");
              return;
            }

            var newServiceInstance =
                {
                  name: newServiceInstanceRequest.name,
                  title: newServiceInstanceRequest.title,
                  description: newServiceInstanceRequest.description,
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
            serviceInstances.push(newServiceInstance);
            console.log("serviceInstance: ", serviceInstances);
            success(newServiceInstance);
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
var s = {
  "provider": {
    "name": "Oslo VTS",
    "id": "NO-VTS-000001"
  },
  "specification": {
    "transport": "web",
    "variant": "web",
    "version": "1.0",
    "name": "Traffic Organisation Service (web)",
    "operationalService": {
      "name": "Traffic Organization Service"
    },
    "serviceId": "imo.tos"
  },
  "name": "Oslo VTS TOS (web)",
  "type": "STATIC",
  "description": "Oslo VTS Traffic Organization Service\n",
  "endpoint": [
    {
      "url": "http://www.oslohavn.no/en/cargo/services_at_port_of_oslo/oslo_vts/",
      "type": "URL"
    }
  ],
  "extent": {
    "area": {
      "points": [
        {
          "lat": "59.14750415186919",
          "lon": "10.24853945248625"
        },
        {
          "lat": "59.06172493759117",
          "lon": "10.31098239982662"
        },
        {
          "lat": "58.94171911685134",
          "lon": "10.48496615599738"
        },
        {
          "lat": "58.94344692618775",
          "lon": "10.9694846052578"
        },
        {
          "lat": "58.97940934208044",
          "lon": "11.05472607483105"
        },
        {
          "lat": "59.00440988871283",
          "lon": "11.1129057231445"
        },
        {
          "lat": "59.03650001180731",
          "lon": "11.12980251608729"
        },
        {
          "lat": "59.08287209177146",
          "lon": "11.21135420258818"
        },
        {
          "lat": "59.19124730593956",
          "lon": "11.22468488812276"
        },
        {
          "lat": "59.78287464383528",
          "lon": "10.87648363357374"
        },
        {
          "lat": "59.78154420177945",
          "lon": "10.19143359556659"
        },
        {
          "lat": "59.14750415186919",
          "lon": "10.24853945248625"
        }
      ],
      "type": "polygon"
    }
  }
}
