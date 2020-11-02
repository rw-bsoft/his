/**
 * 疑似残疾儿童信息报告表单
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.disability");
$import("chis.script.BizTableFormView");
chis.application.cdh.script.disability.DisabilityMonitorRecordForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoFieldWidth = false;
	chis.application.cdh.script.disability.DisabilityMonitorRecordForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadNoData, this)
	this.on("beforePrint", this.onBeforePrint, this)
	this.on("save", this.onSave, this);
	this.saveServiceId = "chis.childrenHealthRecordService";
	this.saveAction = "saveChildrenDisabilityMonitor";
	this.initServiceAction = "initChildDisabilityMonitor";
}

Ext.extend(chis.application.cdh.script.disability.DisabilityMonitorRecordForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.cdh.script.disability.DisabilityMonitorRecordForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var disabilityReason = form.findField("disabilityReason");
				if (disabilityReason) {
					disabilityReason.on("select", this.setOtherReason, this);
					disabilityReason.on("valid", this.resetValue, this);
				}

			},

			onSave : function(entryName, op, json, data) {
				var resBody = json.body;
				if (!resBody) {
					return;
				}
				var updateHealthCard = resBody.updateHealthCard;
				if (updateHealthCard) {
					this.fireEvent("refreshData", "H_01");
				}
			},

			getSaveRequest : function(saveData) {
				var values = saveData;
				var form = this.form.getForm();
				var disabilityType = form.findField("disabilityType");
				values["disabilityType_text"] = disabilityType.getRawValue();
				return values;
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "phrId",
					"fieldValue" : this.exContext.ids["CDH_HealthCard.phrId"]
				};
			},

			onLoadNoData : function() {
				this.data.empiId = this.exContext.ids.empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								"phrId" : this.exContext.ids["CDH_HealthCard.phrId"]
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								this.initFormData(json.body);
								this.initDataId = null;
							}
						}, this)
			},

			setOtherReason : function(combo, record, index) {
				var value = combo.value
				var valueArray = value.split(",");
				if (valueArray.indexOf("16") != -1) {
					combo.clearValue();
					if (record.data.key == 16) {
						combo.setValue({
									key : 16,
									text : "原因未明"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : 16,
								text : "原因未明"
							});
				}
				var lastValue = combo.getValue();
				var disable = true;
				if (lastValue.indexOf("15") > -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherReason");
			},

			resetValue : function(field) {
				field.setEditable(false);
			},

			onLoadData : function(entryName, body) {
				var disabilityReason = body["disabilityReason"]
				if (disabilityReason) {
					var disable = true;
					if (disabilityReason.key)
						if (disabilityReason.key.indexOf("15") > -1) {
							disable = false;
						}
					this.changeFieldState(disable, "otherReason");
				}

			},

			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.0-3DisabilityChildrenMonitor"];
				ids_str.value = "&phrId="
						+ this.exContext.ids["CDH_HealthCard.phrId"]
						+ "&empiId=" + this.exContext.ids.empiId;
				return true;
			}
		});
