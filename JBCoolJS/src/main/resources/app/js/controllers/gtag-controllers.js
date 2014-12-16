/**
 * New node file
 */
var gtagControllers = angular.module('gtagControllers', []);

gtagControllers
/**
 * The controller for schemas search.
 */
.controller('GtagCtrl', ['$rootScope',
                         '$scope', 
                         '$location', 
                         'Gtag', 
                         'GtagState', 
                         'GridOptData',
                         'UserFormData',
                         'SchemaData',
                         'CoolService',
                         'CoverageService',
                         'ColumnService',
                         function ($rootScope,$scope, $location, 
                        		 Gtag, GtagState,
                                 GridOptData,
                        		 UserFormData, 
                        		 SchemaData,
                        		 CoolService,
                        		 CoverageService,
                        		 ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.gtags();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.schemaformparams.gtag = form.globaltagname;
	$scope.useleaftag = false;
	$scope.colDefs = [];
	$scope.gtagSelection = [];
	$scope.gtags = [];

	$scope.allschemas = CoolService.getCoolSchemas();
	$scope.showgtags = true;
	$scope.showsearch = true;
	$scope.showstates = true;
	
	// Generic variable for gtag states
	$scope.statesDefs = [];
	$scope.stateSelection = [];
	$scope.gtagstates = [];
	// Generic variable for leaf gtag tags
	$scope.leafcolDefs = [];
	$scope.leafgtagSelection = [];
	$scope.leafgtag = [];
	$scope.showleafgtags = true;
	
	// Init tabs
	$scope.tabs = [{ title: 'Current/Next', content: 'List of Current and Next' , showstates: true},
	               { title: 'Global Tags', content: 'List of Global Tags', showgtags: true},
	               { title: 'Leaf Tags', content: 'List of leaf tags', showleafgtags: true}
	               ];
	
	// Changed 2013/12/05 23:42
	$scope.statesGridOptions = GridOptData.getgrid('stategrid');

	if ($scope.statesGridOptions == undefined) {

		console.log('Init state grid options');
		$scope.statesGridOptions = GridOptData.gridoptions();
		$scope.statesGridOptions.data = 'gtagstates';
		$scope.statesGridOptions.columnDefs = 'statesDefs';
		$scope.statesGridOptions.selectedItems = $scope.stateSelection;
		$scope.statesGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('state selection changed to '+rowItem);
			if ($scope.stateSelection[0]) {
			form.globaltagname = $scope.stateSelection[0].tagName;
			$scope.schemaformparams.gtag = form.globaltagname;
			UserFormData.setData(form);
			}
		},	        
		GridOptData.addgrid('stategrid', $scope.statesGridOptions);
	}

	
	$scope.gtagstates = GtagState.list({}, function(states) {
		console.log('Retrieved all states gtags');
		
		if ($scope.statesDefs.length >0) {
			$scope.statesDefs.length = 0;
		}
		if ($scope.statesDefs.length <=0) {
			var skipped = [ '.*Index'];
			$scope.statesDefs = ColumnService.getColumns(states[0],skipped);
		}
		console.log(states);
		console.log($scope.statesDefs);
	});
		
	$scope.$watch('gtagstates', function() {
		console.log('Gtag states changed...');
	},true); // compare the content, not only the references

	$scope.$watch('gtagGridOptions', function() {
		console.log('Gtag grid options changed...');
	}); // compare the references

	console.log('Initialize the gtag controller...');
	if ($scope.schemaformparams.list != undefined) {
		$scope.gtags = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} 
	

	// Init variables from the stored SchemaData object.
	$scope.gtagGridOptions = GridOptData.getgrid('gtaggrid');

	if ($scope.gtagGridOptions == undefined) {

		console.log('Init gtag grid options');
		$scope.gtagGridOptions = GridOptData.gridoptions();
		$scope.gtagGridOptions.data = 'gtags';
		$scope.gtagGridOptions.columnDefs = 'colDefs';
		$scope.gtagGridOptions.selectedItems = $scope.gtagSelection;
		$scope.gtagGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('gtag selection changed to '+rowItem);
			$scope.useleaftag = false;
			if ($scope.gtagSelection.length > 0){
				console.log('Use pattern? '+$scope.usepattern+' ; gtag selection is '+$scope.gtagSelection[0].gtagName+' form is '+form.gtagname);
				if (!$scope.usepattern) {
					form.gtagname = $scope.schemaformparams.gtag;
					form.schemaname = $scope.gtagSelection[0].schemaName;
				}
				if ($scope.gtagSelection[0].nodeFullpath) {
					console.log('Take fields from gtagSelection[0]: '+$scope.gtagSelection[0].nodeFullpath);
					form.schemaname = $scope.gtagSelection[0].schemaName;
					form.foldername = $scope.gtagSelection[0].nodeFullpath;
					form.tagname = $scope.gtagSelection[0].tagName;
					$scope.useleaftag = true;
				}
				console.log('schema is '+form.schemaname+' leaftag '+$scope.useleaftag);
				UserFormData.setData(form);
			}
			
		};        
		GridOptData.addgrid('gtaggrid', $scope.gtagGridOptions);
	} else {
		$scope.gtagSelection = $scope.gtagGridOptions.selectedItems;
		if ($scope.gtagSelection && $scope.gtagSelection[0].tagName){
			form.schemaname = $scope.gtagSelection[0].schemaName;
			form.foldername = $scope.gtagSelection[0].nodeFullpath;
			form.tagname = $scope.gtagSelection[0].tagName;
			$scope.useleaftag = true;
		}		
	}

	
	$scope.submit = function submit() {
		
		console.log('Submit: global tag pattern is '+$scope.schemaformparams.gtag);
		console.log('Submit: schema pattern is '+$scope.schemaformparams.schema);
		$scope.colDefs.length = 0;
		$scope.gtagSelection.length = 0;
		$scope.useleaftag = false;
		
		$scope.gtags = Gtag.list(
			   {schema: $scope.schemaformparams.schema, 
				dbname: $scope.schemaformparams.dbname.name, 
				gtag: $scope.schemaformparams.gtag}, 
				function(gtags) {
					if (gtags == null || (gtags.length == 0)) {
						$rootScope.errormessage.text = 'Retrieved empty list of global tags';
						$rootScope.errormessage.color = 'red';
						$location.path('/globaltag');
						return;
					} else {
						$rootScope.errormessage.text = 'Retrieved list of '+gtags.length+' global tags ';
						$rootScope.errormessage.color = 'black';
					}
			if ($scope.colDefs.length >0) {
				$scope.colDefs.length = 0;
			}
			if ($scope.colDefs.length <=0) {
				var skipped = [ '^short'];
				$scope.colDefs = ColumnService.getColumns(gtags[0],skipped);
			}
			console.log(gtags);
			console.log($scope.colDefs);
			
//			$scope.schemaformparams.gridopt = $scope.gtagGridOptions;
			$scope.schemaformparams.list    = $scope.gtags;
			$scope.schemaformparams.columns = $scope.colDefs;

			form.dbname = $scope.schemaformparams.dbname;
			form.schemaname = $scope.schemaformparams.schema;
			form.globaltagname = $scope.schemaformparams.gtag;
			UserFormData.setData(form);
			SchemaData.setGtags($scope.schemaformparams);

		});
	};
	
	$scope.getsummary = function getsummary() {
		
		var globaltagSelected = $scope.gtagGridOptions.selectedItems[0].gtagName;
		console.log('getsummary: global tag is '+globaltagSelected);
		console.log('getsummary: schema pattern is '+$scope.schemaformparams.schema);
		UserFormData.setData(form);
		SchemaData.setGtags($scope.schemaformparams);
		
		$scope.summarytree = Gtag.summary(
			   {schema: $scope.schemaformparams.schema, 
				dbname: $scope.schemaformparams.dbname.name, 
				gtag: globaltagSelected}, 
				function(summarytree) {
					console.log('Get summarytree : '+summarytree);
					if (summarytree == null || (summarytree.length == 0)) {
						$rootScope.errormessage.text = 'Retrieved empty list of coverage summary';
						$rootScope.errormessage.color = 'red';
						$location.path('/globaltag');
						return;
					} else {
						$rootScope.errormessage.text = 'Retrieved list of '+summarytree.length+' coverage summary ';
						$rootScope.errormessage.color = 'black';
						console.log('Store tree in memory');
						CoverageService.setTree(summarytree);
						form.dbname = $scope.schemaformparams.dbname;
						form.schemaname = $scope.schemaformparams.schema;
						form.globaltagname = globaltagSelected;
						UserFormData.setData(form);
						$location.path('/gtagcoverage');
					}
		});
		
	};

	$scope.toggleUseSelection = function toggleUseSelection() {
		if ($scope.gtagSelection.length > 0 && !$scope.usepattern) {
			console.log('Checkbox was toggled : '+$scope.usepattern+' and selection is '+$scope.gtagSelection);
			$scope.schemaformparams.gtag = $scope.gtagSelection[0].gtagName;
			if ($scope.gtagSelection[0].schemaName){
				$scope.schemaformparams.schema = $scope.gtagSelection[0].schemaName;
			}
		}
		form.dbname = $scope.schemaformparams.dbname;
		form.globaltagname = $scope.schemaformparams.gtag;
		form.schemaname = $scope.schemaformparams.schema;
		UserFormData.setData(form);

		console.log('Checkbox was toggled : '+$scope.usepattern
				+' and selected schema is '+$scope.schemaformparams.schema
				+' and selected global tag is '+$scope.schemaformparams.gtag);
	};

	// Init variables from the stored SchemaData object.
	console.log('Initialize the leaf gtag tag variable...');
	if ($scope.schemaformparams.list2 != undefined) {
		$scope.leafgtags = $scope.schemaformparams.list2;
		$scope.leafcolDefs = $scope.schemaformparams.columns2;	
	} 

	$scope.leafgtagGridOptions = GridOptData.getgrid('leafgtaggrid');

	if ($scope.leafgtagGridOptions == undefined) {

		console.log('Init leaf tag gtag grid options');
		$scope.leafgtagGridOptions = GridOptData.gridoptions();
		$scope.leafgtagGridOptions.data = 'leafgtags';
		$scope.leafgtagGridOptions.columnDefs = 'leafcolDefs';
		$scope.leafgtagGridOptions.selectedItems = $scope.leafgtagSelection;
		$scope.leafgtagGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('gtag selection changed to '+rowItem);
			$scope.useleaftag = false;
			if ($scope.leafgtagSelection.length > 0){
				console.log('Use pattern? '+$scope.usepattern+' ; gtag selection is '+$scope.leafgtagSelection[0].gtagName+' form is '+form.gtagname);
				if (!$scope.usepattern) {
					form.gtagname = $scope.schemaformparams.gtag;
					form.schemaname = $scope.leafgtagSelection[0].schemaName;
				}
				if ($scope.leafgtagSelection[0].nodeFullpath) {
					console.log('Take fields from gtagSelection[0]: '+$scope.leafgtagSelection[0].nodeFullpath);
					form.schemaname = $scope.leafgtagSelection[0].schemaName;
					form.foldername = $scope.leafgtagSelection[0].nodeFullpath;
					form.tagname = $scope.leafgtagSelection[0].tagName;
					$scope.useleaftag = true;
				}
				console.log('schema is '+form.schemaname+' leaftag '+$scope.useleaftag);
				UserFormData.setData(form);
			}
			
		};        
		GridOptData.addgrid('leafgtaggrid', $scope.leafgtagGridOptions);
	} else {
		$scope.leafgtagSelection = $scope.leafgtagGridOptions.selectedItems;
		if ($scope.leafgtagSelection && $scope.leafgtagSelection[0].tagName){
			form.schemaname = $scope.gtagSelection[0].schemaName;
			form.foldername = $scope.gtagSelection[0].nodeFullpath;
			form.tagname = $scope.leafgtagSelection[0].tagName;
			$scope.useleaftag = true;
		}		
	}

	$scope.tracetags = function tracetags() {
		console.log('Tracing tags ... ');
		console.log(' global tag pattern is '+$scope.schemaformparams.gtag);
		console.log(' schema pattern is '+$scope.schemaformparams.schema);

		$scope.leafgtags = Gtag.listtags(  {schema: $scope.schemaformparams.schema, 
			dbname: $scope.schemaformparams.dbname.name, 
			gtag: $scope.schemaformparams.gtag}, 
			function(leafgtags) {
				if (leafgtags == null || (leafgtags.length == 0)) {
					$rootScope.errormessage.text = 'Retrieved empty list of leaf tags';
					$rootScope.errormessage.color = 'red';
					$location.path('/tracetags');
					return;
				} else {
					$rootScope.errormessage.text = 'Retrieved list of '+leafgtags.length+' leaf tags ';
					$rootScope.errormessage.color = 'black';
				}
			if ($scope.leafcolDefs.length >0) {
				$scope.leafcolDefs.length = 0;
			}
			if ($scope.leafcolDefs.length <=0) {
				var skipped = [ '^.*Id$', '^nodeName', '^dbName'];
				$scope.leafcolDefs = ColumnService.getColumns(leafgtags[0],skipped);
			}
			//$scope.schemaformparams.gridopt = $scope.gtagGridOptions;
			$scope.schemaformparams.list2    = $scope.leafgtags;
			$scope.schemaformparams.columns2 = $scope.colDefs;

			form.dbname = $scope.schemaformparams.dbname;
			form.schemaname = $scope.schemaformparams.schema;
			form.globaltagname = $scope.schemaformparams.gtag;
			$scope.useleaftag = false;
			UserFormData.setData(form);
			SchemaData.setGtags($scope.schemaformparams);
		});
		//$location.path('/tracetags');
	};
	
}])
/**
 * The controller for schemas search.
 */
