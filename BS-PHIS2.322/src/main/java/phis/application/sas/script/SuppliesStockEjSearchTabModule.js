$package("phis.application.sas.script");

$import("phis.script.TabModule");

phis.application.sas.script.SuppliesStockEjSearchTabModule = function(cfg) {
	phis.application.sas.script.SuppliesStockEjSearchTabModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sas.script.SuppliesStockEjSearchTabModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								frame : this.frame,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							tabPosition : this.tabPosition || "top",
							defaults : {
								border : false
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			}
		});