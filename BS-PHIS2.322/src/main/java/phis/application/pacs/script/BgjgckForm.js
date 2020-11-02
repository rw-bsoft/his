$package("phis.application.pacs.script")

$import("phis.script.TableForm")

phis.application.pacs.script.BgjgckForm= function(cfg) {
	cfg.modal = true;
	cfg.autoLoadData=false;
	phis.application.pacs.script.BgjgckForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pacs.script.BgjgckForm,
		phis.script.TableForm, {
		loadData : function(sqid) {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				this.doNew();
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryDataActionId,
							SQID:sqid
						});
				if (this.form && this.form.el) {
					this.form.el.unmask()
				}
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doPreserve);
				}
				this.initFormData(ret.json.body);
			}
		});
