$package("chis.application.dbs.script.visit")
$import("util.Accredit", "chis.script.BizTableFormView")
chis.application.dbs.script.visit.DiabetesVisitDescriptionForm = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesVisitDescription"
	cfg.colCount = 2;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 300;
	chis.application.dbs.script.visit.DiabetesVisitDescriptionForm.superclass.constructor
			.apply(this, [cfg])
	this.nowDate = this.mainApp.serverDate
	this.saveServiceId = "chis.diabetesVisitService"
	this.saveAction = "saveDiabetesDescription"
}
Ext.extend(chis.application.dbs.script.visit.DiabetesVisitDescriptionForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.visit.DiabetesVisitDescriptionForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("tongueApprence").on("select",
						this.onTongueApprenceSelect, this);
			},
			onTongueApprenceSelect : function(combo, record, index) {
				var value = combo.getValue();
				var valueArray = value.split(",");
				if (valueArray.indexOf("01") != -1) {
					combo.clearValue();
					if (record.data.key == "01") {
						combo.setValue({
									key : "01",
									text : "正常"
								});
					} else {
						combo.setValue(record.data.key);
					}
				}
				if (value == "") {
					combo.setValue({
								key : "01",
								text : "正常"
							});
				}
			},
			saveToServer : function(saveData) {
				saveData.phrId = this.exContext.ids.phrId
				saveData.visitId = this.exContext.args.r.get("visitId")
				chis.application.dbs.script.visit.DiabetesVisitDescriptionForm.superclass.saveToServer
						.call(this, saveData)
			},
			loadData : function() {
				this.doNew()
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesVisitService",
							serviceAction : "getDiabetesDescription",
							method:"execute",
							body : {
								"visitId" : this.exContext.args.r.get("visitId")
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadData);
					return
				}
				if (result.json.body) {
					this.initFormData(result.json.body)
					this
							.fireEvent("loadData", this.entryName,
									result.json.body);
					this.op = "update"
				} else {
					this.initDataId = null;
				}
				this.resetButton(this.exContext.control)
			},
			resetButton : function(data) {
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
						var status = obj["update"]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			}
		});