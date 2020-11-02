$package("chis.application.pd.script.dbgs")

$import("app.modules.list.SimpleListView")

chis.application.pd.script.dbgs.DiabetesGroupStandardListView = function(cfg) {
	chis.application.pd.script.dbgs.DiabetesGroupStandardListView.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.pd.script.dbgs.DiabetesGroupStandardListView,
		app.modules.list.SimpleListView, {
		});