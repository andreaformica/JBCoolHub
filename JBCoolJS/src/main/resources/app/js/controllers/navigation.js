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
angular
.module('globalErrors', [])
.config(function($provide, $httpProvider, $compileProvider) {
    var elementsList = $();

    var showMessage = function(content, cl, time) {
        $('<div/>')
            .addClass('message')
            .addClass(cl)
            .hide()
            .fadeIn('fast')
            .delay(time)
            .fadeOut('fast', function() { $(this).remove(); })
            .appendTo(elementsList)
            .text(content);
    };
    
    $httpProvider.responseInterceptors.push(function($timeout, $q) {
        return function(promise) {
            return promise.then(function(successResponse) {
                if (successResponse.config.method.toUpperCase() != 'GET')
                    showMessage('Success', 'successMessage', 5000);
                return successResponse;

            }, function(errorResponse) {
                switch (errorResponse.status) {
                    case 401:
                        showMessage('Wrong usename or password', 'errorMessage', 20000);
                        break;
                    case 403:
                        showMessage('You don\'t have the right to do this', 'errorMessage', 20000);
                        break;
                    case 500:
                        showMessage('Server internal error: ' + errorResponse.data, 'errorMessage', 20000);
                        break;
                    default:
                        showMessage('Error ' + errorResponse.status + ': ' + errorResponse.data, 'errorMessage', 20000);
                }
                return $q.reject(errorResponse);
            });
        };
    });

    $compileProvider.directive('appMessages', function() {
        var directiveDefinitionObject = {
            link: function(scope, element, attrs) { elementsList.push($(element)); }
        };
        return directiveDefinitionObject;
    });
});

var navigationModule = angular.module('navigation-module',
                 ['ui.bootstrap']);

angular.module('navigation-module')
    .constant('version',
              {'release': '0.1',
               'date':    '17 November 2013'
              })
/**
 * The routing configuration
 */
    .config(function ($routeProvider, $locationProvider) {
        console.log('Navigation module configuration');
        $locationProvider.html5Mode(true);
    })
/**
 * The status is set when a user successfully logs in.
 */
    .value('status', {
        role:      undefined
    })
    .run(function() {
        console.log('navigation module started ...');
    })
    
    /**
 * Controls what happens on the home page.
 */
    .controller('HomeCtrl', ['$rootScope', '$scope', 'status', 'CoolService',
                             function($rootScope, $scope, status, CoolService) {
    	$rootScope.errormessage = { text : 'no errors', color : 'black'};
        $rootScope.errormessage.text = 'Welcome, no errors until now....';
        $rootScope.message = 'Home controller started';
        $rootScope.status  = status;
        
        // Init schemas and nodes for COMP200
        console.log('Loading schemas and nodes for COMP200');
        CoolService.getCoolSchemas();
        CoolService.getCoolNodes();
    }])
