/**
 * 儿童死亡登记表单页面(已建档)
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.dead")
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")
$import("chis.script.ICCardField", "util.widgets.LookUpField", "chis.script.util.Vtype")
chis.application.cdh.script.dead.ChildrenDeadRegisterForm = function(cfg) {
	cfg.width = 780;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 176
	chis.application.cdh.script.dead.ChildrenDeadRegisterForm.superclass.constructor.apply(
			this, [cfg])
	this.invalidateAge = false;
	this.initServiceAction = "initChildDead";
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("doNew", this.onDoNew, this);
	this.on("save", this.onSave, this);
	this.on("loadData", this.onLoadData, this);
	this.on("winShow", this.onWinShow, this);
	this.on("beforePrint", this.onBeforePrint, this);
}
Ext.extend(chis.application.cdh.script.dead.ChildrenDeadRegisterForm, chis.script.BizTableFormView,
		{

			onBeforeCreate : function() {
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								"empiId" : this.empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								var data = json.body;
								var childRegister = data.childRegister;
								if (childRegister) {
									this.OnChangeRegister(childRegister.key,
											true);
									this.oneYearLive(childRegister.key);
								}
								this.initFormData(data);
							}
						}, this)
			},

			oneYearLive : function(childRegister) {
				if (!childRegister) {
					return;
				}
				var oneYearLive = this.form.getForm().findField("oneYearLive");
				if (childRegister == "2") {
					oneYearLive.allowBlank = false;
					oneYearLive.enable();
				} else {
					oneYearLive.reset();
					oneYearLive.allowBlank = true;
					oneYearLive.disable();
				}
				this.validate();
			},

			OnChangeRegister : function(n, isLoadData) {
				var address = this.form.getForm().findField("homeAddress")
				if (!isLoadData) {
					address.reset()
				}
				if (address) {
					if (n == "1") {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 户籍地址:</span>");
					} else {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 暂住证地址:</span>");
					}
				}
			},

			onDoNew : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btn1 = this.form.getTopToolbar().items.item(1);

				if (this.initDataId) {
					if (btn1)
						btn1.enable();
					return;
				}

				if (btn1)
					btn1.disable();
			},

			doSave : function() {
				if (this.invalidateAge) {
					Ext.MessageBox.alert("提示", "死亡日期必须大于等于出生日期.");
					return;
				}
				if (this.exist) {
					Ext.MessageBox.alert("提示", "死亡登记编号重复，不允许保存！")
					return;
				}
				if (this.op == "create") {
					Ext.Msg.show({
						title : '确认建立[' + this.data["childName"] + ']的死亡记录',
						msg : '建立死亡记录会同时注销[' + this.data["childName"]
								+ ']的所有档案,且其相关信息均不可操作,是否继续?',
						modal : false,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								chis.application.cdh.script.dead.ChildrenDeadRegisterForm.superclass.doSave
										.call(this)
							}
						},
						scope : this
					})
				} else {
					chis.application.cdh.script.dead.ChildrenDeadRegisterForm.superclass.doSave
							.call(this);
				}
			},

			onSave : function() {
				this.doCancel();
			},

			onReady : function() {
				chis.application.cdh.script.dead.ChildrenDeadRegisterForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var deadNo = form.findField("deadNo");
				if (deadNo) {
					deadNo.on("change", this.checkDeadNo, this);
				}

				var treatment = form.findField("treatment");
				if (treatment) {
					treatment.on("select", this.setNoTreatment, this);
				}

				var noTreatReason = form.findField("noTreatmentReason");
				if (noTreatReason) {
					noTreatReason.on("select", this.setOtherReason, this);
				}

				var diagnoseLevel = form.findField("diagnoseLevel");
				if (diagnoseLevel) {
					diagnoseLevel.on("select", this.setNoTreatment, this);
				}

				var deathDate = form.findField("deathDate");
				if (deathDate) {
					deathDate.on("valid", this.getDeadYear, this);
				}

			},

			setOtherReason : function(field) {
				var value = field.value
				var disable = true;
				if (value == "6") {
					disable = false;
				}
				this.changeFieldState(disable, "noTreatOtherReason");
			},

			checkDeadNo : function(f) {
				var value = f.getValue();
				if (!value || value == "") {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "checkDeadNo",
							method:"execute",
							body : {
								deadNo : value,
								empiId : this.empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (code == 200) {
								var resBody = json.body;
								if (json.body) {
									this.exist = resBody.isRepeat;
									if (this.exist) {
										Ext.MessageBox.alert("提示信息",
												"死亡登记编号重复!", function() {
													f.focus(true, true);
												}, this);
									}
								}
							}
						}, this)
			},

			setNoTreatment : function(field) {
				var value = field.value
				var name = field.name
				var disable = true;
				if (name == "treatment") {
					if (value == "3") {
						disable = false;
					} else {
						var diagnose = this.form.getForm()
								.findField("diagnoseLevel");
						if (diagnose) {
							var diaValue = diagnose.value;
							if (diaValue == "06") {
								disable = false;
							}
						}
					}
				} else if (name == "diagnoseLevel") {
					if (value == "06") {
						disable = false;
					} else {
						var treatment = this.form.getForm()
								.findField("treatment");
						if (treatment) {
							var treatValue = treatment.value;
							if (treatValue == "3") {
								disable = false;
							}
						}
					}
				}
				this.changeFieldState(disable, "noTreatmentReason");
			},

			getDeadYear : function(field) {
				var deadDate = field.getValue();
				if (deadDate) {
					var form = this.form.getForm();
					var birthday = form.findField("birthday").getValue();
					var diffDate = (deadDate.getTime() - birthday.getTime())
							/ (24 * 60 * 60 * 1000);
					if (diffDate < 0) {
						this.invalidateAge = true
						Ext.MessageBox.alert("提示", "死亡日期必须大于等于出生日期")
						return
					}
					this.invalidateAge = false
					var diffTime = chis.script.util.helper.Helper.getAgeBetween(birthday,
							deadDate);
					var deathYear = form.findField("deathYear");
					deathYear.setValue(diffTime);
				}
			},

			onLoadData : function(entryName, body) {

				var treatment = body["treatment"]
				if (treatment) {
					var disable = true;
					if (treatment.key == "3") {
						disable = false;
					} else {
						var diag = body["diagnoseLevel"]
						if (diag)
							if (diag.key == "5") {
								disable = false;
							}
					}
					this.changeFieldState(disable, "noTreatmentReason");
				}

				var diagnoseLevel = body["diagnoseLevel"]
				if (diagnoseLevel) {
					var disable = true;
					if (diagnoseLevel.key == "5") {
						disable = false;
					} else {
						var treat = body["treatment"]
						if (treat)
							if (treat.key == "3") {
								disable = false;
							}
					}
					this.changeFieldState(disable, "noTreatmentReason");
				}

				var noTreatmentReason = body["noTreatmentReason"]
				if (noTreatmentReason) {
					var disable = true;
					if (noTreatmentReason.key == "6") {
						disable = false;
					}
					this.changeFieldState(disable, "noTreatOtherReason");
				}

				var childRegister = body["childRegister"];
				this.oneYearLive(childRegister.key);
				this.OnChangeRegister(childRegister.key, true);

			},

			onWinShow : function() {
				this.win.doLayout();
				this.invalidateAge = false;
				this.exist = false;
				if (this.initDataId) {
					this.loadData();
				} else {
					this.doNew();
				}
			},

			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.deadRegister"];
				ids_str.value = "&empiId=" + this.empiId + "&deadRegisterId="
						+ this.initDataId;
				return true;
			}
		});