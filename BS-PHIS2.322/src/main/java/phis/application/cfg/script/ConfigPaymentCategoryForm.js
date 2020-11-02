$package("phis.application.cfg.script")
/**
 * 疾病编码维护from
 * zhangyq 2012.5.25
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigPaymentCategoryForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width= 500;
	phis.application.cfg.script.ConfigPaymentCategoryForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigPaymentCategoryForm, phis.script.TableForm, {
			onBeforeSave:function(entryName, op, saveData){
				var data = {"LBMC":saveData.LBMC};
					if(this.initDataId){
						data["FKLB"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configPaymentCategoryService",
							serviceAction : "paymentCategoryVerification",
							method : "execute",
							schemaDetailsList : "GY_FKLB",
							op:this.op,
							body : data
						});
				    		if (r.code==612) {
					 			MyMessageTip.msg("提示", r.msg, true);
					 			return false;
				     		}
				     		if (r.code==613) {
					 			MyMessageTip.msg("提示", r.msg, true);
					 			return false;
				     		}
			}
})