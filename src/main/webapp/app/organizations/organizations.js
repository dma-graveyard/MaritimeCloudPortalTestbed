'use strict';

/* Controllers */

angular.module('mcp.organizations', ['ui.bootstrap'])

    .controller('OrganizationMenuController', ['$scope', 'OrganizationContext',
      function($scope, OrganizationContext) {
        
        $scope.organizations = OrganizationContext.list;
        $scope.currentOrganization = OrganizationContext.currentOrganization;
        $scope.OrganizationContext = OrganizationContext;

        $scope.$watch('OrganizationContext.currentOrganization', function(newOrganizationName) {
          $scope.currentOrganization = OrganizationContext.currentOrganization;
        });

      }])

    .controller('OrganizationListController', ['$scope', '$stateParams', 'OrganizationContext',
      function($scope, $stateParams, OrganizationContext) {
        
        $scope.organizations = OrganizationContext.list;
        $scope.currentOrganization = OrganizationContext.currentOrganization;
        $scope.orderProp = 'description';
        $scope.$stateParams = $stateParams;
        
        $scope.isCurrent = function(organization) {
          return organization === $scope.currentOrganization;
        };

        $scope.$watch('$stateParams.organizationname', function(newOrganizationName) {
          OrganizationContext.setCurrentOrganization(newOrganizationName);
          $scope.currentOrganization = OrganizationContext.currentOrganization;
        });

      }])

    .controller('OrganizationDetailsController', ['$scope', '$stateParams', 'OrganizationService', 'SpecificationService', 'ServiceInstanceService',
      function($scope, $stateParams, OrganizationService, SpecificationService, ServiceInstanceService) {
        $scope.organization = OrganizationService.get({organizationname: $stateParams.organizationname}, function(organization) {
        });
        $scope.specifications = SpecificationService.query({organizationname: $stateParams.organizationname}, function(specifications) {
        });
        $scope.serviceInstances = ServiceInstanceService.query({organizationname: $stateParams.organizationname}, function(serviceInstances) {
        });
      }])

    .controller('OrganizationCreateController', ['$scope', '$location', 'OrganizationService',
      function($scope, $location, OrganizationService) {
        $scope.organization = {name: null, title: null};
        $scope.message = null;
        $scope.alertMessages = null;
        //$("#rPreferredLogin").focus();
        $scope.isTrue = true;

        /**
         * @returns true when there is enough data in the form 
         * to try to submit it. This is not to say that data is
         * valid. pressing submit will cause a series of 
         * validator to be evaluated
         */
        $scope.formIsSubmitable = function() {
          return ($scope.organization.name && $scope.organization.title);
        };

        $scope.submit = function() {
          $scope.message = null;
          $scope.alertMessages = null;

          // validate input values
          if ($scope.organization.name) {
            if ($scope.organization.name === "test") {
              $scope.alertMessages = ["Test? You have to be more visionary than that!"];
              return;
            }
          }

          // Send request
          $scope.message = "Sending request to create organization...";

          OrganizationService.create($scope.organization, function(data) {
            $location.path('/orgs/' + data.name).replace();
            $scope.message = ["Organization created: " + data];

          }, function(error) {
            // Error handler code
            $scope.message = null;
            $scope.alertMessages = ["Error on the serverside :( ", error];
          });
        };
      }])

    // OrganizationContext
    // - the list of organizations the user is a member of
    // - the currently selected organization
    .service("OrganizationContext", [function() {

        // Organizations that the current user is a member of
        this.list = [];
        this.currentOrganization = null;

        this.setUsersOrganizations = function(organizations) {
          this.list = organizations;
        };

        this.setCurrentOrganization = function(target) {
          if (angular.isString(target)) {
            this.currentOrganization = this.getOrganizationByName(target);
          } else {
            if (this.containsOrganization(target)) {
              this.currentOrganization = target;
            }
          }
        };

        this.getOrganizationByName = function(name) {
          for (var i = 0; i < this.list.length; i++) {
            console.log(name, this.list[i].name);
            if (name === this.list[i].name) {
              return this.list[i];
            }
          }
        };

        this.containsOrganization = function(organization) {
          return this.list.indexOf(organization) > -1;
        };

      }])
    ;

