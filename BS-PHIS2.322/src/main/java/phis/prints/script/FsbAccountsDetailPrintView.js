$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.FsbAccountsDetailPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	phis.prints.script.FsbAccountsDetailPrintView.superclass.constructor.apply(
			this, [cfg])
	this.title = "";
}
Ext.extend(phis.prints.script.FsbAccountsDetailPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_FsbAccountsDetail";
		this.conditionFormId = "SimplePrint_form_FsbAccountsDetail";
		this.mainFormId = "SimplePrint_mainform_FsbAccountsDetail";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"AccountsDetail\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
//		items.push({
//					xtype : "button",
//					text : "明  细",
//					iconCls : "default",
//					scope : this,
//					handler : this.doLoadReport
//				})
		items.push({
					xtype : "button",
					text : "打  印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint
				})
		items.push({
					xtype : "button",
					text : "关  闭",
					iconCls : "common_cancel",
					scope : this,
					handler : this.doClose
				})
		return items
	},
	doClose : function() {
		this.opener.closeCurrentTab();
	},
	doPrint : function() {//encodeURI(encodeURI(
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.AccountsDetail";
		 var pages="phis.prints.jrxml.FsbAccountsDetail";
		 var url="resources/"+pages+".print?type="+1;
		url += "&jzrq=" +encodeURI(encodeURI( this.jzrq ))+ "&jzbs=" + this.jzbs+ "&czgh=" + encodeURI(encodeURI(this.mainApp.logonName));
	/*	window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
	*/
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
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
	//	var url = this.printurl + "*.print?pages=phis.prints.jrxml.AccountsDetail";
		var pages="phis.prints.jrxml.FsbAccountsDetail";
		 var url="resources/"+pages+".print?type="+1;
		url += "&jzrq=" +encodeURI(encodeURI( this.jzrq ))+ "&jzbs=" + this.jzbs+ "&czgh=" + encodeURI(encodeURI(this.mainApp.logonName));
		document.getElementById(this.frameId).src = url
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}