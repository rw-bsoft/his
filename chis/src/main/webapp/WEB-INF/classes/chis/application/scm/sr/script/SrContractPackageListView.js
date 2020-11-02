$package("chis.application.scm.sr.script")
$import("chis.script.BizSimpleListView")
chis.application.scm.sr.script.SrContractPackageListView = function(cfg){
	cfg.autoLoadSchema=false;
	cfg.disablePagingTbr = true;
	cfg.selectFirst = true;
	chis.application.scm.sr.script.SrContractPackageListView.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.scm.sr.script.SrContractPackageListView,chis.script.BizSimpleListView,{
	onRowClick : function(grid, rowIndex, e) {
		this.selectedIndex = rowIndex;
		var selectRecord = grid.store.getAt(rowIndex);
		// 根据选中的行加载其他表的数据
		this.loadItemDetail(selectRecord)
	},
	loadItemDetail : function(selectRecord) {
		var me = this;
		var SPID = selectRecord.get('SPID');
		me.SPID = SPID;
		var BagDetailList = this.opener['refSCMItemList_refModule'];
		BagDetailList.requestData.cnd = ['and',
				['eq', ['$', 'a.SPID'], ['s', SPID]]];
		BagDetailList.refresh();
	}
});