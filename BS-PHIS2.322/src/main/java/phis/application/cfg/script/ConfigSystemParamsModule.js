$package("phis.application.cfg.script")

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory")
phis.application.cfg.script.ConfigSystemParamsModule = function(cfg) {
	phis.application.cfg.script.ConfigSystemParamsModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cfg.script.ConfigSystemParamsModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'east',
										width : "40%",
										title : "未分配参数",
										items : this.getUnusedList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										title : "已分配参数",
										items : this.getUsedList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : "20%",
										title : "业务功能类别",
										items : this.getNavTree()
									}]
						});
				this.panel = panel;
				this.usedCollections = new Ext.util.MixedCollection();
				return panel;
			},
			getNavTree : function() {
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : this.navDic,
							parentKey : "",
							parentText : "全部",
							rootVisible : false
						})
				this.tree = tf.tree
				this.tree.on("click", this.onNavTreeClick, this)
				return this.tree;
			},
			getUsedList : function() {
				this.usedList = this.createModule("refUsedList",
						this.refUsedList);
				var _ctx = this;
				this.usedList.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected();
					_ctx.changeRecords("remove", records)
					Ext.each(records, grid.store.remove, grid.store);
					_ctx.unusedList.grid.store.add(records);
					_ctx.unusedList.grid.store.sort('CSMC', 'ASC');
				}
				this.usedList.on("loadData", this.onUsedListLoadData, this);
				return this.usedList.initPanel();
			},
			getUnusedList : function() {
				this.unusedList = this.createModule("refUnUsedList",
						this.refUnUsedList);
				var _ctx = this;
				this.unusedList.requestData.initCnd = ['eq', ['$', 'JGID'],
						['s', this.mainApp['phisApp'].deptId]]
				this.unusedList.requestData.cnd = ['eq', ['$', 'JGID'],
						['s', this.mainApp['phisApp'].deptId]];
				this.unusedList.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected();
					_ctx.changeRecords("add", records)
					Ext.each(records, grid.store.remove, grid.store);
					_ctx.usedList.grid.store.add(records);
					_ctx.usedList.grid.store.sort('CSMC', 'ASC');
				}
				this.unusedList.on("loadData", this.unusedlistLoadData, this);
				return this.unusedList.initPanel();
			},
			onUsedListLoadData : function(store) {
				this.unusedList.loadData();
			},
			unusedlistLoadData : function(store) {
				store.filterBy(function(f_record, id) {
					var count = this.usedList.grid.store.getCount();
					for (var i = 0; i < count; i++) {
						if (f_record.data.CSMC == this.usedList.store.getAt(i).data.CSMC)
							return false;
					}
					return true;
				}, this);
				this.isModify = false;
			},
			onNavTreeClick : function(node, rowIndex, e) {
				this.usedList.requestData.initCnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'SSLB'], ['s', node.attributes.key]]];
				this.usedList.requestData.cnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'SSLB'], ['s', node.attributes.key]]];
				this.usedList.loadData();
			},
			changeRecords : function(op, records) {
				var items = [].concat(records);
				if (op == 'add') {
					for (var i = 0; i < items.length; i++) {
						this.usedCollections
								.add(items[i].get("CSMC"), items[i])
					}
				} else {
					for (var i = 0; i < items.length; i++) {
						this.usedCollections.remove(items[i]);
					}
				}
			},
			afterOpen : function() {
				// 拖动操作
				var _ctx = this;
				var firstGrid = this.usedList.grid;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								_ctx.changeRecords("add", records)
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								firstGrid.store.add(records);
								firstGrid.store.sort('CSMC', 'ASC');
								return true
							}
						});
				var secondGrid = this.unusedList.grid;
				var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
				var secondGridDropTarget = new Ext.dd.DropTarget(
						secondGridDropTargetEl, {
							ddGroup : 'secondGridGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								_ctx.changeRecords("remove", records)
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								secondGrid.store.add(records);
								secondGrid.store.sort('CSMC', 'ASC');
								return true
							}
						});
			},
			onBeforeBusChange : function() {
				if (this.isModify) {
					if (confirm('维护数据已修改，是否保存?')) {
						return this.doSave()
					} else {
						return true;
					}
				}
				return true;
			},
			doSave : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configCommBusPermissionService",
							serviceAction : "savePermission",
							body : {
								params : this.usedCollections.keys,
								busType : this.tree.getChecked("key")
							}
						});
				alert(Ext.encode(this.usedCollections.keys))
			}
		});