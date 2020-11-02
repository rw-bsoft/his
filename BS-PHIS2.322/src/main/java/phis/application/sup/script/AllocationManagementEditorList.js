$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.AllocationManagementEditorList = function(cfg) {
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	phis.application.sup.script.AllocationManagementEditorList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.AllocationManagementEditorList,
		phis.script.EditorList, {
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
			},
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				this.requestData.serviceId = "phis.materialsOutService";
				this.requestData.serviceAction = "getCK02DBGLInfo";
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
			}
		})