'use strict';

/* Controllers */

angular.module('mcp.organizations.members', ['ui.bootstrap'])

    .controller('OrganizationMembersSummaryController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        UserContext.refresh();

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
          $scope.memberStatus = UserContext.membershipStatus($scope.organization.organizationId);
        });
      }])

    .controller('OrganizationMembersController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService', 'AlmanacOrganizationMemberService',
      function ($scope, $stateParams, UserContext, OrganizationService, AlmanacOrganizationMemberService) {

        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.organization.members = AlmanacOrganizationMemberService.query({organizationId: organization.organizationId});
          $scope.userHasWriteAccess = UserContext.isAdminMemberOf($scope.organization.organizationId);
        });

        $scope.canBeRemoved = function (member) {
          return $scope.userHasWriteAccess && UserContext.currentUser() && member.username !== UserContext.currentUser().name;
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

    .controller('MembershipAcceptInviteController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService',
      function ($scope, $stateParams, UserContext, OrganizationService) {

        $scope.busyPromise = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.viewState = 'accept-invite-try';
          $scope.organization = organization;
          var membership = UserContext.membershipOf(organization.organizationId);
          $scope.busyPromise = OrganizationService.acceptMembershipToOrganization(
              membership.organizationId,
              membership.membershipId,
              function () {
                $scope.viewState = 'accept-success';
              },
              function (error) { /*reportError*/
                console.log("Error, dammit!", error);
                $scope.viewState = 'error';
              }
          );
        });
      }])

    .controller('UserLeaveOrganizationController', ['$scope', '$stateParams', 'UserContext', 'OrganizationService',
      function ($scope, $stateParams, UserContext, OrganizationService) {

        $scope.busyPromise = OrganizationService.get({organizationId: $stateParams.organizationId}, function (organization) {
          $scope.viewState = 'confirm-leave';
          $scope.organization = organization;
          $scope.membership = UserContext.membershipOf(organization.organizationId);

          $scope.leave = function (membership) {
            console.log(membership);
            $scope.busyPromise = OrganizationService.dropMembershipToOrganization(
                membership.organizationId,
                membership.membershipId,
                function () {
                  $scope.viewState = 'leave-success';
                },
                function (error) {
                  $scope.viewState = 'error';
                }
            );
          };

        });
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
            $scope.busyPromiseSearch = UserService.query({usernamePattern: pattern, size: 1}, function (page) {
              $scope.page = page;
              $scope.people = page.content;
            }).$promise;
          } else {
            $scope.page = null;
            $scope.people = [];
          }
        };

        $scope.confirm = function (member) {
          $scope.invitedMember = member;
          $scope.viewState = 'invite-confirm';
        };

        $scope.invite = function (member) {

          // fetch and assign a new UUID from the server
          UUID.get({name: "identifier"}, function (newMembershipId) {

            // call server with an invite-request !
            $scope.busyPromise = OrganizationService.InviteUserToOrganization($scope.organization, newMembershipId.identifier, member, function () {
              $scope.viewState = 'invite-success';
            }, function (error) { /*reportError*/
              console.log("Error, dammit!", error);
              $scope.viewState = 'error';
            });
          });

        };
      }])

    .controller('UserJoinOrganizationController', ['$scope', '$stateParams', 'OrganizationService', 'UUID',
      function ($scope, $stateParams, OrganizationService, UUID) {
        $scope.viewState = 'apply-form';
        $scope.organization = OrganizationService.get({organizationId: $stateParams.organizationId});
        $scope.join = function (addtionalMessage) {

          // fetch and assign a new UUID from the server
          UUID.get({name: "identifier"}, function (newMembershipId) {

            // call server with a join-request !
            $scope.busyPromise = OrganizationService.applyForMembershipToOrganization(
                $scope.organization,
                newMembershipId.identifier,
                $scope.currentUser.name,
                !addtionalMessage ? "" : addtionalMessage,
                function () {
                  $scope.viewState = 'apply-success';
                },
                function (error) { /*reportError*/
                  console.log("Error, dammit!", error);
                  $scope.viewState = 'error';
                }
            );
          });
        };
      }])

    ;

