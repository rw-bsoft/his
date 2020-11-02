$package("chis.application.mhc.script.dea")

$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")

chis.application.mhc.script.dea.PrenatalDeathReportForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.labelWidth = 120;
	cfg.fldDefaultWidth = 200;
	cfg.colCount = 3;
	chis.application.mhc.script.dea.PrenatalDeathReportForm.superclass.constructor
			.apply(this, [cfg]);
	this.getButtonStatus = function(btn) {
		if (!this.exContext.control) {
			return undefined;
		}
		var group = btn.prop.group;
		if (!group) {
			return true;
		}
		if (group == "create||update") {
			if (!this.initDataId) {
				return this.exContext.control["create"];
			} else {
				return this.exContext.control["update"];
			}
		} else {
			return this.exContext.control[group];
		}
	}
	this.on("beforeSave", this.beforeSave, this);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("changeDic", this.onChangeDic, this)
	this.on("loadData", this.onLoadData, this)
	this.on("beforePrint", this.onBeforePrint, this)
}

Ext.extend(chis.application.mhc.script.dea.PrenatalDeathReportForm,
		chis.script.BizTableFormView, {
			initPanel : function(sc) {

				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : '2'
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {

						continue;
					}

					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "permanentRegionCode") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										fieldLabel : "<font  color=red>常住地址:<font>",
										"width" : 184

									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}
							if (it.id == "temporaryRegionCode") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										fieldLabel : "<font  color=red>暂住地址:<font>",
										"width" : 184

									});
							this.ff1 = ff1;
							this.ff1.allowBlank = false;
							this.ff1.invalidText = "必填字段";
							this.ff1.regex = /(^\S+)/;
							this.ff1.regexText = "前面不能有空格字符";
							table.items.push(ff1)
							continue;

						}
					}
					var f = this.createField(it)
					f.index = i;

					f.anchor = it.anchor || "100%"

					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					table.items.push(f)
				}

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onRegionCodeClick : function(r) {
			
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				m.areaGridListId = r;
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},
			onQd : function(data) {
				if ("permanentRegionCode" == data.areaGridListId) {
				this.form.getForm().findField("permanentRegionCode")
						.setValue(data.regionCode_text);
				this.data.permanentRegionCode_text = data.regionCode_text;
				this.data.permanentRegionCode = data.regionCode;

				}
				if ("temporaryRegionCode" == data.areaGridListId) {
				this.form.getForm().findField("temporaryRegionCode")
						.setValue(data.regionCode_text);
				this.data.temporaryRegionCode_text = data.regionCode_text;
				this.data.temporaryRegionCode = data.regionCode;

				}
				

			},
			initFormData : function(data) {
				chis.application.mhc.script.dea.PrenatalDeathReportForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (data.permanentRegionCode) {
						if (data.permanentRegionCode.key) {

							this.form.getForm()
									.findField("permanentRegionCode")
									.setValue(data.permanentRegionCode.text);
//							this.form.getForm()
//									.findField("permanentRegionCode").disable();
							this.data.permanentRegionCode = data.permanentRegionCode.key;
							this.data.permanentRegionCode_text = data.permanentRegionCode.text;
						}
					}
						if (data.temporaryRegionCode) {
						if (data.temporaryRegionCode.key) {

							this.form.getForm()
									.findField("temporaryRegionCode")
									.setValue(data.temporaryRegionCode.text);
//							this.form.getForm()
//									.findField("permanentRegionCode").disable();
							this.data.temporaryRegionCode = data.temporaryRegionCode.key;
							this.data.temporaryRegionCode_text = data.temporaryRegionCode.text;
						}
					}
				}
			},
			getAreaGridItems : function() {
				return ["permanentRegionCode", "temporaryRegionCode",
						"registerRegionCode"];
			},
			beforeSave : function(e, o, s) {
				s["phrId"] = this.exContext.ids["phrId"];
			},
			onChangeDic : function(items) {
				var fields = [{
							"type" : "string",
							"length" : "20",
							"acValue" : "1111",
							"id" : "registerRegionCode_Text",
							"alias" : "户口地址",
							"xtype" : "textfield",
							"display" : 2
						}];
				for (var i = 0; i < fields.length; i++) {
					var field = fields[i];
					var exists = this.findSchemaItemExists(this.schema, field);
					if (!exists) {
						items.splice(16, 0, field);
					}
				}
				return true;
			},

			onBeforeCreate : function() {
				util.rmi.jsonRequest({
							serviceId : "chis.deathReportCardService",
							serviceAction : "loadInitPreDeaRepData",
							method : "execute",
							body : {
								"empiId" : this.exContext.ids.empiId,
								"schema" : this.entryName
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (!body) {
								return;
							}
							this.initDataId = body.cardId;
							var initData = body.initData;
							if (initData) {
								this.initFormData(initData);
								var register = initData.register;
								if (register && register.key == '1') {
									this.changeFieldState(true,
											"temporaryRegionCode");
								}
							}
						}, this)
			},

			onLoadData : function(entryName, data) {
				var register = data["register"]
				if (register) {
					var disable = true;
					if (register.key != "1") {
						disable = false;
					}
					this.changeFieldState(disable, "temporaryRegionCode");
				}

				var prenatalScreen = data["prenatalScreen"]
				if (prenatalScreen) {
					var disable = true;
					if (prenatalScreen.key != "n") {
						disable = false;
					}
					this.changeFieldState(disable, "firstCheckWeeks");
					this.changeFieldState(disable, "checkingTimes");
				}
			},

			onReady : function() {
				chis.application.mhc.script.dea.PrenatalDeathReportForm.superclass.onReady
						.call(this);

				var form = this.form.getForm();

				var deadTime = form.findField("deadTime");
				if (deadTime) {
					var serverDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					deadTime.setMinValue(this.exContext.args.birthday);
					deadTime.on("blur", this.getDeadYear, this);
				}

				var inputDate = form.findField("inputDate");
				if (inputDate) {
					var serverDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					inputDate.setMaxValue(serverDate);
				}

				var createPerson = form.findField("createPerson");
				if (createPerson) {
					createPerson.tree.on("beforeexpandnode", function(node) {
								createPerson.tree.filter.filterBy(
										this.filterReportUser, this);
							}, this);
					createPerson.on("select", this.changeManaUnit, this);
				}

				var prenatalScreen = form.findField("prenatalScreen");
				if (prenatalScreen) {
					prenatalScreen
							.on("select", this.selectPrenatalScreen, this);
				}

				var register = form.findField("register");
				if (register) {
					register.on("select", this.selectRegister, this);
				}
			},

			selectRegister : function(field) {
				var value = field.getValue();
				var form = this.form.getForm();
				var temporaryRegionCode = form.findField("temporaryRegionCode");
				if (value == '1') {
					temporaryRegionCode.disable();
				} else {
					temporaryRegionCode.enable();
				}
			},

			selectPrenatalScreen : function(field) {
				var value = field.getValue();
				var disable = true;
				if (value == 'y') {
					disable = false;
				}
				this.changeFieldState(disable, "firstCheckWeeks");
				this.changeFieldState(disable, "checkingTimes");
			},

			getSaveRequest : function(saveData) {
				saveData.pregnantId = this.exContext.ids['MHC_PregnantRecord.pregnantId'];
				saveData.empiId = this.exContext.ids.empiId;
				saveData.status = 0;
				return saveData;
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"empiId" : this.exContext.ids['empiId'],
					"schema" : this.entryName
				};
			},

			filterReportUser : function(node) {
				var folder = node.attributes.folder;
				if (folder) {
					var key = node.attributes.key;
					if (key != this.mainApp.deptId) {
						return false;
					} else {
						return true;
					}
				} else {
					return true;
				}
			},

			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["b.manaUnitId"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("createUnit");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				combox.setValue(manageUnit)
				combox.disable();
			},

			getDeadYear : function(field) {
				var deadDate = field.getValue();
				if (deadDate) {
					var arry = this.exContext.empiData.birthday.split('-');
					var birthday = new Date(arry[1] + '/' + arry[2] + '/'
							+ arry[0]);
					var diffTime = chis.script.util.helper.Helper
							.getPreciseAge(birthday, deadDate);
					this.form.getForm().findField("age").setValue(diffTime);
				}
			},

			doSave : function() {
				var name = this.exContext.empiData.personName;
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				if (!this.initDataId) {
					Ext.Msg.show({
								title : '确认建立[' + name + ']的死亡报告卡',
								msg : '建立死亡报告卡会同时注销[' + name
										+ ']的所有档案,且其相关信息均不可操作,是否继续?',
								modal : false,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.saveToServer(values);
										this
												.fireEvent("refreshEhrView",
														"G_01");
										this
												.fireEvent("refreshEhrView",
														"G_16");
									}
								},
								scope : this
							})
				} else {
					this.saveToServer(values);
				}

			},
			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.deathReport"];
				ids_str.value = "&empiId=" + this.exContext.ids.empiId;
				return true;
			},
			getFormData : function() {
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext);
				if (items) {
					var form = this.form.getForm();
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("permanentRegionCode" == it.id) {
								v = this.data.permanentRegionCode;
							}

						}

						if (v == null || v === "") {
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}

				return values;
			}

		});