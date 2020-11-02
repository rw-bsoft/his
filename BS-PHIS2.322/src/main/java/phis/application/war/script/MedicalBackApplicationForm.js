$package("phis.application.war.script")

$import("phis.script.TableForm")

phis.application.war.script.MedicalBackApplicationForm = function(cfg) {
	phis.application.war.script.MedicalBackApplicationForm.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.war.script.MedicalBackApplicationForm,
		phis.script.TableForm, {
			loadData : function(zyh) {
				if(zyh==null||zyh==""||zyh==undefined){return;}
				var body = {};
				body["ZYH"] = zyh;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.loadData);
					return;
				}
				this.doNew();
				//无用代码 开发时垃圾数据太多先加上
				if(ret.json.body==null){
				return ;}
				this.initFormData(ret.json.body);
			}
		});