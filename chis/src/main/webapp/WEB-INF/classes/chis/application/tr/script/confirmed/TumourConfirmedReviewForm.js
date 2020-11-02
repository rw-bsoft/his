$package("chis.application.tr.script.confirmed")

$import("chis.script.BizTableFormView")

chis.application.tr.script.confirmed.TumourConfirmedReviewForm = function(cfg){
	cfg.autoLoadSchema = false;
    cfg.autoLoadData = false;
	chis.application.tr.script.confirmed.TumourConfirmedReviewForm.superclass.constructor.apply(this,[cfg]);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedReviewForm,chis.script.BizTableFormView,{
	getLoadRequest : function(){
		var body = {};
		body.TERID = this.initDataId || '';
		Ext.apply(body,this.exContext.args);
		return body;
	},
	onLoadData : function(entryName,body){
		this.exContext.control = body[this.entryName+"_control"];
		this.resetButtons(); 
	},
	getSaveRequest : function(saveData){
		Ext.apply(saveData,this.exContext.args);
		saveData.assessment  = "1";
		return saveData;
	},
	onReady : function(){
		chis.application.tr.script.confirmed.TumourConfirmedReviewForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		var assessStageFld = frm.findField("assessStage");
		if(assessStageFld){
			assessStageFld.on("change",this.onAssessStageChange,this);
		}
	},
	onAssessStageChange : function(){
		var frm = this.form.getForm();
		var assessStageFld = frm.findField("assessStage");
		if(!assessStageFld){
			return;
		}
		var asv = assessStageFld.getValue();
		var assessTFld = frm.findField("assessT") ;
		var assessNFld = frm.findField("assessN") ;
		var assessMFld = frm.findField("assessM") ;
		if(asv == '2'){
			if(assessTFld){
				assessTFld.setValue({key:'无',text:'无'});
				assessTFld.disable();
			}
			if(assessNFld){
				assessNFld.setValue({key:'0',text:'0'});
				assessNFld.disable();
			}
			if(assessMFld){
				assessMFld.setValue({key:'0',text:'0'});
				assessMFld.disable();
			}
		}else if(asv == '6'){
			if(assessTFld){
				assessTFld.setValue();
				assessTFld.disable();
			}
			if(assessNFld){
				assessNFld.setValue();
				assessNFld.disable();
			}
			if(assessMFld){
				assessMFld.setValue();
				assessMFld.disable();
			}
		}else{
			if(assessTFld){
				assessTFld.setValue();
				assessTFld.enable();
			}
			if(assessNFld){
				assessNFld.setValue();
				assessNFld.enable();
			}
			if(assessMFld){
				assessMFld.setValue();
				assessMFld.enable();
			}
		}
	}
});