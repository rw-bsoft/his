$package("phis.application.cfg.script.gy")
/**
 * 疾病编码维护from
 * zhangyq 2012.5.25
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDiseaseCodingForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width= 800;
	cfg.modal = true;
	phis.application.cfg.script.ConfigDiseaseCodingForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigDiseaseCodingForm, phis.script.TableForm, {
		onReady : function() {
				phis.application.cfg.script.ConfigDiseaseCodingForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var ICD10 = form.findField("ICD10");
				if (ICD10) {
					ICD10.on("change",
							this.onICD10NumberChange, this);
				}
			},
			onICD10NumberChange : function(f) {
				f.setValue(f.getValue().toUpperCase());		     	
			},
			onBeforeSave:function(entryName, op, saveData){
				var data = {"ICD10":saveData.ICD10,"JBMC":saveData.JBMC};
					if(this.initDataId){
						data["JBXH"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configDiseaseNumberService",
							serviceAction : "iCD10NumberAndJBMCVerification",
							schemaDetailsList : "GY_JBBM",
							method : "execute",
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