$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.HospitalPharmacyMedicineBackHZPrintView = function(cfg) {
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
	phis.prints.script.HospitalPharmacyMedicineBackHZPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.HospitalPharmacyMedicineBackHZPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_HospitalPharmacyMedicineBackHZ";
		this.conditionFormId = "SimplePrint_form_HospitalPharmacyMedicineBackHZ";
		this.mainFormId = "SimplePrint_mainform_HospitalPharmacyMedicineBackHZ";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"HospitalPharmacyMedicineBackHZ\")'></iframe>"
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
		// var url = this.printurl +
		// "*.print?pages=phis.prints.jrxml.HospitalPharmacyMedicineBackHZ";
		var pages="phis.prints.jrxml.HospitalPharmacyMedicineBackHZ";
		 var url="resources/"+pages+".print?type=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			url += "&" + f.getName() + "=" + f.getValue()
		}
		var yf=0;
		if(this.YF){
			yf=this.YF;
		}
		url += "&dateFrom="+this.dateFrom+"&dateTo="+this.dateTo+"&YF="+yf+"&bq="+this.bq;
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
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		// var url = this.printurl +
		// "*.print?pages=phis.prints.jrxml.HospitalPharmacyMedicineBackHZ";
		var pages="phis.prints.jrxml.HospitalPharmacyMedicineBackHZ";
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