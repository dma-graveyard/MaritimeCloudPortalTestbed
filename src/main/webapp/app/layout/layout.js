'use strict';

/* Controllers */

angular.module('mcp.layout', [])

    .controller('SidebarController', ['$scope', '$state',
      function($scope, $state) {
        
        // holder for accordion header 'open status' properties 
        $scope.isOpen = {
        };
        
      }])
    ;

