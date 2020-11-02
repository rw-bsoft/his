$package("chis.application.hc.script")
$import("chis.script.BizTableFormView",
		"chis.application.hc.script.HealthCheckTemplate", "util.Accredit",
		"chis.script.util.widgets.MyMessageTip")
$styleSheet("chis.css.healthCheck")

chis.application.hc.script.HealthCheckHtmlForm = function(cfg) {
	var thisPanel = null;
	cfg.idPostfix = cfg.idPostfix || "_hc01";
	chis.application.hc.script.HealthCheckHtmlForm.superclass.constructor
			.apply(this, [cfg]);
	Ext.apply(this, chis.application.hc.script.HealthCheckTemplate);
	//this.createFields = ["checkDate", "manaDoctorId"];
	this.createFields = ["checkDate"];
	this.schemasHealthCheck = "chis.application.hc.schemas.HC_HealthCheck";
	this.schemasLifestySituation = "chis.application.hc.schemas.HC_LifestySituation";
	this.schemasExamination = "chis.application.hc.schemas.HC_Examination";
	this.schemasAccessoryExamination = "chis.application.hc.schemas.HC_AccessoryExamination";
	this.schemasHealthAssessment = "chis.application.hc.schemas.HC_HealthAssessment";
	this.disableFlds = ["symptomOt", "bmi", "everyPhysicalExerciseTime",
			"insistexercisetime", "exerciseStyle", "smokes", "beginSmokeTime",
			"stopSmokeTime", "alcoholConsumption", "whetherDrink_1",
			"whetherDrink_2", "geginToDrinkTime", "isDrink_1", "isDrink_2",
			"mainDrinkingVvarieties_1", "mainDrinkingVvarieties_2",
			"mainDrinkingVvarieties_3", "mainDrinkingVvarieties_4",
			"mainDrinkingVvarieties_9", "stopDrinkingTime", "drinkOther",
			"jobs", "workTime", "dust", "ray", "physicalFactor", "chemicals",
			"other", "dustPro_1", "dustPro_2", "dustProDesc", "rayPro_1",
			"rayPro_2", "rayProDesc", "physicalFactorPro_1",
			"physicalFactorPro_2", "physicalFactorProDesc", "chemicalsPro_1",
			"chemicalsPro_2", "chemicalsProDesc", "otherPro_1", "otherPro_2",
			"otherProDesc", "leftUp", "leftDown", "rightUp", "rightDown",
			"leftUp2", "leftDown2", "rightUp2", "rightDown2",
			"leftUp3", "leftDown3", "rightUp3", "rightDown3",
			"fundusDesc", "skinDesc", "scleraDesc", "lymphnodesDesc",
			"breathSoundDesc", "ralesDesc", "heartMurmurDesc",
			"abdominAltendDesc", "adbominAlmassDesc", "liverBigDesc",
			"splenomegalyDesc", "dullnessDesc", "dreDesc", "breastDesc",
			"vulvaDesc", "vaginalDesc", "cervixDesc", "palaceDesc",
			"attachmentDesc", "ecgText", "xText", "bText", "psText",
			"othercerebrovascularDiseases", "otherkidneyDiseases",
			"otherheartDisease", "otherVascularDisease", "othereyeDiseases",
			"neurologicalDiseasesDesc", "otherDiseasesoneDesc", "abnormality1",
			"abnormality2", "abnormality3", "abnormality4", "targetWeight",
			"vaccine", "pjOther"]
	// enable
	this.otherDisable = [{
				fld : "symptom",
				type : "checkbox",
				control : [{
							key : "25",
							field : ["symptomOt"]
						}]
			}, {
				fld : "physicalExerciseFrequency",
				type : "radio",
				control : [{
					key : "4",
					exp : 'ne',
					field : ["everyPhysicalExerciseTime", "insistexercisetime",
							"exerciseStyle"]
				}]
			}, {
				fld : "wehtherSmoke",
				type : "radio",
				control : [{
							key : "3",
							exp : 'ne',
							field : ["stopSmokeTime"],
							disField : ["smokes", "beginSmokeTime"]
						}]
			}, {
				fld : "wehtherSmoke",
				type : "radio",
				control : [{
							key : "1",
							exp : 'ne',
							field : ["smokes", "beginSmokeTime"],
							disField : ["stopSmokeTime"]
						}]
			}, {
				fld : "drinkingFrequency",
				type : "radio",
				control : [{
					key : "1",
					exp : 'ne',
					field : ["alcoholConsumption", "whetherDrink_1",
							"whetherDrink_2", "geginToDrinkTime", "isDrink_1",
							"isDrink_2", "mainDrinkingVvarieties_1",
							"mainDrinkingVvarieties_2",
							"mainDrinkingVvarieties_3",
							"mainDrinkingVvarieties_4",
							"mainDrinkingVvarieties_9"],
					disField : ["stopDrinkingTime", "drinkOther"]
				}]
			}, {
				fld : "whetherDrink",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["stopDrinkingTime"]
						}]
			}, {
				fld : "mainDrinkingVvarieties",
				type : "checkbox",
				control : [{
							key : "9",
							field : ["drinkOther"]
						}]
			}, {
				fld : "occupational",
				type : "radio",
				control : [{
					key : "2",
					exp : 'eq',
					field : ["jobs", "workTime", "dust", "ray",
							"physicalFactor", "chemicals", "other"],
					disField : ["dustPro_1", "dustPro_2", "dustProDesc",
							"rayPro_1", "rayPro_2", "rayProDesc",
							"physicalFactorPro_1", "physicalFactorPro_2",
							"physicalFactorProDesc", "chemicalsPro_1",
							"chemicalsPro_2", "chemicalsProDesc", "otherPro_1",
							"otherPro_2", "otherProDesc"]
				}]
			}, {
				fld : "dust",
				type : "text",
				control : [{
							key : "notNull",
							field : ["dustPro_1", "dustPro_2"],
							disField : ["dustProDesc"]
						}]
			}, {
				fld : "dustPro",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["dustProDesc"]
						}]
			}, {
				fld : "ray",
				type : "text",
				control : [{
							key : "notNull",
							field : ["rayPro_1", "rayPro_2"],
							disField : ["rayProDesc"]
						}]
			}, {
				fld : "rayPro",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["rayProDesc"]
						}]
			}, {
				fld : "physicalFactor",
				type : "text",
				control : [{
							key : "notNull",
							field : ["physicalFactorPro_1",
									"physicalFactorPro_2"],
							disField : ["physicalFactorProDesc"]
						}]
			}, {
				fld : "physicalFactorPro",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["physicalFactorProDesc"]
						}]
			}, {
				fld : "chemicals",
				type : "text",
				control : [{
							key : "notNull",
							field : ["chemicalsPro_1", "chemicalsPro_2"],
							disField : ["chemicalsProDesc"]
						}]
			}, {
				fld : "chemicalsPro",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["chemicalsProDesc"]
						}]
			}, {
				fld : "other",
				type : "text",
				control : [{
							key : "notNull",
							field : ["otherPro_1", "otherPro_2"],
							disField : ["otherProDesc"]
						}]
			}, {
				fld : "otherPro",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["otherProDesc"]
						}]
			}, {
				fld : "denture",
				type : "checkbox",
				control : [{
							key : "2",
							exp : 'ne',
							field : ["leftUp", "leftDown", "rightUp",
									"rightDown"]
						},{
							key : "3",
							exp : 'ne',
							field : ["leftUp2", "leftDown2", "rightUp2",
									"rightDown2"]
						},{
							key : "4",
							exp : 'ne',
							field : ["leftUp3", "leftDown3", "rightUp3",
									"rightDown3"]
						}]
			},{
				fld : "fundus",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["fundusDesc"]
						}]
			}, {
				fld : "skin",
				type : "radio",
				control : [{
							key : "7",
							exp : 'eq',
							field : ["skinDesc"]
						}]
			}, {
				fld : "sclera",
				type : "radio",
				control : [{
							key : "4",
							exp : 'eq',
							field : ["scleraDesc"]
						}]
			}, {
				fld : "lymphnodes",
				type : "radio",
				control : [{
							key : "4",
							exp : 'eq',
							field : ["lymphnodesDesc"]
						}]
			}, {
				fld : "breathSound",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["breathSoundDesc"]
						}]
			}, {
				fld : "rales",
				type : "radio",
				control : [{
							key : "4",
							exp : 'eq',
							field : ["ralesDesc"]
						}]
			}, {
				fld : "heartMurmur",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["heartMurmurDesc"]
						}]
			}, {
				fld : "abdominAltend",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["abdominAltendDesc"]
						}]
			}, {
				fld : "adbominAlmass",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["adbominAlmassDesc"]
						}]
			}, {
				fld : "liverBig",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["liverBigDesc"]
						}]
			}, {
				fld : "splenomegaly",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["splenomegalyDesc"]
						}]
			}, {
				fld : "dullness",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["dullnessDesc"]
						}]
			}, {
				fld : "dre",
				type : "radio",
				control : [{
							key : "5",
							exp : 'eq',
							field : ["dreDesc"]
						}]
			}, {
				fld : "breast",
				type : "radio",
				control : [{
							key : "7",
							exp : 'eq',
							field : ["breastDesc"]
						}]
			}, {
				fld : "vulva",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["vulvaDesc"]
						}]
			}, {
				fld : "vaginal",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["vaginalDesc"]
						}]
			}, {
				fld : "cervix",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["cervixDesc"]
						}]
			}, {
				fld : "palace",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["palaceDesc"]
						}]
			}, {
				fld : "attachment",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["attachmentDesc"]
						}]
			}, {
				fld : "ecg",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["ecgText"]
						}]
			}, {
				fld : "x",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["xText"]
						}]
			}, {
				fld : "b",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["bText"]
						}]
			}, {
				fld : "ps",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["psText"]
						}]
			}, {
				fld : "cerebrovascularDiseases",
				type : "checkbox",
				control : [{
							key : "6",
							field : ["othercerebrovascularDiseases"]
						}]
			}, {
				fld : "kidneyDiseases",
				type : "checkbox",
				control : [{
							key : "6",
							field : ["otherkidneyDiseases"]
						}]
			}, {
				fld : "heartDisease",
				type : "checkbox",
				control : [{
							key : "7",
							field : ["otherheartDisease"]
						}]
			}, {
				fld : "VascularDisease",
				type : "checkbox",
				control : [{
							key : "4",
							field : ["otherVascularDisease"]
						}]
			}, {
				fld : "eyeDiseases",
				type : "checkbox",
				control : [{
							key : "5",
							field : ["othereyeDiseases"]
						}]
			}, {
				fld : "neurologicalDiseases",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["neurologicalDiseasesDesc"]
						}]
			}, {
				fld : "otherDiseasesone",
				type : "radio",
				control : [{
							key : "2",
							exp : 'eq',
							field : ["otherDiseasesoneDesc"]
						}]
			}, {
				fld : "abnormality",
				type : "radio",
				control : [{
					key : "2",
					exp : 'eq',
					field : ["abnormality1", "abnormality2", "abnormality3",
							"abnormality4"]
				}]
			}, {
				fld : "riskfactorsControl",
				type : "checkbox",
				control : [{
							key : "5",
							field : ["targetWeight"]
						}, {
							key : "6",
							field : ["vaccine"]
						}, {
							key : "7",
							field : ["pjOther"]
						}]
			}, {
				fld : "nonimmuneFlag",
				type : "radio",
				control : [{
							key : "n",
							exp : 'ne',
							field : ["name_1","name_2","name_3"]
						}]
			}, {
				fld : "medicineFlag",
				type : "radio",
				control : [{
							key : "n",
							exp : 'ne',
							field : ["div_medicine_1","div_medicine_2","div_medicine_3","div_medicine_4","div_medicine_5","div_medicine_6"]
						}]
			}, {
				fld : "inhospitalFlag",
				type : "radio",
				control : [{
							key : "n",
							exp : 'ne',
							field : ["inhospitalDate_1_1","inhospitalDate_1_2","outhospitalDate_1_1","outhospitalDate_1_2"]
						}]
			}, {
				fld : "infamilybedFlag",
				type : "radio",
				control : [{
							key : "n",
							exp : 'ne',
							field : ["inhospitalDate_2_1","inhospitalDate_2_2","outhospitalDate_2_1","outhospitalDate_2_2"]
						}]
			}];
	// checkbox mutal exclusion
	this.mutualExclusion = [{
				fld : "symptom",
				key : "01",
				other : ["symptomOt"]
			}, {
				fld : "cerebrovascularDiseases",
				key : "1",
				other : ["othercerebrovascularDiseases"]
			}, {
				fld : "kidneyDiseases",
				key : "1",
				other : ["otherkidneyDiseases"]
			}, {
				fld : "heartDisease",
				key : "1",
				other : ["otherheartDisease"]
			}, {
				fld : "VascularDisease",
				key : "1",
				other : ["otherVascularDisease"]
			}, {
				fld : "eyeDiseases",
				key : "1",
				other : ["othereyeDiseases"]
			}];
}

Ext.extend(chis.application.hc.script.HealthCheckHtmlForm,
		chis.script.BizTableFormView, {
			doCreate : function() {
				var day=new Date();
				if(day.getFullYear()+"-"+(day.getMonth()+1)+"-"+day.getDate()<="2016-5-29"){
					Ext.Msg.alert("友情提示：","服药情况录入可以直接输汉字或拼音查找药品，删除药品选中不服药保存即可，此消息提示3天。");
				}
				this.fireEvent("create", this);
				this.doNew();
			},
			initPanel : function(sc) {
				this.idPostfix = "_" + this.generateMixed(5);
				if (this.form) {
					return this.form;
				}
				this.loadSchemas();
				var cfg = {
					border : false,
					frame : true,
					collapsible : false,
					autoScroll : true,
					width : this.width,
					height : this.height,
					layout : 'fit',
					region : 'north',
					html : this.getHealthCheckHTML()
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				this.form = new Ext.FormPanel(cfg);
				this.form.on("afterrender", this.onReady, this);
				return this.form;
			},
			loadSchemas : function() {
				this.schemas = [];
				// 基本信息 schema
				var reHCS = util.schema.loadSync(this.schemasHealthCheck);
				if (reHCS.code == 200) {
					this.healthCheckSchema = reHCS.schema;
					this.schemas.push(reHCS.schema);
				} else {
					this.processReturnMsg(re.code, re.msg, this.loadSchemas)
					return;
				}
				// 生活方式 schema
				var reLSS = util.schema.loadSync(this.schemasLifestySituation);
				if (reLSS.code == 200) {
					this.lifestySituationSchema = reLSS.schema;
					this.schemas.push(reLSS.schema);
				} else {
					this.processReturnMsg(re.code, re.msg, this.loadSchemas)
					return;
				}
				// 查体 schema
				var reES = util.schema.loadSync(this.schemasExamination);
				if (reES.code == 200) {
					this.examinationSchema = reES.schema;
					this.schemas.push(reES.schema);
				} else {
					this.processReturnMsg(re.code, re.msg, this.loadSchemas)
					return;
				}
				// 辅助检查 schema
				var reAES = util.schema.loadSync(this.schemasAccessoryExamination);
				if (reAES.code == 200) {
					this.accessoryExaminationSchema = reAES.schema;
					this.schemas.push(reAES.schema);
				} else {
					this.processReturnMsg(re.code, re.msg, this.loadSchemas)
					return;
				}
				// 健康评价表 schema
				var reHAS = util.schema.loadSync(this.schemasHealthAssessment);
				if (reHAS.code == 200) {
					this.healthAssessmentSchema = reHAS.schema;
					this.schemas.push(reHAS.schema);
				} else {
					this.processReturnMsg(re.code, re.msg, this.loadSchemas)
					return;
				}
			},
			addFieldAfterRender : function() {
				var curDate = new Date();
				this.checkDate = new Ext.form.DateField({
							name : 'checkDate',
							defaultValue : curDate,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '年检日期',
							allowBlank : false,
							invalidText : "必填字段",
							regex : /(^\S+)/,
							regexText : "前面不能有空格字符",
							renderTo : Ext.get("div_checkDate" + this.idPostfix)
						});
				this.checkDate.setValue(curDate);
				var serverDate = this.mainApp.serverDate;
				this.checkDate.maxValue = Date.parseDate(serverDate, "Y-m-d");
				var cfg = {
					"width" : 270,
					"defaultIndex" : 0,
					"id" : "chis.dictionary.user01",
					"render" : "Tree",
					"selectOnFocus" : true,
					"onlySelectLeaf" : true
				}
				var deptId = this.mainApp.deptId;
				if (deptId.length > 9) {
					cfg.parentKey = deptId;
				}
//				this.manaDoctorId = this.createDicField(cfg);
//				this.manaDoctorId.allowBlank = false;
//				this.manaDoctorId.invalidText = "必填字段";
//				this.manaDoctorId.regex = /(^\S+)/
//				this.manaDoctorId.regexText = "前面不能有空格字符";
//				this.manaDoctorId.fieldLabel = "责任医生";
//				this.manaDoctorId.tree.expandAll();
//				this.manaDoctorId.render(Ext.get("div_manaDoctor"+ this.idPostfix));
//				this.manaDoctorId.setValue({
//							key : this.mainApp.uid,
//							text : this.mainApp.uname
//						});
//				this.manaDoctorId.on("select", this.changeManaUnitId, this);
//				this.manaDoctorId.disable();
				// 住院史 入/出院日期
				this.inhospitalDate_1_1 = new Ext.form.DateField({
							name : 'inhospitalDate_1_1' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '住院史入院日期1',
							renderTo : Ext.get("inhospitalDate_1_1"+ this.idPostfix)
						});
				this.inhospitalDate_1_1.on("change", function() {
							this.dateCtrl(this.outhospitalDate_1_1, 'min',
									this.inhospitalDate_1_1.getValue());
							this.outhospitalDate_1_1.validate();
						}, this);
				this.outhospitalDate_1_1 = new Ext.form.DateField({
							name : 'outhospitalDate_1_1' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '住院史出院日期1',
							renderTo : Ext.get("outhospitalDate_1_1"+ this.idPostfix)
						});
				this.outhospitalDate_1_1.on("change", function() {
							this.dateCtrl(this.inhospitalDate_1_1, 'max',
									this.outhospitalDate_1_1.getValue());
							this.inhospitalDate_1_1.validate();
						}, this);
				this.inhospitalDate_1_2 = new Ext.form.DateField({
							name : 'inhospitalDate_1_2' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '住院史入院日期2',
							renderTo : Ext.get("inhospitalDate_1_2"+ this.idPostfix)
						});
				this.inhospitalDate_1_2.on("change", function() {
							this.dateCtrl(this.outhospitalDate_1_2, 'min',
									this.inhospitalDate_1_2.getValue());
							this.outhospitalDate_1_2.validate();
						}, this);
				this.outhospitalDate_1_2 = new Ext.form.DateField({
							name : 'outhospitalDate_1_2' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '住院史出院日期2',
							renderTo : Ext.get("outhospitalDate_1_2"+ this.idPostfix)
						});
				this.outhospitalDate_1_2.on("change", function() {
							this.dateCtrl(this.inhospitalDate_1_2, 'max',
									this.outhospitalDate_1_2.getValue());
							this.inhospitalDate_1_2.validate();
						}, this);
				// 家庭病床史 建/撤床日期
				this.inhospitalDate_2_1 = new Ext.form.DateField({
							name : 'inhospitalDate_2_1' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '建床日期1',
							renderTo : Ext.get("inhospitalDate_2_1"+ this.idPostfix)
						});
				this.inhospitalDate_2_1.on("change", function() {
							this.dateCtrl(this.outhospitalDate_2_1, 'min',
									this.inhospitalDate_2_1.getValue());
							this.outhospitalDate_2_1.validate();
						}, this);
				this.outhospitalDate_2_1 = new Ext.form.DateField({
							name : 'outhospitalDate_2_1' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '撤床日期1',
							renderTo : Ext.get("outhospitalDate_2_1"+ this.idPostfix)
						});
				this.outhospitalDate_2_1.on("change", function() {
							this.dateCtrl(this.inhospitalDate_2_1, 'max',
									this.outhospitalDate_2_1.getValue());
							this.inhospitalDate_2_1.validate();
						}, this);
				this.inhospitalDate_2_2 = new Ext.form.DateField({
							name : 'inhospitalDate_2_2' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '建床日期2',
							renderTo : Ext.get("inhospitalDate_2_2"+ this.idPostfix)
						});
				this.inhospitalDate_2_2.on("change", function() {
							this.dateCtrl(this.outhospitalDate_2_2, 'min',
									this.inhospitalDate_2_2.getValue());
							this.outhospitalDate_2_2.validate();
						}, this);
				this.outhospitalDate_2_2 = new Ext.form.DateField({
							name : 'outhospitalDate_2_2' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '撤床日期2',
							renderTo : Ext.get("outhospitalDate_2_2"+ this.idPostfix)
						});
				this.outhospitalDate_2_2.on("change", function() {
							this.dateCtrl(this.inhospitalDate_2_2, 'max',
									this.outhospitalDate_2_2.getValue());
							this.inhospitalDate_2_2.validate();
						}, this);
				// 接种日期 inoculationDate
				this.inoculationDate_1 = new Ext.form.DateField({
							name : 'inoculationDate_1' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '接种日期1',
							renderTo : Ext.get("inoculationDate_1"+ this.idPostfix)
						});
				this.inoculationDate_2 = new Ext.form.DateField({
							name : 'inoculationDate_2' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '接种日期2',
							renderTo : Ext.get("inoculationDate_2"+ this.idPostfix)
						});
				this.inoculationDate_3 = new Ext.form.DateField({
							name : 'inoculationDate_3' + this.idPostfix,
							width : 120,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '接种日期3',
							renderTo : Ext.get("inoculationDate_3"+ this.idPostfix)
						});
				// 药品名称
				var me = this;
				for (var i = 1; i < 7; i++) {
					var fldId = "medicine_" + i;
					this[fldId] = this.createLocalDicField({
								width : 120,
								id : fldId + this.idPostfix,
								name : fldId,
								afterSelect : function(t, record) {
									var id = t.container.id;
									var last_idx = id.lastIndexOf('_');
									var idx = id.substring(last_idx - 1,last_idx);
									var mdFld = document
											.getElementById("eachDose_" + idx+ me.idPostfix);
									if (mdFld) {
										mdFld.value = record.data.YPJL+ record.data.JLDW;
										var useFld = document.getElementById("use_"+idx+me.idPostfix);
										var eachDoseFld = document.getElementById("eachDose_"+idx+me.idPostfix);
										var useDateFld = document.getElementById("useDate_"+idx+me.idPostfix);
										me.addClass(useFld, "x-form-invalid");
										if(eachDoseFld.value==''){
											me.addClass(eachDoseFld, "x-form-invalid");
										}
										me.addClass(useDateFld, "x-form-invalid");
										var fyycxDiv = document.getElementById("medicineYield"+idx+me.idPostfix);
										if(fyycxDiv){
											me.addClass(fyycxDiv, "x-form-invalid");
										}
									}
								},
								afterClear : function(t) {
									var id = t.container.id;
									var last_idx = id.lastIndexOf('_');
									var idx = id.substring(last_idx - 1,
											last_idx);
									var mdFld = document
											.getElementById("eachDose_"+ idx + me.idPostfix);
									if (mdFld) {
										mdFld.value = "";
										var useFld = document.getElementById("use_"+idx+me.idPostfix);
										var eachDoseFld = document.getElementById("eachDose_"+idx+me.idPostfix);
										var useDateFld = document.getElementById("useDate_"+idx+me.idPostfix);
										me.removeClass(useFld, "x-form-invalid");
										me.removeClass(eachDoseFld, "x-form-invalid");
										me.removeClass(useDateFld, "x-form-invalid");
										var fyycxDiv = document.getElementById("medicineYield"+idx+me.idPostfix);
										if(fyycxDiv){
											me.removeClass(fyycxDiv, "x-form-invalid");
										}
									}
								}
							});
					this[fldId].render(Ext.get("div_" + fldId + this.idPostfix));
					thisPanel = this;
				}
			},
			onReady : function() {
				this.addFieldAfterRender();
				this.addFieldDataValidateFun(this.healthCheckSchema);
				this.addFieldDataValidateFun(this.lifestySituationSchema);
				this.addFieldDataValidateFun(this.examinationSchema);
				this.addFieldDataValidateFun(this.accessoryExaminationSchema);
				this.addFieldDataValidateFun(this.healthAssessmentSchema);
				this.controlOtherFld();
				this.mutualExclusionSet();
				this.setBMI();
				this.setRecognizeRadioClick();
				this.addMedicineValidate();
				chis.application.hc.script.HealthCheckHtmlForm.superclass.onReady.call(this);
			},
			addMedicineValidate : function(){
				for (var i = 1; i < 7; i++) {
					var n = this.medicine.length;
					for (var j = 0; j < n; j++) {
						var fn = this.medicine[j];
						if (fn.type == "div") {
							continue;
						}else if(fn.type=="hidden"){
							continue;
						}else if(fn.type=="text"){
							var fld = document.getElementById(fn.id + "_" + i+ this.idPostfix);
							if (fld) {
								var maxLength = 50;
								if(fn.id != 'eachDose'){
									maxLength = 150;
								}
							}
							var notNull = true;
							var me = this;
							var handleFun = function(maxLength, notNull,alias, obj, me) {
								return function() {
									me.validateString(maxLength, notNull,alias, obj, me);
								}
							}
							this.addEvent(fld, "change", handleFun(maxLength, notNull, fn.alias,fld, me));
						}else if(fn.type=="radio"){
							var handleFun = function(fld,alias,idx, me) {
								return function() {
									if(fld.checked){
										var fyycxDiv = document.getElementById("medicineYield"+idx+me.idPostfix);
										if(fyycxDiv){
											me.removeClass(fyycxDiv, "x-form-invalid");
										}
									}
								}
							}
							var myoes = document.getElementsByName(fn.id + i);
							for (var k = 0, klen = myoes.length; k < klen; k++) {
								var myo = myoes[k];
								this.addEvent(myo, "click", handleFun(myo,fn.alias,i, me));
							}
						}
					}
				}
			},
			changeManaUnitId : function(combo, node) {
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
						});
				this.setManaUnit(result.json.manageUnit);
			},
			setManaUnit : function(manageUnit) {
				var manaUnitId = document.getElementById(manaUnitId
						+ this.idPostfix);
				if (manaUnitId) {
					manaUnitId.value = manageUnit.key;
				}
			},
			doNew : function() {

				this.op = "create"
				this.initDataId = null;
				if (this.data) {
					this.data = {}
				}
				document.getElementById("healthCheck" + this.idPostfix).value = "";
				document.getElementById("empiId" + this.idPostfix).value = this.exContext.ids.empiId;
				document.getElementById("phrId" + this.idPostfix).value = this.exContext.ids.phrId;
				document.getElementById("manaUnitId" + this.idPostfix).value = this.mainApp.deptId;
				
				for (var s = 0, sLen = this.schemas.length; s < sLen; s++) {
					var items = this.schemas[s].items;
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.isCreateField(it.id)) {
//							if (it.id == "manaDoctorId") {
//								this.manaDoctorId.setValue({
//											key : this.mainApp.uid,
//											text : this.mainApp.uname
//										});
//							} else 
							if (it.id == "checkDate") {
								var curDate = new Date();
								this.checkDate.setValue(curDate);
							} else {
								eval("this." + it.id + ".setValue()");
							}
						} else {
							if (it.dic) {
//								var notFlds = ["sexCode", "regionCode","manaDoctorId", "manaUnitId"];
								var notFlds = ["sexCode", "regionCode","manaUnitId"];
								var hasEle = this.isHaveElementInArray(notFlds,
										it.id);
								if (!hasEle) {
									if (it.id == "recognize") {
										for (var di = 1; di < 10; di++) {
											var fs = document.getElementsByName(it.id+ di);
											for (var j = 0, len = fs.length; j < len; j++) {
												var f = fs[j];
												if (f.checked) {
													f.checked = false;
												}
											}
										}
									} else {
										var fs = document.getElementsByName(it.id);
										for (var j = 0, len = fs.length; j < len; j++) {
											var f = fs[j];
											if (f.type == "checkbox"
													|| f.type == "radio") {
												if (f.checked) {
													f.checked = false;
												}
											}
										}
									}
								}
							} else {
								var f = document.getElementById(it.id+ this.idPostfix)
								if (f) {
									f.value = '';
								}
							}
						}
					}
				}
				// reset hospital list
				for (var i = 1; i < 3; i++) {
					for (var j = 1; j < 3; j++) {
						var len = this.inhospitalList.length;
						for (var n = 0; n < len; n++) {
							var fn = this.inhospitalList[n];
							if (fn.type == "div") {
								eval("this." + fn.id + "_" + i + "_" + j+ ".setValue()");
							} else {
								var f = document.getElementById(fn.id + "_" + i+ "_" + j + this.idPostfix);
								if (f) {
									f.value = '';
								}
							}
						}
					}
				}
				// reset medicine list
				for (var i = 1; i < 7; i++) {
					var n = this.medicine.length;
					for (var j = 0; j < n; j++) {
						var fn = this.medicine[j];
						if (fn.type == "div") {
							if (fn.id == "medicine") {
								this[fn.id + "_" + i].selectData.YPMC = "";
								this[fn.id + "_" + i].setValue("");
							} else {
								eval("this." + fn.id + "_" + i + ".setValue()");
							}
						} else if (fn.type == "radio" || fn.type == "checkbox") {
							var fs = document.getElementsByName(fn.id + i);
							for (var k = 0, len = fs.length; k < len; k++) {
								var f = fs[k];
								if (f.checked) {
									f.checked = false;
								}
							}
						} else {
							var f = document.getElementById(fn.id + "_" + i+ this.idPostfix);
							if (f) {
								f.value = '';
							}
						}
					}
				}
				// rest Nonimmune Inoculation list
				for (var i = 1; i < 4; i++) {
					var r = {};
					var len = this.nonimmuneInoculation.length;
					for (var j = 0; j < len; j++) {
						var fn = this.nonimmuneInoculation[j];
						if (fn.type == "div") {
							eval("this." + fn.id + "_" + i + ".setValue()");
						} else {
							var f = document.getElementById(fn.id + "_" + i+ this.idPostfix);
							if (f) {
								f.value = '';
							}
						}
					}
				}
				this.fieldValidate(this.healthCheckSchema);
				// this.fieldValidate(this.lifestySituationSchema);
				// this.fieldValidate(this.examinationSchema);
				// this.fieldValidate(this.accessoryExaminationSchema);
				// this.fieldValidate(this.healthAssessmentSchema);
				if(this.exContext.empiData.sexCode=="1"){
					this.mannocheck();
				}
				
			},
			loadData : function() {
				this.doNew();
				this.initFieldDisable();
				this.toLoadData();
			},
			changexycolor: function() {
				var yc_ssy=document.getElementById("constriction"+this.idPostfix);
				var yc_szy=document.getElementById("diastolic"+this.idPostfix);
				var zc_ssy=document.getElementById("constriction_L"+this.idPostfix);
				var zc_szy=document.getElementById("diastolic_L"+this.idPostfix);
				if(!yc_ssy.value && zc_ssy.value && zc_ssy.value >50){
					this.removeClass(yc_ssy,"x-form-invalid");
				}
				if(!yc_szy.value && zc_szy.value && zc_szy.value >50){
					this.removeClass(yc_szy,"x-form-invalid");
				}
				if(!zc_ssy.value && yc_ssy.value && yc_ssy.value >50){
					this.removeClass(zc_ssy,"x-form-invalid");
				}
				if(!zc_szy.value && yc_szy.value && yc_szy.value >50){
					this.removeClass(zc_szy,"x-form-invalid");
				}
			},
			toLoadData : function() {
				var healthCheck = this.exContext.args.healthCheck;
				if (!healthCheck || this.exContext.args.op == "create") {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : this.loadServiceId,
							method : "execute",
							serviceAction : this.loadAction || "",
							schema : this.entryName,
							body : {
								healthCheck : healthCheck
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								this.fireEvent("exception", code, msg,
										healthCheck); // ** 用于一些异常处理
								return
							}
							var body = json.body;
							if (body) {
								var hcData = body.hcData;
								var lsData = body.lsData;
								var exaData = body.exaData;
								var aeData = body.aeData;
								var haData = body.haData;
								var ihList = body.ihList;
								var hhList = body.hhList;
								var msList = body.msList;
								var niList = body.niList;
								if (hcData) {
									this.initFormData(hcData,this.healthCheckSchema);
									this.initDataId = hcData.healthCheck;
									this.fieldValidate(this.healthCheckSchema);
								}
								if (lsData) {
									this.initFormData(lsData,this.lifestySituationSchema);
									this.fieldValidate(this.lifestySituationSchema);
								}
								if (exaData) {
									this.initFormData(exaData,this.examinationSchema);
									this.fieldValidate(this.examinationSchema);
								}
								if (aeData) {
									this.initFormData(aeData,this.accessoryExaminationSchema);
									this.fieldValidate(this.accessoryExaminationSchema);
								}
								if (haData) {
									this.initFormData(haData,this.healthAssessmentSchema);
									this.fieldValidate(this.healthAssessmentSchema);
								}
								if (ihList && ihList.length > 0) {
									this.setHospitalListData(ihList, 1);
								}
								if (hhList && hhList.length > 0) {
									this.setHospitalListData(hhList, 2);
								}
								if (msList && msList.length > 0) {
									this.setMedicineListData(msList);
								}
								if (niList && niList.length > 0) {
									this.setInoculationListData(niList);
								}
								this.setEnable();
								if(this.exContext.empiData.sexCode=="1"){
								 this.mannocheck();
								}
							}
							this.changexycolor();
						}, this)
			},
			doSave : function() {
				var btns = this.form.getTopToolbar().items;
				var saveBtn = btns.item[0];
				if (saveBtn) {
					saveBtn.disable();
				}
				if (!this.htmlFormSaveValidate(this.healthCheckSchema)) {
					return;
				}
				if (!this.htmlFormSaveValidate(this.lifestySituationSchema)) {
					return;
				}
				if (!this.htmlFormSaveValidate(this.examinationSchema)) {
					return;
				}
				if (!this.htmlFormSaveValidate(this.accessoryExaminationSchema)) {
					return;
				}
				if (!this.htmlFormSaveValidate(this.healthAssessmentSchema)) {
					return;
				}
				var errFlds = Ext.query(".info_tablesHc .x-form-invalid");
				var eflen = errFlds.length;
				if (eflen > 0) {
					var isReturn = false;
					var errormsg="";
					for (var i = 0; i < eflen; i++) {
						var fid = errFlds[i].id
						var fn = fid.substring(0, fid.indexOf("_"));
						if (fn != "healthCheck" && fn != "manaUnitId") {
							isReturn = true;
							errormsg=fn;
							if (document.getElementById(fid)) {
								document.getElementById(fid).focus();
								document.getElementById(fid).select();
							} else {
								var eflds = document.getElementsByName(fn);
								if (eflds.length > 0) {
									eflds[0].focus();
									eflds[0].select();
								}
							}
							break;
						}
					}
					if (isReturn) {
						MyMessageTip.msg("提示", errormsg+"值验证未通过，请修改录入值", true);
						return;
					}
				}
				var values = {};
				values.hcData = this.getFormData(this.healthCheckSchema);
				/****************begin add by zhaojian 2018-03-01 增加血压数据异常提醒**************/
				if((values.hcData.constriction!="" && values.hcData.diastolic!="" &&
					((values.hcData.constriction>180 && values.hcData.diastolic>110)||(values.hcData.constriction<90 && values.hcData.diastolic>60)))||
				(values.hcData.constriction_L!="" && values.hcData.diastolic_L!="" &&
					((values.hcData.constriction_L>180 && values.hcData.diastolic_L>110)||(values.hcData.constriction_L<90 && values.hcData.diastolic_L>60)))){
					Ext.Msg.alert("提醒：","血压数据异常：收缩压高于180舒张压高于110，或者收缩压低于90舒张压低于60。")
				}
				/****************end*************/
				var year=this.mainApp.serverDate.substring(0,4);
				var oldyear=parseInt(year)-60
				var olddate=oldyear+"-12-31";
				if(this.exContext.empiData.birthday>olddate && values.hcData.checkWay.indexOf("2") >-1 ){
					Ext.Msg.alert("提醒：","此居民不是老年人，不能勾选老年人体检类型！")
					document.getElementById("checkWay_2"+this.idPostfix).checked=false;
					return;
				}
				var res = util.rmi.miniJsonRequestSync({
						serviceId : this.saveServiceId,
						serviceAction : "getmdcflag",
						method : "execute",
						empiId : this.exContext.ids.empiId
					})
					if (res.code == 200) {
						if(res.json.gxyflag=="n" && values.hcData.checkWay.indexOf("3") >-1 ){
							Ext.Msg.alert("提醒：","此居民未建高血压档案，不能勾高血压体检类型！")
							document.getElementById("checkWay_3"+this.idPostfix).checked=false;
							return;
						}
						if(res.json.tnbflag=="n" && values.hcData.checkWay.indexOf("4") >-1 ){
							Ext.Msg.alert("提醒：","此居民未建糖尿病档案，不能勾糖尿病体检类型！")
							document.getElementById("checkWay_4"+this.idPostfix).checked=false;
							return;
						}
					}
				//我操，原来想用this.op的，结果发现this.op是个坑。
				if(this.initDataId){
				if(values.hcData.checkDate>Date.parseDate(this.exContext.args.data.createDate, "Y-m-d H:i:s")){
					Ext.Msg.alert("提醒：","年检日期不能大于原始录入日期哦！")
					return;
				}
				}
				//2019-04-01 Wangjl 解决健康检查表保存时默认“新建档”的问题
//				if(values.hcData.checkWay){
//				
//				}else{
//					if (this.exContext.args.dataSources) {
//						debugger
//						values.hcData.checkWay = this.exContext.args.dataSources;
//					} else {
//						values.hcData.checkWay = "";
//				  }
//				}
				//数据值校验
				if(values.hcData.symptom.length<1){
					MyMessageTip.msg("提示","请选择症状！其他该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.cerebrovascularDiseases.length<1){
					MyMessageTip.msg("提示","请选择脑血管疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.kidneyDiseases.length<1){
					MyMessageTip.msg("提示","请选择肾脏疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.heartDisease.length<1){
					MyMessageTip.msg("提示","请选择心脏疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.VascularDisease.length<1){
					MyMessageTip.msg("提示","请选择血管疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.eyeDiseases.length<1){
					MyMessageTip.msg("提示","请选择眼部疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.neurologicalDiseases.length<1){
					MyMessageTip.msg("提示","请选择神经系统疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.otherDiseasesone.length<1){
					MyMessageTip.msg("提示","请选择其他系统疾病！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.inhospitalFlag.length<1){
					MyMessageTip.msg("提示","请选择住院情况！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.infamilybedFlag.length<1){
					MyMessageTip.msg("提示","请选择家庭病床情况！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.medicineFlag.length<1){
					MyMessageTip.msg("提示","请选择服药情况！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.hcData.nonimmuneFlag.length<1){
					MyMessageTip.msg("提示","请选择接种情况！其他红色字体该填的也要填上哦！", true);
					return;
				}
				values.lsData = this.getFormData(this.lifestySituationSchema);
				
				if(values.lsData.physicalExerciseFrequency<1){
					MyMessageTip.msg("提示","请选择体育锻炼频率！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.lsData.dietaryHabit<1){
					MyMessageTip.msg("提示","请选择饮食习惯！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.lsData.wehtherSmoke<1){
					MyMessageTip.msg("提示","请选择吸烟状况！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.lsData.drinkingFrequency<1){
					MyMessageTip.msg("提示","请选择饮酒频率！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.lsData.occupational<1){
					MyMessageTip.msg("提示","请选择职业病危害！其他红色字体该填的也要填上哦！", true);
					return;
				}
				//查体
				values.exaData = this.getFormData(this.examinationSchema);
				if(values.exaData.edema<1){
					MyMessageTip.msg("提示","请选择下肢水肿！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.abdominAltend<1){
					MyMessageTip.msg("提示","请选择腹部压痛！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.adbominAlmass<1){
					MyMessageTip.msg("提示","请选择腹部包块！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.liverBig<1){
					MyMessageTip.msg("提示","请选择腹部肝大！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.splenomegaly<1){
					MyMessageTip.msg("提示","请选择腹部脾大！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.dullness<1){
					MyMessageTip.msg("提示","腹部移动性浊音！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.barrelChest<1){
					MyMessageTip.msg("提示","请选择肺-桶状胸！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.breathSound<1){
					MyMessageTip.msg("提示","请选择肺-呼吸音！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.rales<1){
					MyMessageTip.msg("提示","请选择肺-罗音！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.lymphnodes<1){
					MyMessageTip.msg("提示","请选择淋巴结！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.sclera<1){
					MyMessageTip.msg("提示","请选择巩膜！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.skin<1){
					MyMessageTip.msg("提示","请选择皮肤！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.heartRate<1){
					MyMessageTip.msg("提示","请选择心率！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.rhythm<1){
					MyMessageTip.msg("提示","请选择心律！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.exaData.heartMurmur<1){
					MyMessageTip.msg("提示","请选择心脏杂音！其他红色字体该填的也要填上哦！", true);
					return;
				}
				values.aeData = this.getFormData(this.accessoryExaminationSchema);		
				if(values.aeData.motion<1){
					MyMessageTip.msg("提示","请选择运动功能！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.aeData.hearing<1){
					MyMessageTip.msg("提示","请选择听力！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if((values.aeData.leftEye<0)&&(values.aeData.recLeftEye<0)){
					MyMessageTip.msg("提示","请填写左眼视力/左眼矫正视力！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if((values.aeData.rightEye<0)&&(values.aeData.recRightEye<0)){
					MyMessageTip.msg("提示","请选择右眼视力/右眼矫正视力！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.aeData.pharyngeal<1){
					MyMessageTip.msg("提示","请选择咽部！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.aeData.denture<1){
					MyMessageTip.msg("提示","请选择齿列！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.aeData.lip<1){
					MyMessageTip.msg("提示","请选择口唇！其他红色字体该填的也要填上哦！", true);
					return;
				}
				/****************begin add by zhaojian 2018-03-01 增加空腹血糖数据异常提醒**************/
				if((values.hcData.fbs!="" && (values.hcData.fbs>16.7||values.hcData.fbs<3.9))||(values.aeData.fbs2!="" && (values.aeData.fbs2>300.6||values.aeData.fbs2<70.2))){
					Ext.Msg.alert("提醒：","空腹血糖数据异常：血糖高于16.7mmol/L或者低于3.9mmol/L或者血糖高于300.6mg/dL或者低于70.2mg/dL。")
				}
				/****************end*************/
				values.haData = this.getFormData(this.healthAssessmentSchema);
				if(values.haData.abnormality<1){
					MyMessageTip.msg("提示","请选择健康评价！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.haData.mana<1){
					MyMessageTip.msg("提示","请选择健康指导！其他红色字体该填的也要填上哦！", true);
					return;
				}
				if(values.haData.riskfactorsControl<1){
					MyMessageTip.msg("提示","请选择危险因素控制！其他红色字体该填的也要填上哦！", true);
					return;
				}
				values.inhospitalListData = this.getInhospitalListData();
				values.medicineListData = this.getMedicineData();
				values.niListData = this.getNIData();
				this.saveToServer(values);
			},
			getSaveRequest : function(saveData) {
				saveData.hcData.empiId = this.exContext.ids.empiId;
				saveData.hcData.phrId = this.exContext.ids.phrId;
				return saveData;
			},
			saveToServer : function(saveData) {
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
							var btns = this.form.getTopToolbar().items;
							var saveBtn = btns.item[0];
							if (saveBtn) {
								saveBtn.enable();
							}
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								this.fireEvent("exception", code, msg,saveData); // **进行异常处理
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body,this.healthCheckSchema);
							}
							this.fireEvent("save", this.entryName, this.op,json, this.data);
							this.afterSaveData(this.entryName, this.op, json,this.data);
							this.op = "update"
							this.exContext.args.op = "create";
						}, this)// jsonRequest
			},
			// 取表单数据
			getFormData : function(schema) {
				if (!schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = schema.items;
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (this.op == "create" && !ac.canCreate(it.acValue)) {
						continue;
					}
					// 从内存中取
					var v = this.data[it.id];
					if (v == undefined) {
						v = it.defaultValue;
					}
					if (v != null && typeof v == "object") {
						v = v.key;
					}

					// 从页面上取
					if (this.isCreateField(it.id)) {
						var cfv = eval("this." + it.id + ".getValue()");
						values[it.id] = cfv;
					} else {
						if (it.dic) {
//							var notFlds = ["sexCode", "regionCode","manaDoctorId", "manaUnitId"];
							var notFlds = ["sexCode", "regionCode", "manaUnitId"];
							var hasEle = this.isHaveElementInArray(notFlds,it.id);
							if (!hasEle) {
								if (it.id == "recognize") {
									var rv = []
									for (var di = 1; di < 10; di++) {
										var fs = document
												.getElementsByName(it.id + di);
										for (var j = 0, len = fs.length; j < len; j++) {
											var f = fs[j];
											if (f.checked) {
												rv.push(f.value);
											}
										}
									}
									v = rv.join(',');
									values[it.id] = v;
								} else {
									var fs = document.getElementsByName(it.id);
									var vs = [];
									for (var j = 0, len = fs.length; j < len; j++) {
										var f = fs[j];
										if (f.type == "checkbox"
												|| f.type == "radio") {
											if (f.checked) {
												vs.push(f.value);
											}
										}
									}
									v = vs.join(',')
									values[it.id] = v;
								}
							} else {
								if (it.id == "manaUnitId") {
									var manaUnitId = document.getElementById("manaUnitId"+ this.idPostfix);
									if (manaUnitId) {
										v = manaUnitId.value;
										values[it.id] = v;
									}
								}
							}
						} else {
							var f = document.getElementById(it.id+ this.idPostfix)
							if (f) {
								v = f.value || v || '';
								values[it.id] = v;
							}
						}
					}
				}
				return values;
			},
			// 取住院数据
			getInhospitalListData : function() {
				var inhospitalListData = [];
				for (var i = 1; i < 3; i++) {
					for (var j = 1; j < 3; j++) {
						var len = this.inhospitalList.length;
						var r = {};
						r.type = i // 住院类型[1 住院史 2 家庭病床史]
						for (var n = 0; n < len; n++) {
							var fn = this.inhospitalList[n];
							if (fn.type == "div") {
								var dv = eval("this." + fn.id + "_" + i + "_"+ j + ".getValue()");
								if (typeof(dv) == "undefined") {
									dv = "";
								}
								r[fn.id] = dv;
							} else {
								var f = document.getElementById(fn.id + "_" + i+ "_" + j + this.idPostfix);
								if (f) {
									r[fn.id] = f.value || '';
								}
							}
						}
						inhospitalListData.push(r);
					}
				}
				return inhospitalListData;
			},
			// 取用药情况数据
			getMedicineData : function() {
				var medicineListData = [];
				for (var i = 1; i < 7; i++) {
					var n = this.medicine.length;
					var r = {};
					for (var j = 0; j < n; j++) {
						var fn = this.medicine[j];
						if (fn.type == "radio") {
							var myoes = document.getElementsByName(fn.id + i);
							var myv = "";
							for (var k = 0, klen = myoes.length; k < klen; k++) {
								var myo = myoes[k];
								if (myo.checked) {
									myv = myo.value;
								}
							}
							if (typeof(myv) == "undefined") {
								myv = "";
							}
							r[fn.id] = myv;
						} else if (fn.type == "div") {
							r[fn.id] = this[fn.id + "_" + i].getValue();
						} else {
							var f = document.getElementById(fn.id + "_" + i+ this.idPostfix);
							if (f) {
								r[fn.id] = f.value || '';
							}
						}
					}
					medicineListData.push(r);
				}
				return medicineListData;
			},
			// 取 非免疫规划预防接种 数据
			getNIData : function() {
				var niData = [];
				for (var i = 1; i < 4; i++) {
					var r = {};
					var len = this.nonimmuneInoculation.length;
					for (var j = 0; j < len; j++) {
						var fn = this.nonimmuneInoculation[j];
						if (fn.type == "div") {
							var iDate = eval("this." + fn.id + "_" + i+ ".getValue()");
							if (typeof(iDate) == "undefined") {
								iDate = "";
							}
							r[fn.id] = iDate;
						} else {
							var f = document.getElementById(fn.id + "_" + i+ this.idPostfix);
							if (f) {
								r[fn.id] = f.value || '';
							}
						}
					}
					niData.push(r);
				}
				return niData;
			},
			isCreateField : function(fldId) {
				var isCF = false;
				var len = this.createFields.length;
				for (var i = 0; i < len; i++) {
					var cf = this.createFields[i];
					if (cf == fldId) {
						isCF = true;
						break;
					}
				}
				return isCF;
			},
			initFormData : function(data, schema) {
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				// Ext.apply(this.data, data)
				if (schema.id == "chis.application.hc.schemas.HC_HealthAssessment") {
					var items = schema.items
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.isCreateField(it.id)) {
							var cfv = data[it.id]
							eval("this." + it.id + ".setValue(cfv)");
						} else {
							if (it.dic) {
								var dicFV = data[it.id];
								var fv = "";
								if (dicFV) {
									fv = dicFV.key;
								}
								var dvs = fv.split(",");
								if (it.id == "recognize") {
									for (var j = 0, len = dvs.length; j < len; j++) {
										var rid = it.id
												+ dvs[j].substring(0, 1) + "_"
												+ dvs[j] + this.idPostfix
										var f = document.getElementById(rid);
										if (f) {
											f.checked = true;
											eval("this.checked" + rid + "=true");
										} else {
											eval("this.checked" + rid+ "=false");
										}
									}
								} else {
									for (var j = 0, len = dvs.length; j < len; j++) {
										var f = document.getElementById(it.id + "_"+ dvs[j]+ this.idPostfix);
										if (f) {
											f.checked = true;
										}
									}
								}
							} else {
								var f = document.getElementById(it.id+ this.idPostfix)
								if (f) {
									f.value = data[it.id];
									if (it['not-null'] == "1"|| it['not-null'] == "true") {
										if (data[it.id] && data[it.id] != "") {
											this.removeClass(f,"x-form-invalid");
										}
									}
								}
							}
						}
					}
				} else {
					var items = schema.items
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.isCreateField(it.id)) {
							var cfv = data[it.id]
							eval("this." + it.id + ".setValue(cfv)");
						} else {
							if (it.dic) {
								if (it.id == "manaUnitId") {
									var manaUnitId = document.getElementById("manaUnitId"+ this.idPostfix);
									if (manaUnitId) {
										var dicFV = data[it.id];
										if (dicFV) {
											manaUnitId.value = dicFV.key
										}
									}
								} else {
									var dicFV = data[it.id];
									var fv = "";
									if (dicFV) {
										fv = dicFV.key;
									}
									var dvs = fv.split(",");
									for (var j = 0, len = dvs.length; j < len; j++) {
										var f = document.getElementById(it.id + "_"+ dvs[j]+ this.idPostfix);
										if (f) {
											f.checked = true;
										}
									}
								}
							} else {
								var f = document.getElementById(it.id
										+ this.idPostfix)
								if (f) {
									f.value = data[it.id];
									if (it['not-null'] == "1"
											|| it['not-null'] == "true") {
										if (data[it.id] && data[it.id] != "") {
											this.removeClass(f,"x-form-invalid");
										}
									}
								}
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				// this.startValues = form.getValues(true);
				//this.resetButtons(); // ** 用于页面按钮权限控制
				// this.focusFieldAfter(-1, 800);
			},
			setKeyReadOnly : function(status) {
				if (this.healthCheckSchema.keyGenerator == "auto") {
					status = true
				}
				if (this.healthCheckSchema.pkey) {
					var pkey = this.form.getForm().findField(this.healthCheckSchema.pkey)
					if (pkey) {
						pkey.setDisabled(status)
					}
				}
			},
			setHospitalListData : function(listData, type) {
				// 向 住院史列表中set数据 @listData住院记录数据 @type住院类型[1 住院史 2 家庭病床史]
				for (var i = 0, iLen = listData.length; i < iLen; i++) {
					var len = this.inhospitalList.length;
					for (var n = 0; n < len; n++) {
						var fn = this.inhospitalList[n];
						var fv = listData[i][fn.id];
						if (fn.type == "div") {
							eval("this." + fn.id + "_" + type + "_" + (i + 1)+ ".setValue(fv)");
						} else {
							var f = document.getElementById(fn.id + "_" + type+ "_" + (i + 1) + this.idPostfix);
							if (f) {
								f.value = fv || '';
							}
						}
					}
				}
			},
			setMedicineListData : function(msList) {
				// 向 用药 列表中 set数据
				for (var i = 0, iLen = msList.length; i < iLen; i++) {
					var n = this.medicine.length;
					for (var j = 0; j < n; j++) {
						var fn = this.medicine[j];
						var fv = msList[i][fn.id];
						if (fn.type == "radio") {
							var myoes = document.getElementsByName(fn.id+ (i + 1));
							for (var k = 0, klen = myoes.length; k < klen; k++) {
								var myo = myoes[k];
								if (myo.value == fv) {
									myo.checked = true;
								}
							}
						} else if (fn.type == "div") {
							if (fn.id == "medicine") {
								this[fn.id + "_" + (i + 1)].selectData.YPMC = fv;
								this[fn.id + "_" + (i + 1)].setValue(fv);
							} else {
								eval("this." + fn.id + "_" + i+ ".setValue(fv)");
							}
						} else {
							var f = document.getElementById(fn.id + "_"+ (i + 1) + this.idPostfix);
							if (f) {
								f.value = fv || '';
							}
						}
					}
				}
			},
			setInoculationListData : function(niList) {
				// 向 非免疫规划预防接种 列表中set数据
				for (var i = 0, iLen = niList.length; i < iLen; i++) {
					var len = this.nonimmuneInoculation.length;
					for (var j = 0; j < len; j++) {
						var fn = this.nonimmuneInoculation[j];
						var fv = niList[i][fn.id];
						if (fn.type == "div") {
							eval("this." + fn.id + "_" + (i + 1)+ ".setValue(fv)");
						} else {
							var f = document.getElementById(fn.id + "_"+ (i + 1) + this.idPostfix);
							if (f) {
								f.value = fv || '';
							}
						}
					}
				}
			},
			addEvent : function(o, c, h) {
				if (o.attachEvent) {
					o.attachEvent('on' + c, h);
				} else {
					o.addEventListener(c, h, false);
				}
				return true;
			},
			fieldValidate : function(schema) {
				var items = schema.items
				var n = items.length
				var bpvr = true;
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (!this.isCreateField(it.id)) {
						var fld = document.getElementById(it.id+ this.idPostfix);
						if (!fld) {
							continue;
						}
						if (it['not-null'] == "1" || it['not-null'] == "true") {
							if (fld.value == "") {
								var fid = it.id;
								if (fid == "constriction" || fid == "diastolic"
										|| fid == "constriction_L"
										|| fid == "diastolic_L") {
									bpvr = this.BPControl(fid, "add", this);
								} else {
									this.addClass(fld, "x-form-invalid");
								}
								continue;
							} else {
								var fid = it.id;
								if (fid == "constriction" || fid == "diastolic"
										|| fid == "constriction_L"
										|| fid == "diastolic_L") {
									if (bpvr) {
										bpvr = this.BPControl(fid, "remove",this);
									}
								} else {
									this.removeClass(fld, "x-form-invalid");
								}
							}
						}
						var obj = fld;
						switch (it.type) {
							case "string" :
								var maxLength = it.length;
								var fv = fld.value;
								var fvLen = this.getStrSize(fv);
								if (fvLen > maxLength) {
									// obj.value="";
									this.addClass(obj, "x-form-invalid")
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
								}
								break;
							case 'int' :
								var maxValue = it.maxValue;
								var minValue = it.minValue;
								var fv = obj.value;
								var reg = new RegExp("^[0-9]*$");
								var fid = it.id;
								if (!reg.test(fv)) {
									if (fid == "constriction"|| fid == "diastolic"
											|| fid == "constriction_L"|| fid == "diastolic_L") {
										if (bpvr) {
											this.BPControl(fid, "add", this);
										}
									} else {
										this.addClass(obj, "x-form-invalid")
									}
									continue;
								} else {
									if (fid == "constriction"|| fid == "diastolic"
											|| fid == "constriction_L"|| fid == "diastolic_L") {
										if (bpvr) {
											this.BPControl(fid, "remove", this);
										}
									} else {
										this.removeClass(obj, "x-form-invalid");
									}
								}
								if (typeof(minValue) != 'undefined') {
									if (parseInt(fv) < minValue) {
										if (fid == "constriction"
												|| fid == "diastolic"
												|| fid == "constriction_L"
												|| fid == "diastolic_L") {
											if (bpvr) {
												this.BPControl(fid, "add",
																this);
											}
										} else {
											this.addClass(obj,"x-form-invalid")
										}
										continue;
									} else {
										if (fid == "constriction"
												|| fid == "diastolic"
												|| fid == "constriction_L"
												|| fid == "diastolic_L") {
											if (bpvr) {
												this.BPControl(fid, "remove",this);
											}
										} else {
											this.removeClass(obj,"x-form-invalid");
										}
									}
								}
								if (typeof(maxValue) != 'undefined') {
									if (parseInt(fv) > maxValue) {
										if (fid == "constriction"
												|| fid == "diastolic"
												|| fid == "constriction_L"
												|| fid == "diastolic_L") {
											if (bpvr) {
												this.BPControl(fid, "add",this);
											}
										} else {
											this.addClass(obj,"x-form-invalid")
										}
										continue;
									} else {
										if (fid == "constriction" || fid == "diastolic"
												|| fid == "constriction_L" || fid == "diastolic_L") {
											if (bpvr) {
												this.BPControl(fid, "remove",this);
											}
										} else {
											this.removeClass(obj,"x-form-invalid");
										}
									}
								}
								break;
							case "double" :
								var length = it.length;
								var precision = it.precision;
								var maxValue = it.maxValue;
								var minValue = it.minValue;
								var dd = 0;
								if (typeof(precision) != 'undefined') {
									dd = parseInt(precision);
								}
								var iNum = length - dd;
								var regStr = "(^[0-9]{0," + iNum
										+ "}$)|(^[0-9]{0," + iNum
										+ "}(\\.[0-9]{0," + dd + "})?$)";
								if (dd == 0) {
									regStr = "(^[0-9]{0," + iNum
											+ "}$)|(^[[0-9]*\\.[0-9]*]{0,"
											+ iNum + "}$)";
								}
								var reg = new RegExp(regStr);
								var fv = obj.value;
								if (!reg.test(fv)) {
									this.addClass(obj, "x-form-invalid")
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
								}
								if (typeof(minValue) != 'undefined') {
									if (parseInt(fv) < minValue) {
										this.addClass(obj, "x-form-invalid");
										continue;
									} else {
										this.removeClass(obj, "x-form-invalid");
									}
								}
								if (typeof(maxValue) != 'undefined') {
									if (parseInt(fv) > maxValue) {
										this.addClass(obj, "x-form-invalid");
										continue;
									} else {
										this.removeClass(obj, "x-form-invalid");
									}
								}
								break;
						}
					}
				}
			},
			addFieldDataValidateFun : function(schema) {
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (!this.isCreateField(it.id)) {
						var fld = document.getElementById(it.id
								+ this.idPostfix);
						if (!fld) {
							continue;
						}
						var notNull = false;
						if (it['not-null'] == "1" || it['not-null'] == "true") {
							notNull = true;
							if (fld.value == "") {
								this.addClass(fld, "x-form-invalid");
							} else {
								this.removeClass(fld, "x-form-invalid");
							}
						}
						var me = this;
						switch (it.type) {
							case "string" :
								var maxLength = it.length;
								var handleFun = function(maxLength, notNull,alias, obj, me) {
									return function() {
										me.validateString(maxLength, notNull,alias, obj, me);
									}
								}
								this.addEvent(fld, "change", handleFun(maxLength, notNull, it.alias,
												fld, me));
								break;
							case 'int' :
								var maxValue = it.maxValue;
								var minValue = it.minValue;
								var length = it.length;
								var handleFun = function(length, minValue,maxValue, notNull, fid, alias, obj, me) {
									return function() {
										me.validateInt(length, minValue,maxValue, notNull, fid, alias,
												obj, me);
									}
								}
								this.addEvent(fld, "change", handleFun(length,minValue, maxValue, notNull,
												it.id, it.alias, fld, me));
								break;
							case "double" :
								var length = it.length;
								var precision = it.precision;
								var maxValue = it.maxValue;
								var minValue = it.minValue;
								var handleFun = function(length, precision,minValue, maxValue, notNull, alias,
										obj, me) {
									return function() {
										me.validateDouble(length, precision,
												minValue, maxValue, notNull,
												alias, obj, me);
									}
								}
								this.addEvent(fld, "change", handleFun(length,
												precision, minValue, maxValue,
												notNull, it.alias, fld, me));
								break;
						}
					}
				}
			},
			getStrSize : function(str) {
				var realLength = 0, len = str.length, charCode = -1;
				for (var i = 0; i < len; i++) {
					charCode = str.charCodeAt(i);
					if (charCode >= 0 && charCode <= 128) {
						realLength += 1;
					} else {
						realLength += 2;
					}
				}
				return realLength;
			},
			hasClass : function(obj, cls) {
				return obj.className.match(new RegExp('(\\s|^)' + cls+ '(\\s|$)'));
			},
			addClass : function(obj, cls) {
				if (!this.hasClass(obj, cls))
					obj.className += " " + cls;
			},
			removeClass : function(obj, cls) {
				if (this.hasClass(obj, cls)) {
					var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
					obj.className = obj.className.replace(reg, ' ');
				}
			},
			validateString : function(maxLength, notNull, alias, obj, me) {
				var fv = obj.value;
				var fvLen = me.getStrSize(fv);
				if (fvLen > maxLength) {
					// obj.value="";
					me.addClass(obj, "x-form-invalid")
					obj.title = alias + "长度过长，最大为" + maxLength;
					return;
				} else {
					obj.title = alias;
					me.removeClass(obj, "x-form-invalid");
				}
				if (notNull) {
					if (fv == "") {
						obj.title = alias + "不能为空";
						me.addClass(obj, "x-form-invalid");
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
					}
				}
			},
			BPValidate : function(fid,me){
				var validatePass = true;
				if (fid == "constriction_L") {
					var lconsFld = document.getElementById(fid+ me.idPostfix);
					if (this.hasClass(lconsFld, "x-form-invalid")){
						var constrictionFld=document.getElementById("constriction"+ me.idPostfix);
						if(!constrictionFld.value || !constrictionFld.value >50){
							lconsFld.focus();
							lconsFld.select();
							lconsFld.title = "请录入正确的左侧收缩压值";
							MyMessageTip.msg("提示", "请录入正确的左侧收缩压值", true);
							validatePass = false;
							return validatePass;
						}else{
							me.removeClass(lconsFld, "x-form-invalid");
						}
					}
				}
				if(fid == 'diastolic_L'){
					var ldiasFld = document.getElementById(fid+ me.idPostfix);
					if (this.hasClass(ldiasFld, "x-form-invalid")){
						var diastolicFld=document.getElementById("diastolic"+ me.idPostfix);
						if(!diastolicFld.value || !diastolicFld.value >50){
							ldiasFld.focus();
							ldiasFld.select();
							ldiasFld.title = "请录入正确的左侧舒张压值";
							MyMessageTip.msg("提示", "请录入正确的左侧舒张压值", true);
							validatePass = false;
							return validatePass;
						}else{
							me.removeClass(ldiasFld, "x-form-invalid");
						}
					}
				}
				if(fid == 'constriction'){
					var rconsFld = document.getElementById(fid+ me.idPostfix);
					if (this.hasClass(rconsFld, "x-form-invalid")){
						var constriction_LFld=document.getElementById("constriction_L"+ me.idPostfix);
						if(!constriction_LFld.value || !constriction_LFld.value >50){
							rconsFld.focus();
							rconsFld.select();
							rconsFld.title = "请录入正确的右侧收缩压值";
							MyMessageTip.msg("提示", "请录入正确的右侧收缩压值", true);
							validatePass = false;
							return validatePass;
						}else{
							me.removeClass(rconsFld, "x-form-invalid");
						}
					}
				}
				if(fid == 'diastolic'){
					var rdiasFld = document.getElementById(fid + me.idPostfix);
					if (this.hasClass(rdiasFld, "x-form-invalid")){
						var diastolic_LFld=document.getElementById("diastolic_L"+ me.idPostfix);
						if(!diastolic_LFld.value || !diastolic_LFld.value >50){
							rdiasFld.focus();
							rdiasFld.select();
							rdiasFld.title = "请录入正确的右侧舒张压值";
							MyMessageTip.msg("提示", "请录入正确的右侧舒张压值", true);
							validatePass = false;
							return validatePass;
						}else{
							me.removeClass(rdiasFld, "x-form-invalid");
						}
					}
				}
				return validatePass;
			},
			BPControl : function(fid, op, me) {// constriction diastolic
				var validatePass = true;
				var BPLeft = document.getElementById("BPLeft"+me.idPostfix);
				var BPRight = document.getElementById("BPRight"+me.idPostfix);
				if (fid.indexOf("_L") > -1) {
					var relative = "";
					if (fid == "constriction_L") {
						relative = "diastolic";
					} else {
						relative = "constriction";
					}
					var relObj = document.getElementById(relative + "_L"+ me.idPostfix);
					var rv = relObj.value;
					if (rv && rv != "") {
						var fn = fid.substring(0, fid.indexOf("_L"));
						var RBPid = fn + me.idPostfix;
						var ro = document.getElementById(RBPid);
						var relo = document.getElementById(relative
								+ me.idPostfix);
						if (op == "add") {
							me.addClass(ro, "x-form-invalid");
							me.addClass(relo, "x-form-invalid");
							validatePass = false;
							if(BPLeft){
								BPLeft.color="#FF0000";
							}
							if(BPRight){
								BPRight.color="#FF0000";
							}
						} else {
							me.removeClass(ro, "x-form-invalid");
							me.removeClass(relo, "x-form-invalid");
							if(BPLeft){
								BPLeft.color="#FF0000";
							}
							if(BPRight){
								BPRight.color="#000000";
							}
						}
						var selfObj = document.getElementById(fid+ me.idPostfix);
						var sv = selfObj.value;
						if (relative == "constriction") {
							if (parseInt(rv) <= parseInt(sv)) {
								selfObj.title = "舒张压L(mmHg)应小于收缩压L(mmHg)";
								relObj.title = "收缩压L(mmHg)应大于舒张压L(mmHg)"
								me.addClass(selfObj, "x-form-invalid");
								me.addClass(relObj, "x-form-invalid");
								MyMessageTip.msg("提示", selfObj.title, true);
								MyMessageTip.msg("提示", relObj.title, true);
								validatePass = false;
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							} else {
								selfObj.title = "舒张压L(mmHg)";
								relObj.title = "收缩压L(mmHg)"
								me.removeClass(selfObj, "x-form-invalid");
								me.removeClass(relObj, "x-form-invalid");
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#000000";
								}
							}
						} else {
							if (parseInt(rv) >= parseInt(sv)) {
								selfObj.title = "收缩压L(mmHg)应大于舒张压L(mmHg)";
								relObj.title = "舒张压L(mmHg)应小于收缩压L(mmHg)"
								me.addClass(selfObj, "x-form-invalid");
								me.addClass(relObj, "x-form-invalid");
								MyMessageTip.msg("提示", selfObj.title, true);
								MyMessageTip.msg("提示", relObj.title, true);
								validatePass = false;
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							} else {
								selfObj.title = "收缩压L(mmHg)";
								relObj.title = "舒张压L(mmHg)"
								me.removeClass(selfObj, "x-form-invalid");
								me.removeClass(relObj, "x-form-invalid");
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#000000";
								}
							}
						}
					} else {
						var fn = fid.substring(0, fid.indexOf("_L"));
						var RBPid = fn + me.idPostfix;
						var ro = document.getElementById(RBPid);
						var relo = document.getElementById(relative+ me.idPostfix);
						var rov = ro.value;
						var relov = relo.value;
						if (rov == "" || relov == "") {
							me.addClass(relObj, "x-form-invalid");
							validatePass = false;
							if(BPLeft){
								BPLeft.color="#FF0000";
							}
							if(BPRight){
								BPRight.color="#FF0000";
							}
						} else {
							me.addClass(relObj, "x-form-invalid");
							if(BPLeft){
								BPLeft.color="#000000";
							}
							if(BPRight){
								BPRight.color="#000000";
							}
						}
					}
				} else {
					var relative = "";
					if (fid == "constriction") {
						relative = "diastolic";
					} else {
						relative = "constriction";
					}
					var relObj = document.getElementById(relative+ me.idPostfix);
					var rv = relObj.value;
					if (rv && rv != "") {
						var LBPid = fid + "_L" + me.idPostfix;
						var lo = document.getElementById(LBPid);
						var relo = document.getElementById(relative + "_L"+ me.idPostfix);
						if (op == "add") {
							me.addClass(ro, "x-form-invalid");
							me.addClass(relo, "x-form-invalid");
							if(BPLeft){
								BPLeft.color="#FF0000";
							}
							if(BPRight){
								BPRight.color="#FF0000";
							}
						} else {
							me.removeClass(lo, "x-form-invalid");
							me.removeClass(relo, "x-form-invalid");
							if(BPLeft){
								BPLeft.color="#000000";
							}
							if(BPRight){
								BPRight.color="#FF0000";
							}
						}
						var selfObj = document.getElementById(fid+ me.idPostfix);
						var sv = selfObj.value;
						if (relative == "constriction") {
							if (parseInt(rv) <= parseInt(sv)) {
								selfObj.title = "舒张压R(mmHg)应小于收缩压R(mmHg)";
								relObj.title = "收缩压R(mmHg)应大于舒张压R(mmHg)"
								me.addClass(selfObj, "x-form-invalid");
								me.addClass(relObj, "x-form-invalid");
								MyMessageTip.msg("提示", selfObj.title, true);
								MyMessageTip.msg("提示", relObj.title, true);
								validatePass = false;
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							} else {
								selfObj.title = "舒张压R(mmHg)";
								relObj.title = "收缩压R(mmHg)"
								me.removeClass(selfObj, "x-form-invalid");
								me.removeClass(relObj, "x-form-invalid");
								if(BPLeft){
									BPLeft.color="#000000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							}
						} else {
							if (parseInt(rv) >= parseInt(sv)) {
								selfObj.title = "收缩压R(mmHg)应大于舒张压R(mmHg)";
								relObj.title = "舒张压R(mmHg)应小于收缩压R(mmHg)"
								me.addClass(selfObj, "x-form-invalid");
								me.addClass(relObj, "x-form-invalid");
								MyMessageTip.msg("提示", selfObj.title, true);
								MyMessageTip.msg("提示", relObj.title, true);
								validatePass = false;
								if(BPLeft){
									BPLeft.color="#FF0000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							} else {
								selfObj.title = "收缩压R(mmHg)";
								relObj.title = "舒张压R(mmHg)"
								me.removeClass(selfObj, "x-form-invalid");
								me.removeClass(relObj, "x-form-invalid");
								if(BPLeft){
									BPLeft.color="#000000";
								}
								if(BPRight){
									BPRight.color="#FF0000";
								}
							}
						}
					} else {
						var LBPid = fid + "_L" + me.idPostfix;
						var lo = document.getElementById(LBPid);
						var relo = document.getElementById(relative + "_L"+ me.idPostfix);
						var lov = lo.value;
						var relov = relo.value;
						if (lov == "" || relov == "") {
							me.addClass(relObj, "x-form-invalid");
							validatePass = false;
							if(BPLeft){
								BPLeft.color="#FF0000";
							}
							if(BPRight){
								BPRight.color="#FF0000";
							}
						} else {
							me.addClass(relObj, "x-form-invalid");
							if(BPLeft){
								BPLeft.color="#000000";
							}
							if(BPRight){
								BPRight.color="#000000";
							}
						}
					}
				}
				return validatePass;
			},
			validateInt : function(length, minValue, maxValue, notNull, fid,alias, obj, me) {
				var fv = obj.value;
				var reg = new RegExp("^[0-9]*$");
				if (parseInt(fv.length) > parseInt(length)) {
					me.addClass(obj, "x-form-invalid")
					obj.title = alias + "长度过长，最大为" + length;
					return;
				}
				if (!reg.test(fv)) {
					me.addClass(obj, "x-form-invalid")
					obj.title = alias + "为正整数，请输入数字!";
					if (fid == "constriction" || fid == "diastolic"
							|| fid == "constriction_L" || fid == "diastolic_L") {
						me.BPControl(fid, "add", me);
					}
					return;
				} else {
					obj.title = alias;
					me.removeClass(obj, "x-form-invalid");
					if (fid == "constriction" || fid == "diastolic"
							|| fid == "constriction_L" || fid == "diastolic_L") {
						var vp = me.BPControl(fid, "remove", me);
						if (!vp) {
							return;
						}
					}
				}
				if (typeof(minValue) != 'undefined') {
					if (parseInt(fv) < minValue) {
						me.addClass(obj, "x-form-invalid")
						obj.title = alias + "最小值为" + minValue;
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							me.BPControl(fid, "add", me);
						}
						return;
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							var vp = me.BPControl(fid, "remove", me);
							if (!vp) {
								return;
							}
						}
					}
				}
				if (typeof(maxValue) != 'undefined') {
					if (parseInt(fv) > maxValue) {
						me.addClass(obj, "x-form-invalid")
						obj.title = alias + "最大值为" + maxValue;
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							me.BPControl(fid, "add", me);
						}
						return;
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							var vp = me.BPControl(fid, "remove", me);
							if (!vp) {
								return;
							}
						}
					}
				}
				if (notNull) {
					if (fv == "") {
						obj.title = alias + "不能为空";
						me.addClass(obj, "x-form-invalid");
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							me.BPControl(fid, "add", me);
						}
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
						if (fid == "constriction" || fid == "diastolic"
								|| fid == "constriction_L"
								|| fid == "diastolic_L") {
							var vp = me.BPControl(fid, "remove", me);
							if (!vp) {
								return;
							}
						}
					}
				}
			},
			validateDouble : function(length, precision, minValue, maxValue,
					notNull, alias, obj, me) {
				var dd = 0;
				if (typeof(precision) != 'undefined') {
					dd = parseInt(precision);
				}
				var iNum = length - dd;
				var regStr = "(^[0-9]{0," + iNum + "}$)|(^[0-9]{0," + iNum
						+ "}(\\.[0-9]{0," + dd + "})?$)";
				if (dd == 0) {
					regStr = "(^[0-9]{0," + iNum + "}$)|(^[[0-9]*\\.[0-9]*]{0,"
							+ iNum + "}$)";
				}
				var reg = new RegExp(regStr);
				var fv = obj.value;
				if (!reg.test(fv)) {
					me.addClass(obj, "x-form-invalid")
					obj.title = alias + "为浮点型数字，请输入正确的数字类型!";
					return;
				} else {
					obj.title = alias;
					me.removeClass(obj, "x-form-invalid");
				}
				if (typeof(minValue) != 'undefined') {
					if (parseInt(fv) < minValue) {
						me.addClass(obj, "x-form-invalid");
						obj.title = alias + "最小值为" + minValue;
						return;
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
					}
				}
				if (typeof(maxValue) != 'undefined') {
					if (parseInt(fv) > maxValue) {
						me.addClass(obj, "x-form-invalid");
						obj.title = alias + "最大值为" + maxValue;
						return;
					} else {
						me.removeClass(obj, "x-form-invalid");
					}
				}
				if (notNull) {
					if (fv == "") {
						obj.title = alias + "不能为空";
						me.addClass(obj, "x-form-invalid");
					} else {
						obj.title = alias;
						me.removeClass(obj, "x-form-invalid");
					}
				}
			},
			initFieldDisable : function() {
				var len = this.disableFlds.length;
				for (var i = 0; i < len; i++) {
					var fldId = this.disableFlds[i] + this.idPostfix;
					var fld = document.getElementById(fldId);
					if (fld) {
						// fld.style.display = "none";
						fld.disabled = true;
					}
				}
			},
			controlOtherFld : function() {
				var len = this.otherDisable.length;
				var me = this;
				for (var i = 0; i < len; i++) {
					var od = this.otherDisable[i];
					var cArr = od.control;
					var type = od.type;
					if (type == "text") {
						var co = cArr[0];
						// var key = co.key;
						var fieldArr = co.field;
						var disFldArr = co.disField;
						var fObj = document.getElementById(od.fld
								+ this.idPostfix);
						if (fObj) {
							var handleFun = function(obj, cFlds, dFlds, me) {
								return function() {
									me.textOnChange(obj, cFlds, dFlds, me);
								}
							}
							this.addEvent(fObj, "change", handleFun(fObj,fieldArr, disFldArr, me));
							this.addEvent(fObj, "keyup", handleFun(fObj,fieldArr, disFldArr, me));
						}
					}
					if (type == "checkbox") {
						for (var j = 0, cLen = cArr.length; j < cLen; j++) {
							var co = cArr[j];
							var key = co.key;
							var fieldArr = co.field;
							var fId = od.fld + "_" + key + this.idPostfix;
							var fObj = document.getElementById(fId);
							if (!fObj) {
								continue;
							}
							var handleFun = function(obj, cFlds, me) {
								return function() {
									me.checkOnClick(obj, cFlds, me);
								}
							}
							this.addEvent(fObj, "click", handleFun(fObj,fieldArr, me));
						}
					}
					if (type == "radio") {
						var fldName = od.fld;
						var fldes = document.getElementsByName(fldName);
						var handleFun = function(fldName, cArr, me) {
							return function() {
								me.radioOnClick(fldName, cArr, me);
							}
						}
						for (var k = 0, flen = fldes.length; k < flen; k++) {
							var fObj = fldes[k];
							this.addEvent(fObj, "click", handleFun(fldName,cArr, me));
						}
					}
				}
			},
			setFldsDisabled : function(flds, disabled, me) {
				if (disabled) {
					for (var i = 0, len = flds.length; i < len; i++) {
						var cfId = flds[i] + me.idPostfix;
						var cf = document.getElementById(cfId);
						if (cf) {
							cf.disabled = true;
							if (cf.type == "text") {
								cf.value = "";
							}
							if (cf.type == "checkbox" || cf.type == "radio") {
								cf.checked = false;
							}
							if(cfId.indexOf("div_")!=-1){
								cf.style.display="none";
							}
							if(cfId=="inhospitalDate_1_1"+me.idPostfix||cfId=="inhospitalDate_1_2"+me.idPostfix||cfId=="inhospitalDate_2_1"+me.idPostfix||cfId=="inhospitalDate_2_2"+me.idPostfix
							||cfId=="outhospitalDate_1_1"+me.idPostfix||cfId=="outhospitalDate_1_2"+me.idPostfix||cfId=="outhospitalDate_2_1"+me.idPostfix||cfId=="outhospitalDate_2_2"+me.idPostfix){
								cf.style.display="none";
							}
						}
					}
				} else {
					for (var i = 0, len = flds.length; i < len; i++) {
						var cfId = flds[i] + me.idPostfix;
						var cf = document.getElementById(cfId);
						if (cf) {
							cf.disabled = false;
							if(cfId.indexOf("div_")!=-1){
							cf.style.display="block";
							}
							if(cfId=="inhospitalDate_1_1"+me.idPostfix||cfId=="inhospitalDate_1_2"+me.idPostfix||cfId=="inhospitalDate_2_1"+me.idPostfix||cfId=="inhospitalDate_2_2"+me.idPostfix
							||cfId=="outhospitalDate_1_1"+me.idPostfix||cfId=="outhospitalDate_1_2"+me.idPostfix||cfId=="outhospitalDate_2_1"+me.idPostfix||cfId=="outhospitalDate_2_2"+me.idPostfix){
								cf.style.display="block";
							}
						}
					}
				}
			},
			textOnChange : function(obj, cFlds, dFlds, me) {
				var v = obj.value;
				if (v != '') {
					me.setFldsDisabled(cFlds, false, me);
				} else {
					me.setFldsDisabled(cFlds, true, me);
					me.setFldsDisabled(dFlds, true, me);
				}
			},
			checkOnClick : function(obj, cFlds, me) {
				if (obj.checked) {
					me.setFldsDisabled(cFlds, false, me);
				} else {
					me.setFldsDisabled(cFlds, true, me);
				}
			},
			radioOnClick : function(fldName, cArr, me) {
				var flds = document.getElementsByName(fldName);
				var fldValue = "";
				for (var i = 0, len = flds.length; i < len; i++) {
					var f = flds[i];
					if (f.checked) {
						fldValue = f.value;
					}
				}
				var enabled = false;
				var cFlds = [], disField = [];
				for (var g = 0, cLen = cArr.length; g < cLen; g++) {
					var co = cArr[g];
					var key = co.key;
					var exp = co.exp;
					cFlds = co.field;
					disField = co.disField;
					if (exp == "eq") {
						if (key == fldValue) {
							enabled = true;
							break;
						}
					}
					if (exp == "ne") {
						if (key != fldValue) {
							enabled = true;
							break;
						}
					}
				}
				if (enabled) {
					me.setFldsDisabled(cFlds, false, me);
				} else {
					me.setFldsDisabled(cFlds, true, me);
				}
				if (disField && disField.length > 0) {
					if (!enabled) {
						me.setFldsDisabled(disField, true, me);
					}
				}
			},
			setEnable : function() {
				var len = this.otherDisable.length;
				var me = this;
				for (var i = 0; i < len; i++) {
					var od = this.otherDisable[i];
					var cArr = od.control;
					var type = od.type;
					var fld = od.fld;
					if (type == "text") {
						var ftId = fld + this.idPostfix;
						var fldText = document.getElementById(ftId);
						if (fldText && fldText.value != "") {
							var fieldArr = cArr[0].field;
							for (var j = 0, fLen = fieldArr.length; j < fLen; j++) {
								var fid = fieldArr[j] + this.idPostfix;
								var f = document.getElementById(fid);
								if (f) {
									f.disabled = false;
								}
							}
						}
					}
					if (type == "checkbox") {
						for (var k = 0, cLen = cArr.length; k < cLen; k++) {
							var co = cArr[k];
							var key = co.key;
							var fldArr = co.field;
							var fid = fld + "_" + key + this.idPostfix;
							var f = document.getElementById(fid);
							if (f && f.checked) {
								for (var h = 0, hLen = fldArr.length; h < hLen; h++) {
									var tid = fldArr[h] + this.idPostfix;
									var tf = document.getElementById(tid);
									if (tf) {
										tf.disabled = false;
									}
								}
							}
						}
					}
					if (type == "radio") {
						var flds = document.getElementsByName(fld);
						var fldValue = "";
						for (var m = 0, mlen = flds.length; m < mlen; m++) {
							var f = flds[m];
							if (f.checked) {
								fldValue = f.value;
							}
						}
						var isEnable = false;
						var eflds = [];
						for (var n = 0, nlen = cArr.length; n < nlen; n++) {
							var co = cArr[n];
							var key = co.key;
							var exp = co.exp;
							if (exp == 'eq') {
								if (key == fldValue) {
									eflds = co.field;
									isEnable = true;
								}
							}
							if (exp == "ne") {
								if (key != fldValue) {
									eflds = co.field;
									isEnable = true;
								}
							}
						}
						if (isEnable) {
							for (var y = 0, ylen = eflds.length; y < ylen; y++) {
								var tid = eflds[y] + this.idPostfix;
								var tf = document.getElementById(tid);
								if (tf) {
									tf.disabled = false;
								}
							}
						}
					}
				}
			},
			countBMI : function(obj, me) {
				var bmi = document.getElementById("bmi" + me.idPostfix);
				if (bmi) {
					var w = document.getElementById("weight" + me.idPostfix).value;
					var h = document.getElementById("height" + me.idPostfix).value;
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2);
					bmi.value = b;
				}
			},
			setBMI : function() {
				var weightFld = document.getElementById("weight"+ this.idPostfix);
				var heightFld = document.getElementById("height"+ this.idPostfix);
				var me = this;
				var handleFun = function(obj, me) {
					return function() {
						me.countBMI(obj, me);
					}
				}
				this.addEvent(weightFld, "change", handleFun(weightFld, me));
				this.addEvent(heightFld, "change", handleFun(heightFld, me));
			},
			mutualExclusionSet : function() {
				var len = this.mutualExclusion.length;
				for (var i = 0; i < len; i++) {
					var meo = this.mutualExclusion[i];
					var fldName = meo.fld;
					var key = meo.key;
					var other = meo.other;
					var fldes = document.getElementsByName(fldName);
					var me = this;
					var handleFun = function(obj, fldName, key, other, me) {
						return function() {
							me.mutualExclusionClick(obj, fldName, key, other,me);
						}
					}
					for (var j = 0, flen = fldes.length; j < flen; j++) {
						var obj = fldes[j]
						this.addEvent(obj, "click", handleFun(obj, fldName,key, other, me));
					}
				}
			},
			mutualExclusionClick : function(obj, fldName, key, other, me) {
				var objValue = obj.value;
				var fldes = document.getElementsByName(fldName);
				if (objValue == key) {
					for (var k = 0, klen = fldes.length; k < klen; k++) {
						var to = fldes[k];
						if (to.checked && to.value != key) {
							to.checked = false;
						}
					}
					for (var h = 0, hlen = other.length; h < hlen; h++) {
						var oName = other[h];
						var oid = oName + me.idPostfix;
						var ofld = document.getElementById(oid);
						if (ofld) {
							ofld.value = "";
							ofld.disabled = true;
						}
					}
				} else {
					var meid = fldName + "_" + key + me.idPostfix;
					var meo = document.getElementById(meid);
					if (meo) {
						meo.checked = false;
					}
				}
			},
			recognizeRadioClick : function(obj, rid, me) {
				var ro = document.getElementById(rid);
				var checked = eval("me.checked" + rid);
				if (!checked) {
					ro.checked = true;
					eval("me.checked" + rid + "=true");
				} else {
					ro.checked = false;
					eval("me.checked" + rid + "=false");
				}
			},
			setRecognizeRadioClick : function() {
				for (var i = 1; i < 10; i++) {
					for (var j = 1; j < 3; j++) {
						var rid = "recognize" + i + "_" + i + "" + j
								+ this.idPostfix;
						var ro = document.getElementById(rid);
						var me = this;
						var handleFun = function(obj, rid, me) {
							return function() {
								me.recognizeRadioClick(obj, rid, me);
							}
						}
						eval("me.checked" + rid + "=" + ro.checked);
						this.addEvent(ro, "click", handleFun(ro, rid, me));
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
				var url = "resources/chis.prints.template.healthCheck.print?type="+ 1
						+ "&empiId="+ this.empiId+ "&healthCheck="+ this.healthCheck
				url += "&temp=" + new Date().getTime()
				var win = window.open(url,"","height="+ (screen.height - 100)+ ", width="+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			},
			isHaveElementInArray : function(arrayes, element) {
				var hasEle = false;
				for (var i = 0, len = arrayes.length; i < len; i++) {
					if (arrayes[i] == element) {
						hasEle = true;
						break;
					}
				}
				return hasEle
			},
			dateCtrl : function(dateFld, type, date) {
				// type:['min','max'] 将日期字段dateFld的最大(max)/最小(min)值设置为date
				if (type == "max") {
					dateFld.setMaxValue(date);
				}
				if (type == "min") {
					dateFld.setMinValue(date);
				}
			},
			/**
			 * 获取HTML模板中字段值
			 * 
			 * @param {}
			 *            fldName 字段名称 schema中item的id
			 */
			getHtmlFldValue : function(fldName) {
				var fldValue = "";
				var flds = document.getElementsByName(fldName);
				var vs = [];
				for (var i = 0, len = flds.length; i < len; i++) {
					var f = flds[i];
					if (f.type == "text" || f.type == "hidden") {
						vs.push(f.value || '');
					}
					if (f.type == "radio" || f.type == "checkbox") {
						if (f.checked) {
							vs.push(f.value);
						}
					}
				}
				fldValue = vs.join(',');
				return fldValue;
			},
			/**
			 * 给HTML模板中字段赋值
			 * 
			 * @param {}
			 *            fldName 字段名称 schema中item的id
			 * @param {}
			 *            fldValue 字段值
			 */
			setHtmlFldValue : function(fldName, fldValue) {
				var flds = document.getElementsByName(fldName);
				// 清原值
				for (var j = 0, n = flds.length; j < n; j++) {
					var f = flds[j];
					if (f.type == "radio" || f.type == "checkbox") {
						f.checked = false;
					}
				}
				// 赋值
				for (var i = 0, len = flds.length; i < len; i++) {
					var f = flds[i];
					if (f.type == "text" || f.type == "hidden") {
						f.value = fldValue || '';
						if (fldValue && fldValue.length > 0) {
							this.removeClass(f, "x-form-invalid");
						}
					}
					if (f.type == "radio") {
						if (f.value == fldValue) {
							f.checked = true;
							var divId = "div_" + fldName + this.idPostfix;
							var div = document.getElementById(divId);
							if (div) {
								this.removeClass(div, "x-form-invalid");
							}
							break;
						}
					}
					if (f.type == "checkbox") {
						var vs = fldValue.split(',');
						for (var vi = 0, vlen = vs.length; vi < vlen; vi++) {
							if (f.value == vs[vi]) {
								f.checked = true;
								var divId = "div_" + fldName + this.idPostfix;
								var div = document.getElementById(divId);
								if (div) {
									this.removeClass(div, "x-form-invalid");
								}
							}
						}
					}
				}
			},
			htmlFormSaveValidate : function(schema) {
				if (!schema) {
					schema = this.schema;
				}
				var validatePass = true;
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (it.display
							&& (it.display == "1" || it.display == 0 || it.hidden)) {
						continue;
					}
					if (this.isCreateField(it.id)) {
						var isLawful = true;
						for (var cfi = 0, cfLen = this.createFields.length; cfi < cfLen; cfi++) {
							isLawful = eval("this." + this.createFields[cfi]+ ".validate()");
							if (!isLawful) {
								validatePass = false;
								eval("this." + this.createFields[cfi]+ ".focus(true,200)");
//								console.log("-->this." + this.createFields[cfi]
//										+ "验证未通过。");
								MyMessageTip.msg("提示", it.alias + "为必填项", true);
								this.createFldSaveValidate(this.createFields[cfi]);
								break;
							}
						}
						if (validatePass == false) {
							break;
						}
					} else {
						if (it.dic) {
							if (it['not-null'] == "1"
									|| it['not-null'] == "true") {
								var dfv = this.getHtmlFldValue(it.id);
								var divId = "div_" + it.id + this.idPostfix;
								var div = document.getElementById(divId);
								if (div) {
									if (dfv && dfv.length > 0) {
										this.removeClass(div, "x-form-invalid");
										div.title = "";
									} else {
										this.addClass(div, "x-form-invalid");
										div.title = it.alias + "为必填项";
										validatePass = false;
										if (document.getElementsByName(it.id)[0]) {
											document.getElementsByName(it.id)[0].focus();
										}
//										console.log("-->" + it.id + "  "
//												+ it.alias + " 为必填项")
										MyMessageTip.msg("提示", div.title, true);
										break;
									}
								}
							}
						} else {
							var fld = document.getElementById(it.id
									+ this.idPostfix);
							if (!fld) {
								continue;
							}
							var fid = it.id;
							if ((it['not-null'] == "1" || it['not-null'] == "true")
									&& !it["pkey"]) {// 跳过主键必填验证
								if (fld.value == "" || (fld.value == fld.defaultValue && !fld.hidden)) {
									if (fid == "constriction"
											|| fid == "diastolic"
											|| fid == "constriction_L"
											|| fid == "diastolic_L") {
//										validatePass = this.BPControl(fid,
//												"add", this);
										validatePass = this.BPValidate(fid,this);	
									} else {
										this.addClass(fld, "x-form-invalid");
										fld.title = it.alias + "为必填项";
										validatePass = false;
										if (!document.getElementById(it.id
												+ this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id+ this.idPostfix).focus();
										document.getElementById(it.id+ this.idPostfix).select();
//										console.log("-->" + it.id + "  "
//												+ it.alias + " 为必填项")
										MyMessageTip.msg("提示", fld.title, true);
									}
									if (!validatePass) {
										break;
									}
								} else {
									if (fid == "constriction"
											|| fid == "diastolic"
											|| fid == "constriction_L"
											|| fid == "diastolic_L") {
//										validatePass = this.BPControl(fid,
//												"remove", this);
										validatePass = this.BPValidate(fid,this);		
										if (!validatePass) {
											break;
										}
									} else {
										this.removeClass(fld, "x-form-invalid");
									}
									fld.title = it.alias
								}
							}
							var obj = fld;
							switch (it.type) {
								case "string" :
									var maxLength = it.length;
									var fv = fld.value;
									var fvLen = this.getStrSize(fv);
									if (fvLen > maxLength) {
										this.addClass(obj, "x-form-invalid")
										obj.title = it.alias
												+ "中输入的字符串超出定义的最大长度（"
												+ maxLength + "）";
										validatePass = false;
										if (!document.getElementById(it.id+ this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id+ this.idPostfix).focus();
										document.getElementById(it.id+ this.idPostfix).select();
//										console.log("-->" + it.id + "  "
//												+ it.alias
//												+ "中输入的字符串超出定义的最大长度（"
//												+ maxLength + "）")
										MyMessageTip.msg("提示", obj.title, true);
									} else {
										this.removeClass(obj, "x-form-invalid");
										obj.title = it.alias
									}
									break;
								case 'int' :
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var fv = obj.value;
									if (fv == obj.defaultValue) {// 跳过注释文字验证
										continue;
									}
									var reg = new RegExp("^[0-9]*$");
									if (!reg.test(fv)) {
										if (fid == "constriction"
												|| fid == "diastolic"
												|| fid == "constriction_L"
												|| fid == "diastolic_L") {
											validatePass = this.BPValidate(fid,this);	
										} else {
											this.addClass(obj,"x-form-invalid")
										}
										obj.title = it.alias + "中输入了非整数 数字或字符"
										validatePass = false;
										if (!document.getElementById(it.id
												+ this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id+ this.idPostfix).focus();
										document.getElementById(it.id+ this.idPostfix).select();
//										console.log("-->" + it.id + "  "
//												+ it.alias + "中输入了非整数 数字或字符")
										MyMessageTip.msg("提示", obj.title, true);
										break;
									} else {
										if (fid == "constriction"
												|| fid == "diastolic"
												|| fid == "constriction_L"
												|| fid == "diastolic_L") {
											validatePass = this.BPValidate(fid,this);			
											if (!validatePass) {
												break;
											}
										} else {
											this.removeClass(obj,"x-form-invalid");
										}
										obj.title = it.alias
									}
									if (typeof(minValue) != 'undefined') {
										if (parseInt(fv) < minValue) {
											if (fid == "constriction"
													|| fid == "diastolic"
													|| fid == "constriction_L"
													|| fid == "diastolic_L") {
												validatePass = this.BPValidate(fid,this);			
											} else {
												this.addClass(obj,"x-form-invalid")
											}
											obj.title = it.alias
													+ "中输入的值小于了定义的最小值（"
													+ minValue + "）";
											validatePass = false;
											if (!document.getElementById(it.id
													+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id+ this.idPostfix).focus();
											document.getElementById(it.id+ this.idPostfix).select();
//											console.log("-->" + it.id + "  "
//													+ it.alias
//													+ "中输入的值小于了定义的最小值（"
//													+ minValue + "）")
											MyMessageTip.msg("提示", obj.title,true);
											break;
										} else {
											this.removeClass(obj,"x-form-invalid");
											obj.title = it.alias
											if (fid == "constriction"
													|| fid == "diastolic"
													|| fid == "constriction_L"
													|| fid == "diastolic_L") {
//												validatePass = this.BPControl(
//														fid, "remove", this);
												validatePass = this.BPValidate(fid,this);	
												if (!validatePass) {
													break;
												}
											}
										}
									}
									if (typeof(maxValue) != 'undefined') {
										if (parseInt(fv) > maxValue) {
											if (fid == "constriction"
													|| fid == "diastolic"
													|| fid == "constriction_L"
													|| fid == "diastolic_L") {
//												validatePass = this.BPControl(
//														fid, "add", this);
												validatePass = this.BPValidate(fid,this);			
											} else {
												this.addClass(obj,
														"x-form-invalid")
											}
											obj.title = it.alias
													+ "中输入的值大于了定义的最大值（"
													+ maxValue + "）";
											validatePass = false;
											if (!document.getElementById(it.id
													+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id+ this.idPostfix).focus();
											document.getElementById(it.id+ this.idPostfix).select();
//											console.log("-->" + it.id + "  "
//													+ it.alias
//													+ "中输入的值大于了定义的最大值（"
//													+ maxValue + "）")
											MyMessageTip.msg("提示", obj.title,true);
											break;
										} else {
											if (fid == "constriction"
													|| fid == "diastolic"
													|| fid == "constriction_L"
													|| fid == "diastolic_L") {
//												validatePass = this.BPControl(
//														fid, "remove", this);
												validatePass = this.BPValidate(fid,this);			
												if (!validatePass) {
													break;
												}
											} else {
												this.removeClass(obj,
														"x-form-invalid");
											}
											obj.title = it.alias
										}
									}
									break;
								case "double" :
									var length = it.length;
									var precision = it.precision;
									var maxValue = it.maxValue;
									var minValue = it.minValue;
									var dd = 0;
									if (typeof(precision) != 'undefined') {
										dd = parseInt(precision);
									}
									var iNum = length - dd;
									var regStr = "(^[0-9]{0," + iNum
											+ "}$)|(^[0-9]{0," + iNum
											+ "}(\\.[0-9]{0," + dd + "})?$)";
									if (dd == 0) {
										regStr = "(^[0-9]{0," + iNum
												+ "}$)|(^[[0-9]*\\.[0-9]*]{0,"
												+ iNum + "}$)";
									}
									var reg = new RegExp(regStr);
									var fv = obj.value;
									if (fv == obj.defaultValue) {// 跳过注释文字验证
										continue;
									}
									if (!reg.test(fv)) {
										this.addClass(obj, "x-form-invalid")
										obj.title = it.alias + "中输入了非浮点型数据或字符";
										validatePass = false;
										if (!document.getElementById(it.id
												+ this.idPostfix)) {
											continue;
										}
										document.getElementById(it.id+ this.idPostfix).focus();
										document.getElementById(it.id+ this.idPostfix).select();
//										console.log("-->" + it.id + "  "
//												+ it.alias + "中输入了非浮点型数据或字符")
										MyMessageTip.msg("提示", obj.title, true);
										break;
									} else {
										this.removeClass(obj, "x-form-invalid");
										obj.title = it.alias
									}
									if (typeof(minValue) != 'undefined') {
										if (parseInt(fv) < minValue) {
											this.addClass(obj,
															"x-form-invalid");
											obj.title = it.alias
													+ "中输入的值小于了定义的最小值（"
													+ minValue + "）"
											validatePass = false;
											if (!document.getElementById(it.id
													+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id+ this.idPostfix).focus();
											document.getElementById(it.id+ this.idPostfix).select();
//											console.log("-->" + it.id + "  "
//													+ it.alias
//													+ "中输入的值小于了定义的最小值（"
//													+ minValue + "）")
											MyMessageTip.msg("提示", obj.title,true);
											break;
										} else {
											this.removeClass(obj,"x-form-invalid");
											obj.title = it.alias
										}
									}
									if (typeof(maxValue) != 'undefined') {
										if (parseInt(fv) > maxValue) {
											this.addClass(obj,"x-form-invalid");
											obj.title = it.alias
													+ "中输入的值大于了定义的最大值（"
													+ maxValue + "）"
											validatePass = false;
											if (!document.getElementById(it.id
													+ this.idPostfix)) {
												continue;
											}
											document.getElementById(it.id+ this.idPostfix).focus();
											document.getElementById(it.id+ this.idPostfix).select();
//											console.log("-->" + it.id + "  "
//													+ it.alias
//													+ "中输入的值大于了定义的最大值（"
//													+ maxValue + "）")
											MyMessageTip.msg("提示", obj.title,true);
											break;
										} else {
											this.removeClass(obj,"x-form-invalid");
											obj.title = it.alias
										}
									}
									break;
							}
							if (validatePass == false) {
								break;
							}
						}
					}
				}
				return validatePass;
			},
			mannocheck:function(){
				document.getElementById("vulva_1"+this.idPostfix).disabled=true;
				document.getElementById("vulva_2"+this.idPostfix).disabled=true;
				document.getElementById("vaginal_1"+this.idPostfix).disabled=true;
				document.getElementById("vaginal_2"+this.idPostfix).disabled=true;
				document.getElementById("cervix_1"+this.idPostfix).disabled=true;
				document.getElementById("cervix_2"+this.idPostfix).disabled=true;
				document.getElementById("palace_1"+this.idPostfix).disabled=true;
				document.getElementById("palace_2"+this.idPostfix).disabled=true;
				document.getElementById("attachment_1"+this.idPostfix).disabled=true;
				document.getElementById("attachment_2"+this.idPostfix).disabled=true;				
			}
			
		});
		function oncheckWaychange(checkWay){
				var checkWay_1= document.getElementById("checkWay_1"+thisPanel.idPostfix);
				var checkWay_2= document.getElementById("checkWay_2"+thisPanel.idPostfix);
				var checkWay_3= document.getElementById("checkWay_3"+thisPanel.idPostfix);
				var checkWay_4= document.getElementById("checkWay_4"+thisPanel.idPostfix);
				var checkWay_5= document.getElementById("checkWay_5"+thisPanel.idPostfix);
				var checkWay_6= document.getElementById("checkWay_6"+thisPanel.idPostfix);
				if(checkWay=="1" && checkWay_1.checked==true ){
					document.getElementById("checkWay_2"+thisPanel.idPostfix).checked=false;
					document.getElementById("checkWay_3"+thisPanel.idPostfix).checked=false;
					document.getElementById("checkWay_4"+thisPanel.idPostfix).checked=false;
					document.getElementById("checkWay_5"+thisPanel.idPostfix).checked=false;
					document.getElementById("checkWay_6"+thisPanel.idPostfix).checked=false;
				}
				if(checkWay=="2" && checkWay_2.checked==true){
					document.getElementById("checkWay_1"+thisPanel.idPostfix).checked=false;
				}
				if(checkWay=="3" && checkWay_3.checked==true){
					document.getElementById("checkWay_1"+thisPanel.idPostfix).checked=false;
				}
				if(checkWay=="4" && checkWay_4.checked==true){
					document.getElementById("checkWay_1"+thisPanel.idPostfix).checked=false;
				}
				if(checkWay=="5" && checkWay_5.checked==true){
					document.getElementById("checkWay_1"+thisPanel.idPostfix).checked=false;
				}
				if(checkWay=="6" && checkWay_6.checked==true){
					document.getElementById("checkWay_1"+thisPanel.idPostfix).checked=false;
				}
				
			}
			function onFootPulseChange(val){
				var footPulse_1= document.getElementById("footPulse_1"+thisPanel.idPostfix);
				var footPulse_2= document.getElementById("footPulse_2"+thisPanel.idPostfix);
				var footPulse_3= document.getElementById("footPulse_3"+thisPanel.idPostfix);
				var footPulse_4= document.getElementById("footPulse_4"+thisPanel.idPostfix);
				if(val=="1" && footPulse_1.checked==true ){
					footPulse_2.checked=false;
					footPulse_3.checked=false;
					footPulse_4.checked=false;
				}
				if(val=="2" && footPulse_2.checked==true){
					footPulse_1.checked=false;
					footPulse_3.checked=false;
					footPulse_4.checked=false;
				}
				if(val=="3" && footPulse_3.checked==true){
					footPulse_1.checked=false;
					footPulse_2.checked=false;
				}
				if(val=="4" && footPulse_4.checked==true){
					footPulse_1.checked=false;
					footPulse_2.checked=false;
				}
			}
			function onDentureChange(val){
				var denture_1= document.getElementById("denture_1"+thisPanel.idPostfix);
				var denture_2= document.getElementById("denture_2"+thisPanel.idPostfix);
				var denture_3= document.getElementById("denture_3"+thisPanel.idPostfix);
				var denture_4= document.getElementById("denture_4"+thisPanel.idPostfix);
				if(val=="1" && denture_1.checked==true ){
					denture_2.checked=false;
					denture_3.checked=false;
					denture_4.checked=false;
					document.getElementById("leftUp"+thisPanel.idPostfix).value="";
					document.getElementById("leftDown"+thisPanel.idPostfix).value="";
					document.getElementById("rightUp"+thisPanel.idPostfix).value="";
					document.getElementById("rightDown"+thisPanel.idPostfix).value="";
					document.getElementById("leftUp2"+thisPanel.idPostfix).value="";
					document.getElementById("leftDown2"+thisPanel.idPostfix).value="";
					document.getElementById("rightUp2"+thisPanel.idPostfix).value="";
					document.getElementById("rightDown2"+thisPanel.idPostfix).value="";
					document.getElementById("leftUp3"+thisPanel.idPostfix).value="";
					document.getElementById("leftDown3"+thisPanel.idPostfix).value="";
					document.getElementById("rightUp3"+thisPanel.idPostfix).value="";
					document.getElementById("rightDown3"+thisPanel.idPostfix).value="";
				}
				if(val=="2" && denture_2.checked==true){
					document.getElementById("denture_1"+thisPanel.idPostfix).checked=false;
				}
				if(val=="3" && denture_3.checked==true){
					document.getElementById("denture_1"+thisPanel.idPostfix).checked=false;
				}
				if(val=="4" && denture_4.checked==true){
					document.getElementById("denture_1"+thisPanel.idPostfix).checked=false;
				}
			}
			function oncancel(id){
				var rid=id;
				var ro = document.getElementById(rid);
				if (ro.checked) {
					ro.checked = false;
				}
			}
			
			
			