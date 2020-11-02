$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.helper.Helper")

phis.prints.script.DepartmentConsumptionSummaryPrintView = function(cfg) {
	this.exContext = {};
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
	phis.prints.script.DepartmentConsumptionSummaryPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.prints.script.DepartmentConsumptionSummaryPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp.storehouseId == null
						|| this.mainApp.storehouseId == ""
						|| this.mainApp.storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				this.frameId = "SimplePrint_frame_DepartmentConsumptionSummary";
				this.conditionFormId = "SimplePrint_form_DepartmentConsumptionSummary";
				this.mainFormId = "SimplePrint_mainform_DepartmentConsumptionSummary";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : this.getTbar(),
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"DepartmentConsumptionSummary\")'></iframe>"
				})
				this.panel = panel
				return panel
			},

			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var zblb = this.createDicField({
							"defaultIndex" : "0",
							"width" : 100,
							"id" : "phis.dictionary.prescriptionType",
							"emptyText" : "全部类别"
						})
				// zblb.setDisabled(true);
				var dateD = new Date().format('Y-m-d');
				var beginDDate = dateD.substring(0, dateD.lastIndexOf("-"))
						+ "-01";
				var simple = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							layout : "table",
							bodyStyle : 'padding:5px 5px 5px 5px',
							defaults : {},
							defaultType : 'textfield',
							items : [{
										xtype : "label",
										forId : "window",
										text : "日期 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '发药时间开始',
												name : 'dateFrom',
												value : beginDDate,
												strategy : {
													xtype : "date"
												},
												width : 100
											}), {
										xtype : "label",
										forId : "window",
										text : "至"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '发药时间结束',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												},
												width : 100
											}), {
										xtype : "label",
										forId : "window",
										text : "药品类别"
									}, zblb]

						});
				this.simple = simple;
				this.zblb = zblb;
				tbar.add(simple, this.createButtons());
				return tbar;
			},

			createDicField : function(dic) {
				if (dic.id == "phis.dictionary.department_bq") {
					var arr = new Array();
					arr.push(31);
					// dic.filter="['eq',['$map',['s','JGID']],['s','1']]";
				}
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"
				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			},
			doQuery : function() {
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "请输入统计时间");
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}

				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");
				var pages = "phis.prints.jrxml.DepartmentConsumptionSummary";
				var url = "resources/" + pages + ".print?type=1";
				url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&zblb="
						+ (this.zblb.getValue() || '-1');
				document.getElementById(this.frameId).src = url;
			},
			doDetails : function() {
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "请输入统计时间");
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var module = this.createModule("queryWin", this.refkddmModule);
				module.autoLoadData = false;
				var _ctx = this;
				module.panel = this.panel;
				module.doSure = function() {
					var r = module.getSelectedRecord();
					if (r == null) {
						return;
					}
					var items = _ctx.panel.getTopToolbar().items;
					var ksdm = r.get("CKKS");
					_ctx.qjksdm = ksdm;
					// var url = _ctx.printurl +
					// "*.print?pages=phis.prints.jrxml.chargesDaily";
					var pages = "phis.prints.jrxml.DepartmentConsumptionDetails";
					var url = "resources/" + pages + ".print?type=1";
					url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo
							+ "&ksdm=" + ksdm + "&zblb=" + _ctx.zblb.getValue();
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
				var module = this.createModule("queryWin", this.refkddmModule);
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "请输入统计时间");
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (dateFrom != null && dateFrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge', ['$', "str(a.CKRQ,'yyyy-mm-dd')"],
							['s', dateFrom]];
				} else if (dateTo != null && dateTo != ""
						&& (dateFrom == null || dateFrom == "")) {
					timeCnd = ['le', ['$', "str(a.CKRQ,'yyyy-mm-dd')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && dateFrom != null
						&& dateFrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(a.CKRQ,'yyyy-mm-dd')"],
									['s', dateFrom]],
							['le', ['$', "str(a.CKRQ,'yyyy-mm-dd')"],
									['s', dateTo]]];
				}
				module.requestData.zblb = this.zblb.getValue();
				module.requestData.cnd = timeCnd;
				module.requestData.serviceId = "phis.storeDepartmentConsumptionservice";
				module.requestData.serviceAction = "querySUM";
				module.loadData();
			},
			doPrint : function() {
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "请输入统计时间");
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				// var url = this.printurl +
				// "*.print?pages=phis.prints.jrxml.DepartmentConsumptionSummary";
				var pages = "phis.prints.jrxml.DepartmentConsumptionSummary";
				var url = "resources/" + pages + ".print?type=1";
				url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&zblb="
						+ this.zblb.getValue();

				if (this.qjksdm) {
					pages = "phis.prints.jrxml.DepartmentConsumptionDetails";
					url = "resources/" + pages + ".print?type=1";
					url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo
							+ "&ksdm=" + this.qjksdm + "&zblb="
							+ this.zblb.getValue();
					this.qjksdm = null;
				}
				/*
				 * window .open( url, "", "height=" + (screen.height - 100) + ",
				 * width=" + (screen.width - 10) + ", top=0, left=0, toolbar=no,
				 * menubar=yes, scrollbars=yes, resizable=yes,location=no,
				 * status=no")
				 */
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			}

		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}