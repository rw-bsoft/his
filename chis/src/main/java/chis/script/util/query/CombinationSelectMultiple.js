$package("chis.script.util.query");
$import("chis.script.BizSelectListView");

chis.script.util.query.CombinationSelectMultiple = function(cfg) {
	chis.script.util.query.CombinationSelectMultiple.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.script.util.query.CombinationSelectMultiple,
		chis.script.BizSelectListView, {
			resetFirstPage : function() {
				var pt = this.grid.getBottomToolbar();
				if (pt) {
					pt.cursor = 0;
					this.requestData.pageNo = 1;
					pt.afterTextItem
							.setText(String.format(pt.afterPageText, 1));
					pt.inputItem.setValue(1);
					pt.updateInfo();
				} else {
					this.requestData.pageNo = 1;
				}
			},
			setRecords : function(records) {
				this.store.removeAll();
				this.store.add(records);
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
						height : this.height,
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
						});
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
				return win;
			}
		});