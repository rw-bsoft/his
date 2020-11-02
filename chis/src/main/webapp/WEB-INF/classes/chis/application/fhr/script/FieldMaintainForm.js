$package("chis.application.fhr.script")
$import("chis.script.BizTableFormView")

chis.application.fhr.script.FieldMaintainForm = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.fhr.script.FieldMaintainForm.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.fhr.script.FieldMaintainForm,
		chis.script.BizTableFormView, {
			onDefaultTypeChange : function(f) {
				var form = this.form.getForm();
				var defaultType = form.findField("defaultType");
				var defaultValue = form.findField("defaultValue");
				var type = form.findField("type");
				var typeValue = type.getValue();
				var dicRender = form.findField("dicRender");
				var dicValue = dicRender.getValue();
				var value = defaultType.getValue();
				var length = form.findField("length");
				if (value == "1") {
					if (typeValue != "string" || dicValue != "0") {
						Ext.Msg.alert("提示",
								"当默认值类型为“当前登录人”时，类型必须为“字符串”，是否字典必须为“非字典”。");
					} else {
						defaultValue.setValue("%user.userName");
						defaultValue.disable();
						length.setValue(20);
						length.disable();
						return;
					}
				} else if (value == "2") {
					if (typeValue != "string" || dicValue != "0") {
						Ext.Msg.alert("提示",
								"当默认值类型为“当前登录机构”时，类型必须为“字符串”，是否字典必须为“非字典”。");
					} else {
						defaultValue.setValue("%user.manageUnit.name");
						defaultValue.disable();
						length.setValue(50);
						length.disable();
						return;
					}
				} else if (value == "3") {
					if (typeValue != "date" || dicValue != "0") {
						Ext.Msg.alert("提示",
								"当默认值类型为“当前登录日期”时，类型必须为“日期”，是否字典必须为“非字典”。");
					} else {
						defaultValue.setValue("%server.date.date");
						defaultValue.disable();
						length.setValue(20);
						length.disable();
						return;
					}
				} else if (value == "4") {
					if (typeValue != "datetime" || dicValue != "0") {
						Ext.Msg.alert("提示",
								"当默认值类型为“当前登录时间”时，类型必须为“时间”，是否字典必须为“非字典”。");
					} else {
						defaultValue.setValue("%server.date.today");
						defaultValue.disable();
						length.setValue(20);
						length.disable();
						return;
					}
				}
				if (value != "9") {
					defaultType.setValue();
					return true;
				}
				defaultValue.enable();
				length.enable();
				if (f) {
					defaultValue.setValue();
					length.setValue();
				}
			},
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData);
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					Ext.Msg.alert("提示", "字段" + saveRequest.alias + "的代码["
									+ saveRequest.id + "]已存在！")
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.templateService",
							serviceAction : "saveFieldMaintain",
							method : "execute",
							schema : this.entryName,
							op : this.op,
							body : saveRequest
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							var resBody = json.body;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData], resBody);
								this
										.fireEvent("exception", code, msg,
												saveData);
								return;
							}
							Ext.apply(this.data, saveData);
							if (resBody) {
								this.initFormData(resBody);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.afterSaveData(this.entryName, this.op, json,
									this.data);
							this.op = "update"
						}, this)
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
				this.fireEvent("cancel");
			},
			onReady : function() {
				chis.application.fhr.script.FieldMaintainForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var type = form.findField("type");
				if (type) {
					type.on("select", this.onSelectType, this);
				}
				var id = form.findField("id");
				if (id) {
					id.on("blur", this.onIdChange, this);
				}
				var defaultType = form.findField("defaultType");
				if (defaultType) {
					defaultType.on("change", this.onDefaultTypeChange, this);
					defaultType.on("select", this.onDefaultTypeChange, this);
				}
			},
			doSave : function() {
				if (this.onDefaultTypeChange()) {
					return;
				}
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);

				this.canChangeDic(values);
			},
			getHasListInfo : function(values) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "getHasListInfo",
							method : "execute",
							body : values
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.onBeforeSave);
					return;
				}
				this.hasListInfo = result.json.hasListInfo;
			},
			canChangeDic : function(values) {
				var form = this.form.getForm();
				var dicRender = form.findField("dicRender");
				var value = dicRender.getValue();
				if ((this.dicValue == "1" || this.dicValue == "2")
						&& value == "0") {
					this.getHasListInfo(values);
					if (!this.hasListInfo) {
						this.saveToServer(values);
						return;
					}
					Ext.Msg.show({
								title : '提示',
								msg : '保存该记录的同时将删除其关联的值域信息，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.removeDicByFieldId();
										this.saveToServer(values);
									}
								},
								scope : this
							})
				} else {
					this.saveToServer(values);
				}
			},
			removeDicByFieldId : function() {
				util.rmi.jsonRequest({
							serviceId : "chis.templateService",
							serviceAction : "removeDicByFieldId",
							method : "execute",
							fieldId : this.initDataId
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.removeDicByFieldId);
								return;
							}
						}, this)
			},
			onSelectType : function(field) {
				var value = field.getValue();
				var form = this.form.getForm();
				var length = form.findField("length");
				length.enable();
				if (value == "date" || value == "datetime") {
					length.setValue(20);
					length.disable();
				} else {
					length.setValue();
				}
			},
			onIdChange : function(field) {
				var value = field.getValue();
				field.clearInvalid();
				if (!/^\w+$/.test(value)) {
					field.markInvalid("字段代码必须是字母或数字！");
				}
			},
			setFormButton : function() {
				var btns = this.form.getTopToolbar().items;
				var n = btns.getCount()
				var btn = btns.item(0);
				if (this.hasUsed) {
					btn.disable();
				} else {
					btn.enable();
				}
			}
		});