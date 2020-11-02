$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyWindowInfomationForm = function(cfg) {
	cfg.serviceId="pharmacyManageService";
	cfg.repeatInspectionActionId="windowNumberRepeatInspection";//验证窗口编号方法
	cfg.updateActionId="updateWindowInformation";//更新方法
	phis.application.pha.script.PharmacyWindowInfomationForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.onLoadData, this);
	this.on("save",this.onSave,this);
}
Ext.extend(phis.application.pha.script.PharmacyWindowInfomationForm, phis.script.TableForm, {
			// 由于窗口编号是手动修改和增加主键的,故保存前特殊处理
			onBeforeSave : function(entryName, op, saveRequest) {
				if (this.op == "create") {
					// 检验下编号是否重复
					var body={};
					body["ckbh"]=saveRequest["CKBH"];
					body["yfsb"]=saveRequest["YFSB"];
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.repeatInspectionActionId,
								body:body
							});
							if(r.code> 300){
								this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
								return false;
							}else{
							return true;
							}
				} else {
					if (saveRequest["CKBH"] == this.oldCkbh) {
						return true;
					}
					saveRequest["oldCkbh"]=this.oldCkbh;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.updateActionId,
								body:saveRequest
							});
							if(r.code> 300){
								this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
								return false;
							}
							this.fireEvent("save");
							//this.getWin().hide();
							return false;
				}
			},
			// 回填时将窗口编号保存下来
			onLoadData : function(entryName, data) {
				this.oldCkbh = data["CKBH"];
				return true;
			},
			//保存后关闭窗口
			onSave:function(entryName,op,json,data){
				this.getWin().hide();
			}
		});
