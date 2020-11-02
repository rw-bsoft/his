$package("chis.application.per.script.office");

$import("chis.script.BizTableFormView");

chis.application.per.script.office.CheckupProjectOfficeForm = function(cfg) {
	cfg.colCount = 1;
	cfg.fldDefaultWidth = 300;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.width = 450;
	chis.application.per.script.office.CheckupProjectOfficeForm.superclass.constructor.apply(
			this, [cfg]);
	this.saveServiceId = "chis.checkupProjectOfficeService";
	this.saveAction = "saveProjectOffice";
	this.on("save", this.onSave, this);
};

Ext.extend(chis.application.per.script.office.CheckupProjectOfficeForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				if (this.op == "create") {
					this.initDataId = null;
				}
				chis.application.per.script.office.CheckupProjectOfficeForm.superclass.doNew
						.call(this);
			},

			onReady : function() {
				var manageUnit = this.form.getForm().findField("manaUnitId");
				if (manageUnit) {
					if (this.op == "create") {
						if (this.mainApp.deptId.length < 9)
							manageUnit.enable();
						else
							manageUnit.disable();
					} else {
						manageUnit.disable();
					}
					manageUnit.on("beforeselect", this.checkManaUnit, this);
				}
			},
			checkManaUnit : function(comb, node) {
				var key = node.attributes['key'];
				if (this.mainApp.uid == 'system') {
					return true;
				} else {
					if (node.getDepth() == 0) {// 根节点
						return true;
					} else if (key.length == 9) {
						return true;
					} else {
						return false;
					}
				}
			},

			getSaveRequest : function(saveData) {
				var form = this.form.getForm();
				var projectOfficeName = form.findField("projectOfficeName")
						.getValue();
				var manaUnitId = form.findField("manaUnitId").getValue();
				var cnd = [
						'and',
						['eq', ['$', 'projectOfficeName'],
								['s', projectOfficeName]],
						['eq', ['$', 'manaUnitId'], ['s', manaUnitId]]];
				var secCnd;
				if (this.initDataId) {
					secCnd = ['ne', ['$', 'projectOfficeCode'],
							['s', this.initDataId]];
				}
				var cnds;
				if (secCnd) {
					cnds = ['and', secCnd, cnd];
				} else {
					cnds = cnd;
				}
				saveData.cnd = cnds;
				return saveData;
			},

			onSave : function(entryName, op, json, data) {
				if (json.isExist) {
					Ext.Msg.alert("科室名重复", "科室名称[" + data.projectOfficeName
									+ "]已经存在!");
					return;
				}
				// this.doCancel();
			}

		});