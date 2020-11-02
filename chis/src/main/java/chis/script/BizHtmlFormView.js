/**
 * 公共文件*HTML表单
 * 
 * @author : chenxr
 */
$package("chis.script");

$import("app.modules.form.TableFormView", "chis.script.BizCommon",
		"chis.script.BizFormCommon", "chis.script.util.widgets.MyMessageTip")

chis.script.BizHtmlFormView = function(cfg) {
	cfg.idPostfix = cfg.idPostfix
			|| ("_HF" + Math.ceil(Math.random() * 100000));
	cfg.colCount = cfg.colCount || 3;
	cfg.needSetDefaultValue = false;
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.isAutoScroll = cfg.isAutoScroll || false;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.autoHeight = !cfg.isAutoScroll
	cfg.saveServiceId = cfg.saveServiceId || "chis.simpleSave"
	cfg.loadServiceId = cfg.loadServiceId || "chis.simpleLoad"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizFormCommon);
	cfg.createFields = cfg.createFields || [] // 手动用ExtJS创建的字段（控件）
	cfg.disableFlds = cfg.disableFlds || [] // 初始化时要灰掉的字段
	/**
	 * 关联控制字段，当某个字段字段为一个值时，其他字段可用，否则不可用 数据结构说明：()内为替换值-说明部分
	 * this.otherDisable=[{fld:(字段名-schema中item的ID值),type:(字段html类型=radio/checkbox/text),
	 * control :[{key:(字段值),exp:(eq=等于/ne=不等于),
	 * field:[(要控制的字段,意为：fld的值exp(等于/不等于/不为)key时field中字段可用)],
	 * disField:[(与field控制相反)]}]}]--disField mustField:[]--必填字段ID，字典时用div的ID
	 * ，只增加要填的样式，增加必填样式时会自动将文本的disable改为false,取消必填样式时不增加disable=true属性
	 * redLabels:[]--将模板中文本变红色字体 --在type为text或radio时有效 defaultValue:{}--为字段设置默认值
	 * oppositeMF:[]--key取反时，给html标识增加必填样式 oppositeRL:[]--key取反时，将模板中文本变红色字体
	 * eg:this.otherDisable=[ { fld : "occupational", type : "radio", control : [{
	 * key : "2", exp : 'eq', field : ["jobs", "workTime", "dust", "ray",
	 * "physicalFactor", "chemicals", "other"], disField : ["dustPro_1",
	 * "dustPro_2", "dustProDesc", "rayPro_1", "rayPro_2", "rayProDesc",
	 * "physicalFactorPro_1", "physicalFactorPro_2", "physicalFactorProDesc",
	 * "chemicalsPro_1", "chemicalsPro_2", "chemicalsProDesc", "otherPro_1",
	 * "otherPro_2", "otherProDesc"] }] }, { fld : "dust", type : "text",
	 * control : [{ key : "notNull", field : ["dustPro_1", "dustPro_2"],
	 * disField : ["dustProDesc"] , defaultValue : {"assessYearCon_1" :
	 * true,"normScale1" : 75}] } ]
	 */
	cfg.otherDisable = cfg.otherDisable || []
	/**
	 * checkbox 多选技字典 互斥控制 数据结构说明：()内为替换值-说明部分 this.mutualExclusion=[
	 * {fld:(字段名),key:(互斥字典项),excludeKey:[与key互斥的key项目] ,other:[其他选择项控制的字段]} ]
	 * eg:this.mutualExclusion = [{ fld : "symptom", key : "01", other :
	 * ["symptomOt"] }, { fld : "cerebrovascularDiseases", key : "1", other :
	 * ["othercerebrovascularDiseases"] }]
	 * //字段symptom的字段01（无）选择项与其他选择项是互斥的，即选择了01后，其他项的选中状态为清除
	 */
	cfg.mutualExclusion = cfg.mutualExclusion || []
	chis.script.BizHtmlFormView.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.script.BizHtmlFormView, app.modules.form.TableFormView, {
	initPanel : function(sc) {
		if (this.form) {
			return this.form;
		}
		this.idPostfix = "_" + this.generateMixed(5);
		this.loadSchemas();
		var cfg = {
			border : false,
			frame : true,
			collapsible : false,
			autoScroll : true,
			width : this.width,
			height : this.height,
			layout : 'fit',
			region : 'north',
			html : this.getHTMLTemplate() || "" // getHTMLTemplate必须实现
		};
		this.changeCfg(cfg);
		this.initBars(cfg);
		this.form = new Ext.FormPanel(cfg);
		this.form.on("afterrender", this.onReady, this);
		return this.form;
	},
	loadSchemas : function() {
		this.schemas = [];
		// 基本信息 schema
		var resc = util.schema.loadSync(this.entryName);
		if (resc.code == 200) {
			this.schema = resc.schema;
			this.schemas.push(resc.schema);
		} else {
			this.processReturnMsg(resc.code, resc.msg, this.loadSchemas)
			return;
		}
	},
	getHTMLTemplate : function() {
		return "";
	},
	isHaveElementInArray : function(arrayes, element) {
		// 判断某个元素（element）是否存在数组（arrayes）中
		var hasEle = false;
		for (var i = 0, len = arrayes.length; i < len; i++) {
			if (arrayes[i] == element) {
				hasEle = true;
				break;
			}
		}
		return hasEle
	},
	getStrSize : function(str) {// 获取字符串的字节长度
		var realLength = 0, len = str.length, charCode = -1;
		for (var i = 0; i < len; i++) {
			charCode = str.charCodeAt(i);
			if (charCode >= 0 && charCode <= 128) {
				realLength += 1;
			} else {
				realLength += 2;
			}
		}
		return realLength;
	},
	hasClass : function(obj, cls) {// 判断对象obj是否有cls样式
		return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
	},
	addClass : function(obj, cls) {// 给对象obj增加cls样式
		if (!this.hasClass(obj, cls))
			obj.className += " " + cls;
	},
	removeClass : function(obj, cls) {// 移出对象obj的cls样式
		if (this.hasClass(obj, cls)) {
			var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
			obj.className = obj.className.replace(reg, ' ');
		}
	},
	validateString : function(maxLength, notNull, alias, obj, me) {// 处理string类型字段的校验
		var fv = obj.value;
		var fvLen = me.getStrSize(fv);
		if (fvLen > maxLength) {
			// obj.value="";
			me.addClass(obj, "x-form-invalid")
			obj.title = alias + "中输入的字符串超出定义的最大长度（" + maxLength + "）";
			return;
		} else {
			obj.title = alias;
			me.removeClass(obj, "x-form-invalid");
		}
		if (notNull) {
			if (fv == "") {
				obj.title = "字段"+alias + "为必填项";
				me.addClass(obj, "x-form-invalid");
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
	},
	validateInt : function(length, minValue, maxValue, notNull, fid, alias,
			obj, me) {// 处理int类型字段的校验
		var fv = obj.value;
		var reg = new RegExp("^[0-9]*$");
		if (parseInt(fv.length) > parseInt(length)) {
			me.addClass(obj, "x-form-invalid")
			obj.title = alias + "长度过长，最大为" + length;
			return;
		}
		if (!reg.test(fv)) {
			me.addClass(obj, "x-form-invalid")
			obj.title = alias + "为正整数，请输入数字!";
			return;
		} else {
			obj.title = alias;
			me.removeClass(obj, "x-form-invalid");
		}
		if (typeof(minValue) != 'undefined') {
			if (parseInt(fv) < minValue) {
				me.addClass(obj, "x-form-invalid")
				obj.title = alias + "中输入的值小于了定义的最小值（" + minValue + "）";
				return;
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
		if (typeof(maxValue) != 'undefined') {
			if (parseInt(fv) > maxValue) {
				me.addClass(obj, "x-form-invalid")
				obj.title = alias + "中输入的值大于了定义的最大值（" + maxValue + "）";
				return;
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
		if (notNull) {
			if (fv == "") {
				obj.title = "这个字段"+alias + "为必填项";
				me.addClass(obj, "x-form-invalid");
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
	},
	validateDouble : function(length, precision, minValue, maxValue, notNull,
			alias, obj, me) {// 处理double类型字段的校验
		var dd = 0;
		if (typeof(precision) != 'undefined') {
			dd = parseInt(precision);
		}
		var iNum = length - dd - 1;
		var regStr = "(^[0-9]{0," + iNum + "}$)|(^[0-9]{1," + iNum
				+ "}(\\.[0-9]{0," + dd + "})?$)";
		if (dd == 0) {
			regStr = "(^[0-9]{0," + (iNum + 1) + "}$)|(^[[0-9]*\\.[0-9]*]{0,"
					+ iNum + "}$)";
		}
		var reg = new RegExp(regStr);
		var fv = obj.value;
		if (fv && fv.length > iNum && fv.indexOf(".") == -1) {
			me.addClass(obj, "x-form-invalid")
			obj.title = alias + "中输入的值错误"
			return;
		} else {
			obj.title = alias;
			me.removeClass(obj, "x-form-invalid");
		}
		if (!reg.test(fv)) {
			me.addClass(obj, "x-form-invalid")
			obj.title = alias + "中输入了非浮点型数据或字符"
			return;
		} else {
			obj.title = alias;
			me.removeClass(obj, "x-form-invalid");
		}
		if (typeof(minValue) != 'undefined') {
			if (parseInt(fv) < minValue) {
				me.addClass(obj, "x-form-invalid");
				obj.title = alias + "中输入的值小于了定义的最小值（" + minValue + "）";
				return;
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
		if (typeof(maxValue) != 'undefined') {
			if (parseInt(fv) > maxValue) {
				me.addClass(obj, "x-form-invalid");
				obj.title = alias + "中输入的值大于了定义的最大值（" + maxValue + "）";
				return;
			} else {
				me.removeClass(obj, "x-form-invalid");
			}
		}
		if (notNull) {
			if (fv == "") {
				obj.title = "这里"+alias + "为必填项";
				me.addClass(obj, "x-form-invalid");
			} else {
				obj.title = alias;
				me.removeClass(obj, "x-form-invalid");
			}
		}
	},
	addEvent : function(o, c, h) {// HTML对象增加事件控制方法
		if (o.attachEvent) {
			o.attachEvent('on' + c, h);
		} else {
			o.addEventListener(c, h, false);
		}
		return true;
	},
	dicFldValidateClick : function(fldId, alias, obj, me) {
		var fv = me.getHtmlFldValue(fldId);
		var divId = "div_" + fldId + this.idPostfix;
		var fdiv = document.getElementById(divId);
		if (fv && fv.length > 0) {
			if (fdiv) {
				this.removeClass(fdiv, "x-form-invalid");
				fdiv.title = "";
			}
		} else {
			if (fdiv) {
				this.addClass(fdiv, "x-form-invalid");
				fdiv.title = alias + "为必选项!"
			}
		}
	},
	vtypeValidate : function(fieldId, vtype, length) {
		// TODO string类型的 vtype 字段特殊验证各自实现
	},
	addFieldDataValidateFun : function(schema) {
		if (!schema) {
			schema = this.schema;
		}
		var items = schema.items
		var n = items.length
		var frmEl = this.form.getEl();
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (!this.isCreateField(it.id)) {
				if (it.dic) {
					var notNull = false;
					if (it['not-null'] == "1" || it['not-null'] == "true") {
						var dfs = document.getElementsByName(it.id);
						if (!dfs) {
							continue;
						}
						notNull = true;
						var fv = this.getHtmlFldValue(it.id);
						var divId = "div_" + it.id + this.idPostfix;
						var fdiv = document.getElementById(divId);
						if (!frmEl.contains(fdiv)) {
							continue;
						}
						if (fv && fv.length > 0) {
							if (fdiv) {
								this.removeClass(fdiv, "x-form-invalid");
								fdiv.title = "";
							}
						} else {
							if (fdiv) {
								this.addClass(fdiv, "x-form-invalid");
								fdiv.title = it.alias + "为必选项!"
							}
						}
						var me = this;
						for (var di = 0, dlen = dfs.length; di < dlen; di++) {
							var itemFld = dfs[di];
							var handleFun = function(fldId, alias, obj, me) {
								return function() {
									me.dicFldValidateClick(fldId, alias, obj,
											me);
								}
							}
							this.addEvent(itemFld, "click", handleFun(it.id,
											it.alias, itemFld, me));
						}
					}
				} else {
					var fld = document.getElementById(it.id + this.idPostfix);
					if (!fld) {
						continue;
					}
					if (!frmEl.contains(fld)) {
						continue;
					}
					var notNull = false;
					if (it['not-null'] == "1" || it['not-null'] == "true"
							|| this.isMustField(it)) {
						notNull = true;
						if (fld.value == "" || fld.value == fld.defaultValue) {
							this.addClass(fld, "x-form-invalid");
						} else {
							this.removeClass(fld, "x-form-invalid");
						}
					}
					var me = this;
					switch (it.type) {
						case "string" :
							if (typeof it.vtype != 'undefined') {
								this.vtypeValidate(it.id, it.vtype, it.length);
							} else {
								var maxLength = it.length;
								var handleFun = function(maxLength, notNull,
										alias, obj, me) {
									return function() {
										me.validateString(maxLength, notNull,
												alias, obj, me);
									}
								}
								this.addEvent(fld, "change", handleFun(
												maxLength, notNull, it.alias,
												fld, me));
							}
							break;
						case 'int' :
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var length = it.length;
							var handleFun = function(length, minValue,
									maxValue, notNull, fid, alias, obj, me) {
								return function() {
									me.validateInt(length, minValue, maxValue,
											notNull, fid, alias, obj, me);
								}
							}
							this.addEvent(fld, "change", handleFun(length,
											minValue, maxValue, notNull, it.id,
											it.alias, fld, me));
							break;
						case "double" :
							var length = it.length;
							var precision = it.precision;
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var handleFun = function(length, precision,
									minValue, maxValue, notNull, alias, obj, me) {
								return function() {
									me.validateDouble(length, precision,
											minValue, maxValue, notNull, alias,
											obj, me);
								}
							}
							this.addEvent(fld, "change", handleFun(length,
											precision, minValue, maxValue,
											notNull, it.alias, fld, me));
							break;
					}
				}
			}
		}
	},
	addFieldAfterRender : function() {
		// TODO 在HTML中手动增加一些ExtJS创建的对象
	},
	onReady : function() {
		this.addFieldAfterRender();
		// 字段校验
		this.initFieldDisable();
		this.addFieldDataValidateFun(this.schema);
		this.controlOtherFld();
		this.mutualExclusionSet();
		// 扩展
		this.onReadyAffter();
		this.addKeyEvent();
	},
	addKeyEvent : function() {
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
			var btns = this.form.getTopToolbar().items || [];
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
			var btns = this.form.buttons || []
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
		this.btnAccessKeys = btnAccessKeys;
		if (this.win) {
			keyMap.on({
						key : Ext.EventObject.ESC,
						shift : true
					}, this.onEsc, this)
		}
	},
	onReadyAffter : function() {
		// TODO 重写些方法可不重写onReady增加想写在onReady中的代码
	},
	isCreateField : function(fldId) {
		var isCF = false;
		var len = this.createFields.length;
		for (var i = 0; i < len; i++) {
			var cf = this.createFields[i];
			if (cf == fldId) {
				isCF = true;
				break;
			}
		}
		return isCF;
	},
	doCreate : function() {
		this.initDataId = null;
		this.doNew();
	},
	doNew : function() {
		this.op = "create"
		if (this.data) {
			this.data = {}
		}
		var frmEl = this.form.getEl();
		for (var s = 0, sLen = this.schemas.length; s < sLen; s++) {
			var items = this.schemas[s].items;
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.isCreateField(it.id)) {
					// 对自创建手动创建的字段在新建里赋初值，赋值后返回true，否则字段会设置为空值
					var isSet = this.setMyFieldValue(it.id);
					if (!isSet) {
						eval("this." + it.id + ".setValue()");
					}
				} else {
					if (it.dic) {
						var fs = document.getElementsByName(it.id);
						if (fs && fs.length > 0) {
							for (var j = 0, len = fs.length; j < len; j++) {
								var f = fs[j];
								if (!frmEl.contains(f)) {
									continue;
								}
								if (f.type == "checkbox" || f.type == "radio") {
									if (f.checked) {
										f.checked = false;
									}
								}
							}
						}
					} else {
						var f = document.getElementById(it.id + this.idPostfix)
						if (f) {
							f.value = f.defaultValue || '';
							if (f.type == "hidden") {
								f.value = '';
							}
							if (f.defaultValue) {
								// f.style.color = "#999";// 填充注释文字，设灰色
							}
						}
					}
				}
			}
		}
		this.fieldValidate(this.schema);
		this.fireEvent("doNew", this.form);
		this.afterDoNew();
		this.resetButtons();
	},
	fieldValidate : function(schema) {
		if (!schema) {
			schema = this.schema;
		}
		var items = schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (!this.isCreateField(it.id)) {
				if (it.dic) {
					if (it['not-null'] == "1" || it['not-null'] == "true") {
						var dfv = this.getHtmlFldValue(it.id);
						var divId = "div_" + it.id + this.idPostfix;
						var div = document.getElementById(divId);
						if (div) {
							if (dfv && dfv.length > 0) {
								this.removeClass(div, "x-form-invalid");
								div.title = "";
							} else {
								this.addClass(div, "x-form-invalid");
								div.title = "这块"+it.alias + "为必填项";
							}
						}
					}
				} else {
					var fld = document.getElementById(it.id + this.idPostfix);
					if (!fld) {
						continue;
					}
					if (it['not-null'] == "1" || it['not-null'] == "true") {
						if (fld.value == "" || fld.value == fld.defaultValue) {
							this.addClass(fld, "x-form-invalid");
							fld.title = "这一块"+it.alias + "为必填项";
							continue;
						} else {
							this.removeClass(fld, "x-form-invalid");
							fld.title = it.alias
						}
					} else {
						this.removeClass(fld, "x-form-invalid");
						fld.title = it.alias
					}
					var obj = fld;
					switch (it.type) {
						case "string" :
							var maxLength = it.length;
							var fv = fld.value;
							var fvLen = this.getStrSize(fv);
							if (fvLen > maxLength) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入的字符串超出定义的最大长度（"
										+ maxLength + "）";
							} else {
								this.removeClass(obj, "x-form-invalid");
								obj.title = it.alias
							}
							break;
						case 'int' :
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var fv = obj.value;
							if (fv == obj.defaultValue) {// 跳过注释文字验证
								continue;
							}
							var reg = new RegExp("^[0-9]*$");
							var fid = it.id;
							if (!reg.test(fv)) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入了非整数 数字或字符"
								continue;
							} else {
								this.removeClass(obj, "x-form-invalid");
								obj.title = it.alias
							}
							if (typeof(minValue) != 'undefined') {
								if (parseInt(fv) < minValue) {
									this.addClass(obj, "x-form-invalid")
									obj.title = it.alias + "中输入的值小于了定义的最小值（"
											+ minValue + "）";
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							if (typeof(maxValue) != 'undefined') {
								if (parseInt(fv) > maxValue) {
									this.addClass(obj, "x-form-invalid")
									obj.title = it.alias + "中输入的值大于了定义的最大值（"
											+ maxValue + "）";
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							break;
						case "double" :
							var length = it.length;
							var precision = it.precision;
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var dd = 0;
							if (typeof(precision) != 'undefined') {
								dd = parseInt(precision);
							}
							var iNum = length - dd;
							var regStr = "(^[0-9]{0," + iNum + "}$)|(^[0-9]{0,"
									+ iNum + "}(\\.[0-9]{0," + dd + "})?$)";
							if (dd == 0) {
								regStr = "(^[0-9]{0," + iNum
										+ "}$)|(^[[0-9]*\\.[0-9]*]{0," + iNum
										+ "}$)";
							}
							var reg = new RegExp(regStr);
							var fv = obj.value;
							if (fv == obj.defaultValue) {// 跳过注释文字验证
								continue;
							}
							if (!reg.test(fv)) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入了非浮点型数据或字符"
								continue;
							} else {
								this.removeClass(obj, "x-form-invalid");
								obj.title = it.alias
							}
							if (typeof(minValue) != 'undefined') {
								if (parseInt(fv) < minValue) {
									this.addClass(obj, "x-form-invalid");
									obj.title = it.alias + "中输入的值小于了定义的最小值（"
											+ minValue + "）"
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							if (typeof(maxValue) != 'undefined') {
								if (parseInt(fv) > maxValue) {
									this.addClass(obj, "x-form-invalid");
									obj.title = it.alias + "中输入的值大于了定义的最大值（"
											+ maxValue + "）"
									continue;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							break;
					}
				}
			}
		}
	},
	setMyFieldValue : function(fieldId) {
		// TODO 对自创建手动创建的字段在新建里赋初值，赋值后返回true，否则字段会设置为空值
		return false
	},
	initFormData : function(data) {
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		Ext.apply(this.data, data);
		this.initDataId = this.data[this.schema.pkey]
		this.initHTMLFormData(data, this.schema);
		this.fieldValidate(this.schema);
		// this.setKeyReadOnly(true)
		// this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		this.focusFieldAfter(-1, 800);
	},
	initHTMLFormData : function(data, schema) {
		if (!schema) {
			schema = this.schema;
		}
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		// Ext.apply(this.data, data)
		var items = schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i];
			if (it.display
					&& (it.display == "1" || it.display == 0 || it.hidden)) {
				continue;
			}
			if (it.pkey) {
				this.initDataId = data[it.id];
			}
			if (this.isCreateField(it.id)) {
				var cfv = data[it.id];
				if (!cfv && it.defaultValue) {
					cfv = it.defaultValue;
				}
				if (!cfv) {
					cfv = "";
				}
				if (it.type == "date") {
					if (typeof cfv != "string") {
						cfv = Ext.util.Format.date(cfv, 'Y-m-d');
					} else {
						cfv = cfv.substring(0, 10);
					}
				}
				if (!cfv || cfv == '') {
					cfv = this[it.id].defaultValue;
				}
				eval("this." + it.id + ".setValue(cfv);this." + it.id
						+ ".validate();");
			} else {
				if (it.dic) {
					if (!this.fireEvent("dicFldSetValue", it.id, data)) {
						continue;
					} else {
						var dfs = document.getElementsByName(it.id);
						if (!dfs) {
							continue;
						}
						var dicFV = data[it.id];
						var fv = "";
						if (it.defaultValue) {
							fv = it.defaultValue.key;
						}
						if (dicFV) {
							fv = dicFV.key;
						}
						if (!fv) {// yubo
							continue;
						}
						var dvs = fv.split(",");
						for (var j = 0, len = dvs.length; j < len; j++) {
							var f = document.getElementById(it.id + "_"
									+ dvs[j] + this.idPostfix);
							if (f) {
								f.checked = true;
							}
						}
						if (dvs.length > 0) {
							var div = document.getElementById("div_" + it.id
									+ this.idPostfix);
							if (div) {
								this.removeClass(div, "x-form-invalid");
							}
						}
					}
				} else {
					var f = document.getElementById(it.id + this.idPostfix)
					if (f) {
						var v = data[it.id];
						if (!v && !(v == 0) && !it.defaultValue) {
							v = f.defaultValue || "";
							if (f.defaultValue) {
								// f.style.color = "#999";
							}
						} else if (!v && !(v == 0)) {
							v = it.defaultValue;
							// f.style.color = "#000";// 不是注释文字，改黑色字体
						} else {
							// f.style.color = "#000";// 不是注释文字，改黑色字体
						}
						f.value = v;
						if (it['not-null'] == "1" || it['not-null'] == "true") {
							if (data[it.id] && data[it.id] != "") {
								this.removeClass(f, "x-form-invalid");
							}
						}
					}
				}
			}
		}
		this.setKeyReadOnly(true)
		// this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		// this.focusFieldAfter(-1, 800);
	},
	getFormData : function() {
		return this.getHTMLFormData(this.schema);
	},
	getHTMLFormData : function(schema) {
		if (!schema) {
			schema = this.schema;
		}
		// 取表单数据
		if (!schema) {
			return
		}
		var ac = util.Accredit;
		var values = {};
		var items = schema.items;
		var n = items.length
		var frmEl = this.form.getEl();
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (this.op == "create" && !ac.canCreate(it.acValue)) {
				continue;
			}
			// 从内存中取
			var v = this.data[it.id];
			if (v == undefined) {
				v = it.defaultValue;
			}
			if (v != null && typeof v == "object") {
				v = v.key;
			}
			// 从页面上取
			if (this.isCreateField(it.id)) {
				var cfv = eval("this." + it.id + ".getValue()");
				values[it.id] = cfv;
			} else {
				if (it.dic) {
					var fs = document.getElementsByName(it.id);
					if (!fs) {
						continue;
					}
					var vs = [];
					if (fs && fs.length > 0) {
						for (var j = 0, len = fs.length; j < len; j++) {
							var f = fs[j];
							if (frmEl.contains(f)) {
								if (f.type == "checkbox" || f.type == "radio") {
									if (f.checked) {
										vs.push(f.value);
									}
								} else if (f.type == "hidden") {
									vs.push(f.value || '');
								}
							}
						}
					}
					if (vs.length > 1) {
						v = vs.join(',') || ''
					} else {
						v = vs[0] || ''
					}
					values[it.id] = v;
				} else {
					var f = document.getElementById(it.id + this.idPostfix)
					if (f) {
						v = f.value || f.defaultValue || '';
						if (v == f.defaultValue && f.type != "hidden") {
							v = '';
						}
						values[it.id] = v;
					}
				}
			}
		}
		return values;
	},
	initFieldDisable : function() {// 初始化时设置不可用字段
		var len = this.disableFlds.length;
		for (var i = 0; i < len; i++) {
			var fn = this.disableFlds[i];
			var fldId = fn + this.idPostfix;
			if (fn.indexOf('.') != -1) {
				eval(fn + ".disable()");
			} else {
				var fld = document.getElementById(fldId);
				if (fld) {
					// fld.style.display = "none";
					fld.disabled = true;
				}
			}
		}
	},
	mutualExclusionSet : function() {// 实现字段字典选择荐互斥
		var len = this.mutualExclusion.length;
		for (var i = 0; i < len; i++) {
			var meo = this.mutualExclusion[i];
			var fldName = meo.fld;
			var key = meo.key;
			var excludeKey = meo.excludeKey;
			var other = meo.other;
			var fldes = document.getElementsByName(fldName);
			var me = this;
			var handleFun = function(obj, fldName, key, excludeKey, other, me) {
				return function() {
					me.mutualExclusionClick(obj, fldName, key, excludeKey,
							other, me);
				}
			}
			for (var j = 0, flen = fldes.length; j < flen; j++) {
				var obj = fldes[j]
				this.addEvent(obj, "click", handleFun(obj, fldName, key,
								excludeKey, other, me));
			}
		}
	},
	mutualExclusionClick : function(obj, fldName, key, excludeKey, other, me) {
		// 实现字段字典选择荐互斥*给对象增加的事件处理方法
		var objValue = obj.value;
		var fldes = document.getElementsByName(fldName);
		if (objValue == key) {
			for (var k = 0, klen = fldes.length; k < klen; k++) {
				var to = fldes[k];
				if (typeof excludeKey != 'undefined') {
					for (var eki = 0, ekLen = excludeKey.length; eki < ekLen; eki++) {
						if (to.value == excludeKey[eki]) {
							if (to.checked && to.value != key) {
								to.checked = false;
							}
						}
					}
				} else {
					if (to.checked && to.value != key) {
						to.checked = false;
					}
				}
			}
			for (var h = 0, hlen = other.length; h < hlen; h++) {
				var oName = other[h];
				var oid = oName + me.idPostfix;
				var ofld = document.getElementById(oid);
				if (ofld) {
					ofld.value = "";
					ofld.disabled = true;
				}
			}
		} else {
			var meid = fldName + "_" + key + me.idPostfix;
			var meo = document.getElementById(meid);
			if (meo) {
				meo.checked = false;
			}
		}
	},
	/**
	 * 用this.otherDisable中配置实现字段之间的关联控制功能（可用 与不可用 ，必填与不必填，文本显示红色字体与否）
	 */
	controlOtherFld : function() {
		// 实现 关联控制字段，当某个字段字段为一个值时，其他字段可用，否则不可用
		var len = this.otherDisable.length;
		var me = this;
		for (var i = 0; i < len; i++) {
			var od = this.otherDisable[i];
			var cArr = od.control;
			var type = od.type;
			var mutex = od.mutex;
			if (type == "text") {
				var fObj = document.getElementById(od.fld + this.idPostfix);
				if (fObj) {
					var handleFun = function(obj, cArr, me) {
						return function() {
							me.textOnChange(obj, cArr, me);
						}
					}
					this.addEvent(fObj, "change", handleFun(fObj, cArr, me));
					this.addEvent(fObj, "keyup", handleFun(fObj, cArr, me));
				}
			}
			if (type == "checkbox") {
				if (!mutex) {
					for (var j = 0, cLen = cArr.length; j < cLen; j++) {
						var co = cArr[j];
						var key = co.key;
						var fId = od.fld + "_" + key + this.idPostfix;
						var fObj = document.getElementById(fId);
						if (!fObj) {
							continue;
						}
						var handleFun = function(obj, co, me) {
							return function() {
								me.checkOnClick(obj, co, me);
							}
						}
						this.addEvent(fObj, "click", handleFun(fObj, co, me));
					}
				} else {
					var mutexs = [];
					var notmutexs = [];
					var cos = [];
					var notcos = [];
					for (var k = 0, klen = cArr.length; k < klen; k++) {
						var co = cArr[k];
						var key = co.key;
						var fId = od.fld + "_" + key + this.idPostfix;
						var fObj = document.getElementById(fId);
						if (!fObj) {
							continue;
						}
						if (co.mutex) {
							mutexs.push(fObj);
							cos.push(co)
						} else {
							notmutexs.push(fObj);
							notcos.push(co)
						}
					}
					for (var j = 0, cLen = cArr.length; j < cLen; j++) {
						var co = cArr[j];
						var key = co.key;
						var fId = od.fld + "_" + key + this.idPostfix;
						var fObj = document.getElementById(fId);
						if (!fObj) {
							continue;
						}
						if (co.mutex) {
							var handleFun = function(obj, co, me, notmutexs,
									notcos) {
								return function() {
									me.checkOnClick(obj, co, me);
									if (obj.checked) {
										for (var m = 0, ml = notmutexs.length; m < ml; m++) {
											notmutexs[m].checked = false;
											me.checkOnClick(notmutexs[m],
													notcos[m], me);
										}
									}
								}
							}
							this.addEvent(fObj, "click", handleFun(fObj, co,
											me, notmutexs, notcos));

						} else {
							var handleFun = function(obj, co, me, mutexs, cos) {
								return function() {
									me.checkOnClick(obj, co, me);
									if (obj.checked) {
										for (var m = 0, ml = mutexs.length; m < ml; m++) {
											mutexs[m].checked = false;
											me.checkOnClick(notmutexs[m],
													cos[m], me);
										}
									}
								}
							}
							this.addEvent(fObj, "click", handleFun(fObj, co,
											me, mutexs, cos));
						}

					}

				}
			}
			if (type == "radio") {
				var fldName = od.fld;
				var fldes = document.getElementsByName(fldName);
				var handleFun = function(fldName, cArr, me) {
					return function() {
						me.radioOnClick(fldName, cArr, me);
					}
				}
				for (var k = 0, flen = fldes.length; k < flen; k++) {
					var fObj = fldes[k];
					this.addEvent(fObj, "click", handleFun(fldName, cArr, me));
				}
			}
		}
	},
	setRedLabel : function(labels, isRed, me) {
		// 设置标签中的文本是否红色显示
		if (isRed) {
			for (var i = 0, len = labels.length; i < len; i++) {
				var labId = labels[i] + me.idPostfix;
				var lab = document.getElementById(labId);
				if (lab) {
					lab.style.color = "#FF0000";
				}
			}
		} else {
			for (var i = 0, len = labels.length; i < len; i++) {
				var labId = labels[i] + me.idPostfix;
				var lab = document.getElementById(labId);
				if (lab) {
					lab.style.color = "#000000";
				}
			}
		}
	},
	setMustFldsMust : function(flds, must, me) {
		// 设置字段是否必填可用
		if (must) {
			for (var i = 0, len = flds.length; i < len; i++) {
				if (flds[i].indexOf('.') != -1) {
					eval(flds[i] + ".enable();" + flds[i]
							+ ".allowBlank = false;" + flds[i]
							+ ".invalidText = '此字段为必填项';" + flds[i]
							+ ".regex = " + /(^\S+)/ + ";" + flds[i]
							+ ".regexText = '输入的值前面不能有空格字符';" + flds[i]
							+ ".validate();");
				} else {
					var cfId = flds[i] + me.idPostfix;
					var cf = document.getElementById(cfId);
					if (cf) {
						if (cf.type == "text") {
							var cfv = cf.value;
							if (cfv && cfv.length > 0 && cf.value!=cf.defaultValue ) {
								cf.disabled = false;
								this.removeClass(cf, "x-form-invalid");
							}else if(cfv && cfv.length > 0){
							cf.disabled = false;
							} else {
								this.addClass(cf, "x-form-invalid");
								cf.disabled = false;
								cf.value = cf.defaultValue || '';
								// cf.style.color = "#999";
							}
						}
						if (cf.type == "checkbox" || cf.type == "radio") {
							cf.disabled = false;
						}
					} else {
						var cfDivId = "div_" + flds[i] + me.idPostfix;
						var cfDiv = document.getElementById(cfDivId);
						if (cfDiv) {
							var Flds = document.getElementsByName(flds[i]);
							for (var j = 0; j < Flds.length; j++) {
								var itemFld = Flds[j];
								var handleFun = function(fldId, alias, obj, me) {
									return function() {
										me.dicFldValidateClick(fldId, alias,
												obj, me);
									}
								}
								this.addEvent(itemFld, "click", handleFun(
												flds[i], cfDiv.title, itemFld,
												me));
							}
							var dicVal = me.getHtmlFldValue(flds[i]);
							if (dicVal && dicVal.length > 0) {
								me.removeClass(cfDiv, "x-form-invalid");
							} else {
								me.addClass(cfDiv, "x-form-invalid");
							}
						}
					}
				}
			}
		} else {
			for (var i = 0, len = flds.length; i < len; i++) {
				if (flds[i].indexOf('.') != -1) {
					eval(flds[i] + ".allowBlank = true;" + flds[i]
							+ ".validate()");
				} else {
					var cfId = flds[i] + me.idPostfix;
					var cf = document.getElementById(cfId);
					if (cf) {
						cf.disabled = true;
						if (cf.type == "text") {
							cf.value = "";
							me.removeClass(cf, "x-form-invalid");
						}
						if (cf.type == "checkbox" || cf.type == "radio") {
							cf.checked = false;
						}
					} else {
						var cfDivId = "div_" + flds[i] + me.idPostfix;
						var cfDiv = document.getElementById(cfDivId);
						if (cfDiv) {
							me.removeClass(cfDiv, "x-form-invalid");
						}
					}
				}
			}
		}
	},
	setFldsDisabled : function(flds, disabled, me, defaultValue) {
		// 设置字段是否可用
		var frmEl = this.form.getEl();
		if (disabled) {
			for (var i = 0, len = flds.length; i < len; i++) {
				if (flds[i].indexOf('.') != -1) {
					eval(flds[i] + ".setValue();" + flds[i] + ".disable()");
				} else {
					var cfId = flds[i] + me.idPostfix;
					var cf = document.getElementById(cfId);
					if (!frmEl.contains(cf)) {
						continue;
					}
					if (cf) {
						cf.disabled = true;
						if (cf.type == "text") {
							cf.value = "";
							this.removeClass(cf, "x-form-invalid");
						}
						if (cf.type == "checkbox" || cf.type == "radio") {
							cf.checked = false;
							var cfDivId = "div_"
									+ cf.id.substring(0, cf.id.indexOf('_'))
									+ me.idPostfix;
							var cfDiv = document.getElementById(cfDivId);
							if (cfDiv) {
								this.removeClass(cfDiv, "x-form-invalid");
							}
						}
					}
				}
			}
		} else {
			for (var i = 0, len = flds.length; i < len; i++) {
				if (flds[i].indexOf('.') != -1) {
					eval(flds[i] + ".enable()");
					if (this.needSetDefaultValue && defaultValue
							&& defaultValue[flds[i]]) {
						eval(flds[i] + ".setValue(" + defaultValue[flds[i]]
								+ ")");
					}
				} else {
					var cfId = flds[i] + me.idPostfix;
					var cf = document.getElementById(cfId);
					if (!frmEl.contains(cf)) {
						continue;
					}
					if (cf) {
						cf.disabled = false;
						if (this.needSetDefaultValue && defaultValue
								&& defaultValue[flds[i]]) {
							if (cf.type == "text") {
								cf.value = defaultValue[flds[i]];
								this.removeClass(cf, "x-form-invalid");
							}
							if (cf.type == "checkbox" || cf.type == "radio") {
								cf.checked = defaultValue[flds[i]];
								var cfDivId = "div_"
										+ cf.id
												.substring(0, cf.id
																.indexOf('_'))
										+ me.idPostfix;
								var cfDiv = document.getElementById(cfDivId);
								if (cfDiv) {
									this.removeClass(cfDiv, "x-form-invalid");
								}
							}
						}
					}
				}
			}
		}
	},
	textOnChange : function(obj, cArr, me) {
		var co = cArr[0];
		var cFlds = co.field;
		var dFlds = co.disField;
		var mustFlds = co.mustField;
		var redLabels = co.redLabels;
		var omf = co.oppositeMF;
		var orl = co.oppositeRL;
		// 关联控制字段 中text(文本框) 的单击事件处理方法
		var v = obj.value;
		if (v != '') {
			if (cFlds && cFlds.length > 0) {
				me.setFldsDisabled(cFlds, false, me);
			}
			if (dFlds && dFlds.length > 0) {
				me.setFldsDisabled(dFlds, true, me);
			}
			if (mustFlds && mustFlds.length > 0) {
				me.setMustFldsMust(mustFlds, false, me);
			}
			if (redLabels && redLabels.length > 0) {
				me.setRedLabel(redLabels, true, me);
			}
			if (omf && omf.length > 0) {
				me.setMustFldsMust(omf, false, me);
			}
			if (orl && orl.length > 0) {
				me.setRedLabel(orl, true, me);
			}
		} else {
			if (cFlds && cFlds.length > 0) {
				me.setFldsDisabled(cFlds, true, me);
			}
			if (dFlds && dFlds.length > 0) {
				me.setFldsDisabled(dFlds, false, me);
			}
			if (mustFlds && mustFlds.length > 0) {
				me.setMustFldsMust(mustFlds, true, me);
			}
			if (redLabels && redLabels.length > 0) {
				me.setRedLabel(redLabels, false, me);
			}
			if (omf && omf.length > 0) {
				me.setMustFldsMust(omf, true, me);
			}
			if (orl && orl.length > 0) {
				me.setRedLabel(orl, false, me);
			}
		}
	},
	checkOnClick : function(obj, co, me) {
		// 关联控制字段 中checkbox（复选框） 的单击事件处理方法
		var cFlds = co.field;
		var dFlds = co.disField;
		var mustFlds = co.mustField;
		var redLabels = co.redLabels;
		var omf = co.oppositeMF;
		var orl = co.oppositeRL;
		if (obj.checked) {
			if (cFlds && cFlds.length > 0) {
				me.setFldsDisabled(cFlds, false, me);
			}
			if (dFlds && dFlds.length > 0) {
				me.setFldsDisabled(dFlds, true, me);
			}
			if (mustFlds && mustFlds.length > 0) {
				me.setMustFldsMust(mustFlds, true, me);
			}
			if (redLabels && redLabels.length > 0) {
				me.setRedLabel(redLabels, true, me);
			}
			if (omf && omf.length > 0) {
				me.setMustFldsMust(omf, false, me);
			}
			if (orl && orl.length > 0) {
				me.setRedLabel(orl, true, me);
			}
		} else {
			if (cFlds && cFlds.length > 0) {
				me.setFldsDisabled(cFlds, true, me);
			}
			if (dFlds && dFlds.length > 0) {
				me.setFldsDisabled(dFlds, false, me);
			}
			if (mustFlds && mustFlds.length > 0) {
				me.setMustFldsMust(mustFlds, false, me);
			}
			if (redLabels && redLabels.length > 0) {
				me.setRedLabel(redLabels, false, me);
			}
			if (omf && omf.length > 0) {
				me.setMustFldsMust(omf, true, me);
			}
			if (orl && orl.length > 0) {
				me.setRedLabel(orl, false, me);
			}
		}
	},
	radioOnClick : function(fldName, cArr, me) {
		// //关联控制字段 中radiobox(单选) 的单击事件处理方法
		var flds = document.getElementsByName(fldName);
		var frmEl = this.form.getEl();
		var fldValue = "";
		for (var i = 0, len = flds.length; i < len; i++) {
			var f = flds[i];
			if (!frmEl.contains(f)) {
				continue;
			}
			if (f.checked) {
				fldValue = f.value;
			}
		}
		for (var g = 0, cLen = cArr.length; g < cLen; g++) {
			var enabled = false;
			var cFlds = [], disField = [], mFlds = [], redLabs = [], omf = [], orl = [], defaultValue = {};
			var co = cArr[g];
			var key = co.key;
			var exp = co.exp;
			cFlds = co.field;
			disField = co.disField;
			mFlds = co.mustField;
			redLabs = co.redLabels;
			omf = co.oppositeMF;
			orl = co.oppositeRL;
			defaultValue = co.defaultValue
			if (exp == "eq") {
				if (key == fldValue) {
					enabled = true;
				}
			}
			if (exp == "ne") {
				if (key != fldValue) {
					enabled = true;
				}
			}
			if (cFlds && cFlds.length > 0) {
				if (enabled) {
					me.setFldsDisabled(cFlds, false, me, defaultValue);
				} else {
					me.setFldsDisabled(cFlds, true, me, defaultValue);
				}
			}
			if (disField && disField.length > 0) {
				if (!enabled) {
					me.setFldsDisabled(disField, true, me, defaultValue);
				} else {
					me.setFldsDisabled(disField, false, me, defaultValue);
				}
			}
			if (mFlds && mFlds.length > 0) {
				if (enabled) {
					me.setMustFldsMust(mFlds, true, me);
				} else {
					me.setMustFldsMust(mFlds, false, me);
				}
			}
			if (omf && omf.length > 0) {
				if (!enabled) {
					me.setMustFldsMust(omf, true, me);
				} else {
					me.setMustFldsMust(omf, false, me);
				}
			}
			if (redLabs && redLabs.length > 0) {
				if (enabled) {
					me.setRedLabel(redLabs, true, me);
				} else {
					me.setRedLabel(redLabs, false, me);
				}
			}
			if (orl && orl.length > 0) {
				if (enabled) {
					me.setRedLabel(orl, false, me);
				} else {
					me.setRedLabel(orl, true, me);
				}
			}
		}
	},
	/**
	 * 重置 this.otherDisable 中配置的控制，立即执行 --如果说controlOtherFld()
	 * 是让this.otherDisable 中配置的实现 --resetControlOtherFld() 则是让this.otherDisable
	 * 中配置立即生效
	 */
	resetControlOtherFld : function() {
		var len = this.otherDisable.length;
		var me = this;
		for (var i = 0; i < len; i++) {
			var od = this.otherDisable[i];
			var cArr = od.control;
			var type = od.type;
			var fld = od.fld;
			if (type == "text") {
				var ftId = fld + this.idPostfix;
				var fldText = document.getElementById(ftId);
				this.textOnChange(fldText, cArr, me);
			}
			if (type == "checkbox") {
				for (var j = 0, cLen = cArr.length; j < cLen; j++) {
					var co = cArr[j];
					var key = co.key;
					var fId = od.fld + "_" + key + this.idPostfix;
					var fObj = document.getElementById(fId);
					if (!fObj) {
						continue;
					}
					this.checkOnClick(fObj, co, me);
				}
			}
			if (type == "radio") {
				var fldName = od.fld;
				var fldes = document.getElementsByName(fldName);
				if (fldes && fldes.length > 0) {
					this.radioOnClick(fldName, cArr, me);
				}
			}
		}
	},
	// ===============公用工具类型方法=====================
	dateCtrl : function(dateFld, type, date) {
		// type:['min','max'] 将日期字段dateFld的最大(max)/最小(min)值设置为date
		if (type == "max") {
			dateFld.setMaxValue(date);
		}
		if (type == "min") {
			dateFld.setMinValue(date);
		}
	},
	getObjsByClass : function(clsName) {
		// 依据样式名称获取DOM对象
		var tags = this.tags || document.getElementsByTagName("*");
		var list = [];
		for (var k in tags) {
			var tag = tags[k];
			if (tag.className == clsName) {
				list.push(tag);
			}
		}
		return list;
	},
	/**
	 * 获取HTML模板中字段值
	 * 
	 * @param {}
	 *            fldName 字段名称 schema中item的id
	 */
	getHtmlFldValue : function(fldName) {
		var fldValue = "";
		var flds = document.getElementsByName(fldName);
		var frmEl = this.form.getEl();
		var vs = [];
		for (var i = 0, len = flds.length; i < len; i++) {
			var f = flds[i];
			if (!frmEl.contains(f)) {
				continue;
			}
			if (f.type == "text" || f.type == "hidden") {
				vs.push(f.value || '');
			}
			if (f.type == "radio" || f.type == "checkbox") {
				if (f.checked) {
					vs.push(f.value);
				}
			}
		}
		fldValue = vs.join(',');
		return fldValue;
	},
	/**
	 * 给HTML模板中字段赋值
	 * 
	 * @param {}
	 *            fldName 字段名称 schema中item的id
	 * @param {}
	 *            fldValue 字段值
	 */
	setHtmlFldValue : function(fldName, fldValue) {
		var flds = document.getElementsByName(fldName);
		var frmEl = this.form.getEl();
		// 清原值
		for (var j = 0, n = flds.length; j < n; j++) {
			var f = flds[j];
			if (!frmEl.contains(f)) {
				continue;
			}
			if (f.type == "radio" || f.type == "checkbox") {
				f.checked = false;
			}
		}
		// 赋值
		for (var i = 0, len = flds.length; i < len; i++) {
			var f = flds[i];
			if (!frmEl.contains(f)) {
				continue;
			}
			if (f.type == "text" || f.type == "hidden") {
				f.value = fldValue || '';
				if (fldValue && fldValue.length > 0) {
					this.removeClass(f, "x-form-invalid");
				}
			}
			if (f.type == "radio") {
				if (f.value == fldValue) {
					f.checked = true;
					var divId = "div_" + fldName + this.idPostfix;
					var div = document.getElementById(divId);
					if (div) {
						this.removeClass(div, "x-form-invalid");
					}
					break;
				}
			}
			if (f.type == "checkbox") {
				var vs = fldValue.split(',');
				for (var vi = 0, vlen = vs.length; vi < vlen; vi++) {
					if (f.value == vs[vi]) {
						f.checked = true;
						var divId = "div_" + fldName + this.idPostfix;
						var div = document.getElementById(divId);
						if (div) {
							this.removeClass(div, "x-form-invalid");
						}
					}
				}
			}
		}
	},
	createFldSaveValidate : function(cobj) {
		// 扩展对创建字段的验证
	},
	/**
	 * 身份证号 验证
	 * 
	 * @param {}
	 *            pId 字段值
	 * @param {}
	 *            id 字段名称
	 */
	checkIdcard : function(pId, id) {
		var falg = true;
		obj = document.getElementById(id + this.idPostfix);
		var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
		var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
		var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
		if (pId.length != 15 && pId.length != 18) {
			this.addClass(obj, "x-form-invalid");
			obj.title = "身份证号共有 15 码或18位";
			falg = false;
			return;
			// return "身份证号共有 15 码或18位";
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}

		var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0, 6)
				+ "19" + pId.slice(6, 16);
		if (!/^\d+$/.test(Ai)) {
			this.addClass(obj, "x-form-invalid");
			obj.title = "身份证除最后一位外，必须为数字！";
			falg = false;
			return;
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}
		var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
				.slice(12, 14);
		var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
				.getMonth(), day = d.getDate(), now = Date.parseDate(
				this.mainApp.serverDate, "Y-m-d");
		if (year != yyyy || mon + 1 != mm || day != dd || d > now
				|| now.getFullYear() - year > 110
				|| !this.isValidDate(dd, mm, yyyy)) {
			this.addClass(obj, "x-form-invalid");
			obj.title = "身份证输入错误！";
			falg = false;
			return;
		} else {
			this.removeClassMy(obj, "x-form-invalid");

		}
		for (var i = 0, ret = 0; i < 17; i++) {
			ret += Ai.charAt(i) * Wi[i];
		}
		Ai += arrVerifyCode[ret %= 11];
		return pId.length == 18 && pId.toLowerCase() != Ai ? "身份证输入错误！" : Ai;
		if (!flag) {
			return false;
		}
	},
	/**
	 * HTML表单上保存数据里调用，验证数据填写时否合理 如果有一个字段验证不通过就结束下面的验证 返回 false ,并向JS控制台输出日志
	 * 
	 * @param {}
	 *            schema
	 * @return {true | false} true ：验证通过，false 验证未通过
	 */
	htmlFormSaveValidate : function(schema) {
		if (!schema) {
			schema = this.schema;
		}
		var validatePass = true;
		var items = schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			if (it.display
					&& (it.display == "1" || it.display == 0 || it.hidden)) {
				continue;
			}
			if (this.isCreateField(it.id)) {
				var isLawful = true;
				isLawful = eval("this." + it.id + ".validate()");
				if (!isLawful) {
					validatePass = false;
					eval("this." + it.id + ".focus(true,200)");
					// console.log("-->this." + it.id + "验证未通过。");
					MyMessageTip.msg("提示", "表格"+it.alias + "为必填项", true);
					this.createFldSaveValidate(it.id);
					break;
				}
				if (it['not-null'] == "1" || it['not-null'] == "true") {
					var value = eval("this." + it.id + ".getValue()");
					if (!value || value == "") {
						validatePass = false;
						eval("this." + it.id + ".focus(true,200)");
						eval("this." + it.id + ".setValue()");
						// console.log("-->this." + it.id + "验证未通过。");
						MyMessageTip.msg("提示", "表格"+it.alias + "为必填项", true);
						this.createFldSaveValidate(it.id);
						break;
					}
				}
				if (validatePass == false) {
					break;
				}
			} else {
				if (it.dic) {
					if (it['not-null'] == "1" || it['not-null'] == "true") {
						var dfv = this.getHtmlFldValue(it.id);
						var divId = "div_" + it.id + this.idPostfix;
						var div = document.getElementById(divId);
						if (div) {
							if (dfv && dfv.length > 0) {
								this.removeClass(div, "x-form-invalid");
								div.title = "";
							} else {
								this.addClass(div, "x-form-invalid");
								div.title = "表单"+it.alias + "为必填项";
								validatePass = false;
								if (document.getElementsByName(it.id)[0]) {
									document.getElementsByName(it.id)[0]
											.focus();
								}
								// console.log("-->" + it.id + " " + it.alias
								// + " 为必填项")
								MyMessageTip.msg("提示", div.title, true);
								break;
							}
						}
					}
				} else {
					var fld = document.getElementById(it.id + this.idPostfix);
					if (!fld) {
						continue;
					}
					if ((it['not-null'] == "1" || it['not-null'] == "true" || this
							.isMustField(it))
							&& !it["pkey"]) {// 跳过主键必填验证
						if (fld.value == ""
								|| (fld.value == fld.defaultValue && !fld.hidden)) {
							this.addClass(fld, "x-form-invalid");
							fld.title = "表单"+it.alias + "为必填项";
							validatePass = false;
							if (!document
									.getElementById(it.id + this.idPostfix)) {
								continue;
							}
							document.getElementById(it.id + this.idPostfix)
									.focus();
							document.getElementById(it.id + this.idPostfix)
									.select();
							// console.log("-->" + it.id + " " + it.alias
							// + " 为必填项")
							MyMessageTip.msg("提示", fld.title, true);
							break;
						} else {
							this.removeClass(fld, "x-form-invalid");
							fld.title = it.alias
						}
					}
					var obj = fld;
					switch (it.type) {
						case "string" :
							if (typeof it.vtype != 'undefined') {
								if (it.vtype == "idCard") {
									var pId = obj.value;
									if (pId) {
										var flag = this.checkIdcard(pId, it.id);
										if (flag) {
											this.removeClass(obj,
													"x-form-invalid");
											obj.title = it.alias
										} else {
											validatePass = false;
											this
													.addClass(obj,
															"x-form-invalid")
											obj.title = it.alias + "不合法";
											MyMessageTip.msg("提示", obj.title,
													true);
										}
									}

								}
							} else {
								var maxLength = it.length;
								var fv = fld.value;
								var fvLen = this.getStrSize(fv);
								if (fvLen > maxLength) {
									this.addClass(obj, "x-form-invalid")
									obj.title = it.alias + "中输入的字符串超出定义的最大长度（"
											+ maxLength + "）";
									validatePass = false;
									if (!document.getElementById(it.id
											+ this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
									// console.log("-->" + it.id + " " +
									// it.alias
									// + "中输入的字符串超出定义的最大长度（" + maxLength
									// + "）")
									MyMessageTip.msg("提示", obj.title, true);
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							break;
						case 'int' :
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var fv = obj.value;
							if (fv == obj.defaultValue) {// 跳过注释文字验证
								continue;
							}
							var reg = new RegExp("^[0-9]*$");
							var length = it.length;
							if (length) {
								reg = new RegExp("^[0-9]{0," + length + "}$");
							}
							var fid = it.id;
							if (!reg.test(fv)) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入了非整数 数字或字符"
								if (length) {
									obj.title = obj.title + "或超过最大长度（" + length
											+ ")";
								}
								validatePass = false;
								if (!document.getElementById(it.id
										+ this.idPostfix)) {
									continue;
								}
								document.getElementById(it.id + this.idPostfix)
										.focus();
								document.getElementById(it.id + this.idPostfix)
										.select();
								// console.log("-->" + it.id + " " + it.alias
								// + "中输入了非整数 数字或字符")
								MyMessageTip.msg("提示", obj.title, true);
								break;
							} else {
								this.removeClass(obj, "x-form-invalid");
								obj.title = it.alias
							}
							if (typeof(minValue) != 'undefined') {
								if (parseInt(fv) < minValue) {
									this.addClass(obj, "x-form-invalid")
									obj.title = it.alias + "中输入的值小于了定义的最小值（"
											+ minValue + "）";
									validatePass = false;
									if (!document.getElementById(it.id
											+ this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
									// console.log("-->" + it.id + " " +
									// it.alias
									// + "中输入的值小于了定义的最小值（" + minValue
									// + "）")
									MyMessageTip.msg("提示", obj.title, true);
									break;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							if (typeof(maxValue) != 'undefined') {
								if (parseInt(fv) > maxValue) {
									this.addClass(obj, "x-form-invalid")
									obj.title = it.alias + "中输入的值大于了定义的最大值（"
											+ maxValue + "）";
									validatePass = false;
									if (!document.getElementById(it.id
											+ this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
									// console.log("-->" + it.id + " " +
									// it.alias
									// + "中输入的值大于了定义的最大值（" + maxValue
									// + "）")
									MyMessageTip.msg("提示", obj.title, true);
									break;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							break;
						case "double" :
							var length = it.length;
							var precision = it.precision;
							var maxValue = it.maxValue;
							var minValue = it.minValue;
							var dd = 0;
							if (typeof(precision) != 'undefined') {
								dd = parseInt(precision);
							}
							var iNum = length - dd - 1;
							var regStr = "(^[0-9]{0," + iNum + "}$)|(^[0-9]{0,"
									+ iNum + "}(\\.[0-9]{0," + dd + "})?$)";
							if (dd == 0) {
								regStr = "(^[0-9]{0," + iNum + 1
										+ "}$)|(^[[0-9]*\\.[0-9]*]{0," + iNum
										+ "}$)";
							}
							var reg = new RegExp(regStr);
							var fv = obj.value;
							if (fv == obj.defaultValue) {// 跳过注释文字验证
								continue;
							}
							if (fv && fv.length > iNum) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入的值错误";
								validatePass = false;
								if (!document.getElementById(it.id
										+ this.idPostfix)) {
									continue;
								}
								document.getElementById(it.id + this.idPostfix)
										.focus();
								document.getElementById(it.id + this.idPostfix)
										.select();
								MyMessageTip.msg("提示", obj.title, true);
								break;
							} else {
								obj.title = it.alias;
								this.removeClass(obj, "x-form-invalid");
							}
							if (!reg.test(fv)) {
								this.addClass(obj, "x-form-invalid")
								obj.title = it.alias + "中输入了非浮点型数据或字符"
								validatePass = false;
								if (!document.getElementById(it.id
										+ this.idPostfix)) {
									continue;
								}
								document.getElementById(it.id + this.idPostfix)
										.focus();
								document.getElementById(it.id + this.idPostfix)
										.select();
								// console.log("-->" + it.id + " " + it.alias
								// + "中输入了非浮点型数据或字符")
								MyMessageTip.msg("提示", obj.title, true);
								break;
							} else {
								this.removeClass(obj, "x-form-invalid");
								obj.title = it.alias
							}
							if (typeof(minValue) != 'undefined') {
								if (parseInt(fv) < minValue) {
									this.addClass(obj, "x-form-invalid");
									obj.title = it.alias + "中输入的值小于了定义的最小值（"
											+ minValue + "）"
									validatePass = false;
									if (!document.getElementById(it.id
											+ this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
									// console.log("-->" + it.id + " " +
									// it.alias
									// + "中输入的值小于了定义的最小值（" + minValue
									// + "）")
									MyMessageTip.msg("提示", obj.title, true);
									break;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							if (typeof(maxValue) != 'undefined') {
								if (parseInt(fv) > maxValue) {
									this.addClass(obj, "x-form-invalid");
									obj.title = it.alias + "中输入的值大于了定义的最大值（"
											+ maxValue + "）"
									validatePass = false;
									if (!document.getElementById(it.id
											+ this.idPostfix)) {
										continue;
									}
									document.getElementById(it.id
											+ this.idPostfix).focus();
									document.getElementById(it.id
											+ this.idPostfix).select();
									// console.log("-->" + it.id + " " +
									// it.alias
									// + "中输入的值大于了定义的最大值（" + maxValue
									// + "）")
									MyMessageTip.msg("提示", obj.title, true);
									break;
								} else {
									this.removeClass(obj, "x-form-invalid");
									obj.title = it.alias
								}
							}
							break;
					}
					if (validatePass == false) {
						break;
					}
				}
			}
		}
		return validatePass;
	},
	// 支持快捷键
	afterCreateWin : function(win) {
		win.instance = this;
	},
	isMustField : function(it) {
		var len = this.otherDisable.length;
		var me = this;
		var itId = it.id;
		if (it.dic) {
			itId = "div_" + it.id;
		}
		for (var i = 0; i < len; i++) {
			var od = this.otherDisable[i];
			var cArr = od.control;
			var type = od.type;
			var fld = od.fld;
			var fldValue = this.getHtmlFldValue(fld);
			for (var j = 0; j < cArr.length; j++) {
				var co = cArr[j];
				var mustFlds = co.mustField;
				var exp = co.exp;
				var key = co.key;
				if (exp == "eq" && fldValue != key) {
					continue;
				}
				if (exp == "ne" && fldValue == key) {
					continue;
				}
				if (mustFlds) {
					for (var m = 0; m < mustFlds.length; m++) {
						var mF = document.getElementById(mustFlds[m]
								+ this.idPostfix);
						if (mF && mustFlds[m] == itId) {
							return true;
						}
					}

				}
			}
		}
		return false;
	}
});