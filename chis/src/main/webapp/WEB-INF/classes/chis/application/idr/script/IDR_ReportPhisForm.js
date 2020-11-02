$package("chis.application.idr.script")

$import("chis.script.BizTableFormView","chis.script.util.helper.Helper");

chis.application.idr.script.IDR_ReportPhisForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 170;
	cfg.labelWidth = 90;
	cfg.width = 900;
	chis.application.idr.script.IDR_ReportPhisForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforePrint", this.onBeforePrint, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
};

Ext.extend(chis.application.idr.script.IDR_ReportPhisForm,
		chis.script.BizTableFormView, {
			doCreate : function(){
				this.initDataId = null;
				this.doNew();
				if(!this.form.data){
					this.form.data={};
				}
//				this.form.exContext.args.MS_BRZD_JLBH = null;
				this.op = "create";
				this.fireEvent("IDRFormCreate");
			},
			onReady : function() {
				chis.application.idr.script.IDR_ReportPhisForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var patientJob = form.findField("patientJob");
				if (patientJob) {
					patientJob.on("select", this.onSelectPatientJob, this);
				}
				var categoryAInfectious = form.findField("categoryAInfectious")
				this.categoryAInfectious = categoryAInfectious
				if (categoryAInfectious) {
					categoryAInfectious.on("select",
							this.checkCategoryInfectious, this);
				}
				var categoryBInfectious = form.findField("categoryBInfectious");
				this.categoryBInfectious = categoryBInfectious
				if (categoryBInfectious) {
					categoryBInfectious.on("select",
							this.onSelectCategoryBInfectious, this);
				}
				var categoryCInfectious = form.findField("categoryCInfectious")
				this.categoryCInfectious = categoryCInfectious
				if (categoryCInfectious) {
					categoryCInfectious.on("select",
							this.checkCategoryInfectious, this)
				}
				var otherCategoryInfectious = form
						.findField("otherCategoryInfectious")
				this.otherCategoryInfectious = otherCategoryInfectious
				if (otherCategoryInfectious) {
					otherCategoryInfectious.on("keyup",
							this.checkCategoryInfectious, this)
					otherCategoryInfectious.on("blur",
							this.checkCategoryInfectious, this)
				}

				var dateAccident = form.findField("dateAccident");
				if (dateAccident) {
					dateAccident.on("select", this.onSelectDateAccident, this);
				}
				// 加上延迟,防止太快执行,参数没有完全加载
				var d = new Ext.util.DelayedTask(function(){
					var MS_BRZD_JLBH = form.findField("MS_BRZD_JLBH");
					if (MS_BRZD_JLBH) {
						MS_BRZD_JLBH.setValue(this.exContext.args.MS_BRZD_JLBH || '');
					}
					var icd10 = form.findField("icd10");
					if (icd10) {
						icd10.setValue(this.exContext.args.ICD10 || '');
					}
					var personName = form.findField("personName");
					if (personName) {
						personName.setValue(this.exContext.empiData.personName || '');
					}
					var sexCode = form.findField("sexCode");
					if (sexCode) {
						var xbStr = this.exContext.empiData.sexCode || '';
						if(xbStr){//性别
							var sexCodeStr = xbStr.replace(/0/, "");
							var dicName = {
			            		id : "phis.dictionary.gender"
			          		};
							var gender=util.dictionary.DictionaryLoader.load(dicName);
							var di = gender.wraper[sexCodeStr];
							var sexNameStr=""
							if (di) {
								sexNameStr = di.text;
							}
							sexCode.setValue({
								key : sexCodeStr,
								text : sexNameStr
							});
						}
					}
					var birthday = form.findField("birthday");
					if (birthday) {
						birthday.setValue(this.exContext.empiData.birthday || '');
					}
					if (this.exContext.empiData.birthday) { // 设置实足年龄默认值
						var birthdayStr=this.exContext.empiData.birthday;
						if(typeof birthdayStr=="string"){
							birthdayStr=new Date(Date.parse(birthdayStr));
						}
						var diffTime = chis.script.util.helper.Helper
								.getAgeBetween(birthdayStr,
										new Date());
						var fullAge = form.findField("fullAge");
						fullAge.setValue(diffTime);
					}
					var idCard = form.findField("idCard");
					if (idCard) {
						idCard.setValue(this.exContext.empiData.idCard || '');
					}
					var workPlace = form.findField("workPlace");
					if (workPlace) {
						workPlace.setValue(this.exContext.empiData.workPlace || '');
					}
					var mobileNumber = form.findField("mobileNumber");
					if (mobileNumber) {
						mobileNumber.setValue(this.exContext.empiData.mobileNumber || '');
					}
					var fillDate = form.findField("fillDate");
					if (fillDate) {
						fillDate.setValue(new Date());
					}
					var createDate = form.findField("createDate");
					if (createDate) {
						createDate.setValue(new Date());
					}
				},this);
				d.delay(1000);
			},

			onSelectDateAccident : function(field, date) {
				var form = this.form.getForm();
				var deathDate = form.findField("deathDate");
				var dead = deathDate.getValue();
				if (dead && dead < date) {
					deathDate.setValue();
				}
				deathDate.setMinValue(date);
			},
			checkCategoryInfectious : function() {
				if (this.categoryAInfectious.getValue() == ""
						&& this.categoryBInfectious.getValue() == ""
						&& this.categoryCInfectious.getValue() == ""
						&& this.otherCategoryInfectious.getValue() == "") {
					this.categoryAInfectious.allowBlank = false
					Ext.getCmp(this.categoryAInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("<span style='color:red'>" + "甲类传染病"
									+ ":</span>");
					this.categoryBInfectious.allowBlank = false
					Ext.getCmp(this.categoryBInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("<span style='color:red'>" + "乙类传染病"
									+ ":</span>");
					this.categoryCInfectious.allowBlank = false
					Ext.getCmp(this.categoryCInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("<span style='color:red'>" + "丙类传染病"
									+ ":</span>");
					this.otherCategoryInfectious.allowBlank = false
					Ext.getCmp(this.otherCategoryInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("<span style='color:red'>" + "其他传染病"
									+ ":</span>");
				} else {
					this.categoryAInfectious.allowBlank = true
					Ext.getCmp(this.categoryAInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("甲类传染病:");
					this.categoryBInfectious.allowBlank = true
					Ext.getCmp(this.categoryBInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("乙类传染病:");
					this.categoryCInfectious.allowBlank = true
					Ext.getCmp(this.categoryCInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("丙类传染病:");
					this.otherCategoryInfectious.allowBlank = true
					Ext.getCmp(this.otherCategoryInfectious.id).getEl()
							.up('.x-form-item').child('.x-form-item-label')
							.update("其他传染病:");
				}
				this.validate()
			},
			onSelectCategoryBInfectious : function(item) {
				this.checkCategoryInfectious();
				var categoryBInfectious = arguments[5]
						? arguments[5].categoryBInfectious.key
						: arguments[0].getValue();
				var has3, has11, has12, has13, has14, has22, has25 = false;
				if (categoryBInfectious) {
					var adressArray = categoryBInfectious.split(",");
					for (var i = 0; i < adressArray.length; i++) {
						if (adressArray[i] == "0300") { // 如果病毒性肝炎
							has3 = true;
						} else if (adressArray[i] == "1900") {// 炭疽
							has11 = true;
						} else if (adressArray[i] == "0400") {// 痢疾
							has12 = true;
						} else if (adressArray[i] == "2600") {// 肺结核
							has13 = true;
						} else if (adressArray[i] == "0500") {// 伤寒
							has14 = true;
						} else if (adressArray[i] == "0800") {// 梅毒
							has22 = true;
						} else if (adressArray[i] == "2300") {// 疟疾
							has25 = true;
						}

					}
				}
				if (has3) {
					this.changeFieldState(false, "viralHepatitis");
				} else {
					this.changeFieldState(true, "viralHepatitis");
				}
				if (has11) {
					this.changeFieldState(false, "anthrax");
				} else {
					this.changeFieldState(true, "anthrax");
				}
				if (has12) {
					this.changeFieldState(false, "dysentery");
				} else {
					this.changeFieldState(true, "dysentery");
				}
				if (has13) {
					this.changeFieldState(false, "phthisis");
				} else {
					this.changeFieldState(true, "phthisis");
				}
				if (has14) {
					this.changeFieldState(false, "typhia");
				} else {
					this.changeFieldState(true, "typhia");
				}

				if (has22) {
					this.changeFieldState(false, "syphilis");
				} else {
					this.changeFieldState(true, "syphilis");
				}
				if (has25) {
					this.changeFieldState(false, "malaria");
				} else {
					this.changeFieldState(true, "malaria");
				}

			},

			onSelectPatientJob : function(item) {
				if (item.getValue() == 17) {
					this.changeFieldState(false, "otherPatientJob");
				} else {
					this.changeFieldState(true, "otherPatientJob");
				}

			},
			onLoadData : function(entry, body) {
				this.checkCategoryInfectious();
				if (body.patientJob && body.patientJob.key == 17) {
					this.changeFieldState(false, "otherPatientJob");
				} else {
					this.changeFieldState(true, "otherPatientJob");
				}
				if(body.categoryBInfectious){
					var categoryBInfectious = body.categoryBInfectious.key;
					if (categoryBInfectious) {
						var has3, has11, has12, has13, has14, has22, has25 = false;
						var adressArray = categoryBInfectious.split(",");
						for (var i = 0; i < adressArray.length; i++) {
							if (adressArray[i] == "0300") { // 如果病毒性肝炎
								has3 = true;
							} else if (adressArray[i] == "1900") {// 炭疽
								has11 = true;
							} else if (adressArray[i] == "0400") {// 痢疾
								has12 = true;
							} else if (adressArray[i] == "2600") {// 肺结核
								has13 = true;
							} else if (adressArray[i] == "0500") {// 伤寒
								has14 = true;
							} else if (adressArray[i] == "0800") {// 梅毒
								has22 = true;
							} else if (adressArray[i] == "2300") {// 疟疾
								has25 = true;
							}
	
						}
						if (has3) {
							this.changeFieldState(false, "viralHepatitis");
						} else {
							this.changeFieldState(true, "viralHepatitis");
						}
						if (has11) {
							this.changeFieldState(false, "anthrax");
						} else {
							this.changeFieldState(true, "anthrax");
						}
						if (has12) {
							this.changeFieldState(false, "dysentery");
						} else {
							this.changeFieldState(true, "dysentery");
						}
						if (has13) {
							this.changeFieldState(false, "phthisis");
						} else {
							this.changeFieldState(true, "phthisis");
						}
						if (has14) {
							this.changeFieldState(false, "typhia");
						} else {
							this.changeFieldState(true, "typhia");
						}
	
						if (has22) {
							this.changeFieldState(false, "syphilis");
						} else {
							this.changeFieldState(true, "syphilis");
						}
						if (has25) {
							this.changeFieldState(false, "malaria");
						} else {
							this.changeFieldState(true, "malaria");
						}
					}
				}
				var form = this.form.getForm();
				// 加上延迟,防止太快执行,参数没有完全加载
				var d = new Ext.util.DelayedTask(function(){
					var MS_BRZD_JLBH = form.findField("MS_BRZD_JLBH");
					if (MS_BRZD_JLBH) {
						MS_BRZD_JLBH.setValue(this.exContext.args.MS_BRZD_JLBH || '');
					}
					var icd10 = form.findField("icd10");
					if (icd10) {
						icd10.setValue(this.exContext.args.ICD10 || '');
					}
					var personName = form.findField("personName");
					if (personName) {
						personName.setValue(this.exContext.empiData.personName || '');
					}
					var sexCode = form.findField("sexCode");
					if (sexCode) {
						var xbStr = this.exContext.empiData.sexCode || '';
						if(xbStr){//性别
							var sexCodeStr = xbStr.replace(/0/, "");
							var dicName = {
			            		id : "phis.dictionary.gender"
			          		};
							var gender=util.dictionary.DictionaryLoader.load(dicName);
							var di = gender.wraper[sexCodeStr];
							var sexNameStr=""
							if (di) {
								sexNameStr = di.text;
							}
							sexCode.setValue({
								key : sexCodeStr,
								text : sexNameStr
							});
						}
					}
					var birthday = form.findField("birthday");
					if (birthday) {
						birthday.setValue(this.exContext.empiData.birthday || '');
					}
					if (this.exContext.empiData.birthday) { // 设置实足年龄默认值
						var birthdayStr=this.exContext.empiData.birthday;
						if(typeof birthdayStr=="string"){
							birthdayStr=new Date(Date.parse(birthdayStr));
						}
						var diffTime = chis.script.util.helper.Helper
								.getAgeBetween(birthdayStr,
										new Date());
						var fullAge = form.findField("fullAge");
						fullAge.setValue(diffTime);
					}
					var idCard = form.findField("idCard");
					if (idCard) {
						idCard.setValue(this.exContext.empiData.idCard || '');
					}
					var workPlace = form.findField("workPlace");
					if (workPlace) {
						workPlace.setValue(this.exContext.empiData.workPlace || '');
					}
					var mobileNumber = form.findField("mobileNumber");
					if (mobileNumber) {
						mobileNumber.setValue(this.exContext.empiData.mobileNumber || '');
					}
					var fillDate = form.findField("fillDate");
					if (fillDate) {
						fillDate.setValue(new Date());
					}
					var createDate = form.findField("createDate");
					if (createDate) {
						createDate.setValue(new Date());
					}
				},this);
				d.delay(500);
			},
			onBeforeCreate : function() {
				this.checkCategoryInfectious();
				if (this.exContext.args.birthday) { // 设置实足年龄默认值
					var birthday=this.exContext.args.birthday;
					if(typeof birthday=="string"){
						birthday=new Date(Date.parse(birthday));
					}
					var diffTime = chis.script.util.helper.Helper
							.getAgeBetween(birthday,
									new Date());
					var fullAge = this.form.getForm().findField("fullAge");
					fullAge.setValue(diffTime);
				}
				this.changeFieldState(true, "otherPatientJob");
				this.changeFieldState(true, "viralHepatitis");
				this.changeFieldState(true, "anthrax");
				this.changeFieldState(true, "dysentery");
				this.changeFieldState(true, "phthisis");
				this.changeFieldState(true, "typhia");
				this.changeFieldState(true, "syphilis");
				this.changeFieldState(true, "malaria");
			},
			loadData : function() {
				this.initDataId = this.exContext.ids['IDR_Report.RecordID'];
				chis.application.idr.script.IDR_ReportPhisForm.superclass.loadData
						.call(this);
				var form = this.form.getForm();
				var deathDate = form.findField("deathDate");
				if (deathDate) {
					deathDate.minValue = null;
				}
			},
			/**
			 * 获取保存数据的请求数据
			 * 
			 * @return {}
			 */
			getSaveRequest : function(saveData) {
				var values = saveData;
				return values;
			},
			doSave : function() {
				this.data.empiId = this.exContext.ids.empiId;
				this.data.phrId = this.exContext.ids.phrId;
				this.data.MS_BRZD_JLBH = this.exContext.args.MS_BRZD_JLBH || '';
				this.data.icd10 = this.exContext.args.ICD10 || '';
				chis.application.idr.script.IDR_ReportPhisForm.superclass.doSave
						.call(this);
			},
			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.infectiousDisease"];
				ids_str.value = "&empiId=" + this.exContext.ids.empiId;
				return true;
			},
//			doPrint : function(){
//				var pages="phis.prints.jrxml.infectiousDisease";
//				var url="resources/"+pages+".print?type=1";
//				url += "&empiId="+this.exContext.ids.empiId;
//				var LODOP=getLodop();
//				LODOP.PRINT_INIT("打印控件");
//				LODOP.SET_PRINT_PAGESIZE("0","","","");
//				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
//				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
//				rehtm.lastIndexOf("page-break-after:always;");
//				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
//				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
//				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
//				LODOP.PREVIEW();
//			},
			afterSaveData : function(entryName, op, json, data) {
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
			}
		});