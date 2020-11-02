$package("phis.application.reg.script")
/**
 * 疾病编码维护from zhangyq 2012.5.25
 */
$import( "phis.script.TableForm")

phis.application.reg.script.RegistrationDepartmentForm = function(cfg) {
	cfg.colCount = 3;
	phis.application.reg.script.RegistrationDepartmentForm.superclass.constructor.apply(
			this, [cfg])
	this.saveServiceId = "registrationDepartmentService";
	this.saveAction = "saveRegistrationDepartment";
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.reg.script.RegistrationDepartmentForm,
		phis.script.TableForm, {
			onBeforeSave : function(entryName, op, saveData) {
                //增加地点信息，地点代码
                if(saveData.DDXX){
                    saveData["DDDM"]=saveData["KSDM"];
                }else{
                    saveData["DDDM"]="";
                }
                
				var data = {
					"KSMC" : saveData.KSMC
				};
				if (this.op == 'update') {
					saveData["KSDM"] = this.data["KSDM"]
					data["KSDM"] = this.data["KSDM"];
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registrationDepartmentService",
							serviceAction : "registrationDepartmentVerification",
							schemaDetailsList : "MS_GHKS",
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
				return true;
			}
		})