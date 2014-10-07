'use strict';

angular.module('mcp.search.services', [])

    .controller('SearchServiceMapController', ['$scope', 'mapService', 'leafletData', 'ServiceInstanceService', 'searchServiceFilterModel',
      function ($scope, mapService, leafletData, ServiceInstanceService, searchServiceFilterModel) {

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
          servicesLayerMap: {},
          filter: searchServiceFilterModel.filters
        });

        filterAndShowServices();

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

          // (this change will trigger filter watch!)
          delete $scope.filter.location;

          // reset to show all services
          $scope.selectedService = null;
        };

        $scope.moveFilterLocation = function (latlng) {
          if (!$scope.markers.filterLocation)
            $scope.markers.filterLocation = $scope.filterLocation;
          $scope.filterLocation.lat = latlng.lat;
          $scope.filterLocation.lng = latlng.lng;

          // Clear any previously selected service
          $scope.selectedService = null;

          // (this change will trigger filter watch!)
          $scope.filter.location = latlng;
        };

        function filterServices(byFilter) {

          //FIXME: should delegate to server instead (...or at least in advance)

          var allServices = $scope.allServices;
          var services = [];

          allServices.forEach(function (service) {
            if (match(service, byFilter)) {
              services.push(service);
            }
          });

          // filter services to those that contains the filterLocation
          if (byFilter.location)
            services = mapService.filterServicesAtLocation(byFilter.location, services);

          return services;
        }

        function match(service, filter) {
          
          if (filter.operationalService && service.specification.operationalService.id !== filter.operationalService.id)
            return false;
          
          if (filter.technicalSpecification && service.specification.id !== filter.technicalSpecification.id)
            return false;

          if (filter.transportType && service.specification.transportType !== filter.transportType)
            return false;

          return true;
        }

        function filterChanged() {
          //console.log("Filter changed: ", newValue, oldValue);
          filterAndShowServices();
        }

        function filterAndShowServices() {
          $scope.services = filterServices($scope.filter);

          // update marker info
          $scope.filterLocation.message = "" + $scope.services.length + " services near this location";

          // Autoselect single service
          if ($scope.services.length === 1)
            $scope.selectedService = $scope.services[0];

          showServices($scope.services);
        }

        function showServices(servicesAtLocation) {
          // Cleanup
          $scope.servicesLayerMap = {};
          $scope.servicesLayer.clearLayers();
          // Rebuild
          $scope.servicesLayer.addLayer(L.featureGroup(mapService.servicesToLayers(servicesAtLocation, featureGroupCallback)));
          fitToSelectedLayers();
        }

        function clickEventHandler(e) {
          //console.log('Shape clicked: ', e.latlng);
          $scope.moveFilterLocation(e.latlng);
          $scope.$apply();
        }
        
        $scope.$on('leafletDirectiveMap.click', function (event, args) {
          //console.log("Map clicked: ", event, args);
          $scope.moveFilterLocation(args.leafletEvent.latlng);
        });

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
              //console.log('fit map to services', layer.getBounds());
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

        $scope.$watch('filter', function (newValue, oldValue) {
          filterChanged();
        }, true);

        leafletData.getMap(SEARCHMAP_ID).then(function (map) {
          map.addLayer($scope.servicesLayer);
          map.on('mousemove', mouseMoveEventHandler);
        });

      }])

    // Search Filter Object
    // that holds the various filters supplied by controls in eg. the sidebar and used to filter services
    .service('searchServiceFilterModel', function (OperationalServiceService, SpecificationService) {

      this.data = {
        operationalServices: OperationalServiceService.query(),
        technicalSpecifications: null,
        transportTypes: {
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
        }
      };

      this.filters = {
        operationalService: null,
        technicalSpecification: null,
        transportType: null
      };

      this.setOperationalService = function (operationalService) {
        this.filters.operationalService = operationalService;
      };

      this.setTechnicalSpecification = function (technicalSpecification) {
        this.filters.technicalSpecification = technicalSpecification;
      };

      this.setTransportType = function (transportType) {
        this.filters.transportType = transportType;
      };

    });

;
