describe('mapservices', function () {

  // Arrange
  var layers = {
    marker: L.marker([1.2, 3.4]),
    polyline: L.polyline([[1, 2], [3, 4], [5, 6]]),
    circle: L.circle([1, 2], 300),
    rectangle: L.rectangle([[1, -2], [2, -1]]), //southWest, NorthEast
    polygon: L.polygon([[1, 2], [3, 4], [5, 6]]),
    multiPolygon: L.multiPolygon([
      [
        [1, 1], [2, 2], [3, 3]
      ],
      [
        [-1, -1], [-2, -2], [-3, -3], [-4, -4]
      ]])
  },
  mcpShapes = [
    {
      "type": "circle",
      "center-latitude": 1,
      "center-longitude": 2,
      "radius": 300
    },
    {
      type: 'rectangle',
      topLeftLatitude: 2,
      topLeftLongitude: -2,
      buttomRightLatitude: 1,
      buttomRightLongitude: -1
    },
    {
      type: "polygon",
      points: [[2, 1], [4, 3], [6, 5]]
    }
  ],
      scope, mapService;

  beforeEach(angular.mock.module("mcp.mapservices"));
  beforeEach(angular.mock.module("leaflet-directive"));
  beforeEach(angular.mock.inject(function ($rootScope) {
    inject(function ($injector) {
      mapService = $injector.get('mapService');
    });
    scope = $rootScope.$new();
  }));

  it('should identify leaflet marker layer', function () {
    expect(mapService.isMarkerLayer(layers.marker)).to.be.true;
    expect(mapService.isMarkerLayer(layers.polyline)).to.be.false;
    expect(mapService.isMarkerLayer(layers.circle)).to.be.false;
    expect(mapService.isMarkerLayer(layers.rectangle)).to.be.false;
    expect(mapService.isMarkerLayer(layers.polygon)).to.be.false;
    expect(mapService.isMarkerLayer(layers.multiPolygon)).to.be.false;
  });
  it('should identify leaflet circle layer', function () {
    expect(mapService.isCircleLayer(layers.marker)).to.be.false;
    expect(mapService.isCircleLayer(layers.polyline)).to.be.false;
    expect(mapService.isCircleLayer(layers.circle)).to.be.true;
    expect(mapService.isCircleLayer(layers.rectangle)).to.be.false;
    expect(mapService.isCircleLayer(layers.polygon)).to.be.false;
    expect(mapService.isCircleLayer(layers.multiPolygon)).to.be.false;
  });
  it('should identify leaflet polyline layer', function () {
    expect(mapService.isPolylineLayer(layers.marker)).to.be.false;
    expect(mapService.isPolylineLayer(layers.polyline)).to.be.true;
    expect(mapService.isPolylineLayer(layers.circle)).to.be.false;
    expect(mapService.isPolylineLayer(layers.rectangle)).to.be.false;
    expect(mapService.isPolylineLayer(layers.polygon)).to.be.false;
    expect(mapService.isPolylineLayer(layers.multiPolygon)).to.be.false;
  });
  it('should identify leaflet rectangle layer', function () {
    expect(mapService.isRectangleLayer(layers.marker)).to.be.false;
    expect(mapService.isRectangleLayer(layers.polyline)).to.be.false;
    expect(mapService.isRectangleLayer(layers.circle)).to.be.false;
    expect(mapService.isRectangleLayer(layers.rectangle)).to.be.true;
    expect(mapService.isRectangleLayer(layers.polygon)).to.be.false;
    expect(mapService.isRectangleLayer(layers.multiPolygon)).to.be.false;
  });
  it('should identify leaflet polygon layer', function () {
    expect(mapService.isPolygonLayer(layers.marker)).to.be.false;
    expect(mapService.isPolygonLayer(layers.polyline)).to.be.false;
    expect(mapService.isPolygonLayer(layers.circle)).to.be.false;
    expect(mapService.isPolygonLayer(layers.rectangle)).to.be.false;
    expect(mapService.isPolygonLayer(layers.polygon)).to.be.true;
    expect(mapService.isPolygonLayer(layers.multiPolygon)).to.be.false;
  });
  it('should identify leaflet multiPolygon layer', function () {
    expect(mapService.isMultiPolygonLayer(layers.marker)).to.be.false;
    expect(mapService.isMultiPolygonLayer(layers.polyline)).to.be.false;
    expect(mapService.isMultiPolygonLayer(layers.circle)).to.be.false;
    expect(mapService.isMultiPolygonLayer(layers.rectangle)).to.be.false;
    expect(mapService.isMultiPolygonLayer(layers.polygon)).to.be.false;
    expect(mapService.isMultiPolygonLayer(layers.multiPolygon)).to.be.true;
  });
  it('should convert mcp coordinates from array pairs to LatLng objects', function () {
    var array = [[1, 2], [3, 4], [5, 6]];
    expect(mapService.coordsToLatLngs(array)).to.have.length(3);
    expect(mapService.coordsToLatLngs(array)).to.deep.equal([{lat: 2, lng: 1}, {lat: 4, lng: 3}, {lat: 6, lng: 5}]);
  });
  it('should convert array of LatLng objects to array of mcp coordinates', function () {
    var latLngs = [{lat: 2, lng: 1}, {lat: 4, lng: 3}, {lat: 6, lng: 5}];

    var expected = [[1, 2], [3, 4], [5, 6]];
    var actual = mapService.latLngsToCoordinates(latLngs);

    expect(actual).to.have.length(3);
    expect(actual).to.deep.equal(expected);
  });
  it('should convert layers to shapes', function () {
    var myLayers = {
      circle: layers.circle,
      rectangle: layers.rectangle,
      polygon: layers.polygon
    },
    shapes = mapService.layersToShapes(myLayers);

    //console.log(shapes[1]);
    //console.log(mcpShapes[1]);
    expect(shapes).to.have.length(3);
    expect(shapes[0]).to.deep.equal(mcpShapes[0]);
    expect(shapes[1]).to.deep.equal(mcpShapes[1]);
    expect(shapes[2]).to.deep.equal(mcpShapes[2]);
    //expect(shapes).to.deep.equal([mcpShapes[0], mcpShapes[1], {lat: 6, lng: 5}]);
  });
});

