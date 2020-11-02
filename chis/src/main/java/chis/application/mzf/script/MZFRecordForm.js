$package("chis.application.mzf.script")
$import("util.Accredit", "chis.script.BizTableFormView",
		"chis.script.util.helper.Helper")
chis.application.mzf.script.MZFRecordForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = false;
	cfg.showButtonOnTop = true;
	cfg.width = window.screen.width;
	cfg.height = window.screen.height;
	chis.application.mzf.script.MZFRecordForm.superclass.constructor
			.apply(this, [cfg]);
	this.nowDate = this.mainApp.serverDate;
	this.saveServiceId = "chis.mZFRecordService";
	this.saveAction = "saveMZFRecord";
	this.on("save",this.onSave,this);

}
debugger;
Ext.extend(chis.application.mzf.script.MZFRecordForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				debugger;
				chis.application.mzf.script.MZFRecordForm.superclass.doNew
						.call(this)
				this.form.getForm().findField("phrId")
						.setValue(this.exContext.ids.phrId)
			},
			
			doCheck : function() {
				this.fireEvent("addModule");
			},
			
			loadData : function() {
				debugger;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.mZFRecordService",
							serviceAction : "initializeRecord",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId,
								phrId : this.exContext.ids.phrId
							}
						})
				this.result = result
				if (!this.result.json.body) {
					return
				}
				this.loadLocalData(result.json.body)
			},
			
			loadLocalData : function(body) {
				this.doNew();
				debugger;
				if (body) {
					var data = body;
					if (data[this.schema.pkey]) {
						this.initDataId = data[this.schema.pkey]
						this.op = "update";
					} else {
						this.initDataId = null;
						body = {}
					}
					this.initFormData(data);
					this.exContext.control = data["_actions"]
				}
				// 修改当前操作的状态，控制列表按钮。
				body.op = this.op
				if (!this.initDataId) {
					this.setButton(["check"], false);
				} else {
					this.setButton(["check"], true);
				}
			},
			initFormData : function(data) {
				debugger;
				chis.application.mzf.script.MZFRecordForm.superclass.initFormData.call(this, data)
				this.onCalculateBMI();
				this.calculateYears();
				this.resetButton(data);
				if (this.op == "create") {
					this.initDataId = null;
					this.form.getForm().findField("manaDoctorId").disable();
					//this.form.getForm().findField("createUser").enable();
				}
				//this.form.getForm().findField("inputDate").enable();
//				if(data && data.status && data.status.key=="1"){
//					Ext.Msg.alert("友情提醒：","档案已注销，请先恢复档案！");
//				}
//				if(data && data.gxyheightcheck==false){
//					Ext.Msg.alert("友情提醒：","糖尿病档案身高和高血压档案身高不一致，请核实修改！");
//				}
			},
			resetButton : function(data) {
				debugger;
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					var obj = data["_actions"]
					if (obj) {
						var status = obj[this.op]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			},
			
			getSaveRequest : function(saveData) {
				debugger;
				saveData.empiId = this.exContext.ids.empiId
				return saveData;
			},

			setButton : function(m, flag) {
				debugger
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
			},

			onReady : function() {
				debugger;
				chis.application.mzf.script.MZFRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm()

				var weight = form.findField("weight")
				this.weight = weight
				if (weight) {
					weight.on("keyup", this.onWeightCheck, this)
				}

				var height = form.findField("height")
				this.height = height
				if (height) {
					height.on("keyup", this.onHeightCheck, this)
				}

				var diagnosisDate = form.findField("diagnosisDate")
				this.diagnosisDate = diagnosisDate
				this.diagnosisDate.setMaxValue(this.diagnosisDate
						.parseDate(this.nowDate))
				this.diagnosisDate.validate()
				if (diagnosisDate) {
					diagnosisDate
							.on("select", this.onDiagnosisDateChange, this)
					diagnosisDate.on("keyup", this.onDiagnosisDateChange, this)
				}

				var manaDoctorId = form.findField("manaDoctorId")
				this.manaDoctorId = manaDoctorId
				if (manaDoctorId) {
					manaDoctorId.on("select", this.onManaDoctorIdSelect, this)
				}
				
				var createUser = form.findField("createUser")
				this.createUser = createUser
				if (createUser) {
					createUser.on("select", this.onCreateDoctorIdSelect, this)
				}
				
				var diabetesType = form.findField("diabetesType")
				this.diabetesType = diabetesType
				if (diabetesType) {
					diabetesType.on("select", this.onDiabetesTypeSelect, this)
				}
				var fbsField = form.findField("fbs")
				this.fbsField = fbsField
				if (fbsField) {
					fbsField.on("keyup", this.onFbs, this)
					fbsField.on("blur", this.onFbs, this)
				}

				var pbsField = form.findField("pbs")
				this.pbsField = pbsField
				if (pbsField) {
					pbsField.on("keyup", this.onPbs, this)
					pbsField.on("blur", this.onPbs, this)
				}
			},
			
			changeFieldByVisit : function(hasVisit) {
				if (hasVisit) {
					this.diabetesType.disable();
					this.fbsField.disable();
					this.pbsField.disable();
				} else {
					this.diabetesType.enable();
					this.fbsField.enable();
					this.pbsField.enable();
				}
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
						})
				var manaUnitIdCombo = this.form.getForm()
						.findField("manaUnitId");
				if (manaUnitIdCombo) {
					manaUnitIdCombo.setValue(result.json.manageUnit)
				}
			},
			
			onCreateDoctorIdSelect : function(combo, node) {
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
						})
				var manaUnitIdCombo = this.form.getForm().findField("createUnit");
				if (manaUnitIdCombo) {
					manaUnitIdCombo.setValue(result.json.manageUnit)
				}
			},
			onDiagnosisDateChange : function() {
				this.calculateYears()
			},
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
					// alert("身高输入非法")
					this.height.markInvalid("身高必须在130-250之间")
					this.height.focus()
					return
				}
				this.onCalculateBMI()
			},
			onWeightCheck : function() {
				if (this.weight.getValue() == "") {
					return
				}
				if (this.weight.getValue() > 140 || this.weight.getValue() < 20) {
					// alert("身高输入非法")
					this.weight.markInvalid("体重必须在20-140之间")
					this.weight.focus()
					return
				}
				this.onCalculateBMI()
			},
			
			afterSaveData : function(a,b,c) {
				this.refreshEhrTopIcon();
			},
			
			onCalculateBMI : function() {
				var form = this.form.getForm()
				var bmi = form.findField("bmi")
				if (bmi) {
					var w = this.weight.getValue()
					var h = this.height.getValue()
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2)
					bmi.setValue(b)
				}

			},

			calculateYears : function() {
				if (!this.form.getForm().findField("diagnosisDate").validate()) {
					return
				}
				var diagnosisDate = this.form.getForm()
						.findField("diagnosisDate").getValue()
				var month = chis.script.util.helper.Helper.getAgeMonths(
						diagnosisDate, Date.parseDate(this.mainApp.serverDate,
								"Y-m-d"));
				if (month < 1) {
					var days = chis.script.util.helper.Helper.getPeriod(
							diagnosisDate, Date.parseDate(
									this.mainApp.serverDate, "Y-m-d"));
					this.form.getForm().findField("years").setValue(days + "天")
					return
				}
				if (month >= 12) {
					var years = parseInt(month / 12);
					var remainMonth = month - years * 12;
					if (remainMonth == 0) {
						this.form.getForm().findField("years").setValue(years
								+ "年")
					} else {
						this.form.getForm().findField("years").setValue(years
								+ "年" + remainMonth + "月")
					}
					return
				}
				this.form.getForm().findField("years").setValue(month + "月")
			},
			
			onSave:function(schmema,op,body){
				debugger;
				if(body.code < 300){
					MyMessageTip.msg("提示", "保存成功!", true);
				}else{
					MyMessageTip.msg("提示", "保存失败!", true);
				}	
			}
		});