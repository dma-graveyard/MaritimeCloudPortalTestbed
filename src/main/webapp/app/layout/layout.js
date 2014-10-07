'use strict';

/* Controllers */

angular.module('mcp.layout', [])

    .controller('SidebarController', ['$scope', '$state', 'searchServiceFilterModel',
      function($scope, $state, searchServiceFilterModel) {
        
        // holder for accordion header 'open status' properties 
        $scope.isOpen = {
        };
        
        // Search Map Filters
        $scope.operationalServices = searchServiceFilterModel.data.operationalServices;
        $scope.selectedOperationalService = null;
        $scope.setFilterByOperationalService = function(selection){
          searchServiceFilterModel.setOperationalService(selection);
        };
        
      }])
    ;

