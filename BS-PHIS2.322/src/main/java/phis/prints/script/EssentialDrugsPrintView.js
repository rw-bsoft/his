$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.EssentialDrugsPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.EssentialDrugsPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.EssentialDrugsPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_EssentialDrugs";
		this.conditionFormId = "SimplePrint_form_EssentialDrugs";
		this.mainFormId = "SimplePrint_mainform_EssentialDrugs";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : {
// id : this.conditionFormId,
// xtype : "form",
// layout : "hbox",
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				enableOverflow : true,
				items : this.initConditionFields()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"EssentialDrugs\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		items.push(new Ext.form.Label({
			text : "统计日期:"
		}))
		items.push(new Ext.form.DateField({
			id : 'beginDate_out',
			name : 'beginDate',
			value : new Date(),
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
			id : 'endDate_out',
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
		var datefrom = Ext.getDom("beginDate_out").value;
		var dateTo = Ext.getDom("endDate_out").value;
		if (datefrom != null && dateTo != null && datefrom != ""
			&& dateTo != "" && datefrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var pages="phis.prints.jrxml.EssentialDrugs";
		 var url="resources/"+pages+".print?silentPrint=1";
		url += "&datefrom="+ datefrom+"&dateto="+dateTo;
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
	doLoadReport : function() {
		var datefrom = Ext.getDom("beginDate_out").value;
		var dateTo = Ext.getDom("endDate_out").value;
		if (datefrom != null && dateTo != null && datefrom != ""
			&& dateTo != "" && datefrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var pages="phis.prints.jrxml.EssentialDrugs";
		 var url="resources/"+pages+".print?type=1";
		url += "&datefrom="+ datefrom+"&dateto="+dateTo;
		document.getElementById(this.frameId).src = url
		this.fireEvent("loadGraph", datefrom,dateTo);
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}