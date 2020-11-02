$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseMedicinesAliasEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.entryName="phis.application.sto.schemas.YK_YPBM_YKYP";
	phis.application.sto.script.StorehouseMedicinesAliasEditorList.superclass.constructor.apply(this,
			[cfg])
			this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesAliasEditorList,
		phis.script.EditorList, {
			onBeforeCellEdit:function(){
			if(this.isRead){
			return false;}
			return true;
			}
		});
