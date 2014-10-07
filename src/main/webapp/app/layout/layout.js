'use strict';

/* Controllers */

angular.module('mcp.layout', [])

    .controller('SidebarController', ['$scope', '$state', 'searchServiceFilterModel', 'TechnicalServiceService',
      function ($scope, $state, searchServiceFilterModel, TechnicalServiceService) {

        // holder for accordion header 'open status' properties 
        $scope.isOpen = {
          findService: true//$state.is('searchServiceMap')
        };

        $scope.filter = {
        };

        $scope.state = $state;

        // Search Map Filters

        $scope.operationalServices = searchServiceFilterModel.data.operationalServices;
        $scope.specifications = null;
        $scope.transportTypes  = searchServiceFilterModel.data.transportTypes;

        $scope.setFilterByOperationalService = function (selection) {
          searchServiceFilterModel.setOperationalService(selection);
          $scope.filter.selectedOperationalService = selection;

          // rebuild specification filter options
          $scope.specifications = selection ? TechnicalServiceService.query(selection.id) : null;

          // reset specification filter if cleaned
          if (!selection)
            $scope.setFilterByTechnicalSpecification(null);
        };

        $scope.setFilterByTechnicalSpecification = function (selection) {
          searchServiceFilterModel.setTechnicalSpecification(selection);
          $scope.filter.selectedSpecification = selection;
        };

        $scope.setFilterByTransportType = function (selection) {
          console.log(selection);
          searchServiceFilterModel.setTransportType(selection);
          $scope.filter.selectedTransportType = selection;
        };

        $scope.$watch('state.is("restricted.searchServiceMap")', function (newValue, oldValue) {
           $scope.isOpen.findService = newValue;
        });

        $scope.isDirty = function () {
          return $scope.filter.selectedOperationalService || $scope.filter.selectedSpecification || $scope.filter.selectedTransportType;
        };

        $scope.clearFilter = function () {
          $scope.setFilterByOperationalService(null);
          $scope.setFilterByTransportType(null);
        };
      }])
    ;

