'use strict';

// Declare app level module which depends on filters, and services
angular.module('hp', [
  'ui.bootstrap',
  'ngRoute',
  'hp.filters',
  'hp.services',
  'hp.directives',
  'hp.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/home', {templateUrl: 'partials/home.html', controller: 'HomeCtrl'});
  $routeProvider.when('/about', {templateUrl: 'partials/about.html'});
  $routeProvider.otherwise({redirectTo: '/home'});
}]);
