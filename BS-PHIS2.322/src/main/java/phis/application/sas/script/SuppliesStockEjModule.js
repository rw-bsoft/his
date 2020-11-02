$package("phis.application.sas.script");
$import("phis.script.SimpleModule", "util.helper.Helper");
phis.application.sas.script.SuppliesStockEjModule = function(cfg) {
	phis.application.sas.script.SuppliesStockEjModule.superclass.constructor.apply(this,
			[cfg]);
}, Ext.extend(phis.application.sas.script.SuppliesStockEjModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					if (this.mainApp['phis'].treasuryId == null
							|| this.mainApp['phis'].treasuryId == ""
							|| this.mainApp['phis'].treasuryId == undefined) {
						Ext.Msg.alert("提示", "未设置登录库房,请先设置");
						return null;
					}
					if (this.mainApp['phis'].treasuryEjkf == 0) {
						Ext.MessageBox.alert("提示", "该库房不是二级库房!");
						return;
					}
					if (this.mainApp['phis'].treasuryCsbz != 1) {
						Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
						return;
					}
				}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : this.getMode()
									}]
						});
				return panel;
			},
			getMode : function() {
				this.tabModuleEj = this.createModule("getModeEj", this.refMode);
				return this.tabModuleEj.initPanel();
			}
		})