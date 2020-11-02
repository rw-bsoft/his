$package("phis.application.sto.script")

$import("phis.application.sto.script.StorehouseMySimpleDetailForm")

phis.application.sto.script.StorehouseOtherCheckInDetailForm = function(cfg) {
	cfg.disabledField=["RKBZ","LRRQ"];//如果是查看 界面需要灰掉的标签ID
	cfg.showButtonOnTop = false;
	cfg.conditionId="RKFS";
	phis.application.sto.script.StorehouseOtherCheckInDetailForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseOtherCheckInDetailForm,
		phis.application.sto.script.StorehouseMySimpleDetailForm, {
			// 由于框架不支持form回填多表查询,故重写
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
				this.initDataBody["tag"]="qtrk";
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.loadCheckInActionId,
					body : this.initDataBody
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
			}
		})