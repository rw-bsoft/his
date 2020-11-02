$package("phis.application.emr.script")

$import("phis.script.TableForm")

phis.application.emr.script.EMRQxbmWhForm = function(cfg) {
	phis.application.emr.script.EMRQxbmWhForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRQxbmWhForm, phis.script.TableForm, {
	onReady : function() {
		phis.application.emr.script.EMRQxbmWhForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var dwbm = form.findField("DWBM");
//		dwbm.setValue(this.DWBM);
//		this.opener
//		this = this.opener
		console.debug(this.opener)
	}
})
