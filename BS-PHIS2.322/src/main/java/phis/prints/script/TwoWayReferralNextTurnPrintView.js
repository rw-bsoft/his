$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.TwoWayReferralNextTurnPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.TwoWayReferralNextTurnPrintView.superclass.constructor.apply(
			this, [cfg])
	this.ib_ks = 1;
	this.ib_ys = 0;
	this.depValue = [];
}
Ext.extend(phis.prints.script.TwoWayReferralNextTurnPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.TwoWayReferralNextTurn";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.TwoWayReferralNextTurn";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.TwoWayReferralNextTurn";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : {
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				enableOverflow : true
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.TwoWayReferralNextTurn\")'></iframe>"
		})
		this.panel = panel
		panel.on("afterrender", this.onReady, this);
		return panel
	},
	onReady : function() {
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		// var url =
		// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&config="
		// + encodeURI(encodeURI(Ext.encode(printConfig)))
		// + "&silentPrint=1";
		var pages="phis.prints.jrxml.TwoWayReferralNextTurn";
			 var url="resources/"+pages+".print?silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFrom = Ext.getDom("dateFrom").value;
		var dateTo = Ext.getDom("dateTo").value;
		var reg =/\d{4}-\d{2}-\d{2}/ ;
		 if (!reg.test(dateFrom)) {  
			 Ext.MessageBox.alert("提示", "开始时间格式不正确！");
				return 
		}
		 if (!reg.test(dateTo)) {  
			 Ext.MessageBox.alert("提示", "结束时间格式不正确！");
				return 
		}
		if (dateFrom=="开始时间") {
			Ext.MessageBox.alert("提示", "开始时间不能为空！");
			return
		}
		if (dateTo=="结束时间") {
			Ext.MessageBox.alert("提示", "结束时间不能为空！");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"ib_ks" : this.ib_ks,
			"ib_ys" : this.ib_ys,
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00",
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			page : "whole",
			requestData : body
		}
		// 后台servelt
		// var url =
		// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&landscape=1&silentPrint=1&config="
		// + encodeURI(encodeURI(Ext.encode(printConfig)));
		var pages="phis.prints.jrxml.TwoWayReferralNextTurn";
		 var url="resources/"+pages+".print?landscape=1&silentPrint=1&config="
		 + encodeURI(encodeURI(Ext.encode(printConfig)));
	/*
	 * window .open( url, "", "height=" + (screen.height - 100) + ", width=" +
	 * (screen.width - 10) + ", top=0, left=0, toolbar=no, menubar=yes,
	 * scrollbars=yes, resizable=yes,location=no, status=no")
	 */
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
	}
})
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
