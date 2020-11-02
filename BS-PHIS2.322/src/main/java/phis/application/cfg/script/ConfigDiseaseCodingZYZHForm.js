$package("phis.application.cfg.script")
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDiseaseCodingZYZHForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width= 600;
	cfg.modal = true;
	phis.application.cfg.script.ConfigDiseaseCodingZYZHForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigDiseaseCodingZYZHForm, phis.script.TableForm, {
			onBeforeSave:function(entryName, op, saveData){
				var data = {"ZHDM":saveData.ZHDM,"ZHMC":saveData.ZHMC};
					if(this.initDataId){
						data["ZHBS"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configDiseaseNumberService",
							serviceAction : "zYZHVerification",
							schemaDetailsList : "EMR_ZYZH",
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