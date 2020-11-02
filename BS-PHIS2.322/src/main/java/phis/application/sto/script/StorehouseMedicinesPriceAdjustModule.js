/**
 * 药品调价功能
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");

phis.application.sto.script.StorehouseMedicinesPriceAdjustModule = function(cfg) {
	cfg.width = this.width = window.screen.width / 2;
	phis.application.sto.script.StorehouseMedicinesPriceAdjustModule.superclass.constructor.apply(
			this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPriceAdjustModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
				var list = this.getList();
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '未执行调价单',
										region : 'west',
										width : this.width,
										items : this.getUList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '已执行调价单',
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getUList : function() {
				this.uList = this.createModule("uList", this.refUList);
				this.uList.on('priceAdjustWaySelect', this.onPriceAdjustWaySelect, this);
				return this.uList.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			// 调价方式选中后同时刷新左右两边
			onPriceAdjustWaySelect : function(record) {
				this.uList.priceAdjustWayValue = parseInt(record.data.key);
				this.uList.doRefreshWin();
				this.list.priceAdjustWayValue = parseInt(record.data.key);
				this.list.loadData();
		}
,
			afterOpen : function() {
				if(!this.list||!this.uList){return;}
				// 拖动操作
				var firstGrid = this.list.grid;
				var grid=this.uList;
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