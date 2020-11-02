$package("chis.application.fhr.script")

$import("chis.script.BizCombinedModule2")

chis.application.fhr.script.FieldDicMaintainModule = function(cfg) {
	chis.application.fhr.script.FieldDicMaintainModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.itemCollapsible = false;
	this.width = 730;
	this.height =500;
	this.itemHeight = 150;
	this.exContext = {};
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.fhr.script.FieldDicMaintainModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.fhr.script.FieldDicMaintainModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
				this.form.on("cancel", this.onCancel, this);
				this.form.on("beforeSave", this.onBeforeSave, this);
				this.list = this.midiModules[this.actions[1].id];
				this.grid = this.list.grid;
				return panel;
			},

			onCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},

			onWinShow : function() {
				this.list.initQueryFeild();
				if (this.formData && this.op == "update") {
					this.initDataId = this.formData.fieldId;
					this.form.initDataId = this.initDataId;
					this.form.dicValue = this.formData.dicRender.key;
					this.list.fieldId = this.initDataId;
					this.checkHasUsed(this.initDataId);
					this.list.requestData.cnd = ["eq", ["$", "a.fieldId"],
							["s", this.initDataId]];
					this.list.initCnd = ["eq", ["$", "a.fieldId"],
							["s", this.initDataId]];
					this.form.initFormData(this.formData);
					this.form.setFormButton();
					this.form.onDefaultTypeChange();
					this.form.op = this.op;
					if (this.formData.dicRender.key == "0") {
						this.list.clear();
						this.grid.disable();
					} else {
						this.grid.enable();
						this.list.refresh();
					}
					if (this.list.hasUsed == true) {
						this.list.setListButton(false);
					} else {
						this.list.setListButton(true);
					}
				} else {
					this.form.doCreate();
					this.list.hasUsed = false;
					this.form.hasUsed = false;
					this.list.clear();
					this.grid.disable();
					this.form.setFormButton();
					this.form.onDefaultTypeChange();
				}
			},

			onSave : function(entryName, op, json, data) {
				if (data.fieldId) {
					this.initDataId = data.fieldId;
					this.formData=data;
				}
				this.list.fieldId = this.initDataId;
				this.form.initDataId = this.initDataId;
				this.form.dicValue = data.dicRender;
				this.list.requestData.cnd = ["eq", ["$", "a.fieldId"],
						["s", this.initDataId]];
				this.list.initCnd = ["eq", ["$", "a.fieldId"],
						["s", this.initDataId]];
				if (data.dicRender == "0") {
					this.list.clear();
					this.grid.disable();
				} else {
					this.grid.enable();
					this.list.refresh();
				}
				this.fireEvent("save", entryName, op, json, data);
			},
			onBeforeSave : function(entryName, op, saveRequest) {
				var id="";
				if(this.formData){
					id=this.formData.id;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "checkHasSameField",
							method:"execute",
							body : saveRequest,
							schema : entryName,
							id:id
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.onBeforeSave);
					return false;
				}
				return result.json.hasNoSameField;
			},
			checkHasUsed : function(fieldId) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "checkHasUsed",
							method:"execute",
							fieldId : fieldId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.checkHasUsed);
					return;
				}
				this.list.hasUsed = result.json.hasUsed;
				this.form.hasUsed = result.json.hasUsed;
			}

		})