'use strict';

angular.module('mcp.search.services', [])

    .controller('SearchServiceMapController', ['$scope', '$filter', 'mapService', 'leafletData', 'ServiceInstanceService', 'searchServiceFilterModel',
      function ($scope, $filter, mapService, leafletData, ServiceInstanceService, searchServiceFilterModel) {

        var SEARCHMAP_ID = 'searchmap';

        $scope.allServices = ServiceInstanceService.query();
        $scope.filter = searchServiceFilterModel.filter;
        $scope.data = searchServiceFilterModel.data;

        angular.extend($scope, {
          element: {},
          filterLocation: {
            lat: $scope.filter.location ? $scope.filter.location.lat : 51,
            lng: $scope.filter.location ? $scope.filter.location.lng : 0,
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

        function filterServices(filterBy) {

          //FIXME: should delegate to server instead (...or at least in advance)

          var allServices = $scope.allServices;
          var services = [];

          // filter by predefined criterias (see match function)
          allServices.forEach(function (service) {
            if (match(service, filterBy)) {
              services.push(service);
            }
          });

          if (filterBy.anyText) {
            var searchFilter = {$: filterBy.anyText};
            services = $filter('filter')(services, searchFilter, false)
          }

          // filter services to those that contains the filterLocation
          if (filterBy.location)
            services = mapService.filterServicesAtLocation(filterBy.location, services);

          return services;
        }

        function match(service, filter) {

          if (filter.provider && service.providerId !== filter.provider.organizationId)
            return false;


          if (filter.serviceType && service.specification.serviceType !== filter.serviceType)
            return false;

          return true;
        }

        function filterChanged() {
          updateLocationMarker();
          filterAndShowServices();
        }

        function updateLocationMarker() {
          if (!$scope.filter.location)
            $scope.clearFilterlocation();
        }

        function filterAndShowServices() {
          $scope.services = filterServices($scope.filter);

          // share the result with the service filter
          $scope.data.result = $scope.services;

          // update marker info
          $scope.filterLocation.message = "" + $scope.services.length + " services near this location";

          showServices($scope.services);

          // Autoselect single service
          if ($scope.services.length === 1) {
            $scope.unselectService();
            if ($scope.filter.location)
              $scope.selectService($scope.services[0]);
          }

          // Clear selected service if not in list
          if ($scope.selectedService) {
            if ($scope.services.indexOf($scope.selectedService) === -1) {
              $scope.unselectService();
            } else {
              console.log($scope.filter.location);
              // when location marker is placed we should refresh the selection of the service layer 
              var service = $scope.selectedService;
              serviceLayer(service).select();
            }
          }

        }

        function showServices(servicesAtLocation) {
          // Cleanup
          $scope.servicesLayerMap = {};
          $scope.servicesLayer.clearLayers();
          // Rebuild
          $scope.servicesLayer.addLayer(L.featureGroup(mapService.servicesToLayers(servicesAtLocation, featureGroupCallback)));
          fitToSelectedLayers();
        }

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
          $scope.servicesLayerMap[uniqueId(featureGroup.service)] = featureGroup;
        }

        function serviceLayer(service) {
          return $scope.servicesLayerMap[uniqueId(service)];
        }

        function uniqueId(service) {
          return service.providerId + '-' + service.specificationId;
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
            $scope.unselectService();
          } else {
            $scope.selectService(service);
          }
        };

        $scope.unselectService = function () {
          if ($scope.selectedService) {

            // unselect prevous service layer
            if (serviceLayer($scope.selectedService))
              serviceLayer($scope.selectedService).unselect();

            $scope.selectedService = null;
            fitToSelectedLayers();
          }
        };

        $scope.selectService = function (service) {
          // unselect prevous service
          if ($scope.selectedService && serviceLayer($scope.selectedService))
            serviceLayer($scope.selectedService).unselect();

          if (service !== $scope.selectedService) {
            $scope.selectedService = service;
            serviceLayer(service).select();
            fitToLayer(serviceLayer(service));
          }
        };

        $scope.highlightService = function (service) {
          $scope.highlightedService = service;
          serviceLayer(service).highlight();
        };

        $scope.unhighlightService = function (service) {
          $scope.highlightedService = null;
          serviceLayer(service).unhighlight();
        };

        if ($scope.filter.location) {
           $scope.moveFilterLocation($scope.filter.location);
        }

        filterAndShowServices();

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
    .service('searchServiceFilterModel', function (OperationalServiceService, OrganizationService) {

      this.data = {
        operationalServices: OperationalServiceService.query(),
        serviceSpecifications: null,
        organizations: OrganizationService.query(),
        serviceTypes: {
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
        },
        result: []
      };

      this.filter = {
        anyText: null,
        location: null,
        operationalService: null,
        provider: null,
        serviceSpecification: null,
        serviceType: null
      };

      this.clean = function () {
        delete this.filter.anyText;
        delete this.filter.location;
        delete this.filter.operationalService;
        delete this.filter.provider;
        delete this.filter.serviceSpecification;
        delete this.filter.serviceType;
      };

    });

;
