$package("chis.application.rvc.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.rvc.script.RetiredVeteranCadresRecordForm = function(cfg) {
	cfg.autoLoadData = false;// 370921193810173981
	cfg.fldDefaultWidth = 180;
	cfg.autoFieldWidth = false;
	cfg.labelWidth = 90;
	chis.application.rvc.script.RetiredVeteranCadresRecordForm.superclass.constructor
			.apply(this, [cfg]);
	// this.on("loadData", this.onLoadData, this);
	this.on("beforeCreate", this.onBeforeCreate, this)
}

Ext.extend(chis.application.rvc.script.RetiredVeteranCadresRecordForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				this.initDataId = this.exContext.ids["RVC_RetiredVeteranCadresRecord.phrId"];
				chis.application.rvc.script.RetiredVeteranCadresRecordForm.superclass.doNew
						.call(this);
			},

			loadData : function() {
				var form = this.form.getForm();
				var menophania = form.findField("menophania");
				var period = form.findField("period");
				var leucorrhea = form.findField("leucorrhea");
				var menopause = form.findField("menopause");
				var parity = form.findField("parity");
				var mature = form.findField("mature");
				var prematureDelivery = form.findField("prematureDelivery");
				var abortion = form.findField("abortion");
				if (this.exContext.empiData.sexCode == 2) {
					menophania.enable();
					period.enable();
					leucorrhea.enable();
					menopause.enable();
					parity.enable();
					mature.enable();
					prematureDelivery.enable();
					abortion.enable();
				} else {
					menophania.disable();
					period.disable();
					leucorrhea.disable();
					menopause.disable();
					parity.disable();
					mature.disable();
					prematureDelivery.disable();
					abortion.disable();
				}
				chis.application.rvc.script.RetiredVeteranCadresRecordForm.superclass.loadData
						.call(this);
			},

			onReady : function() {
				var form = this.form.getForm();
				var manaDoctorId = form.findField("manaDoctorId");
				manaDoctorId.on("select", this.onManaDoctorIdSelect, this);
				var onceDisease = form.findField("onceDisease");
				onceDisease.on("select", this.onOnceDiseaseSelect, this);
				chis.application.rvc.script.RetiredVeteranCadresRecordForm.superclass.onReady
						.call(this);
			},

			onOnceDiseaseSelect : function(combo, record, index) {
				var value = combo.getValue();
				var valueArray = value.split(",");
				var selValue = record.data.key;
				var other = this.form.getForm().findField("other");
				if (valueArray.indexOf("98") != -1
						|| valueArray.indexOf("97") != -1) {
					combo.clearValue();
					if (record.data.key == 98) {
						combo.setValue(98);
						other.setValue();
						other.disable();
					} else if (record.data.key == 97) {
						combo.setValue(97);
						other.setValue();
						other.disable();
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue(98);
					other.setValue();
					other.disable();
				}
				if (record.data.key == 99) {
					if (valueArray.indexOf("99") != -1) {
						other.enable();
					} else {
						other.setValue();
						other.disable();
					}
				}
			},

			onBeforeCreate : function() {
				var form = this.form.getForm();
				var phrId = form.findField("phrId");
				phrId.setValue(this.exContext.ids.phrId);
				phrId.disable();
				this.data.empiId = this.exContext.ids.empiId
				this.form.el.mask("正在初始化数据，请稍后...")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "initializeRecord",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId
							}
						})
				this.form.el.unmask()
				var body = result.json.body;
				this.initFormData(body);
			},

			initFormData : function(data) {
				var other = this.form.getForm().findField("other");
				if (data.onceDisease && data.onceDisease.key) {
					if (data.onceDisease.key.indexOf("99") != -1) {
						other.enable();
					} else {
						other.setValue();
						other.disable();
					}
				}
				chis.application.rvc.script.RetiredVeteranCadresRecordForm.superclass.initFormData
						.call(this, data);
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
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("manaUnitId");
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

			getLoadRequest : function() {
				if (!this.exContext.ids["RVC_RetiredVeteranCadresRecord.phrId"]) {
					this.initDataId = null;
					return {
						"fieldName" : "phrId",
						"fieldValue" : this.exContext.ids.phrId
					};
				} else {
					return null;
				}
			},

			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				var saveRequest = this.getSaveRequest(saveData);
				var saveCfg = {
					serviceId : this.saveServiceId,
					method : this.saveMethod,
					op : this.op,
					schema : this.entryName,
					module : this._mId, // 增加module的id
					body : saveRequest
				}
				this.fixSaveCfg(saveCfg);
				util.rmi.jsonRequest(saveCfg, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								this
										.fireEvent("exception", code, msg,
												saveData); // **进行异常处理
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body);
							}
							this.afterSaveData(this.entryName, this.op, json,
									this.data);
							this.op = "update"
						}, this)// jsonRequest
			},

			afterSaveData : function(entryName, op, json, data) {
				this.exContext.ids["RVC_RetiredVeteranCadresRecord.phrId"] = json.body["phrId"];
				if (op == "create") {
					this.fireEvent("refreshModule", 'R_02');
				}
				this.fireEvent("save", entryName, op, json, data);
				this.refreshEhrTopIcon();
			}
		})