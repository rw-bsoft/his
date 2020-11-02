$package("phis.script.report")
$import("phis.script.report.Module")
phis.script.report.ReportChart = function(cfg) {
	if(!window.FusionCharts_old)
		window.FusionCharts_old=window.FusionCharts;
	if(window.FusionCharts_new)
		{window.FusionCharts=window.FusionCharts_new;}
	else{
		window.FusionCharts=undefined;
		$import("phis.script.report.FusionCharts");	
		window.FusionCharts_new=window.FusionCharts;
	}
	window.FusionCharts_new=window.FusionCharts;
	this.chartPath='resources/phis/chart/';
	phis.script.report.ReportChart.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.script.report.ReportChart,phis.script.report.Module,{
	getPanel:function(arg)
	{
		var me=this;
		var newcfg=me.newcfg;
		var id=newcfg.reportId||Ext.id();
		me.zid=id;
		var cfg={
				id:id,
				border : false,
				layout:'fit',
				height:'100%',
				width:'100%',
				//bodyStyle:'heigth:100%'
				html:'<div id="chart_'+id+'" style="height:100%;width:100%"></div>'
				
		};
		if(newcfg.reportAutoLoad)
		{
			cfg.listeners={
				afterrender:function()
				{me.loadData();}
			}	
		}
		Ext.apply(cfg,newcfg);
		var panel=new Ext.Panel(cfg);
		//console.log(panel);
		return panel;
	
	},
	loadData:function(datas)
	{
		var me=this;
		var id=me.zid;
		var newcfg=me.newcfg;
		var beforefn=newcfg.beforeQuery;
		var afterfn=newcfg.afterQuery;
		var datas=datas;
		var reportAjaxType=newcfg.reportAjaxType||'Sync';
		var chartXTemplate=newcfg.chartXTemplate;
		if(beforefn){beforefn.apply(this,[]);}
		if(chartXTemplate.data||chartXTemplate.dataset)
		{
			datas={};
			me.renderChart(datas);
			if(afterfn){afterfn.apply(this,[]);}
		}else
		{
			if(datas)
			{
				Ext.apply(datas,chartXTemplate);
				me.renderChart(datas);
				if(afterfn){afterfn.apply(this,[]);}
			}else
			{
				datas={};
			if(!newcfg.reportParam)
				{newcfg.reportParam={};
				}		
			newcfg.reportParam.chartId=id;
			
			if(newcfg.reportServiceId&&newcfg.reportMethod){
				var ajaxcfg={
						serviceId : newcfg.reportServiceId,
						method : newcfg.reportMethod,
						body:newcfg.reportParam||{}
					};
				var callback=function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					datas=json.body;
					me.renderChart(datas);
					if(afterfn){afterfn.apply(this,[]);}
				}
			var r =eval('phis.script.rmi.miniJsonRequest'+reportAjaxType+'(ajaxcfg,callback,this)');
			 if(reportAjaxType=='Sync')
				 {
				    datas=r.json.body;
					me.renderChart(datas);
					if(afterfn){afterfn.apply(this,[]);}
				 }
		}
			}
		}
		
//		datas.data=[{ "label": "Alex","value": "25000","link":'JavaScript:chartClickEvent({chartId:"'+me.zid+'",value:11})'},
//	             { "label": "Mark","value": "35000"},
//	             { "label": "David","value": "42300"},
//	             { "label": "Graham","value": "35300"},
//	             {"label": "John","value": "31300"}
//	             ]
	},
	renderChart:function(datas)
	{
		var me=this;
		var id=me.zid;
		var newcfg=me.newcfg;
		var chart=me.chart;
		Ext.apply(datas,{unescapeLinks:'0'});
		Ext.apply(datas,newcfg.chartXTemplate);
		if(!chart){
		window.FusionCharts=window.FusionCharts_new;
		chart = new FusionCharts(me.chartPath+newcfg.reportType+".swf", "chartId"+id,(!newcfg.reportWidth?'100%':newcfg.reportWidth),(!newcfg.reportHeight?'100%':newcfg.reportHeight), "0", "1");  
		window.FusionCharts=window.FusionCharts_old;
		}
		chart.setJSONData(datas);
		chart.render('chart_'+id);
	},
	renderChart1:function(datas,reportType)
	{
		var me=this;
		var id=me.zid;
		var newcfg=me.newcfg;
		var chart=me.chart;
		if(!chart){
		window.FusionCharts=window.FusionCharts_new;
		chart = new FusionCharts(me.chartPath+reportType+".swf", "chartId"+id,(!newcfg.reportWidth?'100%':newcfg.reportWidth),(!newcfg.reportHeight?'100%':newcfg.reportHeight), "0", "1");  
		window.FusionCharts=window.FusionCharts_old;
		}
		chart.setJSONData(datas);
		chart.render('chart_'+id);
	}
})
