$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.prints.script.ChargesSummaryPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.preview = [{
		value : "1",
		text : "网页预览"
	}, {
		value : "0",
		text : "PDF"
	}, {
		value : "2",
		text : "WORD"
	}, {
		value : "3",
		text : "EXCEL"
	}]
	this.conditions = []
	phis.prints.script.ChargesSummaryPrintView.superclass.constructor.apply(
			this, [cfg])
}
var hztz = false;
Ext.extend(phis.prints.script.ChargesSummaryPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.ChargesSummary";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.ChargesSummary";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.ChargesSummary";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : '',
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
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.ChargesSummary\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			initConditionFields : function() {
				var items = []
				items.push(new Ext.form.Label({
					text : "从"
				}))
				items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : Date.getServerDate(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}))
				items.push(new Ext.form.Label({
					text : "到"
				}))
				items.push(new Ext.form.DateField({
					name : 'endDate',
					value : Date.getServerDate(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}))
				items.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this,
					handler : this.doInquiry
				})
				items.push(new Ext.form.Label({
					text : "汇总时间"
				}))
				items.push(new Ext.form.DateField({
					name : 'hzDate',
					value : Date.getServerDate(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '汇总时间'
				}))
				items.push({
					xtype : "button",
					text : "产 生",
					iconCls : "default",
					scope : this,
					handler : this.doLoadReport
				})
				items.push({
					xtype : "button",
					text : "汇 总",
					iconCls : "commit",
					scope : this,
					handler : this.doCommit,
					disabled : true
				})
				items.push({
					xtype : "button",
					text : "取 消 汇 总",
					iconCls : "writeoff",
					scope : this,
					handler : this.doCancelCommit
				})
				items.push({
					xtype : "button",
					text : "打 印",
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
				return items
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
						+ result.json.HZRQ.substring(0, 10) + '<br/>';
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
						return;
					}
				}, this);
			},
			doInquiry : function() {
				var module = this.createModule("queryWin", this.refQueryList);
				module.autoLoadData = false;
				var _ctx = this;
				module.panel = this.panel;
				module.doSure = function() {
					var items = this.panel.getTopToolbar().items;
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
					var pages = "phis.prints.jrxml.ChargesSummary";
					var url = "resources/" + pages + ".print?type=" + 1;
					url += "&save=2&hzrq=" + encodeURI(encodeURI(hzrq))
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
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var pages = "phis.prints.jrxml.ChargesSummary";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&landscape=1&execJs="
						+ this.getExecJs();
				url += "&save=2&hzrq=" + encodeURI(encodeURI(this.hzDate))
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
			},
			doExcel : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var pages = "phis.prints.jrxml.ChargesSummary";
				var url = "resources/" + pages
						+ ".print?type=3&silentPrint=1&landscape=1&execJs="
						+ this.getExecJs();
				url += "&save=2&hzrq=" + encodeURI(encodeURI(this.hzDate))
				var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			doCommit : function() {
				Ext.Msg.confirm("请确认", "确认汇总【" + this.hzDateQr + "】前日结的费用吗？",
						function(btn) {
							if (btn == 'yes') {
								var form = Ext.getCmp(this.conditionFormId)
										.getForm()
								if (!form.isValid()) {
									return
								}
								Ext.getCmp(this.mainFormId).el.mask("正在汇总...",
										"x-mask-loading")
								hztz = true;
								var pages = "phis.prints.jrxml.ChargesSummary";
								var url = "resources/" + pages + ".print?type="
										+ 1;
								url += "&save=1&hzrq="
										+ encodeURI(encodeURI(this.hzDate))
								document.getElementById(this.frameId).src = url
								var items = this.panel.getTopToolbar().items;
								items.get(8).setDisabled(true);
								items.get(10).setDisabled(false);
								items.get(11).setDisabled(false);
							}
						}, this);
			},
			doLoadReport : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var bars = this.panel.getTopToolbar().items;
				var items = form.items;
				this.hzDateQr = items.get(2).getValue().format('Y-m-d');
				var hzDate = items.get(2).getValue().format('Y-m-d')
						+ " 23:59:59";
				this.hzDate = hzDate;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionVerification,
					hzrq : hzDate
				});
				if (r.code == 600) {
					bars.get(8).setDisabled(true);
					bars.get(10).setDisabled(true);
					bars.get(11).setDisabled(true);
					Ext.MessageBox.alert("提示", "没有产生的数据");
					return;
				} else if (r.code == 601) {
					bars.get(8).setDisabled(true);
					bars.get(10).setDisabled(true);
					bars.get(11).setDisabled(true);
					Ext.MessageBox.alert("提示", "汇总日期不能大于当前日期!");
					return;
				} else if (r.code == 602) {
					bars.get(8).setDisabled(true);
					bars.get(10).setDisabled(true);
					bars.get(11).setDisabled(true);
					Ext.MessageBox.alert("提示", "最后一次汇总日期为"
							+ r.json.body.substring(0, 10)
							+ ",当前所选日期小于或等于最后汇总日期，不能再次汇总!");
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionCheckOutVerification,
					hzrq : hzDate
				});
				if (r.code == 602) {
					bars.get(8).setDisabled(true);
					bars.get(10).setDisabled(true);
					bars.get(11).setDisabled(true);
					Ext.MessageBox.alert("提示", "今天已做过汇总,不能重复汇总!");
					return;
				}
				var pages = "phis.prints.jrxml.ChargesSummary";
				var url = "resources/" + pages + ".print?type=" + 1;
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type" && f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
								"x-mask-loading")
					}
				}
				url += "&temp=" + new Date().getTime() + "&hzrq="
						+ encodeURI(encodeURI(hzDate));
				document.getElementById(this.frameId).src = url
				bars.get(8).setDisabled(false);
				bars.get(10).setDisabled(true);
				bars.get(11).setDisabled(true);
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
				module.requestData.serviceAction = "querySQLList";
				module.loadData();
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
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
	if (hztz) {
		hztz = false;
		MyMessageTip.msg("提示", "汇总成功!", true);
	}
}