.controller('GtagCoverageCtrl', ['$rootScope',
                         '$scope', 
                         '$location', 
                         'Gtag', 
                         'GtagState', 
                         'Schema', 
                         'UserFormData',
                         'SchemaData',
                         'CoverageService',
                         'ColumnService',
                         'CoolService',
                         function ($rootScope,$scope, $location, 
                        		 Gtag, GtagState,
                        		 Schema,
                        		 UserFormData, 
                        		 SchemaData,
                        		 CoverageService,
                        		 ColumnService,
                        		 CoolService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.gtags();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.schemaformparams.gtag = form.globaltagname;
	$scope.loadeddata = false;
	$scope.loadingmessage = 'data are being loaded...wait then click on chart button';

	$scope.schemas = CoolService.getCoolSchemas();
	$scope.folders = CoolService.getCoolNodes();
	
	$scope.coverage = GtagState.coverage({ 
		dbname: $scope.schemaformparams.dbname.name, 
		state: $scope.schemaformparams.gtag}, 
		function(coverage) {
			console.log('Found coverage '+coverage.globalTagName);
			if (coverage == undefined) {
				$scope.coverage = { globalTagName : $scope.schemaformparams.gtag, covComment : 'This tag was not analysed', nUpdatedFolders : 0, nUpdatedSchemas : 0};
			}
		}			
	);
	
	if ($scope.schemas == undefined || $scope.schemas.length < 1 || form.schemaname != 'ATLAS_COOL') {
	$scope.schemas = Schema.list(
			{schema: $scope.schemaformparams.schema, 
			 dbname: $scope.schemaformparams.dbname.name
			}, 
			function(schemas) {
				console.log('Retrieved list of n '+schemas.length+' schemas ');
				$scope.loadedschemas = true;
				$scope.loadeddata = ($scope.loadedfolders && $scope.loadedschemas);
			}
	);
	} else {
		$scope.loadedschemas = true;
		$scope.loadeddata = ($scope.loadedfolders && $scope.loadedschemas);
		if ($scope.loadeddata) {
			$scope.loadingmessage = 'data are loaded';
		}
	}
	if ($scope.folders == undefined || $scope.folders.length < 1 || form.schemaname != 'ATLAS_COOL') {
	$scope.folders = Schema.nodes(
			{schema: $scope.schemaformparams.schema, 
			 dbname: $scope.schemaformparams.dbname.name
			}, 
			function(folders) {
				console.log('Retrieved list of n '+folders.length+' nodes ');
				$scope.loadedfolders = true;
				$scope.loadeddata = ($scope.loadedfolders && $scope.loadedschemas);
				if ($scope.loadeddata) {
					$scope.loadingmessage = 'data are loaded';
				}

			}
	);
	} else {
		$scope.loadedfolders = true;
		$scope.loadeddata = ($scope.loadedfolders && $scope.loadedschemas);		
		if ($scope.loadeddata) {
			$scope.loadingmessage = 'data are loaded';
		}
	}

	$scope.setTagInfo = function() {
		UserFormData.setData(form);
		$location.path('/taginfo');
	}
	
	// Define plotchart function for d3js
	$scope.plotchart = function() {
		$scope.loadeddata = ($scope.loadedfolders && $scope.loadedschemas);
		if (!$scope.loadeddata) {
			$scope.loadingmessage = 'data are not yet loaded, retry...';
			return;
		} else {
			$scope.loadingmessage = 'data are loaded';
			CoverageService.setSchemas($scope.schemas);
			CoverageService.setFolders($scope.folders);	
			$scope.summarytree = CoverageService.buildCoolTree();	
			console.log('Received data ');console.log($scope.summarytree);
		}
		$(function () {
			var screenwidth = 1440;
			var screenheight = 900;
			var m = [20, 120, 20, 120],
			    w = screenwidth - m[1] - m[3],
			    h = screenheight - m[0] - m[2],
			    i = 0,
			    root;

			var tree = d3.layout.tree()
			    .size([h, w]);

			var diagonal = d3.svg.diagonal()
			    .projection(function(d) { return [d.y, d.x]; });

			var vis = d3.select("#body").append("svg:svg")
			    .attr("width", w + m[1] + m[3])
			    .attr("height", h + m[0] + m[2])
			  .append("svg:g")
			    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

			var url = $scope.summarytree;
			console.log('using url '+url);

			var infodiv = d3.select("body").append("div")   
			    .attr("class", "tooltip")               
			    .style("opacity", 0);

			var tooltipwidth = 300;

			//d3.json('http://'+url, function(json) {
			d3.json('http://localhost:8080', function(json) {
			  root = $scope.summarytree;
			  root.x0 = h / 2;
			  root.y0 = 0;

			  function toggleAll(d) {
			    if (d.children) {
			      d.children.forEach(toggleAll);
			      toggle(d);
			    }
			  }

			  // Initialize the display to show a few nodes.
			  root.children.forEach(toggleAll);
			  update(root);
			});

			function update(source) {

			  var duration = d3.event && d3.event.altKey ? 5000 : 500;

			  // Compute the new tree layout.
			  var nodes = tree.nodes(root).reverse();

			  // Normalize for fixed-depth.
			  nodes.forEach(function(d) { d.y = (d.depth * 180); });

			  // Update the nodes
			  var node = vis.selectAll("g.node")
			      .data(nodes, function(d) { return d.id || (d.id = ++i); });
			  
			  // Enter any new nodes at the parent's previous position.
			  var formatTime = d3.time.format("%e %B");    

			  var maxlabelLength = 200;

			  var nodeEnter = node.enter().append("svg:g")
			      .attr("class", "node")
			      .attr("transform", function(d) { 
			      		var translation = "translate(" + source.y0 + "," + source.x0 + ")";
			      		console.log('Use translation '+translation);
			      		return translation; 
			      		})
			      .on("click", function(d) { toggle(d); update(d); });


			  nodeEnter.append("svg:circle")
			      .attr("r", 1e-6)
			      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; })
				  .on("mouseover", function(d) {      
			            infodiv.transition()        
			                .duration(200)      
			                .style("opacity", .9);      
			            infodiv.html(d.name + ' <br/> ' + getInfo(d,'folderInfo'))
			             .style("left", (d3.event.pageX + 10) + "px")     
			             .style("top", (d3.event.pageY - 100) + "px");      
			            })
				  .on("mouseout", function(d) {       
			            infodiv.transition()        
			                .duration(500)      
			                .style("opacity", 0);   
			       });

			  // List the nodes and add the url
//			  var nodeLink = nodeEnter.append("a")
//			      .attr("href",function(d){ return "http://cern.ch";});

			  var nodeText = nodeEnter.append("svg:text")
			      .attr("x", function(d) { 
			      					var pos0 = d.children || d._children ? -10 : 10; 
			      					if (d.name.lastIndexOf('/', 0) === 0) {
			      						pos0 = 10;
			      					}
			      					return pos0;
			      			 })
			      .attr("dy", ".35em")
			      .attr("text-anchor", function(d) { 
			      			var ancorpos = d.children || d._children ? "end" : "start"; 
			      			if (d.name.lastIndexOf('/', 0) === 0) {
			      						ancorpos = "start";
			      			}
			      			return ancorpos;
			      		});

			  nodeText.text(function(d) { return d.name + ((d.nfolders) ? (' - ' + d.nfolders) : ''); })
				.style("fill", function(d) { return d.color; })
			  	.style("fill-opacity", 1e-6); 
			  	 
			  nodeText.append("svg:tspan").style("fill", function(d) { return getColor(d,"value"); }).text(function(d) { return getInfo(d,"value"); });
			  nodeText.on("click",function(d){ 
				  if (d.value == undefined) {
					  console.log('Undefined value');
					  return;
				  }
				  console.log('The text '+d.name+' has been clicked...value is '+d.value);
				  if (d.name == 'taginfo') {
					  var url = d.value.split('/');
					  console.log('Got url '+url[0]+' '+url[1]+' '+url[2]+' '+url[3]+' '+url[4]+' '+url[5]);
					  form.schemaname = url[2];
					  var fld = url[4].replace(/\_\_/g,'\/');
					  form.foldername = fld;
					  form.tagname = url[5];
					  $scope.setTagInfo();
				  }
			  });
			  
			  // Transition nodes to their new position.
			  var nodeUpdate = node.transition()
			      .duration(duration)
			      .attr("transform", function(d) { 
			  	    				if (d.depth>1) {
			 	     					d.y = d.y + maxlabelLength*(d.depth-2);
			 	     				}
			      				var translation = "translate(" + d.y + "," + d.x + ")";
			      				return translation; 
			      		});

			  nodeUpdate.select("circle")
			      .attr("r", 4.5)
			      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

			  nodeUpdate.select("text")
			      .style("fill-opacity", 1);

			  // Transition exiting nodes to the parent's new position.
			  var nodeExit = node.exit().transition()
			      .duration(duration)
			      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
			      .remove();

			  nodeExit.select("circle")
			      .attr("r", 1e-6);

			  nodeExit.select("text")
			      .style("fill-opacity", 1e-6);

			  // Update the links
			  var link = vis.selectAll("path.link")
			      .data(tree.links(nodes), function(d) { return d.target.id; });

			  // Enter any new links at the parent's previous position.
			  link.enter().insert("svg:path", "g")
			      .attr("class", "link")
			      .attr("d", function(d) {
			        var o = {x: source.x0, y: source.y0};
			        return diagonal({source: o, target: o});
			      })
			    .transition()
			      .duration(duration)
			      .attr("d", diagonal);

			  // Transition links to their new position.
			  link.transition()
			      .duration(duration)
			      .attr("d", diagonal);

			  // Transition exiting nodes to the parent's new position.
			  link.exit().transition()
			      .duration(duration)
			      .attr("d", function(d) {
			        var o = {x: source.x, y: source.y};
			        return diagonal({source: o, target: o});
			      })
			      .remove();

			  // Stash the old positions for transition.
			  nodes.forEach(function(d) {
			    d.x0 = d.x;
			    d.y0 = d.y;
			  });
			}


			// create output text
			function getInfo(d,field) {
				if (typeof d.totalIovs === "undefined" && typeof(d.content) === "undefined" ) {
					if ( typeof(d.value) === "undefined" ) {
						return "";
					} else {
						if (d.name === 'taginfo') {
							return ' -> Search in TagInfo';
						}
						return ' - '+d.value;
					}
				} else {
					var outinfo = '';
					if (field == "channels") {
						outinfo = ' NChan: '+d.nchannels;
					} else if (field == "iovs") {
						outinfo = ' Iovs: '+d.totalIovs;
					} else if (field == "holes") {
						outinfo = ' Holes: '+d.totalHoles;
					} else if (field == "nruns") {
						outinfo = ' Nruns: '+d.totalRuns;
					} else if (field == "runsinholes") {
						outinfo = ' Runs In holes: '+d.totalRunsInHole;
					} else if (field == "minrun") {
						outinfo = ' MinRun: '+d.minRun;
					} else if (field == "maxrun") {
						outinfo = ' MaxRun: '+d.maxRun;
					} else if (field == "minsince") {
						outinfo = ' From: '+d.minSince;
					} else if (field == "maxuntil") {
						outinfo = ' To: '+d.maxUntil;
					} else if (field == "folderInfo") {
//					    tooltipwidth = ('Description : '+d.content.nodeIovBase).length;
//					    tooltipwidth = Math.max(tooltipwidth,('Payload Spec: '+d.content.folderPayloadSpec).length);
//					    console.log('tooltip width will be of '+tooltipwidth);
					    outinfo = 'Description : '+d.content.nodeIovBase+'<br/>'
					    	    + 'Payload Spec: '+d.content.folderPayloadSpec+'<br/>'
					    	    + 'Versioning  : '+d.content.folderVersioning+'<br/>'
					     ;
					}
					return outinfo;
				}
			}

			function getIovs(d) {
			    console.log('check iovs on children '+d.length);
				for (var i=0 ; i<d.length; i++) {
					if (d[i].name == "totalIovs") {
						return d.value;
					}
				}
				return -1;
			}

			function getHoles(d) {
			    console.log('check holes on children '+d.length);
				for (var i=0 ; i<d.length; i++) {
					if (d[i].name == "totalHoles") {
						return d.value;
					}
				}
				return -1;
			}

			function getColor(d,field) {
			  if (field === "value") {
			     field = d.name;
			  }
			  if (field == "taginfo") {
				  return "gray";
			  }
			  if (field === "checkholes") {
			  	 var children = d.children;
			  	 var color = "black";
				 console.log("check holes for "+d.name+" children found "+children);
			  	 if (typeof(d.children) != "undefined" && children != null) {
					console.log("check children for "+d.name);
			  	 	var iovs = getIovs(children);
			  	 	console.log('Found iovs '+iovs);
			  	 	if (iovs == 1) color = "blue";
			  	 	if (iovs < 0) color = "orange";
			  	 	var holes = getHoles(children);
			  	 	console.log('Found holes '+holes);
			  	 	if (holes > 0) color = "red";
			  	 	if (holes == 0) color = "green";
			  	 }
			  	 return color;
			  }

			  if (field == "nchannels") {
			  	return "gray";
			  } else if (field == "totalIovs") {
			    if (d.value == 1) {
			    	return "blue";
			    }
			  	return "black";
			  } else if (field == "totalHoles") {
			  	return "red";
			  } else if (field == "totalRuns") {
			  	return "darkgreen";
			  } else if (field == "totalRunsInHoles") {
			  	return "orange";
			  } else if (field == "minRun") {
			  	return "darkgreen";
			  } else if (field == "maxRun") {
			  	return "darkgreen";
			  } else {
			  	return "black";
			  }
			}

			function getColorOld(d,field) {
			  if (field === "value") {
			     field = d.name;
			  }

			  if (field === "checkholes") {
			  	 
			  }

			  if (field == "channels") {
			  	return "gray";
			  } else if (field == "iovs") {
			    if (d.totalIovs == 1) {
			    	return "blue";
			    }
			  	return "black";
			  } else if (field == "holes") {
			  	return "red";
			  } else if (field == "nruns") {
			  	return "darkgreen";
			  } else if (field == "runsinholes") {
			  	return "orange";
			  } else if (field == "minrun") {
			  	return "darkgreen";
			  } else if (field == "maxrun") {
			  	return "darkgreen";
			  } else {
			  	return "black";
			  }
			}

			// Toggle children.
			function toggle(d) {
			  if (d.children) {
			    d._children = d.children;
			    d.children = null;
			  } else {
			    d.children = d._children;
			    d._children = null;
			  }
			}

			function explodeAll(d) {
			    if (d.children) {
			      d.children.forEach(explodeAll);
			      toggle(d);
			    }
			}
		});
	};

}]);
