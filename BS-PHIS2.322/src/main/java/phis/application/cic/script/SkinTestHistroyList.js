$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.SkinTestHistroyList = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	cfg.autoLoadData = false;
	phis.application.cic.script.SkinTestHistroyList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.SkinTestHistroyList,
		phis.script.SimpleList, {
			onWinShow : function() {
				this.requestData.cnd = [
						'and',
						['eq', ['$', 'a.BRBH'], ['d', this.exContext.ids.brid]],
						[
								'and',
								['eq', ['$', 'a.WCBZ'], ['i', 1]],
								['eq', ['$', 'a.JGID'],
										['$', '%user.manageUnit.id']]]];
				this.loadData();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			destory : function() {
				this.win.destroy();
			}
		})