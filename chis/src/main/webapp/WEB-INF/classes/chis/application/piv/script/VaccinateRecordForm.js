$package("chis.application.piv.script")
$import("util.Accredit", "chis.script.BizTableFormView")
chis.application.piv.script.VaccinateRecordForm = function(cfg) {
	cfg.autoFieldWidth = false;
	chis.application.piv.script.VaccinateRecordForm.superclass.constructor.apply(this, [cfg])
	this.entryName="chis.application.piv.schemas.PIV_VaccinateRecord";
	this.fldDefaultWidth = 180 ;
}
Ext.extend(chis.application.piv.script.VaccinateRecordForm, chis.script.BizTableFormView, {

});