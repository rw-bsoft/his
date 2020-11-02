$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.MedicalExpressionForm = function(cfg) {
	cfg.colCount = 1;
	phis.application.cfg.script.MedicalExpressionForm.superclass.constructor.apply(this,
			[cfg]);
//	this.loadServiceId = "medicalExpMaintainService";
//	this.loadServiceAction = "loadMedicalExpData";
	this.saveServiceId = "medicalExpMaintainService";
	this.saveAction = "saveMedicalExpData";
	this.on("beforeSave",this.onBeforeSave,this);
}

Ext.extend(phis.application.cfg.script.MedicalExpressionForm, phis.script.TableForm,
		{
			onBeforeSave:function(entryName, op, saveRequest){
				if(this.op=="update"){
					return true;
				}
				var flag = true;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalExpMaintainService",
							serviceAction : "checkHasMedicalExp",
							method : "execute",
							BDSMC : saveRequest.BDSMC,
							schema : entryName
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.body!=null) {
					flag = r.json.body;
				}
				if(!flag){
					MyMessageTip.msg("提示", "表达式名称["+saveRequest.BDSMC+"]已存在，请重新输入!", true);
				}
				return flag;
			}

		});