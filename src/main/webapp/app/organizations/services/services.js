'use strict';

var fitToPaths = function fitToPaths(mapId, leafletData, mapService) {
  leafletData.getMap(mapId).then(function (map) {
    mapService.fitToGeomitryLayers(map);
  });
};

angular.module('mcp.organizations.services', [])

    .controller('ServiceInstanceDetailsController', ['$scope', 'mapService', 'MAP_DEFAULTS', 'leafletData', '$timeout',
      function ($scope, mapService, MAP_DEFAULTS, leafletData, $timeout) {
        console.log('$scope.service ', $scope.service);

        angular.extend($scope, {
          map: {
            defaults: MAP_DEFAULTS.STATIC,
            paths: mapService.shapesToPaths($scope.service.coverage)
          },
          events: {
            map: {
              enable: ['click'],
              logic: 'emit'
            }
          }
        });

        $scope.$on('leafletDirectiveMap.click', function (event) {
          console.log("Event click: ", event);
        });

        // register a timeout that will fit (position and zoom) the map to its paths
        $timeout(function () {
          fitToPaths('map-' + $scope.$index, leafletData, mapService);
        }, 0);

      }])

    .controller('CreateServiceInstanceController', ['$scope', '$location', 'ServiceInstanceService', '$stateParams', '$state',
      'OperationalServiceService', 'TechnicalServiceService', 'leafletData', 'mapService', 'MAP_DEFAULTS', '$modal', '$timeout',
      function ($scope, $location, ServiceInstanceService, $stateParams, $state,
          OperationalServiceService, TechnicalServiceService, leafletData, mapService, MAP_DEFAULTS, $modal, $timeout) {

        var serviceLayer = new L.FeatureGroup();

        angular.extend($scope, {
          center: {
            // FIXME: get current position from browser instead of using pos of LONDON
            lat: 51.505,
            lng: -0.09,
            zoom: 4
          },
          map: {
            defaults: MAP_DEFAULTS.STATIC
          },
          events: {
            map: {
              enable: ['click'],
              logic: 'emit'
            }
          },
          viewState: 'create',
        });

        // add layers to map and add a draw-listener
        leafletData.getMap("instanceCreateMap").then(function (map) {
          map.addLayer(serviceLayer);
          map.on('click', clickEventHandler);
        });

        angular.extend($scope, {
          //isCreateState: function(){ return $scope.viewState === 'create'},
          isCreateState: function () {
            return $state.current.data.createState;
          },
          isEditState: function () {
            return $state.current.data.editState;
          },
          message: null,
          alertMessages: null,
          selectedOperationalService: null,
          selectedSpecification: null,
          operationalServices: OperationalServiceService.query(),
          service: {
            provider: {
              name: $stateParams.organizationname
            },
            key: {
              providerId: $stateParams.organizationname,
            },
            id: null,
            name: null,
            description: null,
            coverage: []
          },
          formIsSubmitable: function () {
            return ($scope.service.id && $scope.service.name /*&& $scope.service.coverage*/);
          },
          submit: function () {
            $scope.service.specification = $scope.selectedSpecification;
            $scope.service.key = {
              specificationId: $scope.selectedSpecification.id,
              providerId: $stateParams.organizationname,
              instanceId: $scope.service.id
            };

            $scope.alertMessages = null;
            $scope.message = "Sending request to register service instance...";

            if ($scope.isEditState()) {
              $scope.service.$save(function (result) {
                $location.path('/orgs/' + $scope.service.provider.name).replace();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });

            } else {
              ServiceInstanceService.create($scope.service, function (result) {
                $location.path('/orgs/' + $scope.service.provider.name).replace();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });
            }
          }
        });

        if ($scope.isEditState()) {
          $scope.service = ServiceInstanceService.get({serviceInstanceId: $stateParams.serviceInstanceId});
          // FIXME: should lookup value based on id $scope.service.specificationId
          $scope.selectedSpecification = $scope.service.specification;
          // FIXME: should lookup value based on id $scope.selectedSpecification.operationalServiceId
          $scope.selectedOperationalService = $scope.service.specification.operationalService;
          showService();
          console.log('SERVICE', $scope.service);
        }

        $scope.$watch('selectedOperationalService', function (selectedOperationalService) {
          $scope.specifications = selectedOperationalService ? TechnicalServiceService.query(selectedOperationalService.id) : [];
        });

        function clickEventHandler(event) {
          $scope.openCoverageEditor();
        }

        function showService() {
          // Cleanup
          serviceLayer.clearLayers();
          // Rebuild
          serviceLayer.addLayer(L.featureGroup(mapService.servicesToLayers([$scope.service], function (featureGroup) {
            featureGroup.on('click', clickEventHandler);
          })));
          serviceLayer.setStyle(mapService.Styles.STATIC);
          fitToLayer(serviceLayer);
        }

        function fitToLayer(layer) {
          leafletData.getMap('instanceCreateMap').then(function (map) {
            if (layer) {
              map.fitBounds(layer.getBounds());
            }
          });
        }

        $scope.openCoverageEditor = function () {
          $modal.open({
            templateUrl: 'organizations/services/coverageEditor.html',
            controller: 'CoverageEditorController',
            size: 'lg',
            backdrop: 'static',
            resolve: {
              coverage: function () {
                return $scope.service.coverage;
              }
            }
          }).result.then(function (result) {
            // coverage graph submitted
            $scope.service.coverage = result;
            showService();
          }, function () {
            // dialog dismissed (user pressed CANCEL or ESCAPE)
          });
        };

      }])

    .controller('CoverageEditorController', ['$scope', 'leafletData', 'mapService', 'coverage',
      function ($scope, leafletData, mapService, coverage) {

        var options = mapService.createDrawingOptions(),
            drawControl = new L.Control.Draw(options),
            serviceLayer = options.edit.featureGroup;

        angular.extend($scope, {
          center: {
            // FIXME: get current position from browser instead of using pos of LONDON
            lat: 51.505,
            lng: -0.09,
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

      }]);
