$package("phis.application.ivc.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")
//中医项目统计
phis.application.ivc.script.ChineseMedicineProjectPrintView = function(cfg) {
	this.width = 200 
	this.height = 200
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
	this.exContext={};
	phis.application.ivc.script.ChineseMedicineProjectPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ivc.script.ChineseMedicineProjectPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_ChineseMedicineProject";
		this.conditionFormId = "SimplePrint_form_ChineseMedicineProject";
		this.mainFormId = "SimplePrint_mainform_ChineseMedicineProject";
		if (this.mainApp.pharmacyId == null
				|| this.mainApp.pharmacyId == ""
				|| this.mainApp.pharmacyId == undefined) {
			Ext.Msg.alert("提示", "未设置登录药房,请先设置");
			return null;
		}
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : this.initConditionFields(),
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"ChineseMedicineProject\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var tbar = new Ext.Toolbar();
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
								fieldLabel : '时间开始',
								name : 'dateFrom',
								value : new Date()
										.format('Y-m-d'),
								strategy : {
									xtype : "date"
								},
									width : 100
								}), {
							xtype : "label",
							forId : "window",
							text : "至"
						}, new Ext.ux.form.Spinner({
									fieldLabel : '时间结束',
									name : 'dateTo',
									value : new Date()
											.format('Y-m-d'),
									strategy : {
										xtype : "date"
									},
									width : 100
								})]
			
			});
		this.simple = simple;
		tbar.add(simple, this.createButtons());
		return tbar;
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
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		var pages="phis.prints.jrxml.ChineseMedicineProject";
		 var url="resources/"+pages+".print?type=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo;
		document.getElementById(this.frameId).src = url;
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
		var type="1";
		if(this.type && this.type=="3"){
			type=this.type;
		}
		this.type="1";
		var pages="phis.prints.jrxml.ChineseMedicineProject";
		 var url="resources/"+pages+".print?type="+type;
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo;
		if(type=="3"){
			var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			return printWin;
		}else{
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
		rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
		rehtm.lastIndexOf("page-break-after:always;");
		rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		LODOP.PREVIEW();
		}
	},
	doExport : function() {
		this.type="3";
		this.doPrint();
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}