'use strict';

/* Controllers */

angular.module('mcp.organizations.members', ['ui.bootstrap'])


    .controller('OrganizationMembersController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });
      }])

    .controller('OrganizationInviteMemberController', ['$scope', '$stateParams', 'UserService', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserService, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        $scope.viewState = 'invite';

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });
        
        $scope.isNewMember = function(user){
          for (var i = 0; i < $scope.organization.members.length; i++) {
            if (user.username === $scope.organization.members[i].username) {
              return false;
            }
          }
          return true;
        }

        $scope.updateSearch = function (pattern) {
          if (pattern.trim().length > 0) {
            $scope.busyPromise = UserService.query({usernamePattern: pattern}, function (users) {
              $scope.people = users;
            }).$promise;
          } else {
            $scope.people = [];
          }

        };

        $scope.orderProp = 'username';

        $scope.invite = function (member) {
          $scope.invitedMember = member;

          // call server with an invite-request !
          $scope.busyPromise = OrganizationService.InviteUserToOrganization($scope.organization, member, function () {
            $scope.viewState = 'confirm';
          }, function (error) { /*reportError*/
            console.log("Error, dammit!", error);
            //$scope.viewState = 'error';
          });

        };

        $scope.inviteMore = function () {
          $scope.viewState = 'invite';
          $scope.filter_query = '';
          $scope.updateSearch('');
        };
      }])

    ;

