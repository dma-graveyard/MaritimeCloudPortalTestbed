'use strict';

/* App Module */

var mcpApp = angular.module('mcpApp', [
  'ui.router',
  'ui.router.stateHelper',
  'ui.select',
  'ngSanitize',
  'cgBusy',
  'mcp.auth',
  'mcp.dataservices',
  'mcp.directives',
  'mcp.filters',
  'mcp.layout',
  'mcp.mapservices',
  'mcp.organizations',
  'mcp.organizations.services',
  'mcp.search.services',
  'mcp.users',
  'leaflet-directive'
]);

// Setup angular busy indicator
// (https://github.com/cgross/angular-busy)
mcpApp.value('cgBusyDefaults',{
    //message:'Loading Stuff',
    //backdrop: true,
    templateUrl: 'layout/angular-busy.html',
    delay: 0,
    minDuration: 0
});

mcpApp.config(['$stateProvider', 'stateHelperProvider', '$urlRouterProvider', 'USER_ROLES',
  function($stateProvider, stateHelperProvider, $urlRouterProvider, USER_ROLES) {
    $urlRouterProvider.when("", "/");
    //$urlRouterProvider.when("/", "/landingpage");
    
    console.log('Version', mcpInfo.version);

    var publicArea = {
      name: 'public',
      templateUrl: 'layout/public.html',
      children: [
        {
          name: 'landingpage',
          url: "/",
          templateUrl: 'partials/landingpage.html'
        },
        {
          name: 'join',
          url: "/join",
          templateUrl: 'users/join-form.html',
          controller: 'UserSignupController'
        },
        {
          name: 'joinConfirmation',
          url: "/join-confirm",
          templateUrl: 'users/join-confirm.html'
        },
        {
          name: 'userActivation',
          url: "/users/{username}/activate/{activationId}",
          templateUrl: 'users/user-activation.html',
          controller: 'UserActivationController',
        },
        {
          name: 'userResetPasswordLink',
          url: "/users/{username}/reset/{activationId}",
          templateUrl: 'users/user-reset-password.html',
          controller: 'UserResetPasswordController',
        },
        {
          name: 'styleguide',
          url: "/styleguide",
          templateUrl: 'layout/styleguide.html',
        }        
      ]
    };

    var restrictedArea = {
      name: 'restricted',
      templateUrl: 'layout/restricted.html',
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
          templateUrl: 'users/users.html',
          controller: 'UserListController',
        },
        {
          name: 'userDetails',
          url: "/users/{username}",
          templateUrl: 'users/user-detail.html',
          controller: 'UserDetailController',
        },
        {
          name: 'userProfile',
          url: "/users/{username}/profile",
          templateUrl: 'users/user-profile.html',
          controller: 'UserProfileController',
        },
        {
          name: 'organizations',
          url: "/orgs",
          templateUrl: 'organizations/organizations.html',
          controller: 'OrganizationListController',
        },
        {
          name: 'organizationCreate',
          url: "/orgs/new",
          templateUrl: 'organizations/organization-create.html',
          controller: 'OrganizationCreateController',
        },
        {
          name: 'organizationDetails',
          url: "/orgs/{organizationname}",
          templateUrl: 'organizations/organization-detail.html',
          controller: 'OrganizationDetailsController',
        },
        {
          name: 'serviceInstanceCreate',
          url: "/orgs/{organizationname}/createServiceInstance",
          templateUrl: 'organizations/services/instance-editor.html',
          controller: 'EditServiceInstanceController',
          data: {createState: true}
        },
        {
          name: 'serviceInstanceEdit',
          url: "/orgs/{organizationname}/{serviceInstanceId}/edit",
          templateUrl: 'organizations/services/instance-editor.html',
          controller: 'EditServiceInstanceController',
          data: {editState: true}
        },
        {
          name: 'searchServiceMap',
          url: "/search/service/map",
          templateUrl: 'search/search-service-map.html',
          controller: 'SearchServiceMapController',
        },
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

