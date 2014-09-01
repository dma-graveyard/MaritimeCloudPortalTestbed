'use strict';

// ----------------------------------------------------------------------------
// HELPERS 
// ----------------------------------------------------------------------------

var expectIsEnabled = function(element){
  return expect(element.getWebElement().isEnabled());
};

// ----------------------------------------------------------------------------
// PAGE OBJECTS
// ----------------------------------------------------------------------------

var Menu = function() {
  this.brand = element(by.id('brand'));
  this.joinItem = element(by.id('menuLogin'));
  this.loginItem = element(by.id('menuLogin'));
  this.logoutItem = element(by.id('menuLogout'));
//  this.login = element(by.id('navbarLogin'));
  // Init page
  browser.get('app/index.html#/');
};

var LandingPage = function() {
  this.loginButton = element(by.id('login'));
  this.joinButton = element(by.id('join'));
  // Init page
  browser.get('app/index.html#/');
};

var JoinFormPage = function() {
  this.preferredLogin = element(by.model('user.username'));
  // Init page
  browser.get('app/index.html#/join');
};

var LoginDialogPage = function() {

  this.loginDialog = element(by.id('loginDialog'));
  this.usernamefield = element(by.id('usernamefield'));
  this.passwordfield = element(by.id('passwordfield'));
  this.forgotPasswordLink = element(by.id('forgotPasswordLink'));
  this.login = element(by.buttonText('Log In'));
  this.close = element(by.buttonText('x'));
  this.cancel = element(by.buttonText('Cancel'));
  this.signUpLink = element(by.id('signUpLink'));
  
  // Init page
  var navbar = new Menu();
  navbar.loginItem.click();
};

var LoginDialogForgotPasswordPage = function() {

  this.forgotPasswordLink = element(by.id('forgotPasswordLink'));

  this.email = element(by.id('email'));
  this.typeEmail = function(keys) {return this.email.sendKeys(keys);};
  this.backToLoginLink = element(by.id('backToLoginLink'));
  this.send = element(by.buttonText('Send'));
  this.cancel = element(by.buttonText('Cancel'));
  
  // Init page
  var loginDialogPage = new LoginDialogPage();
  loginDialogPage.forgotPasswordLink.click();
  
  this.dialog = loginDialogPage.loginDialog;
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
    page.loginDialog.sendKeys(protractor.Key.ESCAPE);
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

