$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRHtmlPreview = function(cfg) {
	this.cachePrtSetup = {};
	phis.application.emr.script.EMRHtmlPreview.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRHtmlPreview, phis.script.SimpleModule, {
			initPanel : function() {
				// 判断是否有权限
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							// id : this.mKey,
							border : false,
							// html : '<IFRAME width="100%" height="100%" src="'
							// + url
							// + '" frameborder=0 ></iframe>',
							autoScroll : true
						});

				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				var url = ClassLoader.appRootOffsetPath
						+ 'FileContent.search?type=HTML&BLBH='
						+ (this.BLBH || 203);
				this.panel.load({
							url : url,
							scripts : true
						})
			},
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								resizable : false,
								maximizable : false,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("beforehide", function() {
								this.fireEvent("beforeclose", this)
							}, this)
					this.win = win
				}
				return win;
			}
		});