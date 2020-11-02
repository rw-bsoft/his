$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FamilySickBedPatientPlanForm = function(cfg) {
	cfg.colCount = 5;
	phis.application.fsb.script.FamilySickBedPatientPlanForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedPatientPlanForm,
		phis.script.TableForm, {
			// 去除form光标跳转
			focusFieldAfter : function() {

			}
		});
