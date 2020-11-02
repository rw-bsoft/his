$package("phis.application.cfg.script")

$import("phis.script.TabModule")
phis.application.cfg.script.ConfigTreasuryBooksInitializationModule = function(
		cfg) {
	phis.application.cfg.script.ConfigTreasuryBooksInitializationModule.superclass.constructor
			.apply(this, [ cfg ]);

}
Ext
		.extend(
				phis.application.cfg.script.ConfigTreasuryBooksInitializationModule,
				phis.script.TabModule,
				{
					initPanel : function(sc) {
						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryCsbz == "1") {
							Ext.Msg.alert("提示", "该库房账册已经初始化!");
							return null;
						}
						if (this.mainApp.roleType == "group_12") {
							if (this.mainApp['phis'].treasuryEjkf == 0) {
								Ext.MessageBox.alert("提示", "该库房不是二级库房!");
								return;
							}
						} else if (this.mainApp.roleType == "group_11") {
							if (this.mainApp['phis'].treasuryEjkf != 0) {
								Ext.MessageBox.alert("提示", "该库房不是一级库房!");
								return;
							}
						}
						var panel = phis.application.cfg.script.ConfigTreasuryBooksInitializationModule.superclass.initPanel
								.call(this, sc);
						this.panel = panel;
						return panel;
					}
				})
