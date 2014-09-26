'use strict';

angular.module('mcp.search.services', ['leaflet-directive', 'mcp.mapservices'])

    .controller('SearchServiceMapController', ['$scope', 'mapService', 'leafletData', '$timeout', 'ServiceInstanceService',
      function ($scope, mapService, leafletData, $timeout, ServiceInstanceService) {

        var SEARCHMAP_ID = 'searchmap';

        angular.extend($scope, {
          allServices: ServiceInstanceService.query(),
          filterLocation: {
            lat: 51,
            lng: 0,
            //focus: true,
            //message: "Hey, drag me if you want",
            draggable: true
          },
          mouseLocation: {lat: 0, lng: 0}
        });

        angular.extend($scope, {
          services: $scope.allServices,
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
              enable: ['click', 'mousemove'],
              logic: 'emit'
            }
          },
          markers: {
          },
          servicesLayer: L.featureGroup()
        });
        
        showServices($scope.services);

        function featureGroupCallback(featureGroup) {
          // (called whenever servicesToLayers creates a layer)
          featureGroup.on('click', clickEventHandler);
          featureGroup.on('mousemove', mouseMoveEventHandler);
        }
        
        $scope.clearSelection = function () {
          
          delete $scope.markers.filterLocation;
          
          // filter services to those that contains the filterLocation
          $scope.services = $scope.allServices;

          // show services that are reachable 
          showServices($scope.services);
        }

        $scope.moveFilterLocation = function (latlng) {
          if (!$scope.markers.filterLocation)
            $scope.markers.filterLocation = $scope.filterLocation;
          $scope.filterLocation.lat = latlng.lat;
          $scope.filterLocation.lng = latlng.lng;

          // filter services to those that contains the filterLocation
          $scope.services = mapService.filterServicesAtLocation(latlng, $scope.allServices);

          // show services that are reachable 
          showServices($scope.services);
        };

        function showServices(servicesAtLocation) {
          $scope.servicesLayer.clearLayers();
          $scope.servicesLayer.addLayer(L.featureGroup(mapService.servicesToLayers(servicesAtLocation, featureGroupCallback)));
          fitToSelectedLayers();
        }

        function clickEventHandler(e) {

          e.target.setStyle({
            color: 0
          });

          $scope.moveFilterLocation(e.latlng);
          $scope.$apply();
        }

        function mouseMoveEventHandler(e) {
          // update mouse location
          $scope.mouseLocation = e.latlng;
          $scope.$apply();

          // show distance in meters to filterMarker
          $scope.distance = e.latlng.distanceTo($scope.filterLocation);
        }

        function fitToSelectedLayers() {
          leafletData.getMap(SEARCHMAP_ID).then(function (map) {
            if ($scope.services.length)
              map.fitBounds($scope.servicesLayer.getBounds());
          });
        }

        $scope.$on('leafletDirectiveMap.click', function (event, args) {
          console.log("Event click: ", event, args);
          $scope.moveFilterLocation(args.leafletEvent.latlng);
        });

//        // register a timeout that will fit (position and zoom) the map to its paths
//        $timeout(function () {
//          fitToSelectedLayers();
//        }, 100);

        leafletData.getMap(SEARCHMAP_ID).then(function (map) {
          map.addLayer($scope.servicesLayer);
          map.on('mousemove', mouseMoveEventHandler);
        });

//        function servicesToLayers(services) {
//          // associative map 
//          //var servicesAsLayers = {};
//
//          var servicesLayer = L.layerGroup();
//
//          // iterate services, and for each, convert its shapes to layers and 
//          // add it to a layerGroup, finally add the layerGroup to the array-object 
//          services.forEach(function (service) {
//            var featureGroup = L.featureGroup();
//            featureGroup.service = service;
//            featureGroup.on('click', clickEventHandler);
//            featureGroup.on('mousemove', mouseMoveEventHandler);
//            service.coverage.forEach(function (shape) {
//              featureGroup.addLayer(mapService.shapeToLayer(shape));
//            });
//            servicesLayer.addLayer(featureGroup);
//          });
//
//          return servicesLayer;
//        }

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
