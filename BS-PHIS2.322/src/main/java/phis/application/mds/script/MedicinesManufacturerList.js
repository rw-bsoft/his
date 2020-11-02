$package("phis.application.mds.script")

$import("phis.script.SimpleList")

phis.application.mds.script.MedicinesManufacturerList = function(cfg) {
	phis.application.mds.script.MedicinesManufacturerList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.mds.script.MedicinesManufacturerList,
		phis.script.SimpleList, {
			// 用于检验数据是否能被修改和删除,重写doAction
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref
				if (cmd == "update" || cmd == "remove") {
					var r = this.getSelectedRecord();
					if (r == null) {
						return
					}
					
					var body = {"keyName":"YPCD","keyValue":r.data.YPCD,"tableName":"YK_YPCD"};
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.verifiedUsingServiceId,
								serviceAction : this.verifiedUsingActionId,
								body : body
							});
					if (ret.code > 300) {
						var msg = "产地[" + r.data.CDMC + "](全称:" + r.data.CDQC
								+ ")已经被使用,不能被修改和删除";
						this.processReturnMsg(ret.code, msg, this.doAction);
						return;
					}

				}
				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			}
		});