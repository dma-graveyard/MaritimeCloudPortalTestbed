'use strict';

// Inspired by:
//   https://github.com/angular/protractor/issues/156
//   http://www.thoughtworks.com/insights/blog/using-page-objects-overcome-protractors-shortcomings

// TIP:
//   Add an extra 'd' to 'descripe(...)' to test only those senarios!
//   Also, be aware that many scenarios need to the log-in scenario to have run 
//   in advance, so remember to also enable that.  

// ----------------------------------------------------------------------------
// HELPERS 
// ----------------------------------------------------------------------------

browser.addMockModule('mcp.e2e.protractor', function () {
  angular.module('mcp.e2e.protractor', [])
      .run(function () {
        // Raise a e2e test flag to allow for protractor unfreindly elements like carrousel to be removed during testing 
        window.protractorE2EtestIsRunning = true;
      });
});

var expectIsEnabled = function (element) {
  return expect(element.isEnabled());
};

var isDisabled = function (element) {
  // HACK: apparently protractor (or selenium?) cannot read the correct 
  // isEnabled-state of some elements, instead it always resolves to true?!!  
  // (It might be due to the transcluded nature of these elements, like buttons 
  // in panels-directive!?!)
  // Explicitly look for the "disabled"-attribute:
  return element.getAttribute('disabled').then(function (result) {
    return result === 'true';
  });
};

var echo = function (element) {
  element.getOuterHtml().then(function (data) {
    console.log(data);
    console.log("Data: " + data);
  });
  element.isEnabled().then(function (data) {
    console.log(data);
    console.log("Data: " + data);
  });
  element.getAttribute('disabled').then(function (data) {
    console.log(data);
    console.log("Data: " + data);
  });
};

// ----------------------------------------------------------------------------
// PAGE OBJECTS
// ----------------------------------------------------------------------------

var Menu = function () {
  browser.get('app/index.html#/');
  this.brand = element(by.id('brand'));
  this.joinItem = element(by.id('menuLogin'));
  this.loginItem = element(by.id('menuLogin'));
  this.logoutItem = element(by.id('menuLogout'));
};

var LandingPage = function () {
  browser.get('app/index.html#/');
  this.loginButton = element(by.id('login'));
  this.joinButton = element(by.id('join'));
};

var LoginDialogPage = function () {
  // Init page
  var navbar = new Menu();
  navbar.loginItem.click();

  this.loginDialog = element(by.id('loginDialog'));
  this.usernamefield = element(by.id('usernamefield'));
  this.passwordfield = element(by.id('passwordfield'));
  this.forgotPasswordLink = element(by.id('forgotPasswordLink'));
  this.login = element(by.buttonText('Log In'));
  this.close = element(by.buttonText('x'));
  this.cancel = element(by.buttonText('Cancel'));
  this.signUpLink = element(by.id('signUpLink'));
  this.typeEscape = function () {
    var e = this.close;
    e.sendKeys(protractor.Key.ESCAPE);
    // broken in safari (escape does not work!?) ...use close button instead 
    browser.getCapabilities().then(function (data) {
      if (data.caps_.browserName === 'safari') {
        e.click();
      }
    });
  };
};

var LoginHelper = function () {
  var logoutItem = element(by.id('menuLogout'));
//  if(logoutItem.isDisplayed()){
//    logoutItem.click();
//  }
  // Init page
  var loginPage = new LoginDialogPage();
  function typeUsername(keys) {
    return loginPage.usernamefield.sendKeys(keys);
  }
  ;
  function typePassword(keys) {
    return loginPage.passwordfield.sendKeys(keys);
  }
  ;
  this.login = function (username, password) {
    typeUsername(username);
    typePassword(password);
    loginPage.login.click();
  };
  this.isLoggedIn = function () {
    return logoutItem.isDisplayed();
  };
};

