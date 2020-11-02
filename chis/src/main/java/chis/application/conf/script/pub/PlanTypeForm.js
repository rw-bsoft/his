$package("chis.application.conf.script.pub")
$import("chis.application.conf.script.SystemConfigUtilForm")
chis.application.conf.script.pub.PlanTypeForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 158
	chis.application.conf.script.pub.PlanTypeForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.pub.PlanTypeForm, chis.application.conf.script.SystemConfigUtilForm, {

			getFormData : function() {
				var values = chis.application.conf.script.pub.PlanTypeForm.superclass.getFormData
						.call(this);
				if (!values) {
					return;
				}
				var rawValues = this.getRawValues(values);
				Ext.apply(values, rawValues);
				return values;
			}

		});