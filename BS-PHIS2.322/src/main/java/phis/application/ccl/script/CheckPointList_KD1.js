$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckPointList_KD1 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.ccl.script.CheckPointList_KD1.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckPointList_KD1,
		phis.script.SimpleList, {
			onRowClick : function(){
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;//类别ID
				var bwid = record.data.BWID;//部位ID
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkProjectList.requestData.cnd = ['and',['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]],['eq',['$','jgid'],['s',this.mainApp['phis'].phisApp.deptId]]]];
				checkProjectList.requestData.pageNo=1;
				checkProjectList.requestData.pageSize=100;
				checkProjectList.refresh();	
			}
		});