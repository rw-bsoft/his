/**
 * 产时信息表单页面
 * 
 * @author : Liyt
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizTableFormView")

chis.application.mhc.script.delivery.DeliveryChildrenRecordForm = function(cfg) {
	cfg.width = 850;
	cfg.colCount = 3;
	cfg.labelWidth = 100;
	cfg.fldDefaultWidth = 175;
	cfg.autoFieldWidth = false
	chis.application.mhc.script.delivery.DeliveryChildrenRecordForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("doNew", this.onDoNew, this);
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(chis.application.mhc.script.delivery.DeliveryChildrenRecordForm,
		chis.script.BizTableFormView, {

			beforeCreate : function() {
				this.data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				this.data["motherEmpiId"] = this.exContext.empiData.empiId;
			},

			onDoNew : function() {
				if (!this.lastMenstrualPeriod) {
					var res = util.rmi.miniJsonRequestSync({
						serviceId : "chis.pregnantRecordService",
						serviceAction : "getPregnantGsetational",
						method:"execute",
						body : {
							"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
						}
					})
					if (res.code == 200) {
						this.lastMenstrualPeriod = res.json.body;
					}
				}
			},

			onLoadData : function(body) {

			},

			onReady : function() {
				chis.application.mhc.script.delivery.DeliveryChildrenRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var deliveryDate = form.findField("deliveryDate");
				if (deliveryDate) {
					deliveryDate.on("valid", this.setDeliveryWeeks, this);
				}

				var birthDefect = form.findField("birthDefect");
				if (birthDefect) {
					birthDefect.on("select", function(field) {
								var value = field.getValue();
								this.onPrecessBirthDefectSelect(value);
							}, this);
				}
				var isDie = form.findField("isDie");
				if (isDie) {
					isDie.on("select", function(field) {
								var value = field.getValue();
								this.onPrecessIsDieSelect(value);
							}, this);
				}
				var perinatalExpCase = form.findField("perinatalExpCase");
				if (perinatalExpCase) {
					perinatalExpCase.on("select", this.onPerinatalExcpSelect,
							this)
				}

				var aliveCase = form.findField("aliveCase");
				if (aliveCase) {
					aliveCase.on("select", function(field) {
								var value = field.getValue();
								this.onAliveCaseSelect(value);
							}, this)
				}

				var medicineUseCase = form.findField("medicineUseCase");
				if (medicineUseCase) {
					medicineUseCase.on("select", function(field) {
								var value = field.getValue();
								this.onMedcUseCaseSelect(value);
							}, this)
				}

				var lostUseCase = form.findField("lostUseCase");
				if (lostUseCase) {
					lostUseCase.on("select", function(field) {
								var value = field.getValue();
								this.onLostUseCaseSelect(value);
							}, this)
				}

				var NeonatalComplications = form
						.findField("NeonatalComplications");
				if (NeonatalComplications) {
					NeonatalComplications.on("select",
							this.onNeonatalComplications, this);
				}

				var deliveryOutcome = form.findField("deliveryOutcome");
				if (deliveryOutcome) {
					deliveryOutcome.on("select", this.onDeliveryOutcome, this);
				}

			},

			onDeliveryOutcome : function(field) {
				var form = this.form.getForm();
				var isDie = form.findField("isDie");
				var value = field.getValue();
				if (value == "1") {
					isDie.setValue({
								key : "2",
								text : "否"
							})
				} else if (value == "2" || value == "3") {
					isDie.setValue({
								key : "1",
								text : "是"
							})
				}
				var v = isDie.getValue();				
				this.onPrecessIsDieSelect(v);
			},

			onNeonatalComplications : function(field) {
				var form = this.form.getForm();
				var neonatalComplicationsDes = form
						.findField("neonatalComplicationsDes");
				var value = field.getValue();
				if (value == "1") {
					neonatalComplicationsDes.setValue();
					neonatalComplicationsDes.disable();
				} else if (value == "2") {
					neonatalComplicationsDes.enable();
				}
			},

			onPrecessIsDieSelect : function(isDie) {
				if (isDie == "1") {
					this.changeFieldState(false, "dieResaon");
					this.changeFieldState(false, "dieTime");
				} else {
					this.changeFieldState(true, "dieResaon");
					this.changeFieldState(true, "dieTime");
				}
			},

			onPrecessBirthDefectSelect : function(birthDefect) {
				if (birthDefect == "y") {
					this.changeFieldState(false, "birthDefectDesc");
				} else {
					this.changeFieldState(true, "birthDefectDesc");
				}
			},

			onPerinatalExcpSelect : function(combo, record, index) {
				var value = combo.value
				var valueArray = value.split(",");
				if (valueArray.indexOf("1") != -1) {
					combo.clearValue();
					if (record.data.key == "1") {
						combo.setValue({
									key : "1",
									text : "无"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : "1",
								text : "无"
							});
				}
				this.perinatalExpOther(value);
			},

			perinatalExpOther : function(value) {
				if (!value) {
					return;
				}
				var defectDisable = true;
				var otherDisable = true
				if (value.indexOf("5") != -1) {
					defectDisable = false;
				}
				if (value.indexOf("9") != -1) {
					otherDisable = false;
				}
				this.changeFieldState(defectDisable, "perinatalDefect");
				this.changeFieldState(otherDisable, "perinatalExpOther");
			},

			onAliveCaseSelect : function(value) {
				if (!value) {
					return;
				}
				var disable = true
				if (value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "deadReason");
				this.changeFieldState(disable, "deadDate");
			},

			onMedcUseCaseSelect : function(value) {
				if (!value) {
					return;
				}
				var disable = true
				if (value == "1") {
					disable = false;
				}
				this.changeFieldState(disable, "medicine");
				this.changeFieldState(disable, "beginUseDate");
				this.changeFieldState(disable, "stopUseDate");
				this.changeFieldState(disable, "lostUseCase");
			},

			onLostUseCaseSelect : function(value) {
				if (!value) {
					return;
				}
				var disable = true
				if (value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "lostUseNum");
			},

			setDeliveryWeeks : function(field) {
				var date = field.getValue();
				if (!date) {
					return;
				}
				if (!this.lastMenstrualPeriod) {
					return;
				}
				var weeks = (((date - Date.parseDate(this.lastMenstrualPeriod,
						"Y-m-d"))
						/ 1000 / 60 / 60 / 24) + 1)
						/ 7;
				this.form.getForm().findField("deliveryWeeks").setValue(Math
						.floor(weeks));
			},

			loadData : function() {
				this.doNew();
				var datas = this.exContext.args.formDatas
				if (!datas) {
					return;
				}
				this.initFormData(datas);
				this.fireEvent("loadData", datas, this);
			}
		});