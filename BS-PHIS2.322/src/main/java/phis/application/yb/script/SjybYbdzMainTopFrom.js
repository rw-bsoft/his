$package("phis.application.yb.script");
$import("phis.script.TableForm");
phis.application.yb.script.SjybYbdzMainTopFrom = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	this.width = "20"
	phis.application.yb.script.SjybYbdzMainTopFrom.superclass.constructor.apply(this,[cfg]);
};
Ext.extend(phis.application.yb.script.SjybYbdzMainTopFrom, phis.script.TableForm,{
	
});