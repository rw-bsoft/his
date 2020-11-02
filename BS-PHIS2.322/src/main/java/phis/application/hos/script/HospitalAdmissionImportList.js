$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalAdmissionImportList = function(cfg) {
	phis.application.hos.script.HospitalAdmissionImportList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalAdmissionImportList,
		phis.script.SimpleList, {
			doImport : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				this.opener.doCommitImport(r.data);
			},
			doCancel : function() {
				this.getWin().hide();
			}
		});