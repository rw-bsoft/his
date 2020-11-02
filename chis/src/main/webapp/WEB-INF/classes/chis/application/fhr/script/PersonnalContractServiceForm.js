$package("chis.application.fhr.script")

$import("chis.script.modules.form.FieldSetFormView")

chis.application.fhr.script.PersonnalContractServiceForm = function(cfg) {
	this.empiId = cfg.empiId;
	this.record = cfg.record;
	this.personGroup = this.record.get("FS_PersonGroup");
	this.service = this.record.get("FS_Kind");
	cfg.showButtonOnTop = true;
	cfg.colCount = 4;
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.fhr.script.PersonnalContractServiceForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.PersonnalContractServiceForm,
		chis.script.modules.form.FieldSetFormView, {
			loadData : function() {
				this.FS_PersonGroup = this.record.get("FS_PersonGroup");
				this.FS_Kind = this.record.get("FS_Kind");
			},
			setPersonGroupStatus : function() {
				if (this.formFields["notKeyPerson"].getValue() == true) {
					for (var i = 1; i < this.items.length; i++) {
						var it = this.items[i];
						if (it.id == "healthRecord")
							break;
						this.formFields[it.id].setValue(false)
					}
					this.formFields["notKeyPerson"].setValue(true)
				}
			},
			setNotKeyPersonStatus : function(checkbox, checked) {
				if (checked == true) {
					if (checkbox.getName() == "oldPerson") {
						this.formFields["pregnantWomen"].setValue(false);
						this.formFields["baby"].setValue(false);
						this.formFields["notKeyPerson"].setValue(false);
					} else if (checkbox.getName() == "pregnantWomen") {
						this.formFields["oldPerson"].setValue(false);
						this.formFields["baby"].setValue(false);
						this.formFields["notKeyPerson"].setValue(false);
					} else if (checkbox.getName() == "baby") {
						this.formFields["oldPerson"].setValue(false);
						this.formFields["pregnantWomen"].setValue(false);
						this.formFields["notKeyPerson"].setValue(false);
					} else if (checkbox.getName() == "activeClass1") {
						this.formFields["activeClass2"].setValue(false);
						this.formFields["activeClass3"].setValue(false);
					} else if (checkbox.getName() == "activeClass2") {
						this.formFields["activeClass1"].setValue(false);
						this.formFields["activeClass3"].setValue(false);
					} else if (checkbox.getName() == "activeClass3") {
						this.formFields["activeClass2"].setValue(false);
						this.formFields["activeClass1"].setValue(false);
					} else if (checkbox.getName() == "slowDisease"
							|| checkbox.getName() == "disabledPerson"
							|| checkbox.getName() == "mentalDisease") {
						this.formFields["notKeyPerson"].setValue(false);
					} else if (checkbox.getName() == "otherService") {
						this.formFields["otherServiceSelect"].enable()
					}
				} else {
					if (checkbox.getName() == "otherService") {
						this.formFields["otherServiceSelect"].clearValue()
						this.formFields["otherServiceSelect"].disable()
					}
				}
			},
			onReady : function() {
				chis.application.fhr.script.PersonnalContractServiceForm.superclass.onReady
						.call(this);
				var items = this.schema.items;
				this.items = items;
				this.formc = this.form.getForm();
				var notKeyPerson = this.formc.findField("notKeyPerson");
				this.formFields = {}
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.id == "notKeyPerson") {
						this.formFields[it.id] = this.formc.findField(it.id);
						this.formFields[it.id].on("check",
								this.setPersonGroupStatus, this);
						continue;
					}
					this.formFields[it.id] = this.formc.findField(it.id);
					if (it.id == "activeClass")
						continue;
					this.formFields[it.id].on("check",
							this.setNotKeyPersonStatus, this);
				}
				if ((this.FS_PersonGroup == null || this.FS_PersonGroup == "")
						&& (this.FS_Kind == null || this.FS_Kind == "")) {
					this.FS_Kind = "";
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.familyRecordService",
								serviceAction : "getPersonGroup",
								method : "execute",
								empiId : this.empiId
							})
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg);
						return
					}
					this.FS_PersonGroup = result.json.personGroup
				}
				var persongroup = "", person, fskind = "", kind;
				if (this.FS_PersonGroup.indexOf(",") < 0) {
					persongroup = this.FS_PersonGroup;
				} else {
					person = this.FS_PersonGroup.split(",");
				}
				if (this.FS_Kind.indexOf(",") < 0) {
					fskind = this.FS_Kind;
				} else {
					if (this.FS_Kind.indexOf(":") < 0)
						kind = this.FS_Kind.split(",");
					else {
						var kinds = this.FS_Kind.substring(0, this.FS_Kind
										.indexOf(":"))
						kind = kinds.split(",");
						this.otherKind = this.FS_Kind.substring(this.FS_Kind
								.indexOf(":")
								+ 1);
						this.formc.findField("otherServiceSelect")
								.setValue(this.FS_Kind.substring(this.FS_Kind
										.indexOf(":")
										+ 1))
					}
				}
				var m, n;
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (this.FS_PersonGroup.indexOf(",") < 0) {
						if (persongroup == it.alias) {
							this.formc.findField(it.id).setValue(true)
						}
					} else {
						for (m = 0; m < person.length; m++) {
							if (person[m] == it.alias) {
								this.formc.findField(it.id).setValue(true)
							}
						}
					}
					if (this.FS_Kind.indexOf(",") < 0) {
						if (fskind == it.alias) {
							this.formc.findField(it.id).setValue(true)
						}
					} else {
						for (m = 0; m < kind.length; m++) {
							if (kind[m] == it.alias) {
								this.formc.findField(it.id).setValue(true)
							}
						}
					}
				}
			},

			saveToServer : function(saveData) {
				var items = this.schema.items;
				var n = items.length;
				if (saveData["healthRecord"] == false
						|| saveData["healthEval"] == false
						|| saveData["activeSend"] == false
						|| saveData["activeTell"] == false) {
					Ext.Msg.alert("提示", "服务项目中的前四项未填选完整");
					return;
				}
				if ((saveData["slowDisease"] == true && saveData["activeClass3"] == false)
						|| (saveData["mentalDisease"] == true && saveData["activeClass3"] == false)) {
					Ext.Msg.alert("提示",
							"人群分类中[慢性病患者]和[重性精神病患者]必须与服务项目中的[5.3]对应");
					return;
				}
				if (saveData["activeClass3"] == false) {
					if ((saveData["oldPerson"] == true && saveData["activeClass2"] == false)
							|| (saveData["pregnantWomen"] == true && saveData["activeClass2"] == false)
							|| (saveData["baby"] == true && saveData["activeClass2"] == false)
							|| (saveData["disabledPerson"] == true && saveData["activeClass2"] == false)) {
						Ext.Msg
								.alert("提示",
										"人群分类中[大于或等于65岁老人][孕产妇][婴幼儿][残疾人群]必须与服务项目中的[5.2]对应");
						return;
					}
				}
				if (saveData["activeClass2"] == false) {
					if (saveData["notKeyPerson"] == true
							&& saveData["activeClass1"] == false) {
						Ext.Msg.alert("提示", "人群分类中[非重点人群]必须与服务项目中的[5.1]对应");
						return;
					}
				}
				var personGroup = "", serviceContent = "";
				for (var i = 0; i < n; i++) {
					var it = items[i];
					if (saveData[it.id]) {
						if (it.group == "人群分类") {
							personGroup += it.alias + ",";
						} else if (it.group == "服务项目") {
							if (it.id == "otherServiceSelect") {
								serviceContent = serviceContent.substring(0,
										serviceContent.length - 1);
								serviceContent += ":"
										+ this.form
												.getForm()
												.findField("otherServiceSelect")
												.getValue() + ",";
							} else {
								serviceContent += it.alias + ",";
							}
						}
					}
				}
				var returnData = {}
				if (personGroup != "") {
					returnData["personGroup"] = personGroup.substring(0,
							personGroup.length - 1);
				} else {
					returnData["personGroup"] = personGroup;
				}
				if (serviceContent != "") {
					returnData["serviceContent"] = serviceContent.substring(0,
							serviceContent.length - 1);
				} else {
					returnData["serviceContent"] = serviceContent;
				}
				var listdata = []
				debugger;
				var needsave=this.record.data;
				needsave.FS_PersonGroup=returnData.personGroup;
				needsave.FS_Kind=returnData.serviceContent;
				listdata.push(needsave);
				var r = util.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecordService",
							serviceAction : "savePsersonnalContract",
							method: "execute",
							body : listdata					
						});
				if (r.code > 300) {
					MyMessageTip.msg("提示", r.msg, true);
					return;
				}
				this.fireEvent("save", returnData);
				this.getWin().hide();
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
				if (it.xtype == "checkbox") {
					cfg.hideLabel = true;
					cfg.boxLabel = it.alias;
				} else if (it.xtype == "label") {
					cfg.text = it.alias;
					cfg.style = "color:red"
					cfg.height = 80;
				} else {
					cfg.fieldLabel = it.alias;
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					scope : this
				}
				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it['not-null']) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.boxLabel = "<span style='color:red'>" + cfg.boxLabel
							+ "</span>"
				}
				if (it['showRed']) {
					cfg.boxLabel = "<span style='color:red'>" + cfg.boxLabel
							+ "</span>"
				}
				if (it.fixed || it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					cfg.disabled = true
				}
				if (it.dic) {
					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					var combox = this.createDicField(it.dic)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.xtype) {
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						break;
				}
				return cfg;
			}
		});