$package("phis.application.chr.script")

$import("phis.script.TabModule")

phis.application.chr.script.CaseInfoTabModule = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	phis.application.chr.script.CaseInfoTabModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.chr.script.CaseInfoTabModule, phis.script.TabModule, {
	onTabChange : function(tabPanel, newTab, curTab) {
		if (newTab.__inited) {
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
							var p = m.initPanel();
							this.hasLoadData=false;
							m.on("save", this.onSuperRefresh, this);
//							m.on("select", this.onSelectRecord, this);
							if (newTab.id == "allCase") {
								m.on("hasLoadData", this.onHasLoadData,
										this);
								m.on("recordClick", this.onRowClickOrSelect, this)
							}
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
	},
	onHasLoadData:function(){
		this.midiModules["allCase"].selectAllRecords();
		this.hasLoadData=true;
		this.fireEvent("hasLoadData", this)
	},
	initFormData:function(formData){
		var blbh = formData.BLBH;
		var jzxh = formData.JZXH;
		this.YWID2 = blbh;
		this.YWID1 = jzxh;
		this.midiModules["caseInfo"].initFormData(formData);
	},
	loadAllData : function() {
		if(this.hasLoadData){
			return;
		}
		this.midiModules["allCase"].requestData.serviceId = "phis.caseHistoryReviewService";
		this.midiModules["allCase"].requestData.serviceAction = "listAllCaseRecord";
		this.midiModules["allCase"].requestData.YWID1=this.YWID1;
		this.midiModules["allCase"].loadData();
	},
	getSelectedRecords:function(){
		return this.midiModules["allCase"].getSelectedRecords();
	},
	onRowClickOrSelect:function(){
		this.fireEvent("recordClick",this)
	}

});