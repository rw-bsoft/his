$package("phis.application.cfg.script")
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDiseaseCodingZYJBForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width= 600;
	cfg.modal = true;
	phis.application.cfg.script.ConfigDiseaseCodingZYJBForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigDiseaseCodingZYJBForm, phis.script.TableForm, {
			onBeforeSave:function(entryName, op, saveData){
				var data = {"JBDM":saveData.JBDM,"JBMC":saveData.JBMC};
				if(this.initDataId){
					data["JBBS"]=this.initDataId;
				}
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "configDiseaseNumberService",
						serviceAction : "zYJBVerification",
						schemaDetailsList : "EMR_ZYJB",
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