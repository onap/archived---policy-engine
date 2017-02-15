 
function drawSingleSeriesChart(tabId, chartId, chartWidth, chartHeight, chartType, chartData,mme,yyyyMo) {
    // define, set the data for each chart, and render them (if indicated)
    var myChart = new FusionCharts("static/fusion/inc/fusionchart/" + chartType, "myChartId" + chartId, chartWidth, chartHeight);    

    var mmeLabel = "";//mme + "";   
    
    var chartCaptionId = tabId + "-chartdiv" + chartId + "-label";
    
    document.getElementById(chartCaptionId).innerHTML =  "Hosted Voice Usage in "+ yyyyMo; 
    
    myChart.setDataXML("<graph caption='"+mmeLabel + "' xAxisName='' yAxisName='# of Inbound/Outbound calls' showNames='1' decimalPrecision='0' formatNumberScale='0'>" + chartData + "</graph>");
    myChart.setTransparent(true);    
    myChart.render(tabId + "-"+ "chartdiv" + chartId);
    
    
  }

  
  // gauge charts
  function drawGaugeChart(chartId, gaugeIndex, value) {	 
	var   myChart = new FusionCharts("static/fusion/inc/fusionchart/AngularGauge.swf", "myChartId" + chartId, "300", "300", "0", "0");
	//CPU for NYCMNYBWLT1 on 201301 : 12.63//
	var chartData =  
    "<Chart showBorder='0' editMode='1' bgColor='FFFFFF' upperLimit='100' lowerLimit='0' label='CPU' baseFontColor='FFFFFF' majorTMNumber='11' majorTMColor='FFFFFF'  majorTMHeight='8' minorTMNumber='5' minorTMColor='FFFFFF' minorTMHeight='3' toolTipBorderColor='FFFFFF' toolTipBgColor='333333' gaugeOuterRadius='100' gaugeOriginX='150' gaugeOriginY='150' gaugeScaleAngle='270' placeValuesInside='1' gaugeInnerRadius='80%25' annRenderDelay='0' gaugeFillMix='' pivotRadius='10' showPivotBorder='0' pivotFillMix='{CCCCCC},{333333}' pivotFillRatio='50,50' showShadow='0' >" +
		"<colorRange>" +	
		"<color minValue='0' maxValue='50'   code='C1E1C1' alpha='60'/>" + 	
		"<color minValue='50' maxValue='85'  code='F6F164' alpha='60'/>" +
		"<color minValue='85' maxValue='120' code='F70118' alpha='60'/>" +
	"</colorRange>" + 
	"<dials>" + 	
		"<dial value='" + value + "' borderColor='FFFFFF' bgColor='000000,CCCCCC,000000' borderAlpha='0' baseWidth='10'/>" +
	"</dials>" +	
	"<annotations>" +
		"<annotationGroup xPos='150' yPos='150' showBelow='1'>" +		
			"<annotation type='circle' xPos='0' yPos='0' radius='120' startAngle='0' endAngle='360' fillColor='CCCCCC,111111'  fillPattern='linear' fillAlpha='100,100'  fillRatio='50,50' fillAngle='-45'/>" +
			"<annotation type='circle' xPos='0' yPos='0' radius='110' startAngle='0' endAngle='360' fillColor='111111,cccccc'  fillPattern='linear' fillAlpha='100,100'  fillRatio='50,50' fillAngle='-45'/>" +
			"<annotation type='text' label='CPU'/>" +
		"</annotationGroup>" +
	"</annotations>" +
   "</Chart>";
    
	myChart.setDataXML(chartData);
	myChart.render("chartdiv" + chartId);
  }


  function updateGaugeChart(chartId, gaugeIndex, refreshCount) {	 
    var myChart = getChartFromId("myChartId" + chartId);
    var val = 0;
    if (gaugeIndex == "10") val = 12;
    else if (gaugeIndex == "20") val = 15;
    else if (gaugeIndex == "30") val = 52;
    else if (gaugeIndex == "40") val = 42;
    myChart.setData(1, val);
    myChart.setData(2, val+20);
  } 
  
  function updateGaugeChartWithMMEData(chartId, value) {	  
	  var myChart = getChartFromId("myChartId" + chartId);
	  myChart.setData(1, value);
  }
  
 
  function updateGaugeChartWithMMEData(chartId, value, yyyyMo, mme) {	  
	  var myChart = getChartFromId("myChartId" + chartId);
	  myChart.setData(1, value);	  
	  document.getElementById("cpuYyyyMm").innerHTML =  "Hosted Voice Utilization in "+ yyyyMo; 
	  
  } 
  
  function drawMultiSeriesChart(tabId,chartId, chartWidth, chartHeight, chartType) {
    var     myChart = new FusionCharts("static/fusion/inc/fusionchart/" + chartType, "myChartId" + chartId, chartWidth, chartHeight, "0", "0");

	myChart.setTransparent(true);	

  	var chartData =  
	  "<graph caption='Hosted Voice v Audited Communication' xAxisName='Month' yAxisName='Bandwidth Usage'" +
		  " showValues='0' decimalPrecision='0' bgcolor='ffffff' bgAlpha='70'" +
		  " showColumnShadow='1' divlinecolor='c5c5c5' divLineAlpha='60' showAlternateHGridColor='1'" +
		  " alternateHGridColor='f8f8f8' alternateHGridAlpha='60' >" +
		  "<categories>" +
		    "<category name='Jan' />" +
		    "<category name='Feb' />" +
		    "<category name='Mar' />" +
		    "<category name='Apr' />" +
		    "<category name='May' />" +
		    "<category name='Jun' />" +
		    "<category name='Jul' />" +
		    "<category name='Aug' />" +
		    "<category name='Sep' />" +
		    "<category name='Oct' />" +
		    "<category name='Nov' />" +
		    "<category name='Dec' />" +
		  "</categories>" +
		  "<dataset seriesName='Hosted Voice' color='c4e3f7' >" +
		    "<set value='7' />" +
		    "<set value='8.04' />" +
		    "<set value='10.04' />" +
		    "<set value='12.73' />" +
		    "<set value='12.41' />" +
		    "<set value='11.83' />" +
		    "<set value='14.06' />" +
		    "<set value='15.94' />" +
		    "<set value='22.97' />" +
		    "<set value='26.79' />" +
		    "<set value='20.35' />" +
		    "<set value='12.63' />" +
		  "</dataset>" +
		  "<dataset seriesName='Audited Communication' color='Fad35e' >" +
		    "<set value='5.49'/>" +
		    "<set value='8.05'/>" +
		    "<set value='15.36'/>" +
		    "<set value='21.23'/>" +
		    "<set value='23.48' />" +
		    "<set value='15.49' />" +
		    "<set value='26.8' />" +
		    "<set value='30.54' />" +
		    "<set value='64' />" +
		    "<set value='75' />" +
		    "<set value='63' />" +
		    "<set value='34' />" +
		  "</dataset>" +
		  "<trendlines>" +
		    "<line startValue='26000' color='91C728' displayValue='Target' showOnTop='1'/>" +
		  "</trendlines>" +
		"</graph>";	  
	    
	myChart.setDataXML(chartData);
	myChart.render(tabId + "-" + "chartdiv" + chartId);
  }
 
