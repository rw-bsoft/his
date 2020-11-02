$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckTypeList_KD2 = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.sslx=1;
	com.bsoft.phis.checkapply.CheckTypeList_KD2.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckTypeList_KD2, com.bsoft.phis.SimpleList, {
			loadData : function(){
				this.requestData.cnd=['eq', ['$', 'sslx'],['i', this.sslx]];
				com.bsoft.phis.checkapply.CheckTypeList_KD2.superclass.loadData.call(this);
			},
			onRowClick : function() {
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;// Àà±ðID
				var checkPointList = this.opener.midiModules["checkPointList"];
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkPointList.requestData.serviceId = "checkApplyService";
				checkPointList.requestData.serviceAction = "getCheckPaintList";
				checkPointList.requestData.lbid=lbid;
				checkPointList.requestData.pageNo=1;
				checkPointList.requestData.pageSize=100;
				checkPointList.refresh();
				checkProjectList.store.removeAll();
			}
		});