var LoginDialogForgotPasswordPage = function () {
  // Init page
  var loginDialogPage = new LoginDialogPage();
  loginDialogPage.forgotPasswordLink.click();
  browser.waitForAngular();

  this.dialog = loginDialogPage.loginDialog;
  this.forgotPasswordLink = element(by.id('forgotPasswordLink'));
  this.email = element(by.id('email'));
  this.typeEmail = function (keys) {
    return this.email.sendKeys(keys);
  };
  this.backToLoginLink = element(by.id('backToLoginLink'));
  this.send = element(by.buttonText('Send'));
  this.cancel = element(by.buttonText('Cancel'));
};

var SupplyPasswordSnippet = function () {
  this.newPassword = element(by.id('newPassword'));
  this.repeatedPassword = element(by.id('repeatedPassword'));
  this.typePassword = function (keys) {
    return this.newPassword.sendKeys(keys);
  };
  this.retypePassword = function (keys) {
    return this.repeatedPassword.sendKeys(keys);
  };
};

var JoinFormPage = function () {
  browser.get('app/index.html#/join');
  this.preferredLogin = element(by.id('preferredLogin'));
  this.isValidLogin = function (keys) {
    return this.preferredLogin.getAttribute('mcp-invalid-input').then(function (result) {
      return result === 'true';
    });
  };
  this.typePreferredLogin = function (keys) {
    return this.preferredLogin.sendKeys(keys);
  };
  this.email = element(by.id('email'));
  this.typeEmail = function (keys) {
    return this.email.sendKeys(keys);
  };
  // import password snippet
  var supplyPasswordSnippet = new SupplyPasswordSnippet();
  this.newPassword = supplyPasswordSnippet.newPassword;
  this.repeatedPassword = supplyPasswordSnippet.repeatedPassword;
  this.typePassword = supplyPasswordSnippet.typePassword;
  this.retypePassword = supplyPasswordSnippet.retypePassword;
  this.submitButton = element(by.id('submitButton'));
};

var JoinConfirmPage = function () {
  browser.get('app/index.html#/join-confirm');

  this.panelBody = element(by.css('.panel-body'));
};

var ResetPasswordPage = function () {
  browser.get('app/index.html#/users/admin/reset/some-unique-verification-code-XYZ-123');
  // import password snippet
  var supplyPasswordSnippet = new SupplyPasswordSnippet();
  this.newPassword = supplyPasswordSnippet.newPassword;
  this.repeatedPassword = supplyPasswordSnippet.repeatedPassword;
  this.typePassword = supplyPasswordSnippet.typePassword;
  this.retypePassword = supplyPasswordSnippet.retypePassword;
  this.changePasswordButton = element(by.id('changePasswordButton'));
  this.loginButton = element(by.id('loginButton'));
  this.closeButton = element(by.id('closeButton'));
};

var CreateServiceInstancePage = function () {
  browser.get('app/index.html#/orgs/dmi/createServiceInstance');

  this.operationalService = element(by.model('selectedOperationalService'));
  this.specification = element(by.model('selectedSpecification'));
  this.id = element(by.model('service.id'));
  this.name = element(by.model('service.name'));
  this.description = element(by.model('service.description'));
  this.submitButton = element(by.css('.btn-success'));
  this.map = element(by.css('.angular-leaflet-map'));
  
  // (on services page) 
  this.serviceInstanceList = element.all(by.repeater('service in serviceInstances'));
};

var SearchServiceMapPage = function () {
  browser.get('http://localhost:8080/app/index.html#/search/service/map');

  var svgElement = '//*[local-name()="svg" and namespace-uri()="http://www.w3.org/2000/svg"]',
      svgPathElementXpath = svgElement + '//*[local-name()="path"]';

  this.searchmap = element(by.id('searchmap'));
  this.svgPathElementXpath = svgPathElementXpath;
  this.svgPathElements = element.all(by.xpath(svgPathElementXpath));
  this.anyText = element(by.id('anyText'));
  this.providerOpen = element(by.buttonText('provider'));
  this.provider = element(by.model('$select.search'));
  this.providerList = element.all(by.repeater('provider in $select.items'));
  this.reset = element(by.id('mapFilterResetAll'));
};

