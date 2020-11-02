$package("chis.application.psy.script.assessment");

$import("chis.script.BizTableFormView");

chis.application.psy.script.assessment.AnnualAssessmentForm = function(cfg){
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.colCount = "3";
	cfg.fldDefaultWidth = 120;
	cfg.labelWidth = 125;
	chis.application.psy.script.assessment.AnnualAssessmentForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.psychosisAnnualAssessmentService";
	this.saveAction = "savePsyAnnualAssessment";
	this.on("save",this.onSave,this);
};

Ext.extend(chis.application.psy.script.assessment.AnnualAssessmentForm,chis.script.BizTableFormView,{
	doAdd : function(){
		this.fireEvent("add")
	},
	doNew:function(){
		this.initDataId = null;
		chis.application.psy.script.assessment.AnnualAssessmentForm.superclass.doNew.call(this);
	}
	,
	doSave : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
		chis.application.psy.script.assessment.AnnualAssessmentForm.superclass.doSave.call(this);
	},
	
	onSave : function(entryName,op,json,data){
		this.fireEvent("saveAnnual", op,json,data);
	},
	
	setCreateDisOrEnable : function(disable){
	    if (!this.form.getTopToolbar()) {
	          return;
	     }
		var createBtn = this.form.getTopToolbar().items.item(1);
		if(disable){
			createBtn.disable();
		}else{
			createBtn.enable();
		}
	}
});