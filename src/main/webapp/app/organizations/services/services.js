'use strict';

angular.module('mcp.organizations.services', [])

    .controller('ServiceInstanceDetailsController', ['$scope', 'mapService', 'leafletData', '$timeout',
      function ($scope, mapService, leafletData, $timeout) {
        console.log('$scope.service ', $scope.service);

        angular.extend($scope, {
          map: {
            defaults: {
              scrollWheelZoom: false,
              zoomControl: false,
              attributionControl: false,
              zoomAnimation: false
            },
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
          fitToPaths('map-' + $scope.$index);
        }, 0);

        function fitToPaths(mapId) {
          leafletData.getMap(mapId).then(function (map) {
            mapService.fitToGeomitryLayers(map);
          });
        }

      }])

    .controller('ServiceInstanceCreateController', ['$scope', '$location', 'ServiceInstanceService', '$stateParams',
      'OperationalServiceService', 'TechnicalServiceService', 'leafletData', 'mapService', '$modal',
      function ($scope, $location, ServiceInstanceService, $stateParams,
          OperationalServiceService, TechnicalServiceService, leafletData, mapService, $modal) {

        var options = mapService.createDrawingOptions(),
            drawnItems = options.edit.featureGroup;

        angular.extend($scope, {
          center: {
            // FIXME: get current position from browser instead of using pos of LONDON
            lat: 51.505,
            lng: -0.09,
            zoom: 4
          },
          latlongs: []
        });

        leafletData.getMap("instanceCreateMap").then(function (map) {
          map.addLayer(drawnItems);
        });

        angular.extend($scope, {
          message: null,
          alertMessages: null,
          selectedOperationalService: null,
          selectedSpecification: null,
          operationalServices: OperationalServiceService.query(),
          map: {
            paths: mapService.shapesToPaths([])
          },
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

            ServiceInstanceService.create($scope.service, function (result) {
              $location.path('/orgs/' + $scope.service.provider.name).replace();
            }, function (error) {
              $scope.message = null;
              $scope.alertMessages = ["Error on the serverside :( ", error];
            });
          }
        });

        $scope.$watch('selectedOperationalService', function (selectedOperationalService) {
          $scope.specifications = selectedOperationalService ? TechnicalServiceService.query(selectedOperationalService.id) : [];
        });

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
            $scope.map.paths = mapService.shapesToPaths($scope.service.coverage);

          }, function () {

            // dialog dismissed (user pressed CANCEL or ESCAPE)

          });
        };


      }])

    .controller('CoverageEditorController', ['$scope', 'leafletData', 'mapService', 'coverage',
      function ($scope, leafletData, mapService, coverage) {

        // This service expects the scope to inherit a property "service" containing a property "coverage" consisting of an array of shapes!  

        var options = mapService.createDrawingOptions(),
            drawnItems = options.edit.featureGroup,
            drawControl = new L.Control.Draw(options);

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
          drawnItems.addLayer(layer);
        });

        // add layers to map and add a draw-listener
        leafletData.getMap("coverageEditorMap").then(function (map) {
          map.addLayer(drawnItems);

          // FIXME: when angular leaflet 0.7.9 is released use this instead:
          //var drawnItems = $scope.controls.draw.edit.featureGroup;

          map.on('draw:created', function (e) {
            var layer = e.layer;
            drawnItems.addLayer(layer);
          });
        });

        angular.extend($scope, {
          formIsSubmitable: function () {
            return (drawnItems.getLayers().length);
          },
          submit: function () {
            $scope.$close(mapService.layersToShapes(drawnItems.getLayers()));
          }
        });

      }]);
