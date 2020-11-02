/**
 * 儿童意外情况整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.accident")
$import("util.Accredit", "chis.script.BizCombinedModule2")
chis.application.cdh.script.accident.ChildrenAccidentModule = function(cfg) {
	cfg.labelAlign = "left";
	cfg.width = 880;
	cfg.autoLoadData = false;
	chis.application.cdh.script.accident.ChildrenAccidentModule.superclass.constructor.apply(
			this, [cfg])

}
Ext.extend(chis.application.cdh.script.accident.ChildrenAccidentModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.cdh.script.accident.ChildrenAccidentModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("doRemove", this.onDoRemove, this);
				this.list.on("firstRowSelected", this.onRowClick, this);
				this.list.on("loadData", this.onLoadData, this)
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("rowdblclick", this.onRowClick, this)
				return panel;
			},

			onLoadData : function(store) {
				if (store.getCount() == 0) {
					this.form.op = "create";
					this.form.initDataId = null;
					this.form.doNew();
					this.setButtonDisable(true);
				}
			},

			onRowClick : function(grid, index, e) {
				var r = this.grid.getSelectionModel().getSelected();
				this.refreshExContextData(this.form, this.exContext);
				if (r) {
					this.setButtonDisable(false);
					this.form.op = "update";
					var formData = this.castListDataToForm(r.data,
							this.form.schema);
					this.form.initFormData(formData);
				} else {
					this.setButtonDisable(true);
					this.form.op = "create";
					this.form.initDataId = null;
					this.form.doNew();
				}
			},

			onFormSave : function(entryName, op, json, data) {
				this.list.refresh();
			},

			onDoRemove : function(recordId) {
				if (recordId == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + recordId + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(recordId);
								}
							},
							scope : this
						})
			},

			processRemove : function(recordId) {
				if (recordId == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, recordId)) {
					return;
				}
				this.panel.el.mask("正在删除数据...")
				util.rmi.jsonRequest({
							serviceId : this.removeServiceId,
							method:"execute",
							serviceAction : "",
							schema : this.entryName,
							body : {
								"pkey" : recordId
							}
						}, function(code, msg, json) {
							this.panel.el.unmask()
							if (code < 300) {
								var r = this.list.store.getById(recordId);
								this.list.store.remove(r);
								this.form.initDataId = null;
								this.form.doNew();
								this.list.active();
								this.fireEvent("remove", this.entryName,
										'remove', json, recordId)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},

			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			},

			setButtonDisable : function(disabled) {
				if (!this.form.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.form.getTopToolbar().items;
				if (btns) {
					var n = btns.getCount()
					for (var i = 2; i < n; i++) {
						var btn = btns.item(i)
						if (disabled) {
							if (!btn.disabled) {
								btn.disable()
							}
						} else {
							if (btn.disabled) {
								btn.enable();
							}
						}
					}
				}
			}
		});