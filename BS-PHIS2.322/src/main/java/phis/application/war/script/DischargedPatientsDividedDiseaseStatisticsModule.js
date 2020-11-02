$package("phis.application.war.script");
$import("phis.script.SimpleModule")

phis.application.war.script.DischargedPatientsDividedDiseaseStatisticsModule = function(
		cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.exContext = {};
	phis.application.war.script.DischargedPatientsDividedDiseaseStatisticsModule.superclass.constructor
			.apply(this, [cfg])
}
Ext
		.extend(
				phis.application.war.script.DischargedPatientsDividedDiseaseStatisticsModule,
				phis.script.SimpleModule, {
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						this.frameId = "SimplePrint_frame_DischargedPatientsDividedDiseaseStatisticsModule";
						var panel = new Ext.Panel({
							id : "DischargedPatientsDividedDiseaseStatisticsModule",
							width : 1000,
							height : 500,
							title : "",
							tbar : this.initConditionFields(),
							html : "<iframe id='" + this.frameId 
									+ "' width='100%' height='100%' ></iframe>"
						})
						this.panel = panel
						this.panel.on("afterrender", this.onReady, this);
						return panel
					},onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames[this.frameId]
						: document.getElementById(this.frameId);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() {
								_ctx.panel.el.unmask()
										
							});
				} else {
					iframe.onload = function() {
						_ctx.panel.el.unmask()
					};
				}
				},
					doQuery : function(tag) {
						var dateF = this.dateFrom.getValue();
						var dateT = this.dateTo.getValue();
						if (!dateF || !dateT) {
							Ext.MessageBox.alert("提示", "请输入月结日期");
							return
						}
						if (dateF != null && dateT != null && dateF != ""
								&& dateT != "" && dateF > dateT) {
							Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
							return;
						}
						this.panel.el.mask("正在生成报表...", "x-mask-loading");
						var pages = "phis.prints.jrxml.DischargedPatientsDividedDiseaseStatistics";
						var url = "resources/" + pages + ".print?type=1";
						url += "&dateFrom=" + dateF + "&dateTo="
								+ dateT;
						if (tag == 1) {
							var LODOP = getLodop();
							LODOP.PRINT_INIT("打印控件");
							LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
							// 预览LODOP.PREVIEW();
							// 预览LODOP.PRINT();
							// LODOP.PRINT_DESIGN();
							var rehtm = util.rmi.loadXML({
										url : url,
										httpMethod : "get"
									})
							rehtm = rehtm.replace(/table style=\"/g,
									"table style=\"page-break-after:always;")
							rehtm.lastIndexOf("page-break-after:always;");
							rehtm = rehtm
									.substr(
											0,
											rehtm
													.lastIndexOf("page-break-after:always;"))
									+ rehtm
											.substr(rehtm
													.lastIndexOf("page-break-after:always;")
													+ 24);
							LODOP
									.ADD_PRINT_HTM("0", "0", "100%", "100%",
											rehtm);
							LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
									"Full-Width");
							// 预览
							LODOP.PREVIEW();
							this.panel.el.unmask()
						} else {
							document.getElementById(this.frameId).src = url;
						}
					},
					doPrint : function() {
						this.doQuery(1)
					},
					initConditionFields : function() {
						var tbar = new Ext.Toolbar();
						this.dateFrom = new Ext.ux.form.Spinner({
									fieldLabel : '结账日期开始',
									name : 'dateFrom',
									value : new Date().format('Y-m') + "-01",
									strategy : {
										xtype : "date"
									},
									width : 100
								})
						this.dateTo = new Ext.ux.form.Spinner({
									fieldLabel : '结账日期结束',
									name : 'dateTo',
									value : new Date().format('Y-m-d'),
									strategy : {
										xtype : "date"
									},
									width : 100
								})
						var simple = new Ext.FormPanel({
									labelWidth : 50, // label settings here
									// cascade
									title : '',
									layout : "table",
									bodyStyle : 'padding:5px 5px 5px 5px',
									defaults : {},
									defaultType : 'textfield',
									items : [{
												xtype : "label",
												forId : "window",
												text : "统计日期: "
											}, this.dateFrom, {
												xtype : "label",
												forId : "window",
												text : "至"
											}, this.dateTo]

								});
						tbar.add(simple, this.createButtons());
						return tbar;
					}
				})
