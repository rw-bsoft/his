$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.InventoryForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.InventoryForm.superclass.constructor.apply(
			this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sup.script.InventoryForm, phis.script.TableForm, {
	onLoadData : function() {
		if (this.getFormData()) {
			if (this.getFormData().DJZT == 1) {
				if (this.btn.getText().indexOf("弃审") > -1) {
					return;
				}
				this.btn.setText(this.btn.getText().replace("审核", "弃审"));
			} else if (this.getFormData().DJZT == 0) {
				this.btn.setText(this.btn.getText().replace("弃审", "审核"));
			}
		}
		if (this.getFormData().GLFS == 1) {
			this.form.getForm().findField("FSMC").setValue("库存管理");
		} else if (this.getFormData().GLFS == 2) {
			this.form.getForm().findField("FSMC").setValue("科室管理");
		} else if (this.getFormData().GLFS == 3) {
			this.form.getForm().findField("FSMC").setValue("台帐管理");
		}
	}
})