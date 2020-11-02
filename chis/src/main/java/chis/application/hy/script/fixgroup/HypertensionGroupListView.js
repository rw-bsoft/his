// @@ 评估管理列表。
$package("chis.application.hy.script.fixgroup");

$import("chis.script.BizSimpleListView","chis.script.EHRView");

chis.application.hy.script.fixgroup.HypertensionGroupListView = function(cfg) {
	cfg.initCnd = cfg.cnds || ['eq', ['$', 'd.status'], ['s', '0']];
	chis.application.hy.script.fixgroup.HypertensionGroupListView.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.fixgroup.HypertensionGroupListView, chis.script.BizSimpleListView, {
	doModify : function() {
		var r = this.getSelectedRecord();
		this.empiId = r.get("empiId")
		this.showEhrViewWin();
	},

	showEhrViewWin : function() {
		var m = this.midiModules["fixGroup_ehrView"];
		var visitModule = ['C_01', 'C_02', 'C_03', 'C_05', 'C_04'];
//		if (this.mainApp.exContext.hypertensionType == 'paper') {
//			visitModule = ['C_01', 'C_02', 'C_03_HTML', 'C_05', 'C_04'];
//		}
		if (!m) {
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : visitModule,
						mainApp : this.mainApp,
						empiId : this.empiId,
						activeTab : 1
					});
			this.midiModules["fixGroup_ehrView"] = m;
			m.exContext.ids["empiId"] = this.empiId;
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids["empiId"] = this.empiId;
			m.refresh();
		}
		m.getWin().show();
	},
	
	onDblClick : function() {
		this.doModify();
	}
});