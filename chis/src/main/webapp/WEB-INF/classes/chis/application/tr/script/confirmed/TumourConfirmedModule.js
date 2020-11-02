$package("chis.application.tr.script.confirmed")

$import("chis.script.BizCombinedModule2")

chis.application.tr.script.confirmed.TumourConfirmedModule = function(cfg){
	chis.application.tr.script.confirmed.TumourConfirmedModule.superclass.constructor.apply(this,[cfg]);
	this.layOutRegion = "north";
	this.height = 500;
	this.itemHeight = 205;
	this.itemCollapsible = false;
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.confirmed.TumourConfirmedModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.form=this.midiModules[this.actions[0].id];
		this.form.on("save",this.onSaveAfter,this);
		this.tcrList = this.midiModules[this.actions[1].id];
		return panel;
	},
	loadData : function(){
		Ext.apply(this.form.exContext,this.exContext);
		Ext.apply(this.tcrList.exContext,this.exContext);
		this.form.saveServiceId = this.exContext.args.saveServiceId;
		this.form.saveAction=this.exContext.args.saveAction;
		this.form.initDataId = null;
		if(this.exContext.args.loadAction){
			this.form.loadServiceId = this.exContext.args.loadServiceId;
			this.form.loadAction = this.exContext.args.loadAction; 
			this.form.loadData();
		}else{
			this.form.doNew();
		}
		this.tcrList.loadData();
	},
	onSaveAfter : function(entryName,op,json,data){
		this.fireEvent("refreshData","all");
		this.fireEvent("save",entryName,op,json,data);
		this.refreshEhrTopIcon();
	}
});