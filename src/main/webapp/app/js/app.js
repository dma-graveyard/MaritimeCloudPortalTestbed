'use strict';

/* App Module */

var iamApp = angular.module('iamApp', [
  'ngRoute',
  'iamControllers',
  'iamServices',
  'iamFilters',
  'iamAuthModule',
  'iamDirectives.ui.bootstrap'
]);

iamApp.config(['$routeProvider', 'USER_ROLES',
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
        when('/profile', {
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
          data: {}
        }).
        when('/users/:username', {
          templateUrl: 'partials/user-detail.html',
          controller: 'UserDetailController',
          data: {}
        }).
        when('/join', {
          templateUrl: 'partials/user-signup.html',
          controller: 'UserSignupController',
          data: {}
        }).
        when('/landingpage', {
          templateUrl: 'partials/landingpage.html',
//          controller: 'LoginDialogController',
          data: {}
        }).
        otherwise({
          redirectTo: '/landingpage'
        });
  }])

    // PAGE TRANSITION: 
    // Register an "Restricting Route Access" listener
    .run(function($rootScope, $location, AUTH_EVENTS, AuthService, $route) {
      $rootScope.$on('$locationChangeStart', function(event, next, current) {

        // Get the 'next' route object in order to find its authorized roles
        var nextPath = $location.path(),
            nextRoute = $route.routes[nextPath],
            authorizedRoles = null;

        //  Get the authorized roles if nany
        // ( AuthorizedRoles holds the roles that the user 
        //   must have in order to access the targeted page )
        if (nextRoute && nextRoute.data) {
          authorizedRoles = nextRoute.data.authorizedRoles || null;
        }
        console.log("Restricting Route Access...", event, "next:", next, "current:", current, "$route", $route, "nextRoute", nextRoute);
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

