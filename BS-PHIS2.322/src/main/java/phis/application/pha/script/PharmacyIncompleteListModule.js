$package("phis.application.pha.script");

$import("phis.script.TabModule");

phis.application.pha.script.PharmacyIncompleteListModule = function(cfg) {
	cfg.width = 600;
	cfg.height = 400;
	cfg.activateId = 0;
	cfg.modal = true;
	phis.application.pha.script.PharmacyIncompleteListModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyIncompleteListModule,
		phis.script.TabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,
								this.midiModules[newTab.id], null);
					}
					this.tab.doLayout();
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
				$import(cls);
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)");
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									this.moduleOperation(this.op, m, null);
									m.on("save", this.onSuperFormRefresh, this);
									newTab.add(p);
									newTab.__inited = true;
								}, this])
				this.tab.doLayout();

			},
			moduleOperation : function(op, module, values) {
				var cnd=[];
				var viewType=module.viewType;
				if(viewType=="rkd"){
				cnd=['and',['eq',['$','RKPB'],['i',0]],['eq',['$','YFSB'],['l',this.mainApp['phis'].pharmacyId]]];
				}else if(viewType=="ckd"){
				cnd=['and',['eq',['$','CKPB'],['i',0]],['eq',['$','YFSB'],['l',this.mainApp['phis'].pharmacyId]]];
				}else if(viewType=="dbrkd"){
				cnd=['and',['eq',['$','CKBZ'],['i',1]],['eq',['$','RKBZ'],['i',0]],['eq',['$','TYPB'],['i',0]],['eq',['$','SQYF'],['l',this.mainApp['phis'].pharmacyId]]];
				}else if(viewType=="dbtyd"){
				cnd=['and',['eq',['$','CKBZ'],['i',0]],['eq',['$','RKBZ'],['i',1]],['eq',['$','TYPB'],['i',1]],['eq',['$','MBYF'],['l',this.mainApp['phis'].pharmacyId]]];
				}else if(viewType=="sld"){
				cnd=['and',['eq',['$','a.CKPB'],['i',1]],['eq',['$','a.LYPB'],['i',0]],['ne',['$','b.DYFS'],['i',6]],['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]],['eq',['$','a.JGID'],['s',this.mainApp['phisApp'].deptId]]];
				}else if(viewType=="tyd"){
				cnd=['and',['eq',['$','a.CKPB'],['i',0]],['eq',['$','a.LYPB'],['i',1]],['eq',['$','b.DYFS'],['i',6]],['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]],['eq',['$','a.JGID'],['s',this.mainApp['phisApp'].deptId]]];
				}
				module.requestData.cnd=cnd;
				module.loadData();
			}
		});