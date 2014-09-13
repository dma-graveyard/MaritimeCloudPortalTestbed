describe('toLatLngObjects', function() {

  // Arrange
  var scope, controller, $stateParams, SpecificationService;

  beforeEach(angular.mock.module("mcp.organizations.services"));
  beforeEach(angular.mock.module("mcp.dataservices"));
  beforeEach(angular.mock.module("leaflet-directive"));

  beforeEach(angular.mock.inject(function($rootScope) {
    scope = $rootScope.$new();
    $stateParams = {};
    SpecificationService = {};
    scope.service = {
      provider: {
        name: 'dmi'
      },
      specification: {
        id: 'imo-mis-rest',
      },
      key: {
        specificationId: "imo-mis-rest",
        providerId: "dmi",
        instanceId: "dk"
      },
      id: "dk",
      name: "DMI METOC on route (Denmark)",
      description: "Route based Meteorological Services for the waters surrounding Denmark including forecasting and warnings of weather, climate and related environmental conditions in the atmosphere, on land and at sea.",
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

  beforeEach(angular.mock.inject(function($controller, SpecificationService, leafletData) {
    controller = $controller("ServiceInstanceDetailsController", {$scope: scope, $stateParams: $stateParams, SpecificationService: SpecificationService, leafletData: leafletData});
  }));

  it('should convert toLatLngObjects from array pairs to objects', function() {
    var array = [[1, 2], [3, 4], [5, 6]];
    expect(coordsToLatLngs(array)).to.have.length(3);
    expect(coordsToLatLngs(array)).to.deep.equal([{lat: 2, lng: 1}, {lat: 4, lng: 3}, {lat: 6, lng: 5}]);
  });
  
});
