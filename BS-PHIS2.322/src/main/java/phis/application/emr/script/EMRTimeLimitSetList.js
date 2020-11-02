$package("phis.application.emr.script")

$import("phis.script.EditorList")

phis.application.emr.script.EMRTimeLimitSetList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.listServiceId = "phis.emrManageService";
	cfg.serverParams = {
		serviceAction : "listTimeLimitSet"
	}
	phis.application.emr.script.EMRTimeLimitSetList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRTimeLimitSetList,
		phis.script.EditorList, {
			doSave : function() {
				var data = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.emrManageService",
							serviceAction : "saveTimeLimitSet",
							body : data
						});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					MyMessageTip.msg("提示", "保存成功!", true);
					this.refresh()
				}
			}
		})