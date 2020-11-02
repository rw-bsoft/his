$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.ResetBusinessDetailForm = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.sup.script.ResetBusinessDetailForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ResetBusinessDetailForm, phis.script.TableForm,
		{
		})