$package("phis.application.sto.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.sto.script.StorehouseOutPrintView = function(cfg) {
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
	phis.application.sto.script.StorehouseOutPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseOutPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_storehouseList";
		this.conditionFormId = "SimplePrint_form_storehouseList";
		this.mainFormId = "SimplePrint_mainform_storehouseList";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"storehouseOut\")'></iframe>"
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
		var param=phis.script.common.loadSystemParams({"privates" : ['YKCKDDYZ']}).YKCKDDYZ;
		if(param==null || param=="null"){param="A4"};
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		//var pages="phis.prints.jrxml.StorehouseOut";
		var pages="phis.prints.jrxml.StorehouseOut_"+param;
		 var url="resources/"+pages+".print?silentPrint=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			url += "&" + f.getName() + "=" + f.getValue()
		}
		url += "&temp=" + new Date().getTime() + "&yfsb=" + this.yfsb
				+ "&ckfs=" + this.ckfs + "&ckdh=" + this.ckdh + "&ckks=" + this.ckks
		//zhaojian 2017-08-24 去除打印时的边框
		var rehtm=util.rmi.loadXML({url : url,httpMethod : "get"});						
								rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
								//去掉打印body的边框
								rehtm = rehtm.replace("<body", "<body style='margin: 0'")			
				
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		if(param=="A4"){LODOP.SET_PRINT_PAGESIZE("0","","","")};
		if(param=="210X140"){LODOP.SET_PRINT_PAGESIZE("1", "210mm", "140mm", "")};
		//LODOP.SET_PRINT_PAGESIZE("0","","","");
		//预览LODOP.PREVIEW();
		//预览LODOP.PRINT();
		//LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%",rehtm);
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");		
		LODOP.PREVIEW();// 预览
	},
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var pages="phis.prints.jrxml.StorehouseList";
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