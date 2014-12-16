/**
 * New node file
 */
var schemaControllers = angular.module('schemaControllers', []);

schemaControllers
/**
 * The controller for schemas search.
 */
.controller('SchemaCtrl', ['$rootScope',
                           '$scope', 
                           '$location', 
                           'Schema', 
                           'GridOptData',
                           'UserFormData',
                           'SchemaData',
                           'ChartData',
                           'CoolService',
                           'ColumnService',
                           function ($rootScope,$scope, $location, 
                        		   Schema, 
                        		   GridOptData,
                        		   UserFormData, 
                        		   SchemaData,
                        		   ChartData,
                        		   CoolService,
                        		   ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.schemas();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	
	$scope.allschemas = CoolService.getCoolSchemas();
	$scope.showchart = false;
	$scope.showsearch = true;
	
	$scope.colDefs = [];
	$scope.schemaSelection = [];
	$scope.schemas = [];

	$scope.schema = { 
			chartlist : [ { name : 'nfolders', title : 'Number of Folders', field : 'nfolders' } 
			             ], 
			              chart : undefined }; 

	//$scope.nodesurl = '/schema/'+$scope.schemaPattern+'/'+$scope.seldbname.name+'/nodes';

	// Init tabs
	$scope.tabs = [{ title: 'Schemas', content: 'List of schemas' , showschemas: true}
	               ];

	console.log('Initialize the schema controller...');

	// Define plotchart function for highchart
	$scope.plotchart = function() {
		$(function () {
			$('#container').highcharts($scope.chartopt);
		});		
	};
	
	
	// Init variables from the stored SchemaData object.

	if ($scope.schemaformparams.list) {
		//$scope.schemaGridOptions = $scope.schemaformparams.gridopt;
		$scope.schemas = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} 
/*else {
*/

	// Changed 2013/12/05 23:42
	$scope.schemaGridOptions = GridOptData.getgrid('schemagrid');

	if ($scope.schemaGridOptions == undefined) {

		console.log('Init grid options');
		$scope.schemaGridOptions = GridOptData.gridoptions();
		$scope.schemaGridOptions.data = 'schemas';
		$scope.schemaGridOptions.columnDefs = 'colDefs';
		$scope.schemaGridOptions.selectedItems = $scope.schemaSelection;
		$scope.schemaGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('selection changed to '+rowItem);
			if ($scope.schemaSelection.length > 0 && !$scope.usepattern) {
				console.log('Using selection for nodes: '+$scope.usepattern
						+' and selection is '+$scope.schemaSelection[0].schemaName);
			}
		};
		GridOptData.addgrid('schemagrid', $scope.schemaGridOptions);
		/*
		$scope.schemaGridOptions = { 
				data: 'schemas' ,
				columnDefs: 'colDefs',
				showGroupPanel: false,
				enablePaging: false,
				showFooter: true,
				showFilter: true,
				showColumnMenu: true,
				enableColumnResize: true,
				selectedItems: $scope.schemaSelection,
				afterSelectionChange: function(rowItem, evt) {
					console.log('selection changed to '+rowItem);
					if ($scope.schemaSelection.length > 0 && !$scope.usepattern) {
						console.log('Using selection for nodes: '+$scope.usepattern
								+' and selection is '+$scope.schemaSelection[0].schemaName);
					}
				},
				multiSelect: false
		};
		*/
		//$scope.schemaformparams.gridopt = $scope.schemaGridOptions;
	}

	$scope.submit = function submit() {
		console.log('Schema pattern is '+$scope.schemaformparams.schema);
		$scope.schemas = Schema.list({schema: $scope.schemaformparams.schema, 
			dbname: $scope.schemaformparams.dbname.name}, 
				function(schemas) {
			if (schemas == null || (schemas.length == 0)) {
				$rootScope.errormessage.text = 'Retrieved empty list of schemas';
				$rootScope.errormessage.color = 'red';
				$location.path('/schema');
				return;
			} else {
				$rootScope.errormessage.text = 'Retrieved list of '+schemas.length+' schemas ';
				$rootScope.errormessage.color = 'black';
			}
			if ($scope.colDefs.length <=0) {
				if ($scope.colDefs.length <=0) {
					var skipped = [ '^short'];
					$scope.colDefs = ColumnService.getColumns(schemas[0],skipped);
				}
			}
		});

		//$scope.schemaformparams.gridopt = $scope.schemaGridOptions;
		$scope.schemaformparams.list    = $scope.schemas;
		$scope.schemaformparams.columns = $scope.colDefs;

		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		UserFormData.setData(form);
		SchemaData.setSchemas($scope.schemaformparams);
	};

	$scope.toggleUseSelection = function toggleUseSelection() {
		if ($scope.schemaSelection.length > 0 && !$scope.usepattern) {
			console.log('Checkbox was toggled : '+$scope.usepattern+' and selection is '+$scope.schemaSelection);
			$scope.schemaformparams.schema = $scope.schemaSelection[0].schemaName;
		}
		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		UserFormData.setData(form);
		console.log('Checkbox was toggled : '+$scope.usepattern+' and url is '+$scope.schemaformparams.schema);
	};

	$scope.searchnodes = function searchnodes() {
		console.log('Searching nodes ... ');
		UserFormData.setData(form);
		$location.path('/nodes');
	};
	
	// Chart section
	$scope.chartopt = ChartData.getAreaChartOptions();

	$scope.schemalistwatcher = function() {
		console.log('schema list has been modified');
		if ($scope.schema.chart != undefined) {
			$scope.chartopt.title.text = $scope.schema.chart.title;
			$scope.chartopt.yAxis.title.text = $scope.schema.chart.field;
			$scope.chartopt.xAxis.title.text = 'schemas';
			console.log('Changing title to chart '+$scope.chartopt.title.text);
			//console.log(' chart name is '+$scope.run.chart.name);
			var data = $scope.loadData();
		}
	};

	$scope.$watch('schemalistlength', $scope.schemalistwatcher(),true); // compare the content, not only the references

	$scope.loadserie = function loadserie() {
		console.log('Prepare serie for chart using a list of length '+$scope.schemaformparams.list.length);
		var categories = [];
		var data = [];
		if ($scope.schemaformparams.list != undefined) {
			$scope.chartopt.series = [{ data : [], name : 'Number of folders'}];
			var iserie = 0;
			var serie = undefined;
			for (var i=0; i < $scope.schemaformparams.list.length; i++) {	
				//console.log('i = '+i+' : '+$scope.runs[i]);
				if ($scope.schemaformparams.list[i] != undefined) {
					var nfld = $scope.schemaformparams.list[i].nfolders;
					var name = $scope.schemaformparams.list[i].schemaName;
					categories.push(name);
//					var datapoint = { y: nfld, color : "#2f7ed8"};
					data.push(nfld);
				}
			}
			console.log('Using serie of data '+$scope.chartopt.series[0]+' for schema length '+$scope.schemaformparams.list.length);
			$scope.chartopt.series[0].data = data;
			$scope.chartopt.xAxis.categories = categories;
			$scope.plotchart();
		}				
	};

	$scope.loadData = function() {
		console.log('Loading data for '+$scope.schema.chart.field);
		$scope.loadserie();
	};
	
	
}])
/**
 * The controller for schemas details search.
 */
