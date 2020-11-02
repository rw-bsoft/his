$package("app.modules.chart")
$import("app.desktop.Module", "util.rmi.miniJsonRequestSync")
app.modules.chart.QTChartView = function(cfg) {
	app.modules.chart.QTChartView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(app.modules.chart.QTChartView, app.desktop.Module, {
			initPanel : function() {
				// if (this.panel) {
				// return this.panel
				// }
				var store = this.getStore();
				var yaxis;
				if (this.autoHigh) {
					yaxis = new Ext.chart.NumericAxis({
								labelRenderer : Ext.util.Format
										.numberRenderer('0,0')
							})
				} else {
					yaxis = new Ext.chart.NumericAxis({
								labelRenderer : Ext.util.Format
										.numberRenderer('0,0'),
								minimum : 0,
								maximum : 6
							})
				}
				var panel = new Ext.Panel({
							frame : true,
							iconCls : "chart",
							width : 600,
							height : 280,
							layout : 'fit',
							items : [{
										xtype : 'columnchart',
										store : store,
										xField : 'manaUnitId_text',
										url : 'app/modules/chart/charts.swf',
										yAxis : yaxis,
										extraStyle : { // 横坐标文字旋转
											xAxis : {
												labelRotation : -45
											}
										},
										series : [{
													displayName : '健康档案',
													yField : 'healthRecord',
													style : {
														mode : 'stretch',
														color : 0x6EAFFF
													}
												}],
										chartStyle : {
											padding : 5, // 填充。
											animationEnabled : true,
											legend : { // 柱形说明文字。
												display : 'left',
												padding : 5,
												font : {
													hold : true
												}
											},
											font : { // X轴Y轴字体设置。
												name : 'Tahoma',
												color : 0x000000,
												size : 11
											},
											dataTip : { // 提示框显示。
												padding : 5,
												border : { // 边框设置。
													color : 0x99bbe8,
													size : 1
												},
												background : {
													color : 0xDAE7F6,
													alpha : .7
													// 透明度。
												},
												font : { // 字体设置。
													name : 'Tahoma',
													color : 0x15428B,
													size : 15,
													hold : true
												}
											}
										}

									}]
						})
				this.panel = panel
				return panel;
			},

			getStore : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.statService",
							serviceAction : "getRecordNum",
							method:"execute",
							body : {
								deptId : "3301"
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var body = result.json.body;
				this.autoHigh = result.json.autoHigh;
				var store = new Ext.data.JsonStore({
							fields : ['manaUnitId_text', 'healthRecord'],
							data : body
						});
				return store;
			}

		})