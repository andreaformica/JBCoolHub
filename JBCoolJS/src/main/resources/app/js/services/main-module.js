//__________________________________________________________________________
/**
 * JBCool browser javascript library -  November 2013
 * @author Andrea Formica
 * This software is governed by the
 * CeCILL license (http://www.cecill.info/index.en.html)
 */
//__________________________________________________________________________
"use strict";
/**
 * This a AngularJS module
 * @see http://angularjs.org/
 */

var mainModule = angular.module('main-module',
                 ['ngRoute',
                  'ngResource',
                  'ngGrid',
                  'ui.bootstrap',
                  '$strap.directives',
                  'navigation-module',
                  'schemaControllers',
                  'schemaServices',
                  'gridServices',
                  'sharedServices',
                  'gtagControllers',
                  'gtagServices',
                  'tagControllers',
                  'comarunControllers',
                  'comaServices',
                  'chartServices'
  //                'tabDirectives'
                  ]);

mainModule
/*
 * Constant parameters
 */
.constant('version',
              {'release': '0.8',
               'date':    '24 September 2013'
              })
/*
 * datepicker configuration
 */
.value('$strapConfig', {
  datepicker: {
    language: 'en',
    format: 'M d, yyyy'
  }
})          
/**
 * The routing configuration
 */
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
        console.log('Main module configuration');
        //$locationProvider.html5Mode(true).hashPrefix('!');
        $routeProvider
    	//.when('/app/schema', {
        	.when('/schema', {
        		templateUrl: 'views/schema.html',
        		controller: 'SchemaCtrl'
        	})
            .when('/nodes', {
                templateUrl: 'views/schemadetails.html',
                controller: 'SchemaDetailCtrl'
            })
            .when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl'
            })
            .when('/tags', {
                templateUrl: 'views/tags.html',
                controller: 'TagCtrl'
            })
            .when('/taginfo', {
                templateUrl: 'views/tagsdetails.html',
                controller: 'TagInfoCtrl'
            })
            .when('/taginfo/:schema/:dbname/:fld/:tag', {
                templateUrl: 'views/tagsdetails.html',
                controller: 'TagInfoCtrl'
            })
            .when('/textcoverage', {
                templateUrl: 'views/textcoverage.html',
                controller: 'TagInfoCtrl'
            })
            .when('/gtagcoverage', {
                templateUrl: 'views/gtagcoverage.html',
                controller: 'GtagCoverageCtrl'
            })
            .when('/globaltag', {
                templateUrl: 'views/globaltag.html',
                controller: 'GtagCtrl'
            })
            .when('/tracetags', {
                templateUrl: 'views/globaltag.html',
                controller: 'GtagCtrl'
            })
            .when('/runinfo', {
                templateUrl: 'views/comarun.html',
                controller: 'ComaRunCtrl'
            })
            .when('/runinfo/:tmin/:tmax/:tspan/', {
                templateUrl: 'views/comarun.html',
                controller: 'ComaRunCtrl'
            })
            .when('/root', {
                templateUrl: 'views/monitoring.html',
                controller: 'RootCtrl'
            })
           /*
            .when('/coma/:rmin/:rmax/runs', {
                templateUrl: 'views/runs.html',
                controller: 'ComaRunCtrl'
            })*/
            .otherwise({
                redirectTo: '/home'
            });
    }])
    .run(function($rootScope, $location) {
        console.log('Main module started ...'+$location.path());
        $rootScope.hostname = $location.host();
        console.log('set hostname to...'+$rootScope.hostname);
    })
    .controller('MainCtrl',
            ['$rootScope', '$scope', 'version', 
              function($rootScope, $scope, version) {
                     console.log('MainCtrl version:',
                                 version.release, version.date);
                     /**
                      * Welcome message
                      */
                     $rootScope.message = 'Welcome to JBCoolJS! (MainCtrl)';
                     $rootScope.version = 'Version ' + version.release
                         + ' - ' + version.date;
                     $rootScope.status  = { role : 'atlas user' };
                     
                     console.log('MainCtrl started');
                     
                 }]);
