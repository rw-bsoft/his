/**
 * 财务验收功能
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseFinancialAcceptanceModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	cfg.gridDDGroup = "firstGridDDGroup";
	phis.application.sto.script.StorehouseFinancialAcceptanceModule.superclass.constructor.apply(
			this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
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
							items : [{
										layout : "fit",
										ddGroup : "firstGrid",
										border : false,
										split : true,
										title : '未验收入库单',
										region : 'west',
										width : this.width,
										items : this.getUList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}, {
										layout : "fit",
										ddGroup : "secondGrid",
										border : false,
										split : true,
										title : '已验收入库单',
										region : 'center',
										items : this.getList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}]
						});
				this.panel = panel;
				return panel;
			},
			// 未验收入库单界面
			getUList : function() {
				this.uAcceptanceList = this.createModule(
						"StorehouseUndeterminedAcceptanceList", this.refUList);
				this.uAcceptanceList.on("checkInWaySelect",
						this.onCheckInWaySelect, this)
				return this.uAcceptanceList.initPanel();
			},
			// 已验收入库单界面
			getList : function() {
				this.AcceptanceList = this.createModule("StorehouseAcceptanceList",
						this.refList)
				return this.AcceptanceList.initPanel();
			},
			// 入库方式选中后同时刷新左右两边
			onCheckInWaySelect : function(record) {
				this.uAcceptanceList.checkInWayValue =record.data;
				this.uAcceptanceList.doRefreshWin();
				this.AcceptanceList.checkInWayValue =record.data;
				this.AcceptanceList.doCndQuery();
			},
			afterOpen : function() {
				if(!this.AcceptanceList||!this.uAcceptanceList){return;}
				if(this.uAcceptanceList.checkInWayValue){
				this.uAcceptanceList.doRefresh();}
				// 拖动操作
				var firstGrid = this.AcceptanceList.grid;
				var grid=this.uAcceptanceList;
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
			}
		});