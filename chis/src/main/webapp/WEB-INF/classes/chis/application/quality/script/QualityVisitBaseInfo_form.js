/**
 * 高血压随访其他信息录入表单
 * 
 * @author ChenXianRui
 */
$package("chis.application.hy.script.visit");

$import("chis.script.BizTableFormView","chis.application.hy.script.HypertensionUtils");

chis.application.quality.script.QualityVisitBaseInfo_form = function(cfg) {
    cfg.autoLoadSchema = false;
    cfg.isCombined = true;
    cfg.autoLoadData = false;
    cfg.showButtonOnTop = true;
    cfg.autoWidth = true;
    cfg.colCount = 4;
    cfg.labelWidth = 88;
    cfg.fldDefaultWidth = 110;
    cfg.autoFieldWidth = false;
	chis.application.quality.script.QualityVisitBaseInfo_form.superclass.constructor
			.apply(this, [cfg]);
    Ext.apply(this, chis.application.hy.script.HypertensionUtils);
    this.initDataId = this.exContext.args.visitId;
    this.serviceId = "chis.hypertensionVisitService";
    this.serviceAction = "saveHypertensionVisit";
    
    this.on("aboutToSave", this.onAboutToSave, this);
    this.on("beforeLoadData", this.onBeforeLoadData, this);
};

