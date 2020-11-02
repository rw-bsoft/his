/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("app.modules.form.TableFormView", "chis.script.BizCommon",
		"chis.script.BizFormCommon")

chis.script.BizTableFormView = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.isAutoScroll = cfg.isAutoScroll || false;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.autoHeight = !cfg.isAutoScroll
	cfg.saveServiceId = cfg.saveServiceId || "chis.simpleSave"
	cfg.loadServiceId = cfg.loadServiceId || "chis.simpleLoad"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizFormCommon);
	chis.script.BizTableFormView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizTableFormView, app.modules.form.TableFormView, {

	changeCfg : function(cfg) {
		if (this.isAutoScroll) {
			delete cfg.autoWidth;
			delete cfg.autoHeight;
			cfg.autoScroll = true;
		}
	},

	getLoadRequest : function() {
		if (this.initDataId) {
			return {
				pkey : this.initDataId
			};
		} else {
			return null;
		}
	},

	focusFieldAfter : function(index, delay) {
		var items = this.schema.items
		var form = this.form.getForm()
		for (var i = index + 1; i < items.length; i++) {
			var next = items[i]
			var field = form.findField(next.id)
			if (field && !field.disabled && field.xtype != "imagefield") {
				field.focus(false, delay || 200)
				return;
			}
		}
		var btns;
		if (this.showButtonOnTop && this.form.getTopToolbar()) {
			btns = this.form.getTopToolbar().items
			if (btns) {
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					if (btn.cmd == "save") {
						if (btn.rendered) {
							btn.focus()
						}
						return;
					}
				}
			}
		} else {
			btns = this.form.buttons;
			if (btns) {
				var n = btns.length
				for (var i = 0; i < n; i++) {
					var btn = btns[i]
					if (btn.cmd == "save") {
						if (btn.rendered) {
							btn.focus()
						}
						return;
					}
				}
			}
		}
	},

	doNew : function() {
		chis.script.BizTableFormView.superclass.doNew.call(this);
		if (this.initDataId) {
			this.fireEvent("beforeUpdate", this); // **
			// 在数据加载之前做一些初始化操作
		} else {
			this.fireEvent("beforeCreate", this); // ** 在页面新建时做一些初始化操作
		}
	},

	onReady : function() {
		// ** 设置滚动条
		if (this.isAutoScroll) {
			this.form.setWidth(this.getFormWidth());
			this.form.setHeight(this.getFormHeight());
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
		// 屏蔽框架自带的快捷键
		// keyMap.on(keys, this.onAccessKey, this)
		if (this.win) {
			keyMap.on({
						key : Ext.EventObject.ESC,
						shift : true
					}, this.onEsc, this)
		}
	},
	afterCreateWin : function(win) {
		win.instance = this;
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
			value : it.defaultValue,
			enableKeyEvents : it.enableKeyEvents,
			validationEvent : it.validationEvent,
			labelSeparator : ":"
		}
		cfg.listeners = {
			specialkey : this.onFieldSpecialkey,
			scope : this
		}
		if (it.onblur) {
			var func = eval("this." + it.onblur)
			if (typeof func == 'function') {
				Ext.apply(cfg.listeners, {
							blur : func
						})
			}
		}
		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it.editable) {
			cfg.editable = (it.editable == "true") ? true : false
		}
		if (it['not-null'] == "1" || it['not-null'] == "true") {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
			cfg.regex = /(^\S+)/
			cfg.regexText = "前面不能有空格字符"
		}
		if (it.fixed) {
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
			this.changeFieldCfg(it, cfg);
			Ext.apply(combox, cfg)
			combox.on("specialkey", this.onFieldSpecialkey, this)
			return combox;
		}
		if (it.properties && it.properties.mode == "remote") {
			var f = this.createRemoteDicField(it);
			f.on("specialkey", this.onFieldSpecialkey, this)
			return f;
		}
		if (it.length) {
			cfg.maxLength = it.length;
		}
		if (it.maxValue) {
			cfg.maxValue = it.maxValue;
		}
		if (typeof(it.minValue) != 'undefined') {
			cfg.minValue = it.minValue;
		}
		if (it.xtype) {
			if (it.xtype == "htmleditor") {
				cfg.height = it.height || 200;
			}
			if (it.xtype == "textarea") {
				cfg.height = it.height || 65
			}
			if (it.xtype == "datefield"
					&& (it.type == "datetime" || it.type == "timestamp")) {
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
			}
			this.changeFieldCfg(it, cfg);
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
				break;
			case 'date' :
				cfg.xtype = 'datefield'
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
				if (it.maxValue && typeof it.maxValue == 'string'
						&& it.maxValue.length > 10) {
					cfg.maxValue = it.maxValue.substring(0, 10);
				}
				if (it.minValue && typeof it.minValue == 'string'
						&& it.minValue.length > 10) {
					cfg.minValue = it.minValue.substring(0, 10);
				}
				break;
			case 'datetime' :
				cfg.xtype = 'datetimefield'
				cfg.emptyText = "请选择日期时间"
				cfg.format = 'Y-m-d H:i:s'
				break;
			case 'text' :
				cfg.xtype = "htmleditor"
				cfg.enableSourceEdit = false
				cfg.enableLinks = false
				cfg.width = 300
				cfg.height = 180
				break;
		}
		this.changeFieldCfg(it, cfg);
		return cfg;
	}
	
})