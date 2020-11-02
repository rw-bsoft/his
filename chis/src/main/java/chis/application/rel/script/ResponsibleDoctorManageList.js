$package("chis.application.rel.script")

$import("chis.script.BizSimpleListView")

chis.application.rel.script.ResponsibleDoctorManageList = function(cfg) {
	this.initCnd = ['like', ['$', 'a.createUnit'], ['$','%user.manageUnit.id']]
	chis.application.rel.script.ResponsibleDoctorManageList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.rel.script.ResponsibleDoctorManageList,
		chis.script.BizSimpleListView, {
			doCreateNew:function(){
				var module = this.createSimpleModule("refForm",this.refForm);
				module.initPanel();
				module.on("savedata", this.refresh, this);
				module.initDataId = null;
				module.op = "create";
				module.exContext.control = {
					"update" : true
				};
				this.showWin(module);
				module.doNew();
			},
			doUpdatedata:function(){
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var module = this.createSimpleModule("refupForm",this.refForm);
				module.initPanel();
				module.on("savedata", this.refresh, this);
				module.initDataId = r.get("recordId");
				module.op = "update";
				module.exContext.control = {
					"update" : true
				};
				this.showWin(module);
				var form=module.form.getForm();
				form.findField("assistantId").setDisabled(true);
				form.findField("doctorId").setDisabled(true);
				module.loadData();
			}
		})