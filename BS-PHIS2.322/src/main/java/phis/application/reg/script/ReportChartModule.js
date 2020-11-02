$package("phis.application.reg.script")
$import("phis.script.report.Module", "phis.script.report.ReportChart",
		"phis.script.report.Navigation")
phis.application.reg.script.ReportChartModule = function(cfg) {
	phis.application.reg.script.ReportChartModule.superclass.constructor.apply(
			this, [ cfg ])
}
Ext.extend(phis.application.reg.script.ReportChartModule,
		phis.script.report.Module, {
			getPanel : function(arg) {
				var me = this;
				var id = me.zid;
				if (!id) {
					id = Ext.id();
					me.zid = id;
				}
				var beginDate = new Date().format('Y-m-d');
				var endDate = new Date().format('Y-m-d');
				var body = {
					beginDate : beginDate,
					endDate : endDate,
					TJFS : 1
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registrationDoctorPlanService",
					serviceAction : "regCount",
					body : body
				});
				var data = [];
				for ( var i = 0; i < r.json.body.length; i++) {
					var bxmap = {
						"label" : r.json.body[i].KSMC,
						"value" : r.json.body[i].BFB.substring(0,
								r.json.body[i].BFB.indexOf("%"))
					}
					data.push(bxmap);
				}
				var reportChart1cfg = {
					newcfg : {
						// reportWidth:'100%',
						// reportHeight:'100%',
						reportAutoLoad : true,// 是否自动加载数据
						reportType : 'Pie2D',// 图表类型
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
								"caption" : "挂号率",
								"xAxisName" : "挂号率",
								"yAxisName" : "Sales",
								"numberPrefix" : "%"
							},
							"data" : data

						}

					}
				}
				var reportChart1 = new phis.script.report.ReportChart(
						reportChart1cfg);
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
					endDate : this.endDate,
					TJFS : this.tjfs
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registrationDoctorPlanService",
					serviceAction : "regCount",
					body : body
				});
				var category = [];
				var data = [];
				if (this.txfs == 1) {
					txfs = "Pie2D"
				} else if (this.txfs == 2) {
					txfs = "MSColumn2D";
				} else if (this.txfs == 3) {
					txfs = "MSLine";
				} else if (this.txfs == 4) {
					txfs = "Pie3D";
				}
				if (this.txfs == 1||this.txfs == 4) {
					for ( var i = 0; i < r.json.body.length; i++) {
						var bxmap = {
							"label" : r.json.body[i].KSMC,
							"value" : r.json.body[i].BFB.substring(0,
									r.json.body[i].BFB.indexOf("%"))
						}
						data.push(bxmap);
					}
				} else {
					for ( var i = 0; i < r.json.body.length; i++) {
						var labelmap = {
							"label" : r.json.body[i].KSMC
						}
						var datamap = {
							"value" : r.json.body[i].BFB.substring(0,
									r.json.body[i].BFB.indexOf("%"))
						}
						category.push(labelmap);
						data.push(datamap);
					}
				}
				// 图表模版，可配置
				var chartXTemplate = null;
				if (this.txfs == 1||this.txfs == 4) {
					chartXTemplate = {
						"chart" : {
							"caption" : "挂号率",
							"xAxisName" : "挂号率",
							"yAxisName" : "Sales",
							"numberPrefix" : "%"
						},
						"data" : data

					}
				} else {
					chartXTemplate = {
						"chart" : {
							"caption" : "挂号率",
							"yaxisname" : "率(%)",
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
							"seriesname" : "挂号率",
							"color" : "A66EDD",
							"data" : data
						} ]

					}
				}
				this.reportChart1this.renderChart1(chartXTemplate, txfs)
			}
		})
