/**
 * New node file
 */

var gridServices = angular.module('gridServices', [ 'ngResource' ]);

gridServices
/*
 * 
 */
.factory('GridOptData', function() {
	
	// GridOptions class
	function GridOptions() {
	  this.data = undefined;
	  this.columnDefs = undefined;
	  this.showGroupPanel = false;
	  this.enablePaging = false;
	  this.showFooter = true;
	  this.showFilter = true;
	  this.showColumnMenu = true;
	  this.enableColumnResize = true;
	  this.selectedItems = undefined;
	  this.afterSelectionChange = function(rowItem, evt) {
		  console.log('selection changed to '+rowItem);
	  };
	  this.multiSelect = false;
	};

	var gridmap = {};

	return {
		gridoptions: function() {
			console.log('Grid Options: create a new object...');
			var options = new GridOptions();
			return options;
		},
		addgrid: function(gridname, griddata) {
			console.log('Adding grid '+gridname+' with options '); console.log(griddata);
			gridmap[gridname] = griddata;
		},
		getgrid: function(gridname) {
			console.log('Searching grid '+gridname);
			if (gridname in gridmap) {
				console.log(' -- > grid name found');
				var options = gridmap[gridname];
				console.log(' -- > options is '); console.log(options);
				return options;
			}
		}
	};
})
.factory('IovRangeData', function() {
	
	// Shape - superclass
	function IovRange() {
	  this.tmin = undefined;
	  this.tmax = undefined;
	  this.iovtype = 'time';
	};

	var iovrangemap = {};

	return {
		iovrange: function() {
			console.log('IovRange: create a new object...');
			var irange = new IovRange();
			return irange;
		},
		add: function(rangename, irange) {
			console.log('Adding iovrange '+rangename+' as '); console.log(irange);
			iovrangemap[rangename] = irange;
		},
		get: function(rangename) {
			console.log('Searching range '+rangename);
			if (rangename in iovrangemap) {
				console.log(' -- > grid name found');
				var irange = iovrangemap[rangename];
				console.log(' -- > range is '); console.log(irange);
				return irange;
			}
		}
	};
});

