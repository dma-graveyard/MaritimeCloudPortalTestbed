describe('toLatLngObjects', function() {

  // Arrange
  var scope, controller, serviceInstanceService;

  beforeEach(angular.mock.module("mcp.search.services"));
  beforeEach(angular.mock.module("mcp.dataservices"));
  beforeEach(angular.mock.module("mcp.mapservices"));
  beforeEach(angular.mock.module("leaflet-directive"));

  beforeEach(angular.mock.inject(function($rootScope, ServiceInstanceService) {
    scope = $rootScope.$new();
    serviceInstanceService = ServiceInstanceService;
  }));

  beforeEach(angular.mock.inject(function($controller) {
    controller = $controller("SearchServiceMapController", {$scope: scope});
  }));

  it('should have a list of services', function() {
    expect(scope.services).to.have.length(serviceInstanceService.query().length);
  });

});
