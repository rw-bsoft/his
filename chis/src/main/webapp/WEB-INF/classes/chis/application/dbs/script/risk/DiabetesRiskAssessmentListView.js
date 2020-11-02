$package("chis.application.dbs.script.risk")

$import("chis.script.BizSimpleListView")

chis.application.dbs.script.risk.DiabetesRiskAssessmentListView = function(cfg) {
	cfg.initCnd = ['eq',['$','a.estimateType'],['s','1']]
	chis.application.dbs.script.risk.DiabetesRiskAssessmentListView.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.dbs.script.risk.DiabetesRiskAssessmentListView,
		chis.script.BizSimpleListView, {
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
			,
			onDblClick:function(grid,index,e){
				this.doEstimate()
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
			onClose:function(entryName,op,json,data){
				if(data.effect == "2"){
					Ext.Msg.confirm("消息提示","是否新建糖尿病档案",function(btn){
						if(btn == "yes"){
							this.createDiabetesRecord(data.empiId);
						}
					},this);
					this.midiModules["DiabetesRiskCloseForm"].win.hide();
					this.loadData()
				}
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
					var module = this.midiModules["DiabetesRecordListView_EHRView"]
					if (!module) {
						$import("chis.script.EHRView")
						module = new chis.script.EHRView(cfg)
						module.on("save", this.onSave, this)
						this.midiModules["DiabetesRecordListView_EHRView"] = module
					} else {
						Ext.apply(module, cfg)
						module.exContext.ids = {}
						module.exContext.ids.empiId = empiId
						module.refresh()
					}
					module.getWin().show()
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.dbs.script.risk.DiabetesRiskAssessmentListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				
				var bts = this.grid.getTopToolbar().items;
				if(bts.items[6]){
					var status = r.get("status");
					if (status == 9) {
						bts.items[6].disable();
					} else {
						bts.items[6].enable();
					}
				}
			}
		});