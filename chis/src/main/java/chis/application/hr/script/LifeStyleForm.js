/**
 * 个人生活习惯表单页面
 * 
 * @author tianj
 */
$package("chis.application.hr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hr.script.LifeStyleForm = function(cfg) {
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 150;
	cfg.autoFieldWidth = false;
	chis.application.hr.script.LifeStyleForm.superclass.constructor.apply(this,
			[cfg]);
	this.initDataId = this.exContext.ids.lifeStyleId;
	this.showOwnerName = false;
	this.on("loadData", this.onLoadData, this);
	this.on("addfield", this.onAddField, this);
}

Ext.extend(chis.application.hr.script.LifeStyleForm,
		chis.script.BizTableFormView, {
			onLoadData : function(entryName, body) {
				var somke = body.smokeFreqCode;
				if (somke) {
					this.removeSmokeField(somke.key);
				}

				var drink = body.drinkFreqCode;
				if (drink) {
					this.removeDrinkField(drink.key);
				}

				var train = body.trainFreqCode;
				if (train) {
					this.removeTrainField(train.key);
				}
			},

			onReady : function() {
				chis.application.hr.script.LifeStyleForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var f = form.findField("smokeFreqCode");
				if (f) {
					f.on("select", this.smokeField, this);
					f.on("keyup", this.smokeField, this);
					f.on("blur", this.smokeField, this);
				}
				var field = form.findField("drinkFreqCode");
				if (field) {
					field.on("select", this.drinkField, this);
					field.on("keyup", this.drinkField, this);
					field.on("blur", this.drinkField, this);
				}
				var trField = form.findField("trainFreqCode");
				if (trField) {
					trField.on("select", this.trainField, this);
					trField.on("keyup", this.trainField, this);
					trField.on("blur", this.trainField, this);
				}
				var eateHabit = form.findField("eateHabit");
				if (eateHabit) {
					eateHabit.on("select", this.setEateHabit, this);
					eateHabit.on("keyup", this.setEateHabit, this);
					eateHabit.on("blur", this.setEateHabit, this);
				}
			},

			setEateHabit : function(combo, record, index) {
				if (!record) {
					return;
				}
				var value = combo.getValue();
				var valueArray = value.split(",");
				var count = valueArray.length;
				for (var i = 0; i < count; i++) {
					if (this.valueArray && valueArray[i]) {
						var vaa = this.valueArray;
						if (vaa.indexOf("1") == -1
								&& valueArray.indexOf("1") != -1) {
							valueArray.remove("2");
							valueArray.remove("3");
						} else if (vaa.indexOf("2") == -1
								&& valueArray.indexOf("2") != -1) {
							valueArray.remove("1");
							valueArray.remove("3");
						} else if (vaa.indexOf("3") == -1
								&& valueArray.indexOf("3") != -1) {
							valueArray.remove("1");
							valueArray.remove("2");
						}
					}
				}
				combo.clearValue();
				var v = valueArray.toString();
				combo.setValue(v);
				this.valueArray = valueArray;
			},

			setSmokes : function(flag) {
				var smokeCount = this.form.getForm().findField("smokeCount");
				var smokeTypeCode = this.form.getForm()
						.findField("smokeTypeCode");
				var smokeEndAge = this.form.getForm().findField("smokeEndAge");
				var smokeStartAge = this.form.getForm()
						.findField("smokeStartAge");
				var tobacco = this.form.getForm().findField("tobacco");
				if (flag == "1" || flag == "2") {
					smokeCount.enable();
					smokeTypeCode.enable();
					// smokeFreqCode.enable();
					tobacco.enable();
				} else {
					smokeCount.reset();
					smokeCount.disable();
					smokeTypeCode.reset();
					smokeTypeCode.disable();
					// smokeFreqCode.reset();
					// smokeFreqCode.disable();
					tobacco.reset();
					tobacco.disable();
				}
				var cfg = {};
				if (flag == "3") {
					smokeEndAge.allowBlank = false;
					smokeEndAge.invalidText = "必填字段"
					Ext.getCmp(smokeEndAge.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + "戒烟开始年龄:"
									+ "</span>");
				} else {
					smokeEndAge.allowBlank = true;
					smokeEndAge.invalidText = ""
					Ext.getCmp(smokeEndAge.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update("戒烟开始年龄:");
				}
				if (flag != "4" && flag != "") {
					smokeStartAge.allowBlank = false;
					smokeStartAge.invalidText = "必填字段"
					Ext.getCmp(smokeStartAge.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + "开始吸烟年龄:"
									+ "</span>");
				} else {
					smokeStartAge.allowBlank = true;
					smokeStartAge.invalidText = ""
					Ext.getCmp(smokeStartAge.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update("开始吸烟年龄:");
				}
				this.validate()
				// if (flag == "2") {
				// smokeFreqCode.setValue({
				// key : "3",
				// text : "过去吸，现在不吸"
				// })
				// }
			},

			smokeField : function(combo, record, index) {
				var value = combo.value;
				this.removeSmokeField(value);
				this.setSmokes(value);
			},

			removeSmokeField : function(value) {
				var form = this.form.getForm();
				var filed = ["smokeTypeCode", "smokeCount", "smokeStartAge",
						"smokeAddiAge", "smokeYears", "smokeEndAge",
						"sryOutMethodCode", "dryOutMethodCode", "tobacco"];
				for (var i = 0; i < filed.length; i++) {
					var item = filed[i];
					var f = form.findField(item);
					if (f) {
						if (value == "4" || value == "") {
							f.setValue("");
							f.disable();
						} else {
							f.enable();
						}
					}
				}
			},

			drinkField : function(combo, record, index) {
				var value = combo.value;
				this.removeDrinkField(value);
			},

			removeDrinkField : function(value) {
				var form = this.form.getForm();
				var filed = ["drinkTypeCode", "drinkCount", "drinkStartAge",
						"drinkEndAge", "isDrinkEnd", "drunk"];
				for (var i = 0; i < filed.length; i++) {
					var item = filed[i];
					var f = form.findField(item);
					if (f) {
						if (value == "1" || value == "") {
							f.setValue("");
							f.disable();
						} else {
							f.enable();
						}
					}
				}
			},

			trainField : function(combo, record, index) {
				var value = combo.value;
				this.removeTrainField(value);
			},

			removeTrainField : function(value) {
				var form = this.form.getForm();
				var filed = ["trainMinute", "trainYear", "trainModeCode",
						"trainSiteCode", "workTypeCode", "workIntenCode",
						"workFreqCode"];
				for (var i = 0; i < filed.length; i++) {
					var item = filed[i];
					var f = form.findField(item);
					if (f) {
						if (value == "4" || value == "") {
							f.setValue("");
							f.disable();
						} else {
							f.enable();
						}
					}
				}
			},

			onAddField : function(f, it) {
				if (it.id == "smokeFreqCode" || it.id == "drinkFreqCode"
						|| it.id == "eateHabit" || it.id == "trainFreqCode")
					f.labelStyle = 'color:blue;width:80px;font-weight:bold';
			},

			getLoadRequest : function() {
				if (this.exContext.ids.empiId) {
					return {
						empiId : this.exContext.ids.empiId
					};
				} else {
					return null;
				}
			},

			getSaveRequest : function(saveData) {
				saveData["empiId"] = this.exContext.ids.empiId;
				return saveData;
			}
		});