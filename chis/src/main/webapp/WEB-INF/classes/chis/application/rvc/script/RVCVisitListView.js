$package("chis.application.rvc.script")

$import("chis.script.BizSimpleListView","chis.script.EHRView")

chis.application.rvc.script.RVCVisitListView = function(cfg) {
	chis.application.rvc.script.RVCVisitListView.superclass.constructor.apply(this,
			[ cfg ])
//	this.autoLoadData = false;
}

Ext.extend(chis.application.rvc.script.RVCVisitListView,
		chis.script.BizSimpleListView, {
	onDblClick : function(){
		this.doModify();
	},
	
	doModify : function() {
		var r = this.getSelectedRecord();
		this.empiId = r.get("empiId");
		this.showEhrViewWin();
	},
	
	showEhrViewWin : function() {
		var m = this.midiModules["RVCVisitView"];
		if (!m) {
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : ['R_02'],
						mainApp : this.mainApp,
						empiId : this.empiId
					});
			this.midiModules["RVCVisitView"] = m;
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids["empiId"] = this.empiId;
			m.refresh()
		}
		m.getWin().show();
	}
	
	
})