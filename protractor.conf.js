// conf.js
exports.config = {
  seleniumAddress: 'http://localhost:4444/wd/hub',
  baseUrl: "http://localhost:8080",
  specs: ['src/test/specs/*.js'],
  multiCapabilities: [{
      browserName: 'firefox'
//    }, {
//      browserName: 'chrome'
    }]
}