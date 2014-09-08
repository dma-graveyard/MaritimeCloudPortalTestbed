'use strict';

/* Controllers */

angular.module('mcp.organizations') // (notice: adds to existing module!)

    // hvad har vi brug for?
    
    // a service specification consists of
    // - a name
    // - a type
    // - a verbal description
    // - a list of specification files
        
    // From the organization page you can
    // - see the list of specifications 
    // -- and select amongst the list
    
    .controller('SpecificationListController', ['$scope', function($scope) {
        $scope.specifications = [];
        $scope.orderProp = 'description';
      }])

    .controller('SpecificationDetailsController', ['$scope', '$stateParams', 'SpecificationService',
      function($scope, $stateParams, SpecificationService) {
        $scope.specification = SpecificationService.get({specificationname: $stateParams.specificationname}, function(specification) {
        });
      }])

    .controller('SpecificationCreateController', ['$scope', '$location', 'SpecificationService',
      function($scope, $location, SpecificationService) {
        $scope.specification = {name: null, title: null};
        $scope.message = null;
        $scope.alertMessages = null;
        //$("#rPreferredLogin").focus();
        $scope.isTrue = true;

        /**
         * @returns true when there is enough data in the form 
         * to try to submit it. This is not to say that data is
         * valid. pressing submit will cause a series of 
         * validator to be evaluated
         */
        $scope.formIsSubmitable = function() {
          return ($scope.specification.name && $scope.specification.title);
        };

        $scope.submit = function() {
          $scope.message = null;
          $scope.alertMessages = null;

          // validate input values
          if ($scope.specification.name) {
            if ($scope.specification.name === "test") {
              $scope.alertMessages = ["Test? You have to be more visionary than that!"];
              return;
            }
          }

          // Send request
          $scope.message = "Sending request to create specification...";

          SpecificationService.create($scope.specification, function(data) {
            $location.path('/orgs/' + data.name).replace();
            $scope.message = ["Specification created: " + data];

          }, function(error) {
            // Error handler code
            $scope.message = null;
            $scope.alertMessages = ["Error on the serverside :( ", error];
          });
        };
      }])
    ;

