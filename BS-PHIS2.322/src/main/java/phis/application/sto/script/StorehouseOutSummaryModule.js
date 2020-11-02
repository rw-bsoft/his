/**
 * 药品出库汇总
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehouseOutSummaryModule = function(cfg) {
	phis.application.sto.script.StorehouseOutSummaryModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseOutSummaryModule,
		phis.script.SimpleList, {
			loadData:function(){
			this.requestData.serviceId=this.fullserviceId;
			this.requestData.serviceAction=this.serviceAction;
			this.requestData.body={"KSRQ":this.opener.tbar[0].getValue().format('Ymd'),"JSRQ":this.opener.tbar[2].getValue().format('Ymd')};
			phis.application.sto.script.StorehouseOutSummaryModule.superclass.loadData.call(this);
			},
			//出库汇总页面
			doLook:function(){
				var module = this.createModule("ckhzmx", this.refList)
				var r = this.getSelectedRecord();
				if(r==null){
				return;
				}
				var body = {
					"FSID" : r.get("CKFS"),
					"KSRQ" : this.opener.tbar[0].getValue().format('Ymd'),
					"JSRQ" : this.opener.tbar[2].getValue().format('Ymd'),
					"pageNo" : 1,
					"pageSize" : 25
				}
				module.requestData.body=body;
				module.opener = this;
				var _win = module.getWin();
				_win.add(module.initPanel());
				_win.show();
				module.refresh();
			},
			onDblClick : function() {
				this.doLook();
			},
			doPrint : function() {
				var pages="phis.prints.jrxml.SummaryMedicinesOutOfStorage";
				var type="1";
				if(this.printtype && this.printtype=="3"){
					type="3";
					this.printtype="1";
				}
				var url="resources/"+pages+".print?type="+type;
				url += "&kfsb=" + this.mainApp['phis'].storehouseId;
				url += "&ksrq=" + this.opener.tbar[0].getValue().format('Ymd'); 
				url += "&jsrq=" + this.opener.tbar[2].getValue().format('Ymd'); 
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
				LODOP.PREVIEW();// 预览
				}
			},
			doExport : function() {
				this.printtype="3";
				this.doPrint();
			}
});