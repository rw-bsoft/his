$package("phis.application.hph.script")

$import("phis.script.TableForm")

phis.application.hph.script.HospitalPharmacyAccountingForm = function(cfg) {
	cfg.labelWidth=55
	phis.application.hph.script.HospitalPharmacyAccountingForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.hph.script.HospitalPharmacyAccountingForm,
		phis.script.TableForm, {
			onReady : function() {
				this.focusFieldAfter(0, 500)
				this.form.getForm().findField("CFTS").hide();
				this.form.getForm().findField("BRCH").on("change",this.onBRCHChange,this)
				this.form.getForm().findField("ZYHM").on("change",this.onZYHMChange,this)
				phis.application.hph.script.HospitalPharmacyAccountingForm.superclass.onReady.call(this);
			},
			//床号输入
			onBRCHChange:function(f, v){
				if (!v) {
					MyMessageTip.msg("提示", "请输入有效的床号!", true);
					return;
				}
			this.loadZyxx(v,1)
			},
			onZYHMChange:function(f, v){
				if (!v) {
					MyMessageTip.msg("提示", "请输入有效的住院号码!", true);
					return;
				}
			this.loadZyxx(v,2)
			},
			loadZyxx:function(v,type){
				this.form.el.mask("正在查询...", "x-mask-loading");
			var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.loadActionId,
							body : {
							"HM":v,
							"TYPE":type
							}
						});
				this.form.el.unmask();
				if (r.code > 300) {
					MyMessageTip.msg("提示", r.msg, true);
					//this.processReturnMsg(r.code, r.msg, this.doSave);
					this.doNew();
					return;
				}
			if(r.json.body!=null){
			this.doNew(type);
			this.initFormData(r.json.body)
			this.form.getForm().findField("BRCH").disable() ;
			this.form.getForm().findField("ZYHM").disable();
			this.fireEvent("clear",this);
			}
			},
			doNew:function(type){
			phis.application.hph.script.HospitalPharmacyAccountingForm.superclass.doNew.call(this);
			if(type){
			this.focusFieldAfter(0, 500)
			}
			}
		});