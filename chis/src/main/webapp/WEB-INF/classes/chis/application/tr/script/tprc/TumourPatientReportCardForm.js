$package("chis.application.tr.script.tprc");

$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.query.QueryModule")

chis.application.tr.script.tprc.TumourPatientReportCardForm = function(cfg){
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount  = 3
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	chis.application.tr.script.tprc.TumourPatientReportCardForm.superclass.constructor.apply(this,[cfg]);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientReportCardForm,chis.script.BizTableFormView,{
	loadData : function() {
		this.loadServiceId = "chis.tumourPatientReportCardService";
		this.loadAction = "initializeTPRCForm";
		this.empiId = this.exContext.ids.empiId;
		this.initDataId = this.exContext.ids.TPRCID || this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		this.highRiskType = this.exContext.args.highRiskType;
		chis.application.tr.script.tprc.TumourPatientReportCardForm.superclass.loadData.call(this);
		var form = this.form.getForm();
		form.findField("phrId").setValue(this.exContext.ids.phrId);
		
		this.onIsDeathSelect();
	},
	getLoadRequest:function(){
		var body={};
		if(this.empiId){
			body.empiId = this.empiId;
		}
		if(this.initDataId){
			body.TPRCID = this.initDataId;
		}
		if(this.highRiskType){
			body.highRiskType = this.highRiskType;
		}
		if(!body.empiId && !body.pkey){
			return null;
		}
		if(this.exContext.ids.phrId){
			body.phrId = this.exContext.ids.phrId
		}
		return body;
	},
	
	onLoadData : function(entryName,body){
		this.aop = body.op;
		this.fireEvent("TPRCFormLoad",entryName,body);
		//转报上重置一下按钮控制（记录注释时）
		var turnReport = this.exContext.args.turnReport;
		if(turnReport && turnReport == true){
			this.exContext.control.update=true
			this.resetButtons();
		}
	},
	
	saveToServer:function(saveData){
		saveData.empiId = this.empiId;
		this.fireEvent("save",saveData);
	},
	
	onReady:function(){
		chis.application.tr.script.tprc.TumourPatientReportCardForm.superclass.onReady.call(this);
		
		var frm = this.form.getForm();
		var isDeathFld = frm.findField("isDeath");
		if(isDeathFld){
			isDeathFld.on("select",this.onIsDeathSelect,this);
		}
		var reportDoctorFld = frm.findField("reportDoctor");
		if(reportDoctorFld){
			reportDoctorFld.on("select",this.onReportDoctorSelect,this);
		}
		var tumourNameFld = frm.findField("tumourName");
		if (tumourNameFld) {
			tumourNameFld.on("lookup", this.doTNQuery, this);
			tumourNameFld.on("clear", this.doTNClear, this);
			tumourNameFld.validate();
		}
	},
	doTNQuery : function(field){
		if (!field.disabled) {
			var tumourNameQuery = this.midiModules["tumourNameQuery"];
			if (!tumourNameQuery) {
				tumourNameQuery = new chis.script.util.query.QueryModule(
						{
							title : "肿瘤诊断名称选择",
							autoLoadSchema : true,
							isCombined : true,
							autoLoadData : false,
							mutiSelect : false,
							queryCndsType : "1",
							entryName : "chis.application.his.schemas.GY_JBBMQuery",
							buttonIndex : 3,
							selectFormColCount:3,
							itemHeight:125
						});
				this.midiModules["tumourNameQuery"] = tumourNameQuery;
			}
			tumourNameQuery.on("recordSelected", function(r) {
						if (!r) {
							return;
						}
						var frmData = r[0].data;
						var frm = this.form.getForm();
						var tumourNameFld = frm.findField("tumourName");
						if(tumourNameFld){
							tumourNameFld.setValue(frmData.JBMC)
						}
						var ICD10CodeFld = frm.findField("ICD10Code");
						if(ICD10CodeFld){
							ICD10CodeFld.setValue(frmData.ICD10)
						}
					}, this);
			var win = tumourNameQuery.getWin();
			win.setPosition(250, 100);
			win.show();
			tumourNameQuery.form.doSelect();
		}
	},
	doTNClear : function(){
		var frm = this.form.getForm();
		var tumourNameFld = frm.findField("tumourName");
		if(tumourNameFld){
			tumourNameFld.setValue();
		}
		this.fireEvent("selectNoTumourName");
	},
	onIsDeathSelect : function(){
		var frm = this.form.getForm();
		var isDeathFld = frm.findField("isDeath");
		var deathDateFld = frm.findField("deathDate");
		var isDeath = isDeathFld.getValue();
		if(deathDateFld){
			if(isDeath == 'y'){
	            deathDateFld.enable();
				deathDateFld.allowBlank = false;
	            deathDateFld["not-null"] = true;
	            deathDateFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>"+deathDateFld.fieldLabel+":</span>");
	             frm.findField("agreeVisit").setValue({key:'n',text:'否'});
			}else{
	            deathDateFld.disable();
				deathDateFld.allowBlank = true;
	            deathDateFld["not-null"] = false;
	            deathDateFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update(deathDateFld.fieldLabel+":");
	            frm.findField("agreeVisit").setValue({key:'y',text:'是'});
			}
			deathDateFld.validate();
		}
	},
	
	onReportDoctorSelect : function(combo, node) {
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
		this.setManaUnit(result.json.manageUnit);
	},
	setManaUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("reportUnit");
		if (!combox) {
			return;
		}
		if (!manageUnit) {
			combox.enable();
			combox.reset();
			return;
		}
		if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
			combox.setValue(manageUnit);
			combox.disable();
		} else {
			combox.enable();
			combox.reset();
		}
	}
});