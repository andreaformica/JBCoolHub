/**
 * New node file
 */

var gtagServices = angular.module('gtagServices', [ 'ngResource' ]);

gtagServices
.constant('baseurl',
        {'url': 'http://localhost:8080/JBRestCool/rest/'
        })
.factory(
		'Gtag',
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc) {
			
			 var url = baseurl.url+':restpath/:schema/:dbname/:gtag/:action';
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }

			 // action can be schemasummary to get the full summary of a globaltag
			 
			 return $resource(
					 //'http://localhost:8080/JBRestCool/rest/:restpath/:schema/:dbname/:gtag/:action',
					 url,
					 {},
					 {
						 list : {
							 method : 'GET',
							 params : {
								 restpath : 'coolgtagjson',
								 schema : 'ATLAS_COOL',
								 dbname : 'COMP200',
								 gtag   : 'COMCOND',
								 action : 'list'
							 },
							 isArray : true
						 },
						 listtags : {
							 method : 'GET',
							 params : {
								 restpath : 'plsqlcooljson',
								 schema : 'ATLAS_COOL',
								 dbname : 'COMP200',
								 gtag   : 'COMCOND',
								 action : 'trace'
							 },
							 isArray : true
						 },
						 summary : {
							 method : 'GET',
							 params : {
								 restpath : 'coolgtagjson',
								 schema : 'ATLAS_COOLOFL_MUONALIGN',
								 dbname : 'COMP200',
								 gtag   : 'COMCOND-BLKPA-RUN1-03',
								 action : 'schemasummary'
							 },
							 isArray : false
						 }
					 });
}])
.factory(
		'GtagState',
		['$resource', 'baseurl', 'HostSvc', function($resource,baseurl,HostSvc) {
			
			 var url = baseurl.url+':restpath/:state/:action';
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
								 state: 'all',
								 action : 'gtagstate'
							 },
							 isArray : true 
						 },
						 coverage : {
							 method : 'GET',
							 params : {
								 restpath : 'coolgtagjson',
								 state    : 'COMCOND-BLKPA-RUN1-03',
								 action   : 'coverage'
							 },
							 isArray : false
						 }	 
					 });
		 } ]);
