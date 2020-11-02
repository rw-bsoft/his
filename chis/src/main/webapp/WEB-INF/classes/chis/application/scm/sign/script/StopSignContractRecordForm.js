$package("chis.application.scm.sign.script")
$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.Vtype")

chis.application.scm.sign.script.StopSignContractRecordForm=function(cfg){
	chis.application.scm.sign.script.StopSignContractRecordForm.superclass.constructor.apply(this,[cfg]);
	this.labelWidth=100;
	this.colCount = 2;
}

Ext.extend(chis.application.scm.sign.script.StopSignContractRecordForm,chis.script.BizTableFormView,{
	
});