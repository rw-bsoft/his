$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.InventoryEditorList = function(cfg) {
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	phis.application.sup.script.InventoryEditorList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.InventoryEditorList, phis.script.EditorList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				if (this.op) {
					if (this.op == "create") {
						this.requestData.serviceId = "phis.inventoryService";
						this.requestData.serviceAction = "getWzkcInfo";
					} else {
						if (this.requestData.cnd) {
							this.requestData.serviceId = "phis.inventoryService";
							this.requestData.serviceAction = "getPD02Info";
						}
					}
				}
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
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			doInit : function() {
				this.editRecords = [];
			}
		})