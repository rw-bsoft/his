/**
 * 艾滋病梅毒感染孕妇妊娠登记表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.delivery.DeliveryRecord2Form = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 115;
	cfg.fldDefaultWidth = 180;
	cfg.autoFieldWidth = false
	chis.application.mhc.script.delivery.DeliveryRecord2Form.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.mhc.script.delivery.DeliveryRecord2Form, chis.script.BizTableFormView,
		{

			onBeforeCreate : function() {
				this.data["empiId"] = this.exContext.ids.empiId;
				this.data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
			},
			onLoadData : function(entryName, body) {

				var numberofply = body.numberofply;
				var deliveryOutcome = body.deliveryOutcome;

				var deliveryWay = body.deliveryWay;
				var isConjuncture = body.isConjuncture;
				var hasComplication = body.hasComplication;

				if (deliveryWay && deliveryWay.key) {
					this.onProcessDeliveryWaySelect(deliveryWay.key)
				}
				if (isConjuncture && isConjuncture.key) {
					this.onProcessisConjunctureSelect(isConjuncture.key)
				}
				if (hasComplication && hasComplication.key) {
					this.onProcessHasComplicationSelect(hasComplication.key)
				}

				if (deliveryOutcome && deliveryOutcome.key) {
					this.onProcessDeliveryOutcomeSelect(deliveryOutcome.key)
				}

				var form = this.form.getForm();
				var SBP = form.findField("SBP");
				if (SBP) {
					SBP.on("blur", this.onSbpChange, this);
					SBP.on("keyup", this.onSbpChange, this);
				}

				var DBP = form.findField("DBP");
				if (DBP) {
					DBP.on("blur", this.onDbpChange, this);
					DBP.on("keyup", this.onDbpChange, this);
				}

				if (numberofply && numberofply.key && deliveryOutcome
						&& deliveryOutcome.key) {
					var data = {
						"numberofply" : numberofply.key,
						"deliveryOutcome" : deliveryOutcome.key

					}
					this.fireEvent("checkTab", data, this);
				}

			},
			onProcessDeliveryWaySelect : function(deliveryWay) {
				if (deliveryWay == '6') {
					this.changeFieldState(false, "otherDeliveryWay");
				} else {
					this.changeFieldState(true, "otherDeliveryWay");
				}
			},

			onProcessisConjunctureSelect : function(conjuncture) {
				if (conjuncture == '1') {
					this.changeFieldState(false, "ConjunctureDesc");
				} else {
					this.changeFieldState(true, "ConjunctureDesc");
				}
			},

			onProcessHasComplicationSelect : function(hasComplication) {
				if (hasComplication == '1') {
					this.changeFieldState(false, "ComplicationDesc");
				} else {
					this.changeFieldState(true, "ComplicationDesc");
				}
			},
			onProcessDeliveryOutcomeSelect : function(deliveryOutcome) {
				if (deliveryOutcome == '2') {
					this.changeFieldState(false, "diePeriod");
				} else {
					this.changeFieldState(true, "diePeriod");
				}
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "pregnantId",
					"fieldValue" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
				};
			},
			onLoadNoData : function() {
				this.doNew();
			},
			onReady : function() {
				chis.application.mhc.script.delivery.DeliveryRecord2Form.superclass.onReady
						.call(this);
				var form = this.form.getForm();

				var deliveryWay = form.findField("deliveryWay");
				var isConjuncture = form.findField("isConjuncture");
				var hasComplication = form.findField("hasComplication");
				var deliveryOutcome = form.findField("deliveryOutcome");

				var SBP = form.findField("SBP");
				if (SBP) {
					SBP.on("blur", this.onSbpChange, this);
					SBP.on("keyup", this.onSbpChange, this);
				}

				var DBP = form.findField("DBP");
				if (DBP) {
					DBP.on("blur", this.onDbpChange, this);
					DBP.on("keyup", this.onDbpChange, this);
				}
				if (deliveryWay) {
					deliveryWay.on("select", function(field) {
								var value = field.getValue();
								this.onProcessDeliveryWaySelect(value);
							}, this);
				}
				if (isConjuncture) {
					isConjuncture.on("select", function(field) {
								var value = field.getValue();
								this.onProcessisConjunctureSelect(value);
							}, this);
				}
				if (hasComplication) {
					hasComplication.on("select", function(field) {
								var value = field.getValue();
								this.onProcessHasComplicationSelect(value);
							}, this);
				}
				if (deliveryOutcome) {
					deliveryOutcome.on("select", function(field) {
								var value = field.getValue();
								this.onProcessDeliveryOutcomeSelect(value);
							}, this);
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
				if (diastolic > 500 || diastolic < 50) {
					field.markInvalid("舒张压必须在50到500之间！");
					return;
				}
				var constrictionFld = this.form.getForm().findField("SBP");
				var constriction = constrictionFld.getValue();
				if (constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					constrictionFld.clearInvalid();
				}
			},
			onSbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					return;
				}
				if (constriction > 500 || constriction < 50) {
					field.markInvalid("舒张压必须在50到500之间！");
					return;
				}
				var diastolicFld = this.form.getForm().findField("DBP");
				var diastolic = diastolicFld.getValue();
				if (constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					diastolicFld.clearInvalid();
				}
			}
		});