$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.MaintenanceServiceForm = function(cfg) {
	cfg.width = 900;
	cfg.height = 700;
	cfg.colCount = 4;
	cfg.autoLoadData = false;
	phis.application.sup.script.MaintenanceServiceForm.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.application.sup.script.MaintenanceServiceForm, phis.script.TableForm, {
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
			if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId,
					this.initDataBody)) {
				return
			}
			if (this.form && this.form.el) {
				this.form.el.mask("正在载入数据...", "x-mask-loading")
			}
			this.loading = true
			phis.script.rmi.jsonRequest({
				serviceId : this.loadServiceId,
				schema : "phis.application.sup.schemas.WL_WXBG",
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
					this.oper.alertmsg = true;
					this.doNew();
					if(json.body.WZXH){
						json.body.SBWX=1;
						this.oper.SBWX=1
					}else{
						json.body.SBWX=0;
						this.oper.SBWX=0
					}
					this.initFormData(json.body);
					this.oper.alertmsg = false;
					this.fireEvent("loadData", this.entryName, json.body);
				}
				if (this.op == 'create') {
					this.op = "update";
				}
			}, this)// jsonRequest
		}
		})