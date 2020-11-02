$package("phis.application.war.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

phis.application.war.script.WardCommonCharacterList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	cfg.disableContextMenu = true;
	cfg.group = "ZTLB";
	phis.application.war.script.WardCommonCharacterList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.WardCommonCharacterList,
		phis.script.SimpleList, {
			ZtlbRender : function(value, params, r, row, col, store) {
				switch (value) {
					case "1" :
						return "西药";
					case "2" :
						return "中药";
					case "3" :
						return "草药";
					case "5" :
						return "文字医嘱";
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
