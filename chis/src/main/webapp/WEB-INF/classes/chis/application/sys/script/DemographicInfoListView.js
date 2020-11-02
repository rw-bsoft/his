$package("chis.application.conf.script.admin")

$import("app.modules.list.SimpleListView")

chis.application.conf.script.admin.DemographicInfoListView = function(cfg) {
	cfg.xy = [150, 100]
	cfg.autoLoadData = true
	cfg.autoLoadSchema = true
	this.initCnd = ['eq',['$','b.operType'],['s','3']]
	chis.application.conf.script.admin.DemographicInfoListView.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.conf.script.admin.DemographicInfoListView, app.modules.list.SimpleListView, {});