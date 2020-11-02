/**
 * 个人主要问题表单
 * 
 * @author : tianj
 */
$package("chis.application.hivs.script")

$import("chis.script.BizHtmlFormView", "app.modules.list.SimpleListView",
		"chis.application.mpi.script.CombinationSelect",
		"chis.script.BizSimpleFormView", "chis.script.BizTableFormView",
		"chis.application.hivs.script.HIVSTemplate",
		"chis.application.mpi.script.ParentsQueryList",
		"chis.application.mpi.script.SubTableForm","util.Accredit","chis.script.util.helper.Helper")
		
$styleSheet("chis.css.HIVSScreening");

debugger;
chis.application.hivs.script.ScreeningForm = function(cfg) {
	debugger;
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	//cfg.width = 710;
	chis.application.hivs.script.ScreeningForm.superclass.constructor.apply(this, [cfg]);
	Ext.apply(this,chis.application.hivs.script.HIVSTemplate);
	this.createFields = ["screeningDate"];
	this.disableFlds = ["other"];
	this.otherDisable = [{
							fld : "otherCheckbox",
							type : "checkbox",
							control : [{
										key : "1",
										exp : 'eq',
										field : ["other"],
		
									}]
						}];
	this.on("save", this.onSave, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.hivs.script.ScreeningForm, chis.script.BizHtmlFormView, {
	getHTMLTemplate : function() {
		var jobTitle = this.mainApp.jobtitleId;
		return this.getBasicInformationHTML(jobTitle);
	},
	
	addFieldAfterRender : function() {
		for (var i = 0; i < this.createFields.length; i++) {
			var fid = this.createFields[i];
			var field = new Ext.form.DateField({
				id:fid+this.idPostfix,
				name : fid,
				width : 100,
				altFormats : 'Y-m-d',
				format : 'Y-m-d',
				emptyText : '请选择日期',
				allowBlank : false,
				value:new Date(),
				renderTo : Ext.get("div_" + fid
						+ this.idPostfix)
			});
			eval("this." + fid + "=field;");
		}
	},
	
	getHTMLFormData : function(schema) {
		debugger;
		if (!schema) {
			schema = this.schema;
		}
		// 取表单数据
		if (!schema) {
			return
		}
		var ac = util.Accredit;
		var values = {};
		var items = schema.items;
		var n = items.length
		var frmEl = this.form.getEl();
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (this.op == "create" && !ac.canCreate(it.acValue)) {
				continue;
			}
			// 从内存中取
			var v = this.data[it.id];
			if (v == undefined) {
				v = it.defaultValue;
			}
			if (v != null && typeof v == "object") {
				v = v.key;
			}
			// 从页面上取
			if (this.isCreateField(it.id)) {
				v = eval("this." + it.id + ".getValue()");
				values[it.id] = v;
			} else {
				if (it.dic) {
					var fs = document.getElementsByName(it.id);
					if (!fs) {
						continue;
					}
					var vs = [];
					if (fs && fs.length > 0) {
						for (var j = 0, len = fs.length; j < len; j++) {
							var f = fs[j];
							if (frmEl.contains(f)) {
								if (f.type == "checkbox"
										|| f.type == "radio") {
									if (f.checked) {
										vs.push(f.value);
									}
								} else if (f.type == "hidden") {
									vs.push(f.value || '');
								}
							}
						}
					}
					if (vs.length > 1) {
						v = vs.join(',') || ''
					} else {
						v = vs[0] || ''
					}
					values[it.id] = v;
				} else {
					var f = document.getElementById(it.id
							+ this.idPostfix)
					if (f) {
						v = f.value || f.defaultValue || '';
						if (v == f.defaultValue && f.type != "hidden") {
							v = '';
						}
						values[it.id] = v;
					}
				}
			}
			if (v == null || v === "") {
				if (!(it.pkey)
						&& (it["not-null"] == "1" || it['not-null'] == "true")
						&& !it.ref) {
					if (eval("this." + it.id)) {
						eval("this." + it.id + ".focus(true,200)");
					} else if (it.dic) {
						var divId = "div_" + it.id + this.idPostfix;
						var div = document.getElementById(divId);
						if (document.getElementsByName(it.id)[0]) {
							document.getElementsByName(it.id)[0]
									.focus();
						}
					} else {
						if (document.getElementById(it.id
								+ this.idPostfix)) {
							document.getElementById(it.id
									+ this.idPostfix).focus();
							document.getElementById(it.id
									+ this.idPostfix).select();
						}
					}
					MyMessageTip.msg("提示", it.alias + "为必填项", true);
					return;
				}
			}
		}
		return values;
	},
	afterSaveData : function(a,b,c) {
		this.refreshEhrTopIcon();
	},
	doNew : function() {
		debugger;
		if(this.exContext.ids.phrId == null){
			Ext.Msg.alert("提示","请创建个人档案");
			return;
		}	
		var record = this.exContext.record.data;
		if(record != null && typeof this.newFlag == "undefined"){
			this.op = "update";
			this.id = record.screenId;
		}else{
			debugger;
			this.id = null;
			this.op = "create";
			for(var i=0;i<document.forms.length;i++){
				document.forms[i].reset();
			}
			
		}
		chis.application.hivs.script.ScreeningForm.superclass.fieldValidate.call(this);
	},
	
	doAdd : function() {
		this.fireEvent("add");
//		this.doCreate();
	},
	
	setFieldEnable : function() {
		var len = this.otherDisable.length;
		var me = this;
		for (var i = 0; i < len; i++) {
			var od = this.otherDisable[i];
			var cArr = od.control;
			var type = od.type;
			if (type == "text") {
				var fObj = document.getElementById(od.fld
						+ this.idPostfix);
				if (fObj) {
					me.textOnChange(fObj, cArr, me);
				}
			}
			if (type == "checkbox") {
				for (var j = 0, cLen = cArr.length; j < cLen; j++) {
					var co = cArr[j];
					var key = co.key;
					var fId = od.fld + "_" + key + this.idPostfix;
					var fObj = document.getElementById(fId);
					if (!fObj) {
						continue;
					}
					me.checkOnClick(fObj, co, me);
				}
			}
			if (type == "radio") {
				var fldName = od.fld;
				var fldes = document.getElementsByName(fldName);
				me.radioOnClick(fldName, cArr, me);
			}
		}
	},
	
	onReady : function() {
		chis.application.hivs.script.ScreeningForm.superclass.onReady.call(this);
		var form = this.form.getForm();
	},

	initFormData:function(data){
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		Ext.apply(this.data, data);
		this.initDataId = this.data[this.schema.pkey]
		this.initHTMLFormData(data, this.schema);
		this.fieldValidate(this.schema);
		// this.setKeyReadOnly(true)
		// this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		this.focusFieldAfter(-1, 800);
		
		debugger;
		var otherCheckbox = document.getElementById("otherCheckbox_1"+this.idPostfix);
		var otherText= document.getElementById("other"+this.idPostfix);
		if(otherText.value != null && otherText.value != ""){
			otherCheckbox.checked = true;
			otherText.disabled = false;
		}
	},
	
	doCancer : function() {
		this.getWin().hide();
	},
	
	doSave : function(){
		if(this.exContext.ids.phrId == null){
			Ext.Msg.alert("提示","请创建个人档案");
			return;
		}
		
		if(this.initDataId){
			debugger;
			this.op = "update";
			var screenId = this.initDataId;
			var r = this;
			if(typeof this.exContext.record != "undefined"){
				if(typeof this.exContext.record.data.checkFlag != "undefined"){
					var flag = this.exContext.record.data.checkFlag;
					if(flag == 1){
						Ext.Msg.alert("提示","该记录已审核，无法更新");
						return;
					}
				}
			}
		}else{
			this.op = "create";
			var screenId = "0";
		}
			
		var ids = this.exContext.ids;		
		var outHistoryValue=0;
		var seperationTMValue=0;
		var widowedHYValue=0;
		var screeningResultValue=0;
		var operationValue=0;
		var transfusionValue=0;
		var screeningDateValue=null;

		var screeningDate = document.getElementById("screeningDate"+this.idPostfix);
		screeningDateValue = screeningDate.value;
		//判断是否在同一天重复
		var res = util.rmi.jsonRequest({
			serviceId : "chis.hIVSScreeningService",
			serviceAction : "checkHIVSRecord",
			method:"execute",
			body:{
				"empiId":ids.empiId,
				"screeningDate":screeningDateValue,
				"screenId":screenId
			}
		},function(code,msg,json){				
			Ext.MessageBox.hide()
							debugger;
			if(code < 300){
				if((json.count == 0 && this.op == "create") || this.op == "update"){
					var outHistory_0 = document.getElementById("outHistory_0"+this.idPostfix);	
					//var outHistory_1 = document.getElementById("outHistory_1"+this.idPostfix);		
					var outHistory_1 = document.getElementById("outHistory_1"+this.idPostfix);			
					var outHistory_2 = document.getElementById("outHistory_2"+this.idPostfix);		
					var outHistory_3 = document.getElementById("outHistory_3"+this.idPostfix);		
					var outHistory_4 = document.getElementById("outHistory_4"+this.idPostfix);
							
					//外出史
					if(outHistory_0.checked){
						outHistoryValue = "0";
					}else if(outHistory_1.checked){
						outHistoryValue = "1";
					}else if(outHistory_2.checked){
						outHistoryValue = "2";
					}else if(outHistory_3.checked){
						outHistoryValue = "3";
					}else if(outHistory_4.checked){
						outHistoryValue = "4";
					}
							
					//夫妻分居超过三个月 
					var seperationTM = document.getElementById("seperationTM_1"+this.idPostfix);	
					if(seperationTM.checked){
						seperationTMValue = "1";
					}
					//丧偶超过半年
					var widowedHY = document.getElementById("widowedHY_1"+this.idPostfix);
					if(widowedHY.checked){
						widowedHYValue = "1";
					}
					//其他
					var otherTextValue = document.getElementById("other"+this.idPostfix).value;
					
					//手术
					debugger;
					var operation_0 = document.getElementById("operation_0"+this.idPostfix);	
					if(operation_0.checked){
						operationValue = "0";
					}else{
						operationValue = "1";
					}
					
					//输血
					var transfusion_0 = document.getElementById("transfusion_0"+this.idPostfix);	
					if(transfusion_0.checked){
						transfusionValue = "0";
					}else{
						transfusionValue = "1";
					}
					
					//筛查结果
					var screeningResult_0 = document.getElementById("screeningResult_0"+this.idPostfix);	
					if(screeningResult_0 != null){
						if(screeningResult_0.checked){
							screeningResultValue = "0";
						}else{
							screeningResultValue = "1";
						}
					}
					
					if (!this.htmlFormSaveValidate()) {
						return;
					}

					var res = util.rmi.jsonRequest({
						serviceId : "chis.hIVSScreeningService",
						serviceAction : "saveHIVSScreening",
						method:"execute",
						op:this.op,
						body:{
							"id":this.initDataId,
							"empiId":ids.empiId,
							"phrId":ids.phrId,
							"operation":operationValue,
							"transfusion":transfusionValue,
							"outHistory":outHistoryValue,
							"seperationTM":seperationTMValue,
							"widowedHY":widowedHYValue,
							"screeningResult":screeningResultValue,
							"screeningDate":screeningDateValue,
							"other":otherTextValue,
							"checkFlag":"0"
						}
					},function(code,msg,json){				
							Ext.MessageBox.hide()
							if(code < 300){
								debugger;
								MyMessageTip.msg("提示", "保存成功!", true);
								this.fireEvent("save",this);
							}else{
								alert(msg)
							}
					},this);	
				}
				else{
					Ext.Msg.alert("提示", "同一天不可重复筛查", true);
				}
			}
		},this);
	},
	
	onSave : function() {
		this.onLoadData();
		this.getWin().hide();
	}
});