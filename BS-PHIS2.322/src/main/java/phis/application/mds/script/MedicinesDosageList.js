$package("phis.application.mds.script")
$import("phis.script.SimpleList")
phis.application.mds.script.MedicinesDosageList = function(cfg) {
	phis.application.mds.script.MedicinesDosageList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.mds.script.MedicinesDosageList,
		phis.script.SimpleList, {
			onBeforeRemove : function(entryName, r) {
				var body = {
					"keyName" : "YPSX",
					"keyValue" : parseFloat(r.data.YPSX),
					"tableName" : "YK_TYPK"
				};
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.verifiedUsingServiceId,
							serviceAction : this.verifiedUsingActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "剂型[" + r.data.SXMC + "]已经被使用,不能被删除";
					this.processReturnMsg(ret.code, msg, this.onBeforeRemove);
					return false;
				}
				return true;
			}
		});