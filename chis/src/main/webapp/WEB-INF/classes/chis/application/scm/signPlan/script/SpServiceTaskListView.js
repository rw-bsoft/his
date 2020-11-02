$package("chis.application.scm.signPlan.script")
$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")

chis.application.scm.signPlan.script.SpServiceTaskListView = function(cfg) {
	cfg.autoLoadSchema=false;
	cfg.disablePagingTbr = true;
	cfg.selectFirst = true;
	chis.application.scm.signPlan.script.SpServiceTaskListView.superclass.constructor
			.apply(this, [cfg]);	
	
}

Ext.extend(chis.application.scm.signPlan.script.SpServiceTaskListView,
		chis.script.BizSimpleListView, {
	
});