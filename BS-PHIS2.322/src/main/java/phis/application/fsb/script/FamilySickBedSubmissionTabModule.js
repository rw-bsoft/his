$package("phis.application.fsb.script");

$import("phis.script.TabModule");

phis.application.fsb.script.FamilySickBedSubmissionTabModule = function(cfg) {
	phis.application.fsb.script.FamilySickBedSubmissionTabModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("tabchange", this.afterOpen, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedSubmissionTabModule,
		phis.script.TabModule, {
			afterOpen : function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				var m = this.midiModules[newTab.id];
				if (m) {
					if(this.ZYH){
					m.ZYH=this.ZYH;
					}
					m.doRefresh();
				}
			},
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
							if(this.ZYH){
							m.ZYH=this.ZYH;
							}
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							m.opener = this;
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							m.on("cancel", this.onCancel, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			},
			onSuperRefresh:function(){
			this.fireEvent("save",this)
			},
			onCancel:function(){
			this.getWin().hide();
			}
		});