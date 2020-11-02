/**
 * 老年人随访管理（随访记录表单）
 * 
 * @param {}
 *            cfg
 */
$package("chis.application.ohr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.ohr.script.OldPeopleForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.colCount = 4;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 110;
	cfg.labelWidth = 90;
	chis.application.ohr.script.OldPeopleForm.superclass.constructor.apply(this, [cfg]);
	this.on("beforeCreate", this.onNewForm, this);
	this.on("beforeUpdate", this.onNewForm, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.ohr.script.OldPeopleForm, chis.script.BizTableFormView, {
	doNew : function() {
		this.initDataId = this.exContext.args.initDataId;
		chis.application.ohr.script.OldPeopleForm.superclass.doNew.call(this);
	},

	onNewForm : function() {
		var form = this.form.getForm();
		var nextDate = form.findField("nextDate");
		if (nextDate) {
			if (this.exContext.args.nextDateDisable) {
				nextDate.disable();
			} else {
				nextDate.enable();
			}
		}

		this.resetVisitDate(form); // 控制随访日期最大值或最小值

		if (this.mainApp.exContext.oldPeopleMode == 2
				&& this.exContext.args.planDate && nextDate) {
			var serverDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			var currDate = Date.parseDate(this.exContext.args.endDate, "Y-m-d");
			if (serverDate > currDate) {
				nextDate.setMinValue(serverDate);
			} else {
				var e = new Date(this.exContext.args.endDate);
				var nextMinDate = new Date(e.getFullYear(), e.getMonth(), e
								.getDate()
								+ 1);
				nextDate.setMinValue(nextMinDate);
			}
		}

		if (this.exContext.args.initDataId) {
			return;
		}

		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "initOldPeopleVisitForm",
					method: "execute",
					schema : this.entryName,
					body : {
						"empiId" : this.exContext.args.empiId
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						var body = json.body;
						this.data["manaUnitId"] = body.manaUnitId;
						this.data["manaDoctorId"] = body.manaDoctorId;
						this.initFormData(body);
						this.changeOnLoad(body);
					}
				}, this)

		this.data["empiId"] = this.exContext.args.empiId;
		this.data["phrId"] = this.exContext.args.phrId;
		var visitEffect = form.findField("visitEffect");
		this.onVEChange(visitEffect);
	},

	onReady : function() {
		var form = this.form.getForm();

		var visitEffect = form.findField("visitEffect");
		if (visitEffect) {
			visitEffect.on("select", this.onVEChange, this);
			visitEffect.on("change", this.onVEChange, this);
		}
		var nowSymptoms = form.findField("nowSymptoms");
		if (nowSymptoms) {
			nowSymptoms.on("select", this.onNSChange, this);
			nowSymptoms.on("change", this.onNSChange, this);
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
		var health = form.findField("healthAssessment");
		if (health) {
			health.on("select", this.setHealthExp, this);
			health.on("change", this.setHealthExp, this);
		}
		var riskControll = form.findField("riskControll");
		if (riskControll) {
			riskControll.on("select", this.setTargetWeight, this);
		}
		var f = form.findField("smokeFlag");
		if (f) {
			f.on("select", this.smokeField, this);
			f.on("change", this.smokeField, this);
		}
		var field = form.findField("drinkFlag");
		if (field) {
			field.on("select", this.drinkField, this);
			field.on("change", this.drinkField, this)
		}
		var trField = form.findField("trainFreqCode");
		if (trField) {
			trField.on("select", this.trainField, this);
			trField.on("change", this.trainField, this)
		}
		var drinkTypeCode = form.findField("drinkTypeCode");
		if (drinkTypeCode) {
			drinkTypeCode.on("select", this.setDrinkTypeCode, this);
			drinkTypeCode.on("keyup", this.setDrinkTypeCode, this);
			drinkTypeCode.on("blur", this.setDrinkTypeCode, this);
		}
		var eateHabit = form.findField("eateHabit");
		if (eateHabit) {
			eateHabit.on("select", this.setEateHabit, this);
			eateHabit.on("keyup", this.setEateHabit, this);
			eateHabit.on("blur", this.setEateHabit, this);
		}
		var visitDoctor = form.findField("visitDoctor");
		if (visitDoctor) {
			visitDoctor.on("select", this.onVisitDoctorSelect, this);
		}

		this.resetVisitDate(form);
		chis.application.ohr.script.OldPeopleForm.superclass.onReady.call(this);
	},
    setDrinkTypeCode : function(combo, record, index) {
		if (!record) {
			return;
		}
		var value = combo.getValue();
		var valueArray = value.split(",");
		combo.clearValue();
		var v = valueArray.toString();
		combo.setValue(v);
	},
	setEateHabit : function(combo, record, index) {
		if (!record) {
			return;
		}
		var value = combo.getValue();
		var valueArray = value.split(",");
		var count = valueArray.length;
		for (var i = 0; i < count; i++) {
			if (this.valueArray && valueArray[i]) {
				var vaa = this.valueArray;
				if (vaa.indexOf("1") == -1 && valueArray.indexOf("1") != -1) {
					valueArray.remove("2");
					valueArray.remove("3");
				} else if (vaa.indexOf("2") == -1
						&& valueArray.indexOf("2") != -1) {
					valueArray.remove("1");
					valueArray.remove("3");
				} else if (vaa.indexOf("3") == -1
						&& valueArray.indexOf("3") != -1) {
					valueArray.remove("1");
					valueArray.remove("2");
				}
			}
		}
		combo.clearValue();
		var v = valueArray.toString();
		combo.setValue(v);
		this.valueArray = valueArray;
	},

	resetVisitDate : function(form) {
		var visitDate = form.findField("visitDate");
		if (visitDate) {
			var serverDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			var currDate = Date.parseDate(this.exContext.args.endDate, "Y-m-d");
			visitDate.setMinValue(this.exContext.args.beginDate)
			if (currDate > serverDate) {
				visitDate.setMaxValue(serverDate);
			} else {
				visitDate.setMaxValue(currDate);
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
					method: "execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				})
		this.setVisitUnit(result.json.manageUnit)
	},

	setVisitUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("visitUnit");
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

		if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
			combox.setValue(manageUnit)
			combox.disable();
		}
	},

	setHealthExp : function(field) {
		var value = field.getValue();
		var disabled = true;
		if (value == "y") {
			disabled = false;
		}
		this.changeFieldState(disabled, "healthException");
		this.changeFieldAllow(disabled, "healthException");
	},

	setTargetWeight : function(field) {
		var value = field.getValue();
		var disabled = true;
		if (value) {
			if (value.indexOf("5") > -1) {
				disabled = false;
			}
		}
		this.changeFieldState(disabled, "targetWeight");
	},

	onVEChange : function(field) {
		var newValue = field.getValue();
		if (newValue == '9' && this.exContext.args.nextDateDisable) {// 终止管理
			Ext.MessageBox.alert("提示", "只有最后一条记录才可以终止管理！");
			field.reset();
			return;
		}
		var items = this.schema.items;
		var form = this.form.getForm();
		var f = form.findField("noVisitReason");
		var nextDate = form.findField("nextDate");
		var isNextBespeak = this.fireEvent("isNextBespeak");

		if (!this.notNullSchemaItems) {
			this.notNullSchemaItems = [];
			for (var i = 0; i < items.length; i++) {
				if (items[i]["not-null"] == "1"
						|| items[i]['not-null'] == "true") {
					this.notNullSchemaItems.push(items[i].id);
				}
			}
		}

		if (newValue == 1) {// @@ 恢复原必填项。
			Ext.getCmp(f.id).getEl().up('.x-form-item')
					.child('.x-form-item-label').update(f.fieldLabel + ":");
			f.disable();
			f.setValue();
			f.allowBlank = true;

			for (var i = 0; i < items.length; i++) {
				if (items[i].id == "nextDate"
						&& this.mainApp.exContext.oldPeopleMode != 2) {
					continue;
				}
				var field = form.findField(items[i].id);
				if (field && this.notNullSchemaItems.indexOf(items[i].id) > -1) {
					field.allowBlank = false;
					items[i]["not-null"] = true;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + items[i].alias
									+ ":</span>");
				}
			}
			this.validate();
			return;
		}

		if (newValue == 2) {// @@ 去除必填项，激活失访原因文本框
			Ext.getCmp(f.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + f.fieldLabel
							+ ":</span>");
			f.allowBlank = false;
			f.enable();

			for (var i = 0; i < items.length; i++) {
				if (items[i].id == "noVisitReason" || items.evalOnServer) {
					continue;
				}

				var field = form.findField(items[i].id);
				if (items[i].id == "nextDate"
						&& this.mainApp.exContext.oldPeopleMode == 2) {
					field.allowBlank = false;
					items[i]["not-null"] = true;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + items[i].alias
									+ ":</span>");
					continue;
				}

				if (field) {
					field.allowBlank = true;
					items[i]["not-null"] = false;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(items[i].alias
									+ ":");
				}
			}
			this.validate();
			return
		}

		if (newValue == 9) {// @@ 去除必填项，激活失访原因文本框
			Ext.getCmp(f.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + f.fieldLabel
							+ ":</span>");
			f.allowBlank = false;
			f.enable();

			for (var i = 0; i < items.length; i++) {
				if (items[i].id == "noVisitReason" || items.evalOnServer) {
					continue;
				}

				var field = form.findField(items[i].id);
				if (field) {
					field.allowBlank = true;
					items[i]["not-null"] = false;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(items[i].alias
									+ ":");
				}
			}
			this.validate();
		}
	},

	onNSChange : function(field) {
		var newValue = field.getValue();
		if (!newValue) {
			return;
		}
		var disable = true;
		if (newValue == "2") {
			disable = false;
		}
		this.changeFieldState(disable, "newSymptoms")
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

	smokeField : function(combo, record, index) {
		var value = combo.value;
		this.removeSmokeField(value);
	},

	removeSmokeField : function(value) {
		var form = this.form.getForm();
		var f = form.findField("smokeCount");
		if (f) {
			if (value != "4") {
				f.enable();
				f.allowBlank = false;
				Ext.getCmp(f.id).getEl().up('.x-form-item')
						.child('.x-form-item-label')
						.update("<span style='color:red'>" + f.fieldLabel
								+ ":</span>");
			} else {
				f.setValue("");
				f.disable();
				f.allowBlank = true;
				Ext.getCmp(f.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update(f.fieldLabel + ":");
			}
		}
		this.validate();
	},

	drinkField : function(combo, record, index) {
		var value = combo.value;
		this.removeDrinkField(value);
	},

	removeDrinkField : function(value) {
		var form = this.form.getForm();
		var filed = ["drinkTypeCode", "drinkCount"];
		for (var i = 0; i < filed.length; i++) {
			var item = filed[i];
			var f = form.findField(item);
			if (f) {
				if (value != "1") {
					f.enable();
					f.allowBlank = false;
					Ext.getCmp(f.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + f.fieldLabel
									+ ":</span>");
				} else {
					f.setValue("");
					f.disable();
					f.allowBlank = true;
					Ext.getCmp(f.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(f.fieldLabel
									+ ":");
				}
			}
		}
		this.validate();
	},

	trainField : function(combo, record, index) {
		var value = combo.value;
		this.removeTrainField(value);
	},

	removeTrainField : function(value) {
		var form = this.form.getForm();
		var filed = ["trainMinute", "trainTimesWeek"];
		for (var i = 0; i < filed.length; i++) {
			var item = filed[i];
			var f = form.findField(item);
			if (f) {
				if (value == "4" || value == "" || value == null) {
					f.disable();
					f.setValue("");
					f.allowBlank = true;
					Ext.getCmp(f.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(f.fieldLabel
									+ ":");
				} else {
					f.enable();
					f.allowBlank = false;
					Ext.getCmp(f.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + f.fieldLabel
									+ ":</span>");
				}
			}
		}
		this.validate();
	},

	onLoadData : function(entryName, body) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : this.saveServiceId,
					serviceAction : "loadLastVisitedData",
					method: "execute",
					body : {
						empiId : this.exContext.args.empiId
					}
				})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		var form = this.form.getForm();
		var lastVisitId = result.json.visitId;
		var nextDate = form.findField("nextDate");
		if (lastVisitId == this.exContext.args.initDataId) {
			if (nextDate) {
				var serverDate = Date.parseDate(this.mainApp.serverDate,
						"Y-m-d");
				var currDate = Date.parseDate(this.exContext.args.endDate,
						"Y-m-d");
				if (serverDate > currDate) {
					nextDate.setMinValue(serverDate);
				} else {
					var e = new Date(this.exContext.args.endDate);
					var nextMinDate = new Date(e.getFullYear(), e.getMonth(), e
									.getDate()
									+ 1);
					nextDate.setMinValue(nextMinDate);
				}
			}
		} else {
			if (nextDate) {
				nextDate.disabled = true;
			}
		}
		var nowSymptoms = body.nowSymptoms;
		if (nowSymptoms) {
			var nsKey = nowSymptoms.key;
			if (nsKey && nsKey == "2") {
				disable = false;
			} else {
				disable = true;
			}
			this.changeFieldState(disable, "newSymptoms");
		}

		var healthAssessment = body["healthAssessment"];
		if (healthAssessment) {
			var disable = true;
			if (healthAssessment.key == "y") {
				disable = false;
			}
			this.changeFieldState(disable, "healthException");
			this.changeFieldAllow(disable, "healthException");
		}

		var riskControll = body["riskControll"];
		if (riskControll) {
			var disable = true;
			if (riskControll.key) {
				if (riskControll.key.indexOf("5") > -1) {
					disable = false;
				}
			}
			this.changeFieldState(disable, "targetWeight");
		}

		var visitEffect = form.findField("visitEffect");
		this.onVEChange(visitEffect)
		this.changeOnLoad(body);
	},

	changeOnLoad : function(body) {
		var somke = body.smokeFlag;
		if (somke) {
			this.removeSmokeField(somke.key);
		}
		var drink = body.drinkFlag;
		if (drink) {
			this.removeDrinkField(drink.key);
		}
		var train = body.trainFreqCode;
		if (train) {
			this.removeTrainField(train.key);
		}
	},

	changeFieldState : function(value, fieldName) {
		var field = this.form.getForm().findField(fieldName);
		if (field) {
			if (value) {
				field.reset();
				field.disable();
			} else {
				field.enable();
			}
		}
	},

	changeFieldAllow : function(value, fieldName) {
		var field = this.form.getForm().findField(fieldName);
		if (field) {
			if (value) {
				field.allowBlank = true;
				Ext.getCmp(field.id).getEl().up('.x-form-item')
						.child('.x-form-item-label').update(field.fieldLabel
								+ ":");
			} else {
				field.allowBlank = false;
				Ext.getCmp(field.id).getEl().up('.x-form-item')
						.child('.x-form-item-label')
						.update("<span style='color:red'>" + field.fieldLabel
								+ ":</span>");
			}
		}
		this.validate();
	},

	doSave : function() {
		this.fireEvent("save", this.entryName);
	},

	getFormData : function() {
		var values = {};
		values = chis.application.ohr.script.OldPeopleForm.superclass.getFormData.call(this);
		values["visitId"] = this.exContext.args.initDataId
		values["planId"] = this.exContext.args.planId;
		values["planDate"] = this.exContext.args.planDate;
		if (this.mainApp.exContext.oldPeopleMode == 2
				&& values["visitEffect"] != 9) {
			var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			if (!this.exContext.args.nextDateDisable
					&& values["nextDate"] <= now) {
				Ext.MessageBox.alert("提示", "预约日期必须大于当前日期");
				return;
			}
		}
		return values;
	},

	getLoadRequest : function() {
		if (this.exContext.args.initDataId) {
			this.initDataId = null;
			return {
				"fieldName" : "visitId",
				"fieldValue" : this.exContext.args.initDataId
			};
		} else {
			return null;
		}
	}
});