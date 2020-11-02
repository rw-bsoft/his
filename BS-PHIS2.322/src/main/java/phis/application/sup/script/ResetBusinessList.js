$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.ResetBusinessList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.ResetBusinessList.superclass.constructor.apply(
			this, [ cfg ])
	this.on("winShow", this.onWinShow, this);
}
Ext
		.extend(
				phis.application.sup.script.ResetBusinessList,
				phis.script.SimpleList,
				{
					onWinShow : function() {
						this.resetBusinessModule.zblb = this.zblb;
					},
					onReady : function() {
						if (this.grid) {
							phis.application.sup.script.ResetBusinessList.superclass.onReady
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
							var startDate = "";
							var endDate = "";
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
																	'ge',
																	[ '$',
																			"str(a.JZRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.JZRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'ZBLB' ],
													[ 'i', this.zblb ] ] ],
									[
											'eq',
											[ '$', 'KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
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
																			"str(a.JZRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			startDate ] ],
															[
																	'le',
																	[ '$',
																			"str(a.JZRQ,'yyyy-mm-dd')" ],
																	[ 's',
																			endDate ] ] ],
													[ 'eq', [ '$', 'DJZT' ],
															[ 'i', 2 ] ] ],
											[ 'eq', [ '$', 'ZBLB' ],
													[ 'i', this.zblb ] ] ],
									[
											'eq',
											[ '$', 'KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
							this.loadData();

							this.loadData();
						}
					},
					expansion : function(cfg) {
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";

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
								"<h1 style='text-align:center'>已记账重置单:</h1>",
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
						var startDate = "";
						var endDate = "";
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
																'ge',
																[ '$',
																		"str(a.JZRQ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ],
														[
																'le',
																[ '$',
																		"str(a.JZRQ,'yyyy-mm-dd')" ],
																[ 's', endDate ] ] ],
												[ 'eq', [ '$', 'DJZT' ],
														[ 'i', 2 ] ] ],
										[ 'eq', [ '$', 'ZBLB' ],
												[ 'i', this.zblb ] ] ],
								[ 'eq', [ '$', 'KFXH' ],
										[ '$', '%user.properties.treasuryId' ] ] ];
						this.loadData();
					},
					doLook : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						var initDataBody = {};
						initDataBody["DJXH"] = r.data.DJXH;
						this.resetBusinessDetailModuleforList = this
								.createModule("resetBusinessDetailModule",
										this.readRef);
						this.resetBusinessDetailModuleforList.zblb = this.zblb;
						this.resetBusinessDetailModuleforList.on("save",
								this.onSave, this);
						this.resetBusinessDetailModuleforList.on("winClose",
								this.onClose, this);

						this.resetBusinessDetailModuleforList.initPanel();
						var win = this.resetBusinessDetailModuleforList
								.getWin();

						var djzt = r.data.DJZT;
						if (djzt == 2) {
							this.resetBusinessDetailModuleforList
									.changeButtonState("commited");
						}
						win.show()
						win.center()
						if (!win.hidden) {
							this.resetBusinessDetailModuleforList.op = "update";
							this.resetBusinessDetailModuleforList
									.loadData(initDataBody);
						}

					},
					onDblClick : function(grid, index, e) {
						this.doLook();
					},
					onClose : function() {
						this.resetBusinessDetailModuleforList.getWin().hide();
						this.doRefreshWin();
					},
					onSave : function() {
						this.fireEvent("save", this);
					}
				})