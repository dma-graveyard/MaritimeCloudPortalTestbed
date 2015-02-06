'use strict';

/* Controllers */

angular.module('mcp.organizations', ['ui.bootstrap'])

    .controller('OrganizationContextSidebarController', ['$scope', 'OrganizationContext', 'UserContext',
      function ($scope, OrganizationContext, UserContext) {

        $scope.currentOrganization = OrganizationContext.currentOrganization();
        $scope.isOwnerOf = UserContext.isOwnerOf;

        $scope.OrganizationContext = OrganizationContext;
        $scope.$watch('OrganizationContext.currentOrganization()', function (val) {
          $scope.currentOrganization = OrganizationContext.currentOrganization();
        });

      }])

    .controller('DashboardController', ['$scope', '$stateParams', 'OrganizationContext', 'UserContext',
      'AlmanacOrganizationService', 'AlmanacOperationalServiceService', 'AlmanacServiceSpecificationService', 'AlmanacServiceInstanceService', 'UserService', 'ActivityService',
      function ($scope, $stateParams, OrganizationContext, UserContext,
          AlmanacOrganizationService, AlmanacOperationalServiceService, AlmanacServiceSpecificationService, AlmanacServiceInstanceService, UserService, ActivityService
          ) {

        $scope.organizationMemberships = UserContext.organizationMemberships();
        $scope.currentOrganization = OrganizationContext.currentOrganization();
        $scope.orderProp = 'organization.name';

        $scope.statistics = {
        };

        AlmanacOrganizationService.query(function (list) {
          $scope.statistics.organizations = list.length;
        });
        AlmanacOperationalServiceService.query(function (list) {
          $scope.statistics.operationalServices = list.length;
        });
        AlmanacServiceSpecificationService.query(function (list) {
          $scope.statistics.servicesSpecifications = list.length;
        });
        AlmanacServiceInstanceService.query(function (list) {
          $scope.statistics.services = list.length;
        });
        UserService.query(function (list) {
          $scope.statistics.users = list.length;
        });

        $scope.isCurrentContext = function (organization) {
          return organization === OrganizationContext.currentOrganization();
        };
        $scope.hasOrganizations = function () {
          return $scope.organizationMemberships.length > 0;
        };

      }])

    .controller('UserActivitiesController', ['$scope', '$stateParams', 'OrganizationContext', 'UserContext', 'ActivityService',
      function ($scope, $stateParams, OrganizationContext, UserContext, ActivityService
          ) {

        $scope.orderProp = 'dateTime';
        $scope.pageSize = 10;
        ActivityService.get({username: UserContext.currentUser().name, page: 0, size: $scope.pageSize}, function (page) {
          //console.log(page);
          /* page format example: {
           content: Array[10],
           first: true,
           firstPage: true,
           last: false,
           lastPage: false,
           number: 0,
           numberOfElements: 10,
           size: 10,
           sort: Array[1],
           totalElements: 26,
           totalPages: 3,
           $promise: Object,
           $resolved: true
           } */
          $scope.userActivities = page.content;
          $scope.userActivitiesPage = page;
        });

        var organizationIds = [];
        UserContext.organizationMemberships().$promise.then(
            function (memberships) {
              memberships.forEach(function (membership) {
                organizationIds.push(membership.organizationId);
              });
              ActivityService.get({organizationIds: organizationIds, page: 0, size: $scope.pageSize}, function (page) {
                $scope.userOrgsActivities = page.content;
                $scope.userOrgsActivitiesPage = page;
                //console.log("Orgs:", page);
              });
            }
        );
    
        var periodOf30Days = 1000*60*60*24*30;
        var dateLast30Days = Date.now() - periodOf30Days;    
        ActivityService.get({dateTime: dateLast30Days, page: 0, size: $scope.pageSize}, function (page) {
          $scope.publicActivities = page.content;
          $scope.publicActivitiesPage = page;
        });

      }])

    .controller('ActivityController', ['$scope', '$stateParams', 'OrganizationContext', 'UserContext', 'ActivityService',
      'AlmanacOrganizationService', 'AlmanacOperationalServiceService', 'AlmanacServiceSpecificationService', 'AlmanacServiceInstanceService',
      function ($scope, $stateParams, OrganizationContext, UserContext, ActivityService,
          AlmanacOrganizationService, AlmanacOperationalServiceService, AlmanacServiceSpecificationService, AlmanacServiceInstanceService

          ) {

//        console.log($scope.userActivity);

        if ($scope.activity.targetType === 'ServiceInstanceId') {
          $scope.serviceInstance = AlmanacServiceInstanceService.get({serviceInstanceId: $scope.activity.targetId}, function (si) {
            $scope.serviceInstance = si;
          });
        }

        if ($scope.activity.targetType === 'ServiceSpecificationId') {
          $scope.serviceSpecification = AlmanacServiceSpecificationService.get({serviceSpecificationId: $scope.activity.targetId}, function (ss) {
            $scope.serviceSpecification = ss;
          });
        }

        if ($scope.activity.targetType === 'OrganizationId') {
          $scope.organization = AlmanacOrganizationService.get({organizationId: $scope.activity.targetId}, function (organization) {
            $scope.organization = organization;
          });
        }

      }])

    .controller('DashboardContextController', ['$scope', '$stateParams', 'OrganizationContext', 'UserContext',
      function ($scope, $stateParams, OrganizationContext, UserContext
          ) {

        $scope.organizationMemberships = UserContext.organizationMemberships();
        $scope.currentOrganization = OrganizationContext.currentOrganization();
        $scope.$stateParams = $stateParams;
        $scope.orderProp = 'organization.name';

        $scope.isCurrentContext = function (organization) {
          return organization === OrganizationContext.currentOrganization();
        };
        $scope.hasOrganizations = function () {
          return $scope.organizationMemberships.length > 0;
        };
        $scope.setDashboardContext = function (organization) {
          OrganizationContext.setCurrentOrganization(organization);
          $scope.currentOrganization = organization;
        };
        $scope.setUserAsDashboardContext = function () {
          OrganizationContext.resetCurrentOrganization();
          $scope.currentOrganization = null;
        };
      }])

    .controller('AlmanacOrganizationListController', ['$scope', '$stateParams', 'AlmanacOrganizationService',
      function ($scope, $stateParams, AlmanacOrganizationService) {

        $scope.organizations = AlmanacOrganizationService.query();
        $scope.orderProp = 'name';
        $scope.$stateParams = $stateParams;

      }])

    .controller('OrganizationDetailsController', ['$scope', '$stateParams', 'OrganizationService', 'UserContext',
      'AlmanacOrganizationMemberService', 'ServiceSpecificationService', 'ServiceInstanceService',
      function ($scope, $stateParams, OrganizationService, UserContext, AlmanacOrganizationMemberService, ServiceSpecificationService,
          ServiceInstanceService) {

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf(organization.organizationId);
        });

        $scope.specifications = ServiceSpecificationService.query({organizationId: $stateParams.organizationId});
        $scope.serviceInstances = ServiceInstanceService.query({organizationId: $stateParams.organizationId});
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
            return ($scope.organization.organizationId && $scope.organization.name && $scope.organization.primaryAlias && !$scope.aliasAlreadyExist);
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
//FIXME: update users org-list some other how                  
                  OrganizationContext.updateUserOrganizationsList($scope.currentUser);
                },
                function (error) {
                  $scope.message = null;
                  $scope.alertMessages = ["Error on the serverside :( ", error];
                }
            );
          }
        });
        $scope.resolveUniqueAlias = function () {
          if (!angular.isDefined($scope.organization.primaryAlias)) {
            $scope.aliasAlreadyExist = false;
            $scope.aliasNotDefined = true;
            return;
          }
          $scope.aliasNotDefined = false;
          OrganizationService.alias({alias: $scope.organization.primaryAlias}, function (aliasEntry) {
            $scope.aliasAlreadyExist = angular.isDefined(aliasEntry.alias);
          });
        };
        $scope.$watch("organization.primaryAlias",
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

    .controller('OrganizationEditController', ['$scope', '$stateParams', '$location', 'OrganizationService',
      function ($scope, $stateParams, $location, OrganizationService) {

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
          }
        });

        $scope.resolveUniqueAlias = function () {
          if (!angular.isDefined($scope.newAlias)) {
            $scope.aliasAlreadyExist = false;
            $scope.aliasNotDefined = true;
            return;
          }
          $scope.aliasNotDefined = false;

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
    // - the currently selected organization, if any. When empty, the User is considered to be the dashboard context
    .service("OrganizationContext", [function () {

        // The currently selected Organization of the user. (When 'null' the user is the context)
        var currentOrganization = null;

        this.currentOrganization = function () {
          return currentOrganization;
        };

        this.setCurrentOrganization = function (target) {
          currentOrganization = target;
        };

        this.resetCurrentOrganization = function () {
          currentOrganization = null;
        };

      }])
    ;

