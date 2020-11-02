$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.InventoryInList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryInList.superclass.constructor.apply(
			this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.InventoryInList,
				phis.script.SimpleList,
				{
					onReady : function() {
						if (this.grid) {
							phis.application.sup.script.InventoryInList.superclass.onReady
									.call(this);
							var startDate = "";
							var endDate = "";
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
															'and',
															[
																	'ge',
																	[ '$',
																			"str(a.SCSJ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.SCSJ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'PDFS' ], [ 'i', 1 ] ] ],
									[ 'eq', [ '$', 'KFXH' ],
											[ 'i', this.mainApp['phis'].treasuryId ] ] ];
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
																	[ '$',
																			"str(a.SCSJ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.SCSJ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'PDFS' ], [ 'i', 1 ] ] ],
									[ 'eq', [ '$', 'KFXH' ],
											[ 'i', this.mainApp['phis'].treasuryId ] ] ];
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
					getCndBar : function(items) {
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						var datelable = new Ext.form.Label({
							text : "记账日期:"
						})
						this.dateFrom = new Ext.form.DateField({
							id : 'inventorysIndateFrom',
							name : 'inventorysIndateFrom',
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
							id : 'inventorysIndateTo',
							name : 'inventorysIndateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
						return [ "<h1 style='text-align:center'>已记账盘点单:</h1>",
								'-', datelable, this.dateFrom, tolable,
								this.dateTo, '-' ]
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
														'and',
														[
																'ge',
																[ '$',
																		"str(a.SCSJ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ],
														[
																'le',
																[ '$',
																		"str(a.SCSJ,'yyyy-mm-dd')" ],
																[ 's', endDate ] ] ],
												[ 'eq', [ '$', 'DJZT' ],
														[ 'i', 2 ] ] ],
										[ 'eq', [ '$', 'PDFS' ], [ 'i', glfs ] ] ],
								[ 'eq', [ '$', 'KFXH' ],
										[ 'i', this.mainApp['phis'].treasuryId ] ] ];
						this.loadData();
					},
					// 刷新页面
					doRefreshWinGlfs : function(glfs) {
						var glfs = parseInt(glfs);
						this.clear();
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
														'and',
														[
																'ge',
																[ '$',
																		"str(a.SCSJ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ],
														[
																'le',
																[ '$',
																		"str(a.SCSJ,'yyyy-mm-dd')" ],
																[ 's', endDate ] ] ],
												[ 'eq', [ '$', 'DJZT' ],
														[ 'i', 2 ] ] ],
										[ 'eq', [ '$', 'PDFS' ], [ 'i', glfs ] ] ],
								[ 'eq', [ '$', 'KFXH' ],
										[ 'i', this.mainApp['phis'].treasuryId ] ] ];
						this.loadData();
					},
					doLook : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						var initDataBody = {};
						initDataBody["LRXH"] = r.data.LRXH;
						if (this.GLFS == 1) {
							this.inventoryInModule = this.opener.createModule(
									"inventoryInModule", this.addRefkc);
							this.inventoryInModule.GLFS = this.GLFS
							this.inventoryInModule
									.on("save", this.onSave, this);
							var win = this.inventoryInModule.getWin();
							win.add(this.inventoryInModule.initPanel());
							this.inventoryInModule.doUpd();
								this.inventoryInModule
										.changeButtonState("read");
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryInModule.op = "update";
								this.inventoryInModule.initDataBody = initDataBody;
								this.inventoryInModule.loadData(initDataBody);
							}
						} else if (this.GLFS == 2) {
							this.inventoryInModuleks = this.opener
									.createModule("inventoryInModuleks",
											this.addRefks);
							this.inventoryInModuleks.GLFS = this.GLFS
							this.inventoryInModuleks.on("save", this.onSave,
									this);
							var win = this.inventoryInModuleks.getWin();
							win.add(this.inventoryInModuleks.initPanel());
							this.inventoryInModuleks.doUpd();
								this.inventoryInModuleks
										.changeButtonState("read");
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryInModuleks.op = "update";
								this.inventoryInModuleks.initDataBody = initDataBody;
								this.inventoryInModuleks.loadData(initDataBody);
							}
						} else {
							this.inventoryInModuletz = this.opener
									.createModule("inventoryInModuletz",
											this.addReftz);
							this.inventoryInModuletz.GLFS = this.GLFS
							this.inventoryInModuletz.on("save", this.onSave,
									this);
							var win = this.inventoryInModuletz.getWin();
							win.add(this.inventoryInModuletz.initPanel());
							this.inventoryInModuletz.doUpd();
								this.inventoryInModuletz
										.changeButtonState("read");
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryInModuletz.op = "update";
								this.inventoryInModuletz.initDataBody = initDataBody;
								this.inventoryInModuletz.loadData(initDataBody);
							}
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