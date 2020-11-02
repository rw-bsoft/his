$package("chis.application.dc.script")
$import("chis.script.BizTableFormView")
chis.application.dc.script.RabiesRecordForm = function(cfg) {
	cfg.colCount = 3;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 160
	cfg.labelWidth = 100
	cfg.showButtonOnTop = true;
	cfg.autoLoadSchema = true;
	chis.application.dc.script.RabiesRecordForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("doNew", this.onDoNew, this);
	this.on("beforeSave", this.onbeforeSave, this)
}
Ext.extend(chis.application.dc.script.RabiesRecordForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dc.script.RabiesRecordForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var injuryAnimal = form.findField("injuryAnimal");
				if (injuryAnimal) {
					injuryAnimal.on("select", this.onInjuryAnimalChange, this);
					injuryAnimal.on("change", this.onInjuryAnimalChange, this);
				}
			},
			onbeforeSave : function(entryName, op, saveData) {
				saveData["lastModifyUser"] = this.mainApp.uid
				saveData["lastModifyDate"] = this.mainApp.serverDate
			},
			doClose : function() {
				if (!this.exContext.args.rabiesId) {
					return
				}
				Ext.Msg.show({
					title : '结案确认',
					msg : '结案操作后无法恢复，是否继续？',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.form.el.mask("正在保存数据...", "x-mask-loading")
							util.rmi.jsonRequest({
										serviceId : 'chis.rabiesRecordService',
										serviceAction : "logoutRabies",
										method : "execute",
										body : {
											recordId : this.exContext.args.rabiesId
										}
									}, function(code, msg, json) {
										this.form.el.unmask()
										if (code > 300) {
											this.processReturnMsg(code, msg);
											return
										} else {
											this
													.fireEvent(
															"close",
															this.exContext.args.rabiesId);
										}
									}, this);
						}
					},
					scope : this
				})
			},
			onDoNew : function() {
				this.data.empiId = this.exContext.ids.empiId;
				this.data.phrId = this.exContext.ids.phrId;
				this.data.manaUnitId = this.mainApp.deptId;
			},

			doAdd : function() {
				this.form.el.mask("正在查询数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : 'chis.rabiesRecordService',
							serviceAction : "whetherNeedRabiesRecord",
							method : "execute",
							empiId : this.exContext.ids.empiId
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg, this.doAdd,
										[data]);
								return
							}
							if (json.body) {
								var needCreate = json.body.needCreate;
								if (needCreate == 0) {
									this.initFormData(json.body);
									Ext.Msg
											.alert("提示信息",
													"当前还有狂犬病档案未结案，不允许新增！")
									return;
								} else if (needCreate == 1) {
									this.exContext.args.rabiesId = "";
									this.op = "create";
									this.fireEvent("add", this);
								} else
									Ext.Msg.alert("提示信息", json.body.message)
							}
						}, this)
			},
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData); // **
				// 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveRequest)) {
					return;
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.rabiesRecordService",
							serviceAction : "saveRabiesRecord",
							schema : "chis.application.dc.schemas.DC_RabiesRecord",
							method : "execute",
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
							Ext.apply(this.data, resBody);
							if (resBody) {
								this.initFormData(resBody);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.op = "update"
						}, this)
			},
			getSaveRequest : function(saveData) {
				var values = saveData;
				values.empiId = this.exContext.ids.empiId;
				values.phrId = this.exContext.ids.phrId;
				if (this.exContext.args.rabiesId) {
					values.rabiesId = this.exContext.args.rabiesId;
				}
				return values;
			},
			loadInitData : function() {

				if (this.form.getTopToolbar()) {
					var bts = this.form.getTopToolbar().items;
					bts.items[0].enable();
				}
				this.doNew();
				return this.initData;
			},
			doNew : function() {
				this.initData = {}
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm();
				form.reset();
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (!(arguments[0] == 1)) { // whether set defaultValue,
							// it will be setted when
							// there is no args.
							var dv = it.defaultValue;
							if (dv) {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof dv == 'string'
										&& dv.length > 10) {
									dv = dv.substring(0, 10);
								}
								f.setValue(dv);
							}
						}
						if (!it.update && !it.fixed && !it.evalOnServer) {
							f.enable();
						}
						f.validate();
						if (it.type == "date") {
							if (it.minValue) {
								f.setMinValue(it.minValue)
							} else {
								f.setMinValue(null)
							}
							if (it.maxValue) {
								f.setMaxValue(it.maxValue)
							} else {
								f.setMaxValue(null)
							}
						}
					}
				}
				this.setKeyReadOnly(false)
				this.startValues = form.getValues(true);
				this.fireEvent("doNew", this.form)
				this.focusFieldAfter(-1, 800)
				//this.afterDoNew();
				//this.resetButtons();
			},
			onLoadData : function() {
				var form = this.form.getForm();
				var injuryAnimal = form.findField("injuryAnimal");
				var others = form.findField("others");
				if (injuryAnimal) {
					if (injuryAnimal.getValue() == 3) {
						others.enable();
					} else {
						others.disable();
					}
				}
			},
			onInjuryAnimalChange : function(r) {
				var value = r.getValue()
				var form = this.form.getForm();
				var others = form.findField("others");
				if (value == 3) {
					others.enable();
				} else {
					others.reset();
					others.disable();
				}
			},
			loadData : function() {
				if (!this.exContext.args.rabiesId) {
					return;
				}
				this.form.el.mask("正在加载数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : 'chis.rabiesRecordService',
							serviceAction : "loadRabiesRecord",
							method : "execute",
							schema : this.entryName,
							pkey : this.exContext.args.rabiesId
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData);
								return;
							}
							this.doNew();
							if (json.body) {
								this.initFormData(json.body);
								this.fireEvent("loadData", this.entryName,
										this.op, json, this.data);
								this.validate();
							}
							this.op = "update";
						}, this);
			},

			initFormData : function(data) {
				this.beforeInitFormData(data);
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v)
						}
						if (this.initDataId) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			}
		});