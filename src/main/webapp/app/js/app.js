'use strict';

/* App Module */

var mcpApp = angular.module('mcpApp', [
  'ngRoute',
  'mcpControllers',
  'mcpServices',
  'mcpFilters',
  'mcpAuthModule',
  'mcpDirectives'
]);

mcpApp.config(['$routeProvider', 'USER_ROLES',
  function($routeProvider, USER_ROLES) {
    $routeProvider.
        when('/dashboard', {
          templateUrl: 'partials/dashboard.html',
          //controller: 'UserListController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/users', {
          templateUrl: 'partials/user-list.html',
          controller: 'UserListController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/profile/:username', {
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/users/:username', {
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/join', {
          templateUrl: 'partials/user-signup.html',
          controller: 'UserSignupController',
          data: {}
        }).
        when('/landingpage', {
          templateUrl: 'partials/landingpage.html',
          // controller: 'LoginDialogController',
          data: {}
        }).
        when('/orgs', {
          templateUrl: 'partials/organization-list.html',
          controller: 'OrganizationListController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/orgs/new', {
          templateUrl: 'partials/organization-create.html',
          controller: 'OrganizationCreateController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        when('/orgs/:organizationname', {
          templateUrl: 'partials/organization-detail.html',
          controller: 'OrganizationDetailsController',
          data: {
            authorizedRoles: [USER_ROLES.admin, USER_ROLES.user]
          }
        }).
        otherwise({
          redirectTo: '/landingpage'
        });
  }])

    // PAGE TRANSITION: 
    // Register a "Restricting Route Access" listener
    .run(function($rootScope, $location, AUTH_EVENTS, AuthService, $route) {
      $rootScope.$on('$locationChangeStart', function(event, next, current) {

        // Get the 'next' route object (in order to find its authorized roles)
        var nextPath = $location.path(),
            nextRoute = $route.routes[nextPath],
            authorizedRoles = null;

        //  Get the authorized roles if any
        if (nextRoute && nextRoute.data) {
          // ( AuthorizedRoles holds the roles that the user 
          //   must have in order to access the targeted page )
          authorizedRoles = nextRoute.data.authorizedRoles || null;
        }

        // check if user is authenticated and is (in a) authorized (role)
        if (authorizedRoles && !AuthService.isAuthorized(authorizedRoles)) {
          event.preventDefault();
          if (AuthService.isAuthenticated()) { // user is not allowed
            console.log("Restricting Route Access: user is not allowed");
            $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
          } else {                            // user is not logged in
            console.log("Restricting Route Access: user is not logged in");
            $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated, nextRoute.originalPath);
          }
        }
      });

    });

