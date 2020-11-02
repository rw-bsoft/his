$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.FaultySummaryPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.FaultySummaryPrintView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.prints.script.FaultySummaryPrintView, app.desktop.Module, {
	initPanel : function() {
		if (this.mainApp.deptId != this.mainApp.topUnitId) {
			if (this.mainApp.treasuryId == null
					|| this.mainApp.treasuryId == ""
					|| this.mainApp.treasuryId == undefined) {
				Ext.Msg.alert("提示", "未设置登录库房,请先设置");
				return null;
			}
			if (this.mainApp.treasuryEjkf != 0) {
				Ext.MessageBox.alert("提示", "该库房不是一级库房!");
				return;
			}
			if (this.mainApp.treasuryCsbz != 1) {
				Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
				return;
			}
		}
		if (this.mainApp.treasuryEjkf != 0) {
			Ext.MessageBox.alert("提示", "该库房不是一级库房!");
			return;
		}
		if (this.mainApp.treasuryCsbz != 1) {
			Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
			return;
		}
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.StoreMonthlyServletZK";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.StoreMonthlyServletZK";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.StoreMonthlyServletZK";
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
				items : this.getTbar()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.StoreMonthlyServletZK\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var tbar = [];
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		this.dateFrom = new Ext.form.DateField({
					name : 'dateFrom',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				});
		tbar.push(this.dateFrom);
		tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
		this.dateTo = new Ext.form.DateField({
					name : 'dateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				});
		tbar.push(this.dateTo);

		// 账簿类别
		var zblbItems = this.getZblb();
		this.zblbItems = zblbItems;
		var radioGroup = new Ext.form.ComboBox({
					triggerAction : 'all',
					mode : 'local',
					store : new Ext.data.ArrayStore({
								id : 0,
								fields : ['zblbId', 'displayText'],
								data : zblbItems
							}),
					valueField : 'zblbId',
					displayField : 'displayText',
					listeners : {
						afterRender : function(combo) {
							var firstValue = zblbItems[0][0];
							combo.setValue(firstValue);// 同时下拉框会将与name为firstValue值对应的
							// text显示
						}
					}

				});
		this.radioGroup = radioGroup;
		tbar.push(new Ext.form.Label({
					text : " 账簿类别: "
				}));
		tbar.push(this.radioGroup);

		// 报损方式
		var bsfsRadioGroup = new Ext.form.RadioGroup({
					height : 20,
					width : 180,
					name : 'bsfs', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '在库报损',
								name : 'bsfs',
								inputValue : 0
							}, {
								boxLabel : '科室报损',
								name : 'bsfs',
								inputValue : 1
							}]
				})
		this.bsfsRadioGroup = bsfsRadioGroup;
		tbar.push(new Ext.form.Label({
					text : " 报损方式: "
				}));
		tbar.push(this.bsfsRadioGroup);

		tbar.push({
					xtype : "button",
					text : "刷新",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				})
		tbar.push({
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : false
				})
		return tbar;
	},
	doQuery : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"zblb" : this.radioGroup.getValue(),
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00",
			"type" : "BSHZ",
			"bsfs" : this.bsfsRadioGroup.getValue().inputValue
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "报损汇总报表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		//var url = "*.print?pages=phis.prints.jrxml.StoreMonthlyServletZK&config="
		//	+ encodeURI(encodeURI(Ext.encode(printConfig)))
		//	+ "&silentPrint=1";
		var pages="phis.prints.jrxml.StoreMonthlyServletZK";
		 var url="resources/"+pages+".print?config="
		 + encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"zblb" : this.radioGroup.getValue(),
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00",
			"type" : "BSHZ",
			"bsfs" : this.bsfsRadioGroup.getValue().inputValue
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
		//	title : "报损汇总报表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		 var pages="phis.prints.jrxml.StoreMonthlyServletZK";
		 var url="resources/"+pages+".print?config="
		    + encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		 var LODOP=getLodop();
		 LODOP.PRINT_INIT("打印控件");
		 LODOP.SET_PRINT_PAGESIZE("0","","","");
		//预览LODOP.PREVIEW();
		//预览LODOP.PRINT();
		//LODOP.PRINT_DESIGN();
		 LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		 LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		 //预览
		 LODOP.PREVIEW();
		 Ext.getCmp(this.mainFormId).el.unmask("正在生成报表...", "x-mask-loading");
	},
	getZblb : function() {
		var kfxh = this.mainApp.treasuryId;
		var body = {};
		body["KFXH"] = kfxh;
		var r = util.rmi.miniJsonRequestSync({
					serviceId : "phis.storageOfMaterialsService",
					serviceAction : "getZblbByKfxh",
					body : body
				});
		var kfzblb = [];
		kfzblb = r.json.list;
		return kfzblb;

	},
	doAction : function(item, e) {
		var cmd = item.cmd
		var ref = item.ref

		if (ref) {
			this.loadRemote(ref, item)
			return;
		}
		var script = item.script
		if (cmd == "create") {
			if (!script) {
				script = this.createCls
			}
			this.loadModule(script, this.entryName, item)
			return
		}
		if (cmd == "update" || cmd == "read") {
			var r = this.getSelectedRecord()
			if (r == null) {
				return
			}
			if (!script) {
				script = this.updateCls
			}
			this.loadModule(script, this.entryName, item, r)
			return
		}
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
