$package("phis.application.cfg.script")

/**
 * 病人性质维护的药品限制界面List zhangyq 2012.5.25
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigPaymentCategoryList = function(cfg) {
	phis.application.cfg.script.ConfigPaymentCategoryList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
	this.on("remove", this.onRemove, this);

}

Ext.extend(phis.application.cfg.script.ConfigPaymentCategoryList,
		phis.script.SimpleList, {
			onBeforeRemove : function(entryName, r) {
				var data = {
					"FKLB" : r.json.FKLB
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configPaymentCategoryService",
							serviceAction : "paymentCategoryDelVerification",
							method : "execute",
							schemaDetailsList : "GY_FKFS",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			},
			onSave : function(entryName, op, json, rec) {
				this.fireEvent("save", entryName, op, json, rec);
				this.refresh();
				this.resetFkfsDic();
			},
			onRemove : function(entryName, op, json, data) {
				this.resetFkfsDic();
			},
			resetFkfsDic : function() {
				var r = phis.script.rmi.jsonRequest({
							serviceId : "configPaymentCategoryService",
							serviceAction : "resetFkfsDic",
							method : "execute"
						});
			}
		})