$package("phis.application.hos.script")

$import("phis.script.TabModule")

phis.application.hos.script.HospitalDebtDuePromptModuel = function(cfg) {
	
	this.radioGroupValue = [];//用于放置用户选择的是否催款的单据 序号
	phis.application.hos.script.HospitalDebtDuePromptModuel.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.hos.script.HospitalDebtDuePromptModuel, phis.script.TabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				var flag = false;
				if(newTab.exCfg.id == "promptTab"){
					if(!this.midiModules["promptTab"]){
						flag = true;
					}else{
						this.sendToCKData();
					}
				}
				
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
							
						    if(flag){
						    	this.sendToCKData();
						    }
						}, this])

			},
			sendToCKData:function(){
					this.radioGroupValue = [];
					var store = this.midiModules["debtDueTab"].store;
					var grid = this.midiModules["debtDueTab"].grid;
					var Ltype = this.midiModules["debtDueTab"].type;
					var LdicValue = this.midiModules["debtDueTab"].dicValue;
					var LNodetext = this.midiModules["debtDueTab"].Nodetext;
//					var CKJE = this.midiModules["debtDueTab"].CKJE.getValue();
					for(i=0;i < store.getCount();i++){
						var record = store.getAt(i);
						var zyh = record.data.ZYH;//获得住院号
						var CKJE = record.data.CKJE;//获得催款金额
						var el = Ext.fly(grid.getView().getCell(i,1));//将HTML转换成Element
						//通过selector选择器获得radio的值
						var flag = el.child("input[dd='22']").dom.checked;//如果为true,则在催款清单里显示催款单，否则，不显示
						
						var ck = {'zyh':zyh,'flag':flag,'CKJE':CKJE};
						
						this.radioGroupValue.push(ck);
						
					}
					this.midiModules["promptTab"].doActivePanel(this.radioGroupValue,Ltype,LdicValue,LNodetext);
			}
})