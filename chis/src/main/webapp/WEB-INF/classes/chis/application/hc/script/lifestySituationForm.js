$package("app.bia.hc")

$import("chis.script.BizTableFormView")

chis.application.hc.script.lifestySituationForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 110;
	cfg.labelWidth = 95;
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	chis.application.hc.script.lifestySituationForm.superclass.constructor.apply(this,
			[cfg])
	this.selectF = ["whetherDrink", "wehtherSmoke",
			"physicalExerciseFrequency", "drinkingFrequency", "occupational"] // /*,"contactpoisonKinds","coalfireHeating","occupationalexposureHistory","neurologicalDiseases"*/
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.hc.script.lifestySituationForm, chis.script.BizTableFormView, {
			onReady : function() {
				this.other = ["dustPro", "rayPro", "physicalFactorPro",
						"chemicalsPro", "otherPro"]
				this.blank = ["dust", "ray", "physicalFactor", "chemicals",
						"other"]
				chis.application.hc.script.lifestySituationForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				for (var i = 0; i < this.selectF.length; i++) {
					var field = form.findField(this.selectF[i]);
					if (field) {
						field.on("select", eval("this.on" + this.selectF[i]),
								this);
						field.on("blur", eval("this.on" + this.selectF[i]),
								this);
					}
				}
				for (var j = 0; j < this.blank.length; j++) {
					var f = form.findField(this.blank[j]);
					if (f) {
						f.on("blur", this.onblank, this)
					}
				}
				var mainDrinkingVvarieties = form
						.findField("mainDrinkingVvarieties");
				if (mainDrinkingVvarieties) {
					mainDrinkingVvarieties.on("select",
							this.onmainDrinkingVvarieties, this)
				}
				for (var i = 0; i < this.other.length; i++) { // yub
					var field = form.findField(this.other[i]);
					if (field) {
						field.on("blur", this.OnotherDiseasesone, this)
						field.on("select", this.OnotherDiseasesone, this)
					}
				}
				
				
				var beginSmokeTime = form.findField("beginSmokeTime");
				if(beginSmokeTime){
					beginSmokeTime.on("blur",this.beginSmokeTimeControl,this)
				}

				var stopSmokeTime = form.findField("stopSmokeTime");
				if(stopSmokeTime){
					stopSmokeTime.on("blur",this.stopSmokeTimeControl,this)
				}
				
				var geginToDrinkTime = form.findField("geginToDrinkTime");
				if(geginToDrinkTime){
					geginToDrinkTime.on("blur",this.geginToDrinkTimeControl,this)
				}
				
				var stopDrinkingTime = form.findField("stopDrinkingTime");
				if(stopDrinkingTime){
					stopDrinkingTime.on("blur",this.stopDrinkingTimeControl,this)
				}
				var dietaryHabit = form.findField("dietaryHabit");
				if (dietaryHabit) {
					dietaryHabit.on("select", this.setDietaryHabit, this);
					dietaryHabit.on("keyup", this.setDietaryHabit, this);
					dietaryHabit.on("blur", this.setDietaryHabit, this);
				}
			},
			setDietaryHabit : function(combo, record, index) {
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
				var v=valueArray.toString();
				combo.setValue(v);
				this.valueArray = valueArray;
			},
			beginSmokeTimeControl : function(field){
				var stopSmokeTime = this.form.getForm().findField("stopSmokeTime");
				if(stopSmokeTime){
					var value = stopSmokeTime.getValue()
					if(value != null && value != ""){
						field.maxValue = value ;
					}
				}
				field.validate();
			},
			
			geginToDrinkTimeControl : function(field){
				var stopDrinkingTime = this.form.getForm().findField("stopDrinkingTime");
				if(stopDrinkingTime){
					var value = stopDrinkingTime.getValue()
					if(value != null && value != ""){
						field.maxValue = value ;
					}
				}
				field.validate();
			},
			
			stopSmokeTimeControl : function(field){
				var beginSmokeTime = this.form.getForm().findField("beginSmokeTime");
				if(beginSmokeTime){
					var value = beginSmokeTime.getValue()
					if(value != null && value != ""){
						field.minValue = value ;
					}
				}
				field.validate();
			},
			
			stopDrinkingTimeControl : function(field){
				var geginToDrinkTime = this.form.getForm().findField("geginToDrinkTime");
				if(geginToDrinkTime){
					var value = geginToDrinkTime.getValue()
					if(value != null && value != ""){
						field.minValue = value ;
					}
				}
				field.validate();
			},
			
			OnotherDiseasesone : function(field) { // yub
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

			onmainDrinkingVvarieties : function(f) {
				var value = f.getValue()
				var form = this.form.getForm();
				var others = form.findField("drinkOther")
				if (others)
					if (value.indexOf("9") != -1) {
						others.enable();
					} else {
						others.reset();
						others.disable();
					}
			},

			onblank : function(f) {
				var value = f.getValue()
				var pro = this.form.getForm().findField(f.name + "Pro");
				var desc = this.form.getForm().findField(f.name + "ProDesc");
				if (value && value != "") {
					pro.enable()
				} else {
					pro.reset();
					pro.disable();
					desc.reset();
					desc.disable();
				}
			},

			onwhetherDrink : function(comb) { // 是否饮酒
				var value = comb.getValue();
				var form = this.form.getForm();
				var field = form.findField("stopDrinkingTime");
				if (value == "2") {
					field.enable();
				} else {
					field.reset()
					field.disable();
				}
			},

			onphysicalExerciseFrequency : function(comb) {// 体育锻炼频率
				var value = comb.getValue();
				var form = this.form.getForm();
				var fields = ["everyPhysicalExerciseTime",
						"insistexercisetime", "exerciseStyle"]
				for (var i = 0; i < fields.length; i++) {
					var field = form.findField(fields[i]);
					if (!value || value == "" || value == "4") {
						field.reset()
						field.disable();
					} else {
						field.enable();
					}
				}
			},

			onoccupational : function(comb) {// 职业曝露史
				var value = comb.getValue();
				var form = this.form.getForm();
				var fields = ["jobs", "workTime", "dust", "ray",
						"physicalFactor", "chemicals", "other"]
				for (var i = 0; i < fields.length; i++) {
					var field = form.findField(fields[i]);
					if (value == "2") {
						field.enable();
					} else {
						field.reset()
						field.disable();
					}
					if (i > 1)
						this.onblank(field);
				}
			},

			onwehtherSmoke : function(comb) {// 是否吸烟
				var value = comb.getValue();
				var form = this.form.getForm();
				var fields = ["beginSmokeTime", "stopSmokeTime", "smokes"]
				for (var i = 0; i < fields.length; i++) {
					var field = form.findField(fields[i]);
					if (!value || value == "" || value == "1") {
						field.reset()
						field.disable();
					} else {
						field.enable();
					}
				}

				var stopsmokeTime = form.findField("stopSmokeTime")
				if (value == "2") {
					stopsmokeTime.enable()
				} else {
					stopsmokeTime.reset()
					stopsmokeTime.disable()
				}
			},

			ondrinkingFrequency : function(comb) { // 饮酒频率
				var value = comb.getValue();
				var form = this.form.getForm();
				var fields = ["whetherDrink", "stopDrinkingTime",
						"geginToDrinkTime", "isDrink", "alcoholConsumption",
						"mainDrinkingVvarieties"]
				for (var i = 0; i < fields.length; i++) {
					var field = form.findField(fields[i]);
					if (!value || value == "" || value == "1") {
						field.reset()
						field.disable();
					} else {
						field.enable();
						if (form.findField("whetherDrink").getValue() != "2") { // yub
							form.findField("stopDrinkingTime").disable();
						}
					}
				}
			},

			onLoadData : function(entryName, body) {
				var form = this.form.getForm();
				for (var i = 0; i < this.selectF.length; i++) {
					var field = form.findField(this.selectF[i]);
					if (field)
						eval("this.on" + this.selectF[i] + "(field)")
				}
				var mainDrinkingVvarieties = form
						.findField("mainDrinkingVvarieties")
				if (mainDrinkingVvarieties) {
					this.onmainDrinkingVvarieties(mainDrinkingVvarieties);
				}
				for (var j = 0; j < this.blank.length; j++) {
					var f = form.findField(this.blank[j]);
					if (f) {
						this.onblank(f);
					}
				}
				for (var i = 0; i < this.other.length; i++) {
					var field = form.findField(this.other[i]);
					if (field) {
						this.OnotherDiseasesone(field);
					}
				}
			},

			getSaveRequest : function(saveData) {
				saveData.healthCheck = this.exContext.args.healthCheck;
				return saveData;
			},

			getLoadRequest : function() {
				return {
					"healthCheck" : this.exContext.args.healthCheck
				}
			}
		})