$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.ConfigBusinessLockForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 100;
	phis.application.cfg.script.ConfigBusinessLockForm.superclass.constructor
			.apply(this, [cfg])
	this.on('loadData', this.afterLoadData, this)
}

Ext.extend(phis.application.cfg.script.ConfigBusinessLockForm,
		phis.script.TableForm, {
			afterLoadData : function() {
				this.form.getForm().findField("HCYW").setDisabled(true);
			}
		})