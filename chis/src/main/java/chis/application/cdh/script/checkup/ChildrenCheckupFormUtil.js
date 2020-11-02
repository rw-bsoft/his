/**
 * 儿童体格检查表单公共页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup")
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")
chis.application.cdh.script.checkup.ChildrenCheckupFormUtil = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 100;
	cfg.fldDefaultWidth = 150;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = false;
	chis.application.cdh.script.checkup.ChildrenCheckupFormUtil.superclass.constructor.apply(
			this, [cfg])
	this.initServiceAction = "initChildCheckUp";
	this.on("beforePrint", this.onBeforePrint, this);
	this.on("doNew", this.onDoNew, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(chis.application.cdh.script.checkup.ChildrenCheckupFormUtil,
		chis.script.BizTableFormView, {

			onBeforeCreate : function() {
				if (!this.exContext.args[this.checkupType + "_param"].checkupDate) {
					return;
				}
				this.data["phrId"] = this.exContext.ids["CDH_HealthCard.phrId"];
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.initServiceAction,
					method:"execute",
					schema : this.entryName,
					body : {
						"phrId" : this.exContext.ids["CDH_HealthCard.phrId"],
						"empiId" : this.exContext.ids.empiId,
						"birthday" : this.exContext.empiData.birthday,
						"needInit" : this.exContext.args[this.checkupType
								+ "_param"].needInit,
						"checkupDate" : this.exContext.args[this.checkupType
								+ "_param"].checkupDate,
						"nextCheckupDate" : this.exContext.args[this.checkupType
								+ "_param"].nextCheckupDate,
						"checkupStage" : this.exContext.args[this.checkupType
								+ "_param"].checkupStage
					}
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						var data = json.body;
						this.initFormData(data);
					}
				}, this)
			},

			onDoNew : function() {
				var form = this.form.getForm();
				var checkupDate = form.findField("checkupDate");
				if (checkupDate) {
					checkupDate
							.setMinValue(this.exContext.args[this.checkupType
									+ "_param"].minVisitDate);
					checkupDate
							.setMaxValue(this.exContext.args[this.checkupType
									+ "_param"].maxVisitDate);
				}

				var notNextPlan = this.exContext.args.notNextPlan;
				if (notNextPlan) {
					var res = util.rmi.miniJsonRequestSync({
						serviceId : this.saveServiceId,
						serviceAction : "getNextPlanVisited",
						method:"execute",
						body : {
							"checkupStage" : this.exContext.args[this.checkupType
									+ "_param"].checkupStage,
							"recordId" : this.exContext.ids["CDH_HealthCard.phrId"]
						}
					})
					if (res.code == 200) {
						var nextPlanVisted = res.nextPlanVisted;
						if (nextPlanVisted) {
							this.exContext.args.minNextVisitDate = null;
						}
						this.valNextVisitDate();
					}
				} else {
					this.valNextVisitDate();
				}

			},

			valNextVisitDate : function() {
				var form = this.form.getForm();
				var nextTime = form.findField("nextCheckupDate");
				if (nextTime) {
					var minNextVisitDate = this.exContext.args[this.checkupType
							+ "_param"].minNextVisitDate;
					if (minNextVisitDate) {
						nextTime.setMinValue(minNextVisitDate);
						nextTime.enable();
					} else {
						nextTime.disable();
					}
				}
			},

			loadData : function() {
				var initDataId = this.exContext.args[this.checkupType
						+ "_param"].checkupId
				if (initDataId) {
					this.initDataId = initDataId;
				} else {
					this.initDataId = null;
				}
				chis.application.cdh.script.checkup.ChildrenCheckupFormUtil.superclass.loadData
						.call(this);
			},

			onLoadData : function(entryName, body) {
				var guide = body.guide;
				if (guide && guide.key) {
					var disable = true;
					if (guide.key.indexOf("5") > -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherGuide");
				}

				var referral = body.referral;
				if (referral && referral.key) {
					var disable = true;
					if (referral.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "referralReason");
					this.changeFieldState(disable, "referralUnit");
				}
			},

			onReady : function() {
				chis.application.cdh.script.checkup.ChildrenCheckupFormUtil.superclass.onReady
						.call(this);
				var form = this.form.getForm();
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
				if (lastValue.indexOf("5") > -1) {
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

			onBeforeSave : function(entryName, op, saveData) {
				saveData.empiId = this.exContext.empiData.empiId;
			},

			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saveData = saveData
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : this.op,
							schema : this.entryName,
							method:"execute",
							body : this.saveData,
							serviceAction : this.saveAction
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code != 200) {
								this.processReturnMsg(code, msg);
								return
							}
							Ext.apply(this.data, json.body);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							var isWeek = json.isWeek;
							if (!isWeek) {
								return;
							}
							if (json.recordCount == 0) {
								Ext.Msg.show({
									title : '体弱儿童建档',
									msg : '是否需要建立体弱儿童档案',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											var data = {
												"empiId" : this.data.empiId,
												"phrId" : this.data.phrId,
												"debilityReason" : json.debilityReason
											}
											this.fireEvent("activeDebility",
													data);
										}
									},
									scope : this
								});
							} else {
								Ext.MessageBox.alert("消息",
										"有未结案的体弱儿档案，请更新体弱儿档案！")
							}
							this.op = "update"
						}, this)// jsonRequest
			}
		})