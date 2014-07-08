
APP = 'src/main/webapp/app';
TEST = 'src/test/webapp/';

module.exports = function(config){
  config.set({

    basePath : '../../../',

    files : [
      APP + '/bower_components/angular/angular.js',
      APP + '/bower_components/angular-route/angular-route.js',
      APP + '/bower_components/angular-mocks/angular-mocks.js',
      APP + '/bower_components/underscore/underscore.js',
      APP + '/js/**/*.js',
      TEST + '/unit/**/*.js'
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['PhantomJS'],

    plugins : [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-phantomjs-launcher',
            'karma-jasmine',
            'karma-junit-reporter'
            ],

    junitReporter : {
      outputFile: 'target/angular_unit.xml',
      suite: 'unit'
    }

  });
};
