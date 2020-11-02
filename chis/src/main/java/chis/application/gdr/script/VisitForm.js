$package("chis.application.gdr.script")
$import("util.Accredit", "chis.script.BizTableFormView", "chis.script.util.helper.Helper")
chis.application.gdr.script.VisitForm = function(cfg) {
	this.entryName = 'chis.application.gdr.schemas.GDR_Visit';
	chis.application.gdr.script.VisitForm.superclass.constructor.apply(this, [cfg]);
	this.nowDate = this.mainApp.serverDate;
	this.saveServiceId = "chis.groupDinnerService";
	this.saveAction = "saveVisit";
}
Ext.extend(chis.application.gdr.script.VisitForm, chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.gdr.script.VisitForm.superclass.onReady.call(this);
				var poisoning = this.form.getForm().findField("poisoning")
				this.poisoning = poisoning;
				poisoning.on("blur", this.onPoisoning, this);
				poisoning.on("keyup", this.onPoisoning, this);
				poisoning.on("select", this.onPoisoning, this);
			},

			onPoisoning : function() {
				if (this.poisoning.getValue() == "y") {
					this.form.getForm().findField("poisoningCount").enable();
					this.form.getForm().findField("poisoningDesc").enable();
				} else {
					this.form.getForm().findField("poisoningCount").setValue();
					this.form.getForm().findField("poisoningDesc").setValue();
					this.form.getForm().findField("poisoningCount").disable();
					this.form.getForm().findField("poisoningDesc").disable();
				}
			},

			initFormData : function(data) {
				chis.application.gdr.script.VisitForm.superclass.initFormData.call(this, data);
				this.onPoisoning();
			},

			saveToServer : function(saveData) {
				saveData.gdrId = this.gdrId;
				this.initDataId=saveData.visitId;
				chis.application.gdr.script.VisitForm.superclass.saveToServer.call(this,
						saveData);
			},

			loadData : function() {
				this.doNew();
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading");
				}
				this.loading = true;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.groupDinnerService",
							serviceAction : "getVisitInfo",
							method:"execute",
							schema : "chis.application.gdr.schemas.GDR_Visit",
							cnd : ["eq", ["$", "gdrId"], ["s", this.gdrId]]
						});
				if (this.form && this.form.el) {
					this.form.el.unmask();
				}
				this.loading = false;
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadData);
					return;
				}
				if (result.json.body) {
					this.initDataId = result.json.body.visitId;
					this.initFormData(result.json.body);
					
				} else {
					this.initDataId = null;
				}
				this.fireEvent("loadData", this.entryName,
						result.json.body);
				if (this.op == 'create') {
					this.op = "update";
				}
			}

		});