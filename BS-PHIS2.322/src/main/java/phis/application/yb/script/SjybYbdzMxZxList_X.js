$package("phis.application.yb.script");
$import("phis.script.SimpleList");

phis.application.yb.script.SjybYbdzMxZxList_X = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.yb.script.SjybYbdzMxZxList_X.superclass.constructor.apply(this,[cfg]);
};
Ext.extend(phis.application.yb.script.SjybYbdzMxZxList_X, phis.script.SimpleList, {
	addData : function(json) {
		this.store.add(new Ext.data.Record(json));
	}
})