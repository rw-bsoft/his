$package("phis.application.ivc.script")
$import("phis.script.SimpleModule")

phis.application.ivc.script.ReportSettingModule = function(cfg) {
	phis.application.ivc.script.ReportSettingModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ReportSettingModule, phis.script.SimpleModule,
		{
			initPanel : function() {
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
										region : 'west',
										height : 200,
										width : 470,
										items : this.getComboNameList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										height : 200,
										width : 600,
										items : this.getComboNameDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getComboNameList : function() {
				this.comboNameList = this.createModule("getComboNameList",
						this.refComboNameList);
				this.comboNameList.opener = this;
				this.comboNameGrid = this.comboNameList.initPanel();
				this.comboNameList.on("beforeclose", this.beforeclose, this);
				this.comboNameList.on("loadData", this.onListLoadData, this);
				this.comboNameGrid.on("rowClick", this.onListRowClick, this);
				return this.comboNameGrid;
			},
			getComboNameDetailList : function() {
				this.comboNameDetailList = this.createModule(
						"getComboNameDetailList", this.refComboNameDetailList);
//				this.comboNameDetailList.on("afterRemove", this.afterRemove,
//						this);
//				this.comboNameDetailList.on("afterCellEdit",
//						this.afterDetailCellEdit, this);
				this.comboNameDetailGrid = this.comboNameDetailList.initPanel();
				return this.comboNameDetailGrid;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.comboNameDetailList) {
					this.comboNameDetailList.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.comboNameGrid.fireEvent("rowclick",
								this.comboNameGrid, 0);
					}
				} else {
					this.comboNameDetailList.BBBH = "";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				this.beforeclose();
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r) {
					return;
				}
				this.comboNameDetailList.loadData(r.id);
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
                var type="beforeclose";
				if (this.comboNameDetailList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.comboNameDetailList.store
							.getCount(); i++) {
						if (this.comboNameDetailList.store.getAt(i).get("BBBH")) {
							if (confirm('报表归并已经修改，是否保存?')) {
								return this.comboNameDetailList.doSave(type)
							} else {
								break;
							}
						}
					}
					this.comboNameDetailList.store.rejectChanges();
				}
				return true;
			}
		})
