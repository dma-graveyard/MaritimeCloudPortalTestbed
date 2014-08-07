'use strict';

/* App Module */

var mcpApp = angular.module('mcpApp', [
  'ui.router',
  'ui.router.stateHelper',
  'mcpControllers',
  'mcpServices',
  'mcpFilters',
  'mcpAuthModule',
  'mcpDirectives'
]);

mcpApp.config(['$stateProvider', 'stateHelperProvider', '$urlRouterProvider', 'USER_ROLES',
  function($stateProvider, stateHelperProvider, $urlRouterProvider, USER_ROLES) {
    $urlRouterProvider.when("", "/");
    //$urlRouterProvider.when("/", "/landingpage");
    
    var publicArea = {
      name: 'public',
      templateUrl: 'partials/public.html',
      children: [
        {
          name: 'landingpage',
          url: "/",
          templateUrl: 'partials/landingpage.html',
        },
        {
          name: 'join',
          url: "/join",
          templateUrl: 'partials/user-signup.html',
          controller: 'UserSignupController',
        }
      ]
    };

    var restrictedArea = {
      name: 'restricted',
      templateUrl: 'partials/restricted.html',
      data: {
        authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
      },
      children: [
        {
          name: 'dashboard',
          url: "/dashboard",
          templateUrl: 'partials/dashboard.html',
        },
        {
          name: 'users',
          url: "/users",
          templateUrl: 'partials/user-list.html',
          controller: 'UserListController',
        },
        {
          name: 'userDetails',
          url: "/users/{username}",
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
        },
        {
          name: 'userProfile',
          url: "/users/{username}",
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
        }, 
        {
          name: 'organizations',
          url: "/orgs",
          templateUrl: 'partials/organization-list.html',
          controller: 'OrganizationListController',
        },
        {
          name: 'organizationCreate',
          url: "/orgs/new",
          templateUrl: 'partials/organization-create.html',
          controller: 'OrganizationCreateController',
        },
        {
          name: 'organizationDetails',
          url: "/orgs/{organizationname}",
          templateUrl: 'partials/organization-detail.html',
          controller: 'OrganizationDetailsController',
        }
      ]
    };

    stateHelperProvider.setNestedState(publicArea);
    stateHelperProvider.setNestedState(restrictedArea);
  }])

    // PAGE TRANSITION: 
    // Register a "Restricting Route Access" listener
    .run(function($rootScope, AUTH_EVENTS, AuthService) {
      $rootScope.$on('$stateChangeStart', function(event, next) {

        var isRestrictedRoute = next.data && next.data.authorizedRoles;

        // check if user is authenticated and is (in a) authorized (role)
        if (isRestrictedRoute && !AuthService.isAuthorized(next.data.authorizedRoles)) {
          event.preventDefault();
          if (AuthService.isAuthenticated()) { // user is not allowed
            console.log("Restricted Route Access: user is not allowed");
            $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
          } else {                            // user is not logged in
            console.log("Restriced Route Access: user is not logged in", next);
            //CB todo: nextRoute must be defined again after transition to ui-route
            $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated, next.url);
          }
        }
      });

    });

