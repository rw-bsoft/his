$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.query.QueryModule")

chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionForm = function(cfg){
	cfg.autoLoadSchema = false;
    cfg.isCombined = true;
    cfg.autoLoadData = false;
    cfg.showButtonOnTop = true;
    cfg.autoWidth = true;
    cfg.colCount=2;
	chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionForm.superclass.constructor.apply(this,[cfg]);
	this.labelWidth = 112;
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHighRiskCriterionForm,chis.script.BizTableFormView,{
	getHighRiskType : function(){
		var frm = this.form.getForm();
		var highRiskTypeFld = frm.findField("highRiskType");
		var highRiskTypeKey = "";
		var highRiskTypeText = "";
		if(highRiskTypeFld){
			highRiskTypeKey = highRiskTypeFld.getValue();
			highRiskTypeText = highRiskTypeFld.getRawValue();
		}
		return {key:highRiskTypeKey,text:highRiskTypeText};
	},
	loadData : function(){
		
	}
});