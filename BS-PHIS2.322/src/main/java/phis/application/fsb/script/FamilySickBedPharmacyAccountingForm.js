$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.FamilySickBedPharmacyAccountingForm = function(cfg) {
	cfg.labelWidth=55
	phis.application.fsb.script.FamilySickBedPharmacyAccountingForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedPharmacyAccountingForm,
		phis.script.TableForm, {
			onReady : function() {
				this.focusFieldAfter(0, 500)
				this.form.getForm().findField("CFTS").hide();
				//this.form.getForm().findField("BRCH").on("change",this.onBRCHChange,this)
				this.form.getForm().findField("ZYHM").on("change",this.onZYHMChange,this)
				phis.application.fsb.script.FamilySickBedPharmacyAccountingForm.superclass.onReady.call(this);
			},
			onZYHMChange:function(f, v){
				if (!v) {
					MyMessageTip.msg("提示", "请输入有效的家床号码!", true);
					return;
				}
			this.loadJcxx(v,2)
			},
			loadJcxx:function(v,type){
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
			this.form.getForm().findField("ZYHM").disable();
			this.fireEvent("clear",this);
			}
			},
			doNew:function(type){
			phis.application.fsb.script.FamilySickBedPharmacyAccountingForm.superclass.doNew.call(this);
			if(type){
			this.focusFieldAfter(0, 500)
			}
			}
		});