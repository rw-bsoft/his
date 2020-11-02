$package("phis.application.fsb.script")
$import("phis.script.TabModule", "phis.script.rmi.miniJsonRequestSync")

phis.application.fsb.script.FamilySickBedCostsListTabModule = function(cfg) {
	this.width = 1100
	this.height = 600
	this.showButtonOnTop = true
	this.requestData = {};
	phis.application.fsb.script.FamilySickBedCostsListTabModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedCostsListTabModule, phis.script.TabModule, {
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
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
							if(this.requestData.cnd){
							 	this.refresh();
							 }
						}, this])
	},
	refresh : function() {
		var openBy = this.openBy ? this.openBy : '';
		if (this.midiModules['HospitalCostsListDetailedFormatTab' + openBy]) {
			this.midiModules['HospitalCostsListDetailedFormatTab' + openBy].requestData.cnd = this.requestData.cnd;
			this.midiModules['HospitalCostsListDetailedFormatTab' + openBy]
					.refresh();
		}
		if (this.midiModules['HospitalCostsListSumFormatTab' + openBy]) {
			this.midiModules['HospitalCostsListSumFormatTab' + openBy].requestData.cnd = this.requestData.cnd;
			this.midiModules['HospitalCostsListSumFormatTab' + openBy]
					.refresh();
		}
		if (this.midiModules['HospitalCostsListDoctorFormatTab' + openBy]) {
			this.midiModules['HospitalCostsListDoctorFormatTab' + openBy].requestData.cnd = this.requestData.cnd;
			this.midiModules['HospitalCostsListDoctorFormatTab' + openBy]
					.refresh();
		}
	}
})
