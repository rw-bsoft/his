/**
 * 孕妇随访信息表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")
chis.application.mhc.script.visit.PregnantVisitForm = function(cfg) {
	cfg.colCount = 4;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 102
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.visit.PregnantVisitForm.superclass.constructor.apply(this,
			[cfg])
	this.printurl = chis.script.util.helper.Helper.getUrl();
	this.on("doNew", this.onDoNew, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
	this.on("beforeLoadModule", this.beforeLoadModule, this);
}
Ext.extend(chis.application.mhc.script.visit.PregnantVisitForm, chis.script.BizTableFormView, {

	onBeforeCreate : function() {
		this.data["empiId"] = this.exContext.ids.empiId
		this.data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		var form = this.form.getForm();
		var examDate = form.findField("visitDate")
		if (examDate) {
			this.setGestationalWeeks(examDate);
		}
		var visitResult = form.findField("visitResult");
		if (visitResult) {
			this.onVisitResult(visitResult);
		}
		var ifLost = form.findField("ifLost");
		if (ifLost) {
			this.setLostReason(ifLost);
		}
	},

	onDoNew : function() {
		var module = this.midiModules["HighRiskModule"]
		if (module) {
			delete module.exContext.args.initRisknesses;
		}
		var checkModule = this.midiModules["checkUpModule"];
		if (checkModule) {
			checkModule.reset();
		}

		// ** 高危因素
		this.data["highRisknesses"] = [];
		this.data["highRisknessesChanged"] = false;

		this.riskStore = null;
		this.checkUpStore = null;
		this.fetalStore = null;
		this.description = null;

		if (!this.lastMenstrualPeriod) {
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
			}
		}

		var form = this.form.getForm();
		var nextTime = form.findField("nextDate");
		if (nextTime) {
			if (this.exContext.args.nextDateDisable) {
				nextTime.disable()
			}
			if (this.mainApp.exContext.pregnantMode == 1) {
				if (this.exContext.args.nextPlan) {
					nextTime.enable();
					if (!this.initDataId) {
						nextTime.setValue(this.exContext.args.nextPlan
								.get("planDate"));
					}
				} else {
					nextTime.reset();
					nextTime.disable();
				}
			}
			var minNextVisitDate = this.exContext.args.minNextVisitDate;
			if (minNextVisitDate) {
				nextTime.enable()
				nextTime.setMinValue(minNextVisitDate);
			} else {
				nextTime.disable();
			}
		}

		var visitDate = form.findField("visitDate");
		if (visitDate) {
			if (this.exContext.args.nextDateDisable) {
				visitDate.disable()
			} else {
				visitDate.enable()
				visitDate.setMinValue(this.exContext.args.minVisitDate);
				visitDate.setMaxValue(this.exContext.args.maxVisitDate);
				if (!this.initDataId) {
					visitDate.setValue(this.exContext.args.planDate);
				}
			}
		}
		var fetalPosition = form.findField("fetalPosition");
		if (fetalPosition) {
			if (this.exContext.args.fetalDisable) {
				fetalPosition.disable();
			} else {
				fetalPosition.enable();
			}
		}

		var fetalPositionFlag = form.findField("fetalPositionFlag");
		if (fetalPositionFlag) {
			if (this.exContext.args.fetalDisable) {
				fetalPositionFlag.disable();
			} else {
				fetalPositionFlag.enable();
			}
		}

	},

	onReady : function() {
		chis.application.mhc.script.visit.PregnantVisitForm.superclass.onReady.call(this)
		var form = this.form.getForm();

		var category = form.findField("category");
		if (category) {
			category.on("select", this.setSuggestion, this);
		}

		var ifLost = form.findField("ifLost");
		if (ifLost) {
			ifLost.on("select", this.setLostReason, this);
		}

		var visitResult = form.findField("visitResult");
		if (visitResult) {
			visitResult.on("select", this.setExcDesc, this);
			visitResult.on("select", this.onVisitResult, this);
			visitResult.on("change", this.onVisitResult, this);
		}
		var sbp = form.findField("sbp");
		if (sbp) {
			sbp.on("blur", this.onSbpChange, this);
			sbp.on("keyup", this.onSbpChange, this);

		}
		var dbp = form.findField("dbp");
		if (dbp) {
			dbp.on("blur", this.onDbpChange, this);
			dbp.on("keyup", this.onDbpChange, this);
		}

		var examDate = form.findField("visitDate");
		if (examDate) {
			examDate.on("valid", this.setGestationalWeeks, this);
		}

		var doctorId = form.findField("doctorId");
		if (doctorId) {
			doctorId.on("select", this.changeManaUnit, this);
		}

		var guide = form.findField("guide");
		if (guide) {
			guide.on("select", this.setOtherGuide, this);
		}

		var referral = form.findField("referral");
		if (referral) {
			referral.on("select", this.referralChange, this);
		}

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
		var category = body["category"]
		if (category&&category.key) {
			var disable = true;
			if (category.key == "2") {
				disable = false;
			}
			this.changeFieldState(disable, "suggestion");
		}

		var ifLost = body["ifLost"]
		if (ifLost) {
			var disable = true;
			var key = ifLost.key;
			if (key && key == "y") {
				disable = false;
				this.vldLostReason(key);
			}
			this.changeFieldState(disable, "lostReason");
		}

		var visitResult = body["visitResult"]
		if (visitResult) {
			var disable = true;
			var key = visitResult.key;
			if (key) {
				this.changeFieldState(true, "ifLost");
			}
			if (key == "5") {
				disable = false;
			}
			this.changeFieldState(disable, "exceptionDesc");
			disable = true;
			if (key == "4") {
				this.vldVisitResult(key);
			}
		}

		var guide = body.guide;
		if (guide) {
			var disable = true;
			var key = guide.key;
			if (key && key.indexOf("99") > -1) {
				disable = false;
			}
			this.changeFieldState(disable, "otherGuide");
		}

		var referral = body["referral"]
		if (referral && referral.key) {
			var disable = true;
			if (referral.key == "y") {
				disable = false;
			}
			this.changeFieldState(disable, "referralReason");
			this.changeFieldState(disable, "referralUnit");
		}

		var nextDate = this.form.getForm().findField("nextDate");
		var createDate = body["createDate"];
		if (nextDate && createDate) {
			var d = Date.parseDate(createDate.substr(0, 10), "Y-m-d")
			nextDate.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(d));
			nextDate.validate();
		}

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
		this.refreshExContextData(module, this.exContext);
		Ext.apply(module.exContext.args, args);
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
		this.exContext.args.remark = records.highRiskReason;

		this.riskStore = store;

		var form = this.form.getForm();

		var scoreField = form.findField("highRiskScore");
		scoreField.setValue(highRiskScore);

		var levelField = form.findField("highRiskLevel");
		levelField.setValue(highRiskLevel);

		this.data["highRisknesses"] = highRisknesses;
		this.data["highRisknessesChanged"] = true;
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
		if (values["visitResult"] != '4' && values["ifLost"] != 'y'
				&& !this.data["highRisknessesChanged"] && !this.initDataId) {
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
		var date = field.getValue();
		if (!date) {
			return;
		}
		if (!this.lastMenstrualPeriod) {
			return;
		}
		var weeks = (((date - Date.parseDate(this.lastMenstrualPeriod, "Y-m-d"))
				/ 1000 / 60 / 60 / 24) + 1)
				/ 7;
		this.form.getForm().findField("checkWeek").setValue(Math.floor(weeks));
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
	},

	getFormData : function() {
		var ac = util.Accredit;
		var form = this.form.getForm()
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items

		Ext.apply(this.data, this.exContext.empiData)

		if (items) {
			var n = items.length
			var checked;
			for (var i = 0; i < n; i++) {
				checked = false;
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] || it.defaultValue

				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					checked = !f.allowBlank;
					v = f.getValue()
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					// end
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
				} else {
					checked = true;
				}
				if (v == null || v === "") {
					if (checked && !it.pkey && it["not-null"] && !it.ref) {
						Ext.Msg.alert("提示信息", it.alias + "不能为空")
						return;
					}
				}
				values[it.id] = v;
			}
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
	}
});