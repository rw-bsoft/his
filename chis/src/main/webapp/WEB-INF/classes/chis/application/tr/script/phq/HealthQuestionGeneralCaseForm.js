$package("chis.application.tr.script.phq")

$import("chis.script.BizTableFormView","chis.script.util.Vtype")

chis.application.tr.script.phq.HealthQuestionGeneralCaseForm = function(cfg){
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount=4;
	cfg.labelWidth = 80;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	chis.application.tr.script.phq.HealthQuestionGeneralCaseForm.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.phq.HealthQuestionGeneralCaseForm,chis.script.BizTableFormView,{
	loadData:function(){
		
	},
	doSave: function(){
		Ext.apply(this.data,this.exContext.args);
		var values = this.getFormData();
		if(!values){
			return;
		}
		Ext.apply(this.data,values);
		this.data.gcId=this.initDataId;
		this.fireEvent("save",this.data);
	},
	onReady : function(){
		var frm = this.form.getForm();
		var surveyUserFld = frm.findField("surveyUser");
		if(surveyUserFld){
			surveyUserFld.on("select", this.changeSurveyUnit, this);
		}
		chis.application.tr.script.phq.HealthQuestionGeneralCaseForm.superclass.onReady.call(this);
	},
	changeSurveyUnit : function(combo, node) {
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method:"execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setSurveyUnit(result.json.manageUnit)
	},
	setSurveyUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("surveyUnit");
		if (!combox) {
			return;
		}
		if (!manageUnit) {
			combox.enable();
			combox.setValue({
						key : "",
						text : ""
					});
			return;
		}
		combox.setValue(manageUnit)
		combox.disable();
	}
});