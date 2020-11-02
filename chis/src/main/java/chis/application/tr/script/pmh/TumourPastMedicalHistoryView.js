$package("chis.application.tr.script.pmh");

$import("chis.script.BizTabModule");

chis.application.tr.script.pmh.TumourPastMedicalHistoryView = function(cfg){
	cfg.autoLoadData = false;
	chis.application.tr.script.pmh.TumourPastMedicalHistoryView.superclass.constructor.apply(this,[cfg]);
	this.on("loadModule", this.onLoadModule, this);
	this.width=1000;
	this.height=600;
	this.title="既往情况"
}

Ext.extend(chis.application.tr.script.pmh.TumourPastMedicalHistoryView,chis.script.BizTabModule,{
	loadData : function() {
		this.activeModule(this.activeTabIndex || 0);
		this.PMHSave = false;
	},
	onLoadModule : function(moduleName,module){
		Ext.apply(module.exContext,this.exContext);
		if(moduleName == this.actions[0].id){ 
			this.THQModule = module;
		}
		if(moduleName == this.actions[1].id){ 
			this.THCList = module;
		}
		if(moduleName == this.actions[2].id){ 
			this.TMHModule = module;
			this.TMHModule.on("PMHSave",this.onPMHSave,this);
		}
	},
	onPMHSave : function(entryName,op,json,data){
		this.fireEvent("PMHSave",entryName,op,json,data);
		this.PMHSave = true;
	}
});