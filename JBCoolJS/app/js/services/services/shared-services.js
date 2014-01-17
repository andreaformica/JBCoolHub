/**
 * New node file
 */

var sharedServices = angular.module('sharedServices', [ 'ngResource' ]);

sharedServices
/*
 * This service handles global variables content
 */
.factory('HostSvc', function() {
	var resthost = 'pcsaclay32c.cern.ch';
//	var resthost = 'localhost';
		return {
			gethost: function() {
				return resthost;
			}
		}
	}
)
.factory('UserFormData', function() {
	var formdata = {
			dbname: { name: 'COMP200', description: 'Data'}, 
			schemaname: 'ATLAS_COOL',
			foldername: 'none',
			tagname   : 'none',
			gtagname  : 'COMCOND-BLKPA'
	};

	var rundata = {
			runmin: { number: undefined, tspan: 'run'}, 
			runmax: { number: undefined, tspan: 'run'},
			tstart: { format: 'yyyyMMddHHmmSS', value : undefined},
			tend  : { format: 'yyyyMMddHHmmSS', value : undefined},
			tspan : 'run',
			rtype : 'Physics',
			period: 'data'
	};

	var dbnames = [{name: 'COMP200', description: 'Data'},	
	               {name: 'OFLP200', description: 'MC' }];

	return {
		getData: function() {
			console.log('UserForm: getData');
			return formdata;
		},
		setData: function(newData) {
			formdata = newData;
		},
		getDbs: function() {
			console.log('UserForm: Retrieve db names list...');
			return dbnames;
		},
		getComarun: function() {
			console.log('UserForm: Retrieve COMA runmin and runmax...');
			return rundata;
		},
		setComarun: function(newData) {
			rundata = newData;
		}
	};
})
/*
 * This service handles table column generation
 */
.factory('ColumnService', function() {

	return {
		getColumns: function(row, skipped) {
			var columnslist = [];
			angular.forEach(Object.keys(row), function(key){
				var skipit = false;
				for (var i=0; i < skipped.length; i++) {
					if (key.search(skipped[i])>=0) {
						skipit = true;
						console.log("Skip key "+key+" because matching "+skipped[i]);
						continue;
					} 
				}
				if (!skipit)
					columnslist.push({ field: key });
			});			

			return columnslist;
		}
	};
})
/*
 * This service handles table column generation
 */
.factory('CoolService', function(Schema) {

	var allschemas = [];
	var allfolders = [];
	var useddb = 'COMP200';
	
	allschemas = Schema.list(
			{schema: 'ATLAS_COOL', 
				 dbname: useddb
				}, 
				function(schemas) {
					console.log('Retrieved list of n '+schemas.length+' schemas ');
				}
		);

	allfolders = Schema.nodes(
			{schema: 'ATLAS_COOL', 
				 dbname: useddb
				},
				function(folders) {
					console.log('Retrieved list of n '+folders.length+' nodes ');
				}
		);

	
	return {
		getCoolSchemas: function() {	
			return allschemas;
		},
		getCoolNodes: function() {
			return allfolders;
		},
		getNode : function(schema, nodename) {
			var filterednode = allfolders.filter(function(el){
				//console.log('Testing el '+el.name);
				return (el.nodeFullpath == nodename) && (el.schemaName == schema);
			});
			return filterednode;
		}
	};
})
/*
 * This service handles table column generation
 */
.factory('CoverageService', function() {

	var summarytree = {};
	var allschemas = [];
	var allfolders = [];
	var foldersstat = [];
	var cooltree = { name : 'coolroot', children : [] };
	var colorflags = { "black" : 0, "darkgreen" : 1, "darkorange" : 2, "orange" : 3, "darkred" : 4, "red" : 5};

	var getStat = function getStat(schema, dbname, foldername, foldersstatarr) {
		var statarr = [];
		var schemafolders = []; 

		console.log('Get stat for schema '+schema+' and folder '+foldername+' using tree '+foldersstatarr[0].name);
		for (var i = 0; i < foldersstatarr[0].children.length; i++) {
	  		var schemaname = foldersstatarr[0].children[i].name;
	  		//console.log('Check schema '+schemaname);
	  		if (schemaname === schema) {
	  			schemafolders = foldersstatarr[0].children[i].children;
	  			for (var j=0; j<schemafolders.length; j++) {
	  				if (schemafolders[j].nodeName === foldername) {
	  					//console.log('Found folder '+schemafolders[j].name+' totaliovs '+schemafolders[j].totalIovs);
	  					statarr = [];
	  					statarr.push({ name : 'tag', value : schemafolders[j].tagName});
	  					var linkfolder = foldername.replace(/\//g,'__');
	  					var taginfourl = '#/taginfo/'+schemaname+'/'+dbname+'/'+linkfolder+'/'+schemafolders[j].tagName;
	  					statarr.push({ name : 'taginfo', value : taginfourl});
	  					statarr.push({ name : 'totalIovs', value : schemafolders[j].totalIovs});
	  					statarr.push({ name : 'nchannels', value : schemafolders[j].nchannels});
	  					statarr.push({ name : 'totalHoles', value : schemafolders[j].totalHoles});
	  					statarr.push({ name : 'totalRuns', value : schemafolders[j].totalRuns});
	  					statarr.push({ name : 'totalRunsInHole', value : schemafolders[j].totalRunsInHole});
	  					statarr.push({ name : 'minRun', value : schemafolders[j].minRun});
	  					statarr.push({ name : 'maxRun', value : schemafolders[j].maxRun});
	  					statarr.push({ name : 'minSince', value : schemafolders[j].minSince});
	  					statarr.push({ name : 'maxUntil', value : schemafolders[j].maxUntil});
	  					return statarr;
	  				}
	  			}	
	  		}
		}
		return statarr;
	};

	var schematreebuilder = function() {
		cooltree.children.length = 0;
		for (var i = 0; i < allschemas.length; i++) {
			//console.log('Loop over schemas '+i+' '+allschemas[i].schemaName+' '+JSON.stringify(allschemas[i]));
			var schemaname = allschemas[i].schemaName;
			cooltree.children[i] = {};
			var aschema = cooltree.children[i];
			aschema.coolname = schemaname;
			aschema.name = allschemas[i].shortName;
			aschema.nfolders = allschemas[i].nfolders;
			aschema.children = [];
			//console.log("Schema Name found #" + i + " is "+schemaname);
			var childrenfolders = aschema.children;
			var schemacolor = "darkgreen";
			for (var j = 0; j < allfolders.length; j++) {
				var folderschemaname = allfolders[j].schemaName;
				//console.log('Loop over folders '+j+' '+allfolders[j].schemaName+' '+JSON.stringify(allfolders[j]));
				if (schemaname === folderschemaname) {
					var ifolder = childrenfolders.length;
					childrenfolders[ifolder] = {};
					childrenfolders[ifolder].parentschema =aschema.name;
					childrenfolders[ifolder].name = allfolders[j].nodeFullpath;
					childrenfolders[ifolder].content = allfolders[j];
					var statarr = getStat(schemaname, allfolders[j].dbName, childrenfolders[ifolder].name, foldersstat);
					childrenfolders[ifolder].children = statarr;
					var color = "black";
					if (statarr != null && statarr.length > 0) {
						color = "darkgreen";
						for ( var is=0; is < statarr.length; is++) {
							if (statarr[is].name === "totalIovs") {
								var niovs = statarr[is].value;
								if (niovs == 1) {
									color = "darkblue";
								} else if (niovs == 0) {
									color = "darkred";
								}
							}
							if (statarr[is].name === "totalHoles") {
								var nholes = statarr[is].value;
								if (nholes > 0) {
									color = "darkred";
								}
							}
							if (statarr[is].name === "totalRunsInHole") {
								var nrholes = statarr[is].value;
								if (nrholes > 0) {
									color = "red";
								} else if (colorflags[color] >= colorflags["darkred"]) {
									color = "darkorange";
								}
							}
						}
					}
					childrenfolders[ifolder].color = color;
					if (colorflags[color] > colorflags[schemacolor]) {
						schemacolor = color;
					}
				}
			}
			aschema.color = schemacolor;
		}
	};

	return {
		getTree: function() {
			return summarytree;			
		},
		setTree: function(newTree) {
			summarytree = newTree;
			foldersstat.length = 0;
			foldersstat.push(newTree);
		},
		setSchemas: function(schemalist) {
			allschemas = schemalist;
		},
		setFolders: function(folderlist) {
			allfolders = folderlist;
		},
		buildCoolTree: function(){
			schematreebuilder();
			return cooltree;
		}
	};
})
.factory('SchemaData', function() {

	/**
	 * The object in which we store all parameters from the schema.html form.
	 */
	var schemaParameters = { 
			dbname  : undefined, 
			//gridopt : undefined, 
			list    : undefined, 
			schema  : undefined,
			columns : undefined
	};
	/**
	 * The object in which we store all parameters from the schemadetails.html form.
	 */
	var nodeParameters = { 
			dbname  : undefined, 
			//gridopt : undefined, 
			list    : undefined, 
			schema  : undefined,
			node    : undefined,
			columns : undefined
	};

	/**
	 * The object in which we store all parameters from the tags.html form.
	 */
	var tagParameters = { 
			dbname  : undefined, 
			//gridopt : undefined, 
			list    : undefined, 
			schema  : undefined,
			folder  : undefined,
			tag     : undefined,
			columns : undefined,
			resturl : 'plsqlcooljson/',
			url     : 'none'
	};
	/**
	 * The object in which we store all parameters from the tags.html form.
	 */
	var iovParameters = { 
			dbname  : tagParameters.dbname, 
			//gridopt : undefined, 
			list    : undefined, 
			schema  : tagParameters.schema,
			folder  : tagParameters.folder,
			iovtype : undefined,
			tag     : tagParameters.tag,
			columns : undefined,
			channel : 'all',
			chansel : 'channel',  // This can be chanid, if single channel selection is on
			resturl : 'plsqlcooljson/',
			url     : 'none'
	};

	/**
	 * The object in which we store all parameters from the tags.html form.
	 */
	var gtagParameters = { 
			dbname  : undefined, 
			//gridopt : undefined, 
			list    : undefined, 
			schema  : undefined,
			gtag    : undefined,
			columns : undefined
	};

	/**
	 * The object in which we store all parameters from the tags.html form.
	 */
	var runParameters = { 
			runmin  : undefined, 
			runmax  : undefined, 
			rtype   : 'Physics',
			period  : 'data',
			list    : undefined, 
			//gridopt : undefined,
			columns : undefined
	};

	function JBCoolUrl() {
		this.url = undefined;
		this.resturl = 'plsqlcooljson/';

	};
	
	return {
		schemas: function() {
			console.log('SchemaData schemas');
			return schemaParameters;
		},
		setSchemas: function(newData) {
			schemaParameters = newData;
		},
		nodes: function() {
			console.log('SchemaData nodes');
			return nodeParameters;
		},
		setNodes: function(newData) {
			nodeParameters = newData;
		},
		tags: function() {
			console.log('SchemaData tags');
			angular.forEach(Object.keys(tagParameters), function(key){
				console.log('Found key '+key);
				if (key == 'chanlist') {
					console.log('Length of chanlist '+tagParameters.chanlist.length);
				}
			});

			return tagParameters;
		},
		setTags: function(newData) {
			tagParameters = newData;
		},
		iovs: function() {
			console.log('SchemaData iovs');
			angular.forEach(Object.keys(iovParameters), function(key){
				console.log('Found key '+key);
				if (key == 'chanlist') {
					console.log('Length of chanlist '+iovParameters.chanlist.length);
				}
			});

			return iovParameters;
		},
		setIovs: function(newData) {
			iovParameters = newData;
		},
		gtags: function() {
			console.log('SchemaData gtags');
			return gtagParameters;
		},
		setGtags: function(newData) {
			gtagParameters = newData;
		},
		iovperchan: function() {
			tagParameters.url = tagParameters.resturl+tagParameters.schema
			+'/'+tagParameters.dbname.name+tagParameters.node+'/fld/'
			+tagParameters.tag+'/tag/iovsperchan';
			console.log('Creating iovperchan url '+tagParameters.url);
		},
		coverageurl: function() {
			tagParameters.url = tagParameters.resturl+tagParameters.schema
			+'/'+tagParameters.dbname.name+tagParameters.node+'/fld/'
			+tagParameters.tag+'/tag/';
			console.log('Creating tag coverage url '+tagParameters.url);
		},
		comaruns: function() {
			console.log('SchemaData comaruns');
			return runParameters;
		},
		payloadurl: function(schema,dbname,folder,tag,tmin,tmax,iovtype,chan,chansel) {
			var urlgen = new JBCoolUrl();
			var url = urlgen.resturl+schema+'/'+dbname+folder+'/fld/';
			if (tag == undefined) {
				tag = 'none';
			} 
			if (chan == undefined) {
				chansel = 'channel';
				chan = 'all';
			}
			var since = tmin;
			var until = tmax;
			var timesel = 'runlb';
			if (iovtype == 'time') {
				since = tmin.toString('yyyyMMddHHmmss');
				until = tmax.toString('yyyyMMddHHmmss');
				timesel = 'date';
			} else {
				since = tmin + ' - 0';
				until = tmax + ' - 0';
			}
			url += (tag+'/tag/'+chan+'/'+chansel+'/'+since+'/'+until+'/'+timesel);
			url += '/data/list';
			console.log('Creating payload url '+url);
			urlgen.url = url;
			return urlgen;
		},
		channelsurl: function(schema,dbname,folder) {
			var urlgen = new JBCoolUrl();
			var url = urlgen.resturl+schema+'/'+dbname+folder+'/fld/';
			url += 'all/channels';
			console.log('Creating channels url '+url);
			urlgen.url = url;
			return urlgen;
		},
		setComaRuns: function(newData) {
			runParameters = newData;
			console.log('setComaRuns: contains a list of n '+runParameters.list.length)
		}
	};
});
