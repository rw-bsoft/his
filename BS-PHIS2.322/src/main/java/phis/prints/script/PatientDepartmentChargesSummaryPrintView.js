$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.PatientDepartmentChargesSummaryPrintView = function(cfg) {
	// this.width = 800
	// this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	phis.prints.script.PatientDepartmentChargesSummaryPrintView.superclass.constructor
			.apply(this, [cfg])
	Ext.apply(this, phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.PatientDepartmentChargesSummaryPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_patientdepartmentchargessummary";
				this.conditionFormId = "SimplePrint_form_patientdepartmentchargessummary";
				this.mainFormId = "SimplePrint_mainform_patientdepartmentchargessummary";
				this.frameId1 = "SimplePrint_frame_summaryofincome";
				this.frameId2 = "SimplePrint_frame_SummarycategoryHospital";
				this.frameId3 = "SimplePrint_frame_SummarycategoryHospital3";
				this.frameId4 = "SimplePrint_frame_SummarycategoryHospital4";
				this.tab = new Ext.TabPanel({
					title : " ",
					border : false,
					activeTab : 0,
					frame : true,
					// height : 477,
					resizeTabs : this.resizeTabs,
					tabPosition : this.tabPosition || "top",
					autoHeight : true,
					defaults : {
						border : false,
						autoHeight : true,
						autoWidth : true
					},
					items : [{
						id : 'hz1',
						title : '汇总日报(一)',
						html : "<iframe id='"
								+ this.frameId
								+ "' width='100%' height='94%' scrolling='yes' onload='simplePrintMask(\"patientdepartmentchargessummary\")'></iframe>"
					}, {
						id : 'hz2',
						title : '汇总日报(二)',
						html : "<iframe id='"
								+ this.frameId1
								+ "' width='100%' height='100%' onload='simplePrintMask(\"patientdepartmentchargessummary\")'></iframe>"
					}, {
						id : 'hz3',
						title : '汇总日报(三)',
						html : "<iframe id='"
								+ this.frameId2
								+ "' width='100%' height='100%' onload='simplePrintMask(\"patientdepartmentchargessummary\")'></iframe>"
					}, {
						id : 'hz4',
						title : '汇总日报(四)',
						html : "<iframe id='"
								+ this.frameId3
								+ "' width='100%' height='100%' onload='simplePrintMask(\"patientdepartmentchargessummary\")'></iframe>"
					}, {
						id : 'hz5',
						title : '汇总日报(五)',
						html : "<iframe id='"
								+ this.frameId4
								+ "' width='100%' height='100%' onload='simplePrintMask(\"patientdepartmentchargessummary\")'></iframe>"
					}]
				})
				this.tab.on("tabchange", this.onTabChange, this);
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						items : this.initConditionFields()
					},
					items : this.tab
				})
				this.panel = panel
				panel.on("afterrender", this.onAfterrender, this)
				return panel
			},
			onTabChange : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				if (document.getElementById(this.frameId)) {
					document.getElementById(this.frameId).height = this.panel
							.getHeight()
							- 70;
				}
				if (document.getElementById(this.frameId1)) {
					document.getElementById(this.frameId1).height = this.panel
							.getHeight()
							- 70;
				}
				if (document.getElementById(this.frameId2)) {
					document.getElementById(this.frameId2).height = this.panel
							.getHeight()
							- 70;
				}
				if (document.getElementById(this.frameId3)) {
					document.getElementById(this.frameId3).height = this.panel
							.getHeight()
							- 70;
				}
				if (document.getElementById(this.frameId4)) {
					document.getElementById(this.frameId4).height = this.panel
							.getHeight()
							- 70;
				}
				var pages = "phis.prints.jrxml.Summaryofincome";
				var url = "resources/" + pages + ".print?preview=1";
				url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
						+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
				if (document.getElementById(this.frameId1)) {
					document.getElementById(this.frameId1).src = url
				}
				var pages = "phis.prints.jrxml.SummarycategoryHospital";
				var url1 = "resources/" + pages + ".print?preview=1";
				url1 += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
						+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
				if (document.getElementById(this.frameId2)) {
					document.getElementById(this.frameId2).src = url1
				}
				var pages = "phis.prints.jrxml.SummarycategoryHospitalFor";
				var url2 = "resources/" + pages + ".print?preview=1";
				url2 += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
						+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
				if (document.getElementById(this.frameId3)) {
					document.getElementById(this.frameId3).src = url2
				}

				var pages = "phis.prints.jrxml.SummarycategoryHospital1";
				var url3 = "resources/" + pages + ".print?preview=1";
				url3 += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
						+ "&endDate=" + encodeURI(encodeURI(this.hzDate))
						+ "&hz=5";
				if (document.getElementById(this.frameId4)) {
					document.getElementById(this.frameId4).src = url3
				}
			},
			initConditionFields : function() {
				var items = []
				items.push(new Ext.form.Label({
					text : "从"
				}));
				items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d'
				}))
				items.push(new Ext.form.Label({
					text : "到"
				}));
				items.push(new Ext.form.DateField({
					name : 'endDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d'
				}))
				items.push({
					xtype : "button",
					text : "查询(Q)",
					iconCls : "query",
					scope : this,
					handler : this.doInquiry
				})
				items.push(new Ext.form.Label({
					text : "汇总时间："
				}));
				items.push(new Ext.form.DateField({
					name : 'summaryDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d'
				}))
				items.push({
					xtype : "button",
					text : "产生(A)",
					iconCls : "default",
					scope : this,
					handler : this.doLoadReport
				})
				items.push({
					xtype : "button",
					text : "汇总(H)",
					iconCls : "commit",
					scope : this,
					handler : this.doCommit,
					disabled : true
				})
				items.push({
					xtype : "button",
					text : "取消汇总",
					iconCls : "writeoff",
					scope : this,
					handler : this.doCancelCommit
				})
				items.push({
					xtype : "button",
					text : "打印(P)",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : true
				})
				items.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExcel,
					disabled : true
				})
				items.push({
					xtype : "button",
					text : "帮助说明",
					iconCls : "help",
					scope : this,
					handler : this.doHelp
				})
				return items
			},
			doHelp : function() {
				Ext.MessageBox
						.alert(
								"住院管理日终汇总模块分页说明",
								"汇总日报(一)：统计某一次结帐汇总的结算和缴款信息，主要是财务对所有收费员的交账信息进行核对.<br>"
										+ "汇总日报(二)：统计某一次结帐汇总的医院所有收入，包括在院和出院病人发生的所有费用.<br>"
										+ "汇总日报(三)：统计某一次结帐汇总的所有在院病人的所有费用，不包含出院病人的费用，按大类统计.<br>"
										+ "汇总日报(四)：统计某一次结帐汇总的结算出院病人的所有费用，不包含在院病人的费用，按大类和医保分统计.<br>"
										+ "汇总日报(五)：统计某一次结帐汇总的结算出院病人的本次汇总时间内发生的费用，按大类统计.");
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			doCancelCommit : function() {
				var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "queryCancelCommit"
				});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var msg = '<br/><br/>取消收费汇总说明：<br/><br/>&nbsp&nbsp&nbsp&nbsp1.取消汇总日报请慎重'
				var navigatorName = "Microsoft Internet Explorer";
				if (navigator.appName == navigatorName) {
					msg += '<br/><br/>&nbsp&nbsp&nbsp&nbsp2.每次取消只能取消最近一次的日终汇总'
				} else {
					msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/><br/>&nbsp&nbsp&nbsp&nbsp2.每次取消只能取消最近一次的日终汇总'
				}
				msg += '<br/><br/>&nbsp&nbsp&nbsp&nbsp3.汇总日报取消后不能恢复，只能重新汇总';
				msg += '<br/><br/>取消汇总日报：<br/><br/>&nbsp&nbsp&nbsp&nbsp汇总日期:&nbsp&nbsp&nbsp&nbsp'
						+ result.json.HZRQ + '<br/>';
				Ext.MessageBox.confirm('取消汇总', msg, function(btn, text) {
					if (btn == "yes") {
						var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "cancelCommit",
							HZRQ : result.json.HZRQ
						});
						if (res.code > 300) {
							this.processReturnMsg(res.code, res.msg);
							return
						}
						Ext.MessageBox.alert("提示", "取消汇总成功!");
						var itemstb = this.panel.getTopToolbar().items;
						itemstb.get(8).setDisabled(true);
						itemstb.get(10).setDisabled(true);
						itemstb.get(11).setDisabled(true);
						return;
					}
				}, this);
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var datefrom = new Date().format('Y-m-d');
				var dateTo = new Date().format('Y-m-d');
				var items = form.items
				var f = items.get(0);
				if (f.getName() == "beginDate") {
					datefrom = f.getValue().format("Y-m-d");
				}
				f = items.get(1);
				if (f.getName() == "endDate") {
					dateTo = f.getValue().format("Y-m-d");
				}
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (this.tab.getItem("hz1").isVisible()) {
					var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
					var url = "resources/" + pages
							+ ".print?landscape=1&silentPrint=1&execJs="
							+ this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					url += "&save=2";
					var LODOP = getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
					LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
							.loadXML({
								url : url,
								httpMethod : "get"
							}));
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
					// 预览
					LODOP.PREVIEW();
				}
				if (this.tab.getItem("hz2").isVisible()) {
					var pages = "phis.prints.jrxml.Summaryofincome";
					var url = "resources/" + pages
							+ ".print?silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var LODOP = getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
					LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
							.loadXML({
								url : url,
								httpMethod : "get"
							}));
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
					// 预览
					LODOP.PREVIEW();
				}
				if (this.tab.getItem("hz3").isVisible()) {
					var pages = "phis.prints.jrxml.SummarycategoryHospital";
					var url = "resources/" + pages
							+ ".print?silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var LODOP = getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
					LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
							.loadXML({
								url : url,
								httpMethod : "get"
							}));
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
					// 预览
					LODOP.PREVIEW();
				}
				if (this.tab.getItem("hz4").isVisible()) {
					var pages = "phis.prints.jrxml.SummarycategoryHospitalFor";
					var url = "resources/" + pages
							+ ".print?silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var LODOP = getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
					LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
							.loadXML({
								url : url,
								httpMethod : "get"
							}));
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
					// 预览
					LODOP.PREVIEW();
				}
				if (this.tab.getItem("hz5").isVisible()) {// (五)出院发生
					var pages = "phis.prints.jrxml.SummarycategoryHospital1";
					var url = "resources/" + pages
							+ ".print?silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&hz=5";
					var LODOP = getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
					LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
							.loadXML({
								url : url,
								httpMethod : "get"
							}));
					LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
					// 预览
					LODOP.PREVIEW();
				}
			},
			doExcel : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var datefrom = new Date().format('Y-m-d');
				var dateTo = new Date().format('Y-m-d');
				var items = form.items
				var f = items.get(0);
				if (f.getName() == "beginDate") {
					datefrom = f.getValue().format("Y-m-d");
				}
				f = items.get(1);
				if (f.getName() == "endDate") {
					dateTo = f.getValue().format("Y-m-d");
				}
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (this.tab.getItem("hz1").isVisible()) {
					var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
					var url = "resources/" + pages
							+ ".print?type=3&landscape=1&silentPrint=1&execJs="
							+ this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					url += "&save=2";
					var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
				if (this.tab.getItem("hz2").isVisible()) {
					var pages = "phis.prints.jrxml.Summaryofincome";
					var url = "resources/" + pages
							+ ".print?type=3&silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
				if (this.tab.getItem("hz3").isVisible()) {
					var pages = "phis.prints.jrxml.SummarycategoryHospital";
					var url = "resources/" + pages
							+ ".print?type=3&silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
				if (this.tab.getItem("hz4").isVisible()) {
					var pages = "phis.prints.jrxml.SummarycategoryHospitalFor";
					var url = "resources/" + pages
							+ ".print?type=3&silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate));
					var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
				if (this.tab.getItem("hz5").isVisible()) {// (五)出院发生
					var pages = "phis.prints.jrxml.SummarycategoryHospital1";
					var url = "resources/" + pages
							+ ".print?type=3&silentPrint=1&execJs=" + this.getExecJs();
					url += "&beginDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&endDate=" + encodeURI(encodeURI(this.hzDate))
							+ "&hz=5";
					var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
			},
			doCommit : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var itemstb = this.panel.getTopToolbar().items;
				var data = {
					"id_dqrq" : this.hzDate
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionTwo,
					body : data
				});
				if (r.code == 700) {
					MyMessageTip.msg("提示", "已做过汇总结帐，不能重复汇总!", true);
					itemstb.get(8).setDisabled(true);
					return;
				} else if (r.code == 701) {
					if (confirm("请检查是否所有操作员都已做过日终结帐！\n第一次汇总将把所有数据汇总到"
							+ r.json.idt_hzrq + "，确定进行汇总处理吗?")) {
						this.doCheckout();
					}
				} else if (r.code == 702) {
					if (confirm(r.json.idt_LastDate + "之前已做过汇总日报，\n是否查询"
							+ r.json.idt_hzrq + "汇总日报?")) {
						var signum = this.berforQuery(r.json.idt_hzrq,
								r.json.idt_hzrq);
						if (signum == 1)
							return;
						this.tab.unhideTabStripItem(1);
						this.tab.unhideTabStripItem(2);
						this.tab.unhideTabStripItem(3);
						this.tab.unhideTabStripItem(4);
						var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
						var url = "resources/" + pages + ".print?type=1";
						Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
								"x-mask-loading")
						url += "&beginDate=" + r.json.idt_hzrq + "&endDate="
								+ r.json.idt_hzrq + "&save=2";
						document.getElementById(this.frameId).src = url
						this.onTabChange();
					}
				} else if (r.code == 703) {
					if (confirm("请检查是否所有操作员都已做过日终结帐！\n确定结束今日业务进行汇总处理吗?")) {
						this.doCheckout();
					}
				} else if (r.code == 704) {
					if (confirm("请检查是否所有操作员都已做过日终结帐！\n确定对" + r.json.idt_hzrq
							+ "进行汇总处理吗?")) {
						this.doCheckout();
					}
				} else if (r.code == 705) {
					if (confirm("请检查是否所有操作员都已做过日终结帐！" + r.json.idt_LastDate
							+ "\n之后未做过汇总日报，若汇总将把" + r.json.idt_LastDate
							+ "\n之后的数据汇总到" + r.json.idt_hzrq + "，确定进行汇总处理吗?")) {
						this.doCheckout();
					}
				}
				itemstb.get(10).setDisabled(false);
				itemstb.get(11).setDisabled(false);
			},
			doClose : function() {
				this.opener.closeCurrentTab();
			},
			doInquiry : function() {
				var module = this.createModule("queryWin", this.refQueryList);
				module.autoLoadData = false;
				var _ctx = this;
				module.panel = this.panel;
				module.doSure = function() {
					var items = this.panel.getTopToolbar().items;
					items.get(8).setDisabled(true);
					items.get(10).setDisabled(false);
					items.get(11).setDisabled(false);
					var r = module.getSelectedRecord();
					if (r == null) {
						return;
					}
					var hzrq = r.get("HZRQ");
					_ctx.hzDate = hzrq;
					var form = Ext.getCmp(_ctx.conditionFormId).getForm()
					if (!form.isValid()) {
						return
					}
					_ctx.tab.unhideTabStripItem(1);
					_ctx.tab.unhideTabStripItem(2);
					_ctx.tab.unhideTabStripItem(3);
					_ctx.tab.unhideTabStripItem(4);
					_ctx.tab.getItem("hz1").show();
					var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
					var url = "resources/" + pages + ".print?type=1&beginDate="
							+ encodeURI(encodeURI(hzrq)) + "&endDate="
							+ encodeURI(encodeURI(hzrq)) + "&save=2";
					Ext.getCmp(_ctx.mainFormId).el.mask("正在生成报表...",
							"x-mask-loading")
					document.getElementById(_ctx.frameId).src = url
					_ctx.queryWin.hide();
				}
				module.doCancel = function() {
					_ctx.queryWin.hide();
				}

				module.onDblClick = function(grid, index, e) {
					module.doSure();
				}

				if (!this.queryWin) {
					this.queryWin = module.getWin();
					this.queryWin.add(module.initPanel());
					module.on("winShow", this.onQueryWinShow, this);
				}
				this.queryWin.show();
				this.queryWin.center();
			},
			onQueryWinShow : function() {
				var module = this.createModule("queryWin", this.refQueryList);

				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');

				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge', ['$', "str(HZRQ,'yyyy-mm-dd')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le', ['$', "str(HZRQ,'yyyy-mm-dd')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(HZRQ,'yyyy-mm-dd')"],
									['s', datefrom]],
							['le', ['$', "str(HZRQ,'yyyy-mm-dd')"],
									['s', dateTo]]];
				}
				module.requestData.cnd = timeCnd;
				module.requestData.serviceAction = "querySQLZYJSList";
				module.loadData();
			},
			doQuery : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var datefrom = new Date().format('Y-m-d');
				var dateTo = new Date().format('Y-m-d');
				var items = form.items
				var itemstb = this.panel.getTopToolbar().items;
				var f = items.get(0);
				if (f.getName() == "beginDate") {
					datefrom = f.getValue().format("Y-m-d");
				}
				f = items.get(1);
				if (f.getName() == "endDate") {
					dateTo = f.getValue().format("Y-m-d");
				}
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var data = {
					"kssj" : datefrom,
					"jssj" : dateTo
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionQuery,
					body : data
				});
				if (r.code == 800) {
					Ext.MessageBox.alert("提示", "开始时间不能大于当前时间");
					return;
				}
				if (r.code == 801) {
					Ext.MessageBox.alert("提示", "结束时间不能大于当前时间");
					return;
				}
				if (r.code == 802) {
					Ext.MessageBox.alert("提示", "此次时间段没有可查询的数据");
					return;
				}
				this.tab.unhideTabStripItem(1);
				this.tab.unhideTabStripItem(2);
				this.tab.unhideTabStripItem(3);
				this.tab.unhideTabStripItem(4);
				var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
				var url = "resources/" + pages + ".print?type=1";
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "beginDate" || f.getName() == "endDate") {
						url += "&" + f.getName() + "="
								+ f.getValue().format("Y-m-d")
					}
				}
				url += "&save=2";
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading")
				document.getElementById(this.frameId).src = url
				this.onTabChange();
				itemstb.get(10).setDisabled(false);
				itemstb.get(11).setDisabled(false);
			},
			doLoadReport : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var items = form.items
				var itemstb = this.panel.getTopToolbar().items;
				var id_dqrq = new Date().format('Y-m-d');
				var f = items.get(2)
				if (f.getName() == "summaryDate") {
					id_dqrq = f.getValue().format("Y-m-d");
					this.hzDate = f.getValue().format("Y-m-d");
				}
				var data = {
					"id_dqrq" : id_dqrq
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceAction,
					body : data
				});
				if (r.code == 600) {
					if (confirm("第一次汇总将把所有数据结帐到" + r.json.idt_hzrq
							+ "的日期，确定产生汇总日报吗?")) {
						var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
						var url = "resources/" + pages + ".print?preview=1";
						for (var i = 0; i < items.getCount(); i++) {
							var f = items.get(i)
							if (f.getName() == "beginDate"
									|| f.getName() == "endDate"
									|| f.getName() == "summaryDate") {
								url += "&" + f.getName() + "="
										+ f.getValue().format("Y-m-d")
							}
						}
						Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
								"x-mask-loading")
						url += "&temp=" + new Date().getTime()
						document.getElementById(this.frameId).src = url
						itemstb.get(8).setDisabled(false);
						itemstb.get(10).setDisabled(true);
						itemstb.get(11).setDisabled(true);
						this.tab.hideTabStripItem(1);
						this.tab.hideTabStripItem(2);
						this.tab.hideTabStripItem(3);
						this.tab.hideTabStripItem(4);
						this.tab.getItem("hz1").show();
					} else {
						return;
					}
				} else if (r.code == 601) {
					Ext.MessageBox.alert("提示", "最后一次汇总日期为"
							+ r.json.idt_LastDate + ",当前所选日期小于最后汇总日期，不能再次汇总!");
					itemstb.get(10).setDisabled(false);
					itemstb.get(11).setDisabled(false);
					return;
				} else if (r.code == 602) {
					Ext.MessageBox.alert("提示", "最后一次汇总日期为"
							+ r.json.idt_LastDate + ",当前所选日期等于最后汇总日期，不能再次汇总!");
					itemstb.get(10).setDisabled(false);
					itemstb.get(11).setDisabled(false);
					return;
				} else if (r.code == 603) {
					if (confirm(r.json.idt_LastDate + "的日期之后未做过汇总日报，\n若汇总将把"
							+ r.json.idt_LastDate + "日期之后的数据汇总到"
							+ r.json.idt_hzrq + "日期，\n确定产生汇总日报吗?")) {
						var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
						var url = "resources/" + pages + ".print?type=1";
						for (var i = 0; i < items.getCount(); i++) {
							var f = items.get(i)
							if (f.getName() == "beginDate"
									|| f.getName() == "endDate"
									|| f.getName() == "summaryDate") {
								url += "&" + f.getName() + "="
										+ f.getValue().format("Y-m-d")
							}
						}
						url += "&temp=" + new Date().getTime();
						Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
								"x-mask-loading")
						document.getElementById(this.frameId).src = url
						itemstb.get(8).setDisabled(false);
						itemstb.get(10).setDisabled(true);
						itemstb.get(11).setDisabled(true);
						this.tab.hideTabStripItem(1);
						this.tab.hideTabStripItem(2);
						this.tab.hideTabStripItem(3);
						this.tab.hideTabStripItem(4);
						this.tab.getItem("hz1").show();
					} else {
						return;
					}
				} else if (r.code == 604) {
					Ext.MessageBox.alert("提示", "汇总日期不能大于当前日期");
					return;
				} else {
					var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
					var url = "resources/" + pages + ".print?type=1";
					for (var i = 0; i < items.getCount(); i++) {
						var f = items.get(i)
						// if (f.getName() == "type" && f.getValue() == "1") {
						// Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						// "x-mask-loading")
						// }
						if (f.getName() == "beginDate"
								|| f.getName() == "endDate"
								|| f.getName() == "summaryDate") {
							url += "&" + f.getName() + "="
									+ f.getValue().format("Y-m-d")
						}
					}
					Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
							"x-mask-loading")
					url += "&temp=" + new Date().getTime()
					document.getElementById(this.frameId).src = url
					itemstb.get(8).setDisabled(false);
					itemstb.get(10).setDisabled(true);
					itemstb.get(11).setDisabled(true);
					this.tab.hideTabStripItem(1);
					this.tab.hideTabStripItem(2);
					this.tab.hideTabStripItem(3);
					this.tab.hideTabStripItem(4);
					this.tab.getItem("hz1").show();
				}
				var data1 = {
					"id_dqrq" : id_dqrq
				};
				var r1 = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionAfter,
					body : data1
				});
				if (r1.code == 605) {
					Ext.MessageBox.alert("提示", "没有业务数据发生或操作员未做结帐日报");
					itemstb.get(8).setDisabled(true);
					this.tab.getItem("hz1").show();
					return;
				}
			},
			berforQuery : function(datefrom, dateTo) {
				var data = {
					"kssj" : datefrom,
					"jssj" : dateTo
				};
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionQuery,
					body : data
				});
				if (r.code == 802) {
					Ext.MessageBox.alert("提示", "此次时间段没有可查询的数据");
					return 1;
				} else {
					return 0;
				}
			},
			createCommonDic : function(flag) {
				var fields
				var emptyText = "请选择"
				if (flag == "type") {
					fields = this.preview
					emptyText = "预览方式"
				} else {
					fields = []
					flag = ""
				}
				var store = new Ext.data.JsonStore({
					fields : ['value', 'text'],
					data : fields
				});
				var combox = new Ext.form.ComboBox({
					store : store,
					valueField : "value",
					displayField : "text",
					mode : 'local',
					triggerAction : 'all',
					emptyText : emptyText,
					selectOnFocus : true,
					width : 100,
					name : flag,
					allowBlank : false
				})
				return combox
			},
			doCheckout : function() {
				var itemstb = this.panel.getTopToolbar().items;
				var data = {
					"summaryDate" : this.hzDate
				};
				Ext.getCmp(this.mainFormId).el
						.mask("正在汇总...", "x-mask-loading")
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionSave,
					body : data
				});
				Ext.getCmp(this.mainFormId).el.unmask()
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return
				} else {
					MyMessageTip.msg("提示", "汇总成功!", true);
					this.tab.unhideTabStripItem(1);
					this.tab.unhideTabStripItem(2);
					this.tab.unhideTabStripItem(3);
					this.tab.unhideTabStripItem(4);
					itemstb.get(8).setDisabled(true);
					var pages = "phis.prints.jrxml.PatientDepartmentChargesSummary";
					var url = "resources/" + pages + ".print?type=1";
					Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
							"x-mask-loading")
					url += "&beginDate="
							+ encodeURI(encodeURI(r.json.idt_hzrq + " 00:00:00"))
							+ "&endDate="
							+ encodeURI(encodeURI(r.json.idt_hzrq + " 00:00:00"))
							+ "&save=2";
					this.hzDate = this.hzDate + " 00:00:00";
					document.getElementById(this.frameId).src = url
					this.onTabChange();
				}
			},
			onAfterrender : function() {
				this.tab.hideTabStripItem(1);
				this.tab.hideTabStripItem(2);
				this.tab.hideTabStripItem(3);
				this.tab.hideTabStripItem(4);
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}