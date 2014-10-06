'use strict';

angular.module('mcp.search.services', ['leaflet-directive', 'mcp.mapservices'])

    .controller('SearchServiceMapController', ['$scope', 'mapService', 'leafletData', '$timeout', 'ServiceInstanceService',
      function ($scope, mapService, leafletData, $timeout, ServiceInstanceService) {

        var SEARCHMAP_ID = 'searchmap';

        $scope.allServices = ServiceInstanceService.query();
        
        angular.extend($scope, {
          element: {},
          filterLocation: {
            lat: 51,
            lng: 0,
            draggable: false,
            message: "",
            focus: false
          },
          mouseLocation: {lat: 0, lng: 0},
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
          services: $scope.allServices,
          selectedService: null,
          highlightedService: null,
          servicesLayer: L.featureGroup(),
          servicesLayerMap: {}
        });

        showServices($scope.services);

        function featureGroupCallback(featureGroup) {
          
          // (called whenever servicesToLayers creates a layer)
          featureGroup.on('click', clickEventHandler);
          featureGroup.on('mousemove', mouseMoveEventHandler);
          featureGroup.on('mouseover', function (e) {
            $scope.highlightService(e.target.service);
          });
          featureGroup.on('mouseout', function (e) {
            $scope.unhighlightService(e.target.service);
          });
          $scope.servicesLayerMap[featureGroup.service.id] = featureGroup;
        }

        $scope.clearFilterlocation = function () {
          delete $scope.markers.filterLocation;

          // reset to show all services
          $scope.selectedService = null;
          $scope.services = $scope.allServices;
          showServices($scope.services);
        };

        $scope.moveFilterLocation = function (latlng) {
          if (!$scope.markers.filterLocation)
            $scope.markers.filterLocation = $scope.filterLocation;
          $scope.filterLocation.lat = latlng.lat;
          $scope.filterLocation.lng = latlng.lng;

          // filter services to those that contains the filterLocation
          $scope.services = mapService.filterServicesAtLocation(latlng, $scope.allServices);

          // show services that are reachable 
          showServices($scope.services);
          $scope.filterLocation.message = "" + $scope.services.length + " services near this location";

          // Clear any previously selected service
          $scope.selectedService = null;

          // Autoselect single service
          if ($scope.services.length === 1)
            $scope.selectedService = $scope.services[0];
        };

        function showServices(servicesAtLocation) {
          // Cleanup
          $scope.servicesLayerMap = {};
          $scope.servicesLayer.clearLayers();
          // Rebuild
          $scope.servicesLayer.addLayer(L.featureGroup(mapService.servicesToLayers(servicesAtLocation, featureGroupCallback)));
          fitToSelectedLayers();
        }

        function clickEventHandler(e) {
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
          if ($scope.services.length)
            fitToLayer($scope.servicesLayer);
        }

        function fitToLayer(layer) {
          leafletData.getMap(SEARCHMAP_ID).then(function (map) {
            if (layer) {
              map.fitBounds(layer.getBounds(), {paddingBottomRight: [$scope.selectedService ? $scope.element.offsetWidth : 0, 0]});
            }
          });
        }

        $scope.toggleSelectService = function (service) {
          if (service === $scope.selectedService) {
            $scope.selectedService = null;
            $scope.highlightService(service);
            fitToSelectedLayers();
          } else {
            $scope.selectedService = service;
            fitToLayer($scope.servicesLayerMap[service.id]);
          }
        };

        $scope.highlightService = function (service) {
          $scope.highlightedService = service;
          $scope.servicesLayerMap[service.id].highlight();
        };

        $scope.unhighlightService = function (service) {
          $scope.highlightedService = null;
          $scope.servicesLayerMap[service.id].resetStyle();
        };


        $scope.$on('leafletDirectiveMap.click', function (event, args) {
          console.log("Event click: ", event, args);
          $scope.moveFilterLocation(args.leafletEvent.latlng);
        });

        leafletData.getMap(SEARCHMAP_ID).then(function (map) {
          map.addLayer($scope.servicesLayer);
          map.on('mousemove', mouseMoveEventHandler);
        });

      }])
    ;
