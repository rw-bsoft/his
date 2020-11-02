$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.AntimicrobialDrugUseInformationAccountingPrintView = function(cfg) {
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
	this.sfrbrq = "";
	phis.prints.script.AntimicrobialDrugUseInformationAccountingPrintView.superclass.constructor.apply(this,
			[cfg])
	Ext.apply(this,phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.AntimicrobialDrugUseInformationAccountingPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_AntimicrobialDrugUseInformationAccounting";
		this.conditionFormId = "SimplePrint_form_AntimicrobialDrugUseInformationAccounting";
		this.mainFormId = "SimplePrint_mainform_AntimicrobialDrugUseInformationAccounting";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : "",
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"AntimicrobialDrugUseInformationAccounting\")'></iframe>"
		})
		this.panel = panel
		this.panelq = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		items.push(new Ext.form.Label({
					text : "发药时间"
				}))
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}))
		items.push(new Ext.form.Label({
					text : "至"
				}))
		items.push(new Ext.form.DateField({
					name : 'endDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}))
		items.push({
					xtype : "button",
					text : "统计",
					iconCls : "query",
					scope : this,
					handler : this.doquery
				})
		items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : true
				})
		return items
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
	},
	doPrint : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');
//		var url = this.printurl + ".print?pages=AntimicrobialDrugUseInformation";
		 var pages="phis.prints.jrxml.AntimicrobialDrugUseInformation";
		 var url="resources/"+pages+".print?silentPrint=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo
		var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			// 预览LODOP.PREVIEW();
			// 预览LODOP.PRINT();
			// LODOP.PRINT_DESIGN();
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
	},
	doClose : function() {
		this.opener.closeCurrentTab();
	},

	doquery : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');
//		var url = this.printurl + ".print?pages=AntimicrobialDrugUseInformation";
		var pages="phis.prints.jrxml.AntimicrobialDrugUseInformation";
		var url="resources/"+pages+".print?silentPrint=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo
			document.getElementById(_ctx.frameId).src = url
		this.panel.getTopToolbar().items.get(5).setDisabled(false);
	}
	
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}