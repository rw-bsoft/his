$package("chis.application.tr.script.tprc");

$import("chis.script.BizTableFormView");

chis.application.tr.script.tprc.TumourPatientFirstVisitForm = function(cfg){
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount  = 3
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	chis.application.tr.script.tprc.TumourPatientFirstVisitForm.superclass.constructor.apply(this,[cfg]);
	this.on("doNew",this.onDoNew,this);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientFirstVisitForm,chis.script.BizTableFormView,{
	onDoNew : function(){
		this.initDataId = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		this.empiId = this.exContext.ids.empiId;
		this.data.empiId=this.exContext.ids.empiId;
		this.data.TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		this.onAnnulControlChange();
		this.onAnnulCauseSelect();
		this.onHaveTransferSelect();
		this.onIsRelapseChange();
		this.onIsDeathSelect();
	},
	onLoadData : function(entryName,body){
		this.onAnnulControlChange();
		this.onAnnulCauseSelect();
		this.onHaveTransferSelect();
		this.onIsRelapseChange();
		this.onIsDeathSelect();
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
		saveData.TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"]
		saveData.empiId = this.exContext.ids.empiId;
		this.fireEvent("save",saveData);
	},
	onReady : function(){
		chis.application.tr.script.tprc.TumourPatientFirstVisitForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		
		var annulControlFld = frm.findField("annulControl");
		if(annulControlFld){
			annulControlFld.on("change",this.onAnnulControlChange,this);
		}
		var annulCauseFld = frm.findField("annulCause");
		if(annulCauseFld){
			annulCauseFld.on("select",this.onAnnulCauseSelect,this);
		}
		
		var haveTransferFld = frm.findField("haveTransfer");
		if(haveTransferFld){
			haveTransferFld.on("select",this.onHaveTransferSelect,this);
		}
		
		var isRelapseFld = frm.findField("isRelapse");
		if(isRelapseFld){
			isRelapseFld.on("change",this.onIsRelapseChange,this);
		}
		
		var visitDoctorFld = frm.findField("visitDoctor");
		if(visitDoctorFld){
			visitDoctorFld.on("select",this.onVisitDoctorSelect,this);
		}
		
		var isDeathFld = frm.findField("isDeath");
		if(isDeathFld){
			isDeathFld.on("select",this.onIsDeathSelect,this);
		}
		
		var CSVFld = frm.findField("CSV");
		if(CSVFld){
			CSVFld.on("select",this.onCSVSelect,this);
		}
	},
	onAnnulControlChange : function(radioGroup,checkedRadio){
		var frm = this.form.getForm();
		if(!radioGroup){
			radioGroup = frm.findField("annulControl");
		}
		var annulDateFld = frm.findField("annulDate");
		var annulCauseFld = frm.findField("annulCause");
		if(!annulDateFld || !annulCauseFld){
			return;
		}
		var annulControlVal = radioGroup.getValue();
		this.setNotNull(annulControlVal);
		
		if(annulControlVal == '2'){
			annulDateFld.enable();
			annulDateFld.allowBlank = false;
			annulDateFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>"+annulDateFld.fieldLabel+":</span>");
			annulCauseFld.enable();
			annulCauseFld.allowBlank = false;
			annulCauseFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>"+annulCauseFld.fieldLabel+":</span>");
		}else{
			annulDateFld.disable();
			annulDateFld.allowBlank = true;
			annulDateFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update(annulDateFld.fieldLabel+":");
			annulCauseFld.disable();
			annulCauseFld.allowBlank = true;
			annulCauseFld.getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update(annulCauseFld.fieldLabel+":");
		}
		annulDateFld.validate();
		annulCauseFld.validate();
	},
	setNotNull : function(annulControl){
		var frm = this.form.getForm();
		var items = this.schema.items;
		//获取必填项
        if (!this.notNullSchemaItems) {
            this.notNullSchemaItems = [];
            for (var i = 0; i < items.length; i++) {
                if (items[i]["not-null"] || items[i]["not-null"] == "1") {
                    this.notNullSchemaItems.push(items[i].id);
                }
            }
        }
        if (annulControl == '1') {// @@ 恢复原必填项。              
                for (var i = 0; i < items.length; i++) {
                    if(items[i].id == "nextDate" && this.planMode !=2){
                        continue;
                    }
                    var field = frm.findField(items[i].id);
                    if(items[i].id == "nextDate" && this.planMode == 2){
	                    field.allowBlank = false;
	                    items[i]["not-null"] = true;
	                    Ext.getCmp(field.id).getEl().up('.x-form-item')
	                            .child('.x-form-item-label').
	                                update("<span style='color:red'>" + items[i].alias + ":</span>");
	                    continue;
                	}
                    if (field && this.notNullSchemaItems.indexOf(items[i].id) > -1) {
                        field.allowBlank = false;
                        items[i]["not-null"] = true;
                        Ext.getCmp(field.id).getEl().up('.x-form-item')
                                .child('.x-form-item-label').
                                    update("<span style='color:red'>" + items[i].alias + ":</span>");
	                    field.validate();
                    }
                }
                return;
        }
        
        if (annulControl == '2') {// @@ 去除必填项      
            for (var i = 0; i < items.length; i++) {
                if (items.evalOnServer || items[i].id =="visitDoctor" || items[i].id =="visitDate") {
                    continue;
                }
                
                var field = frm.findField(items[i].id);
                if(items[i].id == "nextDate" && this.planMode == 2){
                    field.allowBlank = false;
                    items[i]["not-null"] = true;
                    Ext.getCmp(field.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').
                                update("<span style='color:red'>" + items[i].alias + ":</span>");
                    continue;
                }
                
                if (field) {
                    field.allowBlank = true;
                    items[i]["not-null"] = false;
                    Ext.getCmp(field.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').update(items[i].alias + ":"); 
	                field.validate();
                }
            }
           // this.validate();
            return
        }	
	},
	
	onAnnulCauseSelect : function(){
		var frm = this.form.getForm();
		var annulCauseFld = frm.findField("annulCause");
		var otherCauseFld = frm.findField("otherCause");
		if(!annulCauseFld || !otherCauseFld){
			return;
		}
		var annulCauseVal = annulCauseFld.getValue();
		if(annulCauseVal=='6'){
			otherCauseFld.enable();
		}else{
			otherCauseFld.disable();
		}
	},
	onHaveTransferSelect : function(){
		var frm = this.form.getForm();
		var haveTransferFld = frm.findField("haveTransfer");
		var transferPartFld = frm.findField("transferPart");
		if(!haveTransferFld || !transferPartFld){
			return;
		}
		var haveTransfer = haveTransferFld.getValue();
		if(haveTransfer == 'y'){
			transferPartFld.enable();
		}else{
			transferPartFld.disable();
		}
	},
	onIsRelapseChange : function(radioGroup,checkedRadio){
		var frm = this.form.getForm();
		if(!radioGroup){
			radioGroup = frm.findField("isRelapse");
		}
		var relapseNumberFld = frm.findField("relapseNumber");
		var relapseDate1Fld = frm.findField("relapseDate1");
		var relapseDate2Fld = frm.findField("relapseDate2");
		var relapseDate3Fld = frm.findField("relapseDate3");
		if(!relapseNumberFld || !relapseDate1Fld || !relapseDate2Fld || !relapseDate3Fld){
			return;
		}
		var isRelapse = radioGroup.getValue();
		if(isRelapse == 'y'){
			relapseNumberFld.enable();
	        relapseDate1Fld.enable();
	        relapseDate2Fld.enable();
	        relapseDate3Fld.enable();
		}else{
			relapseNumberFld.disable();
	        relapseDate1Fld.disable();
	        relapseDate2Fld.disable();
	        relapseDate3Fld.disable();
		}
	},
	
	onVisitDoctorSelect : function(combo, node){
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
		var combox = this.form.getForm().findField("vistUnit");
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
	},
	
	onIsDeathSelect:function(){
		var frm = this.form.getForm();
		var isDeathFld = frm.findField("isDeath");
		var deathDateFld = frm.findField("deathDate");
		var deathPlaceFld = frm.findField("deathPlace");
		if(!isDeathFld || !deathDateFld || !deathPlaceFld){
			return;
		}
		var isDeath = isDeathFld.getValue();
		var CSVFld = frm.findField("CSV");
		if(isDeath == 'y'){
			deathDateFld.enable();
			deathDateFld.allowBlank = false;
			Ext.getCmp(deathDateFld.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').
                                update("<span style='color:red'>死亡日期:</span>");
            deathDateFld.validate();
			deathPlaceFld.enable();
			if(CSVFld){
				CSVFld.setValue({key:'0',text:'(0)死亡。'})
			}
		}else{
			deathDateFld.disable();
			deathDateFld.allowBlank = true;
			Ext.getCmp(deathDateFld.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').update("死亡日期:"); 
            deathDateFld.validate();
			deathPlaceFld.disable();
			if(CSVFld){
				var csv = CSVFld.getValue();
				if(csv == 0){
					CSVFld.setValue();
				}
			}
		}
	},
	
	onCSVSelect : function(){
		var frm = this.form.getForm();
		var CSVFld = frm.findField("CSV");
		if(!CSVFld){
			return;
		}
		var csv = CSVFld.getValue();
		var isDeathFld = frm.findField("isDeath");
		if(csv == '0'){
			if(isDeathFld){
				isDeathFld.setValue({key:'y',text:'是'});
			}
		}else{
			if(isDeathFld){
				isDeathFld.setValue({key:'n',text:'否'});
			}
		}
		this.onIsDeathSelect();
	}
});