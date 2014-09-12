// Karma configuration
// Generated on Fri Aug 08 2014 11:52:13 GMT+0200 (CEST)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '.',

    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['mocha', 'chai'],

    // list of files / patterns to load in the browser
    files: [
      'src/main/webapp/ext/bower/angular/angular.js',
      'src/main/webapp/ext/bower/angular-resource/angular-resource.js',
      'src/main/webapp/ext/bower/angular-mocks/angular-mocks.js',
      'src/main/webapp/ext/bower/angular-bootstrap/ui-bootstrap-tpls.js',
      'src/main/webapp/ext/bower/angular-ui-router/release/angular-ui-router.js',
      'src/main/webapp/ext/bower/angular-leaflet-directive/dist/angular-leaflet-directive.js',
      'src/main/webapp/ext/js/angular-ui.github.io/ui-router/stateHelper/1.1.0/statehelper.min.js',
      'src/main/webapp/ext/js/rawgithub.com/gsklee/ngStorage/master/ngStorage.js',
      'src/main/webapp/app/**/*.js',
      'src/test/js/*.js',
      // templates to be compiled by karma 
      'src/main/webapp/app/**/*.html'
    ],

    // list of files to exclude
    exclude: [
      'src/main/webapp/app/assets/**/*.js',
    ],

    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      '**/*.html': ['ng-html2js']
    },

    ngHtml2JsPreprocessor: {
      
      // Note to SAFARI users:
      // If you change settings in this section you may need to clear 
      // the cache of you safari browser in order to make the changes 
      // kick in!
      
      // strip this from the file path
      stripPrefix: 'src/main/webapp/app/',
      // prepend this to the
      //prependPrefix: '',

      //// or define a custom transform function
      //cacheIdFromPath: function(filepath) {
      //  console.log("FILEPATH "+filepath);
      //  return filepath;
      //},

      //// setting this option will create only a single module that contains templates
      //// from all the files, so you can load them all with module('foo')
      //moduleName: 'foo'
    },  
      
    //plugins : [
    //        'karma-chai',
    //        'karma-chrome-launcher',
    //        'karma-firefox-launcher',
    //        'karma-junit-reporter',
    //        'karma-mocha',
    //        'karma-ng-html2js-preprocessor'
    //        ],

    junitReporter : {
      outputFile: 'target/karma-reports/unit.xml',
      suite: 'unit'
    },
      
    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'junit'],

    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: true,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    //browsers: ['Chrome'],
    browsers: ['Chrome', 'Safari', 'Firefox'],
    //browsers: ['Safari'],

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