// ----------------------------------------------------------------------------
// Specs
// ----------------------------------------------------------------------------

describe('menu bar', function () {

  var menu;

  beforeEach(function () {
    browser.get('app/index.html#/');
    menu = new Menu();
  });

  it('should logout before testing', function () {
    menu.logoutItem.isPresent().then(function (present) {
      if (present) {
        console.log("Logging out to be in a clean state");
        menu.logoutItem.click();
      }
    });
  });

  it('should have a brand, a join and a login', function () {
    expect(menu.brand.isPresent()).toBe(true);
    expect(menu.joinItem.isPresent()).toBe(true);
    expect(menu.loginItem.isPresent()).toBe(true);
    expect(menu.logoutItem.isPresent()).toBe(false);
  });

});

describe('maritime cloud portal landingpage', function () {

  var page;

  beforeEach(function () {
    page = new LandingPage();
  });

  it('should have a title, a brand, a join and a login', function () {
    expect(browser.getTitle()).toEqual('Maritime Cloud Portal');
    expect(page.joinButton.isPresent()).toBe(true);
    expect(page.loginButton.isPresent()).toBe(true);
  });

});

describe('login dialog', function () {

  var page;

  beforeEach(function () {
    page = new LoginDialogPage();
  });

  it('should require both username and minimum 4 characters password', function () {
    expectIsEnabled(page.login).toBe(false);
    page.usernamefield.sendKeys('admin');
    expectIsEnabled(page.login).toBe(false);
    page.passwordfield.sendKeys('tes');
    expectIsEnabled(page.login).toBe(false);
    page.passwordfield.sendKeys('t');
    expectIsEnabled(page.login).toBe(true);
  });

  it('should have a link "forgot password"', function () {
    expect(page.forgotPasswordLink.isPresent()).toBe(true);
    page.forgotPasswordLink.click();
    expect(page.usernamefield.isPresent()).toBe(false);
  });

  it('should close dialog on cancel', function () {
    expect(page.loginDialog.isPresent()).toBe(true);
    // should have enabled cancel button
    expect(page.cancel.isPresent()).toBe(true);
    expectIsEnabled(page.cancel).toBe(true);
    page.cancel.click();
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should close dialog on close-x-button', function () {
    expect(page.loginDialog.isPresent()).toBe(true);
    // should have enabled cancel button
    expectIsEnabled(page.close).toBe(true);
    page.close.click();
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should close dialog on escape', function () {
    expect(page.loginDialog.isPresent()).toBe(true);
    page.typeEscape();
    //page.loginDialog.sendKeys(protractor.Key.ESCAPE);
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should navigate to the sign up page', function () {
    expect(page.signUpLink.isPresent()).toBe(true);
    page.signUpLink.click();
    expect(browser.getLocationAbsUrl()).toMatch("/join");
  });

});

describe('login dialog forgot password', function () {

  var page;

  beforeEach(function () {
    page = new LoginDialogForgotPasswordPage();
  });

  it('should require email', function () {
    // should have a link "back to login"
    expect(page.backToLoginLink.isPresent()).toBe(true);
    // should have enabled cancel button
    expect(page.cancel.isPresent()).toBe(true);
    expectIsEnabled(page.cancel).toBe(true);
    // should have visible but disabled send button
    expect(page.send.isPresent()).toBe(true);
    expectIsEnabled(page.send).toBe(false);
    // when providing invalid email 
    page.typeEmail('e@');
    // then send button is still disabled
    expectIsEnabled(page.send).toBe(false);
    // when completing to a valid email
    page.typeEmail('mail.com');
    // then send is enabled
    expectIsEnabled(page.send).toBe(true);
  });

});

