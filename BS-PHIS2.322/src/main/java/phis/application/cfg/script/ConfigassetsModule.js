/**
 * 药品入库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.ConfigassetsModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 500;
	phis.application.cfg.script.ConfigassetsModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.cfg.script.ConfigassetsModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '初建账册-固定资产',
								region : 'north',
								height : 130,
								items : this.getForm()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getList()
							} ],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
						this.panel = panel;
						return panel;
					},
					getForm : function() {
						this.form = this.createModule("form", this.refForm);
						this.form.operater = this;
						return this.form.initPanel();
					},
					getList : function() {
						this.list = this.createModule("list", this.refList);
						this.gridlist = this.list.initPanel();
						return this.gridlist;
					},
					doAction : function(item, e) {
						var cmd = item.cmd
						var script = item.script
						cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
						if (script) {
							$require(script, [
									function() {
										eval(script + '.do' + cmd
												+ '.apply(this,[item,e])')
									}, this ])
						} else {
							var action = this["do" + cmd]
							if (action) {
								action.apply(this, [ item, e ])
							}
						}
					},
					doSave : function() {
						var _ctr = this;
						var whatsthetime = function() {
							var dwmc = _ctr.form.form.getForm().findField(
									"DWMC").getValue();
							var wzmc = _ctr.form.form.getForm().findField(
									"WZMC").getValue();
							if (_ctr.form.wzxh == 0 || wzmc == '') {
								MyMessageTip.msg("提示",
										"物资名称不能为空或者信息有误 ,请用拼音码调出!", true);
								return;
							}
							if (_ctr.form.ghdw == 0 || dwmc == '') {
								MyMessageTip.msg("提示",
										"供货单位不能为空或者信息有误 ,请用拼音码调出!", true);
								return;
							}
							var formdate = {
								"WZXH" : _ctr.form.wzxh,
								"CJXH" : _ctr.form.cjxh,
								"KFXH" : _ctr.mainApp.treasuryId,
								"ZBLB" : _ctr.form.zblb,
								"GHDW" : _ctr.form.ghdw,
								"TZRQ" : _ctr.form.form.getForm().findField(
										"TZRQ").getValue()
										.format('Y-m-d H:i:s'),
								"ZCYZ" : _ctr.form.form.getForm().findField(
										"ZCYZ").getValue(),
								"WHYZ" : _ctr.form.form.getForm().findField(
										"WHYZ").getValue(),
								"HBDW" : _ctr.form.form.getForm().findField(
										"HBDW").getValue()
							};
							var store = _ctr.gridlist.getStore();
							var n = store.getCount();
							var cszc = [];
							if (n == 1) {
								if (_ctr.list.store.getAt(0).data["WZSL"] == undefined
										|| _ctr.list.store.getAt(0).data["WZSL"] == 0
										|| _ctr.list.store.getAt(0).data["CZYZ"] == undefined
										|| _ctr.list.store.getAt(0).data["CZYZ"] == 0
										|| _ctr.list.store.getAt(0).data["WZZT"] == undefined
										|| _ctr.list.store.getAt(0).data["WZZT"] == "") {
									MyMessageTip.msg("提示",
											"第1行物资数量、重置原值、状态不能为空!", true);
									return;
								}
							}
							for ( var i = 0; i < n; i++) {
								if (_ctr.list.store.getAt(i).data["WZSL"] != undefined
										&& _ctr.list.store.getAt(i).data["WZSL"] != 0) {
									if (_ctr.list.store.getAt(i).data["WZZT"] == ""
											&& _ctr.list.store.getAt(i).data["WZZT"] != undefined) {
										MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行状态不能为空!", true);
										return;
									}
									if (_ctr.list.store.getAt(i).data["WZZT"] == "2") {
										if (_ctr.list.store.getAt(i).data["WZSL"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == 0
												|| _ctr.list.store.getAt(i).data["CZYZ"] == undefined
												|| _ctr.list.store.getAt(i).data["CZYZ"] == 0
												|| _ctr.list.store.getAt(i).data["WZZT"] == undefined) {
											MyMessageTip.msg("提示", "第"
													+ (i + 1)
													+ "行物资数量、重置原值、状态不能为空!",
													true);
											return;
										}
									} else if (_ctr.list.store.getAt(i).data["WZZT"] == "1") {
										if (_ctr.list.store.getAt(i).data["WZSL"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == 0
												|| _ctr.list.store.getAt(i).data["CZYZ"] == undefined
												|| _ctr.list.store.getAt(i).data["CZYZ"] == 0
												|| _ctr.list.store.getAt(i).data["WZZT"] == undefined
												|| _ctr.list.store.getAt(i).data["ZYKS"] == undefined
												|| _ctr.list.store.getAt(i).data["ZYKS"] == ""
												|| _ctr.list.store.getAt(i).data["QYRQ"] == undefined
												|| _ctr.list.store.getAt(i).data["QYRQ"] == "") {
											MyMessageTip
													.msg(
															"提示",
															"第"
																	+ (i + 1)
																	+ "行物资数量、重置原值、状态、启用日期、科室不能为空!",
															true);
											return;

										}
									} else {
										if (_ctr.list.store.getAt(i).data["WZSL"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == 0
												|| _ctr.list.store.getAt(i).data["CZYZ"] == undefined
												|| _ctr.list.store.getAt(i).data["CZYZ"] == 0
												|| _ctr.list.store.getAt(i).data["WZZT"] == undefined
												|| _ctr.list.store.getAt(i).data["ZYKS"] == undefined
												|| _ctr.list.store.getAt(i).data["ZYKS"] == "") {
											MyMessageTip.msg("提示", "第"
													+ (i + 1)
													+ "行物资数量、重置原值、状态、科室不能为空!",
													true);
											return;
										}
									}
								}
								if (_ctr.list.store.getAt(i).data["WZZT"] == "2") {
									if (_ctr.list.store.getAt(i).data["WZSL"] != undefined
											&& _ctr.list.store.getAt(i).data["WZSL"] != 0
											&& _ctr.list.store.getAt(i).data["CZYZ"] != undefined
											&& _ctr.list.store.getAt(i).data["CZYZ"] != 0
											&& _ctr.list.store.getAt(i).data["WZZT"] != undefined) {
										cszc
												.push(_ctr.list.store.getAt(i).data);
									}
								} else if (_ctr.list.store.getAt(i).data["WZZT"] == "1") {
									if (_ctr.list.store.getAt(i).data["WZSL"] != undefined
											&& _ctr.list.store.getAt(i).data["WZSL"] != 0
											&& _ctr.list.store.getAt(i).data["CZYZ"] != undefined
											&& _ctr.list.store.getAt(i).data["CZYZ"] != 0
											&& _ctr.list.store.getAt(i).data["WZZT"] != undefined
											&& _ctr.list.store.getAt(i).data["ZYKS"] != undefined
											&& _ctr.list.store.getAt(i).data["QYRQ"] != undefined) {
										cszc
												.push(_ctr.list.store.getAt(i).data);
									}

								} else {
									if (_ctr.list.store.getAt(i).data["WZSL"] != undefined
											&& _ctr.list.store.getAt(i).data["WZSL"] != 0
											&& _ctr.list.store.getAt(i).data["CZYZ"] != undefined
											&& _ctr.list.store.getAt(i).data["CZYZ"] != 0
											&& _ctr.list.store.getAt(i).data["WZZT"] != undefined
											&& _ctr.list.store.getAt(i).data["ZYKS"] != undefined) {
										cszc
												.push(_ctr.list.store.getAt(i).data);
									}
								}
							}
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "configInventoryInitialService",
								serviceAction : "saveAssetsIn",
								body : formdate,
								listBody : cszc
							});

							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg,
										this.onBeforeSave);
								return;
							} else {
								MyMessageTip.msg("提示", "保存成功!", true);
								_ctr.doCancel();
							}
						}
						whatsthetime.defer(500);
					},
					doCancel : function() {
						var win = this.getWin();
						if (win)
							win.hide();
						this.opener.refresh();
					},
					doUpdat : function(jlxh, wzxh, cjxh) {
						this.form.doUpdat(jlxh);
						this.list.requestData.cnd = [
								'and',
								[
										'and',
										[
												'and',
												[ 'eq', [ '$', 'a.WZXH' ],
														[ 'i', wzxh ] ],
												[ 'eq', [ '$', 'a.CJXH' ],
														[ 'i', cjxh ] ] ],
										[ 'eq', [ '$', 'a.JGID' ],
												[ 's', this.mainApp['phisApp'].deptId ] ] ],
								[ 'eq', [ '$', 'a.KFXH' ],
										[ 's', this.mainApp['phis'].treasuryId ] ] ];
						this.list.initCnd = [
								'and',
								[
										'and',
										[ 'eq', [ '$', 'a.WZXH' ],
												[ 'i', wzxh ] ],
										[ 'eq', [ '$', 'a.CJXH' ],
												[ 'i', cjxh ] ] ],
								[ 'eq', [ '$', 'a.KFXH' ],
										[ 's', this.mainApp['phis'].treasuryId ] ] ];
						this.list.loadData();
					},
					doNew : function() {
						if (this.form) {
							this.form.doNew();
						}
						if (this.list) {
							this.list.doNew();
							// this.list.doCreate();
						}
					}
				});