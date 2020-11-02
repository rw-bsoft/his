$package("chis.application.conf.script.tpv");

$import("chis.application.conf.script.RecordManageYearConfigForm");

chis.application.conf.script.tpv.TumourPatientVisitConfigForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 220
	cfg.colCount = 2;
	chis.application.conf.script.tpv.TumourPatientVisitConfigForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.conf.script.tpv.TumourPatientVisitConfigForm,
		chis.application.conf.script.RecordManageYearConfigForm, {
			saveToServer : function(saveData) {
				this.fireEvent("save", saveData, this);
			}
		});