/**
 * 药品出库功能
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyCheckOutModule = function(cfg) {
	cfg.width=this.width =window.screen.width/2;
	phis.application.pha.script.PharmacyCheckOutModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave',this.doSave,this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckOutModule,phis.script.SimpleModule,
		{
			initPanel : function(sc) {
					if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
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
										border : false,
										ddGroup : "firstGrid",
										split : true,
										title : '未确定出库',
										region : 'west',
										width:this.width,
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
										title : '确定出库',
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
			
			getUList : function() {
				this.uCheckOutList = this.createModule(
						"medicinesUndeterminedCheckOutList", this.refUList);
				this.uCheckOutList.on("checkOutWaySelect",
						this.onCheckOutWaySelect, this)
				return this.uCheckOutList.initPanel();
			},
			getList : function() {
				this.checkOutList = this.createModule("medicinesCheckOutList",
						this.refList);
				return this.checkOutList.initPanel();
			},
			// 出库方式选中后同时刷新左右两边
			onCheckOutWaySelect : function(record) {
				this.uCheckOutList.checkOutWayValue = parseInt(record.data.key);
				this.uCheckOutList.doRefreshWin();
				this.checkOutList.checkOutWayValue = parseInt(record.data.key);
				this.checkOutList.doCndQuery();
			},
			afterOpen : function() {
				if(!this.checkOutList||!this.uCheckOutList){return;}
				// 拖动操作
				var firstGrid = this.checkOutList.grid;
				var grid=this.uCheckOutList;
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