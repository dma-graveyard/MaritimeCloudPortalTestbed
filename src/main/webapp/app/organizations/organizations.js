'use strict';

/* Controllers */

angular.module('mcp.organizations', ['ui.bootstrap'])

    .controller('OrganizationMenuController', ['$scope', 'OrganizationContext',
      function ($scope, OrganizationContext) {

        $scope.organizations = OrganizationContext.list;
        $scope.currentOrganization = OrganizationContext.currentOrganization;
        $scope.OrganizationContext = OrganizationContext;

        $scope.$watch('OrganizationContext.currentOrganization', function () {
          $scope.currentOrganization = OrganizationContext.currentOrganization;
        });

      }])

    .controller('OrganizationListController', ['$scope', '$stateParams', 'OrganizationContext',
      function ($scope, $stateParams, OrganizationContext) {

        $scope.organizations = OrganizationContext.list;
        $scope.currentOrganization = OrganizationContext.currentOrganization;
        $scope.orderProp = 'name';
        $scope.$stateParams = $stateParams;

        $scope.isCurrent = function (organization) {
          return organization === $scope.currentOrganization;
        };

        $scope.$watch('$stateParams.organizationId', function (newOrganizationId) {
          // (HACK: needed to get list updated after organization created)
          $scope.organizations = OrganizationContext.list;
          
          OrganizationContext.setCurrentOrganization(newOrganizationId);
          $scope.currentOrganization = OrganizationContext.currentOrganization;
        });

      }])

    .controller('OrganizationDetailsController', ['$scope', '$stateParams', 'OrganizationService', 'ServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $stateParams, OrganizationService, ServiceSpecificationService, ServiceInstanceService) {
        console.log("XXXXX XXXXXXX organization XXXXXXX XXXXXXXX", $stateParams.organizationId);
        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          console.log("organization", organization);
        },
            function (error) {
              console.log("organization error", error);
            }
        );
        $scope.specifications = ServiceSpecificationService.query({organizationId: $stateParams.organizationId}, function (specifications) {
        });
        $scope.serviceInstances = ServiceInstanceService.query({organizationId: $stateParams.organizationId}, function (serviceInstances) {
        });

        $scope.userHasWriteAccess = function () {

          //return UserService.isAdminMemberOf($scope.organization.organizationId);
//          return $scope.organization.teams[0].members[0] === $scope.currentUser.name;
          return true;
        };
      }])

    .controller('OrganizationCreateController', ['$scope', '$location', 'OrganizationService', 'OrganizationContext',
      function ($scope, $location, OrganizationService, OrganizationContext) {
        $scope.organization = {organizationId: null, name: null};
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
        $scope.formIsSubmitable = function () {
          return ($scope.organization.organizationId && $scope.organization.name);
        };

        $scope.submit = function () {
          $scope.message = null;
          $scope.alertMessages = null;

          // validate input values
          if ($scope.organization.organizationId) {
            if ($scope.organization.organizationId === "test") {
              $scope.alertMessages = ["Test!? Really? ...You have to be more visionary than that!"];
              return;
            }
          }

          // Send request
          $scope.message = "Sending request to create organization...";

          $scope.busyPromise = OrganizationService.create($scope.organization,
              function (data) {
                $location.path('/orgs/' + $scope.organization.organizationId).replace();
                $scope.message = ["Organization created: " + data];
                OrganizationContext.list = $scope.currentUser ? OrganizationService.query({member: $scope.currentUser.name}) : [];
              },
              function (error) {
                $scope.message = null;
                $scope.alertMessages = ["Error on the serverside :( ", error];
              }
          );
      
        };
      }])

    // OrganizationContext
    // - the list of organizations the user is a member of
    // - the currently selected organization
    .service("OrganizationContext", [function () {

        // Organizations that the current user is a member of
        this.list = [];
        this.currentOrganization = null;

        this.setUsersOrganizations = function (organizations) {
          this.list = organizations;
        };

        this.setCurrentOrganization = function (target) {
          if (angular.isString(target)) {
            this.currentOrganization = this.getOrganizationById(target);
          } else {
            if (this.containsOrganization(target)) {
              this.currentOrganization = target;
            }
          }
        };

        this.getOrganizationById = function (organizationId) {
          for (var i = 0; i < this.list.length; i++) {
            if (organizationId === this.list[i].organizationId) {
              return this.list[i];
            }
          }
        };

        this.containsOrganization = function (organization) {
          return this.list.indexOf(organization) > -1;
        };

      }])
    ;

