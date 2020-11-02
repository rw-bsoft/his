$package("phis.application.fsb.script")

$import("phis.script.TabModule")

phis.application.fsb.script.FamilySickBedBackMedicineUnderTabModule = function(cfg) {
	this.records=[];
	phis.application.fsb.script.FamilySickBedBackMedicineUnderTabModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedBackMedicineUnderTabModule,
		phis.script.TabModule, {
			showRecord : function(r) {
				this.records = r;
				if (this.midiModules["tycl"]) {
					this.moduleOperation("loadData", this.midiModules["tycl"],
							r);
				}
				if (this.midiModules["thbq"]) {
					this.moduleOperation("loadData", this.midiModules["thbq"],
							r);
				}
			},
			doNew : function() {
				this.records = [];
				if (this.midiModules["tycl"]) {
					this.midiModules["tycl"].doNew();
				}
				if (this.midiModules["thbq"]) {
					this.midiModules["thbq"].doNew();
				}
			},
			moduleOperation : function(op, module, record) {
				var viewType = module.viewType;
				if (op == "loadData") {
					var arr=new Array();
					if(record.length==0){
					arr.push("");
					}
						for (var i = 0; i < record.length; i++) {
							arr.push(record[i].json.ZYH);
						}
						var cnd = ['and', module.cnds,
								['in', ['$', 'a.ZYH'], arr]];
						if(record.length>0){
						cnd=['and',cnd,['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]]]
						}
						module.requestData.cnd = cnd
						module.clearSelect();
						module.loadData();
						this.fireEvent("checkTab",this.tab.getActiveTab().id)
				}
			}
			,
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
									if(newTab.id=="thbq"){
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