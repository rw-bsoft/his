$package("phis.application.cfg.script")

$import("phis.script.SimpleList", "phis.script.SimpleForm")

phis.application.cfg.script.AlreadyClassList = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.cfg.script.AlreadyClassList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.cfg.script.AlreadyClassList, phis.script.SimpleList, {
			onReady : function() {
				phis.application.cfg.script.AlreadyClassList.superclass.onReady
						.call(this);
				if (this.mainApp['phis'].treasuryLbxh == 0) {
					this.setButtonsState(['updateStage'], false);
				}
			},
			loadData : function(ZDXH) {
				var ZDXH2 = -2;
				if (ZDXH) {
					ZDXH2 = ZDXH;
				}
				this.clear();
				this.requestData.serviceId = "phis.configSubstancesClassService";
				this.requestData.serviceAction = "alreadyClassQuery";
                this.requestData.ZDXH = ZDXH2
                
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
				if (this.mainApp['phis'].treasuryLbxh == 0) {
					this.setButtonsState(['updateStage'], false);
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
					for (var j = 0; j < m.length; j++) {
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
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
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
		});