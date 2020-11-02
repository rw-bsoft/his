$package("phis.application.sup.script");
$import("phis.script.SimpleModule");

phis.application.sup.script.ApplyManagementImportModule = function(cfg) {
	cfg.width = 1024;
	phis.application.sup.script.ApplyManagementImportModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.ApplyManagementImportModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
                    this.comboNameList.ksdm = this.ksdm;
                    this.comboNameDetailList.ksdm = this.ksdm;
                    this.comboNameList.zblb = this.zblb;
                    this.comboNameDetailList.zblb = this.zblb;
                    this.comboNameList.refresh();
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
										region : 'west',
										width : 200,
										items : this.getComboNameList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										width : 720,
										items : this.getComboNameDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getComboNameList : function() {
				this.comboNameList = this.createModule("comboNameList",
						this.refComboNameList);
				// this.comboNameList.opener = this;
				this.comboNameList.ksdm = this.ksdm;
				this.comboNameList.zblb = this.zblb;
				this.comboNameGrid = this.comboNameList.initPanel();
				// this.comboNameList.on("beforeclose", this.beforeclose, this);
				this.comboNameList.on("loadData", this.onListLoadData, this);
				this.comboNameGrid.on("rowClick", this.onListRowClick, this);
				return this.comboNameGrid;
			},
			getComboNameDetailList : function() {
				this.comboNameDetailList = this.createModule("comboNameDetailList", this.refComboNameDetailList);
				this.comboNameDetailList.on("afterReject", this.afterReject,this);
				this.comboNameDetailList._ctr = this.comboNameList;
				this.comboNameDetailList.ksdm = this.ksdm;
				this.comboNameDetailList.zblb = this.zblb;
				this.comboNameDetailGrid = this.comboNameDetailList.initPanel();
				return this.comboNameDetailGrid;
			},
			onDetailListLoadData : function() {
				alert(11);
				 var l = this.comboNameDetailList.store.getCount();
				 alert(l);
				 if (l == 0) {
					 var r = this.comboNameList.getSelectedRecord();
					 this.comboNameList.store.remove(r);
					 this.comboNameList.grid.getView().refresh();// 刷新行号
					 if (this.comboNameList.store.getCount() > 0) {
						 this.comboNameList.selectRow(0);
						 this.onListRowClick(this.comboNameList, 0);
					 }
				 }
			 },
			afterReject : function() {
				 this.comboNameList.refresh();
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.comboNameDetailList) {
					this.comboNameDetailList.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.comboNameGrid.fireEvent("rowclick",this.comboNameGrid, 0);
					}
				} else {
					this.comboNameDetailList.clear();
					document.getElementById("totcount").innerHTML = "明细条数：0";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				// this.beforeclose();
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r) {
					return;
				}
				this.comboNameDetailList.loadData(r.data.SLKS, r.data.SLGH);
			}
		})
