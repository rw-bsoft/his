$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicStencilForm = function(cfg) {
	cfg.colCount = 2;
	this.serviceId = "clinicStencilNameService";
	this.serviceAction = "clinicStencilNameVerification"
	cfg.title = '新增病历模板';//新框架中需要增加此行才能在Form中显示title
	phis.application.cic.script.ClinicStencilForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onMySave, this);
}
Ext.extend(phis.application.cic.script.ClinicStencilForm, phis.script.TableForm, {
			/*
			 * 重写getSaveRequest 目的是为了把待保存的数据里的SSLB
			 */
			getSaveRequest : function(saveData) {
				var values = saveData;
				if (this.SSLB) {
					values["SSLB"] = this.SSLB;
				} else {
					values["SSLB"] = 1;
				}
				return values;
			},
			onBeforeSave : function(entryName, op, saveData) {
				var data = {
					"MBMC" : saveData.MBMC,
					"YGDM" : saveData.YGDM,
					"JGID" : saveData.JGID,
					"SSLB" : saveData.SSLB
				};
				if (this.initDataId) {
					data["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			},
			onMySave : function(entryName, op, json, saveData){
				this.opener.refresh();
			}
		});