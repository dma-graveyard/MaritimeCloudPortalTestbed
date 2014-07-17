'use strict';

/* Controllers */

var iamControllers = angular.module('iamControllers', ['ui.bootstrap']);

iamControllers.controller('SidebarController', ['$scope',
  function($scope) {
    $scope.isMinified = false;
    // TODO: not really using this controller just yet. 
  }]);

iamControllers.controller('UserListController', ['$scope', 'UserService',
  function($scope, UserService) {
    // (UserService is defined in services.js)
    $scope.users = UserService.query();
    $scope.orderProp = 'age';
  }]);

iamControllers.controller('UserDetailController', ['$scope', '$routeParams', 'UserService',
  function($scope, $routeParams, UserService) {
    $scope.user = UserService.get({username: $routeParams.username}, function(user) {
      // $scope.mainImageUrl = user.images[0];
    });


    // TODO: extend user with an avatar
    //    $scope.setImage = function(imageUrl) {
    //      $scope.mainImageUrl = imageUrl;
    //    };
  }]);

iamControllers.controller('UserSignupController', ['$scope', 'UserService',
  function($scope, UserService) {
    $scope.user = {};
    $scope.message = null;
    $scope.alertMessages = null;
    //$("#rPreferredLogin").focus();

    $scope.sendRequest = function() {
      $scope.message = null;
      $scope.alertMessages = null;
      if ($scope.user.mmsiNumber) {
        var x = $scope.user.mmsiNumber;
        $scope.user.mmsiNumber = parseInt(x);
        if ($scope.user.mmsiNumber !== x) {
          $scope.alertMessages = ["MMSI must be only digits."];
          return;
        }
      }

      if (!$scope.user.emailAddress) {
        $scope.alertMessages = ["A proper email address is required."];
      } else {
        $scope.message = "Sending request for access.";

        UserService.signUp($scope.user, function(data) {
          $scope.message = ["Request SUCCESS :) " + data];
        }, function(error) {
          // Error handler code
          $scope.alertMessages = ["Error on the serverside :( "];
        });

//          $http.post(embryo.baseUrl + "rest/request-access/save", $scope.request).success(function() {
//            $scope.message = "Request for access has been sent. We will get back to you via email.";
//          }).error(function(data, status) {
//            $scope.alertMessages = embryo.ErrorService.extractError(data, status);
//            $scope.alertMessages.push("Request for access has failed. Please try again.");
//          });
      }
    }
    ;
  }]);


iamControllers.controller('OrganizationListController', ['$scope', 'OrganizationService',
  function($scope, OrganizationService) {
    $scope.organizations = OrganizationService.query();
    $scope.orderProp = 'age';
  }]);

iamControllers.controller('OrganizationDetailsController', ['$scope', '$routeParams', 'OrganizationService',
  function($scope, $routeParams, OrganizationService) {
    $scope.organization = OrganizationService.get({organizationname: $routeParams.organizationname}, function(organization) {
    });
  }]);

iamControllers.controller('OrganizationCreateController', ['$scope', '$location', 'OrganizationService',
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
        $location.path('/orgs/'+data.name).replace();
        $scope.message = ["Organization created: " + data];
        
      }, function(error) {
        // Error handler code
        $scope.message = null;
        $scope.alertMessages = ["Error on the serverside :( ", error];
      });
    };
  }]);


