$package("chis.application.psy.script.html");

$import("chis.script.BizHtmlFormView","chis.application.psy.script.html.PsychosisRecordHtmlTemplate","chis.script.util.widgets.MyMessageTip");
$styleSheet("chis.css.PsychosisHtml")

chis.application.psy.script.html.PsychosisRecordHtmlForm = function(cfg){
	chis.application.psy.script.html.PsychosisRecordHtmlForm.superclass.constructor.apply(this,[cfg]);
	Ext.apply(this,chis.application.psy.script.html.PsychosisRecordHtmlTemplate);
	this.loadServiceId = "chis.psychosisRecordService";
	this.loadAction = "initializePsyRecordForm";
	this.createFields=["manaDoctorId","manaUnitId","guardianAddress","guardianRelation","icDate","diseasedTime","firstCureTime","recentDiagnoseTime","fillTableDate","doctorSign"];
	//this.notDicFlds=["status","cancellationReason","cancellationUser","cancellationUnit","createUnit","createUser","lastModifyUser","lastModifyUnit"];
	this.disableFlds = ["pastSymptomText","lightAffray","causeTrouble","causeAccident","selfHurt","suicide","icSign","this.icDate"];
	this.otherDisable = [{fld:"pastSymptom",type:"checkbox",control:[{key:"99",exp:'eq',field:["pastSymptomText"]}]},
		{fld:"familySocialImpact",type:"checkbox",control:[
			{key:"1",exp:"eq",field:["lightAffray"]},
			{key:"2",exp:"eq",field:["causeTrouble"]},
			{key:"3",exp:"eq",field:["causeAccident"]},
			{key:"4",exp:"eq",field:["selfHurt"]},
			{key:"5",exp:"eq",field:["suicide"]}]},
			{fld:"informedConsent",type:"radio",control:[{key:'1',exp:'eq',field:["icSign","this.icDate"]}]}];
	this.mutualExclusion = [{fld:"familySocialImpact",key:"0",other:["lightAffray","causeTrouble","causeAccident","selfHurt","suicide"]}];
	this.on("loadData",this.onLoadData,this);
	this.on("loadNoData",this.onLoadNoData,this);
}

Ext.extend(chis.application.psy.script.html.PsychosisRecordHtmlForm,chis.script.BizHtmlFormView,{
	getHTMLTemplate : function(){
		return this.getPsyRecordHTMLTemplate();
	},
	addFieldAfterRender : function() {
		// 在HTML中手动增加一些ExtJS创建的对象
		//责任医生
		var cfg = {
			"width" : 200,
			"defaultIndex" : 0,
			"id" : "chis.dictionary.user01",
			"render" : "Tree",
			"selectOnFocus" : true,
			"onlySelectLeaf" : true
		}
		var deptId = this.mainApp.deptId;
		if (deptId.length > 9) {
			cfg.parentKey = deptId;
		}
		this.manaDoctorId = this.createDicField(cfg);
		this.manaDoctorId.allowBlank = false;
		this.manaDoctorId.invalidText = "必填字段";
		this.manaDoctorId.regex = /(^\S+)/
		this.manaDoctorId.regexText = "前面不能有空格字符";
		this.manaDoctorId.fieldLabel = "责任医生";
		this.manaDoctorId.tree.expandAll();
		this.manaDoctorId.render(Ext.get("div_manaDoctorId"+ this.idPostfix));
		this.manaDoctorId.setValue({
					key : this.mainApp.uid,
					text : this.mainApp.uname
				});
		this.manaDoctorId.on("select", this.onManaDoctorIdSelect, this);
		//管辖机构
		this.manaUnitId = this.createDicField({
					"src" : "",
					"width" : 200,
					"id" : "chis.@manageUnit",
					"render" : "Tree",
					includeParentMinLen : 6,
					// onlySelectLeaf:true,
					//"filter" : "['le',['len',['$','item.key']],['i',9]]",
					"parentKey" : this.mainApp.deptId || {},
					//"defaultValue" : this.mainApp.deptId,
					"rootVisible" : "true"
				});
		this.manaUnitId.tree.expandAll();
		this.manaUnitId.render(Ext.get("div_manaUnitId"+ this.idPostfix));
		this.manaUnitId.setValue({
					key : this.mainApp.deptId,
					text : this.mainApp.dept
				});
		this.manaUnitId.disable();
		//与患者关系
		this.guardianRelation = this.createDicField({
			"width" : 200,
			"id" : "chis.dictionary.relaCode1"
		});
		this.guardianRelation.render(Ext.get("div_guardianRelation"+ this.idPostfix));
		//监护人住址
		this.guardianAddress = this.createDicField({
			width : 200,
			id : "chis.dictionary.areaGrid",
			minChars:4,
			includeParentMinLen:6,
			filterMin:10,
			filterMax:18,
			render:"Tree",
			onlySelectLeaf:true
		});
		this.guardianAddress.render(Ext.get("div_guardianAddress"+ this.idPostfix));
		//知情同意 签字时间
		var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		this.icDate = new Ext.form.DateField({
							name : 'icDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '签字时间',
							maxValue:curDate,
							renderTo : Ext.get("div_icDate"+ this.idPostfix)
						});
		//初次发病时间
		this.diseasedTime = new Ext.form.DateField({
							name : 'diseasedTime' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '初次发病时间',
							maxValue:curDate,
							renderTo : Ext.get("div_diseasedTime"+ this.idPostfix)
						});
		//首次抗精神病药治疗时间	
		this.firstCureTime = new Ext.form.DateField({
							name : 'firstCureTime' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '首次抗精神病药治疗时间',
							maxValue:curDate,
							renderTo : Ext.get("div_firstCureTime"+ this.idPostfix)
						});
		//确诊日期
		this.recentDiagnoseTime = new Ext.form.DateField({
							name : 'recentDiagnoseTime' + this.idPostfix,
							width : 100,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '确诊日期',
							maxValue:curDate,
							renderTo : Ext.get("div_recentDiagnoseTime"+ this.idPostfix)
						});
		//填表日期
		this.fillTableDate = new Ext.form.DateField({
							name : 'fillTableDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '填表日期',
							maxValue:curDate,
							defaultValue : curDate,
							renderTo : Ext.get("div_fillTableDate"+ this.idPostfix)
						});
		//医生签字
		var cfg = {
			"width" : 200,
			"defaultIndex" : 0,
			"id" : "chis.dictionary.user01",
			"render" : "Tree",
			"selectOnFocus" : true,
			"onlySelectLeaf":true
		}
		var deptId = this.mainApp.deptId;
		if (deptId.length > 9) {
			cfg.parentKey = deptId;
		}
		this.doctorSign = this.createDicField(cfg);
		this.doctorSign.fieldLabel = "医生签字";
		this.doctorSign.tree.expandAll();
		this.doctorSign.render(Ext.get("div_doctorSign"+ this.idPostfix));
		this.doctorSign.setValue({
					key : this.mainApp.uid,
					text : this.mainApp.uname
				});
		this.doctorSign.on("select", this.onDoctorSignSelect, this);
	},
	onManaDoctorIdSelect : function(combo, node) {
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method : "execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setManaUnit(result.json.manageUnit);
	},
	setManaUnit : function(manageUnit) {
		var combox = this.manaUnitId;
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
	onDoctorSignSelect : function(combo, node){
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method : "execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setDoctorUnit(result.json.manageUnit);
	},
	setDoctorUnit : function(manageUnit){
		var combox = document.getElementById("doctorUnit"+this.idPostfix);
		if (!combox) {
			return;
		}
		combox.value=manageUnit.key;
	},
	//========上面为基本页面加载**###**下面为功能实现=加载数据-保存数据等===
	getLoadRequest : function() {
		var body = {};
		if (this.empiId) {
			body.empiId = this.empiId;
		}
		body.pkey = this.exContext.ids.phrId;
		if (!body.empiId && !body.pkey) {
			return null;
		}
		return body;
	},
	loadData : function() {
		this.empiId = this.exContext.ids.empiId;
		//this.initDataId = this.exContext.ids.phrId;
		chis.application.psy.script.html.PsychosisRecordHtmlForm.superclass.loadData
				.call(this);
		delete this.saving;
	},
	onLoadNoData : function(){
		this.resetControlOtherFld();
		this.addFieldDataValidateFun(this.schema);
	},
	onLoadData:function(entryName,body){
		this.resetControlOtherFld();
		this.addFieldDataValidateFun(this.schema);
		this.initDataId = this.exContext.ids.phrId;
	},
	checkFldValidate : function(){
		var errFlds = Ext.query(".my .x-form-invalid");
		var eflen = errFlds.length;
		if (eflen > 0) {
			for (var i = 0; i < eflen; i++) {
				var fid = errFlds[i].id
				var fn = fid.substring(0, fid.indexOf("_"));
				if(document.getElementById(fid)){
					document.getElementById(fid).focus();
					if(document.getElementById(fid).type=='text'){
						document.getElementById(fid).select();
					}
				}
				//console.log("==================->"+fid)
			}
			//Ext.Msg.alert("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值");
			MyMessageTip.msg("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值",true);
			return false;
		}else{
			return true;
		}
	},
	getSaveData : function(){
		var saveData = this.getFormData();
		if(!saveData){
			saveData = {};
		}
		this.op = this.myOp;
		var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		var curUid = this.mainApp.uid;
		var curUnitId = this.mainApp.deptId;
		if(this.op=="create"){
			saveData.status="0";
			saveData.createUser = curUid;
			saveData.createUnit = curUnitId;
			saveData.createDate = curDate;
			saveData.lastModifyUser = curUid;
			saveData.lastModifyUnit = curUnitId;
			saveData.lastModifyDate = curDate;
		}else{
			saveData.lastModifyUser = curUid;
			saveData.lastModifyUnit = curUnitId;
			saveData.lastModifyDate = curDate;
		}
		return saveData
	},
	doSave : function() {
		if(this.saving){
			return
		}
		if(!this.htmlFormSaveValidate(this.schema)){
			//Ext.Msg.alert("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值");
			//MyMessageTip.msg("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值",true);
			return;
		}
		var saveData = this.getSaveData();
		Ext.apply(this.data,saveData);
		saveData.phrId = this.exContext.ids.phrId;
		this.fireEvent("save", saveData);
	},
	doCheck : function() {//健康检查
		this.fireEvent("addModule");
	},
	initFormData : function(data) {
		this.myOp = data.op;
		this.initHTMLFormData(data,this.schema);
		if (this.myOp == "create") {
			this.initDataId = null;
			this.setButton(["save"], true);
			this.manaDoctorId.enable();
		}
		if (!this.initDataId) {
			this.setButton(["check"], false);
		} else {
			this.setButton(["check"], true);
			//修改时，责任医生不可修改
			if(this.myOp == "update"){
				this.manaDoctorId.disable();
				this.fillTableDate.disable();
				this.doctorSign.disable();
			}
		}
		this.fieldValidate(this.schema);
	},
	/** 重写父类数据加载参数获取方式 * */
	setButton : function(m, flag) {
		if (this.empiId && this.phrId
				&& this.manaDoctorId != this.mainApp.uid
				&& this.mainApp.jobId != "system") {
			if (this.phrId) {
				Ext.Msg.alert("提示", "该病人责任医生非本人，不能新增接诊记录");
			}
			flag = false;
		}
		var btns;
		var btn;
		if (this.showButtonOnTop && this.form.getTopToolbar()) {
			btns = this.form.getTopToolbar();
		} else {
			btns = this.form.buttons;
		}
		if (!btns) {
			return;
		}
		if (this.showButtonOnTop) {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns.items.item(m[j]);
				} else {
					btn = btns.find("cmd", m[j]);
					btn = btn[0];
				}
				if (btn) {
					(flag) ? btn.enable() : btn.disable();
				}
			}
		} else {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns[m[j]];
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == m[j]) {
							btn = btns[i];
						}
					}
				}
				if (btn) {
					(flag) ? btn.enable() : btn.disable();
				}
			}
		}
	}
	
	
});