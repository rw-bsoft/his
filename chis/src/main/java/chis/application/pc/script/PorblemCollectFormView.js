$package("chis.application.pc.script")
$import("chis.script.BizTableFormView", "util.dictionary.DictionaryLoader")

chis.application.pc.script.PorblemCollectFormView = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.pc.script.PorblemCollectFormView.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.pc.script.PorblemCollectFormView, chis.script.BizTableFormView, {
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : "chis.porblemCollectService",
							serviceAction : "savePorblemCollect",
							method:"execute",
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							if (this.op == "create") {
								this.op = "update";
							}
						}, this);
			},
			setField : function() {
				var form = this.form.getForm();
				form.findField("status").disable();
				form.findField("treatTime").disable();
				form.findField("treatDesc").disable();
				form.findField("description").enable();
				form.findField("contactPhone").enable();
				if (this.isTreat) {
					form.findField("treatTime").enable();
					form.findField("treatTime").setValue(this.mainApp.serverDate);
					form.findField("treatDesc").enable();
					form.findField("description").disable();
					form.findField("contactPhone").disable();
					form.findField("status").enable();
				} else {
					var treatTime = form.findField("treatTime");
					if (treatTime) {
						treatTime.setValue("");
					}
				}
			},
			onWinShow : function() {
				this.setButton();
				if (this.op == "create") {
					this.doCreate();
				}
				this.win.doLayout();
				this.setField();
			},
			setButton : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var bts = this.form.getTopToolbar().items;
				if (this.op == "read") {
					bts.items[0].disable();
				} else {
					bts.items[0].enable();
				}
			}
		})