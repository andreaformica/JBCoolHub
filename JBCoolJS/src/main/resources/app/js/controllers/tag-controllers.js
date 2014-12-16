/**
 * New node file
 */
var tagControllers = angular.module('tagControllers', []);

tagControllers
/**
 * The controller for schemas search.
 */
.controller('TagCtrl', ['$rootScope',
                        '$scope', 
                        '$location', 
                        'Tag', 
                        'GridOptData',
                        'HttpTagCb', 
                        'UserFormData',
                        'CoolService',
                        'SchemaData',
                        'ColumnService',
                        function ($rootScope,$scope, $location, 
                        		Tag, GridOptData, HttpTagCb, 
                        		UserFormData, 
                        		CoolService,
                        		SchemaData,
                        		ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.tags();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.schemaformparams.node = form.foldername;
	$scope.colDefs = [];
	$scope.tagSelection = [];
	$scope.tags = [];
	$scope.useleaftag = false;

	$scope.allschemas = CoolService.getCoolSchemas();
	$scope.showsearch = true;
	
	// Init tabs
	$scope.tabs = [{ title: 'Tags', content: 'List of tags' , showtags: true}
	               ];

	console.log('Initialize the tag controller...');

	// Init variables from the stored SchemaData object.
	if ($scope.schemaformparams.list) {
		//$scope.schemaGridOptions = $scope.schemaformparams.gridopt;
		$scope.tags = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} 

	// Changed 2013/12/05 23:42
	$scope.tagGridOptions = GridOptData.getgrid('taggrid');

	if ($scope.tagGridOptions == undefined) {

		console.log('Init grid options');
		$scope.tagGridOptions = GridOptData.gridoptions();
		$scope.tagGridOptions.data = 'tags';
		$scope.tagGridOptions.columnDefs = 'colDefs';
		$scope.tagGridOptions.selectedItems = $scope.tagSelection;
		$scope.tagGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('selection changed to '+rowItem);
			if ($scope.tagSelection.length > 0) {
				console.log('Using selection for tag: '+$scope.tagSelection[0].tagName);
				$scope.schemaformparams.schema = $scope.tagSelection[0].schemaName;	
				$scope.schemaformparams.node = $scope.tagSelection[0].nodeFullpath;	
				$scope.schemaformparams.tag = $scope.tagSelection[0].tagName;	
				form.schemaname = $scope.schemaformparams.schema;
				form.foldername = $scope.schemaformparams.node;
				form.tagname = $scope.schemaformparams.tag;
				$scope.useleaftag = true;
				UserFormData.setData(form);
			} else {
				$scope.useleaftag = false;
			}
		};
		GridOptData.addgrid('taggrid', $scope.tagGridOptions);
	}

	// Init variables from the stored SchemaData object.
/*	if ($scope.schemaformparams.gridopt != undefined) {
		$scope.tagGridOptions = $scope.schemaformparams.gridopt;
		$scope.tags = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} else {
		$scope.tagGridOptions = { 
				data: 'tags' ,
				columnDefs: 'colDefs',
				showGroupPanel: false,
				enablePaging: false,
				showFooter: true,
				showFilter: true,
				showColumnMenu: true,
				enableColumnResize: true,
				enableColumnReordering: true,
				selectedItems: $scope.tagSelection,
				afterSelectionChange: function(rowItem, evt) {
					console.log('selection changed to '+rowItem);
					if ($scope.tagSelection.length > 0) {
						console.log('Using selection for tag: '+$scope.tagSelection[0].tagName);
						$scope.schemaformparams.schema = $scope.tagSelection[0].schemaName;	
						$scope.schemaformparams.node = $scope.tagSelection[0].nodeFullpath;	
						$scope.schemaformparams.tag = $scope.tagSelection[0].tagName;	
						form.schemaname = $scope.schemaformparams.schema;
						form.foldername = $scope.schemaformparams.node;
						form.tagname = $scope.schemaformparams.tag;
						$scope.useleaftag = true;
						UserFormData.setData(form);
					} else {
						$scope.useleaftag = false;
					}
				},	        
				multiSelect: false
		};
		console.log('Init grid options');
		$scope.schemaformparams.gridopt = $scope.tagGridOptions;
	}*/

	$scope.submit = function submit() {
		$scope.colDefs.length = 0;
		console.log('Search tags using:');
		console.log('Schema pattern '+$scope.schemaformparams.schema);
		console.log('Folder '+$scope.schemaformparams.node);
		console.log('Tag '+$scope.schemaformparams.tag);

		//Escape characters 
		var str = $scope.schemaformparams.node;
		var newnodeparam = str.replace(/\//g,'\/');
		console.log('Using parameter '+newnodeparam+"/tags");

		var urlforcallback = 'plsqlcooljson/'+$scope.schemaformparams.schema+'/'
		+ $scope.schemaformparams.dbname.name ;
		HttpTagCb.getTags(
				urlforcallback+newnodeparam+'/tags', 
				function(tags) {
					$scope.tags = tags;
					if (tags == null || (tags.length == 0)) {
						$rootScope.errormessage.text = 'Retrieved empty list of tags';
						$rootScope.errormessage.color = 'red';
						$location.path('/tags');
						return;
					} else {
						$rootScope.errormessage.text = 'Retrieved list of '+tags.length+' tags ';
						$rootScope.errormessage.color = 'black';
					}
					if ($scope.colDefs.length <=0) {
						var skipped = [ '^iov', '^.*Id', '^nodeIsleaf', '^nodeName', '^folder.*tablename', '^nodeTi.*time.*', '^folderVersioning','^dbName'];
						$scope.colDefs = ColumnService.getColumns(tags[0],skipped);
					}
				});				

		//$scope.schemaformparams.gridopt = $scope.tagGridOptions;
		$scope.schemaformparams.list    = $scope.tags;
		$scope.schemaformparams.columns = $scope.colDefs;

		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		form.foldername = $scope.schemaformparams.node;
		form.tagname = $scope.schemaformparams.tag;
		UserFormData.setData(form);
		SchemaData.setTags($scope.schemaformparams);

	};
}])
/**
 * The controller for schemas search.
 */
