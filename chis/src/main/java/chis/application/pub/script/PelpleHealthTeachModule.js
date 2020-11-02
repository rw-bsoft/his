$package("chis.application.pub.script");

$import("chis.script.BizCombinedModule2");

chis.application.pub.script.PelpleHealthTeachModule = function(cfg) {
	chis.application.pub.script.PelpleHealthTeachModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "west";
	this.width = this.width || 1000;
	this.height = this.height || 500;
	this.itemWidth = 600;
	this.itemCollapsible = false;
};

Ext.extend(chis.application.pub.script.PelpleHealthTeachModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.pub.script.PelpleHealthTeachModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("loadData", this.onFormLoadData, this);
				this.form.on("doNew", this.onDoNew, this);
				this.form.on("cancel", this.doCancel, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("select", this.recordsSelect, this);
				this.list.on("clear", this.clearSelect, this)
				this.grid = this.list.grid;
				return panel;
			},
			onDoNew : function() {
				this.recordId = null;
				if (this.list) {
					this.list.recordId = null;
					this.list.store.removeAll();
				}
			},

			onFormSave : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data);
				Ext.apply(data, json.body);
				this.recordId = data.recordId;
				this.list.recordId = data.recordId;
				this.list.requestData.cnd = ["eq", ["$", "a.recordId"],
						["s", this.recordId]];
				this.list.refresh();
			},
			onFormLoadData : function(entryName, body) {
				this.recordId = body.recordId;
				this.list.recordId = body.recordId;
				this.list.requestData.cnd = ["eq", ["$", "a.recordId"],
						["s", this.recordId]];
				this.list.refresh();
			},
			loadData : function() {
				if (this.initDataId) {
					this.form.initDataId = this.initDataId;
					this.form.loadData();
				}
			},
			doCancel : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},
			doCreate : function() {
				this.form.doCreate();
				this.recordId = null;
				if (this.list) {
					this.list.recordId = null;
					this.list.store.removeAll();
				}
			},
			clearSelect : function() {
				if (!this.recordId) {
					return;
				}
				Ext.Msg.show({
					title : '确认清空记录',
					msg : '清空操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							util.rmi.jsonRequest({
										serviceId : 'chis.pelpleHealthTeachService',
										serviceAction : "clearSelectRecords",
										method : "execute",
										recordId : this.recordId
									}, function(code, msg, json) {
										if (this.form && this.form.el) {
											this.form.el.unmask();
										}
										if (code > 300) {
											this.processReturnMsg(code, msg,
													this.recordsSelect);
											return;
										}
										this.list.refresh();
									}, this);
						}
					},
					scope : this
				})
			},
			recordsSelect : function(records, diagnoseType) {
				if (!this.recordId) {
					return;
				}
				var diagnoses = {};
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					var diagnose = {};
					diagnose["recordId"] = this.recordId;
					diagnose["diagnoseType"] = diagnoseType;
					diagnose["ICD10"] = r.get("JBDM") || r.get("ZHDM")
							|| r.get("ICD10");
					diagnose["diagnoseName"] = r.get("JBMC") || r.get("ZHMC");
					diagnose["diagnoseNamePy"] = r.get("PYDM");
					diagnoses[i] = diagnose;
				}
				util.rmi.jsonRequest({
							serviceId : 'chis.pelpleHealthTeachService',
							serviceAction : "saveSelectRecords",
							method : "execute",
							records : diagnoses,
							recordId : this.recordId
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask();
							}
							this.loading = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.recordsSelect);
								return;
							}
							this.list.refresh();
						}, this);
			}
		});