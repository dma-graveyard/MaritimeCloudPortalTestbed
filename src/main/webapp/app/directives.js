'use strict';

/* Directives */

angular.module('mcp.directives', [])
    /**
     * mcp-focus-me: Simpleminded directive that transfer focus to the selected element after 100 ms.
     */
    .directive('mcpFocusMe', function ($timeout) {
      // see http://stackoverflow.com/questions/14833326/how-to-set-focus-in-angularjs
      return {
        link: function (scope, element) {
          $timeout(function () {
            element[0].focus();
          }, 100);
        }
      };
    })

    .directive('panel', function () {
      // as inspired by http://stackoverflow.com/questions/22584357/angularjs-is-there-a-difference-between-transclude-local-in-directive-controll?rq=1
      return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope: true,
        templateUrl: "layout/panel.html",
        link: function (scope, element, attrs, controller, transclude) {
          scope.panelType = attrs["category"] || "default";
          scope.panelSubject = attrs["title"];
          scope.panelSubjectStyle = attrs["titleColor"] ? 'color: ' + attrs["titleColor"] + ';' : '';
          scope.panelSubjectStyle += attrs["titleSize"] ? 'font-size: ' + attrs["titleSize"] + 'px;' : '';
          scope.panelClass = attrs["col"] === "none" ? "" : "col-sm-" + (attrs["col"] || "6");
          scope.panelRowOffset = attrs["offset"] === "" ? "" : "col-sm-offset-" + attrs["offset"];
          scope.panelIcon = attrs["icon"] === "none" ? "" : "fa fa-" + (attrs["icon"] || "info-circle");
          scope.panelIconClass = attrs["iconClass"] || scope.panelIcon;

          transclude(scope, function (clone, scope) {

            // Find the transclude targets (body and buttons nodes) in the template
            var body = element[0].querySelector('.panel-body');
            var buttons = element[0].querySelector('.panel-footer');

            // Iterate the children of the source element
            Array.prototype.forEach.call(clone, function (node) {

              // If it is a A-element or PANEL-BUTTON element
              // (hint: 'panel-button'-elements may have been converted to 'A'-elements by its own directive in advance)
              if (node.tagName === 'A' || node.tagName === 'PANEL-BUTTON') {
                // then move it to the buttons section
                buttons.appendChild(node);
                return;
              }
              // the remainder goes into the body section
              body.appendChild(node);

            });
          });
        }
      };
    })

    .directive("buttons", function () {
      return {
        link: function (scope, element, attrs) {
          scope.panelSubject = attrs["title"];
        },
        restrict: "E",
        scope: true,
        templateUrl: "layout/panel-buttons.html",
        transclude: true
      };
    })
    .directive("panelButton", function () {
      return {
        link: function (scope, element, attrs) {
          scope.btnType = attrs["btnType"];
          scope.btnClass = attrs["btnClass"] ? attrs["btnClass"] : "btn-" + (scope.btnType ? scope.btnType : "info");
        },
        restrict: "E",
        replace: true,
        scope: true,
        templateUrl: "layout/panel-button.html",
        transclude: true
      };
    })

    /**
     * The resize directive binds the current height and width of its target element to
     * the scope properties windowWidth and windowHeight. The values are updated on 
     * window resize
     */
    .directive('resize', function ($window, $timeout) {
      return function (scope) {
        var w = angular.element($window, $timeout);
        scope.initializeDimensions = function () {
          scope.windowHeight = $window.innerHeight;
          scope.windowWidth = $window.innerWidth;
        };

        // fire once on startup
        scope.initializeDimensions();

        angular.element($window).bind('resize', function () {
          scope.initializeDimensions();
          scope.$apply();
        });
      };
    })

    /**
     * Directive that will add the value of the named property of the target 
     * element to the scope property 'element' if this exists in advance
     * 
     * Example: <div element-property="offsetWidth"> 
     *   
     * will the value of the element property 'offsetWidth' to $scope.element.offsetWidth.
     *  
     * @param {type} $timeout
     * @returns {directives_L114.directivesAnonym$5}
     */
    .directive('elementProperty', function ($timeout) {
      return {
        restrict: 'A',
        link: function (scope, element, attrs) {
          var property = attrs['elementProperty'];
          if (scope.element) {
            scope.element[property] = element.prop(property);
          }
        }
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

