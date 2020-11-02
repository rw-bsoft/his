$package("phis.application.sup.script");
$import("phis.script.SimpleModule", "util.helper.Helper");
phis.application.sup.script.MaintenanceMsgModule = function(cfg) {
	phis.application.sup.script.MaintenanceMsgModule.superclass.constructor
			.apply(this, [ cfg ]);
}, Ext.extend(phis.application.sup.script.MaintenanceMsgModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (!this.mainApp['phis'].treasuryId) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryCsbz == 0) {
					Ext.Msg.alert("提示", "该库房账册未初始化,不能进行业务操作!");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
//				if (this.mainApp['phis'].treasuryPdzt == 1) {
//					Ext.MessageBox.alert("提示", "该库房处于盘点状态,不能维修!");
//					return;
//				}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [ {
						layout : "fit",
						split : true,
						title : '',
						region : 'center',
						width : 280,
						items : this.getMode()
					} ]
				});
				return panel;
			},
			getMode : function() {
				this.tabModeWXGL = this.createModule("getModeWXGL",
						this.refMode);
				return this.tabModeWXGL.initPanel();
			}
		})