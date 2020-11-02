$package("chis.application.cvd.script");
$import("app.desktop.Module", "chis.script.BizTableFormView");
chis.application.cvd.script.AssessRegisterForm = function(cfg) {
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 102;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.labelWidth = 105;
	chis.application.cvd.script.AssessRegisterForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this);
};

Ext.extend(chis.application.cvd.script.AssessRegisterForm, chis.script.BizTableFormView, {
			
			onBeforeCreate : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.cvdService",
							serviceAction : "initCvdAssessRegister",
							method : "execute",
							schema : this.entryName,
							empiId : this.exContext.ids["empiId"]
						})
				if (result.json && result.json.body) {
					this.initFormData(result.json.body);
				}
			},
			onReady : function() {
				chis.application.cvd.script.AssessRegisterForm.superclass.onReady
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

				var waistLine = form.findField("waistLine")
				this.waistLine = waistLine
				if (waistLine) {
					waistLine.on("keyup", this.onWaistLineCheck, this)
				}

				var hipLine = form.findField("hipLine")
				this.hipLine = hipLine
				if (hipLine) {
					hipLine.on("keyup", this.onHipLineCheck, this)
				}

				var tc = form.findField("tc")
				this.tc = tc
				if (tc) {
					tc.on("keyup", this.onTcCheck, this)
				}

				var hdl = form.findField("hdl")
				this.hdl = hdl
				if (hdl) {
					hdl.on("keyup", this.onHdlCheck, this)
				}
				
				form.findField("constriction").on("blur", this.onConstrictionChange, this);
		        form.findField("constriction").on("keyup", this.onConstrictionChange, this);
		        form.findField("diastolic").on("blur", this.onDiastolicChange, this);
		        form.findField("diastolic").on("keyup", this.onDiastolicChange, this);
		        this.onBeforeCreate();
			},
			initFormData : function(data) {
				chis.application.cvd.script.AssessRegisterForm.superclass.initFormData
						.call(this, data)
				this.initCalculate()
			},
			initCalculate : function() {
				this.onCalculateBMI()
				this.onCalculateWH()
				this.onCalculateTH()
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
		    },
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
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
					this.weight.markInvalid("体重必须在20-140之间")
					this.weight.focus()
					return
				}
				this.onCalculateBMI()
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
			onWaistLineCheck : function() {
				if (this.waistLine.getValue() == "") {
					return
				}
				this.onCalculateWH()
			},
			onHipLineCheck : function() {
				if (this.hipLine.getValue() == "") {
					return
				}
				this.onCalculateWH()
			},
			onCalculateWH : function() {
				var form = this.form.getForm()
				var wh = form.findField("wh")
				if (wh) {
					var w = this.waistLine.getValue()
					var h = this.hipLine.getValue()
					if (w == "" || h == "") {
						return
					}
					var v = (w / h).toFixed(2)
					wh.setValue(v)
				}
			},
			onTcCheck : function() {
				if (this.tc.getValue() == "") {
					return
				}
				this.onCalculateTH()
			},
			onHdlCheck : function() {
				if (this.hdl.getValue() == "") {
					return
				}
				this.onCalculateTH()
			},
			onCalculateTH : function() {
				var form = this.form.getForm()
				var th = form.findField("th")
				if (th) {
					var t = this.tc.getValue()
					var h = this.hdl.getValue()
					if (t == "" || h == "") {
						return
					}
					var v = (t / h).toFixed(2)
					th.setValue(v)
				}
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}

				if (this.op == "update") {
					Ext.Msg.show({
								title : '消息提示',
								msg : '是否重新生成评价结果？',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									saveData.updateAppraisal = btn
									this.proccessSave(saveData)
								},
								scope : this
							})
					return
				}
				this.proccessSave(saveData)
			},
			proccessSave : function(saveData) {
				saveData.empiId = this.exContext.ids["empiId"];
				saveData.phrId = this.exContext.ids["phrId"];
				saveData.bmi = this.form.getForm().findField("bmi").getValue()
				saveData.wh = this.form.getForm().findField("wh").getValue()
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.cvdService",
							op : this.op,
							method : "execute",
							schema : this.entryName,
							body : saveData,
							serviceAction : "saveAssessRegister"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)// jsonRequest
			},
			doCreate : function() {
				this.doNew()
				this.fireEvent("doCreate");
			},
			doRemove : function() {
				if (!this.initDataId) {
					return
				}
				Ext.Msg.show({
							title : '消息提示',
							msg : '是否确定删除此次心血管评估',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									util.rmi.jsonRequest({
												serviceId : "chis.cvdService",
												serviceAction : "removeAssessRegister",
												body : {
													pkey : this.initDataId,
													empiId:this.exContext.ids.empiId
												},
												method : "execute",
												schema : this.entryName
											}, function(code, msg, json) {
												this.fireEvent("remove")
											}, this)
								}
							},
							scope : this
						})
			}
		});