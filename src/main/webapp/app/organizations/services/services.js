'use strict';
/* Helpers */
function coverageToPaths(areas) { // array of MCP areas : return Array of Leaflet Directive Paths
  var i;
  var paths = {};
  for (i = 0; i < areas.length; i++) {
    paths['p' + i] = parse(areas[i]);
  }
  return paths;
}

function parse(area) {
  var path = {
    weight: 2,
    //color: '#008000',
    //color: '#ff612f',
    fillColor: '#ff69b4'
  };
  if (area.type === 'polygon') {
    path.type = 'polygon';
    path.latlngs = coordsToLatLngs(area.points);
  }
  if (area.type === 'circle') {
    path.type = 'circle';
    path.radius = area.radius;
    path.latlngs = {
      lat: area['center-latitude'],
      lng: area['center-longitude']
    };
  }
  if (!area.type) {
    console.log("unknown area type", area);
    error('unknown area type!');
  }
  return path;
}

/**
 * converts arrays of array based pairs to arrays of objects
 * @param {type} array
 */
function coordsToLatLngs(array) {

  if (array.length === 2 && typeof array[0] === 'number' && typeof array[1] === 'number') {
    return {lat: array[1], lng: array[0]};
  }
  var a = [];
  for (var i = 0; i < array.length; i++) {
    a.push(coordsToLatLngs(array[i]));
  }
  return a;
}


/* Controllers */

angular.module('mcp.organizations.services', ['leaflet-directive'])

    .controller('ServiceInstanceDetailsController', ['$scope', '$stateParams', 'ServiceInstanceService', 'leafletData', '$timeout',
      function($scope, $stateParams, ServiceInstanceService, leafletData, $timeout) {
        console.log('$scope.service ', $scope.service);
        $scope.map = {
          defaults: {
            scrollWheelZoom: false,
            zoomControl: false,
            attributionControl: false,
            zoomAnimation: false
          },
          paths: coverageToPaths($scope.service.coverage)
        };
        $scope.events = {
          map: {
            enable: ['click'],
            logic: 'emit'
          }
        };
        $scope.$on('leafletDirectiveMap.click', function(event) {
          console.log("Event click: ", event);
        });
        // register a timeout that will fit (position and zoom) the map to its paths
        $timeout(function() {
          fitToPaths('map-' + $scope.$index)
        }, 0);

        var fitToPaths = function(mapId) {
          leafletData.getMap(mapId).then(function(map) {
            leafletData.getPaths(mapId).then(function(paths) {
              map.fitBounds(paths['p0'].getBounds());
            });
          });

        }
      }])

//    .controller('SpecificationListController', ['$scope', function($scope) {
//        $scope.specifications = [];
//        $scope.orderProp = 'description';
//      }])
//
//    .controller('SpecificationDetailsController', ['$scope', '$stateParams', 'SpecificationService',
//      function($scope, $stateParams, SpecificationService) {
//        $scope.specification = SpecificationService.get({specificationname: $stateParams.specificationname}, function(specification) {
//        });
//      }])
//
//    .controller('SpecificationCreateController', ['$scope', '$location', 'SpecificationService',
//      function($scope, $location, SpecificationService) {
//        $scope.specification = {name: null, title: null};
//        $scope.message = null;
//        $scope.alertMessages = null;
//        //$("#rPreferredLogin").focus();
//        $scope.isTrue = true;
//        /**
//         * @returns true when there is enough data in the form 
//         * to try to submit it. This is not to say that data is
//         * valid. pressing submit will cause a series of 
//         * validator to be evaluated
//         */
//        $scope.formIsSubmitable = function() {
//          return ($scope.specification.name && $scope.specification.title);
//        };
//        $scope.submit = function() {
//          $scope.message = null;
//          $scope.alertMessages = null;
//          // validate input values
//          if ($scope.specification.name) {
//            if ($scope.specification.name === "test") {
//              $scope.alertMessages = ["Test? You have to be more visionary than that!"];
//              return;
//            }
//          }
//
//          // Send request
//          $scope.message = "Sending request to create specification...";
//          SpecificationService.create($scope.specification, function(data) {
//            $location.path('/orgs/' + data.name).replace();
//            $scope.message = ["Specification created: " + data];
//          }, function(error) {
//            // Error handler code
//            $scope.message = null;
//            $scope.alertMessages = ["Error on the serverside :( ", error];
//          });
//        };
//      }])
//    ;
//
