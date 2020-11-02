$package("phis.application.dpc.script")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")

debugger;
phis.application.dpc.script.DebridementAgreementList = function(cfg) {
	this.initCnd = ["eq", ["$", "empiId"], ["s", cfg.exContext.ids.empiId]];
	phis.application.dpc.script.DebridementAgreementList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(phis.application.dpc.script.DebridementAgreementList,chis.script.BizSimpleListView,{
	loadData : function() {
		this.initCnd = ["eq", ["$", "empiId"], ["s", this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		phis.application.dpc.script.DebridementAgreementList.superclass.loadData.call(this);
	},
	
	onSave : function(entryName, op, json, data) {
		this.refresh();
	},
	
	doPrint : function() {
		debugger;
		var record = this.getSelectedRecord().data;
	  	var url = "resources/phis.prints.jrxml.DebridementAgreement.print?type=" + 1
	     + "&id=" + record.id +"&empiId=" + this.exContext.ids.empiId;
	        url += "&temp=" + new Date().getTime()
        // var win = window
        //     .open(
        //         url,
        //         "",
        //         "height="
        //         + (screen.height-100)
        //         + ", width="
        //         + (screen.width-10)
        //         + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
        // if (Ext.isIE6) {
        //     win.print()
        // } else {
        //     win.onload = function () {
        //         win.print()
        //     }
        // }
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","A5");
		//预览LODOP.PREVIEW();
		//预览LODOP.PRINT();
		//LODOP.PRINT_DESIGN();
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		//预览
		LODOP.PREVIEW();
	},
	
	doRefresh : function() {
		this.refresh();
	}
});
