/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")
$import("app.desktop.Module", "chis.script.BizCommon")
chis.script.BizModule = function(cfg) {
	Ext.apply(cfg, chis.script.BizCommon);
	chis.script.BizModule.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.script.BizModule, app.desktop.Module, {
			keyManageFunc : function(keyCode, keyName) {
				for (var i = 0; i < this.actions.length; i++) {
					var m = this.midiModules[this.actions[i].id]
					if (m) {
						if (m.btnAccessKeys) {
							var btn = m.btnAccessKeys[keyCode];
							if (btn && btn.disabled) {
								continue;
							}
						}
						m.doAction(m.btnAccessKeys[keyCode]);
					}
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : this.width || 800,
								height : this.height || 450,
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
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.win.doLayout()
								this.fireEvent("winShow", this)
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})