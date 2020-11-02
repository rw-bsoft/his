/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.reg.script")

$import("phis.script.SimpleList")

phis.application.reg.script.RegistrationDepartmentConfigList = function(cfg) {
		cfg.selectFirst = false
		phis.application.reg.script.RegistrationDepartmentConfigList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.reg.script.RegistrationDepartmentConfigList,
		phis.script.SimpleList, {
			processRemove : function(r) {
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("在正删除数据...");
				var compositeKey;
				if (this.isCompositeKey) {
					compositeKey = {};
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						compositeKey[this.schema.pkeys[i]] = r
								.get(this.schema.pkeys[i]);
					}
				}
				phis.script.rmi.jsonRequest({
					serviceId : "registrationDepartmentService",
					serviceAction : "removeRegistrationDepartment",
					pkey : r.id,
					body : compositeKey,
					schema : this.entryName,
					action : "remove", // 按钮标识
					module : this.grid._mId
						// 增加module的id
					}, function(code, msg, json) {
					this.unmask()
					if (code < 300) {
						this.store.remove(r)
						this.fireEvent("remove", this.entryName, 'remove',
								json, r.data)
					} else {
						this.processReturnMsg(code, msg, this.doRemove)
					}
				}, this)
			}
		});