/** 
 * 儿童询问表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.inquire")
$import("util.Accredit", "chis.script.BizTableFormView")
chis.application.cdh.script.inquire.ChildrenInquireForm = function(cfg) {
	cfg.width = 850;
	cfg.autoFieldWidth = false
	cfg.labelWidth = 90
	cfg.fldDefaultWidth = 150
	chis.application.cdh.script.inquire.ChildrenInquireForm.superclass.constructor.apply(this,
			[cfg])
	this.initServiceAction = "initChildInquire";
	this.on("doNew", this.onDoNew, this);
	this.on("loadData", this.onLoadData, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
}
Ext.extend(chis.application.cdh.script.inquire.ChildrenInquireForm, chis.script.BizTableFormView, {

	onDoNew : function() {
		this.inquireDate.setMinValue(this.exContext.args.minVisitDate);
		this.inquireDate.setMaxValue(this.exContext.args.maxVisitDate);
		if (this.extend1 >= 12) {
			this.changeFieldState(true, "ricketsSymptom");
		} else {
			this.changeFieldState(false, "ricketsSymptom");
		}
		if (this.extend1 > 24) {
			this.changeFieldState(true, "ricketsSign");
		} else {
			this.changeFieldState(false, "ricketsSign");
		}
		
		var items=['','pneumoniaCount','diarrheaCount','traumaCount','otherCount'];
		for(var i=1,il=items.length;i<=il;i++)
		{
				this.changeFieldState(true, items[i]);
		}
		
	},

	onBeforeCreate : function() {
		this.data["phrId"] = this.exContext.ids["CDH_HealthCard.phrId"];
		var form = this.form.getForm();
		this.inquireDate.setValue(this.planDate);
		this.getAgeDate(this.inquireDate);
		var nowBirth = Date
				.parseDate(this.exContext.empiData.birthday, "Y-m-d");
		nowBirth.setYear(nowBirth.format("Y") + 1);
		this.disableFeed = false;
		if (this.planBeginDate > nowBirth) {
			this.disableFeed = true;
		}
		var feedWay = form.findField("feedWay");
		if (feedWay) {
			var flage = false
			this.changeFieldStatus(flage, false);
		}
		var breastMilkCount = form.findField("breastMilkCount");
		if (breastMilkCount) {
			if (this.disableFeed) {
				breastMilkCount.disable();
			} else {
				breastMilkCount.enable();
			}
		}
		this.form.el.mask("正在加载数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.initServiceAction,
					method:"execute",
					schema : this.entryName,
					body : {
						"empiId" : this.exContext.ids.empiId,
						"extend1" : this.extend1,
						"inquireId" : this.preInqId
					}
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					if (json.body) {
						var body = json.body;
						this.initFormData(body);
						var weanFlag = body.weanFlag;
						if (weanFlag) {
							this.setWeanMess(weanFlag.key);
						}
						var inquireDate = body.inquireDate;
						if (inquireDate) {
							this.getAgeDate(this.inquireDate);
						}

					}
				}, this)
	},

	getFormData : function() {
		var ac = util.Accredit;
		var form = this.form.getForm()
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items

		Ext.apply(this.data, this.exContext.empiData)

		if (items) {
			var n = items.length
			var checked;
			for (var i = 0; i < n; i++) {
				checked = false;
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] || it.defaultValue

				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					checked = !f.allowBlank;
					v = f.getValue()
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					// end
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
				} else {
					checked = true;
				}
				if (v == null || v === "") {
					if (checked && !it.pkey && it["not-null"] && !it.ref) {
						if (it.id == "feedWay") {
							if (!this.disableFeed) {
								Ext.Msg.alert("提示信息", it.alias + "不能为空")
								return;
							}

						} else {
							Ext.Msg.alert("提示信息", it.alias + "不能为空")
							return;
						}
					}
				}
				values[it.id] = v;
			}
		}
		return values;
	},

	onBeforeSave : function(entryName, op, saveData) {
		this.data.planId = this.planId
		saveData["planId"] = this.planId
	},

	onReady : function() {
		chis.application.cdh.script.inquire.ChildrenInquireForm.superclass.onReady.call(this)
		var form = this.form.getForm();

		var inquireDate = form.findField("inquireDate");
		if (inquireDate) {
			this.inquireDate = inquireDate;
			inquireDate.on("valid", this.getAgeDate, this);
		}

		var weanFlag = form.findField("weanFlag");
		if (weanFlag)
			weanFlag.on("select", function(f) {
						var value = f.getValue();
						this.setWeanMess(value);
					}, this);

		var fecesColor = form.findField("fecesColor");
		if (fecesColor) {
			fecesColor.on("select", this.setOtherColor, this);
		}

		var illness = form.findField("illness");
		if (illness) {
			illness.on("select", this.setIllnessType, this);
		}

		var illnessType = form.findField("illnessType");
		if (illnessType) {
			illnessType.on("select", function(field) {
						var value = field.getValue();
						this.setIllnessTypeStatus(value);
					}, this);
		}

		var vitaminADFlage = form.findField("vitaminADFlage");
		if (vitaminADFlage) {
			vitaminADFlage.on("select", this.setVitaminAD, this);
		}
	},

	setIllnessTypeStatus : function(value) {
		var items=['','pneumoniaCount','diarrheaCount','traumaCount','otherCount'];
		for(var i=1,il=items.length;i<=il;i++)
		{
			if(value&&value.indexOf(i)!=-1)
			{
				this.changeFieldState(false, items[i]);	
			}else
			{
				this.changeFieldState(true, items[i]);	
			}
		}
	},

	getAgeDate : function(field) {
		var birthDay = Date
				.parseDate(this.exContext.empiData.birthday, "Y-m-d");
		if (birthDay) {
			var visitDate = field.getValue();
			var form = this.form.getForm();
			if(!visitDate){
				return
			}
			var diffDate = (visitDate.getTime() - birthDay.getTime())
					/ (24 * 60 * 60 * 1000);
			var ageDate = form.findField("ageDate");
			ageDate.setValue(diffDate);
		}
	},

	setVitaminAD : function(field) {
		var value = field.value;
		var disable = true;
		if (value == "1")
			disable = false;
		this.changeFieldState(disable, "vitaminADName");
		this.changeFieldState(disable, "vitaminAD");
	},

	setIllnessType : function(field) {
		var value = field.value
		var disable = true;
		if (value == "1") {
			disable = false;
		}
		this.changeFieldState(disable, "illnessType");
	},

	setOtherColor : function(field) {
		var value = field.value
		var disable = true;
		if (value == "3") {
			disable = false;
		}
		this.changeFieldState(disable, "otherColor");
	},

	setWeanMess : function(value) {
		var disable = false;
		if (value == "n") {
			disable = true;
		} else {
			var feedWay = this.form.getForm().findField("feedWay");
			if (feedWay && !feedWay.disabled)
				feedWay.setValue({
							key : "4",
							text : "人工喂养"
						});
		}
		this.changeFieldState(disable, "weanMonth");
	},

	onLoadData : function(entryName, body) {
		var weanFlag = body["weanFlag"]
		if (weanFlag) {
			var disable = false;
			if (weanFlag.key == "n") {
				disable = true;
			}
			this.changeFieldState(disable, "weanMonth");
		}

		var fecesColor = body["fecesColor"]
		if (fecesColor) {
			var disable = true;
			if (fecesColor.key == "3") {
				disable = false;
			}
			this.changeFieldState(disable, "otherColor");
		}

		var vitaminADFlage = body["vitaminADFlage"]
		if (vitaminADFlage) {
			var disable = true;
			if (vitaminADFlage.key == "1") {
				disable = false;
			}
			this.changeFieldState(disable, "vitaminADName");
			this.changeFieldState(disable, "vitaminAD");
		}

		var illness = body["illness"];
		if (illness) {
			var disable = true;
			if (illness.key == "1") {
				disable = false;
			}
			this.changeFieldState(disable, "illnessType");
		}

		var illnessType = body["illnessType"];
		if (illnessType && illnessType.key) {
			this.setIllnessTypeStatus(illnessType.key);
		}else
		{
			this.setIllnessTypeStatus('');	
		};
	},

	changeFieldStatus : function(flage, clearDateField) {
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			if (((item["not-null"] == "1" || item["not-null"] == "true") && item.id != "inquireDate")) {
				this.setFieldNotNull(flage, item)
			} else if (item.id == "feedWay") {
				var itemField = this.form.getForm().findField(item.id);
				if (itemField) {
					if (this.extend1 < 18) {
						itemField.enable();
					} else {
						itemField.disable();
					}
				}
				if (!this.disableFeed && this.extend1 < 18 && !flage) {
					this.setFieldNotNull(false, item)
				} else {
					this.setFieldNotNull(true, item)
				}
			} else if (item.id == "inquireDate") {
				if (clearDateField && flage) {
					this.form.getForm().findField(item.id).setValue(null);
				}
				this.setFieldNotNull(false, item)
			}
		}
	},

	setFieldNotNull : function(flage, item) {
		var field = this.form.getForm().findField(item.id);
		if (!field) {
			return;
		}
		field.allowBlank = flage
		if (!flage) {
			Ext.getCmp(field.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + item.alias
							+ ":</span>");
		} else {
			Ext.getCmp(field.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:black'>" + item.alias
							+ ":</span>");
		}
		this.validate()
	}
});