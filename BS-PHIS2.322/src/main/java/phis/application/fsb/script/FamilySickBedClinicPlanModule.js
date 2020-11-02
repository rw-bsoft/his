$package("phis.application.fsb.script")
$import("phis.script.SimpleModule")

phis.application.fsb.script.FamilySickBedClinicPlanModule = function(cfg) {
	phis.application.fsb.script.FamilySickBedClinicPlanModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedClinicPlanModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										title : "诊疗计划模版名称",
										layout : "fit",
										border : false,
										region : 'west',
										width : 350,
										items : this.getComboNameList()
									}, {
										title : "诊疗计划模版明细",
										layout : "fit",
										border : false,
										region : 'center',
										items : this.getComboNameDetailList()
									}]
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			getComboNameList : function() {
				this.comboNameList = this.createModule("refPlanNameList",
						this.refPlanNameList);
				this.comboNameList.opener = this;
				var grid = this.comboNameList.initPanel();
				this.comboNameList.on("loadData", this.onListLoadData, this);
				grid.on("rowClick", this.onListRowClick, this);
				return grid;
			},
			onListLoadData : function(store,index) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.comboNameDetailList) {
					this.comboNameDetailList.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.onListRowClick(this.comboNameList.grid, index)
					}
				} else {
					document.getElementById("totcount").innerHTML = "明细条数：0";
					this.comboNameDetailList.ZTBH = "";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				this.beforeclose();
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.comboNameDetailList.ZTBH = r.get("ZTBH");
				this.comboNameDetailList.requestData.cnd = ['eq', ['$', 'a.ZTBH'],
						['l', r.get("ZTBH")]];
				this.comboNameDetailList.loadData();
			},
			getComboNameDetailList : function() {
				this.comboNameDetailList = this.createModule(
						"refPlanDetailsList", this.refPlanDetailsList);
				this.comboNameDetailList.on("loadData", function() {
							if (this.comboNameDetailList.store.getCount() == 0) {
								this.comboNameDetailList.doInsert();
							}
						}, this);
				this.comboNameDetailList.on("afterRemove", this.afterRemove,
						this);
				this.comboNameDetailList.on("afterCellEdit",
						this.afterDetailCellEdit, this);
				var grid = this.comboNameDetailList.initPanel();
				return grid;
			},
			afterRemove : function() {
				if (this.comboNameList) {
					this.comboNameList.loadData();
				}
			},
			afterDetailCellEdit : function(it, record, field, v) {
				if (it.id == "SYPC") {
					field.getStore().each(function(r) {
								if (r.data.key == v) {
									if (record.get("MRCS") != r.data.MRCS) {
										record.set("MRCS", r.data.MRCS);
									}
								}
							}, this);
				}
				if (it.id == 'SYPC' || it.id == 'GYTJ' || it.id == 'YYTS') {
					var store = this.comboNameDetailList.grid.getStore();
					store.each(function(r) {
						if (r.get('YPZH') == record.get('YPZH')) {
							r.set(it.id, v);
							r.set(it.id + '_text', record.get(it.id + '_text'));
							if (it.id == 'SYPC') {
								r.set("MRCS", record.get("MRCS"));
							}
						}
					}, this)
				}
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				if (this.comboNameDetailList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.comboNameDetailList.store
							.getCount(); i++) {
						if (this.comboNameDetailList.store.getAt(i).get("XMBH")) {
							if (confirm('组套明细数据已经修改，是否保存?')) {
								return this.comboNameDetailList.doSave()
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
