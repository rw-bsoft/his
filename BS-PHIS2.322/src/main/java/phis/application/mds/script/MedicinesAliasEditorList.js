$package("phis.application.mds.script")

$import("phis.script.EditorList")

phis.application.mds.script.MedicinesAliasEditorList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.mds.script.MedicinesAliasEditorList.superclass.constructor.apply(this,[cfg])
			this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.mds.script.MedicinesAliasEditorList,
		phis.script.EditorList, {
			onBeforeCellEdit:function(){
			if(this.isRead){
			return false;}
			return true;
			},
			doRead:function(){//update by caijy at 2015.1.19 for 医生站合理用药信息调阅,界面只读
			var btns = this.grid.getTopToolbar();
			 btns.find("cmd", "create")[0].disable()
			  btns.find("cmd", "remove")[0].disable()
			this.loadData();
			}
		});
