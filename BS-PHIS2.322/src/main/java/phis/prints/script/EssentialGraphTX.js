$package("phis.prints.script")
$import("phis.script.report.Module", "phis.script.report.ReportChart",
		"phis.script.report.Navigation")
phis.prints.script.EssentialGraphTX = function(cfg) {
	phis.prints.script.EssentialGraphTX.superclass.constructor.apply(this,
			[ cfg ])
}
Ext.extend(phis.prints.script.EssentialGraphTX, phis.script.report.Module, {
	getPanel : function(arg) {
		var me = this;
		var id = me.zid;
		if (!id) {
			id = Ext.id();
			me.zid = id;
		}
		var category = [];
		var data = [];
		var beginDate = "2014-01-01";
		var endDate = "2014-05-22";

		var labelmap = {
			"label" : ""
		}
		var valuemap = {
			"value" : "0"
		}
		category.push(labelmap);
		data.push(valuemap);
		var reportChart1cfg = {
			newcfg : {
				// reportWidth:'100%',
				// reportHeight:'100%',
				reportAutoLoad : false,// 是否自动加载数据
				reportType : 'MSColumn3D',// 图表类型
				reportServiceId : 'phis.reportService',
				reportMethod : 'doLoadDatas',
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
						"caption" : "国家基本药物统计",
						"yaxisname" : "数量",
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
						"seriesname" : "出库和购入统计",
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
		var body = {
			beginDate : this.beginDate,
			endDate : this.endDate
		};
		var r = phis.script.rmi.miniJsonRequestSync({
			serviceId : "essentialGraphService",
			serviceAction : "queryEssentialGraph",
			body : body
		});
		var category = [];
		var data = [];
		var jbjhjejemap = {
			"label" : "出库基本药物进额"
		}
		category.push(jbjhjejemap);
		var jbjemap = {
			"label" : "出库基本药物零额"
		}
		category.push(jbjemap);
		var jbzlmap = {
			"label" : "出库基本药物数"
		}
		category.push(jbzlmap);
		var jhjemap = {
			"label" : "出库进额"
		}
		category.push(jhjemap);
		var zjemap = {
			"label" : "出库零额"
		}
		category.push(zjemap);
		var zzlemap = {
			"label" : "出库药品数"
		}
		category.push(zzlemap);

		var rjhhjbjemap = {
			"label" : "购入基本药物进额"
		}
		category.push(rjhhjbjemap);
		var rjbjemap = {
			"label" : "购入基本药物零额"
		}
		category.push(rjbjemap);
		var rjbzlmap = {
			"label" : "购入基本药物数"
		}
		category.push(rjbzlmap);
		var rzjhhjjemap = {
			"label" : "购入进额"
		}
		category.push(rzjhhjjemap);
		var lsjemap = {
			"label" : "零售金额"
		}
		category.push(lsjemap);
		var rzzlmap = {
			"label" : "购入药品数"
		}
		category.push(rzzlmap);

		var JBJHJEJEvaluemap = {
			"value" : r.json.body.JBJHJEJE,
			"color" : "CD0000"
		}
		data.push(JBJHJEJEvaluemap);
		var JHJEvaluemap = {
			"value" : r.json.body.JHJE,
			"color" : "00CD00"

		}
		data.push(JHJEvaluemap);
		var JBZLvaluemap = {
			"value" : r.json.body.JBZL,
			"color" : "0000CD"
		}
		data.push(JBZLvaluemap);
		var ZJEvaluemap = {
			"value" : r.json.body.ZJE,
			"color" : "CDCD00"
		}
		data.push(ZJEvaluemap);
		var JBJEvaluemap = {
			"value" : r.json.body.JBJE,
			"color" : "8DEEEE"
		}
		data.push(JBJEvaluemap);
		var zzlvaluemap = {
			"value" : r.json.body.ZZL,
			"color" : "CD00CD"
		}
		data.push(zzlvaluemap);

		var rzjhhjjevaluemap = {
			"value" : r.json.body.RZJHHJJE,
			"color" : "8B0000"
		}
		data.push(rzjhhjjevaluemap);
		var rjbzlvaluemap = {
			"value" : r.json.body.RJHHJBJE,
			"color" : "006400"
		}
		data.push(rjbzlvaluemap);
		var rjbzlvaluemap = {
			"value" : r.json.body.RJBZL,
			"color" : "000080"
		}
		data.push(rjbzlvaluemap);
		var lsjevaluemap = {
			"value" : r.json.body.LSJE,
			"color" : "8B5A00"
		}
		data.push(lsjevaluemap);
		var rjbjevaluemap = {
			"value" : r.json.body.RJBJE,
			"color" : "66CDAA"
		}
		data.push(rjbjevaluemap);
		var rzzlvaluemap = {
			"value" : r.json.body.RZZL,
			"color" : "9400D3"
		}
		data.push(rzzlvaluemap);
		// 图表模版，可配置
		var chartXTemplate = {
			"chart" : {
				"caption" : "国家基本药物统计",
				"yaxisname" : "数量",
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
				"seriesname" : "出库和购入统计",
				"data" : data
			} ]
		}
		this.reportChart1this.renderChart1(chartXTemplate, "MSColumn3D")
	}
})
