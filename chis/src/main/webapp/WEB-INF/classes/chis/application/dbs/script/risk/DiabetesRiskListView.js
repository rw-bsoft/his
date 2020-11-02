$package("chis.application.dbs.script.risk")

$import("chis.script.BizSimpleListView")

chis.application.dbs.script.risk.DiabetesRiskListView = function(cfg) {
//	cfg.initCnd = ['eq',['$','a.status'],['s','0']]
	chis.application.dbs.script.risk.DiabetesRiskListView.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.dbs.script.risk.DiabetesRiskListView,
		chis.script.BizSimpleListView, {
			doConfirm : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if(r == null){
					return;
				}
				var module = this.createSimpleModule("DiabetesRiskConfirmForm",
				"chis.application.dbs.DBS/DBS/D17-1");
				module.on("save",this.onSave,this)
				this.refreshExContextData(module, this.exContext);
				Ext.apply(module.exContext.args, r.data)
				module.initDataId = r.data.riskId
				module.getWin().show();
			}
			,
			doEstimate:function(){
				var r = this.grid.getSelectionModel().getSelected()
				if(r == null){
					return;
				}
				var empiId = r.get("empiId")
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['D_06']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["DiabetesRiskListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.ids.riskId = r.get("riskId")
					module.on("save", this.onSave, this)
					module.on("clearModuleData", this.onClearModuleData, this)
					this.midiModules["DiabetesRiskListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
	
			}
			,
			onClearModuleData:function(exContext){
				exContext.ids.riskId = this.grid.getSelectionModel().getSelected().get("riskId")
			}
			,
			doClose:function(){
				var r = this.grid.getSelectionModel().getSelected()
				if(r == null){
					return;
				}
				var module = this.createSimpleModule("DiabetesRiskCloseForm",
				"chis.application.dbs.DBS/DBS/D18-2");
				this.refreshExContextData(module, this.exContext);
				Ext.apply(module.exContext.args, r.data)
				module.on("save",this.onClose,this)
				module.initDataId = r.data.riskId
				module.getWin().show();
			}
			,
			onSave:function(entryName,data){
				var r = this.grid.getSelectionModel().getSelected()
				this.loadData()	
				if(data.result == 1){
					Ext.Msg.confirm("消息提示","是否进行首次高危评估",function(btn){
						if(btn == "yes"){
							if(r == null){
								return;
							}
							var empiId = r.get("empiId")
							var cfg = {}
							cfg.empiId = empiId
							cfg.initModules = ['D_06']
							cfg.closeNav = true
							cfg.mainApp = this.mainApp
							cfg.exContext = this.exContext
							var module = this.midiModules["DiabetesRiskListView_EHRView"]
							if (!module) {
								$import("chis.script.EHRView")
								module = new chis.script.EHRView(cfg)
								module.exContext.ids.riskId = r.get("riskId")
								module.on("save", this.onSave, this)
								this.midiModules["DiabetesRiskListView_EHRView"] = module
							} else {
								Ext.apply(module, cfg)
								module.exContext.ids = {}
								module.exContext.ids.empiId = empiId
								module.exContext.ids.riskId = r.get("riskId")
								module.refresh()
							}
							module.getWin().show()
						}
					},this);
				}
			}
			,
			onClose:function(entryName,op,json,data){
				if(data.effect == "2"){
					Ext.Msg.confirm("消息提示","是否新建糖尿病档案",function(btn){
						if(btn == "yes"){
							this.createDiabetesRecord(data.empiId);
						}
					},this);
				}
				this.midiModules["DiabetesRiskCloseForm"].win.hide();
				this.loadData()
			}
			,
			doVisit:function(){
				var r = this.grid.getSelectionModel().getSelected()
				if(r == null){
					return;
				}
				var empiId = r.get("empiId")
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['D_07']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["DiabetesRiskVisitListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.ids.riskId = r.get("riskId")
					module.on("save", this.onSave, this)
					this.midiModules["DiabetesRiskVisitListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.exContext.ids.riskId = r.get("riskId")
					module.refresh()
				}
				module.getWin().show()
			}
			,
			createDiabetesRecord:function(empiId){
					var cfg = {}
					cfg.empiId = empiId
					cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04']
					cfg.closeNav = true
					cfg.mainApp = this.mainApp
					cfg.exContext = this.exContext
					cfg.activeTab = 0
					var module = this.midiModules["DiabetesRecocrdListView_EHRView"]
					if (!module) {
						$import("chis.script.EHRView")
						module = new chis.script.EHRView(cfg)
//						module.on("save", this.onSave, this)
						this.midiModules["DiabetesRecocrdListView_EHRView"] = module
					} else {
						Ext.apply(module, cfg)
						module.exContext.ids = {}
						module.exContext.ids.empiId = empiId
						module.refresh()
					}
					module.getWin().show()
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.dbs.script.risk.DiabetesRiskListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var bts = this.grid.getTopToolbar().items;
				if(bts.length<8){
					return;
				}
				var status = r.get("status");
				if(status=='0'){
					bts.items[5].enable();
					bts.items[6].disable();
					bts.items[7].disable();
					bts.items[8].disable();
				}
				if(status =='1' ){
					bts.items[5].disable();
					if(r.get("firstAssessmentDate") == undefined){
						bts.items[6].enable();
						bts.items[7].disable();
						bts.items[8].disable();
					}else{
						bts.items[6].enable();
						bts.items[7].enable();
						bts.items[8].enable();
					}
				}
				if(status =='9' ){
					bts.items[5].disable();
					bts.items[6].disable();
					bts.items[7].disable();
					bts.items[8].disable();
				}
			}
		});