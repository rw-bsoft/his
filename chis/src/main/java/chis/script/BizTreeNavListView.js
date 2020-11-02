$package("chis.script");

$import("app.modules.list.TreeNavListView");

chis.script.BizTreeNavListView = function(cfg) {
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizListCommon);
	cfg.listServiceId = cfg.listServiceId || "chis.simpleQuery"
	cfg.removeServiceId = cfg.removeServiceId || "chis.simpleRemove"
	chis.script.BizTreeNavListView.superclass.constructor.apply(this, [cfg]);

};

Ext.extend(chis.script.BizTreeNavListView, app.modules.list.TreeNavListView, {

});