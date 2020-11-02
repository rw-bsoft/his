/**
 * 物资出库确认（二级）
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.TransferManagementModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.TransferManagementModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on('doSave', this.doSave, this);
}
Ext
		.extend(
				phis.application.sup.script.TransferManagementModule,
				phis.script.SimpleModule,
				{

					initPanel : function(sc) {
						// this.mainApp['phis'].treasuryId = 50;
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
						// 账簿类别
						var zblbItems = this.getZblb();
						this.zblbItems = zblbItems;
						var value = null;
						if (zblbItems && zblbItems[0]) {
							value = zblbItems[0].initialConfig.inputValue;
						}
						var radioGroup = new Ext.form.RadioGroup(
								{
									height : 20,
									width : 520,
									id : 'TransferManagementZblb',
									name : 'TransferManagementZblb', // 后台返回的JSON格式，直接赋值
									value : value,
									items : zblbItems,
									listeners : {
										change : function(group, newValue,
												oldValue) {
											if (this.utransferList
													&& this.transferList) {
												// this.beforeclose();
												var zblbValue = parseInt(newValue.inputValue);
												var addCnd = [ 'eq',
														[ '$', 'ZBLB' ],
														[ 'i', zblbValue ] ];

												this.utransferList.zblb = zblbValue;
												this.utransferList.requestData.pageNo = 1;
												this.utransferList.requestData.cnd = [
														'and',
														[
																'and',
																[
																		'eq',
																		[ '$',
																				'ZBLB' ],
																		[ 'i',
																				zblbValue ] ],
																[
																		'eq',
																		[ '$',
																				'KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'eq',
																[ '$', 'DJZT' ],
																[
																		'i',
																		this.utransferList.statusRadio
																				.getValue().inputValue ] ] ];
												this.utransferList.initCnd = [
														'and',
														[
																'and',
																[
																		'eq',
																		[ '$',
																				'ZBLB' ],
																		[ 'i',
																				zblbValue ] ],
																[
																		'eq',
																		[ '$',
																				'KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'eq',
																[ '$', 'DJZT' ],
																[
																		'i',
																		this.utransferList.statusRadio
																				.getValue().inputValue ] ] ];
												this.utransferList.loadData();

												this.transferList.zblb = zblbValue;
												this.transferList.requestData.pageNo = 1;
												this.transferList.requestData.cnd = [
														'and',
														[
																'eq',
																[ '$', 'ZBLB' ],
																[ 'i',
																		zblbValue ] ],
														[
																'and',
																[
																		'eq',
																		[ '$',
																				'KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[ 'i',
																				2 ] ] ] ];
												this.transferList.initCnd = [
														'and',
														[
																'eq',
																[ '$', 'ZBLB' ],
																[ 'i',
																		zblbValue ] ],
														[
																'and',
																[
																		'eq',
																		[ '$',
																				'KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ],
																[
																		'eq',
																		[ '$',
																				'DJZT' ],
																		[ 'i',
																				2 ] ] ] ];
												this.transferList.loadData();
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
						this.utransferList = this.createModule(
								"undeterminedtransferList", this.refUList);
						this.utransferList.on("save", this.onSave, this);
						var zblbValue = null;
						if (this.zblbItems && this.zblbItems[0]) {
							zblbValue = this.zblbItems[0].initialConfig.inputValue;
						}
						this.utransferList.zblb = this.radioGroup.value;
						this.utransferList.requestData.cnd = [
								'and',
								[ 'eq', [ '$', 'ZBLB' ], [ 'i', zblbValue ] ],
								[
										'and',
										[
												'eq',
												[ '$', 'KFXH' ],
												[ 'i', this.mainApp['phis'].treasuryId ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 0 ] ] ] ];
						this.utransferList.cnds = [
								'and',
								[ 'eq', [ '$', 'ZBLB' ], [ 'i', zblbValue ] ],
								[
										'and',
										[
												'eq',
												[ '$', 'KFXH' ],
												[ 'i', this.mainApp['phis'].treasuryId ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 0 ] ] ] ];
						this.utransferList.requestData.pageNo = 1;
						return this.utransferList.initPanel();
					},
					// 确定出库界面
					getList : function() {
						this.transferList = this.createModule("transferList",
								this.refList);
						this.transferList.on("save", this.onSave, this);
						var zblbValue = null;
						if (this.zblbItems && this.zblbItems[0]) {
							zblbValue = this.zblbItems[0].initialConfig.inputValue;
						}
						this.transferList.zblb = this.radioGroup.value;
						this.transferList.requestData.cnd = [
								'and',
								[ 'eq', [ '$', 'ZBLB' ], [ 'i', zblbValue ] ],
								[
										'and',
										[
												'eq',
												[ '$', 'KFXH' ],
												[ 'i', this.mainApp['phis'].treasuryId ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 2 ] ] ] ];
						this.transferList.cnds = [
								'and',
								[ 'eq', [ '$', 'ZBLB' ], [ 'i', zblbValue ] ],
								[
										'and',
										[
												'eq',
												[ '$', 'KFXH' ],
												[ 'i', this.mainApp['phis'].treasuryId ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 2 ] ] ] ];
						this.transferList.requestData.pageNo = 1;
						return this.transferList.initPanel();
					},
					afterOpen : function() {
						if (!this.transferList || !this.utransferList) {
							return;
						}
						// 拖动操作
						var firstGrid = this.transferList.grid;
						var grid = this.utransferList;
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
						this.transferList.refresh();
						this.utransferList.refresh();
					},
					getZblb : function() {
						var kfxh = this.mainApp['phis'].treasuryId;
						var body = {};
						body["KFXH"] = kfxh;
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getZblbByKfxh",
							method : "execute",
							body : body
						});
						var kfzblb = [];
						kfzblb = r.json.list;
						var items = [];
						if (kfzblb) {
							for ( var i = 0; i < kfzblb.length; i++) {
								var item = new Ext.form.Radio({
									boxLabel : kfzblb[i][1],
									name : 'TansferManagementZblb',
									inputValue : kfzblb[i][0]
								})
								items.push(item);
							}
						}
						return items;
					}
				});