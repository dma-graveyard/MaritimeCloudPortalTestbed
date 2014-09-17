'use strict';

/**
 * # mapService
 * 
 * The 'mapService' module provides varous helper functions for switching between
 * 'Maritime Cloud Portal Shapes' (MCP shapes), 'Angular Leaflet Directive Paths' 
 * (ALD paths) and Leaflet Layers (layers).
 * 
 * MCP shapes are a JSON representation of the Maritim Cloud java shapes.
 */
var mapservices = angular.module('mcp.mapservices', []);

mapservices.factory('mapService', ['$rootScope', function($rootScope) {

    function getLayerShapeType(layer) {

      if (layer instanceof L.Circle) {
        return 'circle';
      }

      if (layer instanceof L.Marker) {
        return 'marker';
      }

      if ((layer instanceof L.Polyline) && !(layer instanceof L.Polygon)) {
        return 'polyline';
      }

      if (layer instanceof L.MultiPolygon) {
        return 'multi-polygon';
      }

      if ((layer instanceof L.Polygon) && !(layer instanceof L.Rectangle)) {
        return 'polygon';
      }

      if (layer instanceof L.Rectangle) {
        return 'rectangle';
      }

    }

    function isGeometry(layer) {
      return getLayerShapeType(layer);
    }

    function isMarkerLayer(layer) {
      return getLayerShapeType(layer) === 'marker';
    }

    function isPolylineLayer(layer) {
      return getLayerShapeType(layer) === 'polyline';
    }

    function isCircleLayer(layer) {
      return getLayerShapeType(layer) === 'circle';
    }

    function isRectangleLayer(layer) {
      return getLayerShapeType(layer) === 'rectangle';
    }

    function isPolygonLayer(layer) {
      return getLayerShapeType(layer) === 'polygon';
    }

    function isMultiPolygonLayer(layer) {
      return getLayerShapeType(layer) === 'multi-polygon';
    }

    /**
     * Converts an object with layer members to an array of MCP shapes
     * @param {Object} layersObject Object of layers
     * @returns {Array} an array of shapes
     */
    function layersToShapes(layersObject) {

      var shapes = [];
      Object.keys(layersObject).forEach(function(prop) {
        shapes.push(layerToShape(layersObject[prop]));
      });
      return shapes;
    }

    function layerToShape(layer) {

      if (isCircleLayer(layer)) {
        return {
          type: 'circle',
          'center-latitude': layer.getLatLng().lat,
          'center-longitude': layer.getLatLng().lng,
          radius: layer.getRadius()
        };
      }

      if (isRectangleLayer(layer)) {
        var bounds = layer.getBounds();
        return {
          type: 'rectangle',
          topLeftLatitude: bounds.getNorthWest().lat,
          topLeftLongitude: bounds.getNorthWest().lng,
          buttomRightLatitude: bounds.getSouthEast().lat,
          buttomRightLongitude: bounds.getSouthEast().lng
        };
      }

      if (isPolygonLayer(layer)) {
        return {
          type: 'polygon',
          points: latLngsToCoordinates(layer.getLatLngs())
        };
      }

      console.log('Unknown layer type !?!?!', layer);

    }

    /**
     * Converts an array of 'MCP shapes' to an array of 'ALD paths'
     * @param {array} shapes of MCP shapes
     * @returns {array} of ALD paths
     */
    function shapesToPaths(shapes) {
      var i, paths = {};
      for (i = 0; i < shapes.length; i++) {
        paths['p' + i] = shapeToPath(shapes[i]);
      }
      return paths;
    }

    /**
     * Parse and converts a MCP shape to a ALD path
     * @param {Object} shape
     * @returns {Object} a corresponding Path object
     */
    function shapeToPath(shape) {
      console.log('shape', shape);
      var path = {
        type: shape.type,
        //color: '#008000',
        //color: '#ff612f',
        weight: 2,
        fillColor: '#ff69b4'
      };

      if (shape.type === 'polygon') {
        path.latlngs = coordsToLatLngs(shape.points);
        return path;
      }
      if (shape.type === 'circle') {
        path.radius = shape.radius;
        path.latlngs = {
          lat: shape['center-latitude'],
          lng: shape['center-longitude']
        };
        return path;
      }
      if (shape.type === 'rectangle') {
        path.radius = shape.radius;
        path.latlngs = [
          {
            lat: shape.buttomRightLatitude,
            lng: shape.buttomRightLongitude
          },
          {
            lat: shape.topLeftLatitude,
            lng: shape.topLeftLongitude
          }
        ];
        return path;
      }
      console.log("unknown area type", shape);
      error('unknown area type!');
    }

    /**
     * Converts array of LatLngs to array of coordinate arrays ([Long,Lat]-pairs)
     * @param {array} latLngs
     * @returns {array} Array of coordinate arrays ([Long,Lat]-pairs)
     */
    function latLngsToCoordinates(latLngs) {
      var coords = [];
      latLngs.forEach(function(e) {
        coords.push(latLngToCoordinate(e));
      });
      return coords;
    }

    /**
     * Converts a LatLng to a [Long,Lat]-pair coordinate array
     * @param {LatLng} latLng
     * @returns {array} Coordinate array ([Long,Lat]-pair)
     */
    function latLngToCoordinate(latLng) {
      return [latLng.lng, latLng.lat];
    }


    /**
     * converts arrays of coordinates (array based pairs) to arrays of objects
     * @param {array} array of coordinates (i.e. [longitude, latitude]-arrays )
     * @returns {array} of ALD latlngs  
     */
    function coordsToLatLngs(array) {
      if (array.length === 2 && typeof array[0] === 'number' && typeof array[1] === 'number') {
        return {lat: array[1], lng: array[0]};
      }

      var a = [];
      array.forEach(function(e) {
        a.push(coordsToLatLngs(e));
      });
      return a;
    }

    /**
     * Helper function to create the necesary options object.
     * FIXME: when ALD is updated from 0.7.8 this function can probably be removed
     * @see https://github.com/tombatossals/angular-leaflet-directive/commit/44efd922f3043479e1f5d2483740a58bb7e27336
     * @returns {object} options object for drawing on the map
     */
    function createDrawingOptions() {
      return {
        draw: {
          polyline: false,
          polygon: {
            allowIntersection: false,
            showArea: true,
            drawError: {
              color: '#b00b00',
              timeout: 1000
            }
            //shapeOptions: {
            //  color: '#bada55'
            //}
          },
          circle: {
            //shapeOptions: {
            //  color: '#662d91'
            //}
          },
          marker: false
        },
        edit: {featureGroup: new L.FeatureGroup()}
      };
    }

    /**
     * Fits the map to the bounds of the contained layers of type 'Geomitry' 
     * @param {Map} map
     */
    function fitToGeomitryLayers(map) {
      var featureGroup = L.featureGroup();
      map.eachLayer(function(l) {
        if (isGeometry(l)) {
          featureGroup.addLayer(l);
        }
      });
      map.fitBounds(featureGroup.getBounds());
    }

    return {
      coordsToLatLngs: coordsToLatLngs,
      createDrawingOptions: createDrawingOptions,
      fitToGeomitryLayers: fitToGeomitryLayers,
      getLayerShapeType: getLayerShapeType,
      isCircleLayer: isCircleLayer,
      isGeometry: isGeometry,
      isMarkerLayer: isMarkerLayer,
      isMultiPolygonLayer: isMultiPolygonLayer,
      isPolygonLayer: isPolygonLayer,
      isPolylineLayer: isPolylineLayer,
      isRectangleLayer: isRectangleLayer,
      shapesToPaths: shapesToPaths,
      latLngsToCoordinates: latLngsToCoordinates,
      layersToShapes: layersToShapes
    };
  }]);

