$package("phis.application.sup.script");

$import("phis.script.SimpleList");

phis.application.sup.script.MeteringEquipmentTestLeftList = function(cfg) {
	// cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sup.script.MeteringEquipmentTestLeftList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sup.script.MeteringEquipmentTestLeftList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear(); 
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.queryAction;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			// 单击记录
			onRowClick : function() {
				var record = this.getSelectedRecord();
				if (record == null) {
					return;
				}
				this.fireEvent("rowClick", record.data.JLXH);
			},
			// 刚打开页面时候默认选中第一条数据
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick(this.grid);
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick(this.grid);
				}
			}
		})