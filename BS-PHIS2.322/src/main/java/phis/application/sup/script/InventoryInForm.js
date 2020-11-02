$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.sup.script.InventoryInForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.InventoryInForm.superclass.constructor.apply(
			this, [ cfg ])
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sup.script.InventoryInForm, phis.script.TableForm,
		{
			onLoadData : function() {
				if (this.getFormData()) {
					if (this.getFormData().PDFS == 1) {
						this.form.getForm().findField("FSMC").setValue("库存管理");
					} else if (this.getFormData().PDFS == 2) {
						this.form.getForm().findField("FSMC").setValue("科室管理");
					} else if (this.getFormData().PDFS == 3) {
						this.form.getForm().findField("FSMC").setValue("台帐管理");
					}
				}
			}
		})