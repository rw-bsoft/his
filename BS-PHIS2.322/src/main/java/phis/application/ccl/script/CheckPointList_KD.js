$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckPointList_KD = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.ccl.script.CheckPointList_KD.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckPointList_KD,
		phis.script.SimpleList, {
			loadData : function(){
				this.requestData.cnd=['and',['eq',['$','a.JGID'],["s",this.mainApp['phis'].phisApp.deptId]],['eq', ['$', 'sslx'],['i', this.sslx]]];
				phis.application.ccl.script.CheckPointList_KD.superclass.loadData.call(this);
			},
			onRowClick : function(){
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;//类别ID
				var bwid = record.data.BWID;//部位ID
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkProjectList.requestData.cnd = ['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]]];
				checkProjectList.requestData.pageNo=1;
				checkProjectList.requestData.pageSize=100;
				checkProjectList.refresh();	
			}
		});