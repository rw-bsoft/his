$package("chis.application.rel.script")

$import("chis.script.BizTableFormView")

chis.application.rel.script.ResponsibleDoctorManageForm = function(cfg) {
	chis.application.rel.script.ResponsibleDoctorManageForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.rel.script.ResponsibleDoctorManageForm,
		chis.script.BizTableFormView, {
			doSaveData:function(){
				var values = this.getFormData();
				if (!values) {
					return;
				}
				values.op=this.op;
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.relevanceManageService",
					serviceAction : "saveResponsibleDoctor",
					method:"execute",
					body :values
				});
				if(result.code >=300){
					alert(result.msg);
					return;
				}
				this.fireEvent("savedata");
			},
			doCreateNew:function(){
				this.clicknew="1"
				this.doNew();
			},
			doNew : function() {
				chis.application.rel.script.ResponsibleDoctorManageForm.superclass.doNew.call(this);
				if(this.clicknew && this.clicknew=="1"){
					this.resetcols();
					this.clicknew="0"
				}
			},
			resetcols : function() {
				var form=this.form.getForm();
				form.findField("assistantId").setDisabled(false);
				form.findField("doctorId").setDisabled(false);
			}
		})