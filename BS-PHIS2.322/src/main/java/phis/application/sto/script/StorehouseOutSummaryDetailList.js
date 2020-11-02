/**
 * 药库入库功能
 * 
 * @author caijy
 */
$package("phis.application.sto.script");
$import("phis.script.SimpleList");

phis.application.sto.script.StorehouseOutSummaryDetailList = function(cfg) {
	cfg.width=800;
	phis.application.sto.script.StorehouseOutSummaryDetailList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseOutSummaryDetailList,
		phis.script.SimpleList, {
			loadData:function(){
			this.requestData.serviceId=this.fullserviceId;
			this.requestData.serviceAction=this.serviceAction;
			phis.application.sto.script.StorehouseOutSummaryDetailList.superclass.loadData.call(this);
			},
			doPrint : function() {
				var r = this.opener.getSelectedRecord();
				var type="1";
				if(this.printtype && this.printtype=="3"){
					type="3"
				}
				this.printtype="1";
				var pages="phis.prints.jrxml.SummaryMedicinesOutOfStorageDetail";
				var url="resources/"+pages+".print?type="+type;
				url += "&ckfs=" + r.get("CKFS");
				url += "&kfsb=" +  this.opener.mainApp.storehouseId;
				url += "&ksrq=" + this.opener.opener.tbar[0].getValue().format('Ymd'); 
				url += "&jsrq=" + this.opener.opener.tbar[2].getValue().format('Ymd'); 
				url += "&temp=" + new Date().getTime();
				if(type=="3"){
					var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}
				else{
				var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					//预览
					LODOP.PREVIEW();
				}
			},
			doExport : function() {
				this.printtype="3";
				this.doPrint();
			}
});