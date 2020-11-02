$package("phis.application.reg.script");

$import("phis.script.SimpleList")

phis.application.reg.script.RegisteredPrescriptionList = function(cfg) {
	//cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	phis.application.reg.script.RegisteredPrescriptionList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.reg.script.RegisteredPrescriptionList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = "phis.registeredManagementService";
				this.requestData.serviceAction = "queryGhdjs";
				this.requestData.body = this.BRID;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
			},/*
			expansion : function(cfg) {
				cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
			},
			onStoreLoadData : function(store, records, ops) {
				this.grid.selModel.selectAll();
				this.grid.getView().focusRow(0);
			},*/
			onDblClick : function(grid, index, e) {
				this.doCommit();
			},
			onEnterKey : function() {
				this.doCommit();
			},
			doCommit : function() {
				var rs = this.grid.getSelectionModel().getSelected();
				this.opener.setGHDJ(rs.data);
				this.win.hide();
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
					this.opener.form.getForm().findField("QMZHM").focus(false, 200);
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = "close"
				if (!this.mainApp || this.closeAction) {
					closeAction = "hide"
				}
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
						constrain : true,
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
				win.instance = this;
				return win;
			}
		})