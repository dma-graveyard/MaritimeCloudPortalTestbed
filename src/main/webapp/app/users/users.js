'use strict';

angular.module('mcp.users', ['ui.bootstrap'])

    .controller('UserListController', ['$scope', 'UserService',
      function($scope, UserService) {
        $scope.users = UserService.query();
        $scope.orderProp = 'age';
      }])

    .controller('UserDetailController', ['$scope', '$stateParams', 'UserService',
      function($scope, $stateParams, UserService) {
        $scope.user = UserService.get({username: $stateParams.username}, function(user) {
          // $scope.mainImageUrl = user.images[0];
        });


        // TODO: extend user with an avatar
        //    $scope.setImage = function(imageUrl) {
        //      $scope.mainImageUrl = imageUrl;
        //    };
      }])

    .controller('UserSignupController', ['$scope', 'UserService',
      function($scope, UserService) {
        $scope.user = {};
        $scope.message = null;
        $scope.alertMessages = null;

        $scope.isValid = function(isFormValid) {
          return isFormValid && $scope.passwordsMatch();
        }
        
        $scope.passwordsMatch = function() {
          return $scope.user.password === $scope.repeatedPassword;
        }
        
        $scope.getError = function(error, minLength, maxLength, patternMsg) {
          if (angular.isDefined(error)) {
            if (error.required) {
              return "Please enter a value";
            } else if (error.minlength) {
              return "Please enter at least "+minLength+" characters";
            } else if (error.maxlength) {
              return "No more than "+maxLength+" characters";
            } else if (error.email) {
              return "Please enter a valid email address";
            } else if (error.pattern) {
              return patternMsg;
            }
          }
        }

        $scope.sendRequest = function() {
          $scope.message = null;
          $scope.alertMessages = null;
          
          if (!$scope.user.emailAddress) {
            $scope.alertMessages = ["A proper email address is required."];
          } else {
            $scope.message = "Sending request for access.";

            UserService.signUp($scope.user, function(data) {
              $scope.message = ["Request SUCCESS :) " + data];
            }, function(error) {
              // Error handler code
              $scope.alertMessages = ["Error on the serverside :( ", error];
              $scope.alertMessages.push("Request for access has failed. Please try again.");
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
      }])
