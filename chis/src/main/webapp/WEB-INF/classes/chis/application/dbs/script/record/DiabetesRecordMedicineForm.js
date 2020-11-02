// @@ 随访中服药信息窗口。
$package("chis.application.dbs.script.record");

$import("app.desktop.Module", "chis.script.BizTableFormView");

chis.application.dbs.script.record.DiabetesRecordMedicineForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	cfg.labelAlign = "left";
	cfg.labelWidth = 90;
	cfg.width = 500;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 150
	cfg.colCount = 2;
	cfg.showButtonOnTop = false;
	this.backDataField = [{
				"medicineId" : "YPXH"
			}];
	this.backOtherField = [{
				"medicineDosage" : "YPJL"
			}, {
				"medicineUnit" : "JLDW"
			}]
	chis.application.dbs.script.record.DiabetesRecordMedicineForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onClose, this);
	this.on("save", this.onSave, this)
	this.on("beforeSave", this.onBeforeSave, this)
};

Ext.extend(chis.application.dbs.script.record.DiabetesRecordMedicineForm,
		chis.script.BizTableFormView, {
			onWinShow : function() {
				if(this.initDataId){
					var data = this.castListDataToForm(this.r.data,this.schema)
					this.initFormData(data)
				}else{
					this.doCreateMedicine()
				}
				
			},
			onSideEffectsFlagChange : function(combo, record, index) {
				var newValue = record.data.key;
				if (newValue == 1) {
					this.form.getForm().findField("sideEffects").enable();
				}
				if (newValue == 2) {
					this.form.getForm().findField("sideEffects").disable();
					this.form.getForm().findField("sideEffects").setValue();
				}
			},

			doCreateMedicine : function() {
				this.op = "create";
				this.doNew();
				this.initDataId = null
				this.data["phrId"] = this.exContext.ids.phrId;
//				this.data["visitId"] = this.exContext.r.get("visitId");
			},
			saveToServer : function(saveData) {
				saveData.phrId = this.exContext.ids.phrId
				saveData.visitId = "0000000000000000"
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.diabetesRecordService",
							serviceAction : "saveMedicine",
							method:"execute",
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], json.body);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "create";
						}, this)// jsonRequest
			},

			doCancel : function() {
				this.fireEvent("close");
				this.getWin().hide();
			},
			onSave : function() {
				if(this.win){
					this.win.hide()
				}
			},
			onClose : function() {
				this.form.getForm().reset();
			},
			onReady : function() {
				chis.application.dbs.script.record.DiabetesRecordMedicineForm.superclass.onReady.call(this)
				var form = this.form.getForm()
				var medicineDosage = form.findField("medicineDosage")
				this.medicineDosage = medicineDosage
				medicineDosage.on("blur", this.onMedicineDosage, this)
				medicineDosage.on("keyup", this.onMedicineDosage, this)
				var medicineMoring = form.findField("medicineMoring")
				this.medicineMoring = medicineMoring
				medicineMoring.on("blur", this.onMedicine, this)
				medicineMoring.on("keyup", this.onMedicine, this)
				var medicineNoon = form.findField("medicineNoon")
				this.medicineNoon = medicineNoon
				medicineNoon.on("blur", this.onMedicine, this)
				medicineNoon.on("keyup", this.onMedicine, this)
				var medicineNight = form.findField("medicineNight")
				this.medicineNight = medicineNight
				medicineNight.on("blur", this.onMedicine, this)
				medicineNight.on("keyup", this.onMedicine, this)
				var medicineSleep = form.findField("medicineSleep")
				this.medicineSleep = medicineSleep
				medicineSleep.on("blur", this.onMedicine, this)
				medicineSleep.on("keyup", this.onMedicine, this)
			},
			onMedicineDosage : function() {
				if (this.medicineDosage.getValue() == "") {
					this.medicineMoring.enable()
					this.medicineNoon.enable()
					this.medicineNight.enable()
					this.medicineSleep.enable()
				} else {
					this.medicineMoring.disable()
					this.medicineNoon.disable()
					this.medicineNight.disable()
					this.medicineSleep.disable()
					this.medicineMoring.setValue()
					this.medicineNoon.setValue()
					this.medicineNight.setValue()
					this.medicineSleep.setValue()
				}
			},
			onMedicine : function() {
				if (this.medicineMoring.getValue() != ""
						|| this.medicineNoon.getValue() != ""
						|| this.medicineNight.getValue() != ""
						|| this.medicineSleep.getValue() != "") {
					this.medicineDosage.disable()
					this.medicineDosage.setValue()
				}else{
					this.medicineDosage.enable()
				}
			},
			
			onBeforeSave : function(entryName, op, saveData) {
				if (saveData.medicineDosage == ""
						&& saveData.medicineMoring == ""
						&& saveData.medicineNoon == ""
						&& saveData.medicineNight == ""
						&& saveData.medicineSleep == "") {
					Ext.Msg.alert("消息提示", "剂量必须填写一个")
					return false
				}
			}
		});