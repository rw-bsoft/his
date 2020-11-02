$package("phis.application.emr.script")

$import("phis.script.TabModule")
$import("phis.script.SimpleForm")

phis.application.emr.script.EMRTemperatureTableModule = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.emr.script.EMRTemperatureTableModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.emr.script.EMRTemperatureTableModule, phis.script.TabModule, {
	freshChar:null,
	onTabChange : function(tabPanel, newTab, curTab) {
		if (newTab.__inited) {
			if(newTab.id == "SMTZList"){ //每次切换都重新查询一边列表
				var cnds = "['eq',['$','ZYH'],['i',this.info.ZYH]]";
				if (this.info.ZYH) {
					this.midiModules[newTab.id].requestData.cnd = eval(cnds);
				}
				this.midiModules[newTab.id].loadData();
				if(this.midiModules["SMTZForm"]){
					this.midiModules[newTab.id].form = this.midiModules["SMTZForm"].form;
				}
			}
			this.fireEvent("tabchange", tabPanel, newTab, curTab);
			return;
		}
		var exCfg = newTab.exCfg
		var cfg = {
			showButtonOnTop : true,
			autoLoadSchema : false,
			isCombined : true
		}
		Ext.apply(cfg, exCfg);
		var ref = exCfg.ref
		if (ref) {
			var body = this.loadModuleCfg(ref);
			Ext.apply(cfg, body)
		}
		var cls = cfg.script
		if (!cls) {
			return;
		}
		if(this.readOnly){
		for(var i = 0; i < cfg.actions.length; i ++){
					if(!cfg.actions[i].properties){
						cfg.actions[i].properties={};
						}
						cfg.actions[i].properties.hide=true;
		}
		}
		if (!this.fireEvent("beforeload", cfg)) {
			return;
		}
		$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)");
							m.setMainApp(this.mainApp);
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							m.opener = this;
							this.midiModules[newTab.id] = m;
							//this.smtzForm=m;
							var p = m.initPanel();
							//this.hasLoadData=false;
							m.on("save", this.onSuperRefresh, this);
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							if(newTab.id == "SMTZList"){
								var cnds = "['eq',['$','ZYH'],['i',this.info.ZYH]]";
								if (this.info.ZYH) {
									this.midiModules[newTab.id].requestData.cnd = eval(cnds);
								}
								this.midiModules[newTab.id].loadData();
								if(this.midiModules["SMTZForm"]){
									this.midiModules[newTab.id].form = this.midiModules["SMTZForm"].form;
								}
							}
							this.fireEvent("tabchange", tabPanel, newTab,curTab);
						}, this])
	},
	onSuperRefresh:function(){
		this.freshChar.onReady();
	}
}
);