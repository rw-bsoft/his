$package("chis.application.tr.script.tprc");

$import("chis.script.BizTableFormView");

chis.application.tr.script.tprc.TumourPatientBaseCaseForm = function(cfg){
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount  = 3
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	chis.application.tr.script.tprc.TumourPatientBaseCaseForm.superclass.constructor.apply(this,[cfg]);
	this.on("doNew",this.onDoNew,this);
	//this.on("loadNoData",this.onLoadNoData,this);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientBaseCaseForm,chis.script.BizTableFormView,{
	onDoNew : function(){
		this.initDataId = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		this.empiId = this.exContext.ids.empiId;
		this.data.empiId=this.exContext.ids.empiId;
		this.data.TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		this.onCensusRegisterCheckSelect();
		this.onLiveAddressCheckSelect();
		this.onSmokingCodeSelect();
		this.onTumourFamilyHistorySelect();
	},
	onLoadNoData : function(){
		this.onCensusRegisterCheckSelect();
		this.onLiveAddressCheckSelect();
		this.onSmokingCodeSelect();
		this.onTumourFamilyHistorySelect();
	},
	onLoadData : function(entryName,body){
		this.onCensusRegisterCheckSelect();
		this.onLiveAddressCheckSelect();
		this.onSmokingCodeSelect();
		this.onTumourFamilyHistorySelect();
		//转报上重置一下按钮控制（记录注释时）
		var turnReport = this.exContext.args.turnReport;
		if(turnReport && turnReport == true){
			this.exContext.control.update=true
			this.resetButtons();
		}
	},
	getLoadRequest:function(){
		var body={};
		if(this.empiId){
			body.empiId = this.empiId;
		}
		if(this.initDataId){
			body.TPRCID = this.initDataId;
		}
		if(!body.empiId && !body.pkey){
			return null;
		}
		return body;
	},
	saveToServer:function(saveData){
		saveData.TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		saveData.empiId = this.exContext.ids.empiId;
		this.fireEvent("save",saveData);
	},
	onReady : function(){
		chis.application.tr.script.tprc.TumourPatientBaseCaseForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		
		var censusRegisterCheckFld = frm.findField("censusRegisterCheck");
		if(censusRegisterCheckFld){
			censusRegisterCheckFld.on("select",this.onCensusRegisterCheckSelect,this);
		}
		
		var liveAddressCheckFld = frm.findField("liveAddressCheck");
		if(liveAddressCheckFld){
			liveAddressCheckFld.on("select",this.onLiveAddressCheckSelect,this);
		}
		
		var smokingCodeFld = frm.findField("smokingCode");
		if(smokingCodeFld){
			smokingCodeFld.on("select",this.onSmokingCodeSelect,this);
		}
		
		var tumourFamilyHistoryFld = frm.findField("tumourFamilyHistory");
		if(tumourFamilyHistoryFld){
			tumourFamilyHistoryFld.on("select",this.onTumourFamilyHistorySelect,this);
		}
	},
	onCensusRegisterCheckSelect : function(){
		var frm = this.form.getForm();
		var censusRegisterCheckFld = frm.findField("censusRegisterCheck");
		var noCheckReasonFld = frm.findField("noCheckReason");
		if(!censusRegisterCheckFld || !noCheckReasonFld){
			return;
		}
		var censusRegisterCheckVal = censusRegisterCheckFld.getValue();
		if(censusRegisterCheckVal == "1"){
			noCheckReasonFld.enable();
		}else{
			noCheckReasonFld.disable();
		}
	},
	onLiveAddressCheckSelect : function(){
		var frm = this.form.getForm();
		var liveAddressCheckFld = frm.findField("liveAddressCheck");
		var LANocheckReasonFld = frm.findField("LANocheckReason");
		if(!liveAddressCheckFld || !LANocheckReasonFld){
			return;
		}
		var liveAddressCheckVal = liveAddressCheckFld.getValue();
		if(liveAddressCheckVal=='1'){
			LANocheckReasonFld.enable();
		}else{
			LANocheckReasonFld.disable();
		}
	},
	onSmokingCodeSelect : function(){
		var frm = this.form.getForm();
		var smokingCodeFld = frm.findField("smokingCode");
		var smokingStartAgeFld = frm.findField("smokingStartAge");
		var stopSmokingAgeFld = frm.findField("stopSmokingAge");
		var smokingNumberFld = frm.findField("smokingNumber");
		if(!smokingCodeFld || !smokingStartAgeFld || !stopSmokingAgeFld || !smokingNumberFld){
			return;
		}
		var smokingCode = smokingCodeFld.getValue();
		if(smokingCode == "1" || smokingCode == "2"){
			smokingStartAgeFld.enable();
			smokingNumberFld.enable();
		}else if(smokingCode == "3"){
			smokingStartAgeFld.enable();
			stopSmokingAgeFld.enable();
			smokingNumberFld.enable();
		}else{
			smokingStartAgeFld.disable();
			stopSmokingAgeFld.disable();
			smokingNumberFld.disable();
		}
	},
	onTumourFamilyHistorySelect : function(){
		var frm = this.form.getForm();
		var tumourFamilyHistoryFld = frm.findField("tumourFamilyHistory");
		var relationshipCodeFld = frm.findField("relationshipCode");
		var tumourTypeFld = frm.findField("tumourType");
		if(!tumourFamilyHistoryFld || !relationshipCodeFld || !tumourTypeFld){
			return;
		}
		var tumourFamilyHistoryVal = tumourFamilyHistoryFld.getValue();
		if(tumourFamilyHistoryVal == 'y'){
			relationshipCodeFld.enable();
			relationshipCodeFld.allowBlank = false;
			relationshipCodeFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>"+relationshipCodeFld.fieldLabel+":</span>");
			tumourTypeFld.enable();
			tumourTypeFld.allowBlank = false;
			tumourTypeFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>"+tumourTypeFld.fieldLabel+":</span>");
		}else{
			relationshipCodeFld.disable();
			relationshipCodeFld.allowBlank = true;
			relationshipCodeFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update(relationshipCodeFld.fieldLabel+":");
			tumourTypeFld.disable();
			tumourTypeFld.allowBlank = true;
			tumourTypeFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update(tumourTypeFld.fieldLabel+":");
		}
	}
});