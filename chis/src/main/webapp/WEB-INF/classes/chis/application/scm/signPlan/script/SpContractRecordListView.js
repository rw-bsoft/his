$package("chis.application.scm.signPlan.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")

chis.application.scm.signPlan.script.SpContractRecordListView = function(cfg) {
	cfg.autoLoadSchema=false;
//	cfg.autoLoadData=false;
//	cfg.disableContextMenu=true;
	cfg.selectFirst = true;
	chis.application.scm.signPlan.script.SpContractRecordListView.superclass.constructor
			.apply(this, [cfg]);	
	
}

Ext.extend(chis.application.scm.signPlan.script.SpContractRecordListView,
		chis.script.BizSimpleListView, {
	onRowClick : function(grid, rowIndex, e) {
		this.selectedIndex = rowIndex;
		var selectRecord = grid.store.getAt(rowIndex);
		// 根据选中的行加载其他表的数据
		this.loadBagDetail(selectRecord)
	},
	loadBagDetail : function(selectRecord) {
		var me = this;
		var SCID = selectRecord.get('SCID');
		me.SCID = SCID;
		var BagDetailList = this.opener['refSCMBagList_refModule'];
		BagDetailList.requestData.cnd = ['and',
				['eq', ['$', 'a.SCID'], ['s', SCID]]];
		BagDetailList.refresh();
	}
});