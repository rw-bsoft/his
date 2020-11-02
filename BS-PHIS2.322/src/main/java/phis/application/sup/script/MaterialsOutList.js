$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.MaterialsOutList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.MaterialsOutList.superclass.constructor.apply(
			this, [ cfg ])
	this.on("winShow", this.onWinShow, this);
}
Ext
		.extend(
				phis.application.sup.script.MaterialsOutList,
				phis.script.SimpleList,
				{
					onWinShow : function() {
						this.materialsOutDetailModule.zblb = this.zblb;
					},
					onReady : function() {
						if (this.grid) {
							phis.application.sup.script.MaterialsOutList.superclass.onReady
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
							var startDate = "";// 开始时间
							var endDate = ""; // 结束时间
							if (this.dateField) {
								startDate = new Date(this.dateField.getValue())
										.format("Y-m-d");
							}
							if (this.dateFieldEnd) {
								endDate = new Date(this.dateFieldEnd.getValue())
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
																	'and',
																	[
																			'ge',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')" ],
																			[
																					's',
																					startDate ] ],
																	[
																			'le',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')" ],
																			[
																					's',
																					endDate ] ] ],
															[
																	'eq',
																	[ '$',
																			'DJZT' ],
																	[ 'i', 2 ] ] ],
													[
															'eq',
															[ '$', 'KFXH' ],
															[
																	'i',
																	this.mainApp['phis'].treasuryId ] ] ],
											[ 'eq', [ '$', 'ZBLB' ],
													[ 'i', this.zblb ] ] ], ,
									[ 'le', [ '$', 'DJLX' ], [ 'i', 4 ] ] ];
							this.initCnd = [
									'and',
									[
											'and',
											[
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'ge',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')" ],
																			[
																					's',
																					startDate ] ],
																	[
																			'le',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')" ],
																			[
																					's',
																					endDate ] ] ],
															[
																	'eq',
																	[ '$',
																			'DJZT' ],
																	[ 'i', 2 ] ] ],
													[
															'eq',
															[ '$', 'KFXH' ],
															[
																	'i',
																	this.mainApp['phis'].treasuryId ] ] ],
											[ 'eq', [ '$', 'ZBLB' ],
													[ 'i', this.zblb ] ] ],
									[ 'le', [ '$', 'DJLX' ], [ 'i', 4 ] ] ];
							this.loadData();
							// 设置分页信息
							this.loadData();
						}
					},
					expansion : function(cfg) {
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						// 顶部工具栏
						var label = new Ext.form.Label({
							text : "单据日期："
						});
						this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
						this.dateFieldEnd = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
						var tbar = cfg.tbar;
						cfg.tbar = [];
						cfg.tbar.push([
								"<h1 style='text-align:center'>已记账出库单:</h1>",
								'-', label, this.dateField, "至",
								this.dateFieldEnd, tbar ]);
						var bar = cfg.tbar;
						cfg.tbar = {
							enableOverflow : true,
							items : bar
						}
					},
					doRefreshWin : function() {
						this.clear();
						var startDate = "";// 开始时间
						var endDate = ""; // 结束时间
						if (this.dateField) {
							startDate = new Date(this.dateField.getValue())
									.format("Y-m-d");
						}
						if (this.dateFieldEnd) {
							endDate = new Date(this.dateFieldEnd.getValue())
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
																'and',
																[
																		'ge',
																		[ '$',
																				"str(a.CKRQ,'yyyy-mm-dd')" ],
																		[ 's',
																				startDate ] ],
																[
																		'le',
																		[ '$',
																				"str(a.CKRQ,'yyyy-mm-dd')" ],
																		[ 's',
																				endDate ] ] ],
														[
																'eq',
																[ '$', 'DJZT' ],
																[ 'i', 2 ] ] ],
												[
														'eq',
														[ '$', 'KFXH' ],
														[
																'i',
																this.mainApp['phis'].treasuryId ] ] ],
										[ 'eq', [ '$', 'ZBLB' ],
												[ 'i', this.zblb ] ] ],
								[ 'le', [ '$', 'DJLX' ], [ 'i', 4 ] ] ];
						this.loadData();
					},
					doLook : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						var initDataBody = {};
						initDataBody["DJXH"] = r.data.DJXH;
						var ret = phis.script.rmi
								.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.verificationMaterialsOutDeleteActionId,
									body : initDataBody
								});
						if (ret.code > 300) {
							this
									.processReturnMsg(ret.code, ret.msg,
											this.doUpd);
							this.doRefresh();
							return;
						}
						this.materialsOutDetailModule = this.oper.createModule(
								"materialsOutDetailModule", this.readRef);
						this.materialsOutDetailModule.zblb = this.zblb;
						this.materialsOutDetailModule.on("save", this.onSave,
								this);
						this.materialsOutDetailModule.oper = this;
						var win = this.materialsOutDetailModule.getWin();
						win.add(this.materialsOutDetailModule.initPanel());
						this.materialsOutDetailModule.doUpd();
						var djzt = r.data.DJZT;
						if (djzt == 2) {
							this.materialsOutDetailModule
									.changeButtonState("commit");
						}
						win.show()
						win.center()
						if (!win.hidden) {
							this.materialsOutDetailModule.op = "update";
							this.materialsOutDetailModule
									.loadData(initDataBody);
						}

					},
					onDblClick : function(grid, index, e) {
						this.doLook();
					},
					onSave : function() {
						this.fireEvent("save", this);
					},
					doPrint : function() {
						var module = this.createModule("noStoreListPrint",
								this.refNoStoreListPrint)
						var r = this.getSelectedRecord()
						if (r == null) {
							MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
							return;
						}
						module.DJXH = r.data.DJXH;
						module.initPanel();
						module.doPrint();
					}
				})