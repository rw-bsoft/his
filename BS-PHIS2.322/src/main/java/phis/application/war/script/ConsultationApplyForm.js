$package("phis.application.war.script");

//$import("app.biz.BizTableFormView");
$import("phis.script.TableForm");

phis.application.war.script.ConsultationApplyForm = function(cfg){
	cfg.colCount = 1;
	phis.application.war.script.ConsultationApplyForm.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(phis.application.war.script.ConsultationApplyForm,phis.script.TableForm,{
	getKSDM : function(){
		var frm = this.form.getForm();
		var ksdmFld = frm.findField("KSDM");
		return ksdmFld.getValue();
	},
	
	getZYH : function(){
		var frm = this.form.getForm();
		var zyhFld = frm.findField("ZYH");
		return zyhFld.getValue();
	}
});