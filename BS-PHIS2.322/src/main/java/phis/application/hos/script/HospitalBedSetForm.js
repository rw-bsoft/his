$package("phis.application.hos.script")

/**
 * 床位维护form
 * 
 * @param {}
 *            cfg
 */

$import("phis.script.TableForm")

phis.application.hos.script.HospitalBedSetForm = function(cfg) {
	cfg.modal = true;
	phis.application.hos.script.HospitalBedSetForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.hos.script.HospitalBedSetForm, phis.script.TableForm, {
			doSave : function() {
				var form = this.form.getForm();
				if (form.findField("CWFY").getValue() < 0) {
					Ext.Msg.alert("提示", "床位费不能小于0!", function() {
								form.findField("CWFY").focus(true, 100);
							});
					return;
				}
				if (this.form.getForm().findField("ICU").getValue() < 0) {
					Ext.Msg.alert("提示", "ICU费用不能小于0!", function() {
								form.findField("ICU").focus(true, 100);
							});
					return;
				}
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				Ext.apply(this.data, this.exContext)

				if (items) {
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

						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空")
								return;
							}
						}
						values[it.id] = v;
					}
				}

				Ext.apply(this.data, values);
				this.saveToServer(values)
			},
			saveToServer : function(saveData) {
				saveData.originalBRCH = this.originalBRCH;
				
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				// if (this.initDataId == null) {
				// this.op = "create";
				// }
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalBedSetService",
							serviceAction : "saveBed",
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								var brch = this.form.getForm().findField("BRCH");
								Ext.Msg.alert("错误",msg, function() {
									brch.focus(true,200);
							});
								return
							}
							this.originalBRCH = this.form.getForm().findField("BRCH").getValue();
							MyMessageTip.msg("提示", "操作成功!", true);
							if (json.body) {
								// this.initFormData(json.body)
								this.focusFieldAfter(-1, 800)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)// jsonRequest
			},
			// add by caijy for checkbox
			initFormData : function(data) {
				var zyh = data["ZYH"];
				Ext.apply(this.data, data);
				this.originalBRCH = data.BRCH;
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (zyh && it.id != "CWFY" && it.id != "ICU") {
							f.setDisabled("true");
						}
						// if (!zyh && it.id == "BRCH") {
						// f.setDisabled("true");
						// }
						var v = data[it.id]
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v == 0) {// add by yangl
									// 解决字典类型值为0(int)时无法设置的BUG
									v = "0";
								}
								f.setValue(v)

							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true)
					this.focusFieldAfter(-1, 800)
				}
			},
			doNew : function() {
				this.op = "create"
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
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
						// "it.update" to "!=false"
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.focusFieldAfter(-1, 800)
				this.validate()
				this.form.getForm().findField("CWKS").setValue(this.data.CWKS);
				this.form.getForm().findField("KSDM").setValue(this.data.KSDM);
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				// alert(defaultWidth)
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					validator : function(str) {
						if (it.id == "BRCH" || it.id == "FJHM") {
							var len = str.length;
							var reLen = 0;
							for (var i = 0; i < len; i++) {
								if (str.charCodeAt(i) < 27
										|| str.charCodeAt(i) > 126) {
									// 全角
									reLen += 2;
								} else {
									reLen++;
								}
							}
							if (reLen > it.length) {
								return '该输入项的最大长度是' + it.length + '，一个汉字长度为2 ';
							}
						}
						if(it.id == "ICU" && str>999999.99){
							return '该输入项的最大值允许为999999.99';
						}
						if(it.id == "CWFY" && str>9999.99){
							return '该输入项的最大值允许为9999.99';
						}
						return true;
					},
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
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
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it['showRed']) {
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
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
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

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
						} else {
							cfg.minValue = 0;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 700
						cfg.height = 450
						break;
				}
				return cfg;
			}
		})