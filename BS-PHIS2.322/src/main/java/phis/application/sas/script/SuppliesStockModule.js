$package("phis.application.sas.script");
$import("phis.script.SimpleModule", "util.helper.Helper");
phis.application.sas.script.SuppliesStockModule = function(cfg) {
	phis.application.sas.script.SuppliesStockModule.superclass.constructor.apply(this,
			[cfg]);
}, Ext.extend(phis.application.sas.script.SuppliesStockModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					if (this.mainApp['phis'].treasuryId == null
							|| this.mainApp['phis'].treasuryId == ""
							|| this.mainApp['phis'].treasuryId == undefined) {
						Ext.Msg.alert("提示", "未设置登录库房,请先设置");
						return;
					}
					if (this.mainApp['phis'].treasuryEjkf != 0) {
						Ext.MessageBox.alert("提示", "该库房不是一级库房!");
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
				// 账簿类别
				var zblbItems = this.getZblb();
				if (!zblbItems || zblbItems == '') {
					Ext.Msg.alert("提示", "未设置登录库房或者没有设置账簿类别,请先设置");
					return;
				}
				var radioGroup = new Ext.form.RadioGroup({
					height : 20,
					width : 620,
					id : 'zblbkccx',
					name : 'zblbkccx',
					value : zblbItems[0].initialConfig.inputValue,
					items : zblbItems,
					listeners : {
						change : function(group, newValue, oldValue) {
							if (this.tabModule.midiModules["stockSearch"]) {
								this.tabModule.midiModules["stockSearch"].loadData(newValue.inputValue);
							} else {
								this.getMode();
								this.tabModule.ZBLB = newValue.inputValue;
							}
							if (this.tabModule.midiModules["stockSearchDetails"]) {
								this.tabModule.midiModules["stockSearchDetails"].loadData(newValue.inputValue);
							} else {
								this.getMode();
								this.tabModule.ZBLB = newValue.inputValue;
							}
							if (this.tabModule.midiModules["stockEjKYJSearchDetails"]) {
								this.tabModule.midiModules["stockEjKYJSearchDetails"].loadData(newValue.inputValue);
							} else {
								this.getMode();
								this.tabModule.ZBLB = newValue.inputValue;
							}
							

						},
						scope : this
					}
				});
				this.radioGroup = radioGroup;
				radioGroup.setValue(zblbItems[0].initialConfig.inputValue);
				this.ZBLB = zblbItems[0].initialConfig.inputValue;
				var tbar = ['', radioGroup];

				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : tbar,
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
				this.tabModule = this.createModule("getMode", this.refMode);
				this.tabModule.ZBLB = this.ZBLB;
				return this.tabModule.initPanel();
			},
			// 得到账簿类别
			getZblb : function() {
				var kfxh = this.mainApp['phis'].treasuryId;
				var body = {};
				body["KFXH"] = kfxh;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "suppliesStockSearchService",
							serviceAction : "getZblbByKfxh",
							method : "execute",
							body : body
						});
				var kfzblb = [];
				kfzblb = r.json.list;
				var items = [];
				for (var i = 0; i < kfzblb.length; i++) {
					var item = new Ext.form.Radio({
								boxLabel : kfzblb[i][1],
								name : 'zblbkccx',
								inputValue : kfzblb[i][0]
							})
					items.push(item);
				}
				return items;
			}
		})