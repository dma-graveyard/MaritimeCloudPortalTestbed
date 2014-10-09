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
        $scope.organizations = searchServiceFilterModel.data.organizations;
        $scope.transportTypes = searchServiceFilterModel.data.transportTypes;
        $scope.filter = searchServiceFilterModel.filter;
        $scope.data = searchServiceFilterModel.data;

        $scope.setFilterByOperationalService = function (selection) {

          // rebuild specification filter options
          $scope.specifications = selection ? TechnicalServiceService.query(selection.id) : null;

          // reset specification filter if cleaned
          if (!selection)
            delete $scope.filter.technicalSpecification;
        };

        $scope.isDirty = function () {
          return $scope.filter.operationalService
              || $scope.filter.technicalSpecification
              || $scope.filter.transportType
              || $scope.filter.provider
              || $scope.filter.anyText
              || $scope.filter.location;
        };

        $scope.clearFilter = function () {
          $scope.setFilterByOperationalService(null);
          searchServiceFilterModel.clean();
        };

        $scope.$watch('state.is("restricted.searchServiceMap")', function (newValue, oldValue) {
           $scope.isOpen.findService = newValue;
        });

      }])
    ;

