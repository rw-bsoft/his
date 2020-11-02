$package("chis.application.scm.sr.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")

chis.application.scm.sr.script.SrContractPersonsListView = function(cfg) {
	cfg.autoLoadSchema=false;
//	cfg.autoLoadData=false;
//	cfg.disableContextMenu=true;
	cfg.selectFirst = true;
	chis.application.scm.sr.script.SrContractPersonsListView.superclass.constructor
			.apply(this, [cfg]);		
}

Ext.extend(chis.application.scm.sr.script.SrContractPersonsListView,
		chis.script.BizSimpleListView, {
	onRowClick : function(grid, rowIndex, e) {
		this.selectedIndex = rowIndex;
		var selectRecord = grid.store.getAt(rowIndex);
		var SCID = selectRecord.get('SCID');
		cnd = ['and',
				['eq', ['$', 'a.SCID'], ['s', SCID]]];
		this.fireEvent("loadTaskDetail",cnd);
	},
	loadData : function() {
		if(this.opener.title && this.opener.title == "履约服务"){
			this.initCnd = ["eq", ["$", "a.favoreeEmpiId"],["s", this.exContext.empiData.empiId]];
			this.requestData.cnd = this.initCnd;
		}
		chis.application.scm.sr.script.SrContractPersonsListView.superclass.loadData.call(this);
	}
//	loadBagDetail : function(selectRecord) {
//		var me = this;
//		var SCID = selectRecord.get('SCID');
//		me.SCID = SCID;
//		var BagDetailList = this.opener['refSCMBagList_refModule'];
//		BagDetailList.requestData.cnd = ['and',
//				['eq', ['$', 'a.SCID'], ['s', SCID]]];
//		BagDetailList.refresh();
//	}
	});