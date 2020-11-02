$package("chis.application.quality.script")

$import("chis.script.BizCombinedModule2")

chis.application.quality.script.QualityCkZkBg_tnbModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.quality.script.QualityCkZkBg_tnbModule.superclass.constructor.apply(
			this, [cfg]);
   	this.itemCollapsible = false
	this.layOutRegion = "north"
	this.itemWidth = 600
	this.itemHeight = 130
	this.width = 800
	this.height = 450
}
Ext.extend(chis.application.quality.script.QualityCkZkBg_tnbModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.quality.script.QualityCkZkBg_tnbModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.list = this.midiModules[this.actions[1].id];
				return panel;
		  },doInitLoad:function(data){
		  	if(data && data!=null){
		  		var CODERNO=data["CODERNO"];
		  		this.form.doNew();
			  	this.form.initDataId=CODERNO;
			  	this.form.doInitFormLoad(CODERNO);
			  	
			  	this.list.doInitLoad(CODERNO);
        }
	   }
 })