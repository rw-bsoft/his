$package("phis.application.war.script")

$import("phis.script.TableForm")

phis.application.war.script.WardHerbEntryForm = function(cfg) {
//	cfg.showButtonOnTop = false;
	cfg.colCount = 3;
	cfg.labelWidth = 30;
	phis.application.war.script.WardHerbEntryForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.WardHerbEntryForm, phis.script.TableForm,
		{
		})