describe('reset password page', function () {

  var page;

  beforeEach(function () {
    page = new ResetPasswordPage();
  });

  it('should require at least 4 character password', function () {
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
    page.typePassword('abc');
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
  });

  it('should require password different from username', function () {
    // HACK: apparently protractor (or selenium?) cannot read the correct 
    // isEnabled-state of this element, instead it always resolves to true?!!  
    // (It might be due to the transcluded nature of the button!?!)
    // Explicitly look for the "disabled"-attribute:
    expect(isDisabled(page.changePasswordButton)).toBe(true);
    page.typePassword('admin');
    page.retypePassword('admin');
    expect(isDisabled(page.changePasswordButton)).toBe(true);
  });

  it('should verify password', function () {
    expect(page.newPassword.isDisplayed()).toBe(true);
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
    expect(isDisabled(page.changePasswordButton)).toBe(true);
    page.typePassword('secret');
    expect(page.repeatedPassword.isDisplayed()).toBe(true);
    page.retypePassword('secret');
    expect(isDisabled(page.changePasswordButton)).toBe(false);
  });

  it('should deny change of password with expired reset-id', function () {
    expect(page.closeButton.isDisplayed()).toBe(false);
    page.typePassword('secret');
    page.retypePassword('secret');
    page.changePasswordButton.click();
    expect(page.closeButton.isDisplayed()).toBe(true);
  });

});

describe('join form', function () {

  var page;

  beforeEach(function () {
    page = new JoinFormPage();
  });

  it('should sign up', function () {
    page.typePreferredLogin('JohnDoe');
    page.typeEmail('john_doe@email.com');
    page.typePassword('secret');
    page.retypePassword('secret');
    // TODO: add httpCallback mock to intercept registration
    // in order to prevent duplicate username errors when testing 
    // with multiple browsers
    //page.submitButton.click();
    //expect(browser.getLocationAbsUrl()).toMatch("/join-confirm");
  });

  it('should require unique username', function () {
    page.typePreferredLogin('admin');
    expect(page.isValidLogin()).toBe(false);
    //    page.typePreferredLogin('somethingElse');
    //    expect(page.isValidLogin()).toBe(true);
  });

});

describe('Join confirmation', function () {

  var page;

  beforeEach(function () {
    page = new JoinConfirmPage();
  });

  it('should inform that an email has been sent', function () {
    expect(page.panelBody.getText()).toContain('email has been sent');
  });

});

// --------------------------------------------------------------------
// LOG IN as 'Tintin'
// ...for testing protected pages
// --------------------------------------------------------------------

describe('HELPER: Log in to test protected pages', function () {

  var page;

  it('should log out if already logged in', function () {
    var menu = new Menu();
    var logoutItem = menu.logoutItem;
    logoutItem.click().then(function () {
      //console.log('Logged out');
    }, function (err) {
      //console.log('Never Logged in');
    });
  });

  it('should log in', function () {
    page = new LoginHelper();
    page.login('Tintin', 'test');
  });

  it('should still be logged in', function () {
    expect(element(by.id('menuLogout')).isDisplayed()).toBe(true);
  });

});

// --------------------------------------------------------------------
// TEST PROTECTED PAGES
// --------------------------------------------------------------------

