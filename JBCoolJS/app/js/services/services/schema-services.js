/**
 * New node file
 */

var schemaServices = angular.module('schemaServices', [ 'ngResource' ]);

schemaServices
.constant('baseurl',
        {'url': 'http://localhost:8080/JBRestCool/rest/'
        })
/*
 * This service retrieves schemas and nodes
 */
.factory(
		'Schema',
		[
		 '$resource', 'baseurl', 'HostSvc',
		 function($resource,baseurl,HostSvc) {
			 var url = baseurl.url+'plsqlcooljson/:schema/:dbname/:action';
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }
			 
			 return $resource(
					 url,
					 {}, {
						 nodes : {
							 method : 'GET',
							 params : {
								 schema : 'ATLAS_COOL',
								 dbname : 'COMP200',
								 action : 'nodes'
							 },
							 isArray : true
						 },
						 list : {
							 method : 'GET',
							 params : {
								 schema : 'ATLAS_COOL',
								 dbname : 'COMP200',
								 action : 'schemas'
							 },
							 isArray : true
						 }
					 });
		 } ])
/*
 * This service retrieves tags using schemas and nodes.
 */
.factory(
		'Tag',
		[
		 '$resource', 'baseurl', 'HostSvc',
		 function($resource,baseurl,HostSvc) {
			 var url = baseurl.url+'plsqlcooljson/:schema/:dbname/:fld*';
			 var host = HostSvc.gethost();
			 if (host != 'localhost') {
				 console.log('replace url with '+url.replace('localhost',host));
				 url = url.replace('localhost',host);
			 }
			 
			 return $resource(
					 url,
					 { fld: '@fld'}, {
						 list : {
							 method : 'GET',
							 params : {
								 schema : 'ATLAS_COOL',
								 dbname : 'COMP200',
								 fld    : '@fld'
								 //action : 'tags'
							 },
							 isArray : true
						 }
					 });
		 } ])
/**
 * Test service for http usage: promise test
 */
.factory('HttpJBCool', ['$http','$q','baseurl','HostSvc',function($http, $q, baseurl,HostSvc) {
	
	var deferredData = $q.defer();

	 var baseurl = baseurl.url;
	 var host = HostSvc.gethost();
	 if (host != 'localhost') {
		 console.log('replace url with '+baseurl.replace('localhost',host));
		 baseurl = baseurl.replace('localhost',host);
	 }
	 
	  return {
	    get: function(url) {
	      var usedurl = baseurl + url;

	      console.log('Using url '+usedurl);
	      deferredData = $q.defer();
	  	  $http.get(usedurl).success(function(data) {
	  	    //success, resolve your promise here
	  		  //console.log('Resolving data '+data);
	  	    deferredData.resolve(data);
	  	  }).error(function(err) {
	  	    //error, use reject here
	  		console.log('Error while retrieving data '+err);
	  	    deferredData.reject(err);
	  	  });
	    	    	
	      return deferredData.promise;
	    }
	  };
}])
/**
 * Same with callback
 */
.factory('HttpTagCb', ['$http','baseurl', 'HostSvc',function($http, baseurl,HostSvc) {
	
	var baseurl = baseurl.url;
	 var host = HostSvc.gethost();
	 if (host != 'localhost') {
		 console.log('replace url with '+baseurl.replace('localhost',host));
		 baseurl = baseurl.replace('localhost',host);
	 }
	
	return { 
		getTags : function(tagurl, callback) {
			var url = baseurl+tagurl;
			console.log('Calling getTags '+url);

			$http.get(url)
			.success(callback)
			.error(function() {
				console.log('Error retrieving tag data for '+url);
			});
		}
	};
}]);

