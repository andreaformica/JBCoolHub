/**
 * New node file
 */
var comarunControllers = angular.module('comarunControllers', []);

comarunControllers
/**
 * The controller for schemas search.
 */
.controller('ComaRunCtrl', ['$rootScope',
                            '$scope', 
                            '$routeParams', 
                            '$location', 
                            'ComaRunType', 
                            'ComaRunRangeType', 
                            'GridOptData',
                            'UserFormData',
                            'SchemaData',
                            'ChartData',
                            'ColumnService',
                            function ($rootScope,$scope, $routeParams, $location, 
                            		ComaRunType, 
                            		ComaRunRangeType, 
                            		GridOptData,
                            		UserFormData, 
                            		SchemaData, 
                            		ChartData,
                            		ColumnService) {

	var form = UserFormData.getComarun();

	$scope.datepicker = {};
	$scope.datepicker.datesince = new Date();
	$scope.datepicker.dateuntil = new Date();
	$scope.radioValue = 'time';
	$scope.showsearch = true;
	$scope.showchart = false;
	
	$scope.run = { 
			chartlist : [ { name : 'lumi', title : 'Integrated Luminosity', field : 'integLumi' }, 
			              { name : 'recEvents', title : 'Recorded Events', field : 'recordedEvents'}], 
			              chart : undefined }; 

	// Init tabs
	$scope.tabs = [{ title: 'Runs', content: 'List of runs in range' , showruns: true}];

	// Define plotchart function for highchart
	$scope.plotchart = function() {
		$(function () {
			$('#container').highcharts($scope.chartopt);
		});
	};

	// Init variables
	$scope.tspanoptions = [ 'time','run'/*,'cooltime'*/];
	if (form.runmin.tspan == 'time') {
		// replace dates in datepicker
		$scope.datepicker.datesince = new Date(parseInt(form.runmin.number));
		$scope.datepicker.dateuntil = new Date(parseInt(form.runmax.number));
	} else {
		$scope.radioValue = 'run';
	}

	$scope.changeradiovalue = function() {
		// TODO : complete this function, should take into account for changes from run -> time
		// in the combo selection.
		if ($scope.radioValue == 'run' && form.runmin.tspan == 'time') {
			form.runmin = { number : 0, tspan : 'run'};
			form.runmax = { number : 999999, tspan : 'run'};
			$scope.schemaformparams.runmin = form.runmin;
			$scope.schemaformparams.runmax = form.runmax;
		}	
	}

	$scope.schemaformparams = SchemaData.comaruns();
	$scope.schemaformparams.runmin = form.runmin;
	$scope.schemaformparams.runmax = form.runmax;
	$scope.schemaformparams.rtype  = form.rtype;
	$scope.schemaformparams.period = form.period;
	$scope.colDefs = [];
	$scope.runSelection = [];
	$scope.runs = [];
	$scope.runlistlength = 0;


	console.log('Initialize the ComaRunCtrl controller...');

	// Init variables from the stored SchemaData object.
	if ($scope.schemaformparams.list) {
		$scope.runs = $scope.schemaformparams.list;
		$scope.colDefs = $scope.schemaformparams.columns;	
	} 

	// Changed 2013/12/05 23:42
	$scope.runGridOptions = GridOptData.getgrid('rungrid');

	if ($scope.runGridOptions == undefined) {

		console.log('Init grid options');
		$scope.runGridOptions = GridOptData.gridoptions();
		$scope.runGridOptions.data = 'runs';
		$scope.runGridOptions.columnDefs = 'colDefs';
		$scope.runGridOptions.selectedItems = $scope.runSelection;
		$scope.runGridOptions.afterSelectionChange = function(rowItem, evt) {
			console.log('selection changed to '+rowItem);
			$scope.runs = $scope.schemaformparams.list;
			if ($scope.schemaformparams.columns != undefined) {
				$scope.colDefs = $scope.schemaformparams.columns;	
				console.log('Getting table columns definition from existing object: '
						+$scope.schemaformparams.columns.length);
				for (var i=0; i < $scope.schemaformparams.columns.length; i++){
					for (key in $scope.schemaformparams.columns[i]) {
						console.log('Print col '+key+' '+$scope.schemaformparams.columns[i][key]);
					}
				}
			}
		};
		GridOptData.addgrid('rungrid', $scope.runGridOptions);
	}
	
	// Read route parameters from url
	if ($routeParams.tmin != undefined) {
		$scope.schemaformparams.runmin.number = $routeParams.tmin;
		$scope.schemaformparams.runmax.number = $routeParams.tmax;
		$scope.schemaformparams.runmin.tspan = $routeParams.tspan;
		$scope.schemaformparams.runmax.tspan = $routeParams.tspan;
		$scope.schemaformparams.tspan = $routeParams.tspan;
		console.log('Init variables from url : '
				+$routeParams.tmin+' '+$routeParams.tmax+' '+$routeParams.tspan);
		$scope.radioValue = $routeParams.tspan;
		if ($routeParams.tspan.search('time') >=0) {
			// fill datepicker
			$scope.datepicker.datesince = new Date(parseInt($routeParams.tmin));
			$scope.datepicker.dateuntil = new Date(parseInt($routeParams.tmax));
			console.log('Setting datepicker to '+$scope.datepicker.datesince+' '+$scope.datepicker.dateuntil);
		} else {
			form.runmin = { number : $scope.schemaformparams.runmin.number, tspan : 'run'};
			form.runmax = { number : $scope.schemaformparams.runmax.number, tspan : 'run'};
			form.tspan = $scope.schemaformparams.tspan;
			UserFormData.setComarun(form);
		}

		$location.path('/runinfo');
	}


	$scope.submit = function submit() {

		var args = {};
		var ComaSvs = undefined;
		if ($scope.radioValue == 'run') {
			console.log('Submit: run min is '+$scope.schemaformparams.runmin.number);
			console.log('        run max is '+$scope.schemaformparams.runmax.number);
			console.log('        type is '   +$scope.schemaformparams.rtype);
			console.log('        period is ' +$scope.schemaformparams.period);
			args = {rmin: $scope.schemaformparams.runmin.number, 
					rmax: $scope.schemaformparams.runmax.number,
					type : $scope.schemaformparams.rtype,
					period: $scope.schemaformparams.period};

			ComaSvs = ComaRunType;
		} else if ($scope.radioValue == 'time') {
			$scope.schemaformparams.runmin = { number : $scope.datepicker.datesince, tspan : 'time'};
			$scope.schemaformparams.runmax = { number : $scope.datepicker.dateuntil, tspan : 'time'};
			console.log('Submit: t min is '+$scope.datepicker.datesince);
			console.log('        t max is '+$scope.datepicker.dateuntil);
			console.log('        type is '+$scope.schemaformparams.rtype);
			console.log('        period is '+$scope.schemaformparams.period);

			args =	{tmin: $scope.datepicker.datesince.getTime(), 
					tmax: $scope.datepicker.dateuntil.getTime(),
					type : $scope.schemaformparams.rtype,
					period: $scope.schemaformparams.period}; 
			ComaSvs = ComaRunRangeType;	
		}

		$scope.runs = ComaSvs.list(
				args, 
				function(runs) {
					$scope.runs = runs;
					if (runs == null || (runs.length == 0)) {
						$rootScope.errormessage.text = 'Retrieved empty list of runs';
						$rootScope.errormessage.color = 'red';
						$location.path('/runinfo');
						return;
					} else {
						$rootScope.errormessage.text = 'Retrieved list of '+runs.length+' runs';
						$rootScope.errormessage.color = 'black';
					}
					if ($scope.colDefs.length >0) {
						$scope.colDefs.length = 0;
					}
					if ($scope.colDefs.length <=0) {
						//var skipped = [ '^short.*','^pdesc.*','^partition.*'];
						var skipped = [ '^short.*','.*Events','.*Desc','^pdesc.*'];
						$scope.colDefs = ColumnService.getColumns(runs[0],skipped);
					}

					//$scope.schemaformparams.gridopt = $scope.runGridOptions;
					$scope.schemaformparams.list    = $scope.runs;
					$scope.schemaformparams.columns = $scope.colDefs;

					$scope.runlistlength = $scope.schemaformparams.list.length;

					form.runmin = $scope.schemaformparams.runmin;
					form.runmax = $scope.schemaformparams.runmax;
					form.rtype = $scope.schemaformparams.rtype;
					form.period = $scope.schemaformparams.period;
					UserFormData.setData(form);
					SchemaData.setComaRuns($scope.schemaformparams);
					$scope.runlistwatcher();
				});

	};
	// Chart section
	$scope.chartopt = ChartData.getTimeChartOptions();

	$scope.runlistwatcher = function() {
		console.log('runs list has been modified');
		if ($scope.run.chart != undefined) {
			$scope.chartopt.title.text = $scope.run.chart.title;
			$scope.chartopt.yAxis.title.text = $scope.run.chart.field;
			console.log('Changing title to chart '+$scope.chartopt.title.text);
			//console.log(' chart name is '+$scope.run.chart.name);
			var data = $scope.loadData(data);
		}
	};

//	$scope.$watch('runlistlength', $scope.runlistwatcher(),true); // compare the content, not only the references

	$scope.loadserie = function loadserie() {
		console.log('Prepare serie for chart using a list of length '+$scope.schemaformparams.list.length);
		//var data = [];
		if ($scope.schemaformparams.list != undefined) {
			$scope.chartopt.series = [{ data : [], type : 'scatter', name : 'All'}];
			var ip = 0;
			var iserie = 0;
			var serie = undefined;
			for (var i=0; i < $scope.schemaformparams.list.length; i++) {	
				//console.log('i = '+i+' : '+$scope.runs[i]);
				if ($scope.schemaformparams.list[i] != undefined) {
					var rundate = $scope.schemaformparams.list[i].startTime;
					var intlumi = $scope.schemaformparams.list[i][$scope.run.chart.field];
					//console.log('i = '+i+' : '+$scope.schemaformparams.list[i].startTime+' '+$scope.schemaformparams.list[i].integLumi);
					var period = $scope.schemaformparams.list[i].pperiod;
					var runnum = $scope.schemaformparams.list[i].runNumber;
					var parentperiod = period;
					if (period.search(/^[A-Z]{1}[0-9]+/) >= 0){
						parentperiod = period.substr(0,1);
					}
					$scope.schemaformparams.list[i].parentp = parentperiod;

					//console.log('eval i = '+i+' : '+rundate+' '+intlumi+' '+period);
					if (intlumi == null) 
						intlumi = 0.;
					// Check serie
					if (serie == undefined) {
						serie = $scope.chartopt.series[iserie];
					}
					var serieperiod = parentperiod;
					if (serie.name != parentperiod) {
						var serieforperiod = $scope.chartopt.series.filter(function(el){
							//console.log('Testing el '+el.name);
							return el.name == parentperiod;
						});
						//console.log('serie for period '+parentperiod+' is  defined ? '+serieforperiod.length);
						if (serieforperiod.length == 0) {
							$scope.chartopt.series.push({ data : [], type : 'scatter', name : parentperiod});
							serieforperiod = $scope.chartopt.series.filter(function(el){
								//console.log('Check el '+el.name+' and compare it to '+parentperiod);
								return el.name == parentperiod;
							});
						}
						serie = serieforperiod[0];
					}
					// Create datapoint
					var datapoint = { name : runnum, x : rundate, y:intlumi};
					serie.data.push(datapoint);
				}
			}
			console.log('Using serie of data '+$scope.chartopt.series[0]+' for runs length '+$scope.schemaformparams.list.length);
		}				
	};

	$scope.loadData = function(data) {
		console.log('Loading data for '+$scope.run.chart.field);
		$scope.loadserie();
		$scope.plotchart();
	};

}])
.controller('RootCtrl', ['$rootScope',
                            '$scope', 
                            '$routeParams', 
                            '$location', 
                            function ($rootScope,$scope, $routeParams, $location) {

	$scope.rootfiles = [ { name : '/web/jbcool/trees/hsimple.root'},{ name : '/web/jbcool/trees/Parse_0.9.3.1.root'}];
	$scope.selrootfile = {};
}]);