describe('create service instance', function () {
  var page;

  beforeEach(function () {
    page = new CreateServiceInstancePage();
  });

  it('should create service instance', function () {

    expect(page.submitButton.isEnabled()).toBe(false);
    
    page.operationalService.sendKeys('M');
    page.specification.sendKeys('M');
    page.id.sendKeys('id1');
    page.name.sendKeys('A Service Name');
    page.description.sendKeys('a very long description');
    
    // Draw circle on map
    element(by.css('.leaflet-draw-draw-circle')).click();
    browser.actions().mouseDown(page.map).mouseMove({x: 30, y:30}).mouseUp().perform();
    
    element(by.css('.leaflet-draw-draw-rectangle')).click();
    browser.actions().mouseDown(page.map).mouseMove({x: -30, y:-30}).mouseUp().perform();
    
    element(by.css('.leaflet-draw-draw-polygon')).click();
    browser.actions()
        .mouseDown(page.map).mouseUp()
        .mouseMove({x: 10, y:10}).mouseDown().mouseUp()
        .mouseMove({x: 10, y:-10}).mouseDown().mouseUp()
        .mouseDown(page.map).mouseUp()
        .perform();
    //browser.sleep(5000);
    
    expect(page.submitButton.isEnabled()).toBe(true);
    expect(browser.getLocationAbsUrl()).toMatch("orgs/dmi/createServiceInstance");
    page.submitButton.click();
    
    // should be on services list page
    expect(browser.getLocationAbsUrl()).not.toMatch("orgs/dmi/createServiceInstance");
    expect(browser.getLocationAbsUrl()).toMatch("orgs/dmi");
    
    // should see service on list
    expect(page.serviceInstanceList.count()).toBe(6);
    expect(page.serviceInstanceList.get(5).element(by.css('h4')).getText()).toEqual('A Service Name - REST');
    
    //browser.sleep(10000);
  });

});

describe('search on map', function () {

  var page;

  beforeEach(function () {
    page = new SearchServiceMapPage();
  });

  it('should load a marker when clicked on the map', function () {

    // wait until the first svgPath element is present
    browser.wait(function () {
      //return element(by.css('img.leaflet-tile-loaded')).isPresent();
      return page.svgPathElements.first().isPresent();
    });

    //    element(by.css('img.leaflet-tile-loaded')).click().then(function () {
    //      browser.sleep(100);
    //      expect(element(by.css('img.leaflet-marker-icon')).isPresent()).toBe(true);
    //    });

    //    element(by.css('img.leaflet-tile-loaded')).click().then(function () {
    //      browser.sleep(100);
    //      expect(element(by.css('img.leaflet-marker-icon')).isPresent()).toBe(true);
    //    });

    //expect(page.svgPathElements.count()).toBe(1);
    expect(page.svgPathElements.count()).toBeGreaterThan(5);

    //    page.svgPathElements.each(function (element) {
    //      // Will print First, Second, Third.
    //      console.log(element);
    //      element.getAttribute('d').then(function (data) {
    //        console.log(data);
    //      });
    //    });

    // find circle coverage of west Greenland
    //expect(element(by.xpath(page.svgElement + '//*[local-name()="path" and @d="M271 634L271 74L641 74L641 634z"]')).isPresent()).toBe(true);
  });

  it('should filter list by provider', function () {

    // hack: reset provider dropdown 
    page.providerOpen.click();
    page.anyText.click();
    expect(page.svgPathElements.count()).toBeGreaterThan(6);

    // when I focus the provider
    page.providerOpen.click();
    // I should see a dropdown list with more than 3 elements
    expect(page.providerList.count()).toBeGreaterThan(3);

    // when I start to enter something (as 'meteoroligical')
    page.provider.sendKeys('me');
    // then the list should be reduced
    expect(page.providerList.count()).toBe(3);
    page.provider.sendKeys('t');
    // ... reduced to 'Danish Meteoroligical Institute'
    expect(page.providerList.count()).toBe(1);

    // when I click the only item on the list
    page.providerList.first().click();
    // then the number of visible services on the map should be reduced to 6
    expect(page.svgPathElements.count()).toBe(6);

    // when I click the reset button
    page.reset.click();
    expect(page.svgPathElements.count()).toBeGreaterThan(6);
  });

  it('should filter list by any text', function () {

    // hack: reset provider dropdown 
    page.providerOpen.click();
    page.anyText.click();
    expect(page.svgPathElements.count()).toBeGreaterThan(1);

    page.anyText.sendKeys('-888');
    expect(page.svgPathElements.count()).toBe(1);

    page.reset.click();
    expect(page.svgPathElements.count()).toBeGreaterThan(1);
  });

});