Ext.extend(chis.application.quality.script.QualityVisitBaseInfo_form,
		chis.script.BizTableFormView, {
	doNew : function(){
		chis.application.quality.script.QualityVisitBaseInfo_form.superclass.doNew.call(this);
		//this.initData(this.exContext.args);
	},
	doSave: function(){
		var values = this.getFormData();
		if(!values){
			return;
		}
	//	 Ext.apply(this.data,values); 
		 this.fireEvent("formSave", values);//返回 保存数据
		//this.saveToServer(values)
	},
	partLoat:function(data){
		this.doNew();
		this.initData(data);
	},
	initData : function(initValues) {
        if (initValues) {
            if (!this.data) {
                this.data = {};
            }
            for (var item in initValues) {
                var field = this.form.getForm().findField(item);      
                if (field &&  initValues[item]!=null &&  initValues[item]!="null") {        
                    field.setValue(initValues[item]);
                } else {
                    this.data[item] = initValues[item];             
                }
            }
        }
        this.medicine = null;
        this.planId = initValues["planId"];
        this.visitId = initValues["visitId"];
        this.empiId = initValues["empiId"];
        this.phrId = initValues["phrId"];
        this.planDate = initValues["planDate"];
        this.sn = initValues["sn"];
        this.planStatus = initValues['planStatus'];
     //   this.nextDateDisable = initValues['nextDateDisable'];
//        this.beginDate = Date.parseDate(initValues["beginDate"], "Y-m-d");
//        this.endDate = Date.parseDate(initValues["endDate"], "Y-m-d");
      
        //设置随访时间选择范围
//        var nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
//        var visitDateObj = this.form.getForm().findField("visitDate");
//        visitDateObj.setValue(this.planDate);//默认值为计划时期
//        if(nowDate < this.beginDate || nowDate > this.endDate){
//        	visitDateObj.setMinValue(this.beginDate);
//        	visitDateObj.setMaxValue(this.endDate);
//        }else if(nowDate >= this.beginDate && nowDate <= this.endDate){
//        	visitDateObj.setMinValue(this.beginDate);
//        	visitDateObj.setMaxValue(nowDate);
//        }
        //设置下次预约时间范围
//        var nextDateObj = this.form.getForm().findField("nextDate");
//        if(nowDate > this.endDate){
//        	nextDateObj.setMinValue(nowDate);
//        }else{
//        	var nextMinDate = new Date(this.endDate.getFullYear(),this.endDate.getMonth(),this.endDate.getDate()+1);
//        	nextDateObj.setMinValue(nextMinDate);
//        }
        var result = util.rmi.miniJsonRequestSync({
            serviceId : "chis.hypertensionVisitService",
            serviceAction : "visitInitialize",
            method:"execute",
//          cnd: ["eq", ["$", "empiId"], ["s", this.empiId]],
            body : {
                lifeStyleSchema : "chis.application.hr.schemas.EHR_LifeStyle",
                fixGroupSchema : "chis.application.hy.schemas.MDC_HypertensionFixGroup",
                empiSchema : "chis.application.mpi.schemas.MPI_DemographicInfo",
                empiId : this.empiId, 
                lastEndDate : initValues["endDate"],
                lastBeginDate : initValues["beginDate"],
                planDate : this.planDate,
                planId : this.planId,
                occurDate : initValues["beginDate"],
                businessType : 1
            }
        });
        if (result.json.body) {
            var groupAlarm = result.json.body.groupAlarm;
            var nonArrivalDate = this.exContext.args.nonArrivalDate;
            if (groupAlarm == 1 && this.planStatus != "9" && !nonArrivalDate) {
                Ext.Msg.alert('提示', "下一次随访需要进行年度评估，请做好相关准备工作。");
            }
            var data = result.json.body.lifeStyle;
            if(data){
                var smokeCount = data.smokeCount;
                var drinkCount = data.drinkCount;
                var drinkTypeCode = data.drinkTypeCode;
                this.form.getForm().findField("smokeCount").setValue(smokeCount);
                this.form.getForm().findField("drinkCount").setValue(drinkCount);
                this.form.getForm().findField("drinkTypeCode").setValue(drinkTypeCode);
            }
            data = result.json.body.minStepInfo;
            if (data) {
                //this.minStep = data.minStep;
                this.nextRemindDate = data.nextRemindDate;
                this.nextPlanId = data.nextPlanId;
            } else {
               // delete this.minStep;
                delete this.mextRemindDate;
                delete this.nextPlanId;
            }
            data = result.json.body.fixGroup;
            if (data) { 
                this.manaUnitId = data.manaUnitId;
                this.height = data.height;
                this.riskLevel = {key : data.riskLevel, text : data.riskLevel_text};
                this.riskiness = {key: data.riskiness, text : data.riskiness_text};
                this.targetHurt = {key : data.targetHurt, text : data.targetHurt_text};
                this.complication = {key : data.complication, text : data.complication_text};
                this.hypertensionGroup = {key : data.hypertensionGroup, text : data.hypertensionGroup_text};
                this.form.getForm().findField("riskLevel").setValue(this.riskLevel);
                this.form.getForm().findField("riskiness").setValue(this.riskiness);
                this.form.getForm().findField("targetHurt").setValue(this.targetHurt);
                this.form.getForm().findField("complication").setValue(this.complication);
                if (this.riskLevel && this.riskLevel.key) {
                    var vs = this.riskLevel.key.split(",");
                    var f = this.form.getForm().findField("smokeCount");
                    if (vs.indexOf("2") == -1) {
                        f.setValue(0);  
                    } else {
                        f.setValue();   
                    }
                }
            } else {
                delete this.manaUnitId;
                delete this.height;
                delete this.riskLevel;
                delete this.targetHurt;
                delete this.complication;
            }
            this.age = result.json.body.age;
            this.sex = result.json.body.sex;
            this.planMode = result.json.body.planMode;
            data = result.json.body.lastVisit;
            if (data) {
                var targetWeight = data.targetWeight;
                var targetHeartRate = data.targetHeartRate;
                var targetSmokeCount = data.targetSmokeCount;
                var targetDrinkCount = data.targetDrinkCount;
                //var targetTrainTimesWeek = data.targetTrainTimesWeek;
                var targetTrainMinute = data.targetTrainMinute;
                var targetSalt = data.targetSalt || "";
                var targetWeightField = this.form.getForm().findField("targetWeight")
                targetWeightField.setValue(targetWeight);
                this.onTargetWeightChange(targetWeightField);
                this.form.getForm().findField("targetHeartRate").setValue(targetHeartRate);
                this.form.getForm().findField("targetSmokeCount").setValue(targetSmokeCount);
                this.form.getForm().findField("targetDrinkCount").setValue(targetDrinkCount);
                //this.form.getForm().findField("targetTrainTimesWeek").setValue(targetTrainTimesWeek);
                this.form.getForm().findField("targetTrainMinute").setValue(targetTrainMinute);
                this.form.getForm().findField("targetSalt").setValue(targetSalt);
                this.lastVisitId = data.visitId;
            } else {
                this.lastVisitId = "0000000000000000";  
            }
            if (this.sex && this.height) {
            		var targetWeightField = this.form.getForm().findField("targetWeight")
                    if (this.sex == 1) {
                        targetWeightField.setValue(this.height - 105);
                    } else if (this.sex == 2) {
                        targetWeightField.setValue(this.height - 110);  
                    }
                     this.onTargetWeightChange(targetWeightField);
            }
            if ((this.sex == 1 && this.age > 55 ) || (this.sex == 2 && this.age > 65)) {
                var combo = this.form.getForm().findField("riskiness");
                var record = new Ext.data.Record({key : 1});
                combo.fireEvent("select", combo, record);
            }
        }
        var visitDateField = this.form.getForm().findField("visitDate");
        //visitDateField.fireEvent("select", visitDateField);
        this.onVisitDateChange(visitDateField);
        
        var visitEffectField  =this.form.getForm().findField("visitEffect");
        //visitEffectField.fireEvent("select", visitEffectField);
        this.onVisitEffectChange(visitEffectField);
        //控制下次预约时间
        //this.setNextDate();
    },
    
	onBeforeLoadData : function(entryName,initDataId){
    	this.phrId = this.exContext.args.phrId;
    	this.empiId = this.exContext.args.empiId;
    	this.visitId = this.exContext.args.visitId;
    	this.planDate = this.exContext.args.planDate;
    	this.sn = this.exContext.args.sn;
    },
    
    loadData: function(){
	    this.doNew();
        if(this.loading){
            return;
        }
        if(!this.schema){
            return;
        }
        if(!this.exContext.args.visitId){
            return;
        }
        if(!this.fireEvent("beforeLoadData",this.entryName,this.initDataId)){
            return;
        } 
        if(this.form && this.form.el){
            this.form.el.mask("正在载入数据...","x-mask-loading");
        }
        this.loading = true;
        this.age = 0;
        this.sex = 0;
        util.rmi.jsonRequest({
            serviceId : "chis.hypertensionVisitService",
            serviceAction : "getVisitInfo",
            method:"execute",
            body : {
                    pkey : this.exContext.args.visitId,
                    planId : this.exContext.args.planId,
                    empiId : this.exContext.args.empiId
                    }
            },
            function(code,msg,json){
                if(this.form && this.form.el){
                    this.form.el.unmask();
                }
                this.loading = false;
                if(code > 300){
                    if (code == 504) {
                        msg = "对应的随访信息未找到!";
                    }                   
                    this.processReturnMsg(code,msg,this.loadData);
                    return;
                }
                var visitEffect = null;
                this.medicine = null;
                //var weight = null;
                var currentSymptoms = null;
                var conF = this.form.getForm().findField("constriction");
                var diaF = this.form.getForm().findField("diastolic");
                if(json.body){
                    if (json.body.visitInfo) {
                        this.initFormData(json.body.visitInfo);
                        this.fireEvent("loadData",this.entryName,json.body.visitInfo);
                        this.fireEvent("visitRecordRefreshed", json.body.visitInfo, json.body.visitInfo);                   
                        visitEffect = json.body.visitInfo.visitEffect;
                        if(json.body.visitInfo.medicine){
                        	this.medicine = json.body.visitInfo.medicine;
                        }
                        weight = json.body.visitInfo.weight;
                        currentSymptoms = json.body.visitInfo.currentSymptoms;
                        this.exContext.visitId = json.body.visitInfo.visitId;
                        this.hypertensionGroup = json.body.visitInfo.hypertensionGroup;
                        conF.fireEvent("blur", conF);
                        diaF.fireEvent("blur", diaF);
                    }
                    this.height = json.body.height; 
                    this.manaUnitId = json.body.manaUnitId;
                    this.age = json.body.age;
                    this.sex = json.body.sex;
                    this.planMode = json.body.planMode;
                    if(json.body.lastVisit){
                        this.lastVisitId = json.body.lastVisit.visitId;
                    }
                }
                
                var rd = new Ext.data.Record(visitEffect);
                var visitEffectField = this.form.getForm().findField("visitEffect");
                visitEffectField.fireEvent("select",this.form.getForm().findField("visitEffect"), null, rd);
                this.onVisitEffectChange(visitEffectField);
                var weightField = this.form.getForm().findField("weight");
                weightField.fireEvent("keyup", weightField); 
                
                var medicineCombo = this.form.getForm().findField("medicine");
                medicineCombo.fireEvent("select", medicineCombo, new Ext.data.Record(this.medicine));
                var currentSymptomsCombo = this.form.getForm().findField("currentSymptoms");
                currentSymptomsCombo.fireEvent("select", currentSymptomsCombo, new Ext.data.Record(currentSymptoms));
                if(this.op == 'create'){
                    this.op = "update";
                }
            },
            this);//jsonRequest
        //控制下次预约时间
       // this.setNextDate();
    },
    setNextDate:function(){
        //控制预约时间
        var form = this.form.getForm();
        var nextDate = form.findField("nextDate");
		//按随访结果时 下次预约时间范围在下一计划的开始结果时间之间
        if( this.planMode == "1" &&nextDate){
        	
            if(this.nextDateDisable || this.exContext.args.visitId){
//                nextDate.disable();
            }else{
//                nextDate.enable();
                if(this.exContext.args.endDate < this.mainApp.serverDate){
                	var p = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
                	var nextMinDate = new Date(p.getFullYear(),p.getMonth(),p.getDate()+1);
                	nextDate.setMinValue(nextMinDate);
                }else{
                	var p = Date.parseDate(this.exContext.args.endDate, "Y-m-d");
                	var nextMinDate = new Date(p.getFullYear(),p.getMonth(),p.getDate()+1);
                	nextDate.setMinValue(nextMinDate);
                }
            }
        }
        //控制nextDate时间
        if(this.planMode=="2"){//按下次预约随访时
       		nextDate.allowBlank = false;
            nextDate["not-null"] = true;
            Ext.getCmp(nextDate.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').
                                update("<span style='color:red'>下次预约时间:</span>");
        }   
    },
        
    onAboutToSave : function(entryName, op, saveData) {
        saveData["empiId"] = this.empiId;
        saveData["phrId"] = this.phrId;
        saveData["lastVisitId"] = this.lastVisitId;
        saveData["nextPlanId"] = this.nextPlanId;
        saveData["manaUnitId"] = this.manaUnitId;
        saveData["height"] = this.height;
        saveData["planDate"] = this.planDate;
        saveData["planId"] = this.planId;
        saveData["endDate"] = this.endDate;     
        saveData["beginDate"] = this.beginDate;
        saveData["fixGroupDate"] = this.exContext.args.fixGroupDate 
        saveData["sn"] = this.sn;
        
        if(this.planMode == "2" && saveData["visitEffect"] != "9"){
        var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
        
        if(!this.nextDateDisable && saveData["nextDate"]<=now){
                Ext.MessageBox.alert("提示","预约日期必须大于当前日期");
                return false;
            }
        }
        if (this.hypertensionGroup) {
            saveData["hypertensionGroup"] = this.hypertensionGroup.key;
        }
    },
    
    saveToServer : function(saveData) {
        if (this.medicine && 
            (this.medicine.key == "1" || this.medicine.key == "2") && 
                (saveData.medicine == "3" || saveData.medicine == "4")) {
            Ext.Msg.show({
                title : '提示',
                msg : "当前操作会引起服药数据删除,是否继续?",
                modal : true,
                width : 300,
                buttons : Ext.MessageBox.YESNO,
                multiline : false,
                fn : function(btn, text) {
                    if (btn == "no") {
                        return;
                    }
                    saveData.deleteMedicine = true;
                    this.executeSaveAction(saveData);
                },
                scope : this
            });
        } else {
            this.executeSaveAction(saveData);
        }
    },

    executeSaveAction : function(saveData) {
        if(this.fireEvent("aboutToSave", this.entryName, this.op, saveData, this)==false){
        	this.form.el.unmask();
            return;
        }
        if (this.hypertensionGroup && this.hypertensionGroup.key) {
            saveData["hypertensionGroup"] = this.hypertensionGroup.key;
        }
        this.fireEvent("saveToServer",this.op,saveData,this);
    },
    
    visitIdChange : function(visitId){
    	 this.exContext.visitId = visitId;
         this.visitId = visitId;
    },

    doImport : function() {
        var module = this.midiModules["HyperClinicImportList"];
        var list = this.list;
        var cfg = {};
        cfg.empiId = this.exContext.args.empiId;
        if (!module) {
            var cls = "chis.application.hy.script.visit.HypertensionClinicList";
            $require(cls, [function() {
                var m = eval("new " + cls + "(cfg)");
                m.on("import", this.onImport, this);
                this.midiModules["HyperClinicImportList"] = m;
                list = m.initPanel();
                list.border = false;
                list.frame = false;
                this.list = list;
                
                var win = m.getWin();
                win.add(list);
                win.show();
            }, this]);
        }else{
            Ext.apply(module, cfg);
            module.requestData.cnd = ['eq', ['$', 'empiId'], ['s', this.exContext.args.empiId]];
            module.loadData();
            var win = module.getWin();
            win.add(list);
            win.show();
        }
    },
    
    onImport : function(record) {
        if (!record || !this.form) {
            return;
        }
//      this.form.getForm().findField("height").setValue(record.get("height"));
        this.height = record.get("height");
        var weightF = this.form.getForm().findField("weight");
        weightF.setValue(record.get("weight"));
        weightF.fireEvent("keyup", weightF, this);
        var consF = this.form.getForm().findField("constriction");
        consF.setValue(record.get("constriction"));
        consF.fireEvent("keyup", consF, this);
        var diasF = this.form.getForm().findField("diastolic");
        diasF.setValue(record.get("diastolic"));
        diasF.fireEvent("keyup", diasF, this);
    },
    
    /*setVisitEndDate : function(endDate) {
        this.endDate = endDate;
    },*/
    
    onReady : function() {
        chis.application.quality.script.QualityVisitBaseInfo_form.superclass.onReady.call(this);
        
        var form = this.form.getForm();
        form.findField("visitEffect").on("select", this.onVisitEffectChange, this); 
        form.findField("visitEffect").on("change", this.onVisitEffectChange, this);
        form.findField("medicine").on("select", this.onMedicinChange, this);
        form.findField("medicine").on("change", this.onMedicinChange, this);
        form.findField("medicineBadEffect").on("select", this.onMedicineBadEffectChange, this);
        form.findField("medicineBadEffect").on("change", this.onMedicineBadEffectChange, this);
        form.findField("constriction").on("blur", this.onConstrictionChange, this);
        form.findField("constriction").on("keyup", this.onConstrictionChange, this);
        form.findField("diastolic").on("blur", this.onDiastolicChange, this);
        form.findField("diastolic").on("keyup", this.onDiastolicChange, this);
        
        form.findField("riskiness").on("select", this.onRiskinessSelect, this);
        form.findField("complication").on("select", this.onComplicationSelect, this);
//      form.findField("diabetesInfo").on("select", this.onDiabetesInfoSelect, this);
        form.findField("targetHurt").on("select", this.onTargetHurtSelect, this);
        form.findField("currentSymptoms").on("select", this.onCurrentSymptomsSelect, this);
        
        form.findField("visitDate").on("select", this.onVisitDateChange, this);
        form.findField("visitDate").on("blur", this.onVisitDateChange, this);
        form.findField("visitDate").on("keyup", this.onVisitDateChange, this);
        var serverDate = this.mainApp.serverDate;
        form.findField("visitDate").maxValue = Date.parseDate(serverDate, "Y-m-d");
        form.findField("nextDate").minValue = Date.parseDate(serverDate, "Y-m-d");
        
        form.findField("weight").on("blur", this.onWeightChange, this);
        form.findField("weight").on("keyup", this.onWeightChange, this);
        form.findField("targetWeight").on("blur", this.onTargetWeightChange, this);
        form.findField("targetWeight").on("keyup", this.onTargetWeightChange, this);
        
        form.findField("drinkTypeCode").on("select", this.onDrinkTypeSelect, this);
        
        form.findField("drinkCount").on("change", this.onDrinkCountChange, this);
        form.findField("drinkCount").on("keyup", this.onDrinkCountChange, this);
        form.findField("smokeCount").on("change", this.onSmokeCountChange, this);
        form.findField("smokeCount").on("keyup", this.onSmokeCountChange, this);
        
        var  visitDoctor =  form.findField("visitDoctor");
        if(visitDoctor){
            visitDoctor.on("select",this.onVisitDoctorSelect,this);
        }
    },
    
  /*  onEmpiIdChange : function(newEmpiId) {
        this.empiId = newEmpiId;    
    },
    
    onPhrIdChange : function(newPhrId) {
        this.phrId = newPhrId;
    },
    
    onVisitIdChange : function(newVisitId) {
//      if (this.visitId == newVisitId) {
//          return;
//      } 
        this.visitId = newVisitId;
        if (this.visitId == null || this.visitId == "") {
            this.doNew();
            return;
        }
        this.cnd = ['eq', ['$', 'visitId'], ['s', this.visitId]];
        this.loadData();
    },*/
    
    onVisitEffectChange : function(combo, record, index) {
        var newValue = combo.getValue();
        this.fireEvent("visitEffect",newValue);
//        if(newValue=='9' && this.nextDateDisable)//终止管理
//        {
//            Ext.MessageBox.alert("提示","只有最后一条记录才可以终止管理！");
//            combo.reset();
//            return;
//        }
        var items = this.schema.items;
        var form = this.form.getForm();
//      var nextDate = form.findField("nextDate");
        var f = form.findField("noVisitReason");

        if (!this.notNullSchemaItems) {
            this.notNullSchemaItems = [];
            for (var i = 0; i < items.length; i++) {
                if (items[i]["not-null"] || items[i]["not-null"] == "1") {
                    this.notNullSchemaItems.push(items[i].id);
                }
            }
        }
        
        if (newValue == 1) {// @@ 恢复原必填项。
                Ext.getCmp(f.id).getEl().up('.x-form-item')
                                .child('.x-form-item-label').update(f.fieldLabel + ":");
                f.disable();
                f.reset();
                f.allowBlank = true;
                
                for (var i = 0; i < items.length; i++) {
                    if(items[i].id == "nextDate" && this.planMode !=2){
                        continue;
                    }
                    var field = form.findField(items[i].id);
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
                    }
                }
                this.validate();
                return;
        }
        
        if (newValue == 2) {// @@ 去除必填项，激活失访原因文本框
            Ext.getCmp(f.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label')
                                .update("<span style='color:red'>" + f.fieldLabel + ":</span>");
            f.allowBlank = false;
            f.enable();
            
            for (var i = 0; i < items.length; i++) {
                if (items.evalOnServer || items[i].id =="noVisitReason") {
                    continue;
                }
                
                var field = form.findField(items[i].id);
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
                }
            }
            this.validate();
            return
        }
        
        if (newValue == 9) {// @@ 去除必填项，激活失访原因文本框
            Ext.getCmp(f.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label')
                                .update("<span style='color:red'>" + f.fieldLabel + ":</span>");
            f.allowBlank = false;
            f.enable();
            
            for (var i = 0; i < items.length; i++) {
                if (items.evalOnServer || items[i].id =="noVisitReason") {
                    continue;
                }
                
                var field = form.findField(items[i].id);
                if (field) {
                    field.allowBlank = true;
                    items[i]["not-null"] = false;
                    Ext.getCmp(field.id).getEl().up('.x-form-item')
                            .child('.x-form-item-label').update(items[i].alias + ":"); 
                }
            }
            this.validate();
        }
    },
    
    onMedicinChange : function(combo, record, index) {  
        var newValue = combo.getValue();
        var mbeField = this.form.getForm().findField("medicineBadEffect");
        if (newValue == 3 || newValue == 4) {
            mbeField.setValue({key : 'n', text : "无"});
            mbeField.disable();
        } else {
            mbeField.enable();
        }
        this.onMedicineBadEffectChange();
        this.fireEvent("medicineSelectChange", newValue, this);
    },
    
    onMedicineBadEffectChange : function(combo, record, index){
    	if(!combo){
    		combo = this.form.getForm().findField("medicineBadEffect");
    	}
    	var newValue = combo.getValue();
        var mbetField = this.form.getForm().findField("medicineBadEffectText");
        if (newValue == 'y') {
            mbetField.enable();
        } else {
            mbetField.disable();
        }
    },
    
    onNoMedicineChange : function(combo, record, index) {
        var newValue = combo.getValue();
        var f = this.form.getForm().findField("otherReason");
        var valueArray = newValue.split(",");
        if (valueArray.indexOf("6") != -1) {
            f.enable();
            combo.clearValue();
            if (record.data.key == 0) {
                combo.setValue({key : 6, text : "其他"});
            } else {
                combo.setValue(record.data.key);
            }
        } else {
            f.disable();
        }
        if (newValue == "") {
            combo.setValue({key : 6, text : "其他"});
        }
    },
    
    onConstrictionChange : function(field) {
        var constriction = field.getValue();
        var diastolicFld = this.form.getForm().findField("diastolic");
        var diastolic = diastolicFld.getValue();
        if (constriction) {
            diastolicFld.maxValue = constriction - 1;
        } else {
            diastolicFld.maxValue = 500;
        }
        diastolicFld.minValue = 50;
        if (diastolic) {
            field.minValue = diastolic + 1;
        } else {
            field.minValue = 50;
        }
        field.maxValue = 500;
        field.validate();
        diastolicFld.validate();
        this.addBPUnnormalToRiskness(constriction, diastolic);
    },
    
    onDiastolicChange : function(field) {
        var diastolic = field.getValue();
        var constrictionFld = this.form.getForm().findField("constriction");
        var constriction = constrictionFld.getValue();
        if (constriction) {
            field.maxValue = constriction - 1;
        } else {
            field.maxValue = 500;
        }
        field.minValue = 50;
        if (diastolic) {
            constrictionFld.minValue = diastolic + 1;
        } else {
            constrictionFld.minValue = 50;
        }
        constrictionFld.maxValue = 500;
        field.validate();
        constrictionFld.validate();
        this.addBPUnnormalToRiskness(constriction, diastolic);
    },
    
    onRiskinessSelect : function(combo, record, index) {
        if (record.data.key == 1) {
            if ((this.sex == 1 && this.age > 55 ) || (this.sex == 2 && this.age > 65)) {
                if (combo.getValue() == "") {
                    combo.setValue(record.data.key);
                } else {
                    var ar = combo.getValue();
                    if (ar.indexOf("1") == -1) {
                        combo.setValue(combo.getValue() + "," + record.data.key);
                    }
                }
            }
        }
        var constriction = this.form.getForm().findField("constriction").getValue();
        var diastolic = this.form.getForm().findField("diastolic").getValue();
        if (record.data.key == 8) {
            if (!this.isBPNormal(constriction, diastolic)) {
                if (combo.getValue() == "") {
                    combo.setValue(record.data.key);
                } else {
                    var ar = combo.getValue();
                    if (ar.indexOf("8") == -1) {
                        combo.setValue(combo.getValue() + "," + record.data.key);
                    }
                }
            }
        }
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("0") != -1) {
            combo.clearValue();
            if (record.data.key == 0) {
            	combo.setValue({key : 0, text : "无"});
            } else {
                combo.setValue(record.data);
            }
        }
        valueArray = combo.getValue().split(",");
        var smokeCountField = this.form.getForm().findField("smokeCount");
        var targetSmokeCountField = this.form.getForm().findField("targetSmokeCount");
        if(valueArray.indexOf("2") >= 0 && record.data.key == "2"){
            smokeCountField.setValue();
            targetSmokeCountField.enable();
        }else{
            smokeCountField.setValue(0);
            targetSmokeCountField.setValue();
            targetSmokeCountField.disable();
        }
        
        if (value == "") {
            combo.setValue({key : 0, text : "无"});
        } 
    },
    
    onComplicationSelect : function(combo, record, index) {
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("0") != -1) {
            combo.clearValue();
            if (record.data.key == 0) {
                combo.setValue({key : 0, text : "无"});
            } else {
                combo.setValue(record.data.key);
            }
        }
        if (value == "") {
            combo.setValue({key : 0, text : "无"});
        }
    },
    
    onTargetHurtSelect : function(combo, record, index) {
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("0") != -1) {
            combo.clearValue();
            if (record.data.key == 0) {
                combo.setValue({key : 0, text : "无"});
            } else {
                combo.setValue(record.data.key);
            }
        }
        if (value == "") {
            combo.setValue({key : 0, text : "无"});
        }
    },
    onCurrentSymptomsSelect:function(combo, record, index){
        var value = combo.getValue();
        var valueArray = value.split(",");
        var otherSymptomsF = this.form.getForm().findField("otherSymptoms");
        if (valueArray.indexOf("9") != -1) {
            combo.clearValue();
            otherSymptomsF.setValue();
            if (record.data.key == 9) {
                combo.setValue(9);
                otherSymptomsF.setValue();
                otherSymptomsF.disable();
            } else {
                combo.setValue(record.data);
            }
        }
        if (value == "") {
            combo.setValue(9);
            otherSymptomsF.setValue();
            otherSymptomsF.disable();
        }
        if (record.data.key == 10) {
            if (valueArray.indexOf("10") != -1) {
                otherSymptomsF.enable();
            } else {
                otherSymptomsF.setValue();
                otherSymptomsF.disable();
            }
        }
    },
    
    onNonMedicineWaySelect : function(combo, record, index) {
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("9") != -1) {
            combo.clearValue();
            if (record.data.key == 9) {
                combo.setValue(9);
            } else {
                combo.setValue(record.data.key);
            }
        }
        if (value == "") {
            combo.setValue(9);
        }
    },
    
    onVisitDateChange : function(field) {   
        if (!field.validate()) {
            return;
        }
        var frm = this.form.getForm(); 
    },
    
    onWeightChange : function(field) {
        if (!field.validate()) {
            return;
        }
        var weight = field.getValue();      
        var rField = this.form.getForm().findField("riskiness");
        var riskness = rField.getValue();
        var riskArray = riskness.split(",");
        if (!weight) {
            this.form.getForm().findField("bmi").setValue();
            this.form.getForm().findField("loseWeight").setValue({key : 1, text : "不需要"});
            if (riskArray.indexOf("5") != -1) {
                riskness = "";
                for (var i = 0; i < riskArray.length; i++) {
                    if (riskArray[i] != 5) {
                        riskness += "," + riskArray[i];
                    }
                }
                rField.setValue(riskness.substring(1));
            }
            return;
        }
        if (weight > 500 || weight <= 0) {
            field.markInvalid("体重数值应在0到500之间！");
            return;
        }
        if (this.height && weight) {
            var temp = this.height * this.height / 10000;
            var bmi = (weight / temp).toFixed(2);
            this.form.getForm().findField("bmi").setValue(bmi);
            if (bmi > 24) {
                this.form.getForm().findField("loseWeight").setValue({key : 2, text : "需要"});
            } else {
                this.form.getForm().findField("loseWeight").setValue({key : 1, text : "不需要"});
            }
            
            if (bmi >= 28) {
                if (riskArray.indexOf("5") == -1) {
                    rField.setValue(riskness + ",5");
                }
            } else {
                if (riskArray.indexOf("5") != -1) {
                    riskness = "";
                    for (var i = 0; i < riskArray.length; i++) {
                        if (riskArray[i] != 5) {
                            riskness += "," + riskArray[i];
                        }
                    }
                    rField.setValue(riskness.substring(1));
                }
            }
        }
    },
    
    onTargetWeightChange : function(field) {
        if (!field.validate()) {
            return;
        }
        var weight = field.getValue();
        if (!weight) {
            return;
        }
        if (weight > 500 || weight <= 0) {
            field.markInvalid("体重数值应在0到500之间！");
            return;
        }
        if (this.height && weight) {
            var temp = this.height * this.height / 10000;
            var bmi = (weight / temp).toFixed(2);
            this.form.getForm().findField("targetBmi").setValue(bmi);
        }
    },
    
    onDrinkTypeSelect : function(combo, record, index) {
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("10") != -1) {
            combo.clearValue();
            if (record.data.key == 10) {
                combo.setValue({key : 10, text : "不饮酒"});
                this.form.getForm().findField("drinkCount").setValue(0);
            } else {
                combo.setValue(record.data.key);
                this.form.getForm().findField("drinkCount").setValue();
            }
        }
        if (value == "") {
            combo.setValue({key : 10, text : "不饮酒"});
            this.form.getForm().findField("drinkCount").setValue(0);
        }
    },
    
    onDrinkCountChange : function(field) {
        field.validate();
        var combo = this.form.getForm().findField("drinkTypeCode");
        if (field.getValue() == 0) {
            combo.setValue({key : 10, text : "不饮酒"});
        } else {
            if (combo.getValue() == 10) {
                combo.setValue();
            }
            combo.allowBlank = false;
            combo.validate();
        }
    },
    
    onSmokeCountChange : function(field) {
        field.validate();
        var rField = this.form.getForm().findField("riskiness");
        var riskness = rField.getValue();
        var riskArray = riskness.split(",");
        if (field.getValue() == 0) {
            riskness = "";
            for (var i = 0; i < riskArray.length; i++) {
                if (riskArray[i] != 2) {
                    riskness += "," + riskArray[i];
                }
            }
            rField.setValue(riskness.substring(1));
        } else {
        	if(riskness == "0"){
        		  rField.setValue({key:"2",value:"吸烟"});
        	}else{
        		if (riskArray.indexOf("2") == -1) {
                    rField.setValue(riskArray + ",2");
                }
        	}
            
        }
    },
    
    onVisitDoctorSelect : function(combo, node) {
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
	        })
          var  manageUnit = result.json.manageUnit;
          this.data.visitUnit = manageUnit.key;
   },
    
    validate:function(){
        if (false == this.form.getForm().isValid()) {
            return false;
        }
        var constrictionFld = this.form.getForm().findField("constriction");
        var diastolicFld = this.form.getForm().findField("diastolic");
        var constriction = constrictionFld.getValue();
        var diastolic = diastolicFld.getValue();
        if (constriction < diastolic) {
            diastolicFld.markInvalid("舒张压应该小于收缩压！");
            constrictionFld.markInvalid("收缩压应该大于舒张压！");
            return false;
        }
        this.setBtnable();
        return true;
    },
    
 /*   onReadOnly : function(op) {
        if(!this.readOnly){
        var bts = this.form.getTopToolbar().items;
        if (!bts || !bts.items[0]) {
            return;
        }

        for(var i=0;i<bts.items.length;i++){
            if (!bts.items[i]) {
                continue;
            }
            if (op == "read") {
                bts.items[i].disable();
            } else {
                bts.items[i].enable();
            }
        }   
        }
    },*/
    
    addBPUnnormalToRiskness : function(constriction, diastolic) {
        var risknessField = this.form.getForm().findField("riskiness");
        if (!this.isBPNormal(constriction, diastolic)) {
            if (risknessField.getValue() != 0) {
                risknessField.setValue(risknessField.getValue() + ",8");
            } else {
                risknessField.setValue("8");
            }
        } else {
            var riskValues = risknessField.getValue().split(",");       
            var newValue = "";
            riskValues.remove("8");
            for (var i = 0; i < riskValues.length; i++) {               
                newValue += riskValues[i] + ",";
            }
            if (newValue == "") {
                risknessField.setValue({key : 0, text : "无"});
            } else {
                risknessField.setValue(newValue.substring(0, newValue.length));
            }
        }
    },
    setBtnable : function(){
      if (!this.form.getTopToolbar()) {
          return;
        }
    	var btns = this.form.getTopToolbar().items;
		if(!btns.item(0)){
			return;
		}
		var rdStatus = this.exContext.ids.recordStatus;
		if(rdStatus && rdStatus == '1'){
			for(var i = 0;i<btns.getCount();i++){
				var btn = btns.item(i);
				if(btn){
					btn.disable();
				}
			}
		}
    } ,
	initFormData:function(data){
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		Ext.apply(this.data,data)
		this.initDataId = this.data[this.schema.pkey]
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
				var v = data[it.id]
				if (v != undefined) {
					if((it.type == 'date' || it.xtype == 'datefield') && typeof v == 'string' && v.length > 10){
						v = v.substring(0,10)
					}
					if((it.type == 'datetime' || it.type == 'timestamp' || it.xtype == 'datetimefield') && typeof v == 'string' && v.length > 19){
						v = v.substring(0,19)
					}
					f.setValue(v)
				}
				if (this.initDataId) {
					if (it.update == false || it.update == "false") {
						f.disable();
					}
				}
			}
		}
		this.setKeyReadOnly(true)	
		this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		this.focusFieldAfter(-1, 800);
	}
});