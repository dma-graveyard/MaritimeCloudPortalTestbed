'use strict';

/* Directives */

angular.module('mcp.directives', [])
    /**
     * mcp-focus-me: Simpleminded directive that transfer focus to the selected element after 100 ms.
     */
    .directive('mcpFocusMe', function($timeout) {
      // see http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
      return {
        link: function(scope, element) {
          $timeout(function() {
            element[0].focus();
          }, 100);
        }
      };
    })

    .directive("panel", function() {
      return {
        link: function(scope, element, attrs) {
          scope.panelSubject = attrs["title"];
          scope.panelClass = attrs["panelClass"] ? attrs["panelClass"] : "col-sm-6";
          scope.panelIcon = attrs["panelIcon"] ? attrs["panelIcon"] : "fa-info blue";
        },
        restrict: "E",
        scope: true,
        templateUrl: "layout/panel.html",
        transclude: true
      };
    })
    .directive("buttons", function() {
      return {
        link: function(scope, element, attrs) {
          scope.panelSubject = attrs["title"];
        },
        restrict: "E",
        scope: true,
        templateUrl: "layout/panel-buttons.html",
        transclude: true
      };
    })
    .directive("btn", function() {
      return {
        link: function(scope, element, attrs) {
          scope.panelButtonSrf = attrs["mcp"];
          console.log(scope.panelButtonSrf);
        },
        restrict: "E",
        replace: true,
        scope: true,
        templateUrl: "layout/panel-button.html",
        transclude: true
      };
    })
    ;


//angular.module('mcpDirectives.ui.bootstrap', ['ui.bootstrap.transition'])
//
//    .directive('mcpMinify', ['$transition', function($transition) {
//
//        return {
//          link: function(scope, element, attrs) {
//
//            var initialAnimSkip = true;
//            var currentTransition;
//
//            function doTransition(change) {
//              var newTransition = $transition(element, change);
//              if (currentTransition) {
//                currentTransition.cancel();
//              }
//              currentTransition = newTransition;
//              newTransition.then(newTransitionDone, newTransitionDone);
//              return newTransition;
//
//              function newTransitionDone() {
//                // Make sure it's this transition, otherwise, leave it alone.
//                if (currentTransition === newTransition) {
//                  currentTransition = undefined;
//                }
//              }
//            }
//
//            function expand() {
//              if (initialAnimSkip) {
//                initialAnimSkip = false;
//                expandDone();
//              } else {
//                element.removeClass('menu-min').addClass('collapsing');
//                doTransition({height: element[0].scrollHeight + 'px'}).then(expandDone);
//              }
//            }
//
//            function expandDone() {
//              element.removeClass('collapsing');
//              //element.addClass('collapse in');
//              element.css({height: 'auto'});
//            }
//
//            function collapse() {
//              if (initialAnimSkip) {
//                initialAnimSkip = false;
//                collapseDone();
//                element.css({height: 0});
//              } else {
//                // CSS transitions don't work with height: auto, so we have to manually change the height to a specific value
//                element.css({height: element[0].scrollHeight + 'px'});
//                //trigger reflow so a browser realizes that height was updated from auto to a specific value
//                var x = element[0].offsetWidth;
//                element.removeClass('collapse in').addClass('collapsing');
//                doTransition({height: 0}).then(collapseDone);
//              }
//            }
//
//            function collapseDone() {
//              element.removeClass('collapsing');
//              element.addClass('menu-min');
//            }
//
//            scope.$watch(attrs.mcpMinify, function(shouldCollapse) {
//              if (shouldCollapse) {
//                collapse();
//              } else {
//                expand();
//              }
//            });
//
//          }
//        };
//      }]);

