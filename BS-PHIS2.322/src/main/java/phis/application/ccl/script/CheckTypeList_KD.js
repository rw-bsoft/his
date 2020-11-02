$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckTypeList_KD = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.sslx=1;
	phis.application.ccl.script.CheckTypeList_KD.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckTypeList_KD, phis.script.SimpleList, {
			loadData : function(){
				this.requestData.cnd=['and',['eq',['$','a.JGID'],["s",this.mainApp['phis'].phisApp.deptId]],['eq', ['$', 'sslx'],['i', this.sslx]]];
				phis.application.ccl.script.CheckTypeList_KD.superclass.loadData.call(this);
			},
			onRowClick : function() {
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;// 类别ID
				var checkPointList = this.opener.midiModules["checkPointList"];
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkPointList.requestData.serviceId = "phis.checkApplyService";
				checkPointList.requestData.serviceAction = "getCheckPaintList";
				checkPointList.requestData.lbid=lbid;
				checkPointList.requestData.pageNo=1;
				checkPointList.requestData.pageSize=100;
				checkPointList.refresh();
				checkProjectList.store.removeAll();
			}
		});