describe('mapservices filterServicesAtLocation should filter to services that contains the filterLocation', function () {

  var mapService,
      services = [
        {id: 'simplePolygon', coverage: [{type: "polygon", points: [[0, 0], [2, 0], [2, 2], [-2, 2]]}]},
        {id: 'simpleCircle', coverage: [{type: "circle", "center-latitude": 0, "center-longitude": 0, "radius": 111320}]},
        {
          id: 'complexPolygonCircle',
          coverage: [
            {type: "polygon", points: [[0, 0], [2, 0], [2, 2], [-2, 2]]},
            {"type": "circle", "center-latitude": 7, "center-longitude": -7, "radius": 40000}
          ]
        }
      ];

  beforeEach(angular.mock.module("mcp.mapservices"));
  beforeEach(angular.mock.module("leaflet-directive"));
  beforeEach(angular.mock.inject(function ($rootScope) {
    inject(function ($injector) {
      mapService = $injector.get('mapService');
    });
    scope = $rootScope.$new();
  }));

  it('empty list of services should give empty result', function () {
    expect(mapService.filterServicesAtLocation(L.latLng(0, 0), [])).to.have.length(0);
  });

  it('should select single service containing location', function () {
    expect(mapService.filterServicesAtLocation(L.latLng(0, 0), [services[1]])).to.have.length(1);
    expect(mapService.filterServicesAtLocation(L.latLng(1, 1), [services[1]])).to.have.length(1);
  });

  it('should select two services containing location', function () {
    expect(mapService.filterServicesAtLocation(L.latLng(0, 0), [services[0], services[1]])).to.have.length(2);
    expect(mapService.filterServicesAtLocation(L.latLng(1, 1), [services[0], services[1]])).to.have.length(2);
  });

  it('should not select any service when service is out of reach from location', function () {
    expect(mapService.filterServicesAtLocation(L.latLng(0, 2), [services[1]])).to.have.length(0);
    expect(mapService.filterServicesAtLocation(L.latLng(0, 3), [services[0], services[1]])).to.have.length(0);
  });

  it('should select service with complex coverage', function () {
    expect(mapService.filterServicesAtLocation(L.latLng(0, 2), [services[2]])).to.have.length(1);
    expect(mapService.filterServicesAtLocation(L.latLng(0, 8), [services[2]])).to.have.length(0);
  });

});
