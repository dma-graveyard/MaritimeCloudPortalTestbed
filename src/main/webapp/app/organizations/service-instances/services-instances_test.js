describe('toLatLngObjects', function() {

  // Arrange
  var scope, controller, $stateParams;

  beforeEach(angular.mock.module("mcp.organizations.services"));
  beforeEach(angular.mock.module("mcp.dataservices"));
  beforeEach(angular.mock.module("leaflet-directive"));

  beforeEach(angular.mock.inject(function($rootScope) {
    scope = $rootScope.$new();
    $stateParams = {};
    scope.service = {
      serviceInstanceId: "dk",
      providerId: "dmi",
      specificationId: "imo-mis-rest",
      name: "DMI METOC on route (Denmark)",
      summary: "Route based Meteorological Services for the waters surrounding Denmark including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
      coverage: [
        {
          type: "polygon",
          points: [[-73.47656249999999, 59.33318942659219], [-73.47656249999999, 84.36725432248352], [-8.4375, 84.36725432248352], [-8.4375, 59.33318942659219]]
        },
        {
          "type": "circle",
          "center-latitude": 75.30888448476105,
          "center-longitude": -73.828125,
          "radius": 456789
        }
      ]
    };
  }));

  beforeEach(angular.mock.inject(function($controller) {
    controller = $controller("ServiceInstanceDetailsController", {$scope: scope});
  }));
  
});
