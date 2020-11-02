$package("chis.application.tr.script.pmh");

$import("chis.script.BizTableFormView");

chis.application.tr.script.pmh.TumourPastMedicalHistoryForm = function(cfg){
	chis.application.tr.script.pmh.TumourPastMedicalHistoryForm.superclass.constructor.apply(this,[cfg]);
	this.fldDefaultWidth=200;
}

Ext.extend(chis.application.tr.script.pmh.TumourPastMedicalHistoryForm,chis.script.BizTableFormView,{
	getSaveRequest:function(savaData){
		savaData.empiId = this.exContext.args.empiId;
		return savaData;
	}
});