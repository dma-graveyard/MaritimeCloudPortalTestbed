'use strict';

var fitToPaths = function fitToPaths(mapId, leafletData, mapService) {
  leafletData.getMap(mapId).then(function (map) {
    mapService.fitToGeomitryLayers(map);
  });
};

angular.module('mcp.organizations.services', [])

    .controller('ServiceInstanceDetailsController', ['$scope',
      function ($scope) {
        $scope.details = {isCollapsed: true};
        $scope.toggleDetails = function () {
          $scope.details.isCollapsed = !$scope.details.isCollapsed;
        };
      }])

    .controller('EditServiceInstanceController', ['$scope', '$location', '$modal', '$stateParams', '$state',
      'OperationalServiceService', 'ServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $location, $modal, $stateParams, $state,
          OperationalServiceService, ServiceSpecificationService, ServiceInstanceService) {

        angular.extend($scope, {
          map: {},
          message: null,
          alertMessages: null,
          selectedOperationalService: null,
          selectedSpecification: null,
          operationalServices: OperationalServiceService.query(),
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
            coverage: []
          },
          formIsSubmitable: function () {
            return ($scope.service.serviceInstanceId && $scope.service.name /*&& $scope.service.coverage*/);
          },
          submit: function () {
            $scope.providerId = $stateParams.organizationId;
            $scope.service.specificationId = $scope.selectedSpecification.serviceSpecificationId;

            $scope.alertMessages = null;
            $scope.message = "Sending request to register service instance...";

            if ($scope.isEditState()) {
              $scope.service.$save(function (result) {
                $location.path('/orgs/' + $scope.service.providerId).replace();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });

            } else {
              ServiceInstanceService.create($scope.service, function (result) {
                $location.path('/orgs/' + $scope.service.providerId).replace();
              }, function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              });
            }
          }
        });

        if ($scope.isEditState()) {
          $scope.service = ServiceInstanceService.get({organizationId: $stateParams.organizationId, serviceInstanceId: $stateParams.serviceInstanceId});
// FIXME: should lookup value based on id $scope.service.specificationId
          $scope.selectedSpecification = ServiceSpecificationService.get({serviceSpecificationId: $scope.service.specificationId});

// FIXME: should lookup value based on id $scope.selectedSpecification.operationalServiceId
//          $scope.selectedOperationalService = OperationalServiceService.query({operationalServiceId: $scope.selectedSpecification.operationalServices[0]});
        }

        $scope.services = [$scope.service];

        $scope.$watch('selectedOperationalService', function (selectedOperationalService) {
          $scope.specifications = selectedOperationalService ? ServiceSpecificationService.query(
              {operationalServiceId: selectedOperationalService.id}) : [];
        });

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
