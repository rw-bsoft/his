$package("phis.application.sup.script")

$import("phis.script.TabModule")
phis.application.sup.script.EquipmentWeighingManagementModule = function(cfg) {
	phis.application.sup.script.EquipmentWeighingManagementModule.superclass.constructor
			.apply(this, [ cfg ]);

}
Ext
		.extend(
				phis.application.sup.script.EquipmentWeighingManagementModule,
				phis.script.TabModule,
				{
					initPanel : function(sc) {

						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryCsbz == "0") {
							Ext.Msg.alert("提示", "该库房未做账册初始化!");
							return null;
						}
						if (this.mainApp['phis'].treasuryEjkf != 0) {
							Ext.MessageBox.alert("提示", "该库房不是一级库房!");
							return;
						}
						var panel = phis.application.sup.script.EquipmentWeighingManagementModule.superclass.initPanel
								.call(this, sc);
						this.panel = panel;
						return panel;
					},
					onTabChange : function(tabPanel, newTab, curTab) {
						if (newTab.__inited) {
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
							return;
						}
						var exCfg = newTab.exCfg
						var cfg = {
							showButtonOnTop : true,
							autoLoadSchema : false,
							isCombined : true
						}
						Ext.apply(cfg, exCfg);
						var ref = exCfg.ref
						if (ref) {
							var body = this.loadModuleCfg(ref);
							Ext.apply(cfg, body)
						}
						var cls = cfg.script
						if (!cls) {
							return;
						}

						if (!this.fireEvent("beforeload", cfg)) {
							return;
						}
						$require(cls, [
								function() {
									var m = eval("new " + cls + "(cfg)");
									m.setMainApp(this.mainApp);
									if (this.exContext) {
										m.exContext = this.exContext;
									}
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									newTab.add(p);
									newTab.__inited = true
									this.tab.doLayout();
									this.fireEvent("tabchange", tabPanel,
											newTab, curTab);
								}, this ])
					}
				})
