$package("phis.application.mds.script")
$import("phis.script.SimpleList")
phis.application.mds.script.MedicinesDispensingModeList = function(cfg) {
	phis.application.mds.script.MedicinesDispensingModeList.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.mds.script.MedicinesDispensingModeList,
		phis.script.SimpleList, {
			//删除前验证下发药方式是否被使用
			onBeforeRemove : function(entryName, r) {
				var body = {"keyName":"FYFS","keyValue": r.data.FYFS,"tableName":"YK_TYPK"};
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.verifiedUsingServiceId,
							serviceAction : this.verifiedUsingActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "发药方式[" + r.data.FSMC + "]已经被使用,不能被删除";
					this.processReturnMsg(ret.code, msg, this.onBeforeRemove);
					return false;
				}
				return true;
			}
	});