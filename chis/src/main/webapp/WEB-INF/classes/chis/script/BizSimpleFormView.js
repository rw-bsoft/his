/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("app.modules.form.SimpleFormView", "chis.script.BizCommon",
		"chis.script.BizFormCommon")

chis.script.BizSimpleFormView = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.isAutoScroll = cfg.isAutoScroll || false;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.autoHeight = !cfg.isAutoScroll
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizFormCommon);
	cfg.saveServiceId = cfg.saveServiceId || "chis.simpleSave"
	cfg.loadServiceId = cfg.loadServiceId || "chis.simpleLoad"
	chis.script.BizSimpleFormView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizSimpleFormView, app.modules.form.SimpleFormView, {
	getLoadRequest : function() {
		if (this.initDataId) {
			return {
				pkey : this.initDataId
			};
		} else {
			return null;
		}
	},

	changeCfg : function(cfg) {
		if (this.isAutoScroll) {
			delete cfg.autoWidth;
			delete cfg.autoHeight;
			cfg.autoScroll = true;
		}
	},
	quickPickMCode : function(f) {
		$import("util.widgets.MyCombox")
		if (f instanceof util.widgets.MyCombox) {
			var st = f.getStore()
			var fs = st.fields
			var searchField = "key"
			var searchValue = f.getRawValue() // 界面的显示值
			for (var i = 0; i < fs.getCount(); i++) {
				if (fs.itemAt(i).name == "mCode") {
					searchField = "mCode"
					break
				}
			}
			for (var i = 0; i < st.getCount(); i++) {
				var r = st.getAt(i)
				if (searchValue == r.get("text")
						&& r.get("key") == f.getValue()) {
					return
				}
			}
			f.setValue(searchValue)
			f.fireEvent("select", f)
			f.fireEvent("valid", f)
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
	getFormData : function() {
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var ac = util.Accredit;
		var values = {};
		var items = this.schema.items;
		// Ext.apply(this.data,this.exContext);
		if (items) {
			var form = this.form.getForm();
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
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
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
					// end
				}
				if (v == null || v === "") {
					if (!(it.pkey)
							&& (it["not-null"] == "1" || it['not-null'] == "true")
							&& !it.ref) {
						Ext.Msg.alert("提示", it.alias + "不能为空");
						return;
					}
				}
				values[it.id] = v;
			}
		}
		return values;
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
		//屏蔽框架自带的快捷键
//		keyMap.on(keys, this.onAccessKey, this)
		if (this.win) {
			keyMap.on({
						key : Ext.EventObject.ESC,
						shift : true
					}, this.onEsc, this)
		}
	},
	afterCreateWin : function(win) {
		win.instance = this;
	}

})