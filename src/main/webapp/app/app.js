'use strict';

/* App Module */

var mcpApp = angular.module('mcpApp', [
  'ui.router',
  'ui.router.stateHelper',
  'ui.select',
  'ngSanitize',
  'cgBusy',
  'angularMoment',
  'mcp.auth',
  'mcp.dataservices',
  'mcp.directives',
  'mcp.filters',
  'mcp.layout',
  'mcp.mapservices',
  'mcp.organizations',
  'mcp.organizations.members',
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

// CQRS REST "command-enabler"
// wrap http-handler in order to intercept non-get methods and add command-name to content-type  
// (shamelessly snatched from "https://github.com/aliostad/m-r/blob/master/SimpleCQRS.Api/Scripts/inventory-item.js")
mcpApp.config(function ($provide) {
  $provide.decorator('$http', function ($delegate) {

    var customHttp = function (config) {

      if (config && (config.method === "PUT" || config.method === "POST" || config.method === "DELETE")
          && config.data && typeof config.data === "object") {

        config.headers = config.headers || {};
        
        // Remove the "Command"-postfix from the command-name (if any)
        // ( We could have removed this from the command function constructors 
        // al toghether in the first place, but I like to keep it here on the 
        // client side in order to remind me that these are Commands as opposed 
        // to all the other functions that lives out here "in the wild" js world) 
        var commandName = config.data.constructor.name.replace(/Command\b/, "");
        
        config.headers["Content-Type"] = "application/json;domain-model=" + commandName;
        if (config.method === "PUT" && config.$scope && config.$scope[constants.concurrencyVersionName]) {
          config.headers["If-Match"] = config.$scope[constants.concurrencyVersionName];
        }

      }

      return $delegate(config);
    };

    angular.extend(customHttp, $delegate);
    return customHttp;
  });
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
          controller: 'UserConfirmEmailAddressController'
        },
        {
          name: 'userConfirmEmailAddress',
          url: "/users/{username}/confirmEmailAddress/{activationId}",
          templateUrl: 'users/user-confirm-email-address.html',
          controller: 'UserConfirmEmailAddressController'
        },
        {
          name: 'userResetPasswordLink',
          url: "/users/{username}/reset/{activationId}",
          templateUrl: 'users/user-reset-password.html',
          controller: 'UserResetPasswordController'
        },
        {
          name: 'styleguide',
          url: "/styleguide",
          templateUrl: 'layout/styleguide.html'
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
          controller: 'DashboardController'
        },
        {
          name: 'users',
          url: "/users",
          templateUrl: 'users/users.html',
          controller: 'UserListController'
        },
        {
          name: 'userDetails',
          url: "/users/{username}",
          templateUrl: 'users/user-detail.html',
          controller: 'UserDetailController'
        },
        {
          name: 'userProfile',
          url: "/users/{username}/profile",
          templateUrl: 'users/user-profile.html',
          controller: 'UserProfileController'
        },
        {
          name: 'userChangeEmailAddressController',
          url: "/users/{username}/profile/change/emailAddress",
          templateUrl: 'users/user-change-email-address.html',
          controller: 'UserChangeEmailAddressController'
        },
        {
          name: 'userChangePasswordController',
          url: "/users/{username}/profile/change/password",
          templateUrl: 'users/user-change-password.html',
          controller: 'UserChangePasswordController'
        },
        {
          name: 'organizations',
          url: "/orgs",
          templateUrl: 'organizations/manage-organizations.html',
          controller: 'DashboardContextController'
        },
        {
          name: 'searchOrganizations',
          url: "/public/orgs",
          templateUrl: 'organizations/public-organizations.html',
          controller: 'AlmanacOrganizationListController'
        },
        {
          name: 'organizationCreate',
          url: "/orgs/new",
          templateUrl: 'organizations/organization-create.html',
          controller: 'OrganizationCreateController'
        },
        {
          name: 'organizationDetails',
          url: "/orgs/{organizationId}",
          templateUrl: 'organizations/organization-detail.html',
          controller: 'OrganizationDetailsController'
        },
        {
          name: 'organizationSettings',
          url: "/orgs/{organizationId}/settings",
          templateUrl: 'organizations/organization-edit.html',
          controller: 'OrganizationEditController'
        },
        {
          name: 'organizationMembers',
          url: "/orgs/{organizationId}/members",
          templateUrl: 'organizations/members/member-list.html',
          controller: 'OrganizationMembersController'
        },
        {
          name: 'organizationMembersInvite',
          url: "/orgs/{organizationId}/members/invite",
          templateUrl: 'organizations/members/membership.html',
          controller: 'OrganizationInviteMemberController'
        },
        {
          name: 'organizationMembersAcceptInvite',
          url: "/orgs/{organizationId}/members/invite/accept",
          templateUrl: 'organizations/members/membership.html',
          controller: 'MembershipAcceptInviteController'
        },
        {
          name: 'organizationMembersJoin',
          url: "/orgs/{organizationId}/members/join",
          templateUrl: 'organizations/members/membership.html',
          controller: 'UserJoinOrganizationController'
        },
        {
          name: 'organizationMembersLeave',
          url: "/orgs/{organizationId}/members/leave",
          templateUrl: 'organizations/members/membership.html',
          controller: 'UserLeaveOrganizationController'
        },
        {
          name: 'serviceInstanceCreate',
          url: "/orgs/{organizationId}/createServiceInstance",
          templateUrl: 'organizations/service-instances/service-instance-create.html',
          controller: 'CreateServiceInstanceController',
          data: {createState: true}
        },
        {
          name: 'serviceInstanceEdit',
          url: "/orgs/{organizationId}/{serviceInstanceId}/edit",
          templateUrl: 'organizations/service-instances/service-instance-edit.html',
          controller: 'EditServiceInstanceController',
          data: {editState: true}
        },
        {
          name: 'searchServiceMap',
          url: "/search/service/map",
          templateUrl: 'search/search-service-map.html',
          controller: 'SearchServiceMapController'
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

