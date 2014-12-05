'use strict';

var fitToPaths = function fitToPaths(mapId, leafletData, mapService) {
  leafletData.getMap(mapId).then(function (map) {
    mapService.fitToGeomitryLayers(map);
  });
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

    .controller('EditServiceInstanceController', ['$scope', '$location', '$modal', '$stateParams', '$state',
      'AlmanacOperationalServiceService', 'AlmanacServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $location, $modal, $stateParams, $state,
          AlmanacOperationalServiceService, AlmanacServiceSpecificationService, ServiceInstanceService) {

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

        angular.extend($scope, {
          map: {}, // this property is populated with methods by the "thumbnail-map"-directive!!!
          message: null,
          alertMessages: null,
          selection: {
            operationalService: null,
            specification: null
          },
          operationalServices: AlmanacOperationalServiceService.query(),
          isCreateState: function () {
            return $state.current.data.createState;
          },
          isEditState: function () {
            return $state.current.data.editState;
          },
          service: {
            serviceInstanceId: null,
            providerId: $stateParams.organizationId,
            name: null,
            summary: null,
            coverage: [],
            endpoints: []
          },
          // FIXME - use service type info!!!!
          protocol: "<select a specification type>",
          selectOperationalService: function (selectedOperationalService) {
            $scope.specifications = selectedOperationalService ? AlmanacServiceSpecificationService.query(
                {operationalServiceId: selectedOperationalService.operationalServiceId}, function (data) {
              // is not in list then reset
              $scope.selection.specification = null;
            }) : [];
          },
          formIsSubmitable: function () {
            return ($scope.service.serviceInstanceId && $scope.service.name /*&& $scope.service.coverage*/);
          },
          isLockedOrInvalidEndpoint: function (newEndpoint) {
            var protocol = $scope.protocol;

            var b = !($scope.selection.specification || $scope.isEditState())
                || !newEndpoint
                || newEndpoint.trim().length === 0
                || indexOfUri(protocol + newEndpoint) >= 0;
            return b;
          },
          addEndpoint: function (newEndpointUri) {
            var protocol = $scope.protocol;
            // FIXME: add validation
            //validateUri(newEndpoint, serviceType);

            // chekc for redundancy!
            if ($scope.isEditState()) {

              // send remote command right away
              ServiceInstanceService.addEndpoint($scope.service, protocol + newEndpointUri, function () {
                $scope.service = ServiceInstanceService.get({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId});
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });
            } else {
              $scope.service.endpoints.push({uri: protocol + newEndpointUri});
            }

          },
          removeEndpoint: function (endpointUri) {
            // FIXME: add validation
            //validateUri(newEndpoint, serviceType);
            if ($scope.isEditState()) {
              ServiceInstanceService.removeEndpoint($scope.service, endpointUri, function () {
                $scope.service = ServiceInstanceService.get({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId});
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });

            } else {
              var index = indexOfUri(endpointUri);
              if (index >= 0) {
                $scope.service.endpoints.splice(index, 1);
              }
            }
          },
          close: function (result) {
            $location.path('/orgs/' + $scope.service.providerId).replace();
          },
          submit: function () {
            $scope.providerId = $stateParams.organizationId;

            $scope.alertMessages = null;
            $scope.message = "Sending request to register service instance...";

            if ($scope.isEditState()) {
              // TODO: skipping response! ...condsider to nest in response of next request
              ServiceInstanceService.changeCoverage($scope.service);
              ServiceInstanceService.changeNameAndSummary($scope.service, function () {
                $location.path('/orgs/' + $scope.service.providerId).replace();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });
            } else {
              $scope.service.specificationId = $scope.selection.specification.serviceSpecificationId;
              // Create instance
              ServiceInstanceService.create($scope.service, function (result) {

                // add endpoints  
                $scope.service.endpoints.forEach(function (endpoint) {

                  // add endpoint 
                  ServiceInstanceService.addEndpoint($scope.service, endpoint.uri, function () {
                  }, function (error) {
                    console.log("Error adding endpoint", protocol + newEndpointUri, error);
                    $scope.alertMessages = ["Error adding endpoint", protocol + newEndpointUri, error];
                  });
                });

                $scope.close();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });
            }
          }
        });

        var indexOfUri = function (endpointUri) {
          for (var i = 0; i < $scope.service.endpoints.length; i++) {
            if ($scope.service.endpoints[i].uri === endpointUri)
              return i;
          }
          return -1;
        };

        var setServiceTypeProtocol = function (ss) {
          console.log("SS", ss);
          if (ss)
            $scope.protocols = servicetypeProtocols[ss.serviceType];
            $scope.protocol = servicetypeProtocols[ss.serviceType][0];
        };
        $scope.setServiceTypeProtocol = setServiceTypeProtocol;

        if ($scope.isEditState()) {
          $scope.service = ServiceInstanceService.get({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId},
          function (data) {

            // we need to rebuild the map once the request has returned the service details
            $scope.map.rebuild();

            // "hydrate" ServiceInstance with ServiceSpecification data
            $scope.service.specification = AlmanacServiceSpecificationService.get({serviceSpecificationId: $scope.service.specificationId}, setServiceTypeProtocol);

            // FIXME: should lookup value based on id $scope.selectedSpecification.operationalServiceId
            //$scope.selectedOperationalService = OperationalServiceService.query({operationalServiceId: $scope.selectedSpecification.operationalServices[0]});
          });

        }

        $scope.services = [$scope.service];

        $scope.openCoverageEditor = function () {
          $modal.open({
            templateUrl: 'organizations/services/coverage-editor.html',
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
        };

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
