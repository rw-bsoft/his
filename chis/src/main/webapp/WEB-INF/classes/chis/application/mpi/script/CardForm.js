/**
 * 卡管理表单
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizTableFormView");

chis.application.mpi.script.CardForm = function(cfg) {
	cfg.width = 300;
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.mpi.script.CardForm.superclass.constructor.apply(this, [cfg]);
	this.empiServiceId = "chis.empiService";
	this.serviceAction = "registerCard";
}

Ext.extend(chis.application.mpi.script.CardForm, chis.script.BizTableFormView, {
	saveToServer : function(saveData) {
		saveData["createUnit"] = this.mainApp.deptId;
		saveData["createUser"] = this.mainApp.uid;
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.empiServiceId,
					method:"execute",
					schema : this.entryName,
					body : saveData,
					serviceAction : this.serviceAction
				}, function(code, msg, json) {
					this.form.el.unmask();
					this.saving = false;
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.saveToServer, [saveData]);
						return;
					}
					this.getWin().hide();
					this.fireEvent("save", this.entryName, this.op,
							json, this.data);
				}, this)// jsonRequest
	}
})