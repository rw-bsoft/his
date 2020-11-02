$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");

phis.application.sup.script.ApplyManagementModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.ApplyManagementModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on('doSave', this.doSave, this);
}
Ext
		.extend(
				phis.application.sup.script.ApplyManagementModule,
				phis.script.SimpleModule,
				{

					initPanel : function(sc) {
						if (this.mainApp['phis'].treasuryId == null
								|| this.mainApp['phis'].treasuryId == ""
								|| this.mainApp['phis'].treasuryId == undefined) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryEjkf != 0) {
							Ext.MessageBox.alert("提示", "该库房不是一级库房!");
							return;
						}
						if (this.mainApp['phis'].treasuryCsbz != 1) {
							Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
							return;
						}
						var zblbItems = this.getZblb();
						this.zblbItems = zblbItems;
						var radioGroup = new Ext.form.RadioGroup(
								{
									height : 20,
									width : 520,
									id : 'ApplyManagementZblb',
									name : 'ApplyManagementZblb', // 后台返回的JSON格式，直接赋值
									value : zblbItems[0].initialConfig.inputValue,
									items : zblbItems,
									listeners : {
										change : function(group, newValue,
												oldValue) {
											if (this.uapplyList
													&& this.applyList) {
												// this.beforeclose();
												var zblbValue = parseInt(newValue.inputValue);
												var addCnd = [ 'eq',
														[ '$', 'ZBLB' ],
														[ 'i', zblbValue ] ];

												this.uapplyList.zblb = zblbValue;
												this.uapplyList.requestData.pageNo = 1;
												this.uapplyList.requestData.cnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'eq',
																				[
																						'$',
																						'ZBLB' ],
																				[
																						'i',
																						zblbValue ] ],
																		[
																				'eq',
																				[
																						'$',
																						'KFXH' ],
																				[
																						'i',
																						this.mainApp['phis'].treasuryId ] ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[
																				'i',
																				this.uapplyList.statusRadio
																						.getValue().inputValue ] ] ],
														[
																'eq',
																[ '$', 'DJLX' ],
																[ 'i', 6 ] ] ];
												this.uapplyList.initCnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'eq',
																				[
																						'$',
																						'ZBLB' ],
																				[
																						'i',
																						zblbValue ] ],
																		[
																				'eq',
																				[
																						'$',
																						'KFXH' ],
																				[
																						'i',
																						this.mainApp['phis'].treasuryId ] ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[
																				'i',
																				this.uapplyList.statusRadio
																						.getValue().inputValue ] ] ],
														[
																'eq',
																[ '$', 'DJLX' ],
																[ 'i', 6 ] ] ];
												this.uapplyList.loadData();

												this.applyList.zblb = zblbValue;
												this.applyList.requestData.pageNo = 1;
												this.applyList.requestData.cnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'eq',
																				[
																						'$',
																						'ZBLB' ],
																				[
																						'i',
																						zblbValue ] ],
																		[
																				'eq',
																				[
																						'$',
																						'KFXH' ],
																				[
																						'i',
																						this.mainApp['phis'].treasuryId ] ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[ 'i',
																				2 ] ] ],
														[
																'eq',
																[ '$', 'DJLX' ],
																[ 'i', 6 ] ] ];
												;
												this.applyList.initCnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'eq',
																				[
																						'$',
																						'ZBLB' ],
																				[
																						'i',
																						zblbValue ] ],
																		[
																				'eq',
																				[
																						'$',
																						'KFXH' ],
																				[
																						'i',
																						this.mainApp['phis'].treasuryId ] ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[ 'i',
																				2 ] ] ],
														[
																'eq',
																[ '$', 'DJLX' ],
																[ 'i', 6 ] ] ];
												;
												this.applyList.loadData();
											}
										},
										scope : this
									}
								});
						this.radioGroup = radioGroup;
						if (this.panel) {
							return this.panel;
						}
						var schema = sc
						if (!schema) {
							var re = util.schema.loadSync(this.entryName)
							if (re.code == 200) {
								schema = re.schema;
							} else {
								this.processReturnMsg(re.code, re.msg,
										this.initPanel)
								return;
							}
						}
						this.schema = schema;
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [ '', radioGroup ],
							items : [ {
								layout : "fit",
								ddGroup : "firstGrid",
								border : false,
								split : true,
								region : 'west',
								width : this.width,
								items : this.getUList(),
								selModel : new Ext.grid.RowSelectionModel({
									singleSelect : true
								})
							}, {
								layout : "fit",
								ddGroup : "secondGrid",
								border : false,
								split : true,
								region : 'center',
								items : this.getList(),
								selModel : new Ext.grid.RowSelectionModel({
									singleSelect : true
								})
							} ]

						});
						this.panel = panel;
						return panel;
					},
					// 未确定出库界面
					getUList : function() {
						this.uapplyList = this.createModule(
								"undeterminedapplyList", this.refUList);
						this.uapplyList.opener = this;
						this.uapplyList.on("save", this.onSave, this);
						this.uapplyList.zblb = this.zblbItems[0].initialConfig.inputValue;
						this.uapplyList.requestData.cnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'eq',
														[ '$', 'ZBLB' ],
														[
																'i',
																this.uapplyList.zblb ] ],
												[
														'eq',
														[ '$', 'KFXH' ],
														[
																'i',
																this.mainApp['phis'].treasuryId ] ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 0 ] ] ],
								[ 'eq', [ '$', 'DJLX' ], [ 'i', 6 ] ] ];
						this.uapplyList.initCnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'eq',
														[ '$', 'ZBLB' ],
														[
																'i',
																this.uapplyList.zblb ] ],
												[
														'eq',
														[ '$', 'KFXH' ],
														[
																'i',
																this.mainApp['phis'].treasuryId ] ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 0 ] ] ],
								[ 'eq', [ '$', 'DJLX' ], [ 'i', 6 ] ] ];
						this.uapplyList.requestData.pageNo = 1;
						return this.uapplyList.initPanel();
					},
					// 确定出库界面
					getList : function() {
						this.applyList = this.createModule("applyList",
								this.refList);
						this.applyList.opener = this;
						this.applyList.on("save", this.onSave, this);
						this.applyList.zblb = this.zblbItems[0].initialConfig.inputValue;
						this.applyList.requestData.cnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'eq',
														[ '$', 'ZBLB' ],
														[
																'i',
																this.applyList.zblb ] ],
												[
														'eq',
														[ '$', 'KFXH' ],
														[
																'i',
																this.mainApp['phis'].treasuryId ] ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 2 ] ] ],
								[ 'eq', [ '$', 'DJLX' ], [ 'i', 6 ] ] ];
						this.applyList.initCnd = [
								'and',
								[
										'and',
										[
												'and',
												[
														'eq',
														[ '$', 'ZBLB' ],
														[
																'i',
																this.applyList.zblb ] ],
												[
														'eq',
														[ '$', 'KFXH' ],
														[
																'i',
																this.mainApp['phis'].treasuryId ] ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 2 ] ] ],
								[ 'eq', [ '$', 'DJLX' ], [ 'i', 6 ] ] ];
						this.applyList.requestData.pageNo = 1;
						return this.applyList.initPanel();
					},
					afterOpen : function() {
						if (!this.applyList || !this.uapplyList) {
							return;
						}
						// 拖动操作
						var firstGrid = this.applyList.grid;
						var grid = this.uapplyList;
						var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
						var firstGridDropTarget = new Ext.dd.DropTarget(
								firstGridDropTargetEl,
								{
									ddGroup : 'firstGridDDGroup',
									notifyDrop : function(ddSource, e, data) {
										var records = ddSource.dragData.selections;
										grid.doCommit();
										return true
									}
								});
					},
					onSave : function() {
						this.applyList.refresh();
						this.uapplyList.refresh();
					},
					getZblb : function() {
						var kfxh = this.mainApp['phis'].treasuryId;
						var body = {};
						body["KFXH"] = kfxh;
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getZblbByKfxh",
							body : body
						});
						var kfzblb = [];
						kfzblb = r.json.list;
						var items = [];
						for ( var i = 0; i < kfzblb.length; i++) {
							var item = new Ext.form.Radio({
								boxLabel : kfzblb[i][1],
								name : 'TansferManagementZblb',
								inputValue : kfzblb[i][0]
							})
							items.push(item);
						}
						return items;
					},
					createApplyManagementModule : function() {
						return this.createModule(
								"applyManagementModule", this.addRef);
					}
				});