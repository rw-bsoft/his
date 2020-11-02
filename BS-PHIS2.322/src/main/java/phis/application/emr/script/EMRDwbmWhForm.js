$package("phis.application.emr.script")

$import("phis.script.TableForm")

phis.application.emr.script.EMRDwbmWhForm = function(cfg) {
	phis.application.emr.script.EMRDwbmWhForm.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRDwbmWhForm, phis.script.TableForm, {
	onReady : function() {
		phis.application.emr.script.EMRDwbmWhForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var bmjb = form.findField("BMJB");
		var zf = form.findField("ZF");
		var zgkf = form.findField("ZGKF");
		bmjb.on("select", this.doChangeSjdwbm, this);
		zf.setValue(1);
		zgkf.setValue(0);
	},
	doChangeSjdwbm : function(field) {
		var form = this.form.getForm();
		var sjdwbm = form.findField("SJDWBM");
		if (field.getValue() == 1) {
			sjdwbm.onDisable()
		} else if (field.getValue() == 2) {
			sjdwbm.onEnable()
		}
	}
})
