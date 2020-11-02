/**
 * 体弱儿随访,随访信息表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.visit");
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper");
chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 80;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.autoLoadData = false;
	chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm.superclass.constructor
			.apply(this, [cfg]);
	this.initServiceAction = "visitInitialization";
	this.on("doNew", this.onDoNew, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("beforePrint", this.onBeforePrint, this)
};

Ext.extend(chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var visitDate = form.findField("visitDate");
				visitDate.setMinValue(this.exContext.empiData.birthday);
				visitDate.on("select", this.onVisitDateChange, this);
				visitDate.on("keyup", this.onVisitDateChange, this);
			},

			onVisitDateChange : function(field) {
				var visitDate = field.getValue();
				if (!this.exContext.empiData.birthday) {
					return;
				}
				var birthDay = Date.parseDate(this.exContext.empiData.birthday,
						"Y-m-d");
				var form = this.form.getForm();
				field.minValue = birthDay
				if (visitDate < birthDay) {
					// field.markInvalid("随诊日期应在出生日期之后！");
					form.findField("visitMonth").setValue("");
					return;
				}
				var monAge = chis.script.util.helper.Helper.getAgeMonths(birthDay,
						visitDate);
				form.findField("visitMonth").setValue(monAge);
			},

			onDoNew : function() {
				var form = this.form.getForm();
				var visitDate = form.findField("visitDate");
				if (visitDate) {
					visitDate.setMinValue(this.exContext.args.minVisitDate);
					visitDate.setMaxValue(this.exContext.args.maxVisitDate);
					if (!this.exContext.args.visitId) {
						visitDate.setValue(this.exContext.args.planDate);
					}
				}

				var notNextPlan = this.exContext.args.notNextPlan;
				if (notNextPlan) {
					var res = util.rmi.miniJsonRequestSync({
								serviceId : this.saveServiceId,
								serviceAction : "getNextPlanVisited",
								method:"execute",
								body : {
									"planId" : this.exContext.args.planId,
									"recordId" : this.exContext.args.recordId
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
				var nextVisitDate = form.findField("nextVisitDate");
				if (nextVisitDate) {
					var minNextVisitDate = this.exContext.args.minNextVisitDate;
					if (minNextVisitDate) {
						nextVisitDate.setMinValue(minNextVisitDate);
						nextVisitDate.enable();
					} else {
						nextVisitDate.disable();
					}
				}
			},

			onBeforeCreate : function() {
				this.data["recordId"] = this.exContext.args.recordId
				this.data["empiId"] = this.exContext.args.empiId
						|| this.exContext.ids.empiId
				if (!this.exContext.args.planDate) {
					return;
				}
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								recordId : this.exContext.args.recordId,
								planDate : this.exContext.args.planDate
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								var data = json.body;
								var form = this.form.getForm();
								var length = form.findField("length");
								if (length) {
									length.setValue(data.height);
								}
								var weight = form.findField("weight");
								if (weight) {
									weight.setValue(data.weight);
								}
								var guidance = form.findField("guidance");
								if (guidance) {
									guidance.setValue(data.correction);
								}
								var nextBeginDate = data.nextBeginDate;
								if (nextBeginDate) {
									form.findField("nextVisitDate")
											.setValue(Date.parseDate(
													nextBeginDate, "Y-m-d"));
								}
								var visitDate = form.findField("visitDate")
										.getValue();
								var monAge = chis.script.util.helper.Helper
										.getAgeMonths(
												Date
														.parseDate(
																this.exContext.empiData.birthday,
																"Y-m-d"),
												visitDate);
								form.findField("visitMonth").setValue(monAge);

							}
						}, this)
			},

			loadData : function() {
				var initDataId = this.exContext.args.visitId
				if (initDataId) {
					this.initDataId = initDataId;
				} else {
					this.initDataId = null;
				}
				chis.application.cdh.script.debility.visit.DebilityChildrenVisitForm.superclass.loadData
						.call(this);
			},

			onBeforeSave : function(entryName, op, saveData) {
				saveData.planId = this.exContext.args.planId;
				this.data.planId = this.exContext.args.planId;
				saveData.birthday = this.exContext.empiData.birthday
			},

			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.0-3tiluozhuifangdan"];
				ids_str.value = "&empiId=" + this.exContext.args.empiId
						+ "&visitId=" + this.exContext.args.visitId;
				return true;
			}

		});