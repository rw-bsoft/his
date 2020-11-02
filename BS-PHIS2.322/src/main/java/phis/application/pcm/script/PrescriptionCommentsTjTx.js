$package("phis.application.pcm.script")
$import("phis.script.report.Module", "phis.script.report.ReportChart",
		"phis.script.report.Navigation")
phis.application.pcm.script.PrescriptionCommentsTjTx= function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsTjTx.superclass.constructor.apply(this,
			[ cfg ])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsTjTx, phis.script.report.Module, {
	getPanel : function(arg) {
		var category = [];
		var data = [];
		var labelmap = {
			"label" : ""
		}
		var valuemap = {
			"value" : ""
		}
		category.push(labelmap);
		data.push(valuemap);
		var reportChart1cfg = {
			newcfg : {
				// reportWidth:'100%',
				// reportHeight:'100%',
				reportAutoLoad : true,// 是否自动加载数据
				reportType : 'MSColumn3D',// 图表类型
				reportParam : {
					h : 111,
					arr : [ 1, 1, 1, 1 ]
				},
				// 点击事件
				reportClickEvent : function(obj) {
					// console.log(obj);
					// reportChart.newcfg.chartXTemplate.caption=obj.label;
					// reportChart.loadData();
				},
				// 查询前事件
				beforeQuery : function() {
				},
				// 完成查询事件
				afterQuery : function() {
				},
				// 图表模版，可配置
				chartXTemplate : {
					"chart" : {
						"caption" : "处方点评统计表",
						"yaxisname" : "不合理数",
						"bgcolor" : "ffffff",
						"showBorder" : "0",
						"numvdivlines" : "10",
						"divlinealpha" : "30",
						"labelpadding" : "10",
						"yaxisvaluespadding" : "10",
						"showvalues" : "1",
						"rotatevalues" : "1",
						"valueposition" : "auto",
						"baseFontSize" : "11"

					},
					"categories" : [ {
						"category" : category
					} ],
					"dataset" : [ {
						"seriesname" : "科室",
						"data" : data
					} ]

				}
			}
		}
		var reportChart1 = new phis.script.report.ReportChart(reportChart1cfg);
		this.reportChart1this = reportChart1;
		var rchart1 = reportChart1.initPanel();
		var cardPanel = new Ext.Panel({
			border : false,
			frame : false,
			layout : 'card',
			flex : 1,
			activeItem : 0,
			items : [ rchart1 ]
		});
		var panel = new Ext.Panel({
			border : false,
			frame : false,
			// html:'这是一个基本Module',
			layout : 'vbox',
			// height:'100%',
			layoutConfig : {
				align : 'stretch'
			},
			items : [ cardPanel ],
			listeners : {
				afterrender : function() {

				}
			}
		});
		return panel;
	},
	loadDataf : function() {
		var category = [];
		var data = [];
		var colors=["CD0000","00CD00","0000CD","CDCD00","8DEEEE","CD00CD","8B0000","006400","000080","8B5A00","66CDAA","9400D3"];
		var j=0;
		var length=this.data.length;
		for(var i=0;i<length;i++){
			if(j>i){
			j=0;}
			var d=this.data.get(i).data;
			if(d.KSDM==0){
			continue;}
			category.push({"label":d.KSDM_text});
			data.push({"value" : d.BHLSL,"color" : colors[j]});
			j++;
		}
		// 图表模版，可配置
		var chartXTemplate = {
			"chart" : {
				"caption" : "处方点评统计表",
				"yaxisname" : "不合理数",
				"bgcolor" : "ffffff",
				"showBorder" : "0",
				"numvdivlines" : "10",
				"divlinealpha" : "30",
				"labelpadding" : "10",
				"yaxisvaluespadding" : "10",
				"showvalues" : "1",
				"rotatevalues" : "1",
				"valueposition" : "auto",
				"baseFontSize" : "10",
				"formatNumber" : "0",
				"formatNumberScale" : 0
			},
			"categories" : [ {
				"category" : category
			} ],
			"dataset" : [ {
				"seriesname" : "科室",
				"data" : data
			} ]
		}
		this.reportChart1this.renderChart1(chartXTemplate, "MSColumn3D")
	}
})
