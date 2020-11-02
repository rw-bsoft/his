$package("phis.application.cic.script")

$import( "phis.script.TableForm")

phis.application.cic.script.ClinicProjectComboUseForm = function(cfg) {
	this.serviceId="clinicProjectComboUseService";
	this.serviceAction="clinicProjectComboUseVerification";
	phis.application.cic.script.ClinicProjectComboUseForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cic.script.ClinicProjectComboUseForm, phis.script.TableForm, {
	/*
	 * 重写getSaveRequest 目的是为了把待保存的数据里的SSLB和SSDM赋值
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
	onReady:function(){
		phis.application.cic.script.ClinicProjectComboUseForm.superclass.onReady
						.call(this)
		var f = this.form.getForm().findField("ZDMC_Form");
		f.on("select",this.onSelect,this);
	},
	onSelect : function(params,record){
		this.form.getForm().findField("ICD10").setValue(record.get("ICD10"));
		this.form.getForm().findField("PYDM").setValue(record.get("PYDM"));
		var exContext={};
		exContext["empiData"]={};
		this.exContext=exContext;
		this.exContext.empiData["ZDXH"]=record.get("key");
		this.exContext.empiData["ZDMC"]=record.get("text");
	},
	onBeforeSave:function(entryName, op, saveData){
				var data = {"ZDMC":saveData.ZDMC,"YGDM":saveData.YGDM,"JGID":saveData.JGID,"SSLB":saveData.SSLB};
					if(this.initDataId){
						data["JLBH"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId :this.serviceId,
							serviceAction : this.serviceAction,
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
});