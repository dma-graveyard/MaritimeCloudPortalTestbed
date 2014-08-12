mcpAppDev = angular.module('mcpAppDev', ['mcpApp', 'ngMockE2E']);
mcpAppDev.run(function($httpBackend) {
  
//  $httpBackend.whenGET(/rest\/users\?usernameExist=admin$/).respond({usernameExist: true});
//  $httpBackend.whenGET(/rest\/users\?usernameExist=/).respond({usernameExist: false});
  $httpBackend.whenGET(/\//).passThrough();
  $httpBackend.whenPOST(/\//).passThrough();
  
});