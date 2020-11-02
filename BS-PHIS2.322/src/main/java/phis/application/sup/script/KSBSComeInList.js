phis.application.sup.script.KSBSComeInList = function(cfg) {
	cfg.width = 800;
	cfg.height = 500;
	cfg.modal = this.modal = true;
	phis.application.sup.script.KSBSComeInList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.KSBSComeInList, phis.script.SimpleList, {
	initPanel : function(sc) {
		var grid = phis.application.sup.script.KSBSComeInList.superclass.initPanel
				.call(this);
		grid.on("dblclick", this.gridDbclick, this);
		this.sm = new Ext.grid.CheckboxSelectionModel();
		return this.grid;
	},
	loadData : function(zblbValue) {
		this.clear();
		this.requestData.serviceId = "phis.faultyService";
		this.requestData.serviceAction = "getKSZCByks";
		this.requestData.ZBLB = Ext.getCmp("faultyStautsRadio").getValue().inputValue;
		this.requestData.BSFS = this.BSFS;
		if (!Ext.isEmpty(this.BSKS)) {
			this.requestData.BSKS = this.BSKS;
		}

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
	},
	doCallIn : function() {
		var record = this.grid.getSelectionModel().getSelected();
		this.edlist.setKSZCInfo(record);
	},
	gridDbclick : function() {
		var record = this.grid.getSelectionModel().getSelected();
		this.edlist.setKSZCInfo(record);
	},
	doCommit : function() {
		var record = this.grid.getSelectionModel().getSelected();
		this.edlist.setKSZCInfo(record);
	},

	doExit : function() {
		this.getWin().hide();
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
				id : this.id,
				title : this.title||this.name,
				width : this.width,
				iconCls : 'icon-grid',
				shim : true,
				layout : "fit",
				animCollapse : true,
				closeAction : "hide",
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
});