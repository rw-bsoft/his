$package("phis.application.spt.script");

$import("phis.script.SimpleList");

phis.application.spt.script.SptMedicinesCompareList = function(cfg) {
	this.exContext = {};
	phis.application.spt.script.SptMedicinesCompareList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.spt.script.SptMedicinesCompareList,
		phis.script.SimpleList, {
			doCancelmatch:function (){
				var r=this.getSelectedRecord();
				var body={};
				if (!r) {
					return;
				}
				body.YPXH=r.get("YPXH");
				body.ZBBM=r.get("ZBBM");
				body.YPCD=r.get("YPCD");
				body.JGID=r.get("JGID");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.SptService",
							serviceAction : "updatenozbbm",
							body:body
						});
						debugger;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
			}
		});
