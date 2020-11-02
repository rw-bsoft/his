$package("chis.application.dc.script");

$import("chis.script.BizSimpleListView");

chis.application.dc.script.VaccinationList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema= true;
	chis.application.dc.script.VaccinationList.superclass.constructor.apply(this, [cfg]);
	this.enableCnd = false;
	this.actions=cfg.actions
};

Ext.extend(chis.application.dc.script.VaccinationList, chis.script.BizSimpleListView, {
			doNew : function() {
				if (!this.readOnly) {
					var btns = this.grid.getTopToolbar().items;
					if (btns) {
						for (var i = 0; i < btns.length; i++) {
							var btn = btns.item(i)
							if (btn.type != "button") {
								continue;
							}
							if (!this.exContext.args.rabiesID || this.exContext.args.rabiesID == "") {
								btn.disable();
							} else {
								btn.enable();
							}
						}
					}
				}
				this.clear();

			},
			loadData : function() {
				this.clear();
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
			},
			doAction : function(item, e) {
				var btns = this.grid.getTopToolbar().items
				if (btns.items[1].disabled) {
					return;
				}
				chis.application.dc.script.VaccinationList.superclass.doAction.call(this, item,
						e);
			}
		});