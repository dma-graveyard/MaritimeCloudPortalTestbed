'use strict';
angular.module('mcp.users', ['ui.bootstrap'])

    .controller('UserListController', ['$scope', 'UserService',
      function ($scope, UserService) {
        $scope.pageSize = 20;
        $scope.totalItems = 0;
        $scope.currentPage = 1;
        $scope.updateSearch = function () {
          $scope.busyPromise = UserService.query({size: $scope.pageSize, page: $scope.currentPage - 1, usernamePattern: $scope.filter_query}, function (page) {
            $scope.page = page;
            $scope.users = page.content;
          });
        };
        $scope.pageChanged = function () {
          $scope.updateSearch();
        };
        // load first page
        $scope.updateSearch();
      }])

    .controller('UserDetailController', ['$scope', '$stateParams', 'UserService',
      function ($scope, $stateParams, UserService) {
        $scope.user = UserService.get({username: $stateParams.username}, function (user) {
          // $scope.mainImageUrl = user.images[0];
        });
        // TODO: extend user with an avatar
        //    $scope.setImage = function(imageUrl) {
        //      $scope.mainImageUrl = imageUrl;
        //    };
      }])

    .controller('UserProfileController', ['$scope', 'UserService', 'Session',
      function ($scope, UserService, Session) {
        $scope.busyPromise = UserService.get({username: Session.userId}, function (user) {
          $scope.user = user;
        });
      }])

    .controller('UserSignupController', ['$scope', 'UserService', '$state',
      function ($scope, UserService, $state) {
        $scope.user = {};
        $scope.message = null;
        $scope.alert = null;
        $scope.usernameAlreadyExist = true;
        $scope.signUpPromise = null;
        $scope.isValid = function (isFormValid) {
          return isFormValid
              && $scope.passwordsMatch()
              && !$scope.passwordEqualsUsername()
              && !$scope.usernameAlreadyExist;
        };
        $scope.passwordsMatch = function () {
          return $scope.user.password === $scope.user.repeatedPassword;
        };
        $scope.passwordEqualsUsername = function () {
          return $scope.user.password === $scope.user.username;
        };
        $scope.getError = function (error, minLength, maxLength, patternMsg) {
          if (angular.isDefined(error)) {
            if (error.required) {
              return "Please enter a value";
            } else if (error.minlength) {
              return "Please enter at least " + minLength + " characters";
            } else if (error.maxlength) {
              return "No more than " + maxLength + " characters";
            } else if (error.email) {
              return "Please enter a valid email address";
            } else if (error.pattern) {
              return patternMsg;
            }
          }

        };
        $scope.resolveUniqueUsername = function () {
          if (!angular.isDefined($scope.user.username)) {
            $scope.usernameAlreadyExist = true;
            return;
          }
          return UserService.isUnique({username: $scope.user.username}, function (data) {
            $scope.usernameAlreadyExist = data.usernameExist;
          });
        };
        $scope.$watch("user.username",
            function (newValue, oldValue, scope) {
              if (newValue !== oldValue) {
                //console.log(newValue, oldValue);
                scope.resolveUniqueUsername();
              }
            }
        );
        $scope.sendRequest = function () {
          $scope.alert = null;
          $scope.user.userId = "";
          $scope.message = "Sending request for access.";
          delete $scope.user.repeatedPassword;
          $scope.signUpPromise = UserService.signUp($scope.user, function (data) {
            $scope.message = null;
            $state.transitionTo("public.joinConfirmation");
          }, function (error) {
            $scope.message = null;
            $scope.alert = "Argh! An error occured on the server :(";
          });
        };
      }])

    .controller('UserChangeEmailAddressController', ['$scope', 'UserService', '$stateParams',
      function ($scope, UserService, $stateParams) {
        $scope.user = {
          userId: "",
          username: $stateParams.username
        };
        UserService.get($scope.user, function (user) {
          $scope.oldEmailAddress = user.emailAddress;
        });
        $scope.viewState = 'supplyEmailAddress';
        $scope.message = null;
        $scope.alert = null;
        $scope.busyPromise = null;
        $scope.isValid = function (isFormValid) {
          return isFormValid && $scope.user.emailAddress;
        };
        $scope.getError = function (error) {
          if (angular.isDefined(error)) {
            return error.required ? "Please enter a value" : error.email ? "Please enter a valid email address" : "";
          }
        };
        $scope.sendRequest = function () {
          $scope.alert = null;
          $scope.message = "Sending request...";
          delete $scope.user.repeatedPassword;
          $scope.busyPromise = UserService.changeUserEmailAddress($scope.user, function (data) {
            $scope.message = null;
            $scope.viewState = 'success';
          }, function (error) {
            $scope.viewState = 'error';
            $scope.message = null;
            $scope.alert = "Argh! An error occured on the server :(";
          });
        };
      }])

    .controller('UserResetPasswordController', ['$scope', '$stateParams', 'AuthService', '$controller',
      function ($scope, $stateParams, AuthService, $controller) {

        // Inherit password field behavior from similar controller
        $controller('UserSignupController', {$scope: $scope}); //This works
        // Override as we do not require a unique username
        $scope.resolveUniqueUsername = function () {
          return true;
        };
        $scope.isValid = function (isFormValid) {
          //console.log(isFormValid, $scope.passwordsMatch(), !$scope.passwordEqualsUsername());
          return isFormValid
              && $scope.passwordsMatch()
              && !$scope.passwordEqualsUsername();
        };
        $scope.busyPromise = null;
        $scope.viewState = 'supplyPassword';
        $scope.user.username = $stateParams.username;
        $scope.changePassword = function (newPassword) {
          $scope.busyPromise = AuthService.resetPassword(
              $stateParams.username,
              $stateParams.activationId,
              newPassword).then(
              function () {
                $scope.viewState = "success";
              },
              function (error) {
                // Error handler code
                //console.log("Error during reset of password: ", error);
                $scope.alert = "Whoops! Something went wrong: (" + error.status + ") " + error.statusText;
                $scope.viewState = "expired";
              });
        };
      }
    ])

    .controller('UserChangePasswordController', ['$scope', '$stateParams', 'UserService', '$controller',
      function ($scope, $stateParams, UserService, $controller) {

        // Inherit password field behavior from similar controller
        $controller('UserSignupController', {$scope: $scope}); //This works
        $scope.isValid = function (isFormValid) {
          return isFormValid
              && $scope.passwordsMatch()
              && !$scope.passwordEqualsUsername();
        };
        $scope.viewState = 'supplyPassword';
        $scope.passwordPrefix = "New";
        $scope.busyPromise = null;
        $scope.user.username = $stateParams.username;
        $scope.retry = function () {
            $scope.viewState = "supplyPassword";
        };
        $scope.changePassword = function (currentPassword, newPassword) {
          $scope.busyPromise = UserService.changeUserPassword({
            userId: "",
            username: $stateParams.username,
            currentPassword: currentPassword,
            changedPassword: newPassword
          }, function () {
            $scope.viewState = "success";
          }, function (error) {
            // Error handler code
            $scope.alert = "Whoops! Something went wrong: (" + error.status + ") " + error.statusText;
            $scope.viewState = error.status === 400 ? 'notFound' : 'error';
          });
        };
      }
    ])

    .controller('UserConfirmEmailAddressController', ['$scope', '$stateParams', 'UserService',
      function ($scope, $stateParams, UserService) {
        //console.log("Activate " + $stateParams.username);
        $scope.busyPromise = null;
        $scope.accountActivated = null;
        $scope.viewState = 'loading';
        $scope.busyPromise = UserService.verifyEmailAddress({
          userId: "",
          username: $stateParams.username,
          emailAddressVerificationId: $stateParams.activationId
        },
        function () {
          $scope.viewState = 'success';
        },
            function (error) {
              console.log(error);
              $scope.viewState = error.status === 400 ? 'notFound' : 'error';
            });
      }
    ])


    // UserContext
    // holds info about: 
    // - the current logged in user 
    // - the users list of organization memberships
    .service('UserContext', ['UserService', 'OrganizationService', function (UserService, OrganizationService) {

        var currentUser = null;
        // Organizations that the current user is a member of
        var organizationMemberships = [];
        this.reset = function () {
          currentUser = null;
          organizationMemberships = [];
        };
        this.refresh = function () {
          updateOrganizationMemberships();
        };
        this.setCurrentUser = function (aUser) {
          currentUser = aUser;
          this.refresh();
        };
        this.currentUser = function () {
          return currentUser;
        };
        this.organizationMemberships = function () {
          return organizationMemberships;
        };
        this.isAdminMemberOf = function (organizationId) {
          for (var i = 0; i < organizationMemberships.length; i++) {
            if (organizationId === organizationMemberships[i].organizationId) {
              return true;
            }
          }
          return false;
        };
        var updateOrganizationMemberships = function () {
          organizationMemberships = !currentUser ? [] : UserService.queryOrganizationMeberships({username: currentUser.name}, function (memberships) {
            memberships.forEach(function (membership) {
              // hydrate with organization
              membership.organization = OrganizationService.get({organizationId: membership.organizationId});
            });
          });
        };
        this.isOwnerOf = function (organization) {
          var membership = findMembership(organization.organizationId);
          return membership; //fixme: && membership.isOwner();
        };
        var findMembership = function (organizationId) {
          for (var i = 0; i < organizationMemberships.length; i++) {
            if (organizationId === organizationMemberships[i].organizationId) {
              return organizationMemberships[i];
            }
          }
          return null;
        };
      }])

    ;
