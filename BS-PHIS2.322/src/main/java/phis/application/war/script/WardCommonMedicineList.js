$package("phis.application.war.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

phis.application.war.script.WardCommonMedicineList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	cfg.disableContextMenu = true;
	cfg.group = "ZTLB";
	phis.application.war.script.WardCommonMedicineList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.WardCommonMedicineList,
		phis.script.SimpleList, {
			ZtlbRender : function(value, params, r, row, col, store) {
				switch (value) {
					case "1" :
						return "西药";
					case "2" :
						return "中药";
					case "3" :
						return "草药";
				}
			},
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			}
		})
