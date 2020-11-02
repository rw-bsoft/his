$package("phis.application.pcm.script")

$import("phis.script.TableForm")

phis.application.pcm.script.PrescriptionCommentsWTWHForm = function(cfg) {
	cfg.colCount = 2
	cfg.name="处方点评问题维护";
	phis.application.pcm.script.PrescriptionCommentsWTWHForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsWTWHForm,
		phis.script.TableForm, {
			onBeforeSave : function(entryName, op, values) {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.beforeSaveServiceId,
							serviceAction : this.serviceAction,
							body : {
								"WTXH" : values.WTXH,
								"WTDM" : values.WTDM,
								"op" : op
							}
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return false;
				}
				return true;
			},
			doClose:function(){
			this.getWin().hide();
			}
		})