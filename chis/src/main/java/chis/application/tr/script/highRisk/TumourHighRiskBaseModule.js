$package("chis.application.tr.script.highRisk")

$import("chis.script.BizCombinedModule2")

chis.application.tr.script.highRisk.TumourHighRiskBaseModule = function(cfg){
	chis.application.tr.script.highRisk.TumourHighRiskBaseModule.superclass.constructor.apply(this,[cfg]);
	this.layOutRegion = "north";
	this.height = 500;
	this.itemHeight = 300;
	this.itemCollapsible = false;
	this.loadServiceId = "chis.tumourHighRiskService";
	this.loadAction = "initializeRecord";
//	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskBaseModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.highRisk.TumourHighRiskBaseModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.form=this.midiModules[this.actions[0].id];
		this.form.on("save",this.onSaveAfter,this);
		this.tcrList = this.midiModules[this.actions[1].id];
		return panel;
	},
	getLoadRequest : function() {
		var body = {
			THRID : this.exContext.ids["MDC_TumourHighRisk.THRID"],
			phrId : this.exContext.ids.phrId,
			empiId : this.exContext.ids.empiId,
			highRiskType: this.exContext.args.highRiskType,
			screeningId : this.exContext.args.screeningId,
			highRiskSource : this.exContext.args.highRiskSource
		};
		return body;
	},
	loadData : function(){
		chis.application.tr.script.highRisk.TumourHighRiskBaseModule.superclass.loadData.call(this);
		Ext.apply(this.form.exContext,this.exContext);
		Ext.apply(this.tcrList.exContext,this.exContext);
	},
	onSaveAfter : function(entryName,op,json,data){
		this.fireEvent("refreshData","all");
		this.fireEvent("save",entryName,op,json,data);
		this.refreshEhrTopIcon();
	}
	
});