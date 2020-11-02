$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDoseFrequencyNOCycleForm = function(cfg) {
	cfg.colCount = 3;
	cfg.width = 400;
	cfg.labelWidth = 40;
	cfg.autoLoad = false;
	this.schema = "phis.application.cfg.schemas.GY_SYPC3";
	this.entryName = "phis.application.cfg.schemas.GY_SYPC3";
	phis.application.cfg.script.ConfigDoseFrequencyNOCycleForm.superclass.constructor
			.apply(this, [cfg])
			
	//this.on("winShow",this.onWinShow,this)
}
Ext.extend(phis.application.cfg.script.ConfigDoseFrequencyNOCycleForm,
		phis.script.TableForm, {
			onWinShow : function() {
				this.selectRecord;
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
						}
						values[it.id] = v;
					}
				}
				// Ext.apply(this.data, values);
				this.fireEvent("doSave", values);

			}

		})