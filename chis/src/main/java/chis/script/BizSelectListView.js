/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("app.modules.list.SelectListView", "chis.script.BizCommon",
		"chis.script.BizListCommon")

chis.script.BizSelectListView = function(cfg) {
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.listServiceId = cfg.listServiceId || "chis.simpleQuery"
	cfg.removeServiceId = cfg.removeServiceId || "chis.simpleRemove"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizListCommon);
	chis.script.BizSelectListView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizSelectListView, app.modules.list.SelectListView, {

			getRemoveRequest : function(r) {
				return {
					pkey : r.id
				};
			},
			onReady : function() {
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.grid.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				// index btns
				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items || [];
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				} else {
					var btns = this.grid.buttons || []
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				// 屏蔽框架自带的快捷键
				// keyMap.on(keys,this.onAccessKey,this)
				keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)

			},
			getWin : function() {
				var win = this.win
				var closeAction = "close"
				if (!this.mainApp || this.closeAction) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						title : this.title || this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}

		})