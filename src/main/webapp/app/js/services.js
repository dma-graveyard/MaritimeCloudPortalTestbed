'use strict';

/* Services */

var iamServices = angular.module('iamServices', ['ngResource']);

iamServices.factory('UserService', ['$resource',
  function($resource) {
    return $resource('/rest/users/:username', {}, {
      query: {method: 'GET', params: {username: ''}, isArray: true},
      signUp: {method: 'POST', params: {}, isArray: false}
    });
  }]);

