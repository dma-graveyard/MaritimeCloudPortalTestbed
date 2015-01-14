'use strict';

/* Controllers */

angular.module('mcp.organizations.members', ['ui.bootstrap'])


    .controller('OrganizationMembersSummaryController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });
      }])

    .controller('OrganizationMembersController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });

        $scope.canBeRemoved= function(member){
          return $scope.userHasWriteAccess && member.username !== UserContext.currentUser().name;
        };

        $scope.viewState = 'list';
        $scope.remove = function (username) {
        };

        $scope.confirmRemove = function (username) {
          $scope.viewState = 'confirm-remove';
          $scope.selectedMember = username;
        };

        $scope.cancel = function () {
          $scope.viewState = 'list';
          $scope.selectedMember = null;
        };

        $scope.remove = function (member) {
          $scope.busyPromise = OrganizationService.RemoveUserFromOrganization($scope.organization, member.membershipId, function () {
            $scope.viewState = 'remove-success';
          }, function (error) { /*reportError*/
            $scope.viewState = 'remove-failed';
            console.log("Error, dammit!", error);
            //$scope.viewState = 'error';
          });
        };

      }])

    .controller('OrganizationInviteMemberController', ['$scope', '$stateParams', 'UserService', 'UserContext', 'OrganizationService', 'UUID', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserService, UserContext, OrganizationService, UUID, AlmanacOrganizationMemberService) {

        $scope.filter_query = '';
        $scope.viewState = 'invite';
        $scope.orderProp = 'username';

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });

        $scope.isNewMember = function (user) {
          for (var i = 0; i < $scope.organization.members.length; i++) {
            if (user.username === $scope.organization.members[i].username) {
              return false;
            }
          }
          return true;
        };

        $scope.updateSearch = function (pattern) {
          if (pattern.trim().length > 0) {
            $scope.busyPromiseSearch = UserService.query({usernamePattern: pattern}, function (users) {
              $scope.people = users;
            }).$promise;
          } else {
            $scope.people = [];
          }
        };

        $scope.confirm = function (member) {
          $scope.invitedMember = member;
          $scope.viewState = 'confirm';
        };

        $scope.invite = function (member) {

          // fetch and assign a new UUID from the server
          UUID.get({name: "identifier"}, function (newMembershipId) {

            // call server with an invite-request !
            $scope.busyPromise = OrganizationService.InviteUserToOrganization($scope.organization, newMembershipId.identifier, member, function () {
              $scope.viewState = 'success';
            }, function (error) { /*reportError*/
              console.log("Error, dammit!", error);
              $scope.viewState = 'error';
            });
          });

        };
      }])

    ;

