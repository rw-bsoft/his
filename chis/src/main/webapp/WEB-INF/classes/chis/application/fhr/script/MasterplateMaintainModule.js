$package("chis.application.fhr.script")

$import("chis.script.BizCombinedModule2")

chis.application.fhr.script.MasterplateMaintainModule = function(cfg) {
	chis.application.fhr.script.MasterplateMaintainModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.itemCollapsible = false;
	this.width = 720;
	this.height = 450;
	this.itemHeight = 70;
	this.exContext = {};
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.fhr.script.MasterplateMaintainModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.fhr.script.MasterplateMaintainModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
				this.form.on("cancel", this.onCancel, this);
				this.form.on("beforeSave", this.onBeforeSave, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("save", this.onListSave, this);
				this.grid = this.list.grid;
				return panel;
			},

			onListSave : function() {
				if (this.formData.manaUnitId == this.mainApp.deptId
						|| this.formData.manaUnitId.key == this.mainApp.deptId) {
					this.setModuleButton(true);
				} else {
					this.setModuleButton(false);
				}
			},

			onCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			onBeforeSave : function(entryName, op, saveRequest) {
				var masterplateName = "";
				if (this.formData) {
					masterplateName = this.formData.masterplateName
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "checkHasSameMP",
							method : "execute",
							body : saveRequest,
							schema : entryName,
							masterplateName : masterplateName
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.onBeforeSave);
					return;
				}
				return result.json.hasNoSameMP;
			},

			onWinShow : function() {
				this.list.initQueryFeild();
				if (this.formData && this.op == "update") {
					this.initDataId = this.formData.masterplateId;
					this.form.initDataId = this.initDataId;
					this.list.masterplateId = this.initDataId;
					this.checkIsUsedMasterplate();
					this.list.requestData.cnd = [this.initDataId];
					this.list.requestData.serviceId = "chis.templateService";
					this.list.requestData.serviceAction = "listFieldByRelation";
					this.form.initFormData(this.formData);
					this.grid.enable();
					this.list.refresh();
					if (this.formData.inputUnit.key == this.mainApp.deptId) {
						this.setModuleButton(true);
					} else {
						this.setModuleButton(false);
					}
				} else {
					this.form.doCreate();
					this.list.clear();
					this.grid.disable();
				}
			},
			checkIsUsedMasterplate : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "checkIsUsedMasterplate",
							method : "execute",
							masterplateId : this.initDataId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.onBeforeSave);
					return;
				}
				this.isUsedMasterplate = result.json.isUsedMasterplate;
			},

			setModuleButton : function(flag) {
				var btnsForm = this.form.form.getTopToolbar().items
				var num = btnsForm.getCount();
				for (var i = 0; i < num; i++) {
					var btnForm = btnsForm.item(i);
					if (!flag || this.isUsedMasterplate) {
						btnForm.disable();
					} else {
						btnForm.enable();
					}
				}
				var btns = this.list.grid.getTopToolbar().items;
				var n = btns.getCount();
				for (var i = 0; i < n - 1; i++) {
					var btn = btns.item(i);
					if (!flag || (this.isUsedMasterplate && (i != 6))) {
						btn.disable();
					} else {
						btn.enable();
					}
				}
			},

			onSave : function(entryName, op, json, data) {
				if (data.masterplateId) {
					this.initDataId = data.masterplateId;
					this.formData = data;
				}
				this.list.masterplateId = this.initDataId;
				this.list.requestData.cnd = [this.initDataId];
				this.list.requestData.serviceId = "chis.templateService";
				this.list.requestData.serviceAction = "listFieldByRelation";
				this.grid.enable();
				this.list.refresh();
				this.fireEvent("save", entryName, op, json, data);
			}

		})