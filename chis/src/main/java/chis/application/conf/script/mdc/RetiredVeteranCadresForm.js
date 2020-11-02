$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.SystemConfigUtilForm")
chis.application.conf.script.mdc.RetiredVeteranCadresForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 220;
	cfg.labelWidth = 130;
	cfg.colCount = 2;
	cfg.autoLoadData = false;
	cfg.saveServiceId = "chis.rvcConfigManageService";
	cfg.saveAction = "saveConfig";
	cfg.loadServiceId = "chis.rvcConfigManageService";
	cfg.loadAction = "queryConfig";
	chis.application.conf.script.mdc.RetiredVeteranCadresForm.superclass.constructor
			.apply(this, [ cfg ])
}
Ext.extend(chis.application.conf.script.mdc.RetiredVeteranCadresForm,
		chis.application.conf.script.SystemConfigUtilForm, {

			onReady : function() {
				var form = this.form.getForm();
				var startMonth = form.findField("RVCStartMonth");
				if (startMonth) {
					startMonth.setEditable(false);
					startMonth.on("select", this.changeManageMonth, this);
				}

				var endMonth = form.findField("RVCEndMonth");
				if (endMonth) {
					endMonth.setEditable(false);
					endMonth.on("select", this.changeManageMonth, this);
				}
				// var planMode = form.findField("planMode")
				// if (planMode) {
				// planMode.on("select", this.planModeChange, this);
				// planMode.on("change", this.planModeChange, this);
				// }
			},

			// planModeChange : function(combo) {
			// this.fireEvent("planModeChange", value);
			// },

			changeManageMonth : function(combo, record, index) {
				var fieldName = combo.getName();
				var form = this.form.getForm();
				if (fieldName == "RVCStartMonth") {
					var endMonth = form.findField("RVCEndMonth");
					if (endMonth) {
						if (index < 1)
							index = combo.getStore().getCount();
						var endRecord = combo.getStore().getAt(index - 1);
						endMonth.setValue({
							"key" : endRecord.get("key"),
							"text" : endRecord.get("text")
						});
					}
				} else {
					var startMonth = form.findField("RVCStartMonth");
					if (startMonth) {
						if (index == combo.getStore().getCount() - 1)
							index = -1;
						var startRecord = combo.getStore().getAt(index + 1);
						startMonth.setValue({
							"key" : startRecord.get("key"),
							"text" : startRecord.get("text")
						});
					}
				}
			}

		});