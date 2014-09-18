'use strict';

// Inspired by:
//   https://github.com/angular/protractor/issues/156
//   http://www.thoughtworks.com/insights/blog/using-page-objects-overcome-protractors-shortcomings

// ----------------------------------------------------------------------------
// HELPERS 
// ----------------------------------------------------------------------------

browser.addMockModule('mcp.e2e.protractor', function() {
  angular.module('mcp.e2e.protractor', [])
      .run(function() {
        // Raise a e2e test flag to allow for protractor unfreindly elements like carrousel to be removed during testing 
        window.protractorE2EtestIsRunning = true;
      });
});

var expectIsEnabled = function(element) {
  return expect(element.isEnabled());
};

var isDisabled = function(element) {
  // HACK: apparently protractor (or selenium?) cannot read the correct 
  // isEnabled-state of some elements, instead it always resolves to true?!!  
  // (It might be due to the transcluded nature of these elements, like buttons 
  // in panels-directive!?!)
  // Explicitly look for the "disabled"-attribute:
  return element.getAttribute('disabled').then(function(result) {
    return result === 'true';
  });
};

var echo = function(element) {
  element.getOuterHtml().then(function(data) {
    console.log(data);
    console.log("Data: " + data);
  });
  element.isEnabled().then(function(data) {
    console.log(data);
    console.log("Data: " + data);
  });
  element.getAttribute('disabled').then(function(data) {
    console.log(data);
    console.log("Data: " + data);
  });
};

// ----------------------------------------------------------------------------
// PAGE OBJECTS
// ----------------------------------------------------------------------------

var Menu = function() {
  browser.get('app/index.html#/');
  this.brand = element(by.id('brand'));
  this.joinItem = element(by.id('menuLogin'));
  this.loginItem = element(by.id('menuLogin'));
  this.logoutItem = element(by.id('menuLogout'));
};

var LandingPage = function() {
  browser.get('app/index.html#/');
  this.loginButton = element(by.id('login'));
  this.joinButton = element(by.id('join'));
};

var LoginDialogPage = function() {
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
  this.typeEscape = function() {
    var e = this.close;
    e.sendKeys(protractor.Key.ESCAPE);
    // broken in safari (escape does not work!?) ...use close button instead 
    browser.getCapabilities().then(function(data) {
      if (data.caps_.browserName === 'safari') {
        e.click();
      }
    });
  };
};

var LoginDialogForgotPasswordPage = function() {
  // Init page
  var loginDialogPage = new LoginDialogPage();
  loginDialogPage.forgotPasswordLink.click();
  browser.waitForAngular();

  this.dialog = loginDialogPage.loginDialog;
  this.forgotPasswordLink = element(by.id('forgotPasswordLink'));
  this.email = element(by.id('email'));
  this.typeEmail = function(keys) {
    return this.email.sendKeys(keys);
  };
  this.backToLoginLink = element(by.id('backToLoginLink'));
  this.send = element(by.buttonText('Send'));
  this.cancel = element(by.buttonText('Cancel'));
};

var SupplyPasswordSnippet = function() {
  this.newPassword = element(by.id('newPassword'));
  this.repeatedPassword = element(by.id('repeatedPassword'));
  this.typePassword = function(keys) {
    return this.newPassword.sendKeys(keys);
  };
  this.retypePassword = function(keys) {
    return this.repeatedPassword.sendKeys(keys);
  };
};

var JoinFormPage = function() {
  browser.get('app/index.html#/join');
  this.preferredLogin = element(by.id('preferredLogin'));
  this.isValidLogin = function(keys) {
    return this.preferredLogin.getAttribute('mcp-invalid-input').then(function(result) {
      return result === 'true';
    });
  };
  this.typePreferredLogin = function(keys) {
    return this.preferredLogin.sendKeys(keys);
  };
  this.email = element(by.id('email'));
  this.typeEmail = function(keys) {
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

var JoinConfirmPage = function() {
  browser.get('app/index.html#/join-confirm');
};

var ResetPasswordPage = function() {
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

// ----------------------------------------------------------------------------
// Specs
// ----------------------------------------------------------------------------

describe('menu bar', function() {

  var menu;

  beforeEach(function() {
    browser.get('app/index.html#/');
    menu = new Menu();
  });

  it('should logout before testing', function() {
    menu.logoutItem.isPresent().then(function(present) {
      if (present) {
        console.log("Logging out to be in a clean state");
        menu.logoutItem.click();
      }
    });
  });

  it('should have a brand, a join and a login', function() {
    expect(menu.brand.isPresent()).toBe(true);
    expect(menu.joinItem.isPresent()).toBe(true);
    expect(menu.loginItem.isPresent()).toBe(true);
    expect(menu.logoutItem.isPresent()).toBe(false);
  });

});

describe('maritime cloud portal landingpage', function() {

  var page;

  beforeEach(function() {
    page = new LandingPage();
  });

  it('should have a title, a brand, a join and a login', function() {
    expect(browser.getTitle()).toEqual('Maritime Cloud Portal');
    expect(page.joinButton.isPresent()).toBe(true);
    expect(page.loginButton.isPresent()).toBe(true);
  });

});

describe('login dialog', function() {

  var page;

  beforeEach(function() {
    page = new LoginDialogPage();
  });

  it('should require both username and minimum 4 characters password', function() {
    expectIsEnabled(page.login).toBe(false);
    page.usernamefield.sendKeys('admin');
    expectIsEnabled(page.login).toBe(false);
    page.passwordfield.sendKeys('tes');
    expectIsEnabled(page.login).toBe(false);
    page.passwordfield.sendKeys('t');
    expectIsEnabled(page.login).toBe(true);
  });

  it('should have a link "forgot password"', function() {
    expect(page.forgotPasswordLink.isPresent()).toBe(true);
    page.forgotPasswordLink.click();
    expect(page.usernamefield.isPresent()).toBe(false);
  });

  it('should close dialog on cancel', function() {
    expect(page.loginDialog.isPresent()).toBe(true);
    // should have enabled cancel button
    expect(page.cancel.isPresent()).toBe(true);
    expectIsEnabled(page.cancel).toBe(true);
    page.cancel.click();
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should close dialog on close-x-button', function() {
    expect(page.loginDialog.isPresent()).toBe(true);
    // should have enabled cancel button
    expectIsEnabled(page.close).toBe(true);
    page.close.click();
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should close dialog on escape', function() {
    expect(page.loginDialog.isPresent()).toBe(true);
    page.typeEscape();
    //page.loginDialog.sendKeys(protractor.Key.ESCAPE);
    expect(page.loginDialog.isPresent()).toBe(false);
  });

  it('should navigate to the sign up page', function() {
    expect(page.signUpLink.isPresent()).toBe(true);
    page.signUpLink.click();
    expect(browser.getLocationAbsUrl()).toMatch("/join");
  });

});

describe('login dialog forgot password', function() {

  var page;

  beforeEach(function() {
    page = new LoginDialogForgotPasswordPage();
  });

  it('should require email', function() {
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

describe('reset password page', function() {

  var page;

  beforeEach(function() {
    page = new ResetPasswordPage();
  });

  it('should require at least 4 character password', function() {
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
    page.typePassword('abc');
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
  });

  it('should require password different from username', function() {
    // HACK: apparently protractor (or selenium?) cannot read the correct 
    // isEnabled-state of this element, instead it always resolves to true?!!  
    // (It might be due to the transcluded nature of the button!?!)
    // Explicitly look for the "disabled"-attribute:
    expect(isDisabled(page.changePasswordButton)).toBe(true);
    page.typePassword('admin');
    page.retypePassword('admin');
    expect(isDisabled(page.changePasswordButton)).toBe(true);
  });

  it('should verify password', function() {
    expect(page.newPassword.isDisplayed()).toBe(true);
    expect(page.repeatedPassword.isDisplayed()).toBe(false);
    expect(isDisabled(page.changePasswordButton)).toBe(true);
    page.typePassword('secret');
    expect(page.repeatedPassword.isDisplayed()).toBe(true);
    page.retypePassword('secret');
    expect(isDisabled(page.changePasswordButton)).toBe(false);
  });

  it('should deny change of password with expired reset-id', function() {
    expect(page.closeButton.isDisplayed()).toBe(false);
    page.typePassword('secret');
    page.retypePassword('secret');
    page.changePasswordButton.click();
    expect(page.closeButton.isDisplayed()).toBe(true);
  });

});

describe('join form', function() {

  var page;

  beforeEach(function() {
    page = new JoinFormPage();
  });

  it('should sign up', function() {
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

  it('should require unique username', function() {
    page.typePreferredLogin('admin');
    expect(page.isValidLogin()).toBe(false);
    //    page.typePreferredLogin('somethingElse');
    //    expect(page.isValidLogin()).toBe(true);
  });

});

/*
 describe('activate account', function() {
 
 var page;
 
 beforeEach(function() {
 page = new JoinConfirmPage();
 });
 
 it('should show account activated', function() {
 // not sure we can do this at the moment
 });
 
 });
 */
