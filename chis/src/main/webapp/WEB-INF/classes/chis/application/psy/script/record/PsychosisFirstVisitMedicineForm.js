$package("chis.application.psy.script.record");

$import("chis.script.BizTableFormView");

chis.application.psy.script.record.PsychosisFirstVisitMedicineForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	cfg.labelAlign = "left";
	cfg.labelWidth = 65;
	cfg.width = 500;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	cfg.colCount = 2;
	chis.application.psy.script.record.PsychosisFirstVisitMedicineForm.superclass.constructor.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onClose, this);
};

Ext.extend(chis.application.psy.script.record.PsychosisFirstVisitMedicineForm, chis.script.BizTableFormView, {
	
			onWinShow : function() {
				this.doNew();
				if (!this.data) {
					this.data = {};
				}
				this.data["phrId"] = this.phrId;
				this.data["visitId"] = this.visitId;
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

			doSave : function() {
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm();
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items;

				Ext.apply(this.data, this.exContext);
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}

						var v = this.data[it.id];
						if (v != null && typeof v == "object") {
							v = v.key;
						}
						var f = form.findField(it.id);
						if (f) {
							v = f.getValue();
						}
						if (v == null || v == "") {
							if (!it.pkey && it["not-null"]) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}
				Ext.apply(this.data, values);
				this.fireEvent("addItem", this.data);
				this.doCancel();
			},

			onClose : function() {
				this.form.getForm().reset();
			}
		});