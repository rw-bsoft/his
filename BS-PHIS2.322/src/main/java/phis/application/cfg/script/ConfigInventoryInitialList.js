$package("phis.application.cfg.script")

$import("phis.script.SimpleList")
phis.application.cfg.script.ConfigInventoryInitialList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.cfg.script.ConfigInventoryInitialList.superclass.constructor
			.apply(this, [ cfg ])
	this.on("winShow", this.onWinShow, this);
}
Ext
		.extend(
				phis.application.cfg.script.ConfigInventoryInitialList,
				phis.script.SimpleList,
				{
					onReady : function() {
						phis.application.cfg.script.ConfigInventoryInitialList.superclass.onReady
								.call(this);
						this.initCnd = [
								'and',
								[ 'eq', [ '$', 'a.CSLB' ], [ 'd', 1 ] ],
								[ 'eq', [ '$', ' a.KFXH' ],
										[ 'd', this.mainApp['phis'].treasuryId ] ] ];
						this.requestData.cnd = [
								'and',
								[ 'eq', [ '$', 'a.CSLB' ], [ 'd', 1 ] ],
								[ 'eq', [ '$', ' a.KFXH' ],
										[ 'd', this.mainApp['phis'].treasuryId ] ] ];
						this.loadData();
					},
					onWinShow : function() {
						if (this.configInventoryInModuleUpd) {
							this.configInventoryInModuleUpd.doUpdat(r
									.get("JLXH"), r.get("WZXH"), r.get("CJXH"));
							this.configInventoryInModuleUpd.opener = this;
						}
						if (this.configInventoryInModuleAdd) {
							this.configInventoryInModuleAdd.doNew();
							this.configInventoryInModuleAdd.opener = this;
						}
					},
					doAdd : function(grid, index, e) {
						this.configInventoryInModuleAdd = this.createModule(
								"configInventoryInModule", this.neworupdRef);
						var win = this.configInventoryInModuleAdd.getWin();
						win.show();
						win.center();
						this.configInventoryInModuleAdd.opener = this;
						if (!win.hidden) {
							this.configInventoryInModuleAdd.doNew();
						}
					},
					doUpd : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						this.configInventoryInModuleUpd = this.createModule(
								"configInventoryInModule", this.neworupdRef);
						var win = this.configInventoryInModuleUpd.getWin();
						win.show();
						win.center();
						this.configInventoryInModuleUpd.opener = this;
						this.configInventoryInModuleUpd.doUpdat(r.get("JLXH"),
								r.get("WZXH"), r.get("CJXH"));
					},
					doRefresh : function() {
						this.refresh()
					},
					onDblClick : function(grid, index, e) {
						var item = {};
						item.text = "修改";
						item.cmd = "upd";
						this.doAction(item, e)

					},
					doExecute : function() {
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configInventoryInitialService",
							serviceAction : "saveTransferInventoryIn"
						});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							MyMessageTip.msg("提示", "转账失败!", true);
							return;
						} else {
							MyMessageTip.msg("提示", "转账成功!", true);
							this.setButtonsState([ 'refresh' ], false);
							this.setButtonsState([ 'add' ], false);
							this.setButtonsState([ 'upd' ], false);
							this.setButtonsState([ 'remove' ], false);
							this.setButtonsState([ 'execute' ], false);
							var exCfg = this.mainApp.taskManager.tasks
									.item(userDomain);
							if (exCfg) {
								exCfg.treasuryCsbz = 1;
							}
							this.mainApp['phis'].treasuryCsbz = 1;
						}
					},
					setButtonsState : function(m, enable) {
						var btns;
						var btn;
						if (this.showButtonOnTop) {
							btns = this.grid.getTopToolbar();
						} else {
							btns = this.grid.buttons;
						}

						if (!btns) {
							return;
						}
						if (this.showButtonOnTop) {
							for ( var j = 0; j < m.length; j++) {
								if (!isNaN(m[j])) {
									btn = btns.items.item(m[j]);
								} else {
									btn = btns.find("cmd", m[j]);
									btn = btn[0];
								}
								if (btn) {
									(enable) ? btn.enable() : btn.disable();
								}
							}
						} else {
							for ( var j = 0; j < m.length; j++) {
								if (!isNaN(m[j])) {
									btn = btns[m[j]];
								} else {
									for ( var i = 0; i < this.actions.length; i++) {
										if (this.actions[i].id == m[j]) {
											btn = btns[i];
										}
									}
								}
								if (btn) {
									(enable) ? btn.enable() : btn.disable();
								}
							}
						}
					}
				})