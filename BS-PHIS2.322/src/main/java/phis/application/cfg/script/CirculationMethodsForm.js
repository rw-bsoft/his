$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.CirculationMethodsForm = function(cfg) {
	phis.application.cfg.script.CirculationMethodsForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.cfg.script.CirculationMethodsForm, phis.script.TableForm,
		{
			// 添加了俩事件（最后一句）
			onReady : function() {
				if (this.CodeFieldSet) {
					for (var i = 0; i < this.CodeFieldSet.length; i++) {
						var CodeField = this.CodeFieldSet[i];
						var target = CodeField[0];
						var codeType = CodeField[1];
						var srcField = CodeField[2];
						var field = this.form.getForm().findField(target);
						var s_field = this.form.getForm().findField(srcField);
						if (s_field) {
							s_field.on("change", function(p, v) {
										this.setValue(v.toUpperCase());
									})
						}
						if (field) {
							if (!field.codeType)
								field.codeType = [];
							field.codeType.push(codeType)
							if (!field.srcField)
								field.srcField = [];
							field.srcField.push(srcField);
							if (!field.hasOnChange) {
								field.hasOnChange = true;
								field.on("change", this.onChange, this);
							}
						}
					}
				}
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.form.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var f1 = 112
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					if (btns) {
						var n = btns.getCount()
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i)
							var key = btn.accessKey
							if (key) {
								btnAccessKeys[key] = btn
								keys.push(key)
							}
						}
					}
				} else {
					var btns = this.form.buttons
					if (btns) {
						for (var i = 0; i < btns.length; i++) {
							var btn = btns[i]
							var key = btn.accessKey
							if (key) {
								btnAccessKeys[key] = btn
								keys.push(key)
							}
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				keyMap.on(keys, this.onAccessKey, this)
				if (this.win) {
					keyMap.on({
								key : Ext.EventObject.ESC,
								shift : true
							}, this.onEsc, this)
				}
				this.eventActions();
			},
			doNew : function() {
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
							if (!(arguments[0] == 1)) { // whether set
														// defaultValue, it will
								// be setted when there is no args.
								var dv = it.defaultValue;
								if (dv) {
									if ((it.type == 'date' || it.xtype == 'datefield')
											&& typeof dv == 'string' && dv.length > 10) {
										dv = dv.substring(0, 10);
									}
									f.setValue(dv);
								}
							}
							if (!it.update && !it.fixed && !it.evalOnServer) {
								f.enable();
							}
							// add by yangl 2012-06-29
							if (it.dic && it.dic.defaultIndex) {
								if (f.store.getCount() == 0)
									continue;
								if (isNaN(it.dic.defaultIndex)
										|| f.store.getCount() <= it.dic.defaultIndex)
									it.dic.defaultIndex = 0;
								f.setValue(f.store.getAt(it.dic.defaultIndex).get('key'));
							}
							f.validate();
						}
					}
					this.setKeyReadOnly(false)
					this.startValues = form.getValues(true);
					this.fireEvent("doNew", this.form)
					this.focusFieldAfter(-1, 800)
					this.afterDoNew();
					this.resetButtons();
				this.opener.initData(this);
			},
			getFormData : function() {
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
				Ext.apply(this.data, this.exContext.empiData)
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
							// add by caijy from checkbox
							if (f.getXType() == "checkbox") {
								var checkValue = 1;
								var unCheckValue = 0;
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
								}
								if (v == true) {
									v = checkValue;
								} else {
									v = unCheckValue;
								}
							}
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
						if (it.type && it.type == "int") {
							v = (v == "null" || v == "" || v == undefined)
									? null
									: parseInt(v);
						}
						values[it.id] = v;
					}
				}
				values.JGID = this.mainApp['phisApp'].deptId
				values.SYKF = this.mainApp['phis'].treasuryEjkf > 0 ? 2 : 1;
				if (values.YWLB == null) {
					values.YWLB = 0;
				}
				return values;
			},

			// DJLX字段特殊处理
			// createField : function(it) {
			// var ac = util.Accredit;
			// var defaultWidth = this.fldDefaultWidth || 200
			// // alert(this.defaultHeight || it.height)
			// var cfg = {
			// name : it.id,
			// fieldLabel : it.alias,
			// xtype : it.xtype || "textfield",
			// vtype : it.vtype,
			// width : defaultWidth,
			// height : this.defaultHeight || it.height,
			// value : it.defaultValue,
			// enableKeyEvents : it.enableKeyEvents,
			// validationEvent : it.validationEvent
			// }
			// if (it.xtype == "checkbox") {
			// cfg.fieldLabel = null;
			// cfg.boxLabel = it.alias;
			// }
			//	
			// if (it.xtype == "uxspinner") {
			// cfg.strategy = {}
			// cfg.strategy.xtype = it.spin_xtype;
			// // alert(Ext.encode(it));
			// }
			// cfg.listeners = {
			// specialkey : this.onFieldSpecialkey,
			// // add by liyl 2012-06-17 去掉输入字符串首位空格
			// blur : function(e) {
			// if (typeof(e.getValue()) == 'string') {
			// e.setValue(e.getValue().trim())
			// }
			// },
			// scope : this
			// }
			//	
			// if (it.inputType) {
			// cfg.inputType = it.inputType
			// }
			// if (it.editable) {
			// cfg.editable = true
			// }
			// if (it['not-null'] && !it.fixed) {
			// cfg.allowBlank = false
			// cfg.invalidText = "必填字段"
			// cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
			// + "</span>"
			// }
			// if (it['showRed']) {
			// cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
			// + "</span>"
			// }
			// // add by yangl 增加readOnly属性
			// if (it.readOnly) {
			// cfg.readOnly = true
			// // cfg.unselectable = "on";
			// cfg.style = "background:#E6E6E6;cursor:default;";
			// cfg.listeners.focus = function(f) {
			// f.blur();
			// }
			// }
			// if (it.fixed || it.fixed) {
			// cfg.disabled = true
			// }
			// if (it.pkey && it.generator == 'auto') {
			// cfg.disabled = true
			// }
			// if (it.emptyText) {
			// cfg.emptyText = it.emptyText;
			// }
			// if (it.evalOnServer && ac.canRead(it.acValue)) {
			// cfg.disabled = true
			// }
			// if (this.op == "create" && !ac.canCreate(it.acValue)) {
			// cfg.disabled = true
			// }
			// if (this.op == "update" && !ac.canUpdate(it.acValue)) {
			// cfg.disabled = true
			// }
			// if (this.hideTrigger && cfg.disabled == true) {
			// cfg.hideTrigger = true;
			// cfg.emptyText = null;
			// }
			// // add by yangl,modify simple code Generation methods
			// if (it.codeType) {
			// if (!this.CodeFieldSet)
			// this.CodeFieldSet = [];
			// this.CodeFieldSet.push([it.target, it.codeType, it.id]);
			// }
			// if (it.mode == "remote") {
			// // 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
			// return this.createRemoteDicField(it);
			// } else if (it.dic) {
			// // add by lyl, check treecheck length
			// if (it.dic.render == "TreeCheck") {
			// if (it.length) {
			// cfg.maxLength = it.length;
			// }
			// }
			// // if(it.id=="DJLX"){
			// // var combox={
			// // xtype:"combo",
			// // name:"DJLX",
			// // fieldLabel:"单据类型",
			// // width:170,
			// // mode:"local",
			// // //disabled:true,
			// // //readOnly:true,
			// // triggerAction:"all",
			// // valueField:"key",
			// // displayField:"text",
			// //// editable:true,
			// // emptyText : '请选择',
			// // selectOnFocus : true,
			// // store: new Ext.data.ArrayStore({
			// // id: 0,
			// // fields: [
			// // 'key',
			// // 'text'
			// // ],
			// // data: [["RK", '入库'],
			// // ["CK", '出库'],
			// // ["SL", '申领'],
			// // ["PD", '盘点'],
			// // ["QT", '其他'],
			// // ["DB", '调拨'],
			// // ["DJ", '登记'],
			// // ["DR", '调入'],
			// // ["ZK" ,"转科"],
			// // ["BS" ,"报损"],
			// // ["TK" ,"退库"],
			// // ["FC" ,"封库"],
			// // ["YH" ,"养护"],
			// // ["CZ" ,"重置"],
			// // ["YS" ,"验收"],
			// // ["JH" ,"计划"]]
			// // })
			// // };
			// // return combox
			// // }
			// it.dic.src = this.entryName + "." + it.id
			// it.dic.defaultValue = it.defaultValue
			// it.dic.width = defaultWidth
			// if (it.dic.fields) {
			// if (typeof(it.dic.fields) == 'string') {
			// var fieldsArray = it.dic.fields.split(",")
			// it.dic.fields = fieldsArray;
			// }
			// }
			// var combox = this.createDicField(it.dic)
			// Ext.apply(combox, cfg)
			// combox.on("specialkey", this.onFieldSpecialkey, this)
			// return combox;
			// }
			//	
			// if (it.length) {
			// cfg.maxLength = it.length;
			// }
			// if (it.xtype) {
			// return cfg;
			// }
			// switch (it.type) {
			// case 'int' :
			// case 'double' :
			// case 'bigDecimal' :
			// cfg.xtype = "numberfield"
			// cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
			// if (it.type == 'int') {
			// cfg.decimalPrecision = 0;
			// cfg.allowDecimals = false
			// } else {
			// cfg.decimalPrecision = it.precision || 2;
			// }
			// if (it.minValue) {
			// cfg.minValue = it.minValue;
			// }
			// if (it.maxValue) {
			// cfg.maxValue = it.maxValue;
			// }
			// break;
			// case 'date' :
			// cfg.xtype = 'datefield';
			// cfg.emptyText = "请选择日期";
			// cfg.format = 'Y-m-d';
			// if (it.minValue) {
			// cfg.minValue = it.minValue;
			// }
			// if (it.maxValue) {
			// cfg.maxValue = it.maxValue;
			// }
			// break;
			// case 'text' :
			// cfg.xtype = "htmleditor"
			// cfg.enableSourceEdit = false
			// cfg.enableLinks = false
			// cfg.width = it.width || 700
			// cfg.height = it.height || 300
			// if (this.plugins)
			// this.getPlugins(cfg);
			// break;
			// case 'label' :
			// cfg.id = it.id;
			// cfg.xtype = "label"
			// cfg.width = it.width || 700
			// cfg.height = it.height || 300
			// // cfg.html = "<span style='color:red'>测试</span>"
			// break;
			// }
			// return cfg;
			// },
			// initFormData : function(data) {
			// Ext.apply(this.data, data)
			// this.initDataId = this.data[this.schema.pkey]
			// var form = this.form.getForm()
			// var items = this.schema.items
			// var n = items.length
			// for (var i = 0; i < n; i++) {
			// var it = items[i]
			// var f = form.findField(it.id)
			// if (f) {
			// var v = data[it.id]
			// if (v != undefined) {
			// if (f.getXType() == "checkbox") {
			// var setValue = "";
			// if (it.checkValue && it.checkValue.indexOf(",") > -1) {
			// var c = it.checkValue.split(",");
			// checkValue = c[0];
			// unCheckValue = c[1];
			// if (v == checkValue) {
			// setValue = true;
			// } else if (v == unCheckValue) {
			// setValue = false;
			// }
			// }
			// if (setValue == "") {
			// if (v == 1) {
			// setValue = true;
			// } else {
			// setValue = false;
			// }
			// }
			// f.setValue(setValue);
			// } else {
			// if (it.dic && v !== "" && v === 0) {// add by yangl
			// // 解决字典类型值为0(int)时无法设置的BUG
			// v = "0";
			// }
			// if(v.key){
			// f.setValue(v.key)
			// }else{
			// f.setValue(v)
			//							
			// }
			//							
			// if (it.dic && v != "0" && f.getValue() != v) {
			// f.counter = 1;
			// this.setValueAgain(f, v, it);
			// }
			//	
			// }
			// }
			// if (it.update == "false") {
			// f.disable();
			// }
			// }
			// this.setKeyReadOnly(true)
			// this.focusFieldAfter(-1, 800)
			// }
			// },
			eventActions : function() {
				this.formWin = this.form.getForm();
				this.formWin.findField("DJLX").on("select", this.onDjlxChange,
						this);
				this.formWin.findField("YWLB").on("select", this.onYwlbChange,
						this);
			},
			onDjlxChange : function() {
				var djlx = this.formWin.findField("DJLX").getValue();
				if (djlx == "RK") {
					this.formWin.findField("YWLB").setValue("1");
					this.formWin.findField("TSBZ").setDisabled(false);
					this.formWin.findField("FKBZ").setDisabled(false);
					this.formWin.findField("JZBZ").setValue("0");
				} else if (djlx == "CK") {
					this.formWin.findField("YWLB").setValue("-1");
					this.formWin.findField("TSBZ").setDisabled(false);
					this.formWin.findField("FKBZ").setValue("0");
					this.formWin.findField("FKBZ").setDisabled(true);
					this.formWin.findField("JZBZ").setValue("1");
				} else if (djlx == "BS") {
					this.formWin.findField("YWLB").setValue("0");
					this.formWin.findField("TSBZ").setValue("0");
					this.formWin.findField("TSBZ").setDisabled(true);
					this.formWin.findField("FKBZ").setValue("0");
					this.formWin.findField("FKBZ").setDisabled(true);
					this.formWin.findField("JZBZ").setValue("0");
					this.formWin.findField("ZHZT").setValue("-2");
				} else if (djlx == "DB" || djlx == "DR") {
					this.formWin.findField("YWLB").setValue("1");
					this.formWin.findField("TSBZ").setValue("0");
					this.formWin.findField("TSBZ").setDisabled(true);
					this.formWin.findField("FKBZ").setValue("0");
					this.formWin.findField("FKBZ").setDisabled(true);
					this.formWin.findField("JZBZ").setValue("0");
				} else {
					this.formWin.findField("YWLB").setValue("0");
					this.formWin.findField("TSBZ").setValue("0");
					this.formWin.findField("TSBZ").setDisabled(true);
					this.formWin.findField("FKBZ").setValue("0");
					this.formWin.findField("FKBZ").setDisabled(true);
					this.formWin.findField("JZBZ").setValue("0");
					this.formWin.findField("ZHZT").setValue("");
				}
			},
			onYwlbChange : function() {
				var ywlb = this.formWin.findField("YWLB").getValue();
				var djlx = this.formWin.findField("DJLX").getValue();
				if (djlx == "RK" && ywlb == "1") {
					this.formWin.findField("ZHZT").setValue("0");
				}
				if (djlx == "RK" && ywlb == "-1") {
					this.formWin.findField("ZHZT").setValue("-3");
				}
				if (djlx == "CK" && ywlb == "1") {
					this.formWin.findField("ZHZT").setValue("0");
				}
				if (djlx == "CK" && ywlb == "-1") {
					this.formWin.findField("ZHZT").setValue("1");
				}
			},
			onBeforeSave : function(entryName, op, saveData) {
				if (this.getFormData().BLSX == 1) {
					MyMessageTip.msg("提示", "系统默认生成方式不容许修改!", true);
					return false;
				}
				var data = {
					"FSMC" : saveData.FSMC,
					"DJLX" : saveData.DJLX
				};
				if (this.initDataId) {
					data["FSXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "circulationMethodsModuleService",
							serviceAction : "queryFSMCVerification",
							schemaDetailsList : "WL_LZFS",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			}
		})
