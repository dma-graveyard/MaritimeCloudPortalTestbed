'use strict';

/**
 * # mapService
 * 
 * The 'mapService' module provides varous helper functions for switching between
 * 'Maritime Cloud Portal Shapes' (MCP shapes), 'Angular Leaflet Directive Paths' 
 * (ALD paths) and Leaflet Layers (layers).
 * 
 * It also contains various filters and directives for smooth convertion and presentation
 * of geographical data.
 * 
 * MCP shapes are a JSON representation of the Maritim Cloud java shapes.
 */
var mapservices = angular.module('mcp.mapservices', []);

mapservices.factory('mapService', ['$rootScope', function ($rootScope) {

    var Styles = {
      DEFAULT: {
        //color: '#008000',
        //color: '#ff612f', //orange
        color: '#ffb752',
        fillOpacity: 0.106,
        //color: '#ffff2f',
        weight: 1,
        //fillColor: '#ff69b4'
        //fillColor: '#ffffff'
      },
      HIGHLIGHT: {
        weight: 2,
        //dashArray: [5,10],
        color: '#008000',
        fillOpacity: 0.206,
      }
    }

    L.FeatureGroup.prototype.highlight = function () {
      if (!this.originalStyle) {
        this.originalStyle = this.getLayers()[0].options;
        this.setStyle(Styles.HIGHLIGHT);
      }
    };

    L.FeatureGroup.prototype.resetStyle = function () {
      if (this.originalStyle) {
        this.setStyle(this.originalStyle);
        delete this.originalStyle;
      }
    };

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
      Object.keys(layersObject).forEach(function (prop) {
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
     * Parse and converts a MCP shape to a Leaflet Layer
     * @param {Object} shape
     * @returns {Object} a corresponding Layer object
     */
    function shapeToLayer(shape) {
      var options = Styles.DEFAULT;
      if (shape.type === 'polygon') {
        return L.polygon(coordsToLatLngs(shape.points), options);
      }
      if (shape.type === 'circle') {
        var latlngs = {
          lat: shape['center-latitude'],
          lng: shape['center-longitude']
        };
        return L.circle(latlngs, shape.radius, options);
      }
      if (shape.type === 'rectangle') {
        var latlngBounds = [
          {
            lat: shape.buttomRightLatitude,
            lng: shape.buttomRightLongitude
          },
          {
            lat: shape.topLeftLatitude,
            lng: shape.topLeftLongitude
          }
        ];
        return L.rectangle(latlngBounds, options);

      }
      console.log("unknown area type", shape);
      error('unknown area type!');
    }

    function servicesToLayers(services, featureGroupCallback) {
      // associative map 
      //var servicesAsLayers = {};

      var servicesLayers = [];

      // iterate services, and for each, convert its shapes to layers and 
      // add it to a layerGroup, finally add the layerGroup to the array-object 
      services.forEach(function (service) {
        var featureGroup = L.featureGroup();
        featureGroup.service = service;
        if (featureGroupCallback) {
          featureGroupCallback(featureGroup);
        }
        service.coverage.forEach(function (shape) {
          featureGroup.addLayer(shapeToLayer(shape));
        });
        servicesLayers.push(featureGroup);
      });

      return servicesLayers;
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
      latLngs.forEach(function (e) {
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
      array.forEach(function (e) {
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
     * Filters the source array of services based on a location
     * @param {type} filterLocation
     * @param {type} services source
     * @returns {Array} an array of the services at the supplied filterLocation
     */
    function filterServicesAtLocation(filterLocation, services) {

      // convert to layers so that we can use leaflet bounding boxs
      var serviceLayers = L.featureGroup(servicesToLayers(services)),
          filteredServices = [];

      // iterate and select all those whith a bounding box that contains the location
      serviceLayers.eachLayer(function (serviceLayer) {
        //console.log('filterLocation',filterLocation);
        if (serviceLayer.getBounds().contains(filterLocation))
          filteredServices.push(serviceLayer.service);
      });

      return filteredServices;
    }


    /**
     * Fits the map to the bounds of the contained layers of type 'Geomitry' 
     * @param {Map} map
     */
    function fitToGeomitryLayers(map) {
      var featureGroup = L.featureGroup();
      map.eachLayer(function (l) {
        if (isGeometry(l)) {
          featureGroup.addLayer(l);
        }
      });
      map.fitBounds(featureGroup.getBounds());
    }

    //function nm2km(nm) {
    //  if (!nm) {
    //    return undefined;
    //  }
    //  return Math.round(nm * 1852 / 1000);
    //}
    //
    //function km2nm(km) {
    //  if (!km) {
    //    return undefined;
    //  }
    //  return Math.round(km * 1000 / 1852);
    //}
    //
    //function m2nm(m) {
    //  if (!m) {
    //    return undefined;
    //  }
    //  return Math.round(m / 1852);
    //}

    /**
     * Takes on decimal degrees argument and convert it to degrees, minutes, seconds. 
     * The original value is retained in the resulting object in the property 'decDeg'
     * 
     * @param {type} decimalDegrees A decimal number reflecting a latitude or longitude degree.
     * @param {type} direction a direction indicator, eg. N, S, E or W (will not impact 
     * calculations, just propagated to the property 'dir' for convinience)
     * @returns {object}
     */
    function decimalDegreesToDegreesMinutesSeconds(decimalDegrees, direction) {
      var value = Math.abs(decimalDegrees),
          degrees = Math.floor(value),
          minutesWithFraction = (value - degrees) * 60,
          minutes = Math.floor(minutesWithFraction),
          seconds = Math.round((value - degrees - minutes / 60) * 1000 * 3600) / 1000;

      return {
        dir: direction,
        deg: degrees,
        min: minutes,
        sec: seconds,
        decDeg: decimalDegrees,
        decMin: minutesWithFraction
      };
    }

    function latLngToDms(latlng) {
      return {
        latDms: decimalDegreesToDegreesMinutesSeconds(latlng.lat, latlng.lat >= 0 ? "N" : "S"),
        lngDms: decimalDegreesToDegreesMinutesSeconds(latlng.lng, latlng.lng >= 0 ? "E" : "W")
      };
    }

    return {
      coordsToLatLngs: coordsToLatLngs,
      createDrawingOptions: createDrawingOptions,
      filterServicesAtLocation: filterServicesAtLocation,
      fitToGeomitryLayers: fitToGeomitryLayers,
      getLayerShapeType: getLayerShapeType,
      isCircleLayer: isCircleLayer,
      isGeometry: isGeometry,
      isMarkerLayer: isMarkerLayer,
      isMultiPolygonLayer: isMultiPolygonLayer,
      isPolygonLayer: isPolygonLayer,
      isPolylineLayer: isPolylineLayer,
      isRectangleLayer: isRectangleLayer,
      servicesToLayers: servicesToLayers,
      shapeToLayer: shapeToLayer,
      shapesToPaths: shapesToPaths,
      latLngsToCoordinates: latLngsToCoordinates,
      latLngToDms: latLngToDms,
      layersToShapes: layersToShapes
    };
  }])

    .directive('latitude', ['mapService', function (mapService) {
        return positionDirective('latitude', formatLatitude, parseLatitude);
      }])

    .directive('longitude', ['mapService', function (mapService) {
        return positionDirective('longitude', formatLongitude, parseLongitude);
      }])

    .filter('distance', ['mapService', function (mapService) {
        return function (distanceInMeters, format) {
          var digits;

          if (!distanceInMeters)
            return null;

          if (format && format.match(/\d/))
            digits = format.match(/\d/);

          digits = digits || 2;

          if (format && format.match(/nm/)) {
            return (distanceInMeters / 1852).toFixed(digits) + ' nmi';
          }

          if (distanceInMeters > 10000) {
            return (distanceInMeters / 1000).toFixed(digits) + ' km';
          }
          return distanceInMeters.toFixed(digits) + ' m';
        };
      }])

    .filter('latlng2dms', ['mapService',
      function (mapService) {
        /**
         * Filter that formats a LatLang value into a Degeres, Minutes, Seconds 
         * format according to a set of patterns:
         * 
         * argument 1: a pattern where 'lat' and 'lng' kaewords are replaced with their formatted values
         * argument 2: a pattern that specifies how the 'lat' part above should be formatted. Defaults to ""
         * argument 3: a pattern that specifies how the 'lng' part above should be formatted. Will use the lat-pattern if omitted.
         * 
         * Pattern: [other]*[symbol[maxLeadingZeroes][fixedDecimals][other]*]*
         *  
         *  symbol: [idDmMs] - symbol designating the fractional value to include:
         *      i: dms.dir    - the directional indicator (usually N, S, E or W)
         *      d: dms.deg    - the integer part of the degrees fraction
         *      D: dms.decDeg - the degrees including decimals
         *      m: dms.min    - the integer minutes part of the degrees
         *      M: dms.decMin - the minutes part of the degrees including decimals
         *      s: dms.sec    - the seconds part of the degrees including decimals
         *      
         *  maxLeadingZeroes: [digit] - the maximum number of leading zeroes added to the fraction value 
         *  fixedDecimals: [digit]    - the fixed size of decimal digits (if supplied) 
         *  
         * @param {type} dms
         * @param {type} latlngFormat
         * @param {type} latFormat
         * @param {type} lngFormat
         * @returns {String}
         */
        function formatLatLngAsDms(dms, latlngFormat, latFormat, lngFormat) {
          latFormat = latFormat || "i d2° m2' s21''";
          var lat = formatDmsPart(dms.latDms, latFormat),
              lng = formatDmsPart(dms.lngDms, lngFormat || latFormat);
          latlngFormat = latlngFormat || 'lat - lng';
          return latlngFormat.replace(/lat/, lat).replace(/lng/, lng);
        }

        function formatDmsPart(dms, format) {
          var formatted = format,
              regexMatchSymbol = /[idmsDM]\d*/g,
              symbolMatch;

          while (symbolMatch = regexMatchSymbol.exec(format)) {
            formatted = formatFraction(symbolMatch[0], dms, formatted);
          }

          // replace '' with " 
          formatted = formatted.replace("''", '"');
          return formatted;
        }

        function formatFraction(symbolFormat, dms, target) {
          var symbol = symbolFormat[0],
              digits = symbolFormat.match(/\d+/),
              value,
              maxLeadingZeroes,
              fixedDecimals;

          if (digits) {
            maxLeadingZeroes = digits[0][0] || 0;
            fixedDecimals = digits[0][1];
          }

          value = selectFractionValue(dms, symbol);
          value = fixedDecimals ? value.toFixed(fixedDecimals) : value;
          value = maxLeadingZeroes ? leadingZeroes(maxLeadingZeroes, value) : value;

          return target.replace(symbolFormat, value);
        }

        function selectFractionValue(dms, symbol) {
          return symbol === 'i' ? dms.dir :
              symbol === 'd' ? dms.deg :
              symbol === 'D' ? dms.decDeg :
              symbol === 'm' ? dms.min :
              symbol === 'M' ? dms.decMin :
              symbol === 's' ? dms.sec : '?';
        }

        function leadingZeroes(size, value) {
          var s = '' + value;
          while (s.indexOf('.') > 0 && s.indexOf('.') < size || s.length < size) {
            s = '0' + s;
          }
          return s;
        }

        return function (input, latlngFormat, latFormat, lngFormat) {
          input = input || {lat: 0, lon: 0};
          var dms = mapService.latLngToDms(input);
          return formatLatLngAsDms(dms, latlngFormat, latFormat, lngFormat);
        };
      }])
    ;

