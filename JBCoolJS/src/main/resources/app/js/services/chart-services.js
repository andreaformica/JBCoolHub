/**
 * New node file
 */

var chartServices = angular.module('chartServices', [ 'ngResource' ]);

chartServices
/*
 * This service handles HighChart options for creating
 * various types of charts.
 */
.value('timechartopt',
	{
        chart: {
//        	 type: 'scatter',
             zoomType: 'x',
             spacingRight: 20
        },
        title: {
            text: 'Default Title'
        },
        subtitle: {
            text: document.ontouchstart === undefined ?
                'Click and drag in the plot area to zoom in' :
                'Pinch the chart to zoom in'
        },
        xAxis: {
            type: 'datetime',
            maxZoom: 30 * 24 * 3600000, // 30 days
            title: {
                text: 'time'
            }
        },
        yAxis: {
            title: {
                text: 'y value'
            }
        },
        tooltip: {
//            shared: true
            formatter: function() {
                return '<b>'+ this.series.name +'</b><br/>'+
                Highcharts.dateFormat('%e. %b', this.x) +': '+ this.point.name + ' - '+this.y +' !';
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            area: {
                fillColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                    stops: [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                    ]
                },
                lineWidth: 1,
                marker: {
                    enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: null
            }/*,
            scatter: {
                marker: {
                    radius: 5,
                    states: {
                        hover: {
                            enabled: true,
                            lineColor: 'rgb(100,100,100)'
                        }
                    }
                },
                states: {
                    hover: {
                        marker: {
                            enabled: false
                        }
                    }
                },
                tooltip: {
                    headerFormat: '<b>{series.name}</b><br>',
                    pointFormat: '{point.x} run, {point.y}'
                }
            }      */      
        },
        series: []
	}
)
.value('barchartopt',
		{
            chart: {
                type: 'column'
            },
            /*
            colors: [
                     '#2f7ed8', 
                     '#0d233a', 
                     '#8bbc21', 
                     '#910000', 
                     '#1aadce', 
                     '#492970',
                     '#f28f43', 
                     '#77a1e5', 
                     '#c42525', 
                     '#a6c96a'
                  ],*/
            //colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
            title: {
                text: 'Column Chart'
            },
            subtitle: {
                text: 'Atlas conditions'
            },
            xAxis: {
                categories: ['DefaultCategory'], //['Africa', 'America', 'Asia', 'Europe', 'Oceania'],
                title: {
                    text: 'X axis name'
                },
                labels: {
                    rotation: -90,
                    align: 'right',
                    style: {
                        fontSize: '11px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }                
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Y axis name',
                    align: 'low'
                }
            },
            tooltip: {
            	 headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                 pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                     '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
                 footerFormat: '</table>',
                 shared: true,
                 useHTML: true
            },
            plotOptions: {
//                bar: {
//                   dataLabels: {
//                        enabled: true
//                    },
//                    color: '#FF0000'
//                },
                column: {
//                	colorbypoint: true,
               	    fillOpacity: 0.9,
                    pointPadding: 0.,
                    borderWidth: 1.,
                    dataLabels: {
                      enabled: true
                    }
                    //color: '#FA0000'
                },
                series: {
//                	shadow: true,
//                    color: '#FF0000'
                }                
            },
            series: []
            /*
             * {
                name: 'Year 1800',
                data: [107, 31, 635, 203, 2]
            }, {
                name: 'Year 1900',
                data: [133, 156, 947, 408, 6]
            }, {
                name: 'Year 2008',
                data: [973, 914, 4054, 732, 34]
            }
             * 
             */
    }
)
.value('areachartopt',
		{
			chart: {
                type: 'area',
                spacingBottom: 30
            },
            title: {
                text: 'Default title'
            },
            subtitle: {
                text: 'a subtitle',
                floating: true,
                align: 'right',
                verticalAlign: 'bottom',
                y: 15
            },
            legend: {
                layout: 'vertical',
                align: 'left',
                verticalAlign: 'top',
                x: 150,
                y: 100,
                floating: true,
                borderWidth: 1,
                backgroundColor: '#FFFFFF'
            },
            xAxis: {
                categories: ['Apples', 'Pears', 'Oranges', 'Bananas', 'Grapes', 'Plums', 'Strawberries', 'Raspberries'],
            title: {
                text: 'X-Axis'
            },
            labels: {
                rotation: -90,
                align: 'right',
//                align: 'high',
                style: {
                    fontSize: '11px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
            },
            yAxis: {
                title: {
                    text: 'Y-Axis'
                },
                labels: {
                    formatter: function() {
                        return this.value;
                    }
                }
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    this.x +': '+ this.y;
                }
            },
            credits: {
                enabled: false
            },
            plotOptions: {
              area: {
             	  fillOpacity: 0.6,
                  pointPadding: 0.,
                  borderWidth: 1.,
                  dataLabels: {
                    enabled: true
                  }
              },
              series: {
              }                
          },
          series: []
		}
)
.factory('ChartData', ['timechartopt', 'barchartopt', 
                       'areachartopt', 
                       function(timechartopt,barchartopt,areachartopt) {

	
	return {
		getTimeChartOptions: function() {
			console.log('ChartData: Retrieve options for time chart...');
			var options = timechartopt;
			return options;
		},
		getBarChartOptions: function() {
			console.log('ChartData: Retrieve options for barchart...');
			var options = barchartopt;
			return options;
		},
		getAreaChartOptions: function() {
			console.log('ChartData: Retrieve options for areachart...');
			return areachartopt;
		}		
	};
}]);