.controller('SchemaDetailCtrl', ['$rootScope',
                                 '$scope', 
                                 '$location', 
                                 'Schema', 
                                 'GridOptData',
                                 'UserFormData',
                                 'SchemaData',
                                 'ColumnService',
                                 function ($rootScope,$scope, $location, 
                                		 Schema, 
                                		 GridOptData,
                                		 UserFormData,
                                		 SchemaData,
                                		 ColumnService) {

	var form = UserFormData.getData();
	$scope.dbnames = UserFormData.getDbs();
	$scope.schemaformparams = SchemaData.nodes();
	$scope.schemaformparams.dbname = form.dbname;
	$scope.schemaformparams.schema = form.schemaname;
	$scope.colDefs = [];
	$scope.nodeSelection = [];
	$scope.nodes = [];

	console.log('Initialize the schemadetail controller...');
	$scope.showsearch = true;

	// Init tabs
	$scope.tabs = [{ title: 'Nodes', content: 'List of nodes' , shownodes: true}
	               ];

	/*
	$scope.nodeGridOptions = { 
			data: 'nodes' ,
			columnDefs: 'colDefs',
			showGroupPanel: false,
			enablePaging: false,
			showFooter: true,
			showFilter: true,
			showColumnMenu: true,
			enableColumnResize: true,
			selectedItems: $scope.nodeSelection,
			afterSelectionChange: function(rowItem, evt) {
				console.log('selection changed to '+rowItem);
				$scope.schemaformparams.node = $scope.nodeSelection[0].nodeFullpath;
				form.foldername = $scope.schemaformparams.node;
				if ($scope.nodeSelection.length > 0 && !$scope.usepattern) {
					console.log('Using selection for tagsurl: '+$scope.usepattern+' and selection is '+$scope.nodeSelection[0].schemaName);
				}
			},
			multiSelect: false
	};
	*/

	// Init variables from the stored SchemaData object.
//	if ($scope.schemaformparams.gridopt != undefined) {
//		$scope.nodeGridOptions = $scope.schemaformparams.gridopt;
//		$scope.nodes = $scope.schemaformparams.list;
//		$scope.colDefs = $scope.schemaformparams.columns;	
		
		// Changed 2013/12/05 23:42
		$scope.nodeGridOptions = GridOptData.getgrid('nodegrid');
		
		if ($scope.nodeGridOptions == undefined) {

			console.log('Init grid options');
			$scope.nodeGridOptions = GridOptData.gridoptions();
			$scope.nodeGridOptions.data = 'nodes';
			$scope.nodeGridOptions.columnDefs = 'colDefs';
			$scope.nodeGridOptions.selectedItems = $scope.nodeSelection;
			$scope.nodeGridOptions.afterSelectionChange = function(rowItem, evt) {
				console.log('selection changed to '+rowItem);
				$scope.schemaformparams.node = $scope.nodeSelection[0].nodeFullpath;
				form.foldername = $scope.schemaformparams.node;
				if ($scope.nodeSelection.length > 0 && !$scope.usepattern) {
					console.log('Using selection for tagsurl: '+$scope.usepattern
							+' and selection is '+$scope.nodeSelection[0].schemaName);
				}
			};
			GridOptData.addgrid('nodegrid', $scope.nodeGridOptions);
	}

	$scope.submit = function submit() {
		$scope.colDefs = [];
		console.log('Search nodes for schema pattern '+$scope.schemaformparams.schema);
		
		$scope.nodes = Schema.nodes({schema: $scope.schemaformparams.schema, dbname: $scope.schemaformparams.dbname.name}, 
				function(nodes) {
			if (nodes == null || (nodes.length == 0)) {
				$rootScope.errormessage.text = 'Retrieved empty list of nodes';
				$rootScope.errormessage.color = 'red';
				$location.path('/nodes');
				return;
			} else {
				$rootScope.errormessage.text = 'Retrieved list of '+nodes.length+' nodes ';
				$rootScope.errormessage.color = 'black';
			}
			if ($scope.colDefs.length <=0) {
				if ($scope.colDefs.length <=0) {
					var skipped = [ '^iov', '^nodeId', '^nodeIsleaf', '^nodeName', '^folder.*tablename', '^nodeTi.*time.*', '^folderVersioning','^dbName'];
					$scope.colDefs = ColumnService.getColumns(nodes[0],skipped);
				}
			}
		});
//		$scope.schemaformparams.gridopt = $scope.nodeGridOptions;
		$scope.schemaformparams.list    = $scope.nodes;
		$scope.schemaformparams.columns = $scope.colDefs;

		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		form.foldername = $scope.schemaformparams.node;
		UserFormData.setData(form);

		SchemaData.setNodes($scope.schemaformparams);
	};
	
	$scope.toggleUseSelection = function toggleUseSelection() {
		if ($scope.nodeSelection.length > 0 && !$scope.usepattern) {
			console.log('Checkbox was toggled : '+$scope.usepattern+' and selection is '+$scope.nodeSelection);
			$scope.schemaformparams.schema = $scope.nodeSelection[0].schemaName;
		}
		form.dbname = $scope.schemaformparams.dbname;
		form.schemaname = $scope.schemaformparams.schema;
		form.foldername = $scope.schemaformparams.node;
		UserFormData.setData(form);

		console.log('Checkbox was toggled : '+$scope.usepattern
				+' and selected schema is '+$scope.schemaformparams.schema
				+' and selected node is '+$scope.schemaformparams.node);
	};

	$scope.searchtags = function searchtags() {
		console.log('Searching tags ... ');
		console.log('Form : '+form.schemaname+","+form.foldername);
		UserFormData.setData(form);
		$location.path('/tags');
	};
	
}]);
