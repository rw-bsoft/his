$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.StorageConfirmeList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	var dat = new Date().format('Y-m-d');
	var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
	this.dateFromValue = dateFromValue;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.initCnd = [
			'and',
			[
					'and',
					[
							'and',
							[
									'and',
									[
											'or',
											[ 'eq', [ '$', 'a.DJLX' ],
													[ 'i', 0 ] ],
											[ 'eq', [ '$', 'a.DJLX' ],
													[ 'i', 6 ] ] ],
									[ 'ge', [ '$', "str(CKRQ,'yyyy-mm-dd')" ],
											[ 's', dateFromValue ] ] ],
							[ 'le', [ '$', "str(CKRQ,'yyyy-mm-dd')" ],
									[ 's', dat ] ] ],
					[ 'eq', [ '$', 'QRBZ' ], [ 'i', 1 ] ] ],
			[ 'eq', [ '$', 'a.CKKF' ], [ '$', '%user.properties.treasuryId' ] ] ];
	phis.application.sup.script.StorageConfirmeList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.StorageConfirmeList,
				phis.script.SimpleList,
				{
					expansion : function(cfg) {
						// 顶部工具栏
						var label = new Ext.form.Label({
							text : "单据日期："
						});
						this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : this.dateFromValue,
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
								"<h1 style='text-align:center'>已记账入库确认单:</h1>",
								'-', label, this.dateField, "至",
								this.dateFieldEnd, tbar ]);
					},
					onChange : function(radiofield, oldvalue) {
						this.QRZT = oldvalue.inputValue;
					},
					doRefresh : function() {
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
						var QRZT = 1;
						this.requestData.cnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'and',
														[
																'or',
																[
																		'eq',
																		[ '$',
																				'a.DJLX' ],
																		[ 'i',
																				0 ] ],
																[
																		'eq',
																		[ '$',
																				'a.DJLX' ],
																		[ 'i',
																				6 ] ] ],
														[
																'ge',
																[ '$',
																		"str(a.CKRQ,'yyyy-mm-dd')" ],
																[ 's',
																		startDate ] ] ],
												[
														'le',
														[ '$',
																"str(a.CKRQ,'yyyy-mm-dd')" ],
														[ 's', endDate ] ] ],
										[ 'eq', [ '$', 'a.QRBZ' ],
												[ 'i', QRZT ] ] ],
								[ 'eq', [ '$', 'a.CKKF' ],
										[ 'i', this.mainApp['phis'].treasuryId ] ] ];
						this.loadData();
					},

					doOpen : function(r) {
						if (!r) {
							r = this.getSelectedRecord();
						}
						if (r == null) {
							return;
						}
						var DJXH = r.get("DJXH");
						var ZBLB = r.get("ZBLB");
						var KFXH = r.get("KFXH");
						var CKRQ = r.get("CKRQ");

						this.storageConfirmeModule = this.createModule(
								"storageConfirmeModule", this.openRef);

						this.storageConfirmeModule
								.on("save", this.onSave, this);
						this.storageConfirmeModule.on("winClose", this.onClose,
								this);
						this.storageConfirmeModule.initPanel();
						var win = this.getWin();
						win.add(this.storageConfirmeModule.initPanel());
						this.storageConfirmeModule.DJXH = DJXH;
						this.storageConfirmeModule.ZBLB = ZBLB;
						this.storageConfirmeModule.KFXH = KFXH;
						this.storageConfirmeModule.CKRQ = CKRQ;

						this.storageConfirmeModule.form.initDataId = DJXH;
						this.storageConfirmeModule.form.loadData();

						this.storageConfirmeModule.list.requestData.cnd = [
								'eq', [ '$', 'DJXH' ], [ 'i', DJXH ] ];
						this.storageConfirmeModule.list.loadData();
						// 如果是单据状态为2 （已确认入库）form中的流转方式隐藏
						this.storageConfirmeModule.form.doIs(r.get("THDJ"));
						if (r.get("QRBZ") == 1) {
							var btns = this.storageConfirmeModule.panel
									.getTopToolbar().items;
							if (!btns) {
								return;
							}
							var n = btns.getCount();
							for ( var i = 0; i < n; i++) {
								var btn = btns.item(i);
								if (i == 1) {
									btn.enable();
								} else {
									btn.disable();
								}
							}
						} else {
							var btns = this.storageConfirmeModule.panel
									.getTopToolbar().items;
							if (!btns) {
								return;
							}
							var n = btns.getCount();
							for ( var i = 0; i < n; i++) {
								var btn = btns.item(i);
								btn.enable();
							}
						}

						win.show();
						win.center();
					},
					doLook : function() {
						this.doOpen();
					},
					onDblClick : function() {
						this.doOpen();
					},
					onClose : function() {
						this.getWin().hide();
					},
					onSave : function() {
						this.fireEvent("save", this);
					}

				})