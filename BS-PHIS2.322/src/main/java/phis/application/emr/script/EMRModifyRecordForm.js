$package("phis.application.emr.script")

$import("phis.script.TableForm")
phis.application.emr.script.EMRModifyRecordForm = function(cfg) {
	cfg.colCount = 1;
	cfg.showButtonOnTop = false;
	cfg.loadServiceId = "emrManageService";
	cfg.serviceAction = "loadEmrHjnr"
	phis.application.emr.script.EMRModifyRecordForm.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.emr.script.EMRModifyRecordForm, phis.script.TableForm, {
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
					serviceId : this.loadServiceId,
					serviceAction : this.serviceAction,
					schema : this.entryName,
					pkey : this.initDataId,
					body : this.initDataBody,
					action : this.op, // 按钮事件
					module : this._mId
						// 增加module的id
					}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					if (json.body) {
						this.doNew()
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
			},
			loadHjnr : function() {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.loadServiceId,
							serviceAction : this.serviceAction,
							schema : this.entryName,
							pkey : this.initDataId,
							body : this.initDataBody,
							action : this.op, // 按钮事件
							module : this._mId
						});
				return result.json.body.HJNR;
			}
		});