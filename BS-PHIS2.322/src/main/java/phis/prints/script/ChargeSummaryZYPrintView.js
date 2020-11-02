﻿$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "phis.script.util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.widgets.DatetimeField")

phis.prints.script.ChargeSummaryZYPrintView = function(cfg) {
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
	this.dyrq = "";
	phis.prints.script.ChargeSummaryZYPrintView.superclass.constructor.apply(
			this, [cfg])
	Ext.apply(this, phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.ChargeSummaryZYPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_ChargeSummaryZY";
		this.conditionFormId = "SimplePrint_form_ChargeSummaryZY";
		this.mainFormId = "SimplePrint_mainform_ChargeSummaryZY";
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
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"ChargeSummaryZY\")'></iframe>"
		})
		this.panel = panel
		this.panelq = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		items.push(new Ext.form.Label({
					text : "从"
				}))
		items.push(new phis.script.widgets.DateTimeField({
					name : 'beginDate',
					value : new Date(),
					width : 150,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d H:i:s',
					emptyText : '开始时间'
				}))
		items.push(new Ext.form.Label({
					text : "到"
				}))
		items.push(new phis.script.widgets.DateTimeField({
					name : 'endDate',
					value : new Date(),
					width : 150,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d H:i:s',
					emptyText : '结束时间'
				}))
		var preview = this.createCommonDic("type")
		preview.value = "1"
		items.push(preview)
		items.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this,
					handler : this.doquery
				})
		items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint
				})
		return items
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
	},
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items

		var f = items.get(0);
		var f1 = items.get(1);
		if (f.getName() == "beginDate") {
			this.begidD = f.getValue();
		}
		if (f1.getName() == "endDate") {
			this.endD = f1.getValue();
		}
		var printConfig = {
			file : "ChargeSummaryZYFile",
			beginDate : this.begidD,
			endDate : this.endD
		}
		var pages="phis.prints.jrxml.TrendsReportServlet";
		var url="resources/"+pages+".print?config="+ encodeURI(Ext.encode(printConfig));
		
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
	},

	doClose : function() {
		this.opener.closeCurrentTab();
	},

	doquery : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var f = items.get(0);
		var f1 = items.get(1);
		if (f.getName() == "beginDate") {
			this.begidD = f.getValue();
		}
		if (f1.getName() == "endDate") {
			this.endD = f1.getValue();
		}
		if (Date.parseDate(this.begidD, 'Y-m-d H:i:s') > Date.parseDate(
				this.endD, 'Y-m-d H:i:s')) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var printConfig = {
			file : "ChargeSummaryZYFile",
			beginDate : this.begidD,
			endDate : this.endD
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.TrendsReportServlet&config="
		//	+ encodeURI(Ext.encode(printConfig));
		var pages="phis.prints.jrxml.TrendsReportServlet";
		 var url="resources/"+pages+".print?config="+ encodeURI(Ext.encode(printConfig));
		if (document.getElementById(this.frameId)) {
			document.getElementById(this.frameId).src = url
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
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}