.controller('TagInfoCtrl', ['$rootScope',
                            '$scope', 
                            '$location', 
                            'Tag', 
                            'HttpJBCool', 
                            'GridOptData',
                            'UserFormData',
                            'CoolService',
                            'SchemaData',
                            'ColumnService',
                            function ($rootScope,$scope, $location, 
                            		Tag, HttpJBCool, 
                            		GridOptData,
                            		UserFormData, 
                            		CoolService,
                            		SchemaData,
                            		ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.tags();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.schemaformparams.node = form.foldername;
	$scope.schemaformparams.tag = form.tagname;
	$scope.colDefs = [];
	$scope.tagSelection = [];
	$scope.tags = [];
	$scope.coverageavailable = false;

	$scope.allschemas = CoolService.getCoolSchemas();
	$scope.showsearch = true;
	
	// Init tabs
	$scope.tabs = [{ title: 'Summary', content: 'Summary per channel' , showtags: true},
	               { title: 'Coverage', content: 'Coverage per channel' , showtagcov: true}
	               ];

	console.log('Initialize the taginfo controller...');

	if ($scope.schemaformparams != undefined) {
		//console.log('Check if chanlist exists :'+$scope.schemaformparams.chanlist);
		if ($scope.schemaformparams.chanlist != undefined) {
			console.log('List of channel summary is '+$scope.schemaformparams.chanlist.length);
		}
	}
	
	// Init variables from the stored SchemaData object.
	if ($scope.schemaformparams.list) {
		//$scope.schemaGridOptions = $scope.schemaformparams.gridopt;
		$scope.tags = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
		$scope.coverageavailable = true;
	} 

	// Changed 2013/12/05 23:42
	$scope.tagGridOptions = GridOptData.getgrid('tagcoverage');

	if ($scope.tagGridOptions == undefined) {

		console.log('Init grid options');
		$scope.tagGridOptions = GridOptData.gridoptions();
		$scope.tagGridOptions.data = 'tags';
		$scope.tagGridOptions.columnDefs = 'colDefs';
		$scope.tagGridOptions.selectedItems = $scope.tagSelection;
		$scope.coverageavailable = false;

		GridOptData.addgrid('tagcoverage', $scope.tagGridOptions);
	}


	$scope.submit = function submit() {
		$scope.colDefs.length = 0;
		console.log('Search tag info using:');
		console.log('Schema pattern '+$scope.schemaformparams.schema);
		console.log('Folder '+$scope.schemaformparams.node);
		console.log('Tag '+$scope.schemaformparams.tag);

		//Escape characters 
		SchemaData.iovperchan();
		var urlforcallback = $scope.schemaformparams.url;

		HttpJBCool.get(urlforcallback).then(function(tags) {
			//first one is the success callback
			$scope.tags = tags;
			if (tags == null || (tags.length == 0)) {
				$rootScope.errormessage.text = 'Retrieved empty list of tags details';
				$rootScope.errormessage.color = 'red';
				$location.path('/taginfo');
				return;
			} else {
				$rootScope.errormessage.text = 'Retrieved list of '+tags.length+' tags details';
				$rootScope.errormessage.color = 'black';
			}
			if ($scope.colDefs.length <=0) {
				var skipped = [ '^tag', '^iov', '^.*Id', '^nodeIsleaf', '^nodeName', '^folder.*tablename', '^nodeTi.*time.*', '^folderVersioning','^dbName'];
				$scope.colDefs = ColumnService.getColumns(tags[0],skipped);
			}			
		},
		function(err) {
			console.log('Received error in retrieving tag coverage information');
			$scope.coverageavailable = false;
			$scope.err = err; 
		}
		); 
		$scope.schemaformparams.gridopt = $scope.tagGridOptions;
		$scope.schemaformparams.list    = $scope.tags;
		$scope.schemaformparams.columns = $scope.colDefs;

		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		form.foldername = $scope.schemaformparams.node;
		form.tagname = $scope.schemaformparams.tag;
		UserFormData.setData(form);
		SchemaData.setTags($scope.schemaformparams);
	};

	$scope.tagIovListSelection = [];
	$scope.iovcolDefs = [];

	// Changed 2013/12/05 23:42
	$scope.tagIovListGridOptions = GridOptData.getgrid('tagiovcoverage');

	if ($scope.tagIovListGridOptions == undefined) {

		console.log('Init grid options');
		$scope.tagIovListGridOptions = GridOptData.gridoptions();
		$scope.tagIovListGridOptions.data = 'iovlist';
		$scope.tagIovListGridOptions.columnDefs = 'iovcolDefs';
		$scope.tagIovListGridOptions.selectedItems = $scope.tagSelection;
		$scope.coverageavailable = false;

		GridOptData.addgrid('tagiovcoverage', $scope.tagIovListGridOptions);
	}

	// Init variables from the stored SchemaData object.
	if ($scope.schemaformparams.list2 != undefined) {
		//$scope.tagIovListGridOptions = $scope.schemaformparams.gridopt2;
		$scope.iovlist = $scope.schemaformparams.list2;
		$scope.iovcolDefs = $scope.schemaformparams.columns2;	
		$scope.coverageavailable = true;
	} else {
		$scope.coverageavailable = false;
	}
	

	$scope.tagcoverage = function tagcoverage() {
		$scope.iovcolDefs.length = 0;
		console.log('Get tag coverage for ... ');
		console.log(' tag pattern is '+$scope.schemaformparams.tag);
		console.log(' node pattern is '+$scope.schemaformparams.node);
		console.log(' schema pattern is '+$scope.schemaformparams.schema);

		SchemaData.coverageurl();

		var urlforcallback = $scope.schemaformparams.url;
		console.log('Using url for coverage '+urlforcallback);

		// building url for coverage: {since}/{until}/{timespan}/rangesummary/list

		HttpJBCool.get(urlforcallback+'0/Inf/time/rangesummary/list').then(function(iovtags) {
			//first one is the success callback
			$scope.iovtags = iovtags;
			$scope.iovlist = [];
			var irow=0;
			$scope.coverageavailable = true;
			for (var i=0; i<$scope.iovtags.length; i++) {
//				angular.forEach(Object.keys($scope.iovtags[i]), function(key){
//					console.log('Found key '+key);
//				});
				console.log('Channel '+$scope.iovtags[i].chanId+' name '+$scope.iovtags[i].channelName);
				var tempiovlist = $scope.iovtags[i].iovlist;
				if (tempiovlist == undefined) {
					return;
				}
				for (var j=0; j<tempiovlist.length;j++) {
					$scope.iovlist[irow] = {};
					//console.log(' iov is hole '+tempiovlist[j].ishole);
					$scope.iovlist[irow].chanId = $scope.iovtags[i].chanId;
					$scope.iovlist[irow].channelName = $scope.iovtags[i].channelName;
					$scope.iovlist[irow].iovbase = $scope.iovtags[i].iovbase;
					$scope.iovlist[irow].since = tempiovlist[j].since;
					$scope.iovlist[irow].until = tempiovlist[j].until;
					$scope.iovlist[irow].niovs = tempiovlist[j].niovs;
					$scope.iovlist[irow].ishole = tempiovlist[j].ishole;
					$scope.iovlist[irow].sinceCoolStr = tempiovlist[j].sinceCoolStr;
					$scope.iovlist[irow].untilCoolStr = tempiovlist[j].untilCoolStr;
					var sinceurl = tempiovlist[j].since / 1000000;
					var untilurl = tempiovlist[j].until / 1000000;
					if ($scope.iovtags[i].iovbase.search('time') >= 0) {
						//console.log('Using time based since and until '+sinceurl+' '+untilurl);
						sinceurl = parseInt(sinceurl);
						untilurl = parseInt(untilurl);
					} else if ($scope.iovtags[i].iovbase.search('^run.*') >= 0) {
						sinceurl = parseInt(tempiovlist[j].sinceCoolStr.split(/-/)[0]);
						if (tempiovlist[j].untilCoolStr.split(/-/)[0].search('^Inf.*') >= 0 ) {
							untilurl = 999999;
						} else {
							untilurl = parseInt(tempiovlist[j].untilCoolStr.split(/-/)[0]);
						}
						console.log('Using run based since and until '+sinceurl+' '+untilurl);
					}
					//$scope.iovlist[irow].sinceUrl = sinceurl;
					//$scope.iovlist[irow].untilUrl = untilurl;
					$scope.iovtags[i].iovlist[j].sinceUrl = sinceurl;
					$scope.iovtags[i].iovlist[j].untilUrl = untilurl;
					irow++;
				}
//				$scope.iovtags[i].iovlist = $scope.iovlist;
			}
			$scope.schemaformparams.chanlist = $scope.iovtags;
			
			if ($scope.iovcolDefs.length <=0) {
				angular.forEach(Object.keys($scope.iovlist[0]), function(key){
					if (key.search("^schema")>=0) {
						console.log("Skip schema");
					} else if (key.search("^db")>=0) {
						console.log("Skip db name");
					} else if (key.search("^node")>=0) {
						console.log("Skip folder name");
					} else if (key.search(".*Url")>=0) {
						console.log("Skip url for since and until");
					} else if (key.search("^tag")>=0) {
						console.log("Skip tag info");
					} else {
						$scope.iovcolDefs.push({ field: key, width : '*' });
					}
				})};
		},
		function(err) {
			$scope.coverageavailable = false;
			$scope.err = err; 
		}
		); 
		//$scope.schemaformparams.gridopt2 = $scope.tagIovListGridOptions;
		$scope.schemaformparams.list2    = $scope.iovlist;
		$scope.schemaformparams.columns2 = $scope.iovcolDefs;
		$scope.schemaformparams.chanlist = $scope.iovtags;
		SchemaData.setTags($scope.schemaformparams);
	};
	
	$scope.textcoverage = function textcoverage() {
		console.log('Show coverage for tag '+form.tagname);
		console.log('Length of chanlist '+$scope.schemaformparams.chanlist );
		$location.path('/textcoverage');
	};
	
	
	$scope.isholerow = function(ishole) {
		//console.log('Received ishole = '+ishole);
		if (ishole) {
			// check if is a bad hole...
			//TODO
			//console.log('This is a hole...');
			return 'ishole';
		} else {
			//console.log('This is an iov...');
			//TODO : check if it is only one iov
			return 'isiovs';
		}
	};
	
}])
/**
 * The controller for IOV search.
 */
