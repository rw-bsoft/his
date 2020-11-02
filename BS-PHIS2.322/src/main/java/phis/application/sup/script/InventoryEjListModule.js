/**
 * 物资入库管理
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.InventoryEjListModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.InventoryEjListModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.InventoryEjListModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].treasuryId == null
						|| this.mainApp['phis'].treasuryId == ""
						|| this.mainApp['phis'].treasuryId == undefined) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf == 0) {
					Ext.MessageBox.alert("提示", "该库房不是二级库房!");
					return;
				}
				if (this.mainApp['phis'].treasuryCsbz != 1) {
					Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
					return;
				}
				var r1 = phis.script.rmi.miniJsonRequestSync({
					serviceId : "inventoryService",
					serviceAction : "queryDJSH",
					KFDJ : 2
				});
				if (r1.code == 801) {
					MyMessageTip.msg("提示", "入库管理业务有单据未记账，不能盘点!", true);
					return;
				}
				if (r1.code == 802) {
					MyMessageTip.msg("提示", "出库管理业务有单据未记账，不能盘点!", true);
					return;
				}
				if (r1.code == 804) {
					MyMessageTip.msg("提示", "调拨管理业务有单据未记账，不能盘点!", true);
					return;
				}
				if (r1.code == 805) {
					MyMessageTip.msg("提示", "消耗明细业务有单据未生成出库单，不能盘点!", true);
					return;
				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
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
					items : [ {
						layout : "fit",
						ddGroup : "firstGrid",
						border : false,
						split : true,
						// title : '未确定入库',
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
						// title : '确定入库',
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
			// 未确定入库界面
			getUList : function() {
				this.uInventoryListEj = this.createModule("uInventoryListEj",
						this.refUList);
				this.uInventoryListEj.oper = this;
				this.uInventoryListEj.on("save", this.onSave, this);
				return this.uInventoryListEj.initPanel();
			},
			// 确定入库界面
			getList : function() {
				this.inventoryListEj = this.createModule("inventoryListEj",
						this.refList);
				this.inventoryListEj.oper = this;
				this.inventoryListEj.on("save", this.onSave, this);
				return this.inventoryListEj.initPanel();
			},
			afterOpen : function() {
				if (!this.inventoryListEj || !this.uInventoryListEj) {
					return;
				}
				// 拖动操作
				var firstGrid = this.inventoryListEj.grid;
				var grid = this.uInventoryListEj;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								grid.doCommit();
								return true
							}
						});
			},
			onSave : function() {
				this.uInventoryListEj.refresh();
				this.inventoryListEj.refresh();
			}
		});