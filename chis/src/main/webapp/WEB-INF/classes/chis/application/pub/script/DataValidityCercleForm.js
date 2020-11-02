$package("chis.application.pub.script")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.pub.script.DataValidityCercleForm = function(cfg) {
	cfg.labelWidth = 200;
	cfg.fldDefaultWidth = 200;
	cfg.autoFieldWidth = false;
	chis.application.pub.script.DataValidityCercleForm.superclass.constructor.apply(this,
			[cfg])
	this.colCount = 2;
	this.saveServiceId = "chis.gpsDataValidityService";
	this.saveAction = "saveValidityCercle";
	this.loadServiceId = "chis.gpsDataValidityService";
	this.loadAction = "getValidityCercle"
}
Ext.extend(chis.application.pub.script.DataValidityCercleForm,
		chis.application.conf.script.SystemConfigUtilForm, {
		})