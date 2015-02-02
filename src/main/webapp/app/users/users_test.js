describe('UserListController', function() {

  // Arrange
  var scope, controller, httpBackend;

  beforeEach(angular.mock.module("mcp.users"));
  beforeEach(angular.mock.module("mcp.dataservices"));

  beforeEach(angular.mock.inject(function($rootScope) {
    scope = $rootScope.$new();
  }));

  beforeEach(angular.mock.inject(function($httpBackend) {
    httpBackend = $httpBackend;

    httpBackend.whenGET(/rest\/api\/users/).respond(
        {
          content: [
            {"emailAddress": "tintin@dma.org", "username": "Tintin"},
            {"emailAddress": "admin@dma.dk", "username": "admin"},
            {"emailAddress": "hadock@dma.org", "username": "Haddock"}]
        }
    );
  }));

  beforeEach(angular.mock.inject(function($controller, UserService) {
    controller = $controller("UserListController", {$scope: scope, UserService: UserService});
    httpBackend.flush();
  }));

  afterEach(function() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  it('UserListController contains users after creation', function() {
    expect(scope.users).to.have.length(3);
    expect(scope.users[0].username).to.equal("Tintin");
  });

  it("testing the test API", function() {

    //ASSERT
    assert.equal(1, 1);
    assert.notEqual(1, 2);

    //EXPECT
    expect(1 + 2).to.equal(3);
    expect(1 + 2).to.be.equal(3);

  });
});

// UserSignupController
describe('UserSignupController', function() {

  // Arrange
  var scope, userSignupController, httpBackend;

  beforeEach(angular.mock.module('ui.router'));
  beforeEach(angular.mock.module("mcp.users"));
  beforeEach(angular.mock.module("mcp.dataservices"));

  beforeEach(angular.mock.inject(function($rootScope) {
    scope = $rootScope.$new();
  }));

  beforeEach(angular.mock.inject(function($httpBackend) {
    httpBackend = $httpBackend;
  }));

  beforeEach(angular.mock.inject(function($controller, UserService, $state) {
    userSignupController = $controller("UserSignupController", {$scope: scope, UserService: UserService, $state: $state});
    // listeners are always called during the first $digest 
    // loop after they was registered. We want the listener 
    // for 'user.username' to have been initially called 
    // before we start the test
    scope.$digest();
  }));

  afterEach(function() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  it('Should be clean after creation', function() {

    expect(scope.user).to.be.empty;
    expect(scope.message).to.be.null;
    expect(scope.alert).to.be.null;
    expect(scope.usernameAlreadyExist).to.be.true;

  });
  it('should flag new username as not already existing', function() {

    // GIVEN a userlist not containing a user with the username 'A'
    httpBackend.expectGET(/rest\/api\/users\/A\/exist/).respond({usernameExist: false});
    // WHEN username is set to 'A':
    scope.user.username = 'A';
    // (and the listener will call resolveUniqueUsername)
    scope.$digest();
    // (which eventually reponds):
    httpBackend.flush();
    // THEN username is identified as being new:
    expect(scope.usernameAlreadyExist).to.be.false;
    expect(scope.isValid(true)).to.be.true;

  });
  it('should flag known username as already existing', function() {

    // GIVEN a userlist containing a user with the username 'Ann'
    httpBackend.expectGET(/rest\/api\/users\/Ann\/exist/).respond({usernameExist: true});
    // WHEN username is set to 'Ann'
    scope.user.username = 'Ann';
    scope.$digest();
    httpBackend.flush();
    // THEN username is still not unique:
    expect(scope.usernameAlreadyExist).to.be.true;
    expect(scope.isValid(true)).to.be.false;

  });
  it('should flag form as invalid when username already exist', function() {

    expect(scope.isValid(true)).to.be.false;
    //scope.user.username = 'Ann';
    scope.user.password = 'secret';
    scope.user.repeatedPassword = 'secret';
    scope.usernameAlreadyExist = false;
    expect(scope.isValid(true)).to.be.true;

  });
  it('should flag form as invalid when password equals username', function() {

    // GIVEN a userlist containing a user with the username 'Ann'
    httpBackend.expectGET(/rest\/api\/users\/Ann\/exist/).respond({usernameExist: false});
    // WHEN username is Ann and password is something else
    expect(scope.isValid(true)).to.be.false;
    scope.user.username = 'Ann';
    scope.$digest();
    httpBackend.flush();
    scope.user.password = 'secret';
    scope.user.repeatedPassword = 'secret';
    scope.usernameAlreadyExist = false;
    // THEN all is good
    expect(scope.isValid(true)).to.be.true;
    // WHEN password is changed to be the same as the username
    scope.user.password = 'Ann';
    scope.repeatedPassword = 'Ann';
    // THEN form is invalid
    expect(scope.isValid(true)).to.be.false;

  });
});

// UserConfirmEmailAddressController
describe('UserConfirmEmailAddressController', function() {

  // Arrange
  var scope, controller, httpBackend, userService;

  beforeEach(angular.mock.module('ui.router'));
  beforeEach(angular.mock.module("mcp.users"));
  beforeEach(angular.mock.module("mcp.dataservices"));

  beforeEach(angular.mock.inject(function($rootScope, $httpBackend, $controller, UserService) {
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    userService = UserService;
  }));

  afterEach(function() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  it('should activate account when username and activationId is valid', function() {

    // GIVEN a user with a valid activation id  
    var $stateParams = {username: 'aUser', activationId: 'VALID-ID-123-xyz'};
    httpBackend.whenPUT(/rest\/api\/users\/aUser/).respond(204);
    // WHEN controller is created
    UserConfirmEmailAddressController = controller("UserConfirmEmailAddressController", {$scope: scope, UserService: userService, $stateParams: $stateParams});
    // THEN initially the viewState is laoding
    expect(scope.viewState).to.equal('loading');
    // WHEN remote request is reolved 
    httpBackend.flush();
    // THEN the account is activated 
    expect(scope.viewState).to.equal('success');
  });
  it('should fail when username and activationId is invalid', function() {

    // GIVEN a user with an invalid activation id  
    var $stateParams = {username: 'aUser', activationId: 'INVALID-123-xyz'};
    httpBackend.whenPUT(/rest\/api\/users\/aUser/).respond(400);
    // WHEN controller is created
    UserConfirmEmailAddressController = controller("UserConfirmEmailAddressController", {$scope: scope, UserService: userService, $stateParams: $stateParams});
    // AND remote request is reolved 
    httpBackend.flush();
    // THEN the account is still not activated 
    expect(scope.viewState).to.equal('notFound');
  });
  it('should fail when username and activationId is unknown', function() {

    // GIVEN a another user that hits a server brain-fart (server error)
    var $stateParams = {username: 'anotherUser', activationId: 'SOME-123-xyz'};
    httpBackend.whenPUT(/rest\/api\/users\/anotherUser/).respond(500);
    // WHEN controller is created
    UserConfirmEmailAddressController = controller("UserConfirmEmailAddressController", {$scope: scope, UserService: userService, $stateParams: $stateParams});
    // AND remote request is reolved 
    httpBackend.flush();
    // THEN an error is indicated
    expect(scope.viewState).to.equal('error');
  });
});

// UserResetPasswordController
describe('UserResetPasswordController', function() {

  // Arrange
  var scope, controller, httpBackend, userService;

  beforeEach(angular.mock.module('ui.router'));
  beforeEach(angular.mock.module("mcp.users"));
  beforeEach(angular.mock.module("mcp.dataservices"));
  beforeEach(angular.mock.module("mcp.auth"));

  beforeEach(angular.mock.inject(function($rootScope, $httpBackend, $controller, AuthService) {
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    authService = AuthService;
    // setup a default user
    stateParams = {username: 'aUser', activationId: 'A-CONFIRMATION-ID-123-xyz'};
    userResetPasswordController = controller("UserResetPasswordController", {$scope: scope, $stateParams: stateParams, AuthService: authService, $controller: controller});

    // setup default responses
    httpBackend.whenPUT(/rest\/api\/users\/aUser/, {"userId":{"identifier":""},"currentPassword":"A-FAKE-CONFIRMATION-ID-123-xyz","changedPassword":"aNewSecret"}).respond(500, '');

  }));

  afterEach(function() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  });

  it('should change password when username, confirmationId and new password is valid', function() {

    // GIVEN a user with a valid confirmationID
    // WHEN the user supply a valid new password
    scope.changePassword('aNewSecret');
    httpBackend.expectPUT(/rest\/api\/users\/aUser/, {"userId":{"identifier":""},"currentPassword":"A-CONFIRMATION-ID-123-xyz","changedPassword":"aNewSecret"}).respond();
    httpBackend.flush();
    // THEN the state of the view should be 'success'
    expect(scope.viewState).to.equal('success');
  });
  it('should get the username and confirmationId from the URL stateParams', function() {

    // GIVEN a user with a valid confirmationID
    // WHEN the controller is constructed
    // Then the username is set
    expect(scope.user.username).to.equal("aUser");
    // AND the state of the view is 'supplyPassword'
    expect(scope.viewState).to.equal('supplyPassword');
  });
  it('should fail when e.g. confirmationId is wrong', function() {

    // GIVEN a user with a valid confirmationID
    // WHEN the user supply a valid new password
    stateParams.activationId = 'A-FAKE-CONFIRMATION-ID-123-xyz';
    scope.changePassword('aNewSecret');
    httpBackend.expectPUT(/rest\/api\/users\/aUser/, {"userId":{"identifier":""},"currentPassword":"A-FAKE-CONFIRMATION-ID-123-xyz","changedPassword":"aNewSecret"});
    httpBackend.flush();
    // THEN the state of the view should be 'success'
    expect(scope.viewState).to.equal('expired');
  });
  it('isValid should be false when password equals username', function() {

    // GIVEN a user with a valid confirmationID
    // WHEN the user supply a real secret as new password
    scope.user.password = "aSecret";
    scope.user.repeatedPassword = "aSecret";
    // THEN 
    expect(scope.isValid(true)).to.be.true;
    // WHEN the user supply the username as new password
    scope.user.username = "aUser";
    scope.user.repeatedPassword = "aUser";
    // THEN 
    expect(scope.isValid(true)).to.be.false;
  });
});