.controller('TagIovCtrl', ['$rootScope',
                            '$scope', 
                            '$location', 
                            'Tag', 
                            'HttpJBCool', 
                            'GridOptData',
                            'UserFormData',
                            'IovRangeData',
                            'CoolService',
                            'SchemaData',
                            'ColumnService',
                            function ($rootScope,$scope, $location, 
                            		Tag, HttpJBCool, 
                            		GridOptData,
                            		UserFormData, 
                            		IovRangeData,
                            		CoolService,
                            		SchemaData,
                            		ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.iovs();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.schemaformparams.node = form.foldername;
	$scope.schemaformparams.tag = form.tagname;
	$scope.colDefs = [];
	$scope.iovSelection = [];
	$scope.channels = [];
	$scope.iovs = [];

	$scope.allschemas = CoolService.getCoolSchemas();
	$scope.allnodes = CoolService.getCoolNodes();
	$scope.showsearch = true;

	var timeform = IovRangeData.iovrange();

	$scope.datepicker = {};
	$scope.timepicker = { timesince : new Date(), timeuntil : new Date()};
	$scope.datepicker.datesince = new Date();
	$scope.datepicker.dateuntil = new Date();
	$scope.radioValue = 'time';
	$scope.showsearch = true;
	$scope.showchart = false;
	

    // Init tabs
	$scope.tabs = [{ title: 'Iovs', content: 'List of iovs in range' , showiovs: true}];

	// Init variables
	$scope.tspanoptions = [ 'time','run'/*,'cooltime'*/];
	if (timeform.iovtype == 'time') {
		// replace dates in datepicker
		if (timeform.tmin == undefined) {
			timeform.tmin = $scope.datepicker.datesince;
		}
		if (timeform.tmax == undefined) {
			timeform.tmax = $scope.datepicker.dateuntil;
		}
		
//		$scope.datepicker.datesince = new Date(parseInt(timeform.tmin));
//		$scope.datepicker.dateuntil = new Date(parseInt(timeform.tmax));
	} else {
		$scope.radioValue = 'run';
		timeform.iovtype = 'run';
	}
	$scope.schemaformparams.timeform = timeform;

	$scope.changeradiovalue = function() {
		// TODO : complete this function, should take into account for changes from run -> time
		// in the combo selection.
		if ($scope.radioValue == 'run' && timeform.iovtype == 'time') {
			timeform.tmin = 0;
			timeform.tmax = 999999;
			timeform.iovtype = 'run';
		} else if ($scope.radioValue == 'time' && timeform.iovtype == 'run') {
			timeform.tmin = $scope.datepicker.datesince;
			timeform.tmax = $scope.datepicker.dateuntil;
			timeform.iovtype = 'time';
		}
		$scope.schemaformparams.timeform = timeform;
	}

	console.log('Initialize the tagiov controller...');

	if ($scope.schemaformparams != undefined) {
		//console.log('Check if chanlist exists :'+$scope.schemaformparams.chanlist);
		if ($scope.schemaformparams.chanlist != undefined) {
			console.log('List of channel summary is '+$scope.schemaformparams.chanlist.length);
		}
	}
	
	// Init variables from the stored SchemaData object.
	if ($scope.schemaformparams.list) {
		//$scope.schemaGridOptions = $scope.schemaformparams.gridopt;
		$scope.iovs = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} 

	// Changed 2013/12/05 23:42
	$scope.tagIovGridOptions = GridOptData.getgrid('tagiovs');

	if ($scope.tagIovGridOptions == undefined) {

		console.log('Init grid options');
		$scope.tagIovGridOptions = GridOptData.gridoptions();
		$scope.tagIovGridOptions.data = 'iovs';
		$scope.tagIovGridOptions.columnDefs = 'colDefs';
		$scope.tagIovGridOptions.selectedItems = $scope.iovSelection;

		GridOptData.addgrid('tagiovs', $scope.tagIovGridOptions);
	}
	
	$scope.getchannels = function getchannels() {
		$scope.colDefs.length = 0;
		console.log('Search tag info using:');
		console.log('Schema pattern '+$scope.schemaformparams.schema);
		console.log('Folder '+$scope.schemaformparams.node);
		var urlforcallback = SchemaData.channelsurl(
				$scope.schemaformparams.schema,$scope.schemaformparams.dbname.name,
				$scope.schemaformparams.node);
		
		HttpJBCool.get(urlforcallback.url).then(function(channels) {
			//first one is the success callback
			$scope.channels = channels;
			if (channels == null || (channels.length == 0)) {
				$rootScope.errormessage.text = 'Retrieved empty list of channels';
				$rootScope.errormessage.color = 'red';
				return;
			} else {
				$rootScope.errormessage.text = 'Retrieved list of '+channels.length+' channels';
				$rootScope.errormessage.color = 'black';
				$scope.schemaformparams.chanlist = $scope.channels;
			}		
		},
		function(err) {
			console.log('Received error in retrieving channels list');
			$scope.err = err; 
		}
		); 
	};
	
	$scope.submit = function submit() {
		$scope.colDefs.length = 0;
		console.log('Search tag info using:');
		console.log('Schema pattern '+$scope.schemaformparams.schema);
		console.log('Folder '+$scope.schemaformparams.node);
		console.log('Tag '+$scope.schemaformparams.tag);
		var format = $scope.schemaformparams.timeform.iovtype;
		console.log('Range format is '+format);
		if (format == 'time') {
			$scope.schemaformparams.timeform.tmin = formatdate($scope.datepicker.datesince);
			$scope.schemaformparams.timeform.tmin += formattime($scope.timepicker.timesince);
			$scope.schemaformparams.timeform.tmax = formatdate($scope.datepicker.dateuntil);
			$scope.schemaformparams.timeform.tmax += formattime($scope.timepicker.timeuntil);
			console.log('IOV Range Time '+$scope.schemaformparams.timeform.tmin+' '+$scope.schemaformparams.timeform.tmax);			
		} else {
			console.log('IOV Range '+$scope.schemaformparams.timeform.tmin+' '+$scope.schemaformparams.timeform.tmax);
		}
		//Escape characters 
		var urlforcallback = SchemaData.payloadurl(
				$scope.schemaformparams.schema,$scope.schemaformparams.dbname.name,
				$scope.schemaformparams.node,$scope.schemaformparams.tag,
				$scope.schemaformparams.timeform.tmin,
				$scope.schemaformparams.timeform.tmax,
				$scope.schemaformparams.timeform.iovtype,
				$scope.schemaformparams.channel,
				$scope.schemaformparams.chansel);

		HttpJBCool.get(urlforcallback.url).then(function(iovs) {
			//first one is the success callback
			$scope.iovs = iovs.iovList;
			if (iovs == null || (iovs.iovList.length == 0)) {
				$rootScope.errormessage.text = 'Retrieved empty list of iovs ';
				$rootScope.errormessage.color = 'red';
				return;
			} else {
				$rootScope.errormessage.text = 'Retrieved list of '+iovs.iovList.length+' iovs ';
				$rootScope.errormessage.color = 'black';
			}
			if ($scope.colDefs.length <=0) {
				var skipped = [ '^tag', '^iov', '^.*Id', '^nodeIsleaf', '^nodeName', '^folder.*tablename', '^nodeTi.*time.*', '^folderVersioning','^dbName'];
				$scope.colDefs = ColumnService.getColumns(iovs.iovList[0],skipped);
			}			
		},
		function(err) {
			console.log('Received error in retrieving tag coverage information');
			$scope.err = err; 
		}
		); 
		//$scope.schemaformparams.gridopt = $scope.tagGridOptions;
		$scope.schemaformparams.list    = $scope.iovs.iovList;
		$scope.schemaformparams.columns = $scope.colDefs;

		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		form.foldername = $scope.schemaformparams.node;
		form.tagname = $scope.schemaformparams.tag;
		form.channel = $scope.schemaformparams.selchan;
		UserFormData.setData(form);
		SchemaData.setIovs($scope.schemaformparams);
	};

}]);

function formatdate(adate)
{

            var currentDate = adate;
            var day = (currentDate.getDate()<10 ? "0" : "") + currentDate.getDate();
            var month = (currentDate.getMonth()<9 ? "0" : "") + (currentDate.getMonth()+1);
            var year =  currentDate.getFullYear()
            var formattedDate =  year + month + day;
            return formattedDate;
};

function formattime(adate)
{

            var currentDate = adate;
            var hour = (currentDate.getHours()<10 ? "0" : "") + currentDate.getHours();
            var minute = (currentDate.getMinutes()<10 ? "0" : "") + (currentDate.getMinutes());
            var sec =  (currentDate.getSeconds()<10 ? "0" : "") + (currentDate.getSeconds())
            var formattedDate =  hour + minute + sec;
            return formattedDate;
};

tagControllers.controller('dateCntrl', function($scope,$timeout){
	// Angular-UI code for datepicker and timepicker
	 $scope.dateOptions = {
		 'year-format': "'yyyy'",
		 'starting-day': 1
	 };
		
	  // Disable weekend selection
	  $scope.disabled = function(date, mode) {
	    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
	  };

	  $scope.open = function() {
        $timeout(function() {
            $scope.opened = true;
        });
    };
});