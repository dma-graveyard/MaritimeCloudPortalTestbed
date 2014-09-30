// Helpers (TODO: should idially be moved into own module)
expectToHaveClass = function(element, cls) {
  expect(element.hasClass(cls), "Element should have class '" + cls + "' but only had '" + element.attr("class") + "'").to.be.true;
};

expectAttributeToContain = function(element, attribute, value) {
  expect(element.attr(attribute), "Element should have attribute '" + attribute + "' but did not'").to.contain(value);
};

function findElementWithClass(element, targetClass) {
  return angular.element(element[0].querySelectorAll('.'+targetClass));
}

expectNotToHaveClass = function(element, cls) {
  expect(element.hasClass(cls), "Element should NOT have class '" + cls + "'").to.be.false;
};

describe('panelButton directive', function() {

  // Arrange
  var element, scope, $compile, $rootScope;

  beforeEach(module('mcp.directives'));

  // Load the directive template 
  // (as pre-compiled by karma html2js)
  beforeEach(module('layout/panel-button.html'));

  beforeEach(inject(function(_$compile_, _$rootScope_) {
    $compile = _$compile_;
    $rootScope = _$rootScope_;
    scope = $rootScope;
    element = $compile('<panel-button>trancluded text</panel-button>')(scope);
    scope.$digest();
  }));

  it('should always have the btn class', function() {
    
    expectToHaveClass(element, 'btn');
    
  });
  it('should default with the btn-info', function() {
    
    expectToHaveClass(element, 'btn-info');
    
  });
  it('should override the button type', function() {
    
    element = $compile('<panel-button btn-type="success"></panel-button>')(scope);
    scope.$digest();
    expectToHaveClass(element, 'btn-success');
    expectNotToHaveClass(element, 'btn-info');
    
  });
  it('should transclude the text to a span element', function() {
    
    element = $compile('<panel-button>TRANSCLUDED TEXT</panel-button>')(scope);
    scope.$digest();
    expect(element.html()).to.include('TRANSCLUDED TEXT');
    
  });
});
describe('panel directive', function() {

  // Arrange
  var element, scope, $compile, $rootScope, title, body;

  beforeEach(module('mcp.directives'));

  // Load the directive template 
  // (as pre-compiled by karma html2js)
  beforeEach(module('layout/panel.html', 'layout/panel-button.html'));

  beforeEach(inject(function(_$compile_, _$rootScope_) {
    $compile = _$compile_;
    $rootScope = _$rootScope_;
    scope = $rootScope;
    element = $compile('<panel title="A TITLE">TRANSCLUDED BODY TEXT {{interpolated}}<panel-button>TRANSCLUDED BUTTON</panel-button>MORE TRANSCLUDED BODY TEXT</panel>')(scope);
    scope.$digest();
    // find element with class 'panel-title'
    title = findElementWithClass(element,'panel-title');
    body = findElementWithClass(element,'panel-body');
}));

  it('should use column size 6 as default', function() {
    
    expectToHaveClass(element, 'col-sm-6');
    
  });  
  it('should use the title attribute as a heading text', function() {
    
    expect(title.html()).to.include('A TITLE');
    expect(title.html()).to.not.include('BODY TEXT');
    
  });  
  it('should transclude all but the panel-button-elements to the body section', function() {
    
    expectToHaveClass(body, 'panel-body');
    expect(body.html()).to.include('TRANSCLUDED BODY TEXT');
    expect(body.html()).to.include('MORE TRANSCLUDED BODY TEXT');
    expect(body.html()).to.not.include('TRANSCLUDED BUTTON');
    
  });  
  it('should interpolate transcluded text', function() {
    
    // Given the body node
    
    expect(body.html()).not.to.include('BODY TEXT is interpolated with scope values');
    // When changing the scope 
    scope.interpolated = "is interpolated with scope values";
    scope.$digest();
    // Then the interpolated value should have changed
    expect(body.html()).to.include('BODY TEXT is interpolated with scope values');
    
  });  
  it('should transclude the panel-button-elements to the buttons section', function() {
    
    // should be the second div-child
    var buttons = findElementWithClass(element,'panel-footer');
    expect(buttons.html()).to.include('TRANSCLUDED BUTTON');
    expect(buttons.html()).to.not.include('TRANSCLUDED BODY TEXT');
    expect(buttons.html()).to.not.include('MORE TRANSCLUDED BODY TEXT');
    
  });  
  it('should change title color', function() {
    
    element = $compile('<panel title-color="red"></panel>')(scope);
    scope.$digest();
    
    // should be the second div-child
    title = findElementWithClass(element,'panel-title');
    expectAttributeToContain(title, 'style', 'color: red');
    
  });  
  it('should change column size', function() {
    
    var panel = $compile('<panel col="3"></panel>')(scope);
    scope.$digest();
    expectToHaveClass(panel, 'col-sm-3');
    
  });  
  it('should use title-icon info and blue color as default', function() {
    
    var titleIcon = title.find('i');
    expectToHaveClass(titleIcon, 'fa-info-circle');
    
  });
  it('should be able to change title-icon', function() {
    
    element = $compile('<panel icon="user"></panel>')(scope);
    scope.$digest();
    
    var titleIcon = findElementWithClass(element,'panel-title').find('i');
    expectToHaveClass(titleIcon, 'fa-user');
    
  });
  it('should be able to change title-class', function() {
    
    element = $compile('<panel icon-class="glyphicon glyphicon-user"></panel>')(scope);
    scope.$digest();
    
    var titleIcon = findElementWithClass(element,'panel-title').find('i');
    expectToHaveClass(titleIcon, 'glyphicon');
    expectToHaveClass(titleIcon, 'glyphicon-user');
    
  });

});
