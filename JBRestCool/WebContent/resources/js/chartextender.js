function chartExtender() {
    //this = chart widget instance

    this.cfg.axes = {
        xaxis : {
            renderer : $.jqplot.DateAxisRenderer, 
            rendererOptions : {
                tickRenderer:$.jqplot.CanvasAxisTickRenderer
            },
            tickOptions : { 
                fontSize:'10pt',
                fontFamily:'Tahoma', 
                formatString:'%b %d %H:%M',
                angle:-40
            }
        },
        yaxis : {
            rendererOptions : {
                tickRenderer:$.jqplot.CanvasAxisTickRenderer
            },
            tickOptions: {
                fontSize:'10pt', 
                fontFamily:'Tahoma', 
                angle:30
            }
        }               
    };
    
    this.cfg.axes.xaxis.ticks = this.cfg.categories;
}
