/**
 * 儿童意外情况表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.accident")
$import("util.Accredit")
$import("chis.script.BizTableFormView")
chis.application.cdh.script.accident.ChildrenAccidentForm = function(cfg) {
	cfg.width = 850;
	cfg.fldDefaultWidth = 150
	cfg.autoFieldWidth = false;
	chis.application.cdh.script.accident.ChildrenAccidentForm.superclass.constructor.apply(
			this, [cfg])
	this.initServiceId = "chis.childrenHealthRecordService";
	this.initServiceAction = "initChildAccident";
	this.on("doNew", this.onDoNew, this)
	this.on("beforeCreate", this.onBeforeCreate, this)
	this.on("loadData", this.onLoadData, this);
	this.on("beforePrint", this.onBeforePrint, this)

}
Ext.extend(chis.application.cdh.script.accident.ChildrenAccidentForm, chis.script.BizTableFormView,
		{

			doRemove : function(item, e) {
				if (this.initDataId == null) {
					return;
				}
				this.fireEvent("doRemove", this.initDataId);
			},

			onDoNew : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btn0 = this.form.getTopToolbar().items.item(0);
				var btn1 = this.form.getTopToolbar().items.item(2);
				var btn2 = this.form.getTopToolbar().items.item(3);
				if (this.initDataId) {
					if (btn2) {
						btn2.enable();
					}
					if (!btn0.disabled) {
						if (btn1) {
							btn1.enable();
						}
					} else {
						if (btn1) {
							btn1.disable();
						}
					}
				} else {
					if (btn1) {
						btn1.disable();
					}
					if (btn2) {
						btn2.disable();
					}
				}
			},

			initFormData : function(data) {
				this.fireEvent("loadData", data, this);
				chis.application.cdh.script.accident.ChildrenAccidentForm.superclass.initFormData
						.call(this,data);
			},

			afterSaveData : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data);
				var btn1 = this.form.getTopToolbar().items.item(2);
				if (btn1.disabled) {
					btn1.enable();
				}
				var btn2 = this.form.getTopToolbar().items.item(3);
				if (btn2.disabled) {
					btn2.enable();
				}
			},

			onBeforeCreate : function() {
				this.data["phrId"] = this.exContext.ids["CDH_HealthCard.phrId"]
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.initServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								"phrId" : this.exContext.ids["CDH_HealthCard.phrId"]
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								var data = json.body;
								var manaUnitId = data.manaUnitId;
								var oneYearLive = data.oneYearLive;
								var form = this.form.getForm();
								var manaUnitIdField = form
										.findField("manaUnitId")
								if (manaUnitIdField && manaUnitId) {
									manaUnitIdField.setValue(manaUnitId);
								}
								var oneYearLiveF = form
										.findField("oneYearLive");
								if (oneYearLiveF && oneYearLive) {
									oneYearLiveF.setValue(oneYearLive);
								}
								this.oneYearLive(oneYearLiveF);
							}
						}, this)
			},

			onReady : function() {
				chis.application.cdh.script.accident.ChildrenAccidentForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var accidentDate = form.findField("accidentDate");
				if (accidentDate) {
					accidentDate.setMinValue(Date.parseDate(
							this.exContext.empiData.birthday, "Y-m-d"));
				}

				var accidentPlace = form.findField("accidentPlace");
				if (accidentPlace) {
					accidentPlace.on("select", this.setOtherPlace, this);
				}

				var presentGuardian = form.findField("presentGuardian");
				if (presentGuardian) {
					presentGuardian.on("select", this.setOtherDescribe, this);
				}

				var firstManageArea = form.findField("firstManageArea");
				if (firstManageArea) {
					firstManageArea.on("select", this.setOtherManageArea, this);
				}

				var firstHandler = form.findField("firstHandler");
				if (firstHandler) {
					firstHandler.on("select", this.setOtherHandler, this);
				}

			},

			setOtherHandler : function(field) {
				var value = field.value;
				var disable = true;
				if (value == "5") {
					disable = false;
				}
				this.changeFieldState(disable, "otherHandler");
			},

			setOtherManageArea : function(field) {
				var value = field.value;
				var disable = true;
				if (value == "4") {
					disable = false;
				}
				this.changeFieldState(disable, "otherManageArea");
			},

			setOtherDescribe : function(field) {
				var value = field.value;
				var disable = true;
				if (value == "7" || value == "8") {
					disable = false;
				}
				this.changeFieldState(disable, "otherDescribe");
			},

			setOtherPlace : function(field) {
				var value = field.value;
				var disable = true;
				if (value == "4") {
					disable = false;
				}
				this.changeFieldState(disable, "otherPlace");
			},

			onLoadData : function(body) {
				var accidentPlace = body["accidentPlace"]
				if (accidentPlace) {
					var disable = true;
					if (accidentPlace.key == "4") {
						disable = false;
					}
					this.changeFieldState(disable, "otherPlace");
				}

				var presentGuardian = body["presentGuardian"]
				if (presentGuardian) {
					var disable = true;
					if (presentGuardian.key == "7"
							|| presentGuardian.key == "8") {
						disable = false;
					}
					this.changeFieldState(disable, "otherDescribe");
				}

				var firstManageArea = body["firstManageArea"]
				if (firstManageArea) {
					var disable = true;
					if (firstManageArea.key == "4") {
						disable = false;
					}
					this.changeFieldState(disable, "otherManageArea");
				}

				var firstHandler = body["firstHandler"]
				if (firstHandler) {
					var disable = true;
					if (firstHandler.key == "5") {
						disable = false;
					}
					this.changeFieldState(disable, "otherHandler");
				}
				this.oneYearLive(null);
			},

			oneYearLive : function(oneYearLiveField) {
				var registeredPermanent = this.exContext.empiData.registeredPermanent
				if (!registeredPermanent) {
					return;
				}
				var oneYearLive;
				if (!oneYearLiveField) {
					oneYearLive = this.form.getForm().findField("oneYearLive");
				} else {
					oneYearLive = oneYearLiveField
				}
				if (registeredPermanent == "2") {
					oneYearLive.allowBlank = false;
				} else {
					oneYearLive.reset();
					oneYearLive.allowBlank = true;
				}
				this.validate();
			},

			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.accident"];
				ids_str.value = "&phrId="
						+ this.exContext.ids["CDH_HealthCard.phrId"]
						+ "&accidentId=" + this.initDataId;
				return true;
			}
		})