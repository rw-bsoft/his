$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.ForRegistrationList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.ForRegistrationList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.ForRegistrationList, phis.script.SimpleList, {
	expansion : function(cfg) {
		var bar = cfg.tbar;
		cfg.tbar = {
			enableOverflow : true,
			items : bar
		}
	},
	onReady : function() {
		if (this.grid) {
			phis.application.sup.script.ForRegistrationList.superclass.onReady
					.call(this);
			var startDate = "";
			var endDate = ""; 
			if (this.dateFrom) {
				startDate = new Date(this.dateFrom.getValue()).format("Y-m-d");
			}
			if (this.dateTo) {
				endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
			}
			this.requestData.cnd = [
					'and',
					[
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.QRRQ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.QRRQ,'yyyy-mm-dd')"],
													['s', endDate]]],
									['eq', ['$', 'DJLX'], ['i', 7]]],
							['eq', ['$', 'QRBZ'], ['i', 1]]],
					['eq', ['$', 'CKKF'], ['i', this.mainApp['phis'].treasuryId]]];
			this.initCnd = [
					'and',
					[
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.QRRQ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.QRRQ,'yyyy-mm-dd')"],
													['s', endDate]]],
									['eq', ['$', 'DJLX'], ['i', 7]]],
							['eq', ['$', 'QRBZ'], ['i', 1]]],
					['eq', ['$', 'CKKF'], ['i', this.mainApp['phis'].treasuryId]]];
			this.loadData();
		}
	},
	getCndBar : function(items) {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var datelable = new Ext.form.Label({
			     text : "记账日期:"
				})
		this.dateFrom = new Ext.form.DateField({
					id : 'forRegistrationsdateFrom',
					name : 'forRegistrationsdateFrom',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				});
		var tolable = new Ext.form.Label({
			text : " 到 "
				});
		this.dateTo = new Ext.form.DateField({
					id : 'forRegistrationsdateTo',
					name : 'forRegistrationsdateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
					});
			return ["<h1 style='text-align:center'>已确认登记单:</h1>", '-', datelable,
					this.dateFrom, tolable, this.dateTo, '-']
	},
	doRefreshWin : function() {
		this.clear();
		var startDate = "";
		var endDate = ""; 
		if (this.dateFrom) {
			startDate = new Date(this.dateFrom.getValue()).format("Y-m-d");
		}
		if (this.dateTo) {
			endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
		}
		this.requestData.cnd = [
				'and',
				[
						'and',
						[
								'and',
								[
										'and',
										[
												'ge',
												['$',
														"str(a.QRRQ,'yyyy-mm-dd')"],
												['s', startDate]],
										[
												'le',
												['$',
														"str(a.QRRQ,'yyyy-mm-dd')"],
												['s', endDate]]],
								['eq', ['$', 'DJLX'], ['i', 7]]],
						['eq', ['$', 'QRBZ'], ['i', 1]]],
				['eq', ['$', 'CKKF'], ['i', this.mainApp['phis'].treasuryId]]];
		this.loadData();
	},
	doLook : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return;
		}
		var initDataBody = {};
		initDataBody["DJXH"] = r.data.DJXH;
		this.forRegistrationModule = this.oper.createModule(
				"forRegistrationModule", this.addRef);
		this.forRegistrationModule.on("save", this.onSave, this);
		var win = this.forRegistrationModule.getWin();
		win.add(this.forRegistrationModule.initPanel());
		var djzt = r.data.DJZT;
		if (djzt == 0) {
			this.forRegistrationModule.changeButtonState("new");
		} else if (djzt == 1) {
			this.forRegistrationModule.changeButtonState("verified");
		} else if (djzt == 2) {
			this.forRegistrationModule.changeButtonState("commited");
		}
		win.show()
		win.center()
		if (!win.hidden) {
			this.forRegistrationModule.op = "update";
			this.forRegistrationModule.initDataBody = initDataBody;
			this.forRegistrationModule.loadData(initDataBody);
            this.forRegistrationModule.loadQRRK(r.data.THDJ);
		}

	},
	onDblClick : function(grid, index, e) {
		var item = {};
		item.text = "查看";
		item.cmd = "look";
		this.doAction(item, e)

	},
	onSave : function() {
		this.fireEvent("save", this);
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
				id : this.id,
				title : this.title,
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
})