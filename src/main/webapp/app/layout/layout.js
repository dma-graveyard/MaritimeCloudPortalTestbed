'use strict';

/* Controllers */

angular.module('mcp.layout', [])

    .controller('SidebarController', ['$scope', '$state', 'searchServiceFilterModel', 'ServiceSpecificationService',
      function ($scope, $state, searchServiceFilterModel, ServiceSpecificationService) {

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
        $scope.serviceTypes = searchServiceFilterModel.data.serviceTypes;
        $scope.filter = searchServiceFilterModel.filter;
        $scope.data = searchServiceFilterModel.data;

        $scope.setFilterByOperationalService = function (selectedOpreationalService) {

          // rebuild specification filter options
          $scope.specifications = selectedOpreationalService ? ServiceSpecificationService.query({
                operationalServiceId : selectedOpreationalService.id
              }) : null;

          // reset specification filter if not in current set
          if ($scope.specifications && $scope.specifications.indexOf($scope.filter.serviceSpecification) === -1){
            delete $scope.filter.serviceSpecification;
          }
        };

        $scope.isDirty = function () {
          return $scope.filter.operationalService
              || $scope.filter.serviceSpecification
              || $scope.filter.serviceType
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

