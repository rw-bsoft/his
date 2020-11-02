/**
 * 药品入库功能
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyCheckInModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.pha.script.PharmacyCheckInModule.superclass.constructor.apply(
			this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckInModule,
		phis.script.SimpleModule, {
			
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
										title : '未确定入库',
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
										title : '确定入库',
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
			// 未确定入库界面
			getUList : function() {
				this.uCheckInList = this.createModule(
						"medicinesUndeterminedCheckInList", this.refUList);
				this.uCheckInList.on("checkInWaySelect",
						this.onCheckInWaySelect, this)
				return this.uCheckInList.initPanel();
			},
			// 确定入库界面
			getList : function() {
				this.checkInList = this.createModule("medicinesCheckInList",
						this.refList)
				return this.checkInList.initPanel();
			},
			// 入库方式选中后同时刷新左右两边
			onCheckInWaySelect : function(record) {
				this.uCheckInList.checkInWayValue = parseInt(record.data.key);
				this.uCheckInList.doRefreshWin();
				this.checkInList.checkInWayValue = parseInt(record.data.key);
				this.checkInList.doCndQuery();
			},
			afterOpen : function() {
				if(!this.checkInList||!this.uCheckInList){return;}
				// 拖动操作
				var firstGrid = this.checkInList.grid;
				var grid=this.uCheckInList;
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