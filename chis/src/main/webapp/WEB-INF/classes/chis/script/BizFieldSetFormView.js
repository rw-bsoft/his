/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("chis.script.modules.form.FieldSetFormView", "chis.script.BizCommon",
		"chis.script.BizFormCommon")

chis.script.BizFieldSetFormView = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.isAutoScroll = cfg.isAutoScroll || false;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.autoHeight = !cfg.isAutoScroll
	cfg.saveServiceId = cfg.saveServiceId || "chis.simpleSave"
	cfg.loadServiceId = cfg.loadServiceId || "chis.simpleLoad"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizFormCommon);
	chis.script.BizFieldSetFormView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizFieldSetFormView,
		chis.script.modules.form.FieldSetFormView, {

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
			}

		})