/**
 * New node file
 */

var comaServices = angular.module('comaServices', [ 'ngResource' ]);

comaServices
.constant('baseurl',
        {'url': 'http://localhost:8080/JBRestCool/rest/'
        })
.factory(
		'ComaRun', 
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc) {
			 var url = baseurl.url+':restpath/:rmin/:rmax/:action';
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }
			 
			 return $resource(
					 url,
					 {},
					 {
						 runs : {
							 method : 'GET',
							 params : {
								 restpath : 'comajson',
								 rmin   : '0',
								 rmax   : '999999',
								 action : 'runs'
							 },
							 isArray : true
						 } 
					 });
}])
.factory(
		'ComaRunRange',
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc)  {
			 var url = baseurl.url+':restpath/:tmin/:tmax/:tspan/:action';
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 url = url.replace('localhost',host);
			 }
			
			 return $resource(
					 url,
					 {},
					 {
						 list : {
							 method : 'GET',
							 params : {
								 restpath : 'comajson',
								 tmin   : '0',
								 tmax   : 'Inf',
								 tspan  : 'time',
								 action : 'runsbyiov'
							 },
							 isArray : true
						 }	 
		 });
}])
.factory(
		'ComaRunType',
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc)  {
			 var url = baseurl.url+':restpath/:rmin/:rmax/:type/:period/:action';
			
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }
			 return $resource(
					 url,
					 {},
					 {
						 list : {
							 method : 'GET',
							 params : {
								 restpath : 'comajson',
								 rmin   : '0',
								 rmax   : '999999',
								 type   : 'Physics',
								 period : 'data',	 
								 action : 'runs'
							 },
							 isArray : true
						 }	 
		 });
}])
.factory(
		'ComaRunRangeType',
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc)  {
			 var url = baseurl.url+':restpath/:tmin/:tmax/:tspan/:type/:period/:action';
			
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }
			 return $resource(
					 url,
					 {},
					 {
						 list : {
							 method : 'GET',
							 params : {
								 restpath : 'comajson',
								 tmin   : '0',
								 tmax   : 'Inf',
								 tspan  : 'time',
								 type   : 'Physics',
								 period : 'data',	 
								 action : 'runsbyiov'
							 },
							 isArray : true
						 }	 
		 });
}]);
