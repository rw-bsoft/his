$package("chis.application.cons.script")
$import("chis.script.BizTableFormView", "chis.script.util.Vtype",
		"chis.application.mpi.script.CombinationSelect",
		"chis.application.hr.script.BasicPersonalInformationForm")

chis.application.cons.script.ConsultationRecordForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	cfg.labelWidth = 80;
	cfg.width = 715;
	this.queryServiceId = "chis.consultationRecordService";
	chis.application.cons.script.ConsultationRecordForm.superclass.constructor
			.apply(this, [cfg])
	// this.on("loadData", this.onLoadData, this);
	this.on("winShow", this.onWinShow, this);
	this.on("beforePrint", this.onBeforePrint, this);
}

Ext.extend(chis.application.cons.script.ConsultationRecordForm,
		chis.script.BizTableFormView, {

			doCreate : function() {
				this.empiId = "";
				this.phrId = "";
				this.manaDoctorId = "";
				chis.application.cons.script.ConsultationRecordForm.superclass.doCreate
						.call(this);
				this.setButton(["save"], false);
			},

			onReady : function() {
				var form = this.form.getForm();
				var idCard = form.findField("idCard");
				idCard.on("change", this.onIdCardChange, this);
				var healthNo = form.findField("healthNo");
				healthNo.on("change", this.onHealthNoChange, this);
				var personName = form.findField("personName");
				personName.on("change", this.onPersonInfoChange, this);
				var birthday = form.findField("birthday");
				birthday.on("change", this.onPersonInfoChange, this);
				var sexCode = form.findField("sexCode");
				sexCode.on("change", this.onPersonInfoChange, this);
			},

			onHealthNoChange : function(f) {
				var value = f.getValue();
				if (!value) {
					return;
				}
				this.empiId = "";
				util.rmi.jsonRequest({
							serviceId : this.queryServiceId,
							serviceAction : "queryPersonInfoByHealthNo",
							method : "execute",
							body : {
								healthNo : value
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.personName) {
								this.empiId = json.empiId;
								this.initFormData(json)
								this.checkupPhrId();
								this.queryPerson = false;
							} else {
								this.ifCreateRecord();
							}
						}, this)
			},

			onIdCardChange : function(f) {
				if (!f.validate()) {
					return;
				}
				var value = f.getValue();
				if (!value) {
					return;
				}
				this.empiId = "";
				util.rmi.jsonRequest({
							serviceId : this.queryServiceId,
							serviceAction : "queryPersonInfoByIdCard",
							method : "execute",
							body : {
								idCard : value
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.personName) {
								this.empiId = json.empiId;
								this.initFormData(json)
								this.checkupPhrId();
								this.queryPerson = false;
							} else {
								this.ifCreateRecord();
							}
						}, this)
			},

			onPersonInfoChange : function() {
				if (!this.queryPerson) {
					return;
				}
				var form = this.form.getForm();
				var personName = form.findField("personName").getValue();
				var birthday = form.findField("birthday").getValue();
				var sexCode = form.findField("sexCode").getValue();
				if (!(personName && birthday && sexCode)) {
					return;
				}
				this.empiId = "";
				var values = {};
				values.personName = personName;
				values.birthday = birthday;
				values.sexCode = sexCode;
				util.rmi.jsonRequest({
							serviceId : this.queryServiceId,
							serviceAction : "advancedSearch",
							method : "execute",
							body : values
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.body) {
								this.showDataInSelectView(json.body);
							} else {
								this.ifCreateRecord();
							}
						}, this)
			},

			showDataInSelectView : function(data) {
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				if (records.length == 0) {
					return;
				}
				var empiIdSelectView = this.midiModules["empiIdSelectView"];
				if (!empiIdSelectView) {
					var empiIdSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人记录",
								width : 500,
								height : 300
							});
					this.midiModules["empiIdSelectView"] = empiIdSelectView;
					empiIdSelectView.on("onSelect", function(r) {
								this.empiId = r.get("empiId");
								this.initFormData(r.data);
								this.checkupPhrId();
							}, this);
				}
				empiIdSelectView.getWin().show();
				var task = new Ext.util.DelayedTask(function() {
							empiIdSelectView.setRecords(records);
						}, this);
				task.delay(300);
			},

			checkupPhrId : function() {
				if (!this.empiId) {
					return;
				}
				this.phrId = "";
				util.rmi.jsonRequest({
							serviceId : this.queryServiceId,
							serviceAction : "checkupHealthRecord",
							method : "execute",
							body : {
								empiId : this.empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.haveRecord) {
								this.phrId = json.phrId;
								var manaDoctorId = json.manaDoctorId;
								this.manaDoctorId = manaDoctorId.key;
								var manaDoctorIdField = this.form.getForm()
										.findField("manaDoctorId");
								manaDoctorIdField.setValue(manaDoctorId);
								this.setButton(["save"], true);
							} else {
								this.ifCreateRecord();
							}
						}, this)
			},

			ifCreateRecord : function() {
				var msg = "没有该人的个人基本信息记录和个人健康档案记录，是否新建？";
				if (this.empiId) {
					"没有该人的个人健康档案记录，是否新建？";
				}
				Ext.Msg.show({
							title : '新建个人信息',
							msg : msg,
							buttons : Ext.Msg.YESNO,
							fn : function(btn, text) {
								if (btn == "no") {
									this.setButton(["save"], false);
									this.queryPerson = true;
									return;
								}
								var m = this.createModule("grjbxx2",
										"chis.application.hr.HR/HR/B34101")
								if (m.nll) {
									m.nll = {};
								}
								m.id = "jzjlrgxx2";
								m.on("save", this.onSaveEmpiInfo, this)
								m.op = "create";
								win = m.getWin();
								this.empiWin = win
								win.add(m.initPanel())
								m.doNew();
								win.setPosition(250, 100);
								win.show();
								if (this.empiId) {
									var data = {};
									data.empiId = this.empiId;
									m.loadData(data)
								} else {
									var data = {};
									var fieldNames = ["personName", "sexCode",
											"birthday", "idCard", "healthNo"];
									var form = this.form.getForm();
									for (var i = 0; i < fieldNames.length; i++) {
										var name = fieldNames[i]
										var field = form.findField(name);
										var value = field.getValue();
										if (name == "healthNo") {
											data["cardNo"] = value;
										} else {
											data[name] = value;
										}
									}
									m.initFormData(data);
								}
							},
							scope : this,
							// animEl : 'elId',
							icon : Ext.MessageBox.QUESTION
						});
			},

			onSaveEmpiInfo : function(entryName, op, json, data) {
				this.queryPerson = false;
				this.empiWin.hide();
				this.manaDoctorId = data.jbxx.manaDoctorId;
				this.setButton(["save"], true);
				this.initFormData(data.jbxx)
				var form = this.form.getForm();
				var healthNo = form.findField("healthNo");
				healthNo.setValue(data.jbxx.cardNo);
				var sexCodeField = form.findField("sexCode");
				var sexCode = data.sexCode
				if (!sexCode) {
					sexCode = data.jbxx.sexCode
				}
				var sexCode_text = "未知的性别";
				if (sexCode == "1") {
					var sexCode_text = "男";
				} else if (sexCode == "2") {
					var sexCode_text = "女";
				} else if (sexCode == "9") {
					var sexCode_text = "未说明的性别";
				}
				sexCodeField.setValue({
							key : sexCode,
							text : sexCode_text
						});
				this.empiId = json.body.empiId;
				this.phrId = json.body.phrId;
				if (!this.phrId) {
					this.checkupPhrId();
				}
			},

			onWinShow : function() {
				this.queryPerson = true;
				var task = new Ext.util.DelayedTask(function() {
					if (this.initDataId) {
						this.doNew();
						var listEntryName = "chis.application.cons.schemas.CONS_ConsultationRecord";
						var data = this.exContext[listEntryName].data
						this.empiId = data.empiId;
						this.phrId = data.phrId;
						this.manaDoctorId = data.manaDoctorId;
						this.setButton(["save", "print"], true);
						var initData = {};
						this.initFormData(this.castListDataToForm(data,
								this.schema));
					} else {
						this.empiId = "";
						this.phrId = "";
						this.manaDoctorId = "";
						this.setButton(["save", "print"], false);
						var consultationDate = this.form.getForm()
								.findField("consultationDate");
						consultationDate.setValue(this.mainApp.serverDate);
					}
				}, this);
				task.delay(500);
			},

			getSaveRequest : function(savaData) {
				savaData.empiId = this.empiId;
				savaData.phrId = this.phrId;
				return savaData;
			},

			afterSaveData : function(entryName, op, json, data) {
				this.setButton(["print"], true);
			},

			setButton : function(m, flag) {
				if (this.empiId
						&& this.phrId
						&& this.manaDoctorId != this.mainApp.uid
						&& (this.mainApp.jobId == "chis.01" || this.mainApp.jobId == "chis.05")) {
					Ext.Msg.alert("提示", "该病人责任医生非本人，不能新增接诊记录");
					flag = false;
				}
				var btns;
				var btn;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar();
				} else {
					btns = this.form.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}

			},

			createModule : function(moduleName, moduleId, exCfg) {
				var item = this.midiModules[moduleName]
				if (!item) {
					var moduleCfg = this.loadModuleCfg(moduleId);
					var cfg = {
						showButtonOnTop : true,
						border : false,
						frame : false,
						autoLoadSchema : false,
						isCombined : true,
						exContext : {}
					};
					Ext.apply(cfg, exCfg);
					Ext.apply(cfg, moduleCfg);
					var cls = moduleCfg.script;
					if (!cls) {
						return;
					}
					if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
						return;
					}
					$import(cls);
					item = eval("new " + cls + "(cfg)");
					item.setMainApp(this.mainApp);
					this.midiModules[moduleName] = item;
				}
				return item;
			},

			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id,
							method : "execute"
						})
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},

			onBeforePrint : function(type, pages, ids_str) {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先保存记录。");
					return;
				}
				pages.value = "chis.prints.template.consultation";
				ids_str.value = "&recordId=" + this.initDataId;
			}
		})