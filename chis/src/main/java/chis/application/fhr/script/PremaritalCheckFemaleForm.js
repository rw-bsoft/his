$package("chis.application.fhr.script")
$import("chis.script.util.helper.Helper","chis.script.BizTableFormView")
chis.application.fhr.script.PremaritalCheckFemaleForm = function(cfg) {
	this.entryName = "chis.application.fhr.schemas.PER_XCQYFW"
	this.autoLoadData = false;
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = false;
	cfg.showButtonOnTop = true;
	chis.application.fhr.script.PremaritalCheckFemaleForm.superclass.constructor.apply(this, [cfg])
	this.width = 900
}
Ext.extend(chis.application.fhr.script.PremaritalCheckFemaleForm, chis.script.BizTableFormView, {
	
	
			doNew : function() {
				this.empiId = this.exContext.ids["empiId"];
				this.initDataId = this.exContext.ids["phrId"];
				chis.application.fhr.script.PremaritalCheckFemaleForm.superclass.doNew
						.call(this);
			},
		 
//			checkItemExists : function(schema, item) {
//				for (var i = 0; i < schema.items.length; i++) {
//					var schemaItem = schema.items[i];
//					if (schemaItem.id == item.id)
//						return true;
//				}
//				return false;
//			},
			loadData : function() {
				this.doNew()
		        if (!this.initDataId) {
					this.fireEvent("beforeLoadData");
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true	
				
				util.rmi.jsonRequest({
							serviceId : "chis.premaritalCheckXcqyfwService",
							serviceAction : "loaddata",
							schema : this.entryName,
							method:"execute",
							phrId : this.initDataId
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							}

							if (this.op == 'create') {
								this.op = "update"
							}
							if(!json.body){
								this.op = "create"
								this.formphrId=this.form.getForm().findField("phrId");
								this.formphrId.setValue(this.initDataId);
								this.formphrId.disable();
							}
						}, this)
			},
//
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				 
				if(saveData.stratdate >= saveData.enddate){
					Ext.Msg.show({
					title : '乡村签约服务',
					msg : '签约开始时间必须小于签约结束时间!'});
					return;
				}
				saveData["empiId"]=this.empiId;
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.premaritalCheckXcqyfwService",
							op : this.op,
							schema : this.entryName,
							method:"execute",
							body : saveData,
							serviceAction : "savePremaritalCheckFemale"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)
			}
//			setPhrId : function(_phrId) {
//				var form = this.form.getForm()
//				var phrId = form.findField("phrId")
//				if (phrId) {
//					phrId.setValue(_phrId)
//				}
//			},
//			setManaUnitId : function(_manaUnitId,_manaUnitId_text) {
//				var form = this.form.getForm()
//				var manaUnitId = form.findField("manaUnitId")
//				if (manaUnitId) {
//					manaUnitId.setValue({
//						key:_manaUnitId,
//						text:_manaUnitId_text
//					})
//				}
//			},
//			setManaDoctorId : function(_manaDoctorId,_manaDoctorId_text) {
//				var form = this.form.getForm()
//				var manaDoctorId = form.findField("manaDoctorId")
//				if (manaDoctorId) {
//					manaDoctorId.setValue({
//						key:_manaDoctorId,
//						text:_manaDoctorId_text
//					})
//				}
//			},
//			onReady : function() {
//				chis.application.fhr.script.PremaritalCheckFemaleForm.superclass.onReady.call(this)
//				var form = this.form.getForm()
//
//				var weight = form.findField("weight")
//				this.weight = weight
//				if (weight) {
//					weight.on("keyup", this.onWeightCheck, this)
//				}
//
//				var height = form.findField("height")
//				this.height = height
//				if (height) {
//					height.on("keyup", this.onHeightCheck, this)
//				}
//
//				var manaDoctorId = form.findField("manaDoctorId")
//				this.manaDoctorId = manaDoctorId
//				if (manaDoctorId) {
//					manaDoctorId.on("select", this.onManaDoctorIdSelect, this)
//				}
//			},
//			onManaDoctorIdSelect : function(combo, node, index) {
//				if (node.leaf && node.parentNode != null) {
//					var manaUnitIdCombo = this.form.getForm()
//							.findField("manaUnitId");
//					if (manaUnitIdCombo) {
//						manaUnitIdCombo.setValue({
//									key : node.parentNode.id,
//									text : node.parentNode.text
//								})
//					}
//				}
//			},
//			onDiagnosisDateChange : function() {
//				this.calculateYears()
//			},
//			onHeightCheck : function() {
//				if (this.height.getValue() == "") {
//					return
//				}
//				if (this.height.getValue() > 250
//						|| this.height.getValue() < 130) {
//					//  
//					this.height.markInvalid("身高必须在130-250之间")
//					this.height.focus()
//					return
//				}
//				this.onCalculateBMI()
//			},
//			onWeightCheck : function() {
//				if (this.weight.getValue() == "") {
//					return
//				}
//				if (this.weight.getValue() > 140 || this.weight.getValue() < 20) {
//					//  
//					this.weight.markInvalid("体重必须在20-140之间")
//					this.weight.focus()
//					return
//				}
//				this.onCalculateBMI()
//			},
//			calculateYears : function() {
//				var diagnosisDate = this.form.getForm()
//						.findField("diagnosisDate").getValue()
//						
//				var month = util.helper.Helper.getAgeMonths(diagnosisDate,
//						Date.parseDate(this.mainApp.serverDate, "Y-m-d"));
//				if (month < 1) {
//					var days = util.helper.Helper.getPeriod(diagnosisDate,
//							Date.parseDate(this.mainApp.serverDate, "Y-m-d"));
//					this.form.getForm().findField("years").setValue(days + "天")
//				}
//				if (month >= 12) {
//					var years = parseInt(month / 12);
//					var remainMonth = month - years * 12;
//					if (remainMonth == 0) {
//						return years + "年";
//					}
//					this.form.getForm().findField("years").setValue(years + "年" + remainMonth + "月")
//				}
//				this.form.getForm().findField("years").setValue(month + "月")
//			},
//			setButtonStatus : function(v) {
//				alert(1)
//				var btns = this.form.getTopToolbar().items;
//				if (!btns) {
//					return;
//				}
//				var n = btns.getCount()
//				for (var i = 0; i < n; i++) {
//					var btn = btns.item(i)
//					if (v == "disable") {
//						btn.disable()
//					} else {
//						btn.enable()
//					}
//				}
//			}
		});