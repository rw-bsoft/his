$package("phis.application.cfg.script")

$import("phis.script.SimpleModule")

phis.application.cfg.script.ConfigPharmacyPermissionsModule = function(cfg) {
	phis.application.cfg.script.ConfigPharmacyPermissionsModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigPharmacyPermissionsModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.lastBusCode = "1";
				this.lastBusType = "药房业务";
				this.ksdm = "";
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										title : "全部员工(双击拖动选择)",
										split : true,
										region : 'west',
										width : '50%',
										items : this.getRList()
									}, {
										layout : "fit",
										border : false,
										title : "拥有权限的员工",
										split : true,
										region : 'center',
										items : this.getLList()
									}],
							tbar : this.getTbar()
						});
					
				this.panel = panel;
				this.panel.on("beforeclose",this.onBeforeBusSelect,this);
				return panel;
			},
			getTbar : function() {
				var btn = {
					id : "save",
					text : "保存",
					iconCls : "save",
					handler : this.doSave,
					scope : this
				};
				var labelText = new Ext.form.Label({
							text : "选择业务"
						});
				var dic = {
					id : "phis.dictionary.busPermisstionType",
					defaultValue : 1,
					autoLoad : true
				}
				var combo = util.dictionary.SimpleDicFactory.createDic(dic);
				combo.setEditable(false);
				combo.on("select", this.onBusSelect, this);
				combo.on("beforeselect", this.onBeforeBusSelect, this);
				this.combo = combo;
				return [btn, '->', labelText, '-', combo];
			},
			getLList : function() {
				var module = this.createModule("cfgppLlist", this.refLList);
				this.llist = module;
				// 双击添加操作
				var _ctx = this;
				module.opener = this;
				module.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected()
					Ext.each(records, grid.store.remove, grid.store);
					_ctx.rlist.grid.store.add(records);
					_ctx.rlist.grid.store.sort('PERSONID', 'ASC');
				}
				module.on("loadData", this.pharmarcyChange, this);
				return module.initPanel();
			},
			getRList : function() {
				var module = this.createModule("cfgppRlist", this.refRList);
				module.on("loadData", this.rlistLoadData, this);
				var _ctx = this;
				module.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected();
					Ext.each(records, grid.store.remove, grid.store);
					_ctx.llist.grid.store.add(records);
					_ctx.llist.grid.store.sort('PERSONID', 'ASC');
				}
				this.rlist = module;
				return module.initPanel();
			}, 
			rlistLoadData : function(store) {
				store.filterBy(function(f_record, id) {
					var count = this.llist.grid.store.getCount();
					for (var i = 0; i < count; i++) {
						if (f_record.data.PERSONID == this.llist.store.getAt(i).data.PERSONID)
							return false;
					}
					return true;
				}, this);
				this.isModify = false;
			},
			pharmarcyChange : function(pharmarcyId) {
				// this.rlist.requestData.cnd =
				// ['eq',['$','JGID'],['s',this.mainApp['phisApp'].deptId]];
				// this.rlist.requestData.pageSize = 0;
				this.rlist.refresh();
			},
			onBeforeBusSelect : function() {
				if (this.isModify) {
					if (confirm('业务权限维护模块数据已修改，是否保存?')) {
						return this.doSave()
					} else {
						return true;
					}
				}
				return true;
			},
			onBusSelect : function(p, r, index) {
				// 修改llist的tbar显示内容
				if (r.data.text != this.lastBusType) {
					var tbar = this.llist.grid.getTopToolbar();
					tbar.removeAll();
					tbar.add(this.llist.getTopCombo(r.data));
					tbar.doLayout();
					// 设置llist的permissionType = r.data.key
					this.llist.permissionType = r.data.key;
					this.lastBusType = r.data.text;
					this.lastBusCode = r.data.key;
					this.llist.grid.getStore().removeAll();
					this.rlist.grid.getStore().removeAll();
				}
			},
			doSave : function() {
				this.panel.el.mask();
				// 提交后台保存
				var ksdm = this.llist.partmentCombo.getValue();
				if (ksdm == null || ksdm == "") {
					MyMessageTip.msg("提示", "未找到有效【" + this.lastBusType + "】!",
							true);
					this.panel.el.unmask();
					return false;
				}

				var body = [];
				var l_store = this.llist.grid.getStore();
				for (var i = 0; i < l_store.getCount(); i++) {
					var r = l_store.getAt(i)
					body.push(r.data)
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configCommBusPermissionService",
							serviceAction : "savePermission",
							body : body,
							busType : this.lastBusCode,
							ksdm : ksdm
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				this.panel.el.unmask();
				if (code < 300) {
					MyMessageTip.msg("提示", "【" + this.lastBusType + "】权限设置成功!",
							true);
					this.isModify = false;
					return true;
				} else {
					this.processReturnMsg(code, msg)
					return false;
				}
			},
			afterOpen : function() {
				// 拖动操作
				var _ctx = this;
				var firstGrid = this.rlist.grid;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								firstGrid.store.add(records);
								firstGrid.store.sort('PERSONID', 'ASC');
								return true
							}
						});
				var secondGrid = this.llist.grid;
				var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
				var secondGridDropTarget = new Ext.dd.DropTarget(
						secondGridDropTargetEl, {
							ddGroup : 'secondGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								secondGrid.store.add(records);
								secondGrid.store.sort('PERSONID', 'ASC');
								return true
							}
						});
				// 设置默认值
				this.combo.setRawValue(this.lastBusType);
			}

		});