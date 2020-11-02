$package("phis.application.cic.script");

$import("phis.script.common", "phis.script.TabModule");

phis.application.cic.script.ClinicDiagnosisEntryQuickInputZyTab = function(cfg) {
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, phis.script.common);
	phis.application.cic.script.ClinicDiagnosisEntryQuickInputZyTab.superclass.constructor.apply(this, [cfg]);

}

Ext.extend(phis.application.cic.script.ClinicDiagnosisEntryQuickInputZyTab,
		phis.script.TabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					this.fireEvent("afterTabChange",
							this.midiModules[newTab.id]);
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
					var m = eval("new " + cls + "(cfg)")
					this.midiModules[newTab.id] = m;
					m.mainApp = this.mainApp;
					m.on("choose", function(record) {
						this.fireEvent("quickInput", newTab.id, record);
					}, this)
					var p = m.initPanel();
					m.on("quickInput", this.onQuickInput, this)
					newTab.add(p);
					newTab.__inited = true;
					this.fireEvent("afterTabChange", m);
					this.tab.doLayout();
				}, this])
			},
			onQuickInput : function(newTab, grid) {
				this.fireEvent("quickInput", newTab, grid);
			}
		});