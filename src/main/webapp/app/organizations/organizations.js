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

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId});
        $scope.specifications = ServiceSpecificationService.query({organizationId: $stateParams.organizationId}, function (specifications) {
        });
        $scope.serviceInstances = ServiceInstanceService.query({organizationId: $stateParams.organizationId}, function (serviceInstances) {
        });

        $scope.userHasWriteAccess = function () {

// FIXME: rewrite to use a list of organizations the user is a member of  
          //return UserService.isAdminMemberOf($scope.organization.organizationId);
//          return $scope.organization.teams[0].members[0] === $scope.currentUser.name;
          return true;
        };
      }])

    .controller('OrganizationCreateController', ['$scope', '$location', 'UUID', 'OrganizationService', 'OrganizationContext',
      function ($scope, $location, UUID, OrganizationService, OrganizationContext) {
        angular.extend($scope, {
          organization: {organizationId: null, name: null, url: "http://hardcoded.bwah.org"},
          /**
           * @returns true when there is enough data in the form 
           * to try to submit it. This is not to say that data is
           * valid. pressing submit will cause a series of 
           * validator to be evaluated
           */
          formIsSubmitable: function () {
            return ($scope.organization.organizationId && $scope.organization.name);
          },
          submit: function () {
            $scope.alertMessages = null;
            $scope.message = "Sending request to create organization...";

            // validate input values
            // ...todo...

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
          }
        });
        $scope.resolveUniqueAlias = function () {
          if (!angular.isDefined($scope.service.primaryAlias)) {
            $scope.aliasAlreadyExist = false;
            $scope.aliasNotDefined = true;
            return;
          }
          OrganizationService.alias({alias: $scope.service.primaryAlias}, function (aliasEntry) {
            $scope.aliasAlreadyExist = angular.isDefined(aliasEntry.alias);
          });
        };
        $scope.$watch("service.primaryAlias",
            function (newValue, oldValue, scope) {
              if (newValue !== oldValue) {
                scope.resolveUniqueAlias();
              }
            }
        );
    
        // Fetch and assign a new UUID from the server
        UUID.get({name: "identifier"}, function (newUuid) {
          $scope.organization.organizationId = newUuid.identifier;
        });

      }])

    .controller('OrganizationEditController', ['$scope', '$stateParams', '$location', 'OrganizationService', 'OrganizationContext',
      function ($scope, $stateParams, $location, OrganizationService, OrganizationContext) {

        var reportError = function (error) {
          $scope.message = null;
          $scope.alertMessages = ["Error on the serverside :( ", error];
        };
        var getHydratedOrganization = function () {

          OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
            $scope.organization = organization;

            //hydrate organization
            // add list of aliases
            OrganizationService.aliases({organizationId: $stateParams.organizationId}, function (aliases) {
              // ...but remove the primary alias from the list
              var list = [];
              aliases.forEach(function (alias) {
                if (alias.alias !== organization.primaryAlias)
                  list.push(alias);
              });
              organization.aliases = list;
            });
          });

        };

        angular.extend($scope, {
          organization: getHydratedOrganization(),
          nameAndSummaryIsSubmitable: function () {
            return ($scope.organization.name);
          },
          submitNameAndSummary: function () {
            $scope.message = "Sending request to change organization...";
            $scope.alertMessages = null;
            $scope.busyPromise = OrganizationService.changeNameAndSummary($scope.organization,
                function () {
                  $scope.message = ["Name and summary changed!"];
                  // todo: propagate changes to organization context list
                  // ...
                }, reportError);
          },
          submitUrl: function () {
            $scope.message = "Sending request to change organization website URL...";
            $scope.alertMessages = null;
            $scope.busyPromise = OrganizationService.changeWebsiteUrl($scope.organization,
                function () {
                  $scope.message = ["Website URL changed!"];
                }, reportError);
          },
          addAlias: function (newAlias) {
            console.log("$scope.ORG ---- ", $scope);

            OrganizationService.addAlias($scope.organization, newAlias, function () {

              // reload serviceInstance
              $scope.service = getHydratedOrganization();
              $scope.message = "Alias added!";
              $scope.newAlias = "";

            }, reportError);
          },
          removeAlias: function (alias) {
            OrganizationService.removeAlias($scope.organization, alias, function () {

              // reload serviceInstance
              $scope.service = getHydratedOrganization();
              $scope.message = "Alias removed!";
              $scope.newAlias = "";

            }, reportError);
          },
          close: function () {
            $location.path('/orgs/' + $scope.organization.organizationId).replace();
          }
        });

        $scope.resolveUniqueAlias = function () {
          if (!angular.isDefined($scope.newAlias)) {
            $scope.aliasAlreadyExist = false;
            $scope.aliasNotDefined = true;
            return;
          }

          OrganizationService.alias({alias: $scope.newAlias}, function (aliasEntry) {
            $scope.aliasAlreadyExist = angular.isDefined(aliasEntry.alias);
          });
        };

        $scope.$watch("newAlias",
            function (newValue, oldValue, scope) {
              if (newValue !== oldValue) {
                //console.log(newValue, oldValue);
                scope.resolveUniqueAlias();
              }
            }
        );

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

