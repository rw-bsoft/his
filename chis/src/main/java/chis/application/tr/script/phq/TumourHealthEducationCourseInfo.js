$package("chis.application.tr.script.phq");

$import("chis.script.BizTableFormView");

chis.application.tr.script.phq.TumourHealthEducationCourseInfo = function(cfg){
	cfg.colCount = 1;
	chis.application.tr.script.phq.TumourHealthEducationCourseInfo.superclass.constructor.apply(this,[cfg]);
	this.autoLoadData = false;
}

Ext.extend(chis.application.tr.script.phq.TumourHealthEducationCourseInfo,chis.script.BizTableFormView,{
	getModalityValue : function(){
		var frm = this.form.getForm();
		var modalityFld = frm.findField("modality");
		var mv = "";
		if(modalityFld){
			mv = modalityFld.getValue();
		}
		return mv;
	}
});