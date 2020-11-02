$package("chis.application.pub.script");

$import("app.modules.form.TableFormView");

chis.application.pub.script.DrugDirectoryForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "保存"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.pub.script.DrugDirectoryForm.superclass.constructor.apply(
			this, [cfg]);
	this.saveServiceId = "chis.drugManageService";
	this.saveAction = "saveDrug";
	this.on("save", this.onSave, this);
};

Ext.extend(chis.application.pub.script.DrugDirectoryForm,
		app.modules.form.TableFormView, {
			fixSaveCfg : function(saveCfg) {
				saveCfg.serviceAction = this.saveAction
			},
			onSave : function(entryName, op, json, data) {
				this.win.hide();
			}
		});