$package("phis.application.war.script");

$import("phis.script.TabModule");
phis.application.war.script.AdditionalProjectsSubmitTabModule = function(cfg) {
	phis.application.war.script.AdditionalProjectsSubmitTabModule.superclass.constructor
			.apply(this, [cfg]);
			this.on("tabchange",this.afterTabChange,this)
},

Ext.extend(phis.application.war.script.AdditionalProjectsSubmitTabModule,
		phis.script.TabModule, {
			afterTabChange:function(tabPanel, newTab, curTab){
			 	this.midiModules[newTab.id].doRefresh()
			 }
		});