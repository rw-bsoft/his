$package("phis.application.pha.script");

$import("phis.script.TabModule");

phis.application.pha.script.EssentialDrugsModule = function(cfg) {
	this.width = 1024;
	cfg.activateId = 0;
	cfg.modal = true;
	phis.application.pha.script.EssentialDrugsModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.pha.script.EssentialDrugsModule,
		phis.script.TabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (newTab.id == "txprintTab") {
						this.openTxbb(this.midiModules[newTab.id]);
					}
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
					return;
				}
				var exCfg = newTab.exCfg
				var exContext = {
					readOnly : true
				}
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var result = this.mainApp.taskManager.loadModuleCfg(ref);
					if (result.code == 200) {
						Ext.apply(cfg, result.json.body)
					}
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}
				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$import(cls);
				$require(cls, [ function() {
					var m = eval("new " + cls + "(cfg)")
					m.setMainApp(this.mainApp);
					this.midiModules[newTab.id] = m;
					var p = m.initPanel();
					if (newTab.id == "printTab") {
						m.on("loadGraph", this.onLoadGraph, this);
					}
					newTab.add(p);
					newTab.__inited = true;
				}, this ])
				this.tab.doLayout();
				this.tab.syncSize();
				if (newTab.id == "txprintTab") {
					this.openTxbb(this.midiModules[newTab.id]);
				}
			},
			onLoadGraph : function(datefrom, dateTo) {
				this.datefrom = datefrom;
				this.dateTo = dateTo;
			},
			openTxbb : function(m) {
				m.beginDate = this.datefrom
				m.endDate = this.dateTo
				m.loadDataf();
			}
		});