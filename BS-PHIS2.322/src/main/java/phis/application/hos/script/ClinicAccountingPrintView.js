$package("phis.application.hos.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.hos.script.ClinicAccountingPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.application.hos.script.ClinicAccountingPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.hos.script.ClinicAccountingPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_clinicAccounting";
		this.conditionFormId = "SimplePrint_form_clinicAccounting";
		this.mainFormId = "SimplePrint_mainform_clinicAccounting";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"clinicAccounting\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "会计日期 从:"
				}));
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
		tbar.push(new Ext.form.Label({
					text : "统筹区域:"
				}));
		this.xzqyval = util.dictionary.SimpleDicFactory.createDic({
					id : "xzqy",
					width : 150,
					defaultIndex : "0"
				});
		tbar.push(this.xzqyval);
		tbar.push(new Ext.form.Label({
					text : "人员类别:"
				}));
		this.rylbval = util.dictionary.SimpleDicFactory.createDic({
					id : "rylb",
					width : 150,
					defaultIndex : "0"
				});
		tbar.push(this.rylbval);

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
	getdictext : function() {
		var til1 = "";
		var til2 = "";
		if ("全部" != this.xzqyval.getRawValue()) {
			til1 = "(" + this.xzqyval.getRawValue() + ")";
		}
		if ("全部" != this.rylbval.getRawValue()) {
			til2 = this.rylbval.getRawValue();
		}
		return til1 + til2;
	},
	doQuery : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		var xzqyval = this.xzqyval.value;
		var rylbval = this.rylbval.value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:59:59",
			"xzqy" : xzqyval,
			"rylb" : rylbval
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			title : this.getdictext() + "基本医疗保险住院费用结算汇总表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		var url = "clinicAccounting.clinicAccounting?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		var xzqyval = this.xzqyval.value;
		var rylbval = this.rylbval.value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:59:59",
			"xzqy" : xzqyval,
			"rylb" : rylbval
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			title : this.getdictext() + "基本医疗保险住院费用结算汇总表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		var url = "clinicAccounting.clinicAccounting?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		//预览LODOP.PREVIEW();
		//预览LODOP.PRINT();
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		//预览
		LODOP.PREVIEW();
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
