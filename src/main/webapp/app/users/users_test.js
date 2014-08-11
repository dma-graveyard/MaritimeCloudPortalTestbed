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
    console.log(scope.users[0]);
    //scope.users[0].$delete();
    //expect(scope.users[0].username).to.equal("Tintin");
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