$package("phis.application.mds.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.mds.script.MedicinesBasicPrintView = function(cfg) {//MedicinesBasicPrintView
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
	phis.application.mds.script.MedicinesBasicPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.mds.script.MedicinesBasicPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_basicMediMsg";
		this.conditionFormId = "SimplePrint_form_basicMediMsg";
		this.mainFormId = "SimplePrint_mainform_basicMediMsg";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"basicMediMsg\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		var preview = this.createCommonDic("type")
		preview.value = "1"
		items.push(preview)
		items.push({
					xtype : "button",
					text : "生成报表",
					iconCls : "default",
					scope : this,
					handler : this.doLoadReport
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
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.BasicMediMsg";
		var pages="phis.prints.jrxml.BasicMediMsg";
		 var url="resources/"+pages+".print?type=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			url += "&" + f.getName() + "=" + f.getValue()
		}
		if(this.VALUE&&this.VALUE!=null&&this.VALUE!=""){
		url += "&LB=" + this.LB+"&VALUE="+this.VALUE;
		}
		if(this.ZBY){
		url += "&ZBY=1";
		}
		window.open(url,"","height="+ (screen.height - 100)+ ", width="+ (screen.width - 10)
				+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
	},
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
	//	var url = this.printurl + "*.print?pages=phis.prints.jrxml.BasicMediMsg";
		var pages="phis.prints.jrxml.BasicMediMsg";
		 var url="resources/"+pages+".print?type=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			if (f.getName() == "type" && f.getValue() == "1") {
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading")
			}
		}

		url += "&temp=" + new Date().getTime()
		document.getElementById(this.frameId).src = url
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