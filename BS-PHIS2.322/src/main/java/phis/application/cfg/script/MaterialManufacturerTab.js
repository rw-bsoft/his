$package("phis.application.cfg.script")

$import("phis.script.SimpleModule")

phis.application.cfg.script.MaterialManufacturerTab = function(cfg) {
	phis.application.cfg.script.MaterialManufacturerTab.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cfg.script.MaterialManufacturerTab,
		phis.script.SimpleModule, {
	initPanel : function(sc) {
		if (this.panel) {
			if (!this.isCombined) {
				this.fireEvent("beforeAddToWin", this.panel)
				this.addPanelToWin();
			}
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : true,
					frame : false,
					layout : 'border',
					defaults : {
						border : true
					},
					items : [{
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'north',
								height : 250,
								items : this.getList(this.refCJXXList)
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getList(this.refCJZJXXList)
							}],
						tbar : this.tbar
				});
		this.panel = panel;
		this.panel.on("beforeclose",this.onBeforeBusSelect,this);
		return panel;
	},
	getList:function(relList){
		var module = this.createModule(relList, relList);
		if(relList==this.refCJXXList){
			module.requestData.cnd = ['eq',['$','WZXH'],['i',this.opener.cfg.WZXH]];
			this.module = module
			module.opener=this;
			var form = module.initPanel();
			module.refresh();
		}else{
			module.opener=this;
//			module.requestData.cnd = ['eq',['$','CJXH'],['i',0]];
			var form = module.initPanel();
		}
		return form;
	},
	getSaveData:function(){
		return this.module.getSaveData();
	},
	refresh:function(){
		this.midiModules["phis.application.cfg.CFG/CFG/CFG57010201"].requestData.cnd = ['eq',['$','WZXH'],['i',this.opener.cfg.WZXH]];
		this.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].requestData.cnd = ['eq',['$','DXXH'],['i',0]];
		this.midiModules["phis.application.cfg.CFG/CFG/CFG57010201"].refresh();
		this.midiModules["phis.application.cfg.CFG/CFG/CFG57010202"].refresh();
	},
	isDirty:function(){
		var dirty = false;
		if(this.midiModules["phis.application.cfg.CFG/CFG/CFG57010201"]){
			var store = this.midiModules["phis.application.cfg.CFG/CFG/CFG57010201"].grid.getStore();
			var n = store.getCount()
			var data = []
			for(var i = 0;i < n; i ++){
				var r = store.getAt(i)
				if(r.dirty){
					dirty=true;
				}
			}		
		}
		return dirty;
	}
});