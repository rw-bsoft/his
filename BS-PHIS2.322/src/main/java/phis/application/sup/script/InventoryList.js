$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.InventoryList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryList.superclass.constructor.apply(
			this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.InventoryList,
				phis.script.SimpleList,
				{
					onReady : function() {
						if (this.grid) {
							phis.application.sup.script.InventoryList.superclass.onReady
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
															'and',
															[
																	'ge',
																	[ '$',
																			"str(a.ZDRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.ZDRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'GLFS' ], [ 'i', 1 ] ] ],
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
																			"str(a.ZDRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.ZDRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'GLFS' ], [ 'i', 1 ] ] ],
									[ 'eq', [ '$', 'KFXH' ],
											[ 'i', this.mainApp['phis'].treasuryId ] ] ];
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
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						var datelable = new Ext.form.Label({
							text : "记账日期:"
						})
						this.dateFrom = new Ext.form.DateField({
							id : 'inventorysdateFrom',
							name : 'inventorysdateFrom',
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
							id : 'inventorysdateTo',
							name : 'inventorysdateTo',
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
																		"str(a.ZDRQ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ],
														[
																'le',
																[ '$',
																		"str(a.ZDRQ,'yyyy-mm-dd')" ],
																[ 's', endDate ] ] ],
												[ 'eq', [ '$', 'DJZT' ],
														[ 'i', 2 ] ] ],
										[ 'eq', [ '$', 'GLFS' ], [ 'i', glfs ] ] ],
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
																		"str(a.ZDRQ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ],
														[
																'le',
																[ '$',
																		"str(a.ZDRQ,'yyyy-mm-dd')" ],
																[ 's', endDate ] ] ],
												[ 'eq', [ '$', 'DJZT' ],
														[ 'i', 2 ] ] ],
										[ 'eq', [ '$', 'GLFS' ], [ 'i', glfs ] ] ],
								[ 'eq', [ '$', 'KFXH' ],
										[ 'i', this.mainApp['phis'].treasuryId ] ] ];
						this.loadData();
					},
					// 查看
					doLook : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						var initDataBody = {};
						initDataBody["DJXH"] = r.data.DJXH;
						if (this.GLFS == 1) {
							this.inventoryModule = this.opener.createModule(
									"inventoryModule", this.addRefkc);
							this.inventoryModule.GLFS = this.GLFS
							this.inventoryModule.on("save", this.onSave, this);
							var win = this.inventoryModule.getWin();
							win.add(this.inventoryModule.initPanel());
							this.inventoryModule.doUpd();
							var djzt = r.data.DJZT;
							if (djzt == 2) {
								this.inventoryModule
										.changeButtonState("commited");
							}
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryModule.op = "update";
								this.inventoryModule.initDataBody = initDataBody;
								this.inventoryModule.loadData(initDataBody);
							}
						} else if (this.GLFS == 2) {
							this.inventoryModuleks = this.opener.createModule(
									"inventoryModuleks", this.addRefks);
							this.inventoryModuleks.GLFS = this.GLFS
							this.inventoryModuleks
									.on("save", this.onSave, this);
							var win = this.inventoryModuleks.getWin();
							win.add(this.inventoryModuleks.initPanel());
							this.inventoryModuleks.doUpd();
							var djzt = r.data.DJZT;
							if (djzt == 2) {
								this.inventoryModuleks
										.changeButtonState("commited");
							}
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryModuleks.op = "update";
								this.inventoryModuleks.initDataBody = initDataBody;
								this.inventoryModuleks.loadData(initDataBody);
							}
						} else {
							this.inventoryModuletz = this.opener.createModule(
									"inventoryModuletz", this.addReftz);
							this.inventoryModuletz.GLFS = this.GLFS
							this.inventoryModuletz
									.on("save", this.onSave, this);
							var win = this.inventoryModuletz.getWin();
							win.add(this.inventoryModuletz.initPanel());
							this.inventoryModuletz.doUpd();
							var djzt = r.data.DJZT;
							if (djzt == 2) {
								this.inventoryModuletz
										.changeButtonState("commited");
							}
							win.show()
							win.center()
							if (!win.hidden) {
								this.inventoryModuletz.op = "update";
								this.inventoryModuletz.initDataBody = initDataBody;
								this.inventoryModuletz.loadData(initDataBody);
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
					},
		            doPrint : function() {
		                var r = this.getSelectedRecord()
		                var module;
		                if(r.get("GLFS") == 1){
		                	module = this.createModule("inventoryPrint",
		                        this.refInventoryPrint)
		                }else if(r.get("GLFS") == 2){
		                	module = this.createModule("inventoryKsPrint",
		                        this.refInventoryKsPrint)
		                }else if(r.get("GLFS") == 3){
		                	module = this.createModule("inventoryTzPrint",
		                        this.refInventoryTzPrint)
		                }
		                if (r == null) {
		                    MyMessageTip.msg("提示", "打印失败：无效的盘点单信息!", true);
		                    return;
		                }
//		                if(r.data.DJZT==0){
//		                    MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
//		                    return;
//		                }
		                module.djxh=r.data.DJXH;
		                module.initPanel();
		                module.doPrint();
		            }
				})