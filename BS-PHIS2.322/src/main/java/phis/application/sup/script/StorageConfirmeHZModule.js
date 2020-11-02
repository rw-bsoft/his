$package("phis.application.sup.script");
$import("phis.script.SimpleModule", "phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");
phis.application.sup.script.StorageConfirmeHZModule = function(cfg) {
	cfg.width =(window.screen.width - 150) / 2;
	phis.application.sup.script.StorageConfirmeHZModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sup.script.StorageConfirmeHZModule,phis.script.SimpleModule, {
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
				this.uCheckInList = this.createModule("uCheckInList", this.refUList);
				this.uCheckInList.on("save", this.onSave, this);
				var _ctx = this;
				this.uCheckInList.onDblClick = function(){
					var r = this.getSelectedRecord();
					_ctx.isModify = true;
				    if (r) {
						//Ext.each(r, this.grid.store.remove, this.grid.store);
						//_ctx.checkInList.grid.store.add(r);
						_ctx.uCheckInList.doOpen(r);
					}
				}
				return _ctx.uCheckInList.initPanel();
			},
			// 确定入库界面
			getList : function() {
				this.checkInList = this.createModule("checkInList",this.refList);
				this.checkInList.on("save", this.onSave, this);
				return this.checkInList.initPanel();
			},
			
			afterOpen : function() {
				if(!this.checkInList||!this.uCheckInList){return;}
				var firstGrid = this.checkInList.grid;
				var grid =this.uCheckInList;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								grid.doOpen();
								return true
							}
			 			});
			 },
			 onSave : function() {
				this.checkInList.doRefresh();
                this.uCheckInList.doRefresh();
			}
		});