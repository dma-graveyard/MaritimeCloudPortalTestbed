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

    httpBackend.whenGET(/rest\/users/).respond(
        [
          {"emailAddress": "tintin@dma.org", "username": "Tintin"},
          {"emailAddress": "admin@dma.dk", "username": "admin"},
          {"emailAddress": "hadock@dma.org", "username": "Haddock"}
        ]);
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
    // for 'user.username' to hav been initially called 
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
    expect(scope.alertMessages).to.be.null;
    expect(scope.usernameAlreadyExist).to.be.true;
    
  });
  it('should flag new username as not already existing', function() {
    
    // GIVEN a userlist not containing a user with the username 'A'
    httpBackend.expectGET(/rest\/users\/A\/exist/).respond({usernameExist: false});
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
    httpBackend.expectGET(/rest\/users\/Ann\/exist/).respond({usernameExist: true});
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
    scope.usernameAlreadyExist = false;
    expect(scope.isValid(true)).to.be.true;
    
  });
});