/**
 * 物资入库管理
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.InventoryInEjListModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.InventoryInEjListModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.InventoryInEjListModule,
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
				this.uInventoryInEjList = this.createModule(
						"uInventoryInEjList", this.refUList);
				this.uInventoryInEjList.oper = this;
				this.uInventoryInEjList.on("save", this.onSave, this);
				return this.uInventoryInEjList.initPanel();
			},
			// 确定入库界面
			getList : function() {
				this.inventoryinEjList = this.createModule("inventoryinEjList",
						this.refList);
				this.inventoryinEjList.oper = this;
				this.inventoryinEjList.on("save", this.onSave, this);
				return this.inventoryinEjList.initPanel();
			},
			afterOpen : function() {
				if (!this.inventoryinEjList || !this.uInventoryInEjList) {
					return;
				}
				// 拖动操作
				var firstGrid = this.inventoryinEjList.grid;
				var grid = this.uInventoryInEjList;
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
				this.uInventoryInEjList.refresh();
				this.inventoryinEjList.refresh();
			}
		});