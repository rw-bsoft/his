/**
 * 药品公用信息-用药限制
 * 
 * @author caijy
 */
$package("phis.application.mds.script");
$import("phis.script.EditorList");

phis.application.mds.script.MedicinesLimitEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.mds.script.MedicinesLimitEditorList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.mds.script.MedicinesLimitEditorList,
		phis.script.EditorList, {
			afterCellEdit : function(e) {
				var f = e.field
				var v = e.value
				var old = e.originalValue;// 编辑前的值
				var record = e.record
				var reg = /^[0-9]{1,3}\.{0,1}[0-9]{0,1}$/;
				if (!reg.test(v) || !(0 <= parseFloat(v) <= 100)) {
					record.set("ZFBL", old);
				}
			},
			onBeforeCellEdit : function() {
				if (this.isRead) {
					return false;
				}
				return true;
			}

		})
