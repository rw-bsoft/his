$package("chis.application.dbs.script.risk")

$import("chis.script.BizSimpleListView")

chis.application.dbs.script.risk.DiabetesRiskVisitListView = function(cfg) {
//	cfg.initCnd = ['eq',['$','a.estimateType'],['s','1']]
	chis.application.dbs.script.risk.DiabetesRiskVisitListView.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.dbs.script.risk.DiabetesRiskVisitListView,
		chis.script.BizSimpleListView, {
		doVisit:function(){
				var r = this.grid.getSelectionModel().getSelected()
				if(r == null){
					return;
				}
				var empiId = r.get("empiId")
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['D_07']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["DiabetesRiskVisitListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["DiabetesRiskVisitListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
			}
			,
			onDblClick:function(grid,index,e){
				this.doVisit()
			}
		});