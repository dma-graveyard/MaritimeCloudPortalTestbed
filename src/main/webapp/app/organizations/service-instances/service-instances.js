'use strict';

var fitToPaths = function fitToPaths(mapId, leafletData, mapService) {
  leafletData.getMap(mapId).then(function (map) {
    mapService.fitToGeomitryLayers(map);
  });
};

// FIXME move to a service or config or something
var servicetypeProtocols = {
  AISASM: ['ais:'],
  DGNSS: ['dgnss:'],
  FTP: ['ftp:'],
  EMAIL: ['mailto:'],
  HTTP: ['http://', 'https://'],
  MMS: ['mms:'],
  NAVTEX: ['navtex:'],
  REST: ['http://', 'https://'],
  SOAP: ['http://', 'https://'],
  TCP: ['tcp:'],
  TEL: ['tel:'],
  UDP: ['udp:'],
  VHF: ['vhf:'],
  WWW: ['http://', 'https://']
};

var indexOfUri = function (endpoints, endpointUri) {
  if (endpoints) {
    for (var i = 0; i < endpoints.length; i++) {
      if (endpoints[i].uri === endpointUri)
        return i;
    }
  }
  return -1;
};

angular.module('mcp.organizations.services', [])

    .controller('ServiceInstanceDetailsController', ['$scope', 'AlmanacServiceSpecificationService', 'AlmanacOrganizationService',
      function ($scope, AlmanacServiceSpecificationService, AlmanacOrganizationService) {
        $scope.details = {isCollapsed: true};
        $scope.toggleDetails = function () {
          $scope.details.isCollapsed = !$scope.details.isCollapsed;
        };
        $scope.service.specification = AlmanacServiceSpecificationService.get({serviceSpecificationId: $scope.service.specificationId});
        $scope.service.provider = AlmanacOrganizationService.get({organizationId: $scope.service.providerId});
      }
    ])

    .controller('CreateServiceInstanceController', ['$scope', '$location', '$modal', '$stateParams', '$state', 'UUID',
      'AlmanacOperationalServiceService', 'AlmanacServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $location, $modal, $stateParams, $state, UUID,
          AlmanacOperationalServiceService, AlmanacServiceSpecificationService, ServiceInstanceService) {

        var reportError = function (error) {
          $scope.message = null;
          $scope.alertMessages = ["Error on the serverside :( ", error];
        };

        angular.extend($scope, {
          map: {}, // this property is populated with methods by the "thumbnail-map"-directive!!!
          message: null,
          alertMessages: null,
          selection: {
            operationalService: null,
            specification: null
          },
          operationalServices: AlmanacOperationalServiceService.query(),
          service: {
            serviceInstanceId: null,
            providerId: $stateParams.organizationId,
            name: null,
            summary: null,
            coverage: [],
            endpoints: []
          },
          protocol: "<select a specification type>",
          selectOperationalService: function (selectedOperationalService) {
            $scope.specifications = selectedOperationalService ? AlmanacServiceSpecificationService.query(
                {operationalServiceId: selectedOperationalService.operationalServiceId}, function (data) {
              // is not in list then reset
              $scope.selection.specification = null;
            }) : [];
          },
          setServiceTypeProtocol: function (serviceSpecification) {
            if (serviceSpecification) {
              $scope.protocols = servicetypeProtocols[serviceSpecification.serviceType];
              $scope.protocol = servicetypeProtocols[serviceSpecification.serviceType][0];
            }
          },
          isLockedOrInvalidEndpoint: function (newEndpoint) {
            var protocol = $scope.protocol;

            var b = !($scope.selection.specification)
                || !newEndpoint
                || newEndpoint.trim().length === 0
                || indexOfUri($scope.service.endpoints, protocol + newEndpoint) >= 0;
            return b;
          },
          addEndpoint: function (newEndpointUri) {
            var protocol = $scope.protocol;

            // FIXME: add validation
            //validateUri(newEndpoint, serviceType);

            $scope.service.endpoints.push({uri: protocol + newEndpointUri});
          },
          removeEndpoint: function (endpointUri) {
            var index = indexOfUri($scope.service.endpoints, endpointUri);
            if (index >= 0) {
              $scope.service.endpoints.splice(index, 1);
            }
          },
          openCoverageEditor: function () {
            $modal.open({
              templateUrl: 'organizations/service-instances/coverage-editor.html',
              controller: 'CoverageEditorController',
              size: 'lg',
              backdrop: 'static',
              resolve: {
                coverage: function () {
                  return $scope.service.coverage;
                },
                mapOptions: function () {
                  return {bounds: $scope.map.handle.getBounds()};
                }
              }
            }).result.then(function (result) {
              // submit
              $scope.service.coverage = result;
              $scope.map.rebuild();
            }, function () {
              // dismiss
            });
          },
          close: function (result) {
            $location.path('/orgs/' + $scope.service.providerId).replace();
          },
          formIsSubmitable: function () {
            return ($scope.service.serviceInstanceId && $scope.service.name /*&& $scope.service.coverage*/);
          },
          submit: function () {
            $scope.providerId = $stateParams.organizationId;

            $scope.alertMessages = null;
            $scope.message = "Sending request to register service instance...";

            $scope.service.specificationId = $scope.selection.specification.serviceSpecificationId;
            // Create instance
            ServiceInstanceService.create($scope.service, function () {

              // add endpoints  
              $scope.service.endpoints.forEach(function (endpoint) {

                // add endpoint 
                ServiceInstanceService.addEndpoint($scope.service, endpoint.uri, function () {
                  // report endpoint added
                }, function (error) {
                  console.log("Error adding endpoint", protocol + newEndpointUri, error);
                  $scope.alertMessages = ["Error adding endpoint", protocol + newEndpointUri, error];
                });

                // add primary alias
                ServiceInstanceService.addAlias($scope.service, $scope.service.primaryAlias, function () {
                  // alias created
                  console.log("alias created", $scope.service.primaryAlias);
                  $scope.message = "Created alias " + $scope.service.primaryAlias;

                }, function (error) {
                  console.log("Oh shoot - failed to create the alias for this service instance :-( ", $scope.service.primaryAlias, error);
                  $scope.alertMessages = ["Oh shoot - failed to create the alias for this service instance :-( " + $scope.service.primaryAlias, error];
                });

                $scope.close();
              });

            }, reportError);
          }
        });

        // this property is referenced by the "thumbnail-map"-directive!!!
        $scope.services = [$scope.service];

        // Fetch and assign a new UUID from the server
        UUID.get({name: "identifier"}, function (newUuid) {
          $scope.service.serviceInstanceId = newUuid.identifier;
        });

      }])

    .controller('EditServiceInstanceController', [
      '$scope', '$location', '$modal', '$stateParams', 'AlmanacServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $location, $modal, $stateParams, AlmanacServiceSpecificationService, ServiceInstanceService) {

        var reportError = function (error) {
          $scope.message = null;
          $scope.alertMessages = ["Error on the serverside :( ", error];
        };

        var getHydratedServiceInstance = function () {
          return ServiceInstanceService.get({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId},
          function (serviceInstance) {

            // "hydrate" ServiceInstance with ServiceSpecification data
            AlmanacServiceSpecificationService.get({serviceSpecificationId: $scope.service.specificationId}, function (serviceSpecification) {
              serviceInstance.specification = serviceSpecification;

              // empty the services array (a reference is held by the map, so we cannot just assign a new empty array!)
              $scope.services.length = 0;
              // add the new instance reference
              $scope.services.push(serviceInstance);
              // rebuild the map once the request has returned the serviceInstance
              $scope.map.rebuild();

              if (serviceInstance.specification) {
                $scope.protocols = servicetypeProtocols[serviceSpecification.serviceType];
                $scope.protocol = servicetypeProtocols[serviceSpecification.serviceType][0];
              }

              // add list of aliases
              ServiceInstanceService.aliases({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId}, function (aliases) {
                // ...but remove the primary alias from the list
                var list = [];
                aliases.forEach(function (alias) {
                  if (alias.alias !== serviceInstance.primaryAlias)
                    list.push(alias);
                });
                serviceInstance.aliases = list;
              });
            });

          }, reportError);
        };

        angular.extend($scope, {
          map: {}, // this property is populated with methods by the "thumbnail-map"-directive!!!
          services: [], // this property is referenced by the "thumbnail-map"-directive!!!
          message: null,
          alertMessages: null,
          service: getHydratedServiceInstance(),
          protocol: "<select a specification type>",
          formIsSubmitable: function () {
            return ($scope.service.serviceInstanceId && $scope.service.name /*&& $scope.service.coverage*/);
          },
          isLockedOrInvalidEndpoint: function (newEndpoint) {
            var protocol = $scope.protocol;

            var b = !newEndpoint
                || newEndpoint.trim().length === 0
                || indexOfUri($scope.service.endpoints, protocol + newEndpoint) >= 0;
            return b;
          },
          addAlias: function (newAlias) {
            ServiceInstanceService.addAlias($scope.service, newAlias, function () {

              // reload serviceInstance
              $scope.service = getHydratedServiceInstance();
              $scope.message = "Alias added!";

            }, reportError);
          },
          removeAlias: function (alias) {
            ServiceInstanceService.removeAlias($scope.service, alias, function () {

              // reload serviceInstance
              $scope.service = getHydratedServiceInstance();
              $scope.message = "Alias removed!";

            }, reportError);
          },
          addEndpoint: function (newEndpointUri) {
            var protocol = $scope.protocol;

            // FIXME: add validation
            //validateUri(newEndpoint, serviceType);

            // send remote command right away 
            ServiceInstanceService.addEndpoint($scope.service, protocol + newEndpointUri, function () {

              // reload serviceInstance
              $scope.service = getHydratedServiceInstance();
              $scope.message = "Endpoint added!";

            }, reportError);
          },
          removeEndpoint: function (endpointUri) {
            ServiceInstanceService.removeEndpoint($scope.service, endpointUri, function () {

              // reload serviceInstance
              $scope.service = getHydratedServiceInstance();
              $scope.message = "Endpoint removed!";

            }, reportError);
          },
          openCoverageEditor: function () {
            $modal.open({
              templateUrl: 'organizations/service-instances/coverage-editor.html',
              controller: 'CoverageEditorController',
              size: 'lg',
              backdrop: 'static',
              resolve: {
                coverage: function () {
                  return $scope.service.coverage;
                },
                mapOptions: function () {
                  return {bounds: $scope.map.handle.getBounds()};
                }
              }
            }).result.then(function (result) {
              // submit
              $scope.service.coverage = result;
              $scope.map.rebuild();
              ServiceInstanceService.changeCoverage($scope.service, function () {
                $scope.message = "Coverage changed!";
              }, reportError /*TODO: shouldn't we reload original in this case?*/);
            });
          },
          close: function (result) {
            $location.path('/orgs/' + $scope.service.providerId).replace();
          },
          submit: function () {
            $scope.providerId = $stateParams.organizationId;

            $scope.alertMessages = null;
            $scope.message = "Sending request to register service instance...";

            ServiceInstanceService.changeNameAndSummary($scope.service, function () {
              $scope.message = "Name and summary successfully updated!";
            }, reportError);
          }
        });

        $scope.resolveUniqueAlias = function () {
          if (!angular.isDefined($scope.newAlias)) {
            $scope.aliasAlreadyExist = true;
            return;
          }

          ServiceInstanceService.alias({organizationId: $stateParams.organizationId, alias: $scope.newAlias}, function (aliasEntry) {
            console.log('alias result', aliasEntry);
            $scope.aliasAlreadyExist = angular.isDefined(aliasEntry.alias);
          });
        };

        $scope.$watch("newAlias",
            function (newValue, oldValue, scope) {
              if (newValue !== oldValue) {
                //console.log(newValue, oldValue);
                scope.resolveUniqueAlias();
              }
            }
        );


      }])

    .controller('CoverageEditorController', ['$scope', 'leafletData', 'mapService', 'coverage', 'mapOptions',
      function ($scope, leafletData, mapService, coverage, mapOptions) {

        var options = mapService.createDrawingOptions(),
            drawControl = new L.Control.Draw(options),
            serviceLayer = options.edit.featureGroup;

        angular.extend($scope, {
          center: {
            lat: 51,
            lng: 0,
            zoom: 4
          },
          controls: {
            custom: [drawControl]
          },
          latlongs: []
        });

        // convert supplied coverage shapes to layers 
        mapService.shapesToLayers(coverage).forEach(function (layer) {
          serviceLayer.addLayer(layer);
        });

        // add layers to map and add a draw-listener
        leafletData.getMap("coverageEditorMap").then(function (map) {
          map.addLayer(serviceLayer);
          serviceLayer.setStyle(mapService.Styles.STATIC);

          if (coverage.length) {
            mapService.fitToGeomitryLayers(map);
          } else {
            if (mapOptions.bounds)
              map.fitBounds(mapOptions.bounds);
          }

          map.on('draw:created', function (e) {
            var layer = e.layer;
            serviceLayer.addLayer(layer);
          });
        });

        angular.extend($scope, {
          formIsSubmitable: function () {
            return (serviceLayer.getLayers().length);
          },
          submit: function () {
            $scope.$close(mapService.layersToShapes(serviceLayer.getLayers()));
          }
        });

      }])
    ;
