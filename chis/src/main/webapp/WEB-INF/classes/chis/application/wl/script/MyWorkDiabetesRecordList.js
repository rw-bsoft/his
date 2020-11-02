$package("chis.application.wl.script");
$import("chis.script.BizSimpleListView");

chis.application.wl.script.MyWorkDiabetesRecordList = function(cfg) {
	this.initCnd = cfg.cnds
	chis.application.wl.script.MyWorkDiabetesRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkDiabetesRecordList, chis.script.BizSimpleListView, {
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	doCreateByEmpiId : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return
		}
		this.empiId = r.get("empiId")
		this.showEhrViewWin()
	}
	,
	onDblClick:function(){
		this.doCreateByEmpiId()
	}
	,
	showEhrViewWin : function() {
		var m = this.midiModules["ehrView"];
		if (!m) {
			$import("chis.script.EHRView");
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : ['D_01', 'D_02', 'D_03', 'D_05',
								'D_04'],
						mainApp : this.mainApp,
						empiId : this.empiId,
						activeTab : 0
					});
			this.midiModules["ehrView"] = m;
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids = {};
			m.exContext.ids["empiId"] = this.empiId;
			m.activeTab = 0
			m.refresh();
		}
		m.getWin().show();
	}
});