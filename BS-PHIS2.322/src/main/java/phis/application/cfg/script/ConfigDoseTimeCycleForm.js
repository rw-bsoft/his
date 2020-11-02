$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDoseTimeCycleForm = function(cfg) {
	cfg.colCount = 5;
	cfg.width = 580;
	cfg.labelWidth = 1;
	this.defaultHeight=22;
	this.schema = "phis.application.cfg.schemas.GY_SYPC2";
	this.entryName = "phis.application.cfg.schemas.GY_SYPC2"
	phis.application.cfg.script.ConfigDoseTimeCycleForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigDoseTimeCycleForm,
		phis.script.TableForm, {
			onWinShow : function() {
				this.doNew();
				var items = this.schema.items;
				var ZXSJ = this.ZXSJ;
				if (ZXSJ) {
					var num = "";
					if (ZXSJ.length == 5) {
						num = ZXSJ.indexOf("0");
						if (num == 0) {
							num = ZXSJ.substring(1, 2);
						} else {
							num = ZXSJ.substring(0, 2);
						}
						this.form.getForm().findField("sj" + (num))
								.setValue(true);
						document.getElementById("input_sj" + (num)).value = ZXSJ;
						for (var i = 0; i < items.length; i++) {
							if (this.form.getForm().findField("sj" + (i))
									.getValue() == false) {
								document.getElementById("input_sj" + (i)).value = items[i].alias;
							}
						}
					}
					var sub1;
					var sub2;
					var index;
					if (ZXSJ.length > 5) {
						for (var i = 0; i < ZXSJ.length; i = i + 6) {
							sub1 = ZXSJ.substring(i, i + 5);
							index = sub1.indexOf("0");
							if (index == 0) {
								sub2 = sub1.substring(1, 2);
							} else {
								sub2 = sub1.substring(0, 2);
							}
							this.form.getForm().findField("sj" + sub2)
									.setValue(true);
							document.getElementById("input_sj" + (sub2)).value = sub1;
						}
						for (var i = 0; i < items.length; i++) {
							if (this.form.getForm().findField("sj" + (i))
									.getValue() == false) {
								document.getElementById("input_sj" + (i)).value = items[i].alias;
							}
						}
					}
				}
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					style : "padding-left: 0px",
					height : this.defaultHeight || it.height,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
				if (it.xtype == "checkbox") {
					cfg.fieldLabel = null;
					cfg.labelStyle = 'display:none';
					cfg.boxLabel = it.alias;
					cfg.onRender = function(ct, position) {
						Ext.form.Checkbox.superclass.onRender.call(this, ct,
								position);
						if (this.inputValue !== undefined) {
							this.el.dom.value = this.inputValue;
						}
						this.wrap = this.el.wrap({
									cls : 'x-form-check-wrap'
								});
						if (this.boxLabel) {
							this.wrap.createChild({
								id : 'input_' + it.id,
								name : 'input_' + it.id,
								tag : 'input',
								type : 'textfield',
								style : 'width:50px;height:18px;',
								value : this.boxLabel,
								// htmlFor : this.el.id,
								cls : 'x-form-cb-label'
									// html : this.boxLabel
								});
						}
						if (this.checked) {
							this.setValue(true);
						} else {
							this.checked = this.el.dom.checked;
						}
						// Need to repaint for IE, otherwise positioning is
						// broken
						if (Ext.isIE && !Ext.isStrict) {
							this.wrap.repaint();
						}
						this.resizeEl = this.positionEl = this.wrap;
					}
				}

				if (it.xtype == "uxspinner") {
					cfg.strategy = {}
					cfg.strategy.xtype = it.spin_xtype;
					// alert(Ext.encode(it));
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					// add by liyl 2012-06-17 去掉输入字符串首位空格
					blur : function(e) {
						if (typeof(e.getValue()) == 'string') {
							e.setValue(e.getValue().trim())
						}
					},
					scope : this
				}

				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = true
				}
				if (it['not-null'] && !it.fixed) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				if (it['showRed']) {
					cfg.fieldLabel = "<span style='color:red'>"
							+ cfg.fieldLabel + "</span>"
				}
				// add by yangl 增加readOnly属性
				if (it.readOnly) {
					cfg.readOnly = true
					// cfg.unselectable = "on";
					cfg.style = "background:#E6E6E6;cursor:default;";
					cfg.listeners.focus = function(f) {
						f.blur();
					}
				}
				if (it.fixed || it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.emptyText) {
					cfg.emptyText = it.emptyText;
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
				if (this.hideTrigger && cfg.disabled == true) {
					cfg.hideTrigger = true;
					cfg.emptyText = null;
				}
				// add by yangl,modify simple code Generation methods
				if (it.codeType) {
					if (!this.CodeFieldSet)
						this.CodeFieldSet = [];
					this.CodeFieldSet.push([it.target, it.codeType, it.id]);
				}
				if (it.mode == "remote") {
					// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
					return this.createRemoteDicField(it);
				} else if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					if (it.dic.fields) {
						if (typeof(it.dic.fields) == 'string') {
							var fieldsArray = it.dic.fields.split(",")
							it.dic.fields = fieldsArray;
						}
					}
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
						cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
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
						cfg.xtype = 'datefield';
						cfg.emptyText = "请选择日期";
						cfg.format = 'Y-m-d';
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						if (this.plugins)
							this.getPlugins(cfg);
						break;
					case 'label' :
						cfg.id = it.id;
						cfg.xtype = "label"
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						// cfg.html = "<span style='color:red'>测试</span>"
						break;
				}
				return cfg;
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
						}
						values[it.id] = v;
					}
				}
				// Ext.apply(this.data, values);
				this.fireEvent("doSave", values);

			}

		})