$package("phis.application.top.script")

$import("phis.script.SimpleList")

// 窗口切换基类

phis.application.top.script.PublicSwitchBaseList = function(cfg) {
	cfg.width = 405;
	cfg.height = 250;
	phis.application.top.script.PublicSwitchBaseList.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.top.script.PublicSwitchBaseList, phis.script.SimpleList,
		{
			expansion : function(cfg) {
				cfg.viewConfig.emptyText = "<font color='red' style='font-size:13'>对不起，您还未设置任何可切换"
						+ this.getTagName() + ",请与管理员联系!</font>";
			},
			
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'refresh',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : false,
						maximizable : false,
						shadow : false,
						modal : this.modal || false,
						items : this.initPanel()
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
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
					}

				})