$package("chis.application.hc.script")
$import("util.Accredit", "chis.script.util.helper.Helper")
$import("app.modules.form.TableFormView")
chis.application.hc.script.HealthCheckForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.colCount = 4;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 115
	this.labelWidth = cfg.labelWidth | 90;
	this.value = cfg.value | 2
	this.other = [];
	chis.application.hc.script.HealthCheckForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.onLoadData, this);

}
Ext.extend(chis.application.hc.script.HealthCheckForm, app.modules.form.TableFormView, {
			onReady : function() {
				chis.application.hc.script.HealthCheckForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				for (var i = 0; i < this.other.length; i++) {
					var field = form.findField(this.other[i]);
					if (field) {
						field.on("select", this.onOthers, this)
						// field.on("keyup",this.onOthers,this)
						field.on("blur", this.onOthers, this)
					}
				}
			},
			onBeforeSave : function(entry, op, saveData) {
				saveData.empiId = this.empiId
				saveData.phrId = this.phrId
				saveData.healthCheck = this.healthCheck
			},
			doCreate : function() {
				this.fireEvent("doCreate", this);
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (btns.length > 2) {
					btns.item(2).disable();
				}
			},
			loadInitData : function() {
				this.doNew();
				return this.initData;
			},
			getDesc : function(f) {
				return f + "Desc";
			},
			onOthers : function(comb, r, index) {
				var value = comb.getValue();
				var combName = comb.name;
				var form = this.form.getForm();
				var others = form.findField(this.getDesc(combName))
				if (others)
					if (value.indexOf(this.value) != -1) {
						others.enable();
					} else {
						others.reset();
						others.disable();
					}
			},
			onLoadData : function(entryName, body) {
				for (var i = 0; i < this.other.length; i++) {
					var field = this.form.getForm().findField(this.other[i]);
					var fieldDecs = this.form.getForm().findField(this
							.getDesc(this.other[i]));
					if (field && fieldDecs) {
						if (field.getValue().indexOf(this.value) != -1) {
							fieldDecs.enable();
						} else {
							fieldDecs.disable();
						}
					}
				}
			},
			loadData : function() {
				if (!this.healthCheck) {
					return;
				}
				if (this.loading) {
					return;
				}
				if (!this.schema) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true;
				util.rmi.jsonRequest({
							serviceId : "chis.simpleQuery",
							schema : this.entryName,
							method:"execute",
							cnd : ["eq", ["$", "healthCheck"],
									["s", this.healthCheck]]
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask();
							}
							this.loading = false;
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData);
								return;
							}
							this.doNew()
							if (json.body[0]) {
								this.initFormData(json.body[0])
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}
						}, this)
			},

			initFormData : function(data) {
				Ext.apply(this.data, data);
				this.initDataId = this.data[this.schema.pkey];
				var form = this.form.getForm();
				var items = this.schema.items;
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = this.data[it.id]

						if (v) {
							if (it.dic && !(v.text)) {
								f.setValue({
											key : v,
											text : this.data[it.id + "_text"]
										})
							} else {
								f.setValue(v)
							}
						}
						if (it.update == false) {
							f.disable();
						}
					}
				}
				this.setKeyReadOnly(true);
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
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (it.dic && it.defaultValue) {
							this.initData[it.id + "_text"] = it.defaultValue.text;
							this.initData[it.id] = it.defaultValue.key;
						} else {
							this.initData[it.id] = it.defaultValue;
						}
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}
						if (it.type == "date") {
							if (it.minValue)
								f.setMinValue(it.minValue)
							else
								f.setMinValue(null)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
							else
								f.setMaxValue(null)
						}
					}
				}
				this.setKeyReadOnly(false);
				this.resetButtons(); // ** add by yzh **
				// ** 新增状态 打印不能用 add by CHENXR
				var btns = this.form.getTopToolbar().items;
				if (btns.length > 2) {
					if (btns.item(1).disabled == false) {
						btns.item(2).disable();
					}
				}
				this.fireEvent("doNew")
				this.focusFieldAfter(-1, 800)
				this.validate()
			}
		});