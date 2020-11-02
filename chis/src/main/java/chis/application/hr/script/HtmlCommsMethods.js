$package("chis.application.hr.script")
chis.application.hr.script.HtmlCommsMethods = {
	// text类型的获取值
	getValueById : function(id) {
		return document.getElementById(id + "_" + this.id).value
	},
	// radio类型的获取值
	getRadioValue : function(id) {
		var v = document.getElementsByName(id + "_" + this.id);
		var le = v.length;
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				return v[i].value;
			}
		}
		return 0;
	},
	// checkBox类型的获取值
	getCheckBoxValues : function(id) {
		var v = document.getElementsByName(id + "_" + this.id);
		var le = v.length;
		var value = new Array();
		for (var i = 0; i < le; i++) {
			if (v[i].checked) {
				value.push(v[i].value);
			}
		}
		return value;
	},
	// 取下拉框被选中的值,传入select的id
	getSelect : function(id) {
		var obj = document.getElementById(id + "_" + this.id); // selectid
		var index = obj.selectedIndex; // 选中索引
		// var text = obj.options[index].text; // 选中文本
		var year = obj.options[index].value; // 选中值
		return year;
	},
	// text类型的赋值
	setValueById : function(id, v) {
		document.getElementById(id + "_" + this.id).value = v;
	},
	// radio类型的赋值
	setRadioValue : function(id, v) {
		var dom = document.getElementsByName(id + "_" + this.id);
		var le = dom.length;
		for (var i = 0; i < le; i++) {
			if (dom[i].value == v) {
				dom[i].checked = true;
				break;
			}

		}
	},// 给下拉框赋值
	setSelectValue : function(id, v) {
		var dom = document.getElementById(id + "_" + this.id);
		// var le = dom.length;
		var le = dom.options.length;
		for (var i = 0; i < le; i++) {
			if (dom.options[i].value == v) {
				dom.options[i].selected = true;
				break;
			}
		}
	},
	// checkBox类型的赋值
	setCheckBoxValues : function(id, v) {
		var dom = document.getElementsByName(id + "_" + this.id);
		var k = dom.length;
		// 下面代码是后台传值list或者string, 二选一 确定后台传值类型后 将另一个判断删掉
		if (typeof v == "object") {// 如果值是list类型
			var l = v.length;
			for (var i = 0; i < l; i++) {
				for (var j = 0; j < k; j++) {
					if (v[i] == dom[j].value) {
						dom[j].checked = true;
						break;
					}
				}
			}
		} else {
			if (v.indexOf(",") > -1) {
				value = v.split(",");
				var l = value.length;
				for (var i = 0; i < l; i++) {
					for (var j = 0; j < k; j++) {
						if (value[i] == dom[j].value) {
							dom[j].checked = true;
							break;
						}
					}
				}
			}
		}
	},
	// 给单选框添加事件：当为必填项时候，保存的时候，如果为空，就将整个td的边框为红色
	radioToCss : function(name) {
		var _cfg = this;
		var obj = document.getElementsByName(name + "_" + this.id);
		var handleFun = function(objValue, name, _cfg) {
			return function() {
				_cfg.FooRadioToCss(objValue, name, _cfg);
			}
		}
		for (var j = 0; j < obj.length; j++) {
			var objValue = obj[j];
			if (window.attachEvent) {// IE的事件代码
				obj[j].attachEvent("onclick", handleFun(objValue.value, name,
								_cfg));
			} else {
				obj[j].addEventListener("click", handleFun(objValue.value,
								name, _cfg), false);
			}
		}
	},
	FooRadioToCss : function(v, n,_cfg) {
		var obj = document.getElementById(n+"ToCss_"+this.id);
		obj.style.border = "";
	},
	// 给控件动态加css
	addClass : function(id) {
		var obj = document.getElementById(id+"ToCss_"+this.id);
		obj.style.border = "2px solid red";
	},
	createField : function(it, i) {
		var ac = util.Accredit;
		var defaultWidth = it.width || 100
		var cfg = {
			name : it.id,
			// fieldLabel : it.alias,
			xtype : it.xtype || "textfield",
			vtype : it.vtype,
			width : defaultWidth,
			height : this.defaultHeight || it.height,
			value : it.defaultValue,
			enableKeyEvents : it.enableKeyEvents,
			validationEvent : it.validationEvent
		}
		if (it.xtype == "checkbox") {
			cfg.fieldLabel = null;
			cfg.boxLabel = it.alias;
		}

		// if (it.renderTo) {
		// cfg.renderTo = it.renderTo
		// }

		if (it.xtype == "uxspinner") {
			cfg.strategy = {}
			cfg.strategy.xtype = it.spin_xtype;
		}
		// .........
		cfg.listeners = {
			specialkey : this.onFieldSpecialkey,
			// add by liyl 2012-06-17 去掉输入字符串首位空格
			// blur : function(e) {
			// if (typeof(e.getValue()) == 'string') {
			// e.setValue(e.getValue().trim())
			// }
			// },
			scope : this
		}

		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it.editable) {
			cfg.editable = true
		}
		if (it['not-null'] && !it.fixed) {
			// alert( cfg.fieldLabel)
			// Ext.getDom(it.id+this.id).addClass('background:#E6E6E6;cursor:default;');
			//				

			cfg.allowBlank = false
			// cfg.invalidText = "必填字段"
			// cfg.fieldLabel = "<span style='color:red'>"
			// + cfg.fieldLabel + "</span>"
		}
		if (it['showRed']) {
			cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
					+ "</span>"
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
		if (it.dic) {
			// add by lyl, check treecheck length
			if (it.dic.render == "TreeCheck") {
				if (it.length) {
					cfg.maxLength = it.length;
				}

			}

			it.dic.src = this.entryName + "." + it.id
			it.dic.defaultValue = it.defaultValue
			it.dic.width = 50
			if (it.dic.fields) {
				if (typeof(it.dic.fields) == 'string') {
					var fieldsArray = it.dic.fields.split(",")
					it.dic.fields = fieldsArray;
				}

			}
			var dic = it.dic;
			if (it.tag == "radioGroup") {
				var store = util.dictionary.SimpleDicFactory.getStore(dic)
				store.load()
				var _ctr = this;
				store.on("load", function() {
					var count = this.getCount();
					var items = new Array();
					for (var i = 0; i < count; i++) {
						items.push({
									name : it.id,
									boxLabel : this.getAt(i).get('text'),
									inputValue : this.getAt(i).get('key')
								})
					}
					var raidoGroup = new Ext.form.RadioGroup({
								width : it.width || 300,
								hideLabel : true,
								labelSeparator : "",
								// disabled : true,
								renderTo : it.id,
								items : items
							})
						// raidoGroup.index=i;
						// raidoGroup.render(it.id);
						// _ctr.form.add(raidoGroup);
					});

				return null;
			}
			// return util.dictionary.SimpleDicFactory.createDic(dic)
			var cmbCfg = {
				store : util.dictionary.SimpleDicFactory.getStore(dic),
				valueField : "key",
				displayField : "text",
				searchField : dic.searchField || "mCode",
				editable : (dic.editable != undefined) ? dic.editable : true,
				minChars : 2,
				selectOnFocus : true,
				triggerAction : dic.remote ? "query" : "all",
				pageSize : dic.pageSize,
				hideTrigger : it.fixed,
				width : dic.width || 200,
				listWidth : dic.listWidth,
				value : dic.defaultValue
			}
			if (!it.fixed) {
				cmbCfg.emptyText = dic.emptyText || '请选择';
			} else {
				cmbCfg.style = "border-style:none;background-image:none;";
			}
			var combox = new util.widgets.MyCombox(cmbCfg)
			Ext.apply(combox, cfg)
			combox.on("specialkey", this.onFieldSpecialkey, this)
			if (dic.autoLoad) {
				combox.store.load()
			}

			return combox;
		}
		if (it.fixed) {
			cfg.style = "border-style:none;background-image:none;";
		} else {
			// cfg.style =
			// "border-style:none;background-image:none;border-bottom-style:dashed;";
		}
		if (it.length) {
			cfg.maxLength = it.length;
		}
		if (it.xtype) {
			if (it.xtype == 'checkbox') {
				return new Ext.form.Checkbox(cfg);
			}

			return new Ext.form.Field(cfg);
		}
		switch (it.type) {
			case 'int' :
			case 'double' :
			case 'bigDecimal' :
				cfg.xtype = "numberfield"
				cfg.style = "color:#00AA00;font-weight:bold;text-align:right;border-style:none;background-image:none;border-bottom-style:dashed;";
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
					cfg.maxValue = it.maxValue.substring(0, 10);
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
				cfg.width = it.width || 1000
				cfg.height = it.height || 300
				// cfg.html = "<span style='color:red'>测试</span>"
				break;

		}
		if (cfg.xtype == 'numberfield') {
			return new Ext.form.NumberField(cfg);
		}
		if (cfg.xtype == 'datefield') {
			return new Ext.form.DateField(cfg);
			// alert(Ext.encode(cfg))
		}
		// if(cfg.xtype == 'textfield'){
		// return new Ext.form.TextField(cfg)
		// //alert(Ext.encode(cfg))
		// }

		return new Ext.form.Field(cfg);
	}

}