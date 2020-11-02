$package("phis.application.cic.script")

$import( "phis.script.TableForm")

phis.application.cic.script.ClinicProjectSetForm = function(cfg) {
	this.serviceId="clinicComboNameService";
	this.serviceAction="clinicComboNameVerification"
	//cfg.title = '项目组套-新增';
	phis.application.cic.script.ClinicProjectSetForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onMySave, this);
}
Ext.extend(phis.application.cic.script.ClinicProjectSetForm, phis.script.TableForm, {
	/*
	 * 重写getSaveRequest 目的是为了把待保存的数据里的SSLB
	 */
    getSaveRequest : function(saveData) {
		var values = saveData;
		if(this.SSLB){
			values["SSLB"] = this.SSLB;
		}else{
			values["SSLB"]=1;
		}
		return values;   
	},
	onBeforeSave:function(entryName, op, saveData){
		var data = {"ZTMC":saveData.ZTMC,"YGDM":this.mainApp.uid,"JGID":saveData.JGID,"SSLB":saveData.SSLB,"ZTLB":saveData.ZTLB};
			if(this.initDataId){
				data["ZTBH"]=this.initDataId;
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
		
	}
});