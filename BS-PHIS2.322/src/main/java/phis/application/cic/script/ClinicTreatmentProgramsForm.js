$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicTreatmentProgramsForm = function(cfg) {
	cfg.colCount = 1;
	this.serviceId="clinicTreatmentProgramsNameService";
	this.serviceAction="clinicTreatmentProgramsNameVerification"
	phis.application.cic.script.ClinicTreatmentProgramsForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onMySave, this);
}

Ext.extend(phis.application.cic.script.ClinicTreatmentProgramsForm,
		phis.script.TableForm, {
	getSaveRequest : function(saveData) {
		var values = saveData;
		if(this.ZLBH){
			values["ZLBH"] =this.ZLBH;
		}
		return values;   
	},
	onBeforeSave:function(entryName, op, saveData){
		var data = {"ZLMC":saveData.ZLMC,"YGDM":saveData.YGDM,"JGID":saveData.JGID,"SSLB":saveData.SSLB};
			if(this.initDataId){
				data["ZLXH"]=this.initDataId;
			}
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction :this.serviceAction,
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
	onMySave : function(entryName, op, json, saveData){
//		MyMessageTip.msg("提示", '保存成功!', true);
	}
});