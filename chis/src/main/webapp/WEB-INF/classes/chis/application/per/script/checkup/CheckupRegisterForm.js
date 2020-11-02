$package("chis.application.per.script.checkup");

$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper");

chis.application.per.script.checkup.CheckupRegisterForm = function(cfg) {
	cfg.colCount = "4";
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.labelWidth = 80;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	chis.application.per.script.checkup.CheckupRegisterForm.superclass.constructor
			.apply(this, [cfg]);
	this.loadServiceId = "chis.checkupRecordService";
	this.loadAction = "initPerRegisterForm";
	this.finalCheckAction = "finalCheck";
	this.printurl = chis.script.util.helper.Helper.getUrl();
	this.on("loadData", this.onLoadData, this);
	this.on("doNew", this.onDoNew, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupRegisterForm,
		chis.script.BizTableFormView, {
			doCreate : function() {
				this.fireEvent("create");
				this.setButtonsState(['finalCheck', 'revoke', 'printCheckup'],
						false);
				this.setButtonsState(['create', 'save'], true);
				this.getPastHistoryAndAllergy();
				this.initDataId = "";
				this.doNew();
			},
			doNew : function() {
				this.op = "create";
				if (this.data) {
					this.data = {};
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm();
				var items = this.schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var f = form.findField(it.id);
					if (f) {
						f.setValue(it.defaultValue);
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") {
							if (it.minValue) {
								f.setMinValue(it.minValue);
							} else {
								f.setMinValue(null);
							}
							if (it.maxValue) {
								f.setMaxValue(it.maxValue);
							} else {
								f.setMaxValue(null);
							}
						}
					}
				}
				this.setKeyReadOnly(false);
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew");
				this.focusFieldAfter(-1, 800);
				this.validate();

				this.fireEvent("bady", "remove", true);
				// this.initDataId = this["PER_CheckupRegister.checkupNo"];

				this.data["phrId"] = this.exContext.ids.phrId;
				this.data["empiId"] = this.exContext.empiData.empiId;
				this.data["name"] = this.exContext.empiData.personName;
				this.data["mobileNumber"] = this.exContext.empiData.mobileNumber;
				this.data["birthday"] = this.exContext.empiData.birthday;
				this.data["idCard"] = this.exContext.empiData.idCard;
				this.data["zipCode"] = this.exContext.empiData.zipCode;
				this.data["address"] = this.exContext.empiData.address;
				this.data["phoneNumber"] = this.exContext.empiData.contactPhone;
				this.data["sex"] = this.exContext.empiData.sexCode;

				var checkupOrgan = this.form.getForm()
						.findField("checkupOrganization");
				if (checkupOrgan) {
					if (this.mainApp.deptId.length < 9) {
						checkupOrgan.enable();
					} else {
						checkupOrgan.disable();
					}
				}

				this.validate();
			},
			// 从主档默认既往史和过敏史
			// 既往史取疾病史
			// 过敏史取药物过敏史
			getPastHistoryAndAllergy : function() {
				if (this.exContext.args.empiId) {
					var form = this.form.getForm();
					util.rmi.jsonRequest({
								serviceId : 'chis.simpleQuery',
								schema : "chis.application.hr.schemas.EHR_PastHistory",
								method : "execute",
								queryCndsType : "",
								empiId : this.exContext.args.empiId,
								cnd : ['eq', ['$', 'a.empiId'],
										['s', this.exContext.args.empiId]]
							}, function(code, msg, json) {
								if (code > 300) {
									this.processReturnMsg(code, msg);
									return
								}

								if (json.body) {

									var total = json.totalCount;
									if (total > 0) {
										var data = json.body;
										var pastHistory = "";
										var allergy = "";
										for (var i = 0; i < total; i++) {
											// 药物过敏史
											if (data[i].pastHisTypeCode == "03") {
												allergy = allergy
														+ data[i].diseaseText
														+ ",";

											}
											// 疾病史
											if (data[i].pastHisTypeCode == "05") {
												pastHistory = pastHistory
														+ data[i].diseaseText
														+ ",";
											}
										}

										allergy = allergy.substr(0,
												allergy.length - 1);
										pastHistory = pastHistory.substr(0,
												pastHistory.length - 1);
										var pastHistoryFiled = form
												.findField("pastHistory");
										var allergyFiled = form
												.findField("checkupAllergy");
										if (pastHistoryFiled) {
											pastHistoryFiled
													.setValue(pastHistory);
										}
										if (allergyFiled) {
											allergyFiled.setValue(allergy);
										}
									}
								}
							}, this);
				}
			},

			// 数据填充完后判定体检明细是否存在,如果已经存在则不能修改体检套餐
			onLoadData : function(entryName, body) {
				this.beforeInitFormData(body);// 重置权限-击左边列表时
				this.validate();
				this.initDataId = this.exContext.args.initDataId;
				var checkupNo = body.checkupNo || this.initDataId;
				var checkupType = this.form.getForm().findField("checkupType");
				var cnds = ['eq', ['$', 'checkupNo'], ['s', checkupNo]];
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							method : "execute",
							schema : "chis.application.per.schemas.PER_CheckupDetail",
							cnd : cnds
						});
				var resultBody = result.json.body;
				if (!resultBody || resultBody.length < 1) {
					checkupType.enable();
				} else {
					checkupType.disable();
				}

				var checkupSymptom = body.checkupSymptom;
				if (checkupSymptom) {
					this.removecheckupSymptomField(checkupSymptom.key);
				}

				var totalCheckupDate = body.totalCheckupDate;
				var checkupType = this.form.getForm().findField("checkupType");
				this.setButtonsState(['printCheckup'], true);
				var status = body.status.key;
				if (status == '1') {
					this.setButtonsState(['revoke', 'save', 'finalCheck'],
							false);
					if (this.ehrStatus == '1') {
						this.setButtonsState(['create'], false);
					} else {
						this.setButtonsState(['create'], true);
					}
				} else {
					this
							.setButtonsState(['revoke', 'save', 'finalCheck'],
									true);
					if (totalCheckupDate) {
						this.setButtonsState(['revoke'], true);
						this.setButtonsState(['save', 'finalCheck'], false);
						checkupType.disable();
					} else {
						this.setButtonsState(['revoke'], false);
						this.setButtonsState(['save', 'finalCheck'], true);
						checkupType.enable();
					}
					if (this.ehrStatus == '1') {
						this.setButtonsState(['revoke', 'save', 'finalCheck',
										'create'], false);
					} else {
						this.setButtonsState(['create'], true);
					}
				}
			},
			onDoNew:function(){
				if (this.ehrStatus == '1') {
					this.setButtonsState(['revoke', 'save', 'finalCheck',
									'create'], false);
				} else {
					this.setButtonsState(['create'], true);
				}
			}
			,
			doSave : function() {
				var checkupId = this.form.getForm().findField("checkupId");
				if (!checkupId)
					return;
				var checkId = checkupId.getValue();
				if (!checkId) {
					chis.application.per.script.checkup.CheckupRegisterForm.superclass.doSave
							.call(this);
					return;
				}
				var checkupOrgan = this.form.getForm()
						.findField("checkupOrganization");
				if (!checkupOrgan)
					return;
				var checkUnit = checkupOrgan.getValue();
				var checkUnitText = checkupOrgan.getRawValue();
				if (!checkUnit) {
					Ext.Msg.alert("错误", "体检单位不能为空!");
					return;
				}

				var cnd = ['and',
						['eq', ['$', 'checkupOrganization'], ['s', checkUnit]],
						['eq', ['$', 'checkupId'], ['s', checkId]]];
				var secCnd;
				if (this.initDataId) {
					secCnd = ['ne', ['$', 'checkupNo'], ['s', this.initDataId]];
				}
				var cnds;
				if (secCnd) {
					cnds = ['and', secCnd, cnd];
				} else {
					cnds = cnd;
				}

				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.simpleQuery",
					schema : "chis.application.per.schemas.PER_CheckupRegister",
					method : "execute",
					cnd : cnds
				});
				if (result.json.body.length > 0) {
					Ext.Msg.alert("体检编号重复", "[" + checkUnitText + "]已经存在体检编号["
									+ checkId + "]!");
					return;
				}
				chis.application.per.script.checkup.CheckupRegisterForm.superclass.doSave
						.call(this);
			},
			saveToServer : function(saveData) {
				saveData.phrId = this.exContext.ids.phrId;
				this.fireEvent("save", this.entryName, this.op, saveData);
			},
			doFinalCheck : function() {
				Ext.Msg.show({
							title : '确认总检[' + this.initDataId + ']',
							msg : '体检记录总检后将无法操作体检信息，确定是否继续？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									var date = this.form.getForm()
											.findField("totalCheckupDate");
									if (date) {
										this.finalCheckSave();
									}
								}
							},
							scope : this
						});
			},
			finalCheckSave : function() {
				var body = {};
				body.checkupNo = this.initDataId;
				body.empiId = this.exContext.args.empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.finalCheckAction,
							method : "execute",
							op : "update",
							body : body
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								this.initFormData(json.body);
								this.fireEvent("finalCheck", this.entryName,
										"update", json, body, this);
							}
						}, this);
			},
			doRevoke : function() {
				var module = this.midiModules["revokeModule"];
				if (!module) {
					var moduleCfg = this.loadModuleCfg(this.refRevokeModule);
					var cfg = {
						title : "总检撤销",
						mainApp : this.mainApp,
						closeAction : "hide",
						inited : false,
						showButtonOnTop : true,
						disablePagingTbr : true,
						checkupNo : this.initDataId,
						empiId : this.exContext.args.empiId,
						initDataId : null
					};
					Ext.apply(cfg, moduleCfg);
					delete cfg.id;
					var cls = moduleCfg.script;
					$require(cls, [function() {
								module = eval("new " + cls + "(cfg)");
								module
										.on("recordSave", this.onRevokeSave,
												this);
								this.midiModules["revokeModule"] = module;
								var win = module.getWin();
								win.setPosition(300, 200);
								win.show();
							}, this]);
				} else {
					module.checkupNo = this.initDataId;
					module.empiId = this.exContext.args.empiId;
					module.initDataId = null;
					var win = module.getWin();
					win.setPosition(300, 200);
					win.show();
				}

			},
			doPrintCheckup : function() {
				if (!this.initDataId) {
					return
				}
				var url = "resources/chis.prints.template.physicalExamination.print?type=" + 1 + "&checkupNo="
						+ this.initDataId + "&empiId="
						+ this.exContext.args.empiId;
				url += "&temp=" + new Date().getTime();
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");

				if (Ext.isIE6) {
					win.print();
				} else {
					win.onload = function() {
						win.print();
					};
				}
			},
			onRevokeSave : function(entryName, op, json, body) {
				this.setButtonsState(['revoke'], false);
				this.setButtonsState(['save', 'finalCheck'], true);
				var date = this.form.getForm().findField("totalCheckupDate");
				date.setValue();
				this.fireEvent("formReadOnly", this.initDataId, this);
				this.fireEvent("revokeSave", entryName, op, json, body);
			},

			checkOrgan : function(node, comb) {
				var key = node.attributes['key'];
				if (key.length == 9)
					return true;
				else
					return false;
			},
			onReady : function() {
				chis.application.per.script.checkup.CheckupRegisterForm.superclass.onReady
						.call(this);
				var checkupType = this.form.getForm().findField("checkupType");
				if (checkupType) {
					checkupType.on("select", this.getComboName, this);
					checkupType.on("change", this.getComboName, this);
				}

				var checkupOrgan = this.form.getForm()
						.findField("checkupOrganization");
				if (checkupOrgan) {
					checkupOrgan.on("beforeselect", this.checkOrgan, this);
				}
			},
			getComboName : function(field) {
				var value = field.getValue();
				if (!value) {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : "chis.simpleQuery",
							schema : "chis.application.per.schemas.PER_Combo",
							method : "execute",
							queryCndsType : "",
							cnd : ['eq', ['$', 'id'], ['s', value]]
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body && body.length > 0) {
								var comboName = body[0].comboName;
								var combo_text = body[0].comboName_text;
								var sexCode = this.exContext.empiData.sexCode;
								var age = this.exContext.empiData.age;
								var checkupType = this.form.getForm()
										.findField("checkupType");
								if (comboName == "05" && sexCode != 2) {// 类型为-妇女健康促进工程-个档应为女性
									checkupType.setValue();
									Ext.Msg
											.alert("提示",
													"个人健康档案性别为“男”，不能进行体检类型为“妇女健康促进工程”的套餐体检！");
									return;
								}
								var childrenRegisterAge = this.mainApp.exContext.childrenRegisterAge;
								if (age < childrenRegisterAge
										&& (comboName != "01"
												&& comboName != "04" && comboName != "99")) {
									checkupType.setValue();
									Ext.Msg.alert("提示", "本套餐不适合该年龄段！");
									return;
								}
								if (age >= childrenRegisterAge
										&& comboName == "01") {
									checkupType.setValue();
									Ext.Msg.alert("提示", "本套餐不适合该年龄段！");
									return;
								}
								var oldPeopleAge = this.mainApp.exContext.oldPeopleAge;
								if (age < oldPeopleAge && comboName == "03") {
									checkupType.setValue();
									Ext.Msg.alert("提示", "本套餐不适合该年龄段！");
									return;
								}

								this.form.getForm().findField("comboName")
										.setValue({
													key : comboName,
													text : combo_text
												});
							}
						}, this);
			},
			checkOrgan : function(comb, node) {
				var key = node.attributes['key'];
				if (key.length == 9) {
					return true;
				} else {
					return false;
				}
			},
			setButtonsState : function(m, enable) {
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
							(enable) ? btn.enable() : btn.disable();
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
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			}
		});