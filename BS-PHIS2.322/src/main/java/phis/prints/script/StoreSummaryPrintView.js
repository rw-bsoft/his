$package("phis.prints.script")
$import("app.desktop.Module","phis.script.widgets.Spinner","util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.SimpleModule")

phis.prints.script.StoreSummaryPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.StoreSummaryPrintView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.prints.script.StoreSummaryPrintView, app.desktop.Module, {
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
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.StoreSummaryPrintView";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.StoreSummaryPrintView";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.StoreSummaryPrintView";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.StoreSummaryPrintView\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {

		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "会计期间:"
				}));
		this.date = new Ext.ux.form.Spinner({
					fieldLabel : '会计期间开始',
					name : 'summaryDate',
					value : new Date().format('Y-m'),
					strategy : {
						xtype : "month"
					},
					width : 100
				});
		tbar.push(this.date)
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
		var date = this.date.getValue();
		if (!date) {
			return
		}
		var body = {
			"date" : date,
			"type" : "HZ"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
		//	title : "库房收支汇总报表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		//var url = "*.print?pages=phis.prints.jrxml.StoreSummaryPrintView&config="
		//	+ encodeURI(encodeURI(Ext.encode(printConfig)))
		//	+ "&silentPrint=1";
		var pages="phis.prints.jrxml.StoreSummaryPrintView";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var date = this.date.value;
		if (!date) {
			return
		}
		var body = {
			"date" : date,
			"type" : "HZ"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
		//	title : "库房收支汇总报表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		var pages="phis.prints.jrxml.StoreSummaryPrintView";
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
