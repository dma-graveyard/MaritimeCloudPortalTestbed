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
          },
          openDetails: function () {
            console.log('clicked');
            ;
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

    .controller('EditServiceInstanceController', ['$scope', '$location', 'ServiceInstanceService', '$stateParams', '$state',
      'OperationalServiceService', 'TechnicalServiceService', 'leafletData', 'mapService', 'MAP_DEFAULTS', '$modal',
      function ($scope, $location, ServiceInstanceService, $stateParams, $state,
          OperationalServiceService, TechnicalServiceService, leafletData, mapService, MAP_DEFAULTS, $modal) {

        var serviceLayer = new L.FeatureGroup(),
            instanceMap;

        angular.extend($scope, {
          center: {
            lat: 51,
            lng: 0,
            zoom: 4
          },
          map: {
            defaults: MAP_DEFAULTS.STATIC
          },
          mapHandle: {},
          events: {
            map: {
              enable: ['click'],
              logic: 'emit'
            }
          },
          viewState: 'create',
          clickEventHandler: clickEventHandler
        });


        // add layers to map and add a draw-listener
        leafletData.getMap("instanceEditorMap").then(function (map) {
          map.addLayer(serviceLayer);
          map.on('click', clickEventHandler);

          // zoom to current location when no coverage graph
          if (!$scope.isEditState())
            map.locate({setView: true, maxZoom: 7});
          instanceMap = map;
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
              providerId: $stateParams.organizationname
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

        $scope.services = [$scope.service];

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
          leafletData.getMap('instanceEditorMap').then(function (map) {
            if (layer) {
              map.fitBounds(layer.getBounds());
            }
          });
        }

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
                //return {bounds: instanceMap.getBounds()};
                console.log('$scope.mapHandle', $scope.mapHandle);
                return {bounds: $scope.map.handle.getBounds()};
              }
            }
          }).result.then(function (result) {
            // coverage graph submitted
            $scope.service.coverage = result;
//            serviceLayer = $scope.map.group;
            $scope.map.rebuild();
//            showService();
          }, function () {
            // dialog dismissed (user pressed CANCEL or ESCAPE)
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


    .directive('thumbnailMap', function (mapService) {
      return {
        priority: 0,
        restrict: 'E',
        replace: false,
        scope: {
          services: "=",
          map: "=",
          onClick: "&" // " on-click='handle(...)' "
        },
        link: function (scope, element, attrs) {
          var group = new L.FeatureGroup(),
              map = L.map(element[0], {
                center: [40, -86],
                zoom: 4,
                attributionControl: false,
                boxZoom: false, 
                doubleClickZoom: false,
                dragging: false,
                keyboard: false,
                scrollWheelZoom: false,
                tap: false, 
                touchZoom: false,
                zoomAnimation: false,
                zoomControl: false
              });

          function populateServicesGroup(group, services) {
            group.clearLayers();
            group.addLayer(L.featureGroup(mapService.servicesToLayers(services, function (featureGroup) {
              // add click handler to layers
              featureGroup.on('click', scope.onClick);
            })));
            group.setStyle(mapService.Styles.STATIC);
          }

          function fitToContent(group, services) {

            // (a service can be new, that is, has no coverage defined yet)
            var isNewService = services.length === 1 && services[0].coverage.length === 0;
            
            if (isNewService) {
              map.locate({setView: true, maxZoom: 7});
            } else {
              map.fitBounds(group.getBounds());
            }
          }

          function rebuild() {
            populateServicesGroup(group, scope.services);
            fitToContent(group, scope.services);
          }

          rebuild();

          // Add click-handler to map
          map.on('click', scope.onClick);
          L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
          }).addTo(map);
          group.addTo(map);

          // expose the map and group layer to the outer scope 
          // as $scope.map.handle and $scope.map.group
          if (scope.map) {
            scope.map.handle = map;
            scope.map.group = group;
            scope.map.rebuild = rebuild;
          }
        }

      };
    });
;
