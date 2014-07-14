'use strict';

/* Controllers */

var iamControllers = angular.module('iamControllers', ['ui.bootstrap']);

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



