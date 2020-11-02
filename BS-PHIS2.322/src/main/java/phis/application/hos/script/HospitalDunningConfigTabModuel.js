$package("phis.application.hos.script")

$import("phis.script.TabModule")

phis.application.hos.script.HospitalDunningConfigTabModuel = function(cfg) {
	
	this.radioGroupValue = [];//用于放置用户选择的是否催款的单据 序号
	phis.application.hos.script.HospitalDunningConfigTabModuel.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.hos.script.HospitalDunningConfigTabModuel, phis.script.TabModule, {
	initPanel : function() {
		if (this.tab) {
			return this.tab;
		}
		var body = {
				"privates" : ['CKWH']
			}
		var rv = this.loadSystemParams(body);
		this.systemParams = rv;
		var tabItems = []
		var actions = this.actions
		for (var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			if(this.systemParams.CKWH==1){
				if("natureDunningTab"==ac.id){
					continue;
				}
			}else{
				if("departmentDunningTab"==ac.id){
					continue;
				}
			}
			tabItems.push({
				frame : this.frame,
				layout : "fit",
				title : ac.name,
				exCfg : ac,
				id : ac.id
					// modify by yangl
				})
		}
		var tab = new Ext.TabPanel({
					title : " ",
					border : false,
					width : this.width,
					activeTab : 0,
					frame : true,
					resizeTabs : this.resizeTabs,
					tabPosition : this.tabPosition || "top",
					// autoHeight : true,
					defaults : {
						border : false
						// autoHeight : true,
						// autoWidth : true
					},
					items : tabItems
				})
		tab.on("tabchange", this.onTabChange, this);
		tab.on("beforetabchange", this.onBeforeTabChange, this);
		tab.activate(this.activateId)
		this.tab = tab
		return tab;
	}
})