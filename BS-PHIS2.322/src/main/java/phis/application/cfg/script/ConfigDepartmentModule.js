$package("phis.application.cfg.script")

$import("phis.script.SimpleModule")
phis.application.cfg.script.ConfigDepartmentModule = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 150;
	phis.application.cfg.script.ConfigDepartmentModule.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext
		.extend(
				phis.application.cfg.script.ConfigDepartmentModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								region : 'west',
								width : "48%",
								items : this.getHsqxYgList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
								width : "31%",
								items : this.getHsqxKsListList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'east',
								width : "22%",
								items : this.getDepartmentList()
							} ]
						});
						this.panel = panel;
						return panel;
					},
					getHsqxYgList : function() {
						this.hsqxYgList = this.createModule("getHsqxYgList",
								this.refHsqxYgList);
						this.hsqxYgGrid = this.hsqxYgList.initPanel();
						this.hsqxYgList.on("loadData", this.onListLoadData,
								this);
						this.hsqxYgGrid.on("loadData", this.onListLoadData,
								this);
						this.hsqxYgGrid.on("rowClick", this.onListRowClick,
								this);
						return this.hsqxYgGrid;
					},
					getHsqxKsListList : function() {
						this.hsqxKsListList = this.createModule(
								"getHsqxKsListList", this.refHsqxKsListList);
						var _ctx = this;
						this.hsqxKsListList.onDblClick = function(grid, index,
								e) {
							_ctx.isModify = true;
							var records = grid.getSelectionModel()
									.getSelected();
							Ext.each(records, grid.store.remove, grid.store);
							_ctx.departmentList.grid.store.add(records);
							_ctx.departmentList.grid.store.sort('KSDM', 'ASC');
						}
						this.hsqxKsListGrid = this.hsqxKsListList.initPanel();
						this.hsqxKsListList.on("loadData",
								this.onKsListLoadData, this);
						this.hsqxKsListGrid.on("loadData",
								this.onKsListLoadData, this);
						return this.hsqxKsListGrid;
					},
					getDepartmentList : function() {
						this.departmentList = this.createModule(
								"getDepartmentList", this.refDepartmentList);
						var _ctx = this;
						this.departmentList.onDblClick = function(grid, index,
								e) {
							_ctx.isModify = true;
							var records = grid.getSelectionModel()
									.getSelected();
							Ext.each(records, grid.store.remove, grid.store);
							_ctx.hsqxKsListList.grid.store.add(records);
							_ctx.hsqxKsListList.grid.store.sort('KSDM', 'ASC');
						}
						this.departmentGrid = this.departmentList.initPanel();
						this.departmentList.loadData();
						return this.departmentGrid;
					},
					onListLoadData : function(store) {
						// 如果第一次打开页面，默认模拟选中第一行
						if (this.hsqxKsListList) {
							this.hsqxKsListList.clear();
						}
						if (store.getCount() > 0) {
							if (!this.initDataId) {
								this.hsqxYgGrid.fireEvent("rowclick",
										this.hsqxYgGrid, 0);
							}
						} else {
							this.hsqxKsListList.YGID = "";
						}
					},
					onKsListLoadData : function(store) {
						var r = store.getAt(0);
						if (r) {
							this.hsqxYgList.KSDM = r.get("KSDM");
						} else {
							this.hsqxYgList.KSDM = 0;
						}
					},
					onListRowClick : function(hsqxYgGrid, rowIndex, e) {
						var r = hsqxYgGrid.store.getAt(rowIndex);
						if (!r)
							return;
						this.hsqxKsListList.YGID = r.get("YGID");
						this.hsqxKsListList.requestData.cnd = [ 'eq',
								[ '$', 'YGID' ], [ 's', r.get("YGID") ] ];
						this.departmentList.requestData.cnds = [ 'eq',
								[ '$', 'YGID' ], [ 's', r.get("YGID") ] ];
						this.hsqxKsListList.loadData();
						this.departmentList.loadData();
					},
					afterOpen : function() {
						// 拖动操作
						var _ctx = this;
						var firstGrid = this.departmentList.grid;
						var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
						var firstGridDropTarget = new Ext.dd.DropTarget(
								firstGridDropTargetEl,
								{
									ddGroup : 'firstGridKSSelectGroup',
									notifyDrop : function(ddSource, e, data) {
										_ctx.isModify = true;
										var records = ddSource.dragData.selections;
										Ext.each(records,
												ddSource.grid.store.remove,
												ddSource.grid.store);
										firstGrid.store.add(records);
										firstGrid.store.sort('KSDM', 'ASC');
										return true
									}
								});
						var secondGrid = this.hsqxKsListList.grid;
						var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
						var secondGridDropTarget = new Ext.dd.DropTarget(
								secondGridDropTargetEl,
								{
									ddGroup : 'secondGridDepartmentGroup',
									notifyDrop : function(ddSource, e, data) {
										_ctx.isModify = true;
										var records = ddSource.dragData.selections;
										Ext.each(records,
												ddSource.grid.store.remove,
												ddSource.grid.store);
										secondGrid.store.add(records);
										secondGrid.store.sort('KSDM', 'ASC');
										return true
									}
								});
					},
					onBeforeBusSelect : function() {
						if (this.isModify) {
							if (confirm('医生权限数据已修改，是否保存?')) {
								return this.hsqxKsListList.doSave()
							} else {
								return true;
							}
						}
						return true;
					}
				});