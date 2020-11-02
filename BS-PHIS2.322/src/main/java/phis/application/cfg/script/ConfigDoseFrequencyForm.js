$package("phis.application.cfg.script")

/**
 * 给药频次维护from zhangyq 2012.5.25
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDoseFrequencyForm = function(cfg) {
	cfg.colCount = 2;
	cfg.width= 500;
	phis.application.cfg.script.ConfigDoseFrequencyForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cfg.script.ConfigDoseFrequencyForm, phis.script.TableForm, {
	onBeforeSave:function(entryName, op, saveData){
		        var MRCS  = saveData.MRCS;
		        var ZXSJ = saveData.ZXSJ;
		        var ZXZQ = saveData.ZXZQ;
		        var RZXZQ = saveData.RZXZQ;
				var data = {"PCMC":saveData.PCMC,"MRCS":saveData.MRCS,"ZXSJ":saveData.ZXSJ,"ZXZQ":saveData.ZXZQ,"RZXZQ":saveData.RZXZQ};
					if(this.initDataId){
						data["PCBM"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configDoseFrequencyService",
							serviceAction : "DoseFrequencyVerification",
							schemaDetailsList : this.entryName,
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
			},
 getSaveRequest : function(saveData) {
		var values = saveData; 
		if(saveData.PCMC){
			values["PCMC"] = saveData.PCMC.toUpperCase();
		}
		return values;   
	}
})