$package("chis.application.conf.script.mhc")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.mhc.PregnantConfigForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 220;
	cfg.colCount = 2;
	cfg.labelWidth = 120;
	chis.application.conf.script.mhc.PregnantConfigForm.superclass.constructor.apply(this,
			[cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.conf.script.mhc.PregnantConfigForm,
		chis.application.conf.script.SystemConfigUtilForm, {

			saveToServer : function(saveData) {
				this.fireEvent("save", saveData, this);
			},

			onReady : function() {
				var form = this.form.getForm();
				var visitIntervalSame = form.findField("visitIntervalSame");
				visitIntervalSame.on("check", function() {
							this.fireEvent("check", visitIntervalSame.checked,
									this)
						}, this)

				var planMode = form.findField("planMode");
				if (planMode) {
					planMode.on("select", this.changePlanMode, this);
					planMode.on("change", this.changePlanMode, this);
				}
			},

			changePlanMode : function(field) {
				var value = field.getValue();
				var visitIntervalSame = this.form.getForm()
						.findField("visitIntervalSame");

				if (value == '2') {
					visitIntervalSame.setValue("");
					visitIntervalSame.disable();
				} else {
					visitIntervalSame.enable();
					var same = visitIntervalSame.getValue();
					if (same) {
						value = "2";
					}
				}
				this.fireEvent("planModeChange", value);
			},

			onLoadData : function() {
				var planMode = this.form.getForm().findField("planMode");
				this.planMode = planMode.getValue();
				this.changePlanMode(planMode);
			}

		});