$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckPointList_KD2 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	com.bsoft.phis.checkapply.CheckPointList_KD2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckPointList_KD2,
		com.bsoft.phis.SimpleList, {
			onRowClick : function(){
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;//���ID
				var bwid = record.data.BWID;//��λID
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkProjectList.requestData.cnd = ['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]]];
				checkProjectList.requestData.pageNo=1;
				checkProjectList.requestData.pageSize=100;
				checkProjectList.refresh();	
			}
		});