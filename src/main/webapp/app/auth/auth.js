/*  Copyright 2014 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

//  The techniques applied in this file follows the suggestions in
//
//  "Techniques for authentication in AngularJS applications" 
//  
//  (https://medium.com/opinionated-angularjs/techniques-for-authentication-in-angularjs-applications-7bbf0346acec)

angular.module('mcp.auth', ['ui.bootstrap', 'http-auth-interceptor', 'ngStorage'])


/* Constants */

// Authentication events broadcasted on rootScope
.constant('AUTH_EVENTS', {
  loginSuccess: 'auth-login-success',
  loginFailed: 'auth-login-failed',
  loginCancelled: 'auth-login-cancelled',
  logoutSuccess: 'auth-logout-success',
  sessionTimeout: 'auth-session-timeout',
  notAuthenticated: 'auth-not-authenticated',
  notAuthorized: 'auth-not-authorized'
})

.constant('USER_ROLES', {
  all: '*',
  admin: 'ADMIN',
  user: 'USER',
  editor: 'editor',
  guest: 'guest'
})


/* Controllers */

// A container for global application logic
.controller('ApplicationController', function($rootScope, $scope, $modal, $location, $localStorage, USER_ROLES, AUTH_EVENTS, AuthService, httpAuthInterceptorService, Session) {
  $scope.sidebar = { isMinified: false };
  $scope.userRoles = USER_ROLES;
  $scope.isAuthorized = AuthService.isAuthorized;
  $scope.isLoggedIn = AuthService.isAuthenticated;
  $scope.navigationTarget = null;
  $scope.message = null;
  $scope.alertMessages = [];
  $scope.$storage = $localStorage.$default({
    userSession: {
      id: null,
      userId: null,
      userRole: null,
      user: {name: null, role: null}
    }
  });
  Session.importFrom($scope.$storage.userSession);
  $scope.currentUser = Session.user;

  $scope.$watch('$storage.userSession.userId', function() {
    console.log("User session changed! ", $scope.$storage.userSession.userId);
    $scope.currentUser = $scope.$storage.userSession.user;
  }, true);

  // Login listener that binds the login session to current user upon login success
  $scope.$on(AUTH_EVENTS.loginSuccess, function() {
    console.log("EVENT: User logged in! ", Session.user, "Session: ", Session);
    $scope.currentUser = Session.user;
    Session.exportTo($scope.$storage.userSession);
    // Process pending requests
    httpAuthInterceptorService.loginConfirmed();
    // Navigate to defered page
    if ($scope.navigationTarget) {
      console.log("forwarding to defered path: ", $scope.navigationTarget);
      $location.path($scope.navigationTarget).replace();
      $scope.navigationTarget = null;
    }
  });

  // Logout listener that cleans up state bound to the current user
  $scope.$on(AUTH_EVENTS.logoutSuccess, function() {
    console.log("EVENT: User logged out! Session: ", Session);

    //Clean up state
    //delete $scope.$storage;
    $scope.$storage.$reset({
      userSession: {
        id: null,
        userId: null,
        userRole: null,
        user: null
      }
    });
    Session.importFrom($scope.$storage.userSession);
    $scope.currentUser = Session.user;

    //$scope.currentUser = null;
    $scope.navigationTarget = null;
    $location.path('/').replace();
    //Session.exportTo($scope.$storage.userSession);
  });

  // Login listener that resets current user upon login failure
  $scope.$on(AUTH_EVENTS.loginFailed, function() {
    console.log("User login failed!");
    //$scope.currentUser = null;
    //$scope.navigationTarget = null;
    $scope.alertMessages = ["User login failed!"];
    $scope.openLoginDialog();
  });

  // Login listener that resets any pending requests if user cancels login
  $scope.$on(AUTH_EVENTS.loginCancelled, function() {
    console.log("User login cancelled!");
    httpAuthInterceptorService.loginCancelled();
  });

  // Login listener that warns that user is not authorized for the action
  $scope.$on(AUTH_EVENTS.notAuthorized, function() {
    console.log("User not authorized!");
    $scope.alertMessages = ["User not authorized!"];
    // TODO: flash an error message somehow...!?
  });

  // Login listener that brings up the login dialog whenver the "event:auth-loginRequired!" event is fired
  $scope.$on('event:auth-loginRequired', function() {
    console.log("event:auth-loginRequired!");
    $scope.openLoginDialog();
  });
  
  $scope.$on(AUTH_EVENTS.notAuthenticated, function(event, targetRoute) {
    console.log("AUTH_EVENTS.notAuthenticated targetRoute: ", targetRoute," event:", event);
    $scope.navigationTarget = targetRoute;
    $scope.openLoginDialog('sm');
  });

  $scope.openLoginDialog = function(size) {
    var modalInstance = $modal.open({
      templateUrl: 'auth/loginDialog.html',
      //controller: ModalInstanceController,
      size: size,
      backdrop: 'static',
      resolve: {
//        user: function() {
//          return $scope.user;
//        }
      }
    });

    modalInstance.result.then(function(credentials) {
      // User pressed OK
      console.log('Login dialog comitted: ', credentials, new Date());
      AuthService.login(credentials).then(function() {
        $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
      }, function() {
        $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
      });
    }, function() {
      // User pressed CANCEL
      console.log('Login cancelled at: ' + new Date());
      $rootScope.$broadcast(AUTH_EVENTS.loginCancelled);
    });
  };

  $scope.logout = function() {
    console.log("Logging out...");
    AuthService.logout().then(function() {
      $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
    }, function() {
      $rootScope.$broadcast(AUTH_EVENTS.logoutFailed);
    });
  };

})


/* Services */

// Service logic related to the remote authentication and authorization (access control)
.service('AuthService', function($http, Session) {
  self = this;
  this.login = function(credentials) {
    console.log("Logging in with " + credentials.username);
    return $http
        .post('/rest/authentication/login', credentials, {ignoreAuthModule: true})
        .then(function(respone) {
          var data = respone.data;
          console.log("Login response data: ", data);
          Session.create(data.id, data.username, data.role);
        });
  };
  this.isAuthenticated = function() {
    return !!Session.userId;
  };
  this.isAuthorized = function(authorizedRoles) {
    if (!angular.isArray(authorizedRoles)) {
      authorizedRoles = [authorizedRoles];
    }
    return (self.isAuthenticated() &&
        authorizedRoles.indexOf(Session.userRole) !== -1);
  };
  this.logout = function() {
    console.log("Logging out...");
    return $http
        .post('/rest/authentication/logout', {}, {ignoreAuthModule: true})
        .then(function(respone) {
          var data = respone.data;
          console.log("Logout response data: ", data);
          Session.destroy();
        });
  };

})

// the user’s session information
.service('Session', function() {
  this.create = function(sessionId, userId, userRole) {
    this.id = sessionId;
    this.userId = userId;
    this.userRole = userRole;
    this.user = {name: userId, role: userRole};
  };
  this.importFrom = function(userSession) {
    this.id = userSession.id;
    this.userId = userSession.userId;
    this.userRole = userSession.userRole;
    this.user = userSession.user;
  };
  this.exportTo = function(userSession) {
    userSession.id = this.id;
    userSession.userId = this.userId;
    userSession.userRole = this.userRole;
    userSession.user = this.user;
  };
  this.destroy = function() {
    this.id = null;
    this.userId = null;
    this.userRole = null;
    this.user = null;
  };
  return this;
});
