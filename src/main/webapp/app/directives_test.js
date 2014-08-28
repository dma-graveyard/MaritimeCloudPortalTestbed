// Helpers (TODO: should idially be moved into own module)
expectToHaveClass = function(element, cls) {
  expect(element.hasClass(cls), "Element should have class '" + cls + "' but only had '" + element.attr("class") + "'").to.be.true;
};

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

  it('should always have the btn and pull-right class', function() {
    
    expectToHaveClass(element, 'btn');
    expectToHaveClass(element, 'pull-right');
    
  });
  it('should default with the btn-info', function() {
    
    expectToHaveClass(element, 'btn-info');
    
  });
  it('should override the button type', function() {
    
    element = $compile('<panel-button btn-type="success"></panel-button>')(scope);
    scope.$digest();
    expectToHaveClass(element, 'btn-success');
    expectNotToHaveClass(element, 'btn-info');
    expectToHaveClass(element, 'pull-right');
    
  });
  it('should transclude the text to a span element', function() {
    
    element = $compile('<panel-button>TRANSCLUDED TEXT</panel-button>')(scope);
    scope.$digest();
    expect(element.html()).to.include('TRANSCLUDED TEXT');
    
  });
});
describe('panel directive', function() {

  // Arrange
  var element, scope, $compile, $rootScope;

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
    //console.log('DEBUG element ' + element + '\n', element);
  }));

  it('should use column size 6 as default', function() {
    
    expectToHaveClass(element, 'col-sm-6');
    
  });  
  it('should use the title attribute as a heading text', function() {
    
    var title = element.find('h4');
    expect(title.html()).to.include('A TITLE');
    expect(title.html()).to.not.include('BODY TEXT');
    
  });  
  it('should transclude all but the panel-button-elements to the body section', function() {
    
    var body = element.children().find('div');
    expectToHaveClass(body, 'mcp-panel-body');
    expect(body.html()).to.include('TRANSCLUDED BODY TEXT');
    expect(body.html()).to.include('MORE TRANSCLUDED BODY TEXT');
    expect(body.html()).to.not.include('TRANSCLUDED BUTTON');
    
  });  
  it('should interpolate transcluded text', function() {
    
    // Given the body node
    var body = element.children().find('div');
    expect(body.html()).not.to.include('BODY TEXT is interpolated with scope values');
    // When changing the scope 
    scope.interpolated = "is interpolated with scope values";
    scope.$digest();
    // Then the interpolated value should have changed
    expect(body.html()).to.include('BODY TEXT is interpolated with scope values');
    
  });  
  it('should transclude the panel-button-elements to the buttons section', function() {
    
    // should be the second div-child
    var buttons = element.find('div').next();
    expect(buttons.html()).to.include('TRANSCLUDED BUTTON');
    expect(buttons.html()).to.not.include('TRANSCLUDED BODY TEXT');
    expect(buttons.html()).to.not.include('MORE TRANSCLUDED BODY TEXT');
    
  });  
  it('should change title color', function() {
    
    element = $compile('<panel title-color="red"></panel>')(scope);
    scope.$digest();
    
    // should be the second div-child
    var title = element.find('h4');
    expectToHaveClass(title, 'red');
    
  });  
  it('should change column size', function() {
    
    var panel = $compile('<panel col="3"></panel>')(scope);
    scope.$digest();
    expectToHaveClass(panel, 'col-sm-3');
    
  });  
  it('should use title-icon info and blue color as default', function() {
    
    var titleIcon = element.find('h4').find('i');
    expectToHaveClass(titleIcon, 'fa-info');
    expectToHaveClass(titleIcon, 'blue');
    
  });
  it('should be able to change title-icon', function() {
    
    element = $compile('<panel icon="user"></panel>')(scope);
    scope.$digest();
    
    var titleIcon = element.find('h4').find('i');
    expectToHaveClass(titleIcon, 'fa-user');
    expectToHaveClass(titleIcon, 'blue');
    
  });
  it('should be able to change title-class', function() {
    
    element = $compile('<panel icon-class="fa-user red"></panel>')(scope);
    scope.$digest();
    
    var titleIcon = element.find('h4').find('i');
    expectToHaveClass(titleIcon, 'fa-user');
    expectToHaveClass(titleIcon, 'red');
    
  });

});
