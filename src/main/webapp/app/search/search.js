'use strict';

angular.module('mcp.search.services', ['leaflet-directive', 'mcp.mapservices'])

    .controller('SearchServiceMapController', ['$scope', 'mapService', 'leafletData', '$timeout', 'ServiceInstanceService',
      function ($scope, mapService, leafletData, $timeout, ServiceInstanceService) {

        angular.extend($scope, {
          services: ServiceInstanceService.query(),
          swindowHeight: 600,
          velementWidth: 123
        });

        angular.extend($scope, {
          map: {
            defaults: {
              scrollWheelZoom: true,
              zoomControl: true,
              attributionControl: true,
              zoomAnimation: true
            },
            paths: {} //mapService.shapesToPaths($scope.service.coverage)
          },
          events: {
            map: {
              enable: ['click'],
              logic: 'emit'
            }
          },
          servicesLayer: servicesToLayers($scope.services)
        });

        $scope.$on('leafletDirectiveMap.click', function (event) {
          console.log("Event click: ", event);
        });

        // register a timeout that will fit (position and zoom) the map to its paths
        $timeout(function () {
          fitToPaths("searchmap");
        }, 100);

        function servicesToLayers(services) {
          // associative map 
          //var servicesAsLayers = {};

          var servicesLayer = L.layerGroup();

          // iterate services, and for each, convert its shapes to layers and 
          // add it to a layerGroup, finally add the layerGroup to the array-object 
          services.forEach(function (service) {
            var layerGroup = L.layerGroup();
            service.coverage.forEach(function (shape) {
              layerGroup.addLayer(mapService.shapeToLayer(shape));
            });
            //servicesAsLayers[service.provider.id + '#' + service.id] = layerGroup;
            servicesLayer.addLayer(layerGroup);
          });

          return servicesLayer;
        }

        leafletData.getMap("searchmap").then(function (map) {
          map.addLayer($scope.servicesLayer);
        });

        function fitToPaths(mapId) {
          leafletData.getMap(mapId).then(function (map) {
            mapService.fitToGeomitryLayers(map);
          });
        }

      }])
    ;
//    .controller('ServiceInstanceCreateController', ['$scope', '$location', 'OrganizationService', '$stateParams',
//      'OperationalServiceService', 'TechnicalServiceService', 'leafletData', 'mapService',
//      function($scope, $location, OrganizationService, $stateParams,
//          OperationalServiceService, TechnicalServiceService, leafletData, mapService) {
//
//        var options = mapService.createDrawingOptions(),
//            drawnItems = options.edit.featureGroup,
//            drawControl = new L.Control.Draw(options);
//
//        angular.extend($scope, {
//          center: {
//            // FIXME: get current position from browser instead of using pos of LONDON
//            lat: 51.505,
//            lng: -0.09,
//            zoom: 4
//          },
//          controls: {
//            custom: [drawControl]
//          },
//          latlongs: []
//        });
//
//        leafletData.getMap().then(function(map) {
//          map.addLayer(drawnItems);
//
//          // FIXME: when angular leaflet 0.7.9 is released use this instead:
//          //var drawnItems = $scope.controls.draw.edit.featureGroup;
//
//          map.on('draw:created', function(e) {
//            var layer = e.layer;
//            drawnItems.addLayer(layer);
//            console.log(JSON.stringify(layer.toGeoJSON()));
//          });
//        });
//
//        angular.extend($scope, {
//          message: null,
//          alertMessages: null,
//          selectedOperationalService: null,
//          selectedSpecification: null,
//          operationalServices: OperationalServiceService.query(),
//          service: {
//            provider: {
//              name: $stateParams.organizationname
//            },
//            key: {
//              specificationId: "imo-mis-rest",
//              providerId: $stateParams.organizationname,
//              instanceId: 'test'
//            },
//            id: null,
//            name: null,
//            description: null,
//            coverage: []
//          },
//          formIsSubmitable: function() {
//            return ($scope.service.id && $scope.service.name /*&& $scope.service.coverage*/);
//          },
//          submit: function() {
//            $scope.service.coverage = mapService.layersToShapes(drawnItems.getLayers());
//            $scope.service.specification = $scope.selectedSpecification;
//
//            $scope.service.key = {
//              specificationId: $scope.selectedSpecification.id,
//              providerId: $stateParams.organizationname,
//              instanceId: $scope.service.id
//            };
//
//            $scope.alertMessages = null;
//            $scope.message = "Sending request to register service instance...";
//            
//            OrganizationService.registerServiceInstance($scope.service, function(result) {
//              $location.path('/orgs/' + $scope.service.provider.name).replace();
//            }, function(error) {
//              $scope.message = null;
//              $scope.alertMessages = ["Error on the serverside :( ", error];
//            });
//          }
//        });
//
//        $scope.$watch('selectedOperationalService', function(selectedOperationalService) {
//          $scope.specifications = selectedOperationalService ? TechnicalServiceService.query(selectedOperationalService.id) : [];
//        });
//      }]);
