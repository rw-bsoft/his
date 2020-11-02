$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.SystemConfigUtilForm")
chis.application.conf.script.mdc.OldPeopleForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 220;
	cfg.labelWidth = 130;
	cfg.colCount = 2;
	chis.application.conf.script.mdc.OldPeopleForm.superclass.constructor.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.conf.script.mdc.OldPeopleForm, chis.application.conf.script.SystemConfigUtilForm, {

			onReady : function() {
				var form = this.form.getForm();
				form.findField("oldPeopleVisitIntervalSame").on("check",
						this.visitIntervalChange, this);

				var startMonth = form.findField("oldPeopleStartMonth");
				if (startMonth) {
					startMonth.setEditable(false);
					startMonth.on("select", this.changeManageMonth, this);
				}

				var endMonth = form.findField("oldPeopleEndMonth");
				if (endMonth) {
					endMonth.setEditable(false);
					endMonth.on("select", this.changeManageMonth, this);
				}
				var planMode = form.findField("planMode")
				if (planMode) {
					planMode.on("select", this.planModeChange, this);
					planMode.on("change", this.planModeChange, this);
				}
			},

			visitIntervalChange : function(combo) {
				var value = combo.getValue();
				this.fireEvent("visitIntervalChange", value, this.planMode);
			},

			planModeChange : function(combo) {
				var value = combo.getValue();
				this.planMode = value;
				var oldPeopleVisitIntervalSame = this.form.getForm()
						.findField("oldPeopleVisitIntervalSame");

				if (value == '2') {
					oldPeopleVisitIntervalSame.setValue("");
					oldPeopleVisitIntervalSame.disable();
				} else {
					oldPeopleVisitIntervalSame.enable();
					var same = oldPeopleVisitIntervalSame.getValue();
					if (same) {
						value = "2";
					}
				}
				this.fireEvent("planModeChange", value);
			},

			doSave : function() {
				if (!this.validate()) {
					return
				}
				this.fireEvent("saveAll", this.getFormData());
			},

			changeManageMonth : function(combo, record, index) {
				var fieldName = combo.getName();
				var form = this.form.getForm();
				if (fieldName == "oldPeopleStartMonth") {
					var endMonth = form.findField("oldPeopleEndMonth");
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
					var startMonth = form.findField("oldPeopleStartMonth");
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
			},

			onLoadData : function() {
				if (!this.readOnly) {
					var planMode = this.form.getForm().findField("planMode");
					this.planMode = planMode.getValue();
					var oldPeopleVisitIntervalSame = this.form.getForm()
							.findField("oldPeopleVisitIntervalSame");
					if (oldPeopleVisitIntervalSame)
						this.visitIntervalChange(oldPeopleVisitIntervalSame)
				}
			}
		});