$package("chis.application.wl.script");
$import("chis.script.BizSimpleListView");

chis.application.wl.script.MyWorkHypertensionRecordList = function(cfg) {
	this.initCnd = cfg.cnds 
	chis.application.wl.script.MyWorkHypertensionRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkHypertensionRecordList, chis.script.BizSimpleListView, {
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
						initModules : ['C_01', 'C_02', 'C_03', 'C_05',
								'C_04'],
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