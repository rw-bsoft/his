$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyCheckInPrintForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.pha.script.PharmacyCheckInPrintForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyCheckInPrintForm, phis.script.TableForm, {
			addJe : function(jhje, lsje) {
				var form = this.form.getForm();
				if (form) {
					form.findField("LSHJ").setValue(lsje);
					form.findField("JHHJ").setValue(jhje);
				}
			}
			,
			doNew:function(){
			var form = this.form.getForm();
			if(form){
			form.findField("LSHJ").setValue(0);
			form.findField("JHHJ").setValue(0);
			}
			}
		})