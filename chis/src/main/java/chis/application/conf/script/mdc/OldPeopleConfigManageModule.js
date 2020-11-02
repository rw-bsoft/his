$package("chis.application.conf.script.mdc")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.mdc.OldPeopleConfigManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.mdc.OldPeopleConfigManageModule.superclass.constructor.apply(
			this, [cfg])
	this.layOutRegion = "north"
	this.itemWidth = this.width
	this.itemHeight = 190
}
Ext.extend(chis.application.conf.script.mdc.OldPeopleConfigManageModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.mdc.OldPeopleConfigManageModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("saveAll", this.onSaveAll, this)
				this.form.on("visitIntervalChange", this.visitIntervalChange,
						this)
				this.form.on("planModeChange", this.planModeChange, this)
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("beforeCellEditToModule",
						this.onBeforeCellEditToModule, this)
				this.grid = this.list.grid;
				this.grid.setAutoScroll(true);
				return panel;
			},

			loadData : function() {
				if (this.form) {
					this.form.fieldReadOnly = this.fieldReadOnly;
					this.form.readOnly = this.readOnly;
					this.form.loadData();
				}
				if (this.list) {
					this.list.readOnly = this.readOnly;
					this.list.loadData();
				}

			},

			onSaveAll : function(saveData) {
				var planMode = saveData.planMode;
				var needCheckgrid = 0;
				var gridData
				var oldPeopleAge = saveData.oldPeopleAge
				var oldPeopleVisitIntervalSame = saveData.oldPeopleVisitIntervalSame
				var oldPeopleStartMonth = saveData.oldPeopleStartMonth
				var oldPeopleEndMonth = saveData.oldPeopleEndMonth
				if (!oldPeopleVisitIntervalSame && planMode == '1') {
					gridData = this.list.getSaveData();
					if (!gridData) {
						return
					}
					needCheckgrid = 1
				}
				var allData = {
					form : saveData,
					grid : gridData
				}
				if (needCheckgrid == 1) {
					needCheckgrid = 0
					if (gridData == "") {
						Ext.Msg.alert("提示", "列表不能为空")
						return
					}

					for (var i = 0; i < gridData.length; i++) {
						if (oldPeopleAge > gridData[0].oldPeopleStartAge
								|| oldPeopleAge > gridData[0].oldPeopleEndAge) {

							Ext.Msg.alert("提示", "起始年龄或终止年龄小于老年人起始年龄!");
							return
						}
					}
				}

				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							schema : this.entryName,
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction,
							method:"execute",
							body : allData
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.list.store.commitChanges();
							this.mainApp.exContext.oldPeopleAge = oldPeopleAge;
							this.mainApp.exContext.oldPeopleMode = planMode;
						}, this)

			},

			visitIntervalChange : function(value, planMode) {
				if (planMode == 1) {
					if (value) {
						this.grid.disable();
					} else {
						this.grid.enable();
					}
				}
			},

			planModeChange : function(value) {
				if (value == "2") {
					this.grid.disable();
				} else {
					if (this.form.planMode != value) {
						this.grid.store.removeAll();
					}
					this.grid.enable();
				}
			},

			onBeforeCellEditToModule : function() {
				var oldPeopleAge = this.form.form.getForm()
						.findField("oldPeopleAge").getValue();
				this.list.oldPeopleAge = oldPeopleAge;
			}
		});
