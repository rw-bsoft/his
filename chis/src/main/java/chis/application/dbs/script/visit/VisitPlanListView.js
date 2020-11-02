$package("chis.application.dbs.script.visit")

$import("app.modules.list.SimpleListView")

chis.application.dbs.script.visit.VisitPlanListView = function(cfg) {
	chis.application.dbs.script.visit.VisitPlanListView.superclass.constructor.apply(this, [cfg]);
	this.entryName = "MDC_VisitPlan"
}

Ext.extend(chis.application.dbs.script.visit.VisitPlanListView, app.modules.list.SimpleListView, {
			onDblClick : function(grid, index, e) {
				var r = grid.getSelectionModel().getSelected()
				if (r.data.planType == "2") {
					$import("chis.application.dbs.script.visit.DiabetesModule")
					var cfg = {}
					cfg.activateId = "MDC_DiabetesVisitFormView"
					cfg.canWriteVisit = true
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "moduleConfigLocator",
								id : "D11"
							})
					cfg.actions = result.json.body.actions

					var planDate = r.data.planDate
					planDate = planDate.substring(0, 10)
					cfg.planDate = planDate
					cfg.phrId = r.data.phrId
					var m = new chis.application.dbs.script.visit.DiabetesModule(cfg)
					m.getWin().show()
				}
			}
		});