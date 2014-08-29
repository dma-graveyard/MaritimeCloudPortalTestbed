
//var env = require('./environment.js');

// A small suite to make sure the cucumber framework works.
exports.config = {
//  seleniumAddress: env.seleniumAddress,
  seleniumAddress: 'http://localhost:4444/wd/hub',

  framework: 'cucumber',

  // Spec patterns are relative to this directory.
  specs: [
    'cucumber/*.feature'
  ],

//capabilities: env.capabilities,
capabilities : {
    browserName : 'chrome',
    'chromeOptions': {
        args: ['--test-type']
    }
},


//  multiCapabilities: [{
////      browserName: 'firefox'
////    }, {
//      browserName: 'chrome'
//    }],

//  baseUrl: env.baseUrl,
  baseUrl: "http://localhost:8080",

  cucumberOpts: {
    require: 'cucumber/stepDefinitions.js',
    tags: '@dev',
    format: 'summary'
  }
};

