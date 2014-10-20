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
      'OperationalServiceService', 'TechnicalServiceService', 'leafletData', 'mapService',
      function ($scope, $location, ServiceInstanceService, $stateParams,
          OperationalServiceService, TechnicalServiceService, leafletData, mapService) {

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

        leafletData.getMap("instanceCreateMap").then(function (map) {
          map.addLayer(drawnItems);

          // FIXME: when angular leaflet 0.7.9 is released use this instead:
          //var drawnItems = $scope.controls.draw.edit.featureGroup;

          map.on('draw:created', function (e) {
            var layer = e.layer;
            drawnItems.addLayer(layer);
            console.log(JSON.stringify(layer.toGeoJSON()));
          });
        });

        angular.extend($scope, {
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
              specificationId: "imo-mis-rest",
              providerId: $stateParams.organizationname,
              instanceId: 'test'
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
            $scope.service.coverage = mapService.layersToShapes(drawnItems.getLayers());
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
      }]);
