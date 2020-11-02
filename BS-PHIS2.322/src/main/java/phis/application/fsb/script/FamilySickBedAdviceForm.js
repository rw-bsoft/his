$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FamilySickBedAdviceForm = function(cfg) {
	cfg.colCount = 5;
	cfg.autoLoadData = false;
	phis.application.fsb.script.FamilySickBedAdviceForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedAdviceForm,
		phis.script.TableForm, {
			// 去除form光标跳转
			focusFieldAfter : function() {

			}
		});
