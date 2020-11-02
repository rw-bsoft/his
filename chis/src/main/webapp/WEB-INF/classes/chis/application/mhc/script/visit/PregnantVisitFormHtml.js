/**
 * 孕妇随访信息表单页面
 * 
 * @author : xuzb  
 */
$package("chis.application.mhc.script.visit")
$styleSheet("chis.css.PregnantVisitFormHtml")
$import("chis.script.BizHtmlFormView","chis.script.util.widgets.MyMessageTip")
chis.application.mhc.script.visit.PregnantVisitFormHtml = function(cfg) {
	cfg.colCount = 4;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 102
	cfg.autoFieldWidth = false;
	this.autoLoadData = true;
	chis.application.mhc.script.visit.PregnantVisitFormHtml.superclass.constructor.apply(this,
			[cfg])
	this.printurl = chis.script.util.helper.Helper.getUrl();
	this.on("doNew", this.onDoNew, this);
    this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
//	this.on("beforeLoadModule", this.beforeLoadModule, this);
	//this.otherDisable = [{fld:"visitResult",type:"radio",control:[{key:"4",exp:"eq",field:["this.gestationMode","this.endDate"]}]}]; 
	  this.createFields = ["visitDate"];
}
var Ctx_=null;//
function doOnblur(id,value,type,otherId){//按钮id,值,属性，需要控制的对象Id 
	if(type=="radio"){
		Ctx_.setRadioType(id,value,type,otherId);
	}else if(type=="checkbox"){
		Ctx_.setCheckboxType(id,value,type,otherId);
	}else{
		Ctx_.doOnclock(id,value);
	}
}
Ext.extend(chis.application.mhc.script.visit.PregnantVisitFormHtml, chis.script.BizHtmlFormView, {
	onBeforeCreate : function() {
		
//		var visitResult = form.findField("visitResult");
//		if (visitResult) {
//			this.onVisitResult(visitResult);
//		}
//		var ifLost = form.findField("ifLost");
//		if (ifLost) {
//			this.setLostReason(ifLost);
//		}
	},

	onDoNew : function() {
		this.pregnantId=this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		
		this.data["pregnantId"] = this.pregnantId;
		this.data["highRisknessesChanged"] = false;
		var schema=this.schema;
			var items = schema.items
			var n = items.length
			for (var i = 0; i < n; i++) {
			var it = items[i]
			if (this.isBackField(this.hiddenField,it.id)) { //隐藏的信息设初始值
				var f = document.getElementById(it.id+ this.idPostfix)
					f.value = "";
				   
			}else if (this.isBackField(this.isOnreaField,it.id)) {//创建的时间类型 
				var cfv = ""
				if(it.id=="visitDate"){
					cfv = this.curDate
					//this.visitDate.setValue(curDate);
			      }else{
			    	  cfv  = "";
			      }
				eval("this." + it.id + ".setValue(cfv)");
			} else {
				if (it.dic) {
					if(it.id=='fetalPosition'){
						this.fetalPosition.clearValue();	
					}else{
						var dicFV = data[it.id];
						var fv = "";  
						var dvs = fv.split(",");
						for (var j = 0, len = dvs.length; j < len; j++) {
							var f = document.getElementById(it.id+ dvs[j] + this.idPostfix);
							if (f) {
								f.checked = true;
							}
						}
					}
				} else {
					var f = document.getElementById(it.id + this.idPostfix)
					if (f) {
						f.style.color = "#999";
						var v = "";
							if(it.properties && it.properties.type=="checkbox"){
								this.setCheckboxValue(it.id, v)
							}else if (it.properties && it.properties.type=="radio"){
								this.setRadioValue(it, v);
							}else{
								if(v=="" || v==null || v=="null"){
									v=f.defaultValue;
								}
								f.value = v;
 
								if (it['not-null'] == "1" || it['not-null'] == "true") {
									if (data[it.id] && data[it.id] != "") {
										this.removeClass(f,"x-form-invalid");
									}
								}
							}
						}
				  }
			  }
			}
			this.addFieldDataValidateFun(schema);//页面非空控制
			var res = util.rmi.miniJsonRequestSync({
				serviceId : "chis.pregnantRecordService",
				serviceAction : "getPregnantGsetational",
				method:"execute",
				body : {
					"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
				}
			})
			if (res.code == 200) {
				this.lastMenstrualPeriod = res.json.body;
					this.setGestationalWeeks(this.visitDate);
			}
			if(this.exContext.args.nextPlan){
			   	this.nextDate.setValue(this.exContext.args.nextPlan.get("planDate"));
			}
			this.riskStore = null;
		this.checkUpStore = null;
		this.fetalStore = null;
		this.description = null;
		this.setTypePb(this.arrey,true);
		  this.visitDate.setValue(this.exContext.args.planDate);
	    this.setHiddenVuale();
		this.nextDate.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(new Date()));
	},

	setOtherGuide : function(combo) {
		var lastValue = combo.getValue();
		var disable = true;
		if (lastValue.indexOf("99") > -1) {
			disable = false;
		}
		this.changeFieldState(disable, "otherGuide");
	},

	referralChange : function(combo) {
		var lastValue = combo.getValue();
		var disable = true;
		if (lastValue == "y") {
			disable = false;
		}
		this.changeFieldState(disable, "referralReason");
		this.changeFieldState(disable, "referralUnit");
	},

	onVisitResult : function(field) {
		var newValue = field.getValue();
		if (newValue == '4' && this.exContext.args.nextDateDisable)// 终止妊娠
		{
			Ext.MessageBox.alert("提示", "只有最后一条记录才可以终止妊娠！")
			field.reset();
			return;
		}
		this.vldVisitResult(newValue);
	},

	vldVisitResult : function(value) {
		var allowBlank = true;
		if (value == '4') {// 终止妊娠，其他灰，妊娠终止原因红。
			allowBlank = false;
		} else {
			allowBlank = true;
		}
		this.changeFieldState(allowBlank, "gestationMode");
		this.changeFieldState(allowBlank, "endDate");
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			if (((item["not-null"] == "1" || item["not-null"] == "true") && (item.id != "gestationMode" && item.id != "endDate"))) {
				this.setFieldNotNull(!allowBlank, item);
			} else if (item.id == "gestationMode" || item.id == "endDate") {
				if (allowBlank) {
					item["not-null"] == "0";
				} else {
					item["not-null"] == "1";
				}
				this.setFieldNotNull(allowBlank, item);
			}
		}
		this.validate();
	},

	setFieldNotNull : function(flage, item) {
		var field = this.form.getForm().findField(item.id);
		if (!field) {
			return;
		}
		field.allowBlank = flage
		if (!flage) {
			Ext.getCmp(field.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + item.alias
							+ ":</span>");
		} else {
			Ext.getCmp(field.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:black'>" + item.alias
							+ ":</span>");
		}
		this.validate()
	},

	onSbpChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var constriction = field.getValue();
		if (!constriction) {
			return;
		}
		if (constriction > 500 || constriction < 10) {
			field.markInvalid("收缩压必须在10到500之间！");
			return;
		}
		var diastolicFld = this.form.getForm().findField("dbp");
		var diastolic = diastolicFld.getValue();
		if (constriction <= diastolic) {
			field.markInvalid("收缩压应该大于舒张压！");
			diastolicFld.markInvalid("舒张压应该小于收缩压！");
			return;
		} else {
			diastolicFld.clearInvalid();
		}
	},

	onDbpChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var diastolic = field.getValue();
		if (!diastolic) {
			return;
		}
		if (diastolic > 500 || diastolic < 10) {
			field.markInvalid("舒张压必须在10到500之间！");
			return;
		}
		var constrictionFld = this.form.getForm().findField("sbp");
		var constriction = constrictionFld.getValue();
		if (constriction <= diastolic) {
			constrictionFld.markInvalid("收缩压应该大于舒张压！");
			field.markInvalid("舒张压应该小于收缩压！");
			return;
		} else {
			constrictionFld.clearInvalid();
		}
	},

	changeManaUnit : function(combo, node) {
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
		this.setManaUnit(result.json.manageUnit)
	},

	setManaUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("hospitalCode");
		if (!combox) {
			return;
		}

		if (!manageUnit) {
			combox.enable();
			combox.reset();
			return;
		}

		combox.setValue(manageUnit)
		combox.disable();
	},

	onLoadData : function(entryName, body) {
		if(body!=null && body.visitId!=null){
			this.initFormData(body);
		}
		this.nextDate.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(new Date()));
		this.nextDate.validate();
	},

	doHighRisk : function(item, e) {
		if (!this.riskStore && this.op == "create") {
			var indexModule = this.midiModules["checkUpModule"];
			var visitBody = this.getFormData();
			var indexBody;
			if (indexModule) {// @@ indexModule不一定打开过需要判断下。
				indexBody = indexModule.getSaveData();
			}
			this.form.el.mask("正在执行初始化，请稍候...", "x-mask-loading")
			util.rmi.jsonRequest({
				serviceId : "chis.pregnantRecordService",
				serviceAction : "initHighRiskReason",
				method:"execute",
				body : {
					"empiId" : this.exContext.ids.empiId,
					"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"],
					"visitRecord" : visitBody,
					"checkList" : indexBody
				}
			}, function(code, msg, json) {
				this.form.el.unmask();
				if (code > 300) {
					this.processReturnMsg(code, msg, this.onOpenHighRiskForm);
					return;
				}
				this.openHighRisknessForm(json.body);
			}, this);
		} else {
			this.openHighRisknessForm();
		}
	},

	openHighRisknessForm : function(risknesses) {
		var module = this.createCombinedModule("HighRiskModule",
				this.refHighRiskModule);
		module.on("moduleClose", this.onModuleClose, this);
		module.__actived = this.riskStore == null ? false : true;
		var args = {
			"visitId" : this.initDataId,
			"week" : this.exContext.args.pregnantWeeks
		};
		if (this.riskStore) {
			args.initRisknesses = null;
		} else if (this.op == "create") {
			args.initRisknesses = risknesses;
		}
		Ext.apply(this.exContext.args, args);
		this.refreshExContextData(module, this.exContext);
		module.getWin().show();
	},

	doCheckUp : function() {
		var module = this.createCombinedModule("checkUpModule",
				this.refCheckUpModule);
		module.__actived = this.checkUpStore == null ? false : true;
		module.on("recordSave", this.onCheckUpSave, this);
		var args = {
			"visitId" : this.initDataId
		};
		this.refreshExContextData(module, this.exContext);
		Ext.apply(module.exContext.args, args);
		module.getWin().show();
	},

	beforeLoadModule : function(moduleName, cfg) {
		if (moduleName == "checkUpModule") {
			cfg.isVisitModule = true;
		}
		cfg.__actived = false;
	},

	doDescription : function() {
		var module = this.midiModules["description"]
		if (!module) {
			var moduleCfg = this.loadModuleCfg(this.refDescriptionForm);
			var cfg = {
				isCombined : true,
				mainApp : this.mainApp,
				exContext : {},
				__actived : false
			}
			Ext.apply(cfg, moduleCfg);
			delete cfg.id
			var cls = moduleCfg.script;
			$import(cls);
			module = eval("new " + cls + "(cfg)");
			module.on("recordSave", this.onGetDescription, this)
			this.midiModules["description"] = module;
		}
		module.__actived = this.description == null ? false : true;
		var args = {
			"visitId" : this.initDataId
		};
		this.refreshExContextData(module, this.exContext);
		Ext.apply(module.exContext.args, args);
		module.getWin().show();
	},

	doFetals : function() {
		var module = this.midiModules["fetalModule"]
		if (!module) {
			var moduleCfg = this.loadModuleCfg(this.refFetalsForm);
			var cfg = {
				autoLoadData : false,
				closeAction : "hide",
				mainApp : this.mainApp,
				exContext : {},
				__actived : false
			};
			Ext.apply(cfg, moduleCfg);
			delete cfg.id
			var cls = moduleCfg.script;
			$import(cls);
			module = eval("new " + cls + "(cfg)");
			module.on("recordSave", this.onFetalsSave, this);
			this.midiModules["fetalModule"] = module
		}
		module.__actived = this.fetalStore == null ? false : true;
		var args = {
			"visitId" : this.initDataId
		};
		this.refreshExContextData(module, this.exContext);
		Ext.apply(module.exContext.args, args);
		var win = module.getWin();
		win.setPosition(350, 200);
		win.show()
	},
	onModuleClose : function(records, store) { 
		var highRiskScore = records.highRiskScore;
		var highRiskLevel = records.highRiskLevel;
		var highRisknesses = records.highRisknesses;
	    this.riskStore = store; 
		this.data["highRisknesses"] = highRisknesses;
		this.data["highRisknessesChanged"] = true;
	    
	    
		var gaoW = document.getElementById("highRiskScore"+this.idPostfix);
		var pingF = document.getElementById("highRiskLevel"+this.idPostfix);
	    if (highRiskScore != null && highRiskScore != "") {
	    	gaoW.value = highRiskScore;
	    	gaoW.style.color = "#000";
	    }else{
	    	gaoW.value = "";
	    	gaoW.style.color = "#999";
	    }
	    if (highRiskLevel != null && highRiskLevel != "") {
	    	pingF.value = highRiskLevel;
	    	pingF.style.color = "#000";
	    }else{
	    	pingF.value = "";
	    	pingF.style.color = "#999";
	    }
		//this.data["highRisknessesChanged"] = true;
	},

	onGetDescription : function() {
		var module = this.midiModules["description"]
		if (module) {
			this.description = module.getFormData();
			module.getWin().hide();
		}
	},

	onCheckUpSave : function() {
		var module = this.midiModules["checkUpModule"]
		if (module) {
			this.checkUpStore = module.getIndexData();
			module.getWin().hide();
		}
	},

	onFetalsSave : function(store, fetailsData) {
		var module = this.midiModules["fetalModule"]
		if (module) {
			this.fetalStore = fetailsData;
			module.getWin().hide();
		}
	},

	doSave : function() {
		var values = this.getFormData();
		if (!values) {
			return;
		}
		 this.key=false
		 this.doVerify(values);
		 if(this.key){
				return;
		 }
		if (values["visitResult"] != '4' && values["ifLost"] != 'y'
				&& !this.data["highRisknessesChanged"]&& !this.initDataId) {
			Ext.Msg.show({
						title : '提示信息',
						msg : "还未进行高危评定,无法保存随访记录!",
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OK,
						multiline : false,
						fn : function() {
							this.doHighRisk();
						},
						scope : this
					});
			return;
		}
		if (this.mainApp.exContext.pregnantMode == 2) {
			var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			if (this.exContext.args.planDate >= now) {
				if (values["nextDate"] != ""
						&& values["nextDate"] <= this.exContext.args.planDate) {
					Ext.MessageBox.alert("提示", "预约日期必须大于计划日期")
					return
				}
			} else {
				if (!this.exContext.args.nextDateDisable
						&& values["nextDate"] != ""
						&& values["nextDate"] <= now) {
					Ext.MessageBox.alert("提示", "预约日期必须大于当前日期")
					return
				}
			}
		}
		Ext.apply(this.data, values);
		values["planId"] = this.exContext.args.planId
		values["planDate"] = this.exContext.args.planDate
		values["sn"] = this.exContext.args.sn
		values["remark"] = this.exContext.args.remark
		values["pregnantWeeks"] = this.exContext.args.pregnantWeeks
		values["highRisknesses"] = this.data["highRisknesses"]
		values["highRisknessesChanged"] = this.data["highRisknessesChanged"]
		values["checkUpList"] = this.checkUpStore
		values["description"] = this.description
		values["fetalsData"] = this.fetalStore
		values["lastMenstrualPeriod"] = this.lastMenstrualPeriod;
		this.saveToServer(values);
	},

	setExcDesc : function(field) {
		var value = field.getValue();
		var disable = true;
		if (value && value == "5") {
			disable = false;
		}
		this.changeFieldState(disable, "exceptionDesc");
		this.changeFieldState(true, "ifLost");
	},

	setLostReason : function(field) {
		var value = field.getValue();
		this.vldLostReason(value);
	},

	vldLostReason : function(value) {
		var allowBlank = true;
		if (value && value == "y") {
			allowBlank = false;
		} else {
			allowBlank = true;
		}
		this.changeFieldState(allowBlank, "lostReason");
		this.changeFieldState(!allowBlank, "visitResult");
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			if (((item["not-null"] == "1" || item["not-null"] == "true") && item.id != "lostReason")) {
				this.setFieldNotNull(!allowBlank, item);
			} else if (item.id == "lostReason") {
				if (allowBlank) {
					item["not-null"] == "0";
				} else {
					item["not-null"] == "1";
				}
				this.setFieldNotNull(allowBlank, item)
			}
		}
		this.validate();
	},

	setGestationalWeeks : function(field) {
		var  date=this.exContext.args.planDate
		
		if (!date) {
			return;
		}
		if (!this.lastMenstrualPeriod) {
			return;
		}
		var weeks = (((date - Date.parseDate(this.lastMenstrualPeriod, "Y-m-d"))
				/ 1000 / 60 / 60 / 24) + 1)
				/ 7;
	//	this.form.getForm().findField("checkWeek").setValue(Math.floor(weeks));
		var checkWeek = document.getElementById("checkWeek"+ this.idPostfix)
		     checkWeek.style.color="#000";
		     checkWeek.disabled=true;
		     checkWeek.value=Math.floor(weeks);
	},

	setButtonEnable : function(status) {
		if (!this.form.getTopToolbar()) {
			return;
		}
		var btns = this.form.getTopToolbar().items;
		for (var i = 0; i < btns.getCount(); i++) {
			var btn = btns.item(i);
			if (status)
				btn.enable()
			else
				btn.disable()
		}
	},

	setSuggestion : function(field) {
		var value = field.getValue();
		var disable = true;
		if (value && value == "2") {
			disable = false;
		}
		this.changeFieldState(disable, "suggestion");
	},getFormData : function(){
		return this.getHTMLFormData(this.schema);
	},
	getHTMLFormData : function(schema) {
		//this.emptyValue();//验证方法
	//	this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		var items = this.schema.items
		var n = items.length 
		var values = {};//
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var v=""
			if (this.isBackField(this.hiddenField,it.id)) { //隐藏的信息设初始值
				v= document.getElementById(it.id+ this.idPostfix).value
			}else if (this.isBackField(this.isOnreaField,it.id)) {//创建的时间类型
				v= eval("this." + it.id + ".getValue()");
				if(it.type=="date"){
					v = Ext.util.Format.date(v,'Y-m-d');
				}
			} else {
				if (it.dic) {
						if(it.id=='fetalPosition')
						{
							var fs =this.fetalPosition;	
							v=fs.getValue();
						}else
						{
						var fs = document.getElementsByName(it.id);							
						var vs = [];
						for (var j = 0, len = fs.length; j < len; j++) {
							var f = fs[j];
							if (f.type == "checkbox" || f.type == "radio") {
								if (f.checked) {
									vs.push(f.value);
								}
							}else if(f.type == "hidden"){
								vs.push(f.value || '');
							}
						}
						v = vs.join(',')
					}
					//	values[it.id] = v;
				} else {
					if(it.properties && it.properties.type=="checkbox"){
						v=this.getCheckboxValue(it.id)
					}else if(it.properties && it.properties.type=="radio"){
						v=this.getRadioValue(it.id)
					}else{
						var f = document.getElementById(it.id+ this.idPostfix)
						if (f) {
							v = f.value || v || '';
							if(v==f.defaultValue){
								v='';
							}
						}
					}
				}
			}
			values[it.id] = v;
		 } 
		return values;
	},

	doPrintVisit : function() {
		alert("孕产妇随访打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
		if (!this.initDataId) {
			return
		}
		var url = "resources/chis.prints.template.pregnantHighRiskVisit.print?type=0&planType=8&visitId="
				+ this.initDataId + "&empiId=" + this.exContext.ids.empiId
				+ "&pregnantId="
				+ this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		url += "&temp=" + new Date().getTime();
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	},
	getHTMLTemplate : function() {
		var html =  //	+'<title>第2～5次产前随访服务记录表</title>'
	 
		 '<body style=" padding:20px;">'// 
		+'<div class="my">'
		+'<table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
		+'  <tr>'
		+' <td style="font-weight:bold;color:#EA0000;" colspan="2">随访日期</td>'
		+'     <td colspan="2"><div '
		+' id="div_visitDate'+this.idPostfix+'"/></td>'
		+'</tr>'
		+'<tr>'
		+'  <td width="8%" style="font-weight:bold;" rowspan="7" >随访管理</td>'
		+' <td style="font-weight:bold;" width="14%" id="ifLost_type'+this.idPostfix+'" >是否失访</td>'
		+' <td><input type="radio" name="ifLost'+this.idPostfix+'" '
	    +' id="ifLost'+this.idPostfix+'"onclick="doOnblur('+"'ifLost'"+',1,'+"'radio'"+','+"'lostReason'"+')" value="1"/>'
	    +'是　　原因：<input type="text"    '
	    +' id="lostReason'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}else{doOnblur('+"'lostReason'"+');this.style.color=\'#000\'}"   style="color:#999"'
		+'name="sl" class="input_btline" />　'
	    +' <input type="radio" name="ifLost'+this.idPostfix+'" '
	    +' id="ifLost'+this.idPostfix+'"onclick="doOnblur('+"'ifLost'"+',2,'+"'radio'"+','+"'lostReason'"+')" value="2"/>否　</td>'
		+'  </tr>'
		+' <tr>'
		+'  <td style="font-weight:bold;" >随访方式</td>'
		+' <td> <input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="1" />门诊  '
		+'<input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="2" />家庭 '
		+'<input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="3" />电话  '
		+'<input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="4" />短信 '
		+' <input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="5" />网络 '
		+'<input type="radio" name="visitMode'+this.idPostfix+'" '
		+' id="visitMode'+this.idPostfix+'"  value="6" onclick="doOnblur('+"'visitMode'"+',6,'+"'checkbox'"+')"/>其他'
		+'</td>'
		+'  </tr>'
		+' <tr>'
		+'  <td style="font-weight:bold;" >随访结果</td>'
		+'  <td><input type="radio" name="visitResult'+this.idPostfix+'" '
		+' id="visitResult'+this.idPostfix+'"  value="1" onclick="doOnblur('+"'visitResult'"+',1)"/>正在治疗'
		+'  <input type="radio"    name="visitResult'+this.idPostfix+'" '
		+' id="visitResult'+this.idPostfix+'"  value="2" onclick="doOnblur('+"'visitResult'"+',2)"/>转为正常'
		+' <input type="radio"    name="visitResult'+this.idPostfix+'" '
		+' id="visitResult'+this.idPostfix+'"  value="3" onclick="doOnblur('+"'visitResult'"+',3)"/>病情加重住院治疗'
		+' <input type="radio"   name="visitResult'+this.idPostfix+'"  '
		+' id="visitResult'+this.idPostfix+'"  value="4" onclick="doOnblur('+"'visitResult'"+',4)"/>已终止妊娠'
		+' <input type="radio"    name="visitResult'+this.idPostfix+'" '
		+' id="visitResult'+this.idPostfix+'"  value="5" onclick="doOnblur('+"'visitResult'"+',5)"/>其他'
		+'<input type="text" '
	    +' id="exceptionDesc'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
		+'name="sl" class="input_btline" />　</td>'
		+' </tr>'
		+'  <tr>'
		+'  <td style="font-weight:bold;" id="gestationMode_div'+this.idPostfix+'">终止妊娠方式</td>'
		+'     <td colspan="2"><div '
		+' id="gestationMode'+this.idPostfix+'"  value="妊娠诊断方法" /></td>'
		+'  </tr>'
		+'  <tr>'
		+'   <td style="font-weight:bold;"  id="endDate_div'+this.idPostfix+'">终止妊娠时间</td>'
		+'     <td colspan="2" id="endDate2'+this.idPostfix+'"><div '
		+' id="endDate'+this.idPostfix+'"  value="终止妊娠时间"/></td>'
		+'  </tr>'
		+' <tr>'
		+'   <td style="font-weight:bold;" >高危评分</td>'
		+'     <td colspan="2"><input type="text" name="gwpf" class="width80 input_btline" '
		+' id="highRiskScore'+this.idPostfix+'"  value="" '
		+ ' onclick=" doOnblur('+"'highRiskScore'"+',1) "'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:155px;color:#999;"'
		+'/></td>'
		+' </tr>'
		+'  <tr>'
		+' <td style="font-weight:bold;" >高危评级</td>'
		+'     <td colspan="2"><input type="text" name="gwpj" class="width80 input_btline" '
		+' id="highRiskLevel'+this.idPostfix+'"  value=""'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:155px;color:#999;"'
		+'/></td>'
		+' </tr>'
		+' <tr>'
		+'<td style="font-weight:bold;" colspan="2">孕周(周)</td>'
	    +' <td><input type="text" name="tbyz" class="width80 input_btline" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:155px;color:#999;"'
		+' id="checkWeek'+this.idPostfix+'"  value="" /></td>'
	    +' </tr>'
	    +'  <tr>'
	    +'  <td style="font-weight:bold;" colspan="2">主诉</td>'
	    +'  <td>'
	    +'<input type="text" '
	    +' id="selfFeelSymptom'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:550px;color:#999;"   '
		+'name="sl" class="input_btline" /></td>'
	    +' </tr>'
	    +' <tr>'
	    +' <td style="font-weight:bold;color:#EA0000;" colspan="2"  id="weight_type'+this.idPostfix+'">体重（kg）</td>'
	    +' <td><input type="text" name="tz" class="width80 input_btline" '
		+' id="weight'+this.idPostfix+'"  value="" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\';}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}else{this.style.border=\'\'}"   style="width:155px;color:#999;"'
		+'/></td>'
	    +' </tr>'
	    
	    +'  <tr>'
	    +'  <td style="font-weight:bold;" rowspan="4" >产科检查</td>'
	    +'  <td style="font-weight:bold;color:#EA0000;" width="12%"  id="heightFundusUterus_type'+this.idPostfix+'">宫底高度（cm）</td>'
	    +'   <td><input type="text" name="tz" class="width80 input_btline" '
		+' id="heightFundusUterus'+this.idPostfix+'"  value="" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}else{this.style.border=\'\'}"   style="width:155px;color:#999;"'
		+'/></td>'
	    +' </tr>'
	    +'   <tr>'
	    +'  <td style="font-weight:bold;color:#EA0000;"  id="abdomenCircumFerence_type'+this.idPostfix+'" >腹围（cm）</td>'
	    +'  <td><input type="text" name="tz" class="width80 input_btline" '
		+' id="abdomenCircumFerence'+this.idPostfix+'"  value="" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}else{this.style.border=\'\'}"   style="width:155px;color:#999;"'
		+'/></td>'
	    +'  </tr>'
	    +'  <tr>'
	    +'  <td style="font-weight:bold;" >胎位</td>'
	    +' <td>'
	    +'<div '
		+' id="fetalPosition'+this.idPostfix+'"  value="胎方位" /></td>'
	    +' </tr>'
	    +' <tr>'
	    +'  <td style="font-weight:bold;" >胎心率（次/分钟）</td>'
	    +'  <td>'
	    +'<input type="text" '
	    +' id="fetalHeartRate'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
		+'name="sl" class="input_btline" /></td>'
	    +'   </tr>'
	    +' <tr>'
	    +'  <td style="font-weight:bold;color:#EA0000;" colspan="2"  id="xueY_type'+this.idPostfix+'">血压（mmHg）</td>'
	    +'  <td><input type="text" name="xy" class="width60 input_btline" '
		+' id="sbp'+this.idPostfix+'"  value="收缩压" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('+"'sbp'"+',1)}else{doOnblur('+"'sbp'"+',2);this.style.border=\'\';}" style="width:72px;color:#999;"'
	    +'/>/<input type="text" name="xy2" class="width60 input_btline" '
		+' id="dbp'+this.idPostfix+'"  value="收张压" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'   
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('+"'dbp'"+',1)}else{doOnblur('+"'dbp'"+',2);this.style.border=\'\';}"   style="width:72px;color:#999;"'
	    +'/></td>'
	    +'  </tr>'
	    +'   <tr>'
	    +'  <td style="font-weight:bold;" colspan="2">血红蛋白（g/L）</td>'
	    +' <td><input type="text" name="sg" class="width60 input_btline" '
		+' id="haemoglobin'+this.idPostfix+'"  value=""'
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:155px;color:#999;"'
		+'/></td>'
	    +' </tr>'
	    +'   <tr>'
	    +'  <td style="font-weight:bold;" colspan="2">尿蛋白</td>'
	    +'  <td><input type="text" name="ndb" class="width60 input_btline"  '
		+' id="albuminuria'+this.idPostfix+'"  value="" '
		+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:155px;color:#999;"'
		+'/> </td>'
	    +'  </tr>'
	    +'   <tr>'
	    +'  <td style="font-weight:bold;" colspan="2" >其他辅助检查&nbsp;<span class="red"></span></td>'
	    +'  <td>'
	    +'<input type="text" '
	    +' id="otherExam'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:295px;color:#999;" '
		+'name="sl" class="input_btline" /></td>'
	    +' </tr>'
	    +' <tr>'
	    +'   <td style="font-weight:bold;" colspan="2">分类</td>'
	    +' <td>'
	    +' <input type="radio" name="category'+this.idPostfix+'" '
	    +' id="category'+this.idPostfix+'"onclick="doOnblur('+"'category'"+',1,'+"'radio'"+')" value="1"/>未见异常　　'
	    +' <input type="radio" name="category'+this.idPostfix+'" '
	    +' id="category'+this.idPostfix+'"onclick="doOnblur('+"'category'"+',2,'+"'radio'"+')" value="2"/>异常'
		+'<input type="text" '
	    +' id="suggestion'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
		+'name="sl" class="input_btline" /></td>'
	    +'  </tr>'
	    +'  <tr>'
	    +'  <td style="font-weight:bold;" colspan="2">指导</td>'
	    +'  <td><input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="01" />个人卫生 <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="03" />膳食  <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="02" />心理  <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="06" />运动 <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="07" />自我监测 <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="08" />分娩准备 <input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="09" />母乳喂养<input type="checkbox" name="guide" '
		+' id="guide'+this.idPostfix+'" value="99" onclick="doOnblur('+"'guide'"+',99,'+"'checkbox'"+')"/>其他'
		+'<input type="text" '
		+' id="otherGuide'+this.idPostfix+'" class="input_btline" style="width:100px;color:#999;" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  value=""' 
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   '
		+'/></td>'
	    +' </tr>'
	    +' <tr>'
	    +'  <td style="font-weight:bold;" rowspan="3">转诊建议</td>'
	    +'  <td  style="font-weight:bold;"></td>'
	    +' <td><input type="radio" name="referral'+this.idPostfix+'" '
		+' id="referral'+this.idPostfix+'"onclick="doOnblur('+"'referral'"+',1,'+"'radio'"+')" value="1"/>无  <input type="radio" name="referral'+this.idPostfix+'" '
		+' id="referral'+this.idPostfix+'"onclick="doOnblur('+"'referral'"+',2,'+"'radio'"+')" value="2"/>有</td>'
	    +'</tr>'
	    +'<tr>'
	    +'  <td style="font-weight:bold;">原因</td>'
	    +'  <td><input type="text"  style="width:480px;color:#999;"  class="input_btline"   '
		+' id="referralReason'+this.idPostfix+'"  value="" '
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  '
		+'/></td>'
	    +' </tr>'
	    +' <tr>'
	    +'  <td style="font-weight:bold;">机构及科室</td>'
	    +'  <td><input  style="width:480px;color:#999;"    class="input_btline" '
		+' id="referralUnit'+this.idPostfix+'" type="text" size="50" value=""'
		+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
		+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   '
		+'/></td>'
	    +'</tr>'
	    +'  <tr>'
	    +'  <td style="font-weight:bold;" colspan="2">下次访视日期</td>'
	    +'  <td><div id="nextDate'+this.idPostfix+'"></div></td>'
	    +'  </tr>'
	    +'<tr>'
	    +'  <td style="font-weight:bold;" colspan="2">随访医生签名</td>'
	    +'  <td><div id="doctorId'+this.idPostfix+'"></div></td>'
	    +' </tr>'
	    +' </table>'
	    +' </div>'
	    +'<input value="孕妇档案编号" type="hidden"  id="pregnantId'+this.idPostfix+'" />'
		+'<input value="EMPIID" type="hidden"  id="empiId'+this.idPostfix+'" />'
		+'<input value="检查机构" type="hidden"  id="hospitalCode'+this.idPostfix+'" />'
		+'<input value="录入机构" type="hidden"  id="createUnit'+this.idPostfix+'" />'
		+'<input value="录入人" type="hidden"  id="createUser'+this.idPostfix+'" />'
		+'<input value="最后修改机构" type="hidden"  id="lastModifyUnit'+this.idPostfix+'" />'
		+'<input value="最后修改人" type="hidden"  id="lastModifyUser'+this.idPostfix+'" />'
		+'<input value="最后修改日期" type="hidden"  id="lastModifyDate'+this.idPostfix+'" />'
		+'<input value="随访序号" type="hidden"  id="visitId'+this.idPostfix+'" />'
	    +'</body>';
	//this.html = html;
	return html;																
	},doOnclock :function(id,value){
		var dom = document.getElementById(id+this.idPostfix);
		   if(id=="weight" || id=="weight"){
					var v=value;
					if(id=="weight"){
						if (v != null && v != "") {
							dom.value = v;
						    dom.style.color = "#000";
						    dom.style.border = "";
						    var gao=this.height/100;
							var bmi=v/gao/gao;
							var bmi =bmi.toFixed(2);
							var mbBmi=document.getElementById("bmi"+this.idPostfix)
							mbBmi.value=bmi;
							mbBmi.style.color = "#000";
						} else { 
								dom.value = dom.defaultValue
								dom.style.color = "#999";
						}
					}else if(id=="weight"){
						if (v != null && v != "") {
							dom.value = v;
						    dom.style.color = "#000";
						    
						    var gao=this.height/100;
							var bmi=v/gao/gao;
							var bmi =bmi.toFixed(2);
							var mbBmi=document.getElementById("mbBmi"+this.idPostfix)
							mbBmi.value=bmi;
							mbBmi.style.color = "#000";
						} else { 
								dom.value = dom.defaultValue
								dom.style.color = "#999";
						}
				}
			  }else if(id=="sbp" || id=="dbp"){
				var sbp=document.getElementById("sbp"+this.idPostfix)//收缩压
				var dbp=document.getElementById("dbp"+this.idPostfix)//舒张压
				var v=sbp.value;
				var k=dbp.value;
				if(id=="sbp" && value==2){
					 k=parseInt(k);
					 v=parseInt(v);
					if (v > 500 || v < 10) {
					  MyMessageTip.msg("提示", "收缩压必须在10到500之间！",true);
						return;
					}
					if (k>v) {
						MyMessageTip.msg("提示", "收缩压应该大于舒张压！",true);
						return;
					}
					
				}else if(id=="dbp" && value==2){
					 k=parseInt(k);
					 v=parseInt(v);
					if (k > 500 || k < 10) {
						MyMessageTip.msg("提示", "舒张压(mmHg)必须在10到500之间！",true);
						return;
					}
					if (k>v) {
							MyMessageTip.msg("提示", "收缩压应该大于舒张压！",true);
							return;
						}
				}
			}else if(id=="highRiskScore"){
				this.openHighRisknessForm();
			}else if(id=="visitResult"){//随访结果控制
	                    this.KzVisitResult(id,value);
			}else if(id=="lostReason"){
				if(dom.value!="" && dom.value!="null"  && dom.value!=null){
					      dom.style.border = "";
						   dom.style.color = "#999";
				}
						
			}else{
				var key=dom.value;
				//key=this.ZzValue("3", key);
				if(key=="false"){
					dom.value=dom.defaultValue;
					dom.style.color = "#999";
					return;
				}
			}
		},setRadioType :function(id,on,type,otherId){
			var dom = document.getElementsByName(id+this.idPostfix);
			var le = dom.length;
			var domId = document.getElementById(id+this.idPostfix);
			var key=eval("this."+id+"Value");
			eval( "this."+id+"Value=on;");
			if(on==key){ 
				if(id=="ifLost"){
				   return
				}
				for (var i = 0; i < le; i++) {
					if (dom[i].checked==true) {
						dom[i].checked=false;
						eval( "this."+id+"Value=\"\";");
					}
				}
				  if( id=="referral"){
	                    this.KzVisitResult(id,on);
	                 }
			}else{ 
				  if( id=="visitResult" ||  id=="ifLost" || id=="category" || id=="referral"){
	                    this.KzVisitResult(id,on);
	                 }
			}
		},setCheckboxType :function(id,on,type){
			if(id=="visitMode" && on=="9" && this.visitMode!="9"){
				 other="";
				 this.visitMode="5";
				var visitMode = document.getElementById(other+ this.idPostfix)
				  if (visitMode) { 
					  visitMode.disabled = false;
				  }
			  }else if(id=="visitMode" && on=="9" && this.visitMode=="9"){
				  other="";
					var visitMode = document.getElementById(other+ this.idPostfix)
					  if (visitMode) { 
						  visitMode.disabled = true;
						  visitMode.value=visitMode.defaultValue;
						  visitMode.style.color="#999";
			   }
			  }	 
			 if(id=="visitResult" && on=="5" && this.exceptionDesc!="5"){
				    other="exceptionDesc";
				    this.exceptionDesc="5";
					var exceptionDesc = document.getElementById(other+ this.idPostfix)
					  if (exceptionDesc) { 
						  exceptionDesc.disabled = false;
					  }
			  }else if(id=="visitResult" && on=="5" && this.exceptionDesc=="5"){
				  other="exceptionDesc";
				  this.exceptionDesc="";
					var exceptionDesc = document.getElementById(other+ this.idPostfix)
					  if (exceptionDesc) { 
						  exceptionDesc.disabled = true;
						  exceptionDesc.value=exceptionDesc.defaultValue;
						  exceptionDesc.style.color="#999";
			      } 
			  }
			 if(id=="guide" && on=="99" && this.otherGuide=="99"){
				    other="otherGuide";
				    this.otherGuide="";
					 var otherGuide = document.getElementById(other+ this.idPostfix)
					  if (otherGuide) { 
						  otherGuide.disabled = true;
						  otherGuide.value=otherGuide.defaultValue;
						  otherGuide.style.color="#999";
					  }
			  }else if(id=="guide" && on=="99" && this.otherGuide!="99"){
				  other="otherGuide";
				  this.otherGuide="99";
					 var otherGuide = document.getElementById(other+ this.idPostfix)
					  if (otherGuide) { 
						  otherGuide.disabled = false;
			          }
			  }
		},
		initFormData:function(data){
			var schema=this.schema;
		//	this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
			var items = schema.items
			var n = items.length
			for (var i = 0; i < n; i++) {
			var it = items[i]
			if (this.isBackField(this.hiddenField,it.id)) { //隐藏的信息设初始值
				this.setHiddenVuale(data,it.id);
			}else if (this.isBackField(this.isOnreaField,it.id)) {//创建的时间类型 
				var cfv = data[it.id]
				
				if(!cfv){
					cfv="";
				}
				if(it.type=="date"){
					cfv = Ext.util.Format.date(cfv,'Y-m-d');
				}
				eval("this." + it.id + ".setValue(cfv)");
			} else {
				 
				if (it.dic) {
					//continue;
					if(it.id=='fetalPosition'){
						this.fetalPosition.setValue(data[it.id]);	
					}else{
					if (!this.fireEvent("dicFldSetValue",it.id,data)) {
						continue;
					} else {
						var dicFV = data[it.id];
						var fv = ""; 
						if (dicFV && dicFV.key!=null) {
							fv = dicFV.key;
						}
						var dvs = fv.split(",");
						for (var j = 0, len = dvs.length; j < len; j++) {
							if(it.id=='guide')
							{
								var f = document.getElementsByName(it.id);
								for(var kk=0,kl=f.length;kk<kl;kk++)
								{
									if(f[kk].value==dvs[j])
										f[kk].checked=true;
								}
								
							}
							else
							{
							var f = document.getElementById(it.id+ dvs[j] + this.idPostfix);
							if (f) {
								f.checked = true;
							}
							if(it.other && it.otherKey){
								var other=it.other;
								var otherKey =it.otherKey;
								var vluer=dvs[j];
								if( vluer==otherKey){
									  eval("this." + it.id + "=vluer");
									var visitMode = document.getElementById(other+ this.idPostfix)
									  if (visitMode) { 
										  visitMode.disabled = false;
									  }
							   }else if(vluer!=otherKey){
								    vluer="";
								    eval("this." + it.id + "=vluer");
									var visitMode = document.getElementById(other+ this.idPostfix)
									  if (visitMode) { 
										  visitMode.disabled = true;
									  }
							  }
							}
							
							}
					  }
					}
					
					}
				} else {
					var f = document.getElementById(it.id +this.idPostfix)
					if (f) {
						var v = data[it.id];
							if(it.properties && it.properties.type=="checkbox"){
								this.setCheckboxValue(it.id, v)
							}else if (it.properties && it.properties.type=="radio"){
								this.setRadioValue(it, v);
							}else{
								if(v=="" || v==null || v=="null"){
									v=f.defaultValue;
								}
								f.value = v;
								if(v!=null && v!="" && v!=f.defaultValue){//初始有值不變灰
									f.style.color = "#000";
								}
								if (it['not-null'] == "1" || it['not-null'] == "true") {
									if (data[it.id] && data[it.id] != "") {
										this.removeClass(f,"x-form-invalid");
									}
								}
							}
						}
				}
			}
		  }
		},doVerify : function(data){
			//保存验证
		  var  ifLost=this.getRadioValue("ifLost");//是否失访
		   if(ifLost==1){
		   	   var visitDate =data.visitDate;
	           if(visitDate==null || visitDate ==""){
					this.key=true
					this.visitDate.focus();
					MyMessageTip.msg("提示", "随访日期不能为空!",true); 
					return;
				}
			   var lostReason =data.lostReason;//失访原因
	           if(lostReason==null || lostReason ==""){
					this.key=true
					this.visitDate.focus();
						var domJd =  document.getElementById("lostReason"+this.idPostfix);
							 domJd.focus();
					MyMessageTip.msg("提示", "失访原因不能为空!",true);
					return;
				}
		   }else{
			   var visitDate =data.visitDate;
	           if(visitDate==null || visitDate ==""){
					this.key=true
					this.visitDate.focus();
					MyMessageTip.msg("提示", "随访日期不能为空!",true);
					return;
				}
				
			   var visitResult =data.visitResult;
				if(visitResult=="4"){
					var gestationMode =data.gestationMode;
						 if(gestationMode==null || gestationMode ==""){
							this.key=true
							 this.gestationMode.focus();
							MyMessageTip.msg("提示", "妊娠确诊方法不能为空!",true);
							return;
				    	}
				    	
					  var endDate =data.endDate;
					 if(endDate==null || endDate ==""){
							this.key=true
							 this.endDate.focus();
							MyMessageTip.msg("提示", "妊娠确诊时间不能为空!",true);
							return;
				    	}
				}else{
				       var weight =data.weight;
			           if(weight==null || weight ==""){
							this.key=true
						
							MyMessageTip.msg("提示", "基础体重(kg)不能为空!",true);
							var weight =  document.getElementById("weight"+this.idPostfix);
							    weight.focus();
							    weight.style.border = "1px solid red";
							return;
						}
			           var heightFundusUterus =data.heightFundusUterus;
			           if(heightFundusUterus==null || heightFundusUterus ==""){
							this.key=true
							MyMessageTip.msg("提示", "宫高(cm)不能为空!",true);
							var heightFundusUterus =  document.getElementById("heightFundusUterus"+this.idPostfix);
						          heightFundusUterus.focus();
							    heightFundusUterus.style.border = "1px solid red";
							return;
						}
			           var abdomenCircumFerence =data.abdomenCircumFerence;
			           if(abdomenCircumFerence==null || abdomenCircumFerence ==""){
							this.key=true
							MyMessageTip.msg("提示", "腹围(cm)不能为空!",true);
							var abdomenCircumFerence =  document.getElementById("abdomenCircumFerence"+this.idPostfix);
							  abdomenCircumFerence.focus();
							    abdomenCircumFerence.style.border = "1px solid red";
							return;
						}
			           var sbp =data.sbp;
			           if(sbp==null || sbp ==""){
							this.key=true
							MyMessageTip.msg("提示", "收缩压(mmHg)不能为空!",true);
							var sbp =  document.getElementById("sbp"+this.idPostfix);
						         sbp.focus();
							     sbp.style.border = "1px solid red";
							return;
						}
			           var dbp =data.dbp;
			           if(dbp==null || dbp ==""){
							this.key=true
							MyMessageTip.msg("提示", "收缩压(mmHg)不能为空!",true);
							var dbp =  document.getElementById("dbp"+this.idPostfix);
							    dbp.focus();
							    dbp.style.border = "1px solid red";
							return;
						}
					var  k=parseInt(dbp);
					var v=parseInt(sbp);
					if (v > 500 || v < 10) {
						this.key=true
					  MyMessageTip.msg("提示", "收缩压必须在10到500之间！",true);
						return;
					}
					if (k>v) {
						
						this.key=true
						MyMessageTip.msg("提示", "收缩压应该大于舒张压！",true);
						return;
					}
					if (k > 500 || k < 10) {
						this.key=true
						MyMessageTip.msg("提示", "舒张压(mmHg)必须在10到500之间！",true);
						return;
					}
					if (k>v) {
						this.key=true
							MyMessageTip.msg("提示", "收缩压应该大于舒张压！",true);
							return;
						}
				     }
				}
	},
		isBackField : function(arly,fldId) {
			var isCF = false;
			var len = arly.length;
			for (var i = 0; i < len; i++) {
				var cf =arly[i];
				if (cf == fldId) {
					isCF = true;
					break;
				}
			}
			return isCF;
		},setHiddenVuale : function(data,itId) {
			if(data=="" || data ==null || data =="null"){
				  //empiId  this.pregnantId
				var eValue = this.exContext.ids["empiId"];
				var empiId = document.getElementById("empiId"+this.idPostfix);
				    empiId.value = eValue; 
				    
			    //孕妇档案编号
				var pregnantId = document.getElementById("pregnantId"+ this.idPostfix);
			    	pregnantId.value = this.pregnantId;     
			    	
			     //最后机构
				var hospitalCode = document.getElementById("hospitalCode"+this.idPostfix);
				    hospitalCode.value = this.mainApp.deptId; 
				//录入机构
				var createUnit = document.getElementById("createUnit"+ this.idPostfix);
				    createUnit.value = this.mainApp.deptId; 
				//录入
				var createUser = document.getElementById("createUser"+ this.idPostfix);
				    createUser.value = this.mainApp.uid; 
				//最后修改机构;
				var lastModifyUnit = document.getElementById("lastModifyUnit"+ this.idPostfix);
				    lastModifyUnit.value =this.mainApp.deptId; 
			  //最后修改人
				var lastModifyUser = document.getElementById("lastModifyUser"+ this.idPostfix);
				    lastModifyUser.value = this.mainApp.uid; 
			  //最后修改日期;
				var lastModifyDate = document.getElementById("lastModifyDate"+this.idPostfix);
				    lastModifyDate.value = this.mainApp.serverDateTime; 
				 
				 this.doctorId.setValue( this.mainApp.uid);
			}else{
				var f = document.getElementById(itId + this.idPostfix)
				this.doctorId.setValue( this.mainApp.uid);
				var v='';
				if(data!="" && data !=null && data !="null"){
					var key=data[itId];
					if(key!="" && key !=null && key !="null"){
							if(itId=="hospitalCode"){//随访机构
								v= data[itId].key;
							}else if(itId=="createUnit"){//录入机构
								v= data[itId].key;
							}
							else if(itId=="createUser"){//录入人
								v= data[itId].key;
							}
							else if(itId=="lastModifyUnit"){//最后修改机构
								v=this.mainApp.deptId
							}
							else if(itId=="lastModifyUser"){//最后修改人
								v= data[itId].key;
							}else if(itId=="lastModifyDate"){//最后修改日期
								v=this.mainApp.serverDateTime
							}else if(itId=="pregnantId"){//孕妇档案编号
								v=this.pregnantId
							}else{
								v= key;
							}
				  }else{
						v=''
				  }
				}else{
					v=''
			  }
			  f.value = v;	
			}
	},
	getRadioValue : function(id){
		var dom = document.getElementsByName(id+this.idPostfix);
		var le = dom.length;
		var v="";
		for (var i = 0; i < le; i++) {
			if(dom[i].checked == true){
				v=dom[i].value
			}
		}
		return v
	},
	getCheckboxValue :function (name){
		var dom = document.getElementsByName(name+this.idPostfix);
		var le = dom.length;
		var v="";//5,6,8,9
		for (var j = 0; j < le; j++) {
			if(dom[j].checked == true){
				if(v==""){
					v=dom[j].value
				}else{
					v=v+","+dom[j].value;
				}
			}
		}
		return v
	},
	// radio类型的赋值
	setRadioValue : function(it, v) { 
		var dom = document.getElementsByName(it.id+this.idPostfix);
		 var le = dom.length;
		 if(v=="" || v==null){ 
		 	     for (var j = 0; j < le; j++) {
		 	           	dom[j].checked =false;
		 	     }
		  }else{  
		 		 for (var j = 0; j < le; j++) {
		               if(it.other && it.otherKey){
								var other=it.other;
								var otherKey =it.otherKey;
								var vluer=v;
								if( vluer==otherKey){
									  eval("this." + it.id + "=vluer");
									var visitMode = document.getElementById(other+ this.idPostfix)
									  if (visitMode) { 
										  visitMode.disabled = false;
									  }
							   }else if(vluer!=otherKey){
								    vluer="";
								    eval("this." + it.id + "=vluer");
									var visitMode = document.getElementById(other+ this.idPostfix)
									  if (visitMode) { 
										  visitMode.disabled = true;
									  }
							  }
		        }
		         if(v==	dom[j].defaultValue){
		 	 	 	dom[j].checked =true;
		 	      }
		 }
	  }
	  if( it.id=="visitResult" ||  it.id=="ifLost"  ||  it.id=="referral"  ||  it.id=="category"){
	      this.KzVisitResult(it.id,v);
	  }
		
	   // eval( "this."+id+"Value=v;"); disabled
	},
	// Checkbox类型的赋值
	setCheckboxValue :function (id,v){
		var dom = document.getElementsByName(id+this.idPostfix);
		var k = dom.length;
		if(v!=null && v!=""){
			if(v.length==1){
				for (var j = 0; j < k; j++) {
					if (v == dom[j].value) {
						dom[j].checked = true;
						break;
					}
				}
			}else{
				if (v.indexOf(",") > -1) {
					value = v.split(",");
					var l = value.length;
					for (var i = 0; i < l; i++) {
						for (var j = 0; j < k; j++) {
							if (value[i] == dom[j].value) {
								dom[j].checked = true;
								break;
							}
						}
					}
				}
			}
		}
	 },setTypePb : function(arrey,key){
            var len = arrey.length;
				for (var i = 0; i < len; i++) {
					var cf =arrey[i];
					  var fdiv = document.getElementById(cf+this.idPostfix);
					  if(key==true){
					  	   fdiv.style.color = "#EA0000";
					  }else{
					  	 fdiv.style.color = "#000";
		              }
		 }
		 
	 },setTypeXhx :function(arrey,key){
	 	   var len = this.yanZ.length;
				for (var i = 0; i < len; i++) {
					var cf =this.yanZ[i]; 
					  var fdiv = document.getElementById(cf+this.idPostfix);
					  if(key==true && (fdiv.value==null || fdiv.value ==""  || fdiv.value ==fdiv.defaultValue)){
						  	   this.addClass(fdiv, "x-form-invalid");
						  }else{
						  	this.removeClass(fdiv, "x-form-invalid");
						  	fdiv.style.border = "";
			       }
			 }
	 },
	addFieldAfterRender : function() {
				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 250,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							maxValue : curDate,
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_visitDate" + this.idPostfix)
						});
			   //随访医生
		   this.doctorId = this.createDicField({
				"width" : 250,
				"defaultIndex" : 0,
				"id" : "chis.dictionary.user",
				"render" : "Tree",
				"selectOnFocus" : true,
				"onlySelectLeaf" : true,
				"parentKey" : "%user.manageUnit.id",
				"defaultValue" : {
					"key" : this.mainApp.uid,
					"text" : this.mainApp.uname + "--"
							+ this.mainApp.jobtitle
				}
			});
		        this.doctorId.render("doctorId" + this.idPostfix);
		//        
		    var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.endDate = new Ext.form.DateField({
									name : 'endDate' + this.idPostfix,
									width : 155,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '终止妊娠时间',
									maxValue:curDate,
									fieldLabel : "终止妊娠时间",
									renderTo : Ext.get("endDate" + this.idPostfix)
								});
				//下次随访日期
				this.nextDate = new Ext.form.DateField({
									name : 'nextDate' + this.idPostfix,
									width : 250,
									altFormats : 'Y-m-d',
									format : 'Y-m-d',
									emptyText : '下次随访日期',
									sixValue:curDate,
									fieldLabel : "下次随访日期",
									renderTo : Ext.get("nextDate" + this.idPostfix)
								});
			  //妊娠确诊方法
				   this.gestationMode = this.createDicField({
						"width" : 155,
						"defaultIndex" : 0,
						"id" : "chis.dictionary.gestationMode",
						"render" : "Tree",
						"selectOnFocus" : true,
						"parentKey" : "%chis.dictionary.gestationMode",
						"defaultValue" : {
							"key" : "%chis.dictionary.gestationMode"
							//"text" : "%chis.dictionary.gestationMode"
						}
					});
			   this.gestationMode.render("gestationMode" + this.idPostfix);  
			   
			   
			   //胎位
			   this.fetalPosition = this.createDicField({
					"width" : 155,
					"defaultIndex" : 0,
					"id" : "chis.dictionary.CV5105_01",
					"selectOnFocus" : true
				});
			   this.fetalPosition.render("fetalPosition" + this.idPostfix);  
			   
			   
			   
			   var isOnreaField=["doctorId","visitDate"
				                  ,"endDate","nextDate","gestationMode"];
				this.isOnreaField=isOnreaField;//字典创建
				var hiddenField=["pregnantId","empiId","hospitalCode","createUnit","visitId"
				                  ,"createUser","lastModifyUnit","lastModifyUser","lastModifyDate"];
		 	   this.hiddenField=hiddenField; //隐藏字段
		 	   
		 	   var yanZ=["weight","abdomenCircumFerence","heightFundusUterus","sbp","dbp"];
		 	   this.yanZ=yanZ;//必填字体
			  var arrey=["xueY_type","abdomenCircumFerence_type","heightFundusUterus_type","weight_type"];
			   this.arrey=arrey;//not-null 验证
		//		
		 	  var highRiskLevel = document.getElementById("highRiskLevel"+this.idPostfix);
				highRiskLevel.disabled = true;
				
			//	this.createFields=[];
				this.controlOtherFld();
		 	 
				var serverDate = this.mainApp.serverDate;
				this.visitDate.maxValue = Date.parseDate(serverDate, "Y-m-d");
				  Ctx_=this;
			},KzVisitResult :function  (id,v){
			     if(id=="visitResult"){
					var gestationMode = document.getElementById("gestationMode_div"+this.idPostfix);
					var endDate2 = document.getElementById("endDate_div"+this.idPostfix);
					 var exceptionDesc = document.getElementById("exceptionDesc"+this.idPostfix);
					if(v=="4"){
							  exceptionDesc.disabled = true;
							 this.setTypeXhx( this.yanZ,false);
							this.setTypePb(this.arrey,false);
							   var pbIfLost=this.getRadioValue("ifLost");
						      if(pbIfLost!="1"){
						      	  gestationMode.style.color = "#EA0000";
						         endDate2.style.color = "#EA0000";
						      }
							 this.gestationMode.enable();
			                this.endDate.enable();
			                exceptionDesc.value="";
					}else if(v=="5"){
					           exceptionDesc.disabled = false;
					            gestationMode.style.color = "#000";
						       endDate2.style.color = "#000";
						      var pbIfLost=this.getRadioValue("ifLost");
						      if(pbIfLost!="1"){
						      	this.setTypeXhx( this.yanZ,true);
								this.setTypePb(this.arrey,true);
						      }
								 this.gestationMode.disable();
			                   this.endDate.disable();
			                    this.gestationMode.setValue("");
			                     this.endDate.setValue("");
					}else{
						 exceptionDesc.disabled = true;
						      gestationMode.style.color = "#000";
						       endDate2.style.color = "#000";
						         var pbIfLost=this.getRadioValue("ifLost");
						      if(pbIfLost!="1"){
						      	this.setTypeXhx( this.yanZ,true);
								this.setTypePb(this.arrey,true);
						      }
								 this.gestationMode.disable();
			                   this.endDate.disable();
			                   this.gestationMode.setValue("");
			                     this.endDate.setValue("");
					                exceptionDesc.value="";
					}
				 }else if(id=="ifLost"){
					     var ifLost_type = document.getElementById("ifLost_type"+ this.idPostfix)
					         var lostReason = document.getElementById("lostReason"+ this.idPostfix)
						  if(id=="ifLost" && v==1){
						 	         	this.addClass(lostReason, "x-form-invalid"); 
										    lostReason.disabled = false;
								    	 this.setTypePb(this.arrey,false);
								    	  ifLost_type.style.color="#EA0000";
									   this.setTypeXhx( this.yanZ,false);
									   var gestationMode = document.getElementById("gestationMode_div"+this.idPostfix);
				                    	var endDate2 = document.getElementById("endDate_div"+this.idPostfix);
					                             gestationMode.style.color = "#000";
						                       endDate2.style.color = "#000";
						                       var dom = document.getElementsByName("visitResult"+this.idPostfix);
													 var le = dom.length;
													 	     for (var j = 0; j < le; j++) {
													 	           	dom[j].checked =false;
													 	           	dom[j].disabled =true;
													 	     }
						  }else {
						    
										   this.removeClass(lostReason, "x-form-invalid");
										    ifLost_type.style.color="#000";
										      lostReason.disabled = true;
										      var pbKey=this.getRadioValue("visitResult");
										     if(pbKey!="4"){
										          	this.setTypeXhx( this.yanZ,true);
													this.setTypePb(this.arrey,true);
										     }
										      var dom = document.getElementsByName("visitResult"+this.idPostfix);
													 var le = dom.length;
													 	     for (var j = 0; j < le; j++) {
													 	           	dom[j].disabled =false;
													 	     }
											     
						  }
			  }else  if(id=="category"){
							var other="suggestion";
							var suggestion = document.getElementById(other+ this.idPostfix)
							  if (suggestion) {
								  if(v==2){
									  suggestion.disabled = false;
								  }else{
									  suggestion.value=suggestion.defaultValue;
									  suggestion.style.color="#999";
									  suggestion.disabled = true;
								  }
							  }
			   }else if(id=="referral"){
							var other="referralReason";
							var referralReason = document.getElementById(other+ this.idPostfix)
							var referralUnit = document.getElementById("referralUnit"+ this.idPostfix)
							  if (referralReason) {
								  if(v==2){
									  referralReason.disabled = false;
									  referralUnit.disabled = false;
								  }else{
									  referralReason.value=referralReason.defaultValue;
									  referralReason.style.color="#999";
									  referralReason.disabled = true;
									  referralUnit.value=referralUnit.defaultValue;
									  referralUnit.style.color="#999";
									  referralUnit.disabled = true;
								  }
							  }
			  }
   }
});