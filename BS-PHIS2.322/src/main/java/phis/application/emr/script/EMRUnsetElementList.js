$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRUnsetElementList = function(cfg) {
	cfg.selectFirst = false;
	cfg.disablePagingTbr = true;
	phis.application.emr.script.EMRUnsetElementList.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.emr.script.EMRUnsetElementList, phis.script.SimpleList, {
			onRowClick : function(grid, index, e) {
				var r = this.store.getAt(index);
				if (!r)
					return;

				var n = this.emr.FunActiveXInterface("BsDocSearchElem", r
								.get("Parakey"), '#' + r.get("Elem"))
				if (!n) {
					MyMessageTip.msg("提示", "对不起，未找到当前元素!", true);
					return;
				}
				var elePos = this.emr.StrReturnData;
				this.emr.FunActiveXInterface("BsCurrentPos", '1', elePos);
			},
			paraKeyRender : function(value, metaData, r, row, col, store) {
				if (value.indexOf("..") > 0) {
					return value.split("..")[1];
				}
			},
			elemKeyRender : function(value, metaData, r, row, col, store) {
				if (value.indexOf("-") > 0) {
					return value.split("-")[1];
				} else {
					return value;
				}
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
								collapsible : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								maximizable : false,
								shadow : false,
								modal : this.modal || false,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
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
			}
		})
