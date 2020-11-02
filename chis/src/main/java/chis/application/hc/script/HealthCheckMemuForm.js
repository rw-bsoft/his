$package("app.bia.hc")
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")

chis.application.hc.script.HealthCheckMemuForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 110;
	cfg.autoFieldWidth = false;
	cfg.labelWidth = 95;
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	this.printurl = chis.script.util.helper.Helper.getUrl();
	chis.application.hc.script.HealthCheckMemuForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.hc.script.HealthCheckMemuForm, chis.script.BizTableFormView, {
	initPanel : function() {
		var panel = chis.application.hc.script.HealthCheckMemuForm.superclass.initPanel
				.call(this);
		this.onThisYear();
		return panel;
	},

	doNew : function() {
		this.initDataId = this.exContext.args.initDataId;
		chis.application.hc.script.HealthCheckMemuForm.superclass.doNew.call(this);
	},

	doCreate : function() {
		this.exContext.args.initDataId = null;
		this.fireEvent("create", this);
		this.onThisYear();
		this.changeLabel(false, false, false, false);
		this.doNew();
	},
	
	onBeforeCreate : function(){
			util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "initAnnualHealthCheck",
					method: "execute",
					body : {
						"empiId" : this.exContext.args.empiId
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						var body = json.body;
						this.data["manaDoctorId"] = body;
						var form = this.form.getForm();
						form.findField("manaDoctorId").setValue( body);
					}
				}, this)
	},

	onReady : function() {
		this.other = ["cerebrovascularDiseases", "heartDisease",
				"kidneyDiseases", "VascularDisease", "eyeDiseases"];
		this.otherValue={
			cerebrovascularDiseases:6,
			heartDisease:7,
			kidneyDiseases:6,
			VascularDisease:4,
			eyeDiseases:5
		};
		chis.application.hc.script.HealthCheckMemuForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var height = form.findField("height");
		if (height) {
			height.on("blur", this.onHeightChange, this);
			height.on("keyup", this.onHeightChange, this);
		}
		var weight = form.findField("weight");
		if (weight) {
			weight.on("blur", this.onWeightChange, this);
			weight.on("keyup", this.onWeightChange, this);
		}
		var constriction = form.findField("constriction"); // 收缩压R
		if (constriction) {
			constriction.on("blur", this.onConstrictionChange, this);
			constriction.on("keyup", this.onConstrictionChange, this);

			constriction.on("blur", this.onConstrictionNotBlank, this);
			constriction.on("keyup", this.onConstrictionNotBlank, this);
		}
		var constriction_L = form.findField("constriction_L"); // 收缩压L
		if (constriction_L) {
			constriction_L.on("blur", this.onConstrictionChange, this);
			constriction_L.on("keyup", this.onConstrictionChange, this);

			constriction_L.on("blur", this.onConstrictionNotBlank, this);
			constriction_L.on("keyup", this.onConstrictionNotBlank, this);
		}
		var diastolic = form.findField("diastolic");// 舒张压R
		if (diastolic) {
			diastolic.on("blur", this.onDiastolicChange, this);
			diastolic.on("keyup", this.onDiastolicChange, this);

			diastolic.on("blur", this.onConstrictionNotBlank, this);
			diastolic.on("keyup", this.onConstrictionNotBlank, this);
		}
		var diastolic_L = form.findField("diastolic_L");// 舒张压L
		if (diastolic_L) {
			diastolic_L.on("blur", this.onDiastolicChange, this);
			diastolic_L.on("keyup", this.onDiastolicChange, this);

			diastolic_L.on("blur", this.onConstrictionNotBlank, this);
			diastolic_L.on("keyup", this.onConstrictionNotBlank, this);
		}
		var symptom = form.findField("symptom"); // 症状
		if (symptom) {
			symptom.on("select", this.OnSymptomSelect, this);
		}
		var neurologicalDiseases = form.findField("neurologicalDiseases");
		if (neurologicalDiseases) {
			neurologicalDiseases.on("select", this.OnotherDiseasesone, this);
			neurologicalDiseases.on("blur", this.OnotherDiseasesone, this);
		}
		var otherDiseasesone = form.findField("otherDiseasesone");
		if (otherDiseasesone) {
			otherDiseasesone.on("select", this.OnotherDiseasesone, this);
			otherDiseasesone.on("blur", this.OnotherDiseasesone, this);
		}
		for (var i = 0; i < this.other.length; i++) {
			var field = form.findField(this.other[i]);
			if (field) {
				field.on("select", this.OnOtherSelect, this)
			}
		}
		this.fireEvent("ready");
	},

	OnotherDiseasesone : function(field) {
		var value = field.getValue();
		var fieldName = field.name
		var desc = this.form.getForm().findField(fieldName + "Desc");
		if (desc) {
			if (value.indexOf("2") != -1) { // 有
				desc.enable()
			} else {
				desc.reset()
				desc.disable()
			}
		}
	},

	onHeightChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var height = field.getValue();
		if (!height) {
			return;
		}
		if (height >= 300 || height <= 0) {
			field.markInvalid("身高数值应在0到300之间！");
			return;
		}
		var weight = this.form.getForm().findField("weight").getValue();
		if (height && weight) {
			var temp = height * height / 10000;
			this.form.getForm().findField("bmi").setValue((weight / temp)
					.toFixed(2));
		}
	},

	onWeightChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var weight = field.getValue();
		if (!weight) {
			return;
		}
		if (weight > 500 || weight <= 0) {
			field.markInvalid("体重数值应在0到500之间！");
			return;
		}
		var height = this.form.getForm().findField("height").getValue();
		if (height && weight) {
			var temp = height * height / 10000;
			this.form.getForm().findField("bmi").setValue((weight / temp)
					.toFixed(2));
		}
	},

	onConstrictionNotBlank : function(field) {
		var form = this.form.getForm();
		var consName = field.name;
		/*
		 * //右非空 //左非空 @全红 //左为空 @右红 //右为空 //左为空 @全红 //左非空 @左红
		 */
		// if(consName.indexOf("_L")>0){
		// 右边
		this.diastolic = form.findField("diastolic");
		this.constriction = form.findField("constriction");
		// 左边
		this.diastolic_N = form.findField("diastolic_L");
		this.constriction_N = form.findField("constriction_L");
		var diValue = this.diastolic.getValue();
		var coValue = this.constriction.getValue();
		var diNValue = this.diastolic_N.getValue();
		var coNValue = this.constriction_N.getValue();
		if (diValue != "" || coValue != "") {// 右非空
			if (diNValue != "" || coNValue != "") {// 左非空
				this.changeLabel(false, false, false, false)
			} else {
				this.changeLabel(false, false, true, true)
			}
		} else {
			if (diNValue != "" || coNValue != "") {// 宁一遍非空
				this.changeLabel(true, true, false, false)
			} else {
				this.changeLabel(false, false, false, false)
			}
		}
		this.diastolic.validate();
		this.constriction.validate();
		this.diastolic_N.validate();
		this.constriction_N.validate();
	},

	changeLabel : function(di, co, diN, coN) {
		this.diastolic.el.parent().parent().first().dom.innerHTML = this.diastolic.fieldLabel
				.replace((di == true ? /red/ : /black/), (di == true
								? "black"
								: "red"));
		this.diastolic.allowBlank = di;
		this.constriction.el.parent().parent().first().dom.innerHTML = this.constriction.fieldLabel
				.replace((co == true ? /red/ : /black/), (co == true
								? "black"
								: "red"));
		this.constriction.allowBlank = co;

		this.diastolic_N.el.parent().parent().first().dom.innerHTML = this.diastolic_N.fieldLabel
				.replace((diN == true ? /red/ : /black/), (diN == true
								? "black"
								: "red"));
		this.diastolic_N.allowBlank = diN;
		this.constriction_N.el.parent().parent().first().dom.innerHTML = this.constriction_N.fieldLabel
				.replace((coN == true ? /red/ : /black/), (coN == true
								? "black"
								: "red"));
		this.constriction_N.allowBlank = coN;
		var items = this.schema.items;
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == this.diastolic.name)
				items[i]["not-null"] = !di
			if (items[i].id == this.constriction.name)
				items[i]["not-null"] = !co
			if (items[i].id == this.diastolic_N.name)
				items[i]["not-null"] = !diN
			if (items[i].id == this.constriction_N.name)
				items[i]["not-null"] = !coN
		}

	},

	onBlutdruckChange : function() {
		var diastolic = this.form.getForm().findField("diastolic");
		var constriction = this.form.getForm().findField("constriction");
		var diastolic_L = this.form.getForm().findField("diastolic_L");
		var constriction_L = this.form.getForm().findField("constriction_L");
		if (diastolic.getValue() == 0 && constriction.getValue() == 0
				&& diastolic_L.getValue() == 0
				&& constriction_L.getValue() == 0) {
			return false;
		} else {
			return true;
		}
	},

	setMinBlutdruck : function(field) {
		var constriction = "constriction_L";
		var diastolic = "diastolic_L";
		var fName = field.name;
		if (fName.indexOf("_L") > 0) {
			constriction = "constriction";
			diastolic = "diastolic";
		}
		var constrictionFld = this.form.getForm().findField(constriction);
		var diastolicFld = this.form.getForm().findField(diastolic);
		if (constrictionFld.getValue() == 0 && diastolicFld.getValue() == 0) {
			constrictionFld.minValue = 0;
			diastolicFld.minValue = 0;
		}
	},

	onDiastolicChange : function(field) {
		var diastolic = field.getValue();
		var fName = field.name;
		var constrictionNa = "constriction"
		if (fName.indexOf("_L") > 0) {
			constrictionNa = "constriction_L";
		}
		var constrictionFld = this.form.getForm().findField(constrictionNa);
		var constriction = constrictionFld.getValue();
		if (diastolic == 0 && constriction == 0) {
			field.minValue = 0;
			constrictionFld.minValue = 0;
			if (this.onBlutdruckChange()) {
				return;
			}
		} else {
			this.setMinBlutdruck(field);
		}
		if (constriction) {
			field.maxValue = constriction - 1;
		} else {
			field.maxValue = 500;
		}
		field.minValue = 50;
		if (diastolic) {
			constrictionFld.minValue = diastolic + 1;
		} else {
			constrictionFld.minValue = 50;
		}
		constrictionFld.maxValue = 500;
		field.validate();
		constrictionFld.validate();
	},

	onConstrictionChange : function(field) {
		var constriction = field.getValue();
		var fName = field.name;
		var diastolicNa = "diastolic"
		if (fName.indexOf("_L") > 0) {
			diastolicNa = "diastolic_L";
		}
		var diastolicFld = this.form.getForm().findField(diastolicNa);
		var diastolic = diastolicFld.getValue();
		if (constriction == 0 && diastolic == 0) {
			field.minValue = 0;
			diastolicFld.minValue = 0;
			if (this.onBlutdruckChange()) {
				return;
			}
		} else {
			this.setMinBlutdruck(field);
		}
		if (constriction) {
			diastolicFld.maxValue = constriction - 1;
		} else {
			diastolicFld.maxValue = 500;
		}
		diastolicFld.minValue = 50;
		if (diastolic) {
			field.minValue = diastolic + 1;
		} else {
			field.minValue = 50;
		}
		field.maxValue = 500;
		field.validate();
		diastolicFld.validate();
	},

	// 互斥控制
	OnSymptomSelect : function(c, r) {
		var value = c.getValue();
		var valueArray = value.split(",");
		if (valueArray.indexOf("01") != -1) {
			c.clearValue();
			if (r.data.key == "01") {
				c.setValue({
							key : "01",
							text : "无症状"
						});
			} else {
				c.setValue(r.data.key);
			}
		}
		if (value == "") {
			c.setValue({
						key : "01",
						text : "无症状"
					});
		}
		this.changeSymptomOt(c);
	},

	OnOtherSelect : function(c, r) {
		var value = c.getValue();
		var valueArray = value.split(",");
		if (valueArray.indexOf("1") != -1) {
			c.clearValue();
			if (r.data.key == "1") {
				c.setValue({
							key : "1",
							text : "未发现"
						});
			} else {
				c.setValue(r.data.key);
			}
		}
		if (value == "") {
			c.setValue({
						key : "1",
						text : "未发现"
					});
		}
		this.onOthers(c);
	},

	// 其他选择是控制
	changeSymptomOt : function(c) {
		var value = c.getValue();
		var symptomOt = this.form.getForm().findField("symptomOt");
		if (symptomOt) {
			if (value.indexOf("25") != -1) { // 其他
				symptomOt.enable()
			} else {
				symptomOt.reset()
				symptomOt.disable()
			}
		}
	},

	doPrintCheck : function() {
		// alert("健康检查打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
		if (!this.initDataId) {
			return
		}
		this.empiId = this.exContext.args.empiId;
		this.healthCheck = this.initDataId;
		var url = "resources/chis.prints.template.healthCheck.print?type=" + 1 + "&empiId="
				+ this.empiId + "&healthCheck=" + this.healthCheck
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	},

	onThisYear : function() {
		var checkDate = this.form.getForm().findField("checkDate");
		var p = new Date();
		var minValue = new Date(p.getFullYear(), 0, 1);
		checkDate.setMinValue(minValue);
		checkDate.setMaxValue(p);
		checkDate.validate();
	},

	onBeforeYear : function(y) {
		var checkDate = this.form.getForm().findField("checkDate");
		var minValue = new Date(y.getFullYear(), 0, 1);
		var maxValue = new Date(y.getFullYear(), 11, 31);
		checkDate.setMinValue(minValue);
		checkDate.setMaxValue(maxValue);
		checkDate.validate();
	},

	onLoadData : function(entryName, body) {
		var form = this.form.getForm();
		var checkDate = this.form.getForm().findField("checkDate");
		if (checkDate) {
			if (new Date().getFullYear() == checkDate.getValue().getFullYear()) {
				this.onThisYear();
			} else {
				this.onBeforeYear(checkDate.getValue());
			}
		}
		var neurologicalDiseases = form.findField("neurologicalDiseases");
		if (neurologicalDiseases) {
			this.OnotherDiseasesone(neurologicalDiseases)
		}
		var otherDiseasesone = form.findField("otherDiseasesone");
		if (otherDiseasesone) {
			this.OnotherDiseasesone(otherDiseasesone)
		}
		var symptom = form.findField("symptom");
		if (symptom) {
			this.changeSymptomOt(symptom)
		}
		for (var i = 0; i < this.other.length; i++) {
			var field = form.findField(this.other[i]);
			if (field) {
				this.onOthers(field);
			}
		}
		var xy = ["diastolic_L", "diastolic", "constriction_L", "constriction"]
		for (var i = 0; i < xy.length; i++) {
			var xyfield = form.findField(xy[i]);
			if (xyfield) {
				if (xy[i].indexOf("diastolic") != -1) {
					this.onDiastolicChange(xyfield);
				} else {
					this.onConstrictionChange(xyfield);
				}
				this.onConstrictionNotBlank(xyfield)
			}
		}
	},

	onOthers : function(comb, r, index) {
		var value = comb.getValue();
		var combName = comb.name;
		var form = this.form.getForm();
		var others = form.findField(this.getDesc(combName));
		if (others) {
			if (value.indexOf(this.otherValue[combName]) != -1) {
				others.enable();
			} else {
				others.reset();
				others.disable();
			}
		}
	},

	getDesc : function(f) {
		return "other" + f;
	},

	getSaveRequest : function(saveData) {
		if (this.exContext.args.dataSources) {
			saveData.checkWay = this.exContext.args.dataSources;
		}else{
			saveData.checkWay = "1";
		}
		saveData.phrId = this.exContext.args.phrId;
		saveData.empiId = this.exContext.args.empiId;
		return saveData;
	},

	loadData : function() {
		this.doNew();
		var data = this.exContext.args.data;
		if (!data) {
			this.onThisYear();
			this.resetButtons();
			return;
		}
		data = this.castListDataToForm(data, this.schema);
		this.initFormData(data);
		this.onLoadData();
	}
})