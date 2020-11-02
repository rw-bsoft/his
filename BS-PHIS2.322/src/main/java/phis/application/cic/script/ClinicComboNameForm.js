$package("phis.application.cic.script")

$import( "phis.script.TableForm")

phis.application.cic.script.ClinicComboNameForm = function(cfg) {
	this.serviceId="clinicComboNameService";
	this.serviceAction="clinicComboNameVerification"
	//cfg.title = '处方组套-新增';//新框架中需要增加此行才能在Form中显示title
	phis.application.cic.script.ClinicComboNameForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.cic.script.ClinicComboNameForm, phis.script.TableForm, {
			onReady : function() {
				phis.application.cic.script.ClinicComboNameForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var f = form.findField("ZTLB");
				if(f){
					f.store.on("load", this.fillRkfs, this);
				}
			},
			doNew : function() {
				phis.application.cic.script.ClinicComboNameForm.superclass.doNew
						.call(this);
				this.fillRkfs();
			},
			// 新增页面打开药品类别值带过来
			fillRkfs : function() {
				/*if(!this.ZTLB)
				return;
				if (this.ZTLB && this.op == "create") {
					var form = this.form.getForm();
					if(form){
						form.findField("ZTLB").setValue(this.ZTLB);
					}
				}*/
			},
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
		if(this.ZTLB){
			values["ZTLB"] = this.ZTLB;
		}else{
			values["ZTLB"] = 1;
		}
		return values;   
	},
	onBeforeSave:function(entryName, op, saveData){
				var data = {"ZTMC":saveData.ZTMC,"YGDM":saveData.YGDM,"JGID":saveData.JGID,"SSLB":saveData.SSLB,"ZTLB":saveData.ZTLB};
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
			}
});