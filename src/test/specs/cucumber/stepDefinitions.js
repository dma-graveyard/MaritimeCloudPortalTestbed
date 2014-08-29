/*  Copyright 2014 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


// Use the external Chai As Promised to deal with resolving promises in
// expectations.
var chai = require('chai');
var chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);

var expect = chai.expect;

// Chai expect().to.exist syntax makes default jshint unhappy.
// jshint expr:true

module.exports = function() {

  this.Given(/^I run Cucumber with Protractor$/, function(next) {
    console.log("HELLO");
    next();
  });

  this.Given(/^I go on(?: the website)? "([^"]*)"$/, function(url, next) {
    browser.get(url);
    next();
  });

  this.Then(/^it should still do normal tests$/, function(next) {
    expect(true).to.equal(true);
    next();
  });

  this.Then(/^it should expose the correct global variables$/, function(next) {
    expect(protractor).to.exist;
    expect(browser).to.exist;
    expect(by).to.exist;
    expect(element).to.exist;
    expect($).to.exist;
    next();
  });

  this.Then(/the title should equal "([^"]*)"$/, function(text, next) {
    console.log(browser.getTitle().then(function(data) {
      console.log(data);
    }));
    expect(browser.getTitle()).to.eventually.equal(text).and.notify(next);
  });

  this.When(/^I click on "([^"]*)"$/, function(linkText, next) {
    
//    
//    //console.log(
//    element(by.css(".skin-1")).then(function(data) {
//      //console.log("body", data, data1);
//      console.log("body inner ", data.getOuterHtml());
//      data.getInnerHtml().then(function(data) {
//        //console.log("body", data, data1);
//        console.log("body inner ", data);
//      })
//    });
//    //);
//
//    //console.log(
//    //element(by.css(".skin-1")).getInnerHtml().then(function(data) {
//    element(by.partialButtonText("Log")).getTagName().then(function(data) {
//      console.log("Body II ", data);
//    });
//    //);

    var clickable = element(by.id("logout"));
    clickable.click();
    next();
  });

  this.Then(/^I should see "([^"]*)"$/, function(arg1, next) {
    // Write code here that turns the phrase above into concrete actions
//    callback.pending();
    next();
  });

};