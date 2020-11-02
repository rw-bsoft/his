$package("phis.application.hph.script")

$import("phis.script.TabModule")

phis.application.hph.script.HospitalPharmacyMedicineBack = function(cfg) {
	this.records=[];
	phis.application.hph.script.HospitalPharmacyMedicineBack.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyMedicineBack,
		phis.script.TabModule, {
			showRecord : function(r) {
				this.records = r;
				if (this.midiModules["HZTYTab"]) {
					this.moduleOperation("loadData", this.midiModules["HZTYTab"],
							r);
				}
				if (this.midiModules["BRTYTab"]) {
					this.moduleOperation("loadData", this.midiModules["BRTYTab"],
							r);
				}
			},
			moduleOperation : function(op, module) {
				var viewType = module.viewType;
				if (op == "loadData") {
					if(!this.fyfs||!this.tjbq){
//					this.doNew();	
					return;}
					if (viewType == "HZTYTab") {
					module.requestData.cnd=['and',['eq',['$','a.YFSB'],['s',this.YF]],module.cnds];
					module.requestData.initCnd=['and',['eq',['$','a.YFSB'],['s',this.YF]],module.cnds];
					}else if(viewType == "BRTYTab"){
					module.requestData.cnd=['and',['eq',['$','a.YFSB'],['s',this.YF]],module.cnds];
					module.requestData.initCnd=['and',['eq',['$','a.YFSB'],['s',this.YF]],module.cnds];
					}
					module.clearSelect();
					module.loadData();
				}
			},
			doNew : function() {
				this.records = [];
				if (this.midiModules["HZTYTab"]) {
					this.midiModules["HZTYTab"].doNew();
				}
				if (this.midiModules["BRTYTab"]) {
					this.midiModules["BRTYTab"].doNew();
				}
			},
			
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					this.moduleOperation("loadData", this.midiModules[newTab.id], this.records);
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
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									newTab.add(p);
									newTab.__inited = true
									this.tab.doLayout();
									if(newTab.id=="BRTYTab"){
									this.moduleOperation("loadData", this.midiModules[newTab.id], this.records);
									}
								}, this])
			},
			//获取选中记录
			getRecords:function(){
			var t=this.tab.getActiveTab();
			var module=this.midiModules[t.id];
			var record= module.getSelectedRecords();
			var ret=new Array();
			for(var i=0;i<record.length;i++){
			ret.push(record[i].json);
			}
			return ret;
			}
		});