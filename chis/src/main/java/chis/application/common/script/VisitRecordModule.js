$package("chis.application.common.script")
$import("util.Accredit", "chis.script.BizCombinedTabModule",
		"chis.script.util.widgets.MyMessageTip")

chis.application.common.script.VisitRecordModule = function(cfg) {
	this.autoLoadData = false
	chis.application.common.script.VisitRecordModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemWidth = 260 // ** 第一个Item的宽度
	this.itemHeight = 432 // ** 第一个Item的高度
	this.nowDate = this.mainApp.serverDate;
	this.on("loadModule", this.onLoadModule, this);
}
Ext.extend(chis.application.common.script.VisitRecordModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.common.script.VisitRecordModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.midiModules["VisitRecordList"] = this.list
				this.list.on("rowClick", this.onRowClick, this)
				this.list.on("loadData", this.onLoadData, this)
				this.list.on("refresh", this.onRefresh, this)
				if(this.key=='C_08')//高血压
				{
					this.list.SFLB="7";
				}
				if(this.key=='D_08')//糖尿病
				{
					this.list.SFLB="8";
				}
				
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this);
				return panel;
			},
			onLoadModule : function(moduleId, module) {
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					this.midiModules["DiabetesComplicationListView"] = module
				}

			},
			loadData : function() {
				this.clearAllActived()
				this.tab.setActiveTab(0)
				this.tab.enable()
				this.refreshExcontext()
				this.midiModules["VisitRecordList"].doNow()
			},
			onRefresh : function() {
				this.midiModules["VisitRecordList"].selectedIndex = 0
			},
			onLoadData : function(store, records, opt) {
				var r =store.getAt(0)
				this.midiModules["VisitRecordDetialModule"].setActivePanel(r);
			},
			onRowClick : function(grid, index, e) {
				// this.clearAllActived()
				this.tab.setActiveTab(0)
				this.tab.enable()
				var r = grid.store.getAt(index)
				this.midiModules["VisitRecordDetialModule"].setActivePanel(r);
			}
		});