
describe('maritime cloud portal landingpage', function() {
  var login = element(by.id('login'));
  var join = element(by.id('join'));
  var preferredLogin = element(by.model('user.username'));
  var navbarLogin = element(by.id('navbarLogin'));
  var logout = element(by.id('logout'));
  //var brand = element(by.css('a.navbar-brand'));
  var brand = element(by.id('brand'));
//  var goButton = element(by.id('gobutton'));
//  var latestResult = element(by.binding('latest'));
//  var history = element.all(by.repeater('result in memory'));

  beforeEach(function() {
    browser.get('app/index.html#/');
  });

  it('should have a title, a brand, a join and a login', function() {
    expect(browser.getTitle()).toEqual('Maritime Cloud Portal');
    expect(brand.isPresent()).toBe(true);
    expect(join.isPresent()).toBe(true);
    expect(login.isPresent()).toBe(true);
  });
});

describe('login dialog', function() {
  
  var navbarLogin = element(by.id('navbarLogin'));
  var usernamefield = element(by.id('usernamefield'));
  var passwordfield = element(by.id('passwordfield'));
  
  var login = element(by.buttonText('Log In'));
  var cancel = element(by.buttonText('Cancel'));

  beforeEach(function() {
    browser.get('app/index.html#/');
    navbarLogin.click();
  });

  it('should require both username and password', function() {
    expect(login.getWebElement().isEnabled()).toBe(false);
//    expect(logout.isPresent()).toBe(false);
//    expect(login.isPresent()).toBe(true);
    //expect(browser.getTitle()).toEqual('Maritime Cloud Portal');
  });

  it('should require both username and password', function() {
    expect(login.getWebElement().isEnabled()).toBe(false);
    usernamefield.sendKeys('admin');
    expect(login.getWebElement().isEnabled()).toBe(false);
    passwordfield.sendKeys('tes');
    expect(login.getWebElement().isEnabled()).toBe(false);
    passwordfield.sendKeys('t');
    expect(login.getWebElement().isEnabled()).toBe(true);
  });
});

