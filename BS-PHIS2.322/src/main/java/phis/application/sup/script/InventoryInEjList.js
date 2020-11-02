$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.InventoryInEjList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryInEjList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.InventoryInEjList,
		phis.script.SimpleList, {
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.InventoryInEjList.superclass.onReady
							.call(this);
					var startDate = "";// 开始时间
					var endDate = ""; // 结束时间
					if (this.dateFrom) {
						startDate = new Date(this.dateFrom.getValue())
								.format("Y-m-d");
					}
					if (this.dateTo) {
						endDate = new Date(this.dateTo.getValue())
								.format("Y-m-d");
					}
					this.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.SCSJ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.SCSJ,'yyyy-mm-dd')"],
													['s', endDate]]],
									['eq', ['$', 'DJZT'], ['i', 2]]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					this.initCnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.SCSJ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.SCSJ,'yyyy-mm-dd')"],
													['s', endDate]]],
									['eq', ['$', 'DJZT'], ['i', 2]]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					// 设置分页信息
					this.loadData();
				}
			},

			expansion : function(cfg) {
							var bar = cfg.tbar;
							cfg.tbar = {
								enableOverflow : true,
								items : bar
							}
						},
			// 生成日期框
			getCndBar : function(items) {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "记账日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'inventorysInEjdateFrom',
							name : 'inventorysInEjdateFrom',
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
							id : 'inventorysInEjdateTo',
							name : 'inventorysInEjdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				return ["<h1 style='text-align:center'>已记账入库单:</h1>", '-',
						datelable, this.dateFrom, tolable, this.dateTo, '-']
			},
			// 刷新页面
			doRefreshWin : function() {
				var glfs = parseInt(this.GLFS);
				this.clear();
				var startDate = "";// 开始时间
				var endDate = ""; // 结束时间
				if (this.dateFrom) {
					startDate = new Date(this.dateFrom.getValue())
							.format("Y-m-d");
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
												'ge',
												['$',
														"str(a.SCSJ,'yyyy-mm-dd')"],
												['s', startDate]],
										[
												'le',
												['$',
														"str(a.SCSJ,'yyyy-mm-dd')"],
												['s', endDate]]],
								['eq', ['$', 'DJZT'], ['i', 2]]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]];
				this.loadData();
			},
			// 查看
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["LRXH"] = r.data.LRXH;
				this.inventoryInEjModule = this.oper.createModule(
						"inventoryInEjModule", this.addRef);
				this.inventoryInEjModule.on("save", this.onSave, this);
				this.inventoryInEjModule.oper = this;
				var win = this.inventoryInEjModule.getWin();
				win.add(this.inventoryInEjModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.inventoryInEjModule.changeButtonState("new");
				} else if (djzt == 2) {
					this.inventoryInEjModule.changeButtonState("subt");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.inventoryInEjModule.op = "update";
					this.inventoryInEjModule.initDataBody = initDataBody;
					this.inventoryInEjModule.loadData(initDataBody);
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
					win.on("hide", function() { // ** add by yzh
								// 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}
		})