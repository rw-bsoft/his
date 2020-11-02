$package("chis.application.hy.script.visit");

$import("app.desktop.Module", "chis.script.BizTableFormView");

chis.application.hy.script.visit.HypertensionVisitMedicineForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.labelWidth = 80;
	cfg.width = 600;
	cfg.colCount = 2;
	this.phrId = cfg.phrId;
	this.visitId = cfg.visitId;
	this.record = cfg.record;
	this.backDataField = [{
				"medicineId" : "YPXH"
			}];
	this.backOtherField = [{
				"medicineDosage" : "YPJL"
			}, {
				"medicineUnit" : "JLDW"
			}]
	chis.application.hy.script.visit.HypertensionVisitMedicineForm.superclass.constructor
			.apply(this, [cfg]);
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionMedicine";
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onClose, this);
	Ext.apply(this, { resetButtons : Ext.emptyFn});
};

Ext.extend(chis.application.hy.script.visit.HypertensionVisitMedicineForm,
		chis.script.BizTableFormView, {
			onWinShow : function() {
				if (this.op == "create") {
					this.doNew();
				} else {
					if (!this.data) {
						this.data = {};
					}
					this.data["phrId"] = this.phrId;
					this.data["visitId"] = this.visitId;
					if (this.record && this.op == "update") {
						var form = this.form.getForm();
						var data = this.record.data;
						form.findField("medicineName")
								.setValue(data.medicineName);
						form.findField("medicineFrequency")
								.setValue(data.medicineFrequency);
						form.findField("medicineDosage")
								.setValue(data.medicineDosage);
						form.findField("medicineUnit").setValue(data.medicineUnit);
						form.findField("medicineDate")
								.setValue(data.medicineDate);
						form.findField("totalCount").setValue(data.totalCount);
						form.findField("otherMedicineDesc")
								.setValue(data.otherMedicineDesc);

						this.data.id = this.record.id;
						Ext.apply(this.data, data);
					}
				}
			},

			onSideEffectsFlagChange : function(combo, record, index) {
				var newValue = record.data.key;
				if (newValue == 1) {
					this.form.getForm().findField("sideEffects").enable();
				}
				if (newValue == 2) {
					this.form.getForm().findField("sideEffects").disable();
					this.form.getForm().findField("sideEffects").setValue();
				}
			},

			doCancel : function() {
				this.fireEvent("close");
				this.getWin().hide();
			},

			doCreateMedicine : function() {
				this.op = "create";
				if (this.op == "create") {
					this.doNew();
				}
			},

			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values.medicineId = this.medicineId
				var items = this.schema.items;
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						if (it.dic && values[it.id]) {
							var f = this.form.getForm().findField(it.id);
							if (f) {
								values[it.id + '_text'] = f.getRawValue();
							}
						}
					}
				}
				if (this.op == "create") {
					this.fireEvent("addItem", values);
				} else if (this.op == "update") {
					this.fireEvent("updateItem", values);
				}
			},

			onClose : function() {
				// this.form.getForm().reset();
			}
		});