$package("chis.application.def.script.limb");

$import("chis.script.BizTabModule");

chis.application.def.script.limb.LimbDefModule = function(cfg) {
	this.autoLoadData = false;
	chis.application.def.script.limb.LimbDefModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 160;
	this.itemHeight = 400;
};
Ext.extend(chis.application.def.script.limb.LimbDefModule, chis.script.BizTabModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
                            height : this.height,
							items : [{
										layout : "fit",
										collapsible : true,
										title : '',
										region : 'west',
										width : this.itemWidth || this.width,
                                        height : this.itemHeight || this.height,
										items : this.getLimbDeformityRecordList()
									}, {
										layout : "fit",
										title : '',
										region : 'center',
//										width : 800,
//										height : 450,
										frame : true,
										items : this.getTabModule()
									}]
						});
				return panel
			},
			getLimbDeformityRecordList : function() {
				$import("chis.application.def.script.limb.LimbDeformityRecordList")
				var module = this.midiModules["LimbDeformityRecordList"]
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.showButtonOnTop = true
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.disablePagingTbr = true
				cfg.mainApp = this.mainApp
				cfg.height = 350
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.def.script.limb.LimbDeformityRecordList(cfg)
					this.midiModules["LimbDeformityRecordList"] = module
					module.on("loadData", this.onLoadData, this)
					module.on("rowclick", this.onRowClick, this)
					var grid = module.initPanel()
					grid.border = false
					grid.frame = false
					this.grid = grid
					return grid
				}
			},
			getLimbDeformityRecordForm : function() {
				var module = this.midiModules["LimbDeformityRecordForm"]
				if (!module) {
					$import("chis.application.def.script.limb.LimbDeformityRecordForm")
					var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF01_1_1_1")
					cfg.isCombined = true
					cfg.autoLoadSchema = false
					cfg.autoLoadData = false
					cfg.showButtonOnTop = true
					cfg.mainApp = this.mainApp
					cfg.colCount = 3
					cfg.autoFieldWidth = false
					cfg.fldDefaultWidth = 150
					cfg.labelWidth = 120
					cfg.exContext = this.exContext
					module = new chis.application.def.script.limb.LimbDeformityRecordForm(cfg)
					module.on("save", this.onSave, this)
					module.on("create", this.onCreate, this)
					module.on("close", this.onClose, this)
					this.midiModules["LimbDeformityRecordForm"] = module
					var form = module.initPanel()
					this.form = form
					return form
				}
			},
			getTabModule : function() {
				var tabItems = []
				tabItems.push({
							layout : "fit",
							title : "训练登记",
							name : "LimbDeformityRecordForm"
						});
				tabItems.push({
							layout : "fit",
							title : "训练评估",
							name : "LimbTrainingEvaluateModule"
						})
				tabItems.push({
							layout : "fit",
							title : "训练计划与记录",
							name : "LimbTrainingPlanModule"
						});
			     var cfg = {
                    title : " ",
                    border : false,
                    width : this.width,
                    activeTab : 0,
                    frame : true,
                    autoHeight : true,
                    defaults : {
                        border : false,
                        autoHeight : true,
                        autoWidth : true
                    },
                    items : tabItems
                };
                if (this.isAutoScroll) {
                    delete cfg.defaults;
                    delete cfg.autoHeight;
                    delete cfg.width;
                    cfg.frame = true;
                    cfg.defaults = {
                        border : false
                    };
                }
                var mainTab = new Ext.TabPanel(cfg);
				mainTab.on("tabchange", this.onTabChange, this)
				this.tab = mainTab;
				return mainTab;
			},
			onTabChange : function(tabPanel, newTab, oldTab) {
				if (newTab.__actived) {
					return;
				}

				if (newTab.__inited) {
					this.midiModules[newTab.name].exContext = this.exContext
					this.midiModules[newTab.name].loadData()
					newTab.__actived = true;
					return;
				}
				var p = {}
				if (newTab.name == "LimbDeformityRecordForm") {
					p = this.getLimbDeformityRecordForm()
				} else if (newTab.name == "LimbTrainingEvaluateModule") {
					p = this.getLimbTrainingEvaluateModulePanel()
				} else if (newTab.name == "LimbTrainingPlanModule") {
					p = this.getLimbTrainingPlanModulePanel()
				}
				var m = this.midiModules[newTab.name]
				newTab.add(p)
				newTab.__inited = true
				this.tab.doLayout();
				newTab.__actived = true;
				if (newTab.name == "LimbDeformityRecordForm") {
					return
				}
				m.loadData()
			},
			getLimbTrainingEvaluateModulePanel : function() {
				var module = this.midiModules["LimbTrainingEvaluateModule"]
				if (!module) {
					$import("chis.application.def.script.limb.LimbTrainingEvaluateModule")
					var cfg = {}
					cfg.mainApp = this.mainApp
					cfg.colCount = 3
					cfg.autoFieldWidth = false
					cfg.fldDefaultWidth = 150
					cfg.labelWidth = 120
					cfg.exContext = this.exContext
					cfg.op = this.evaluateModuleOp
					cfg.isAutoScroll = true;
					cfg.width = this.getFormWidth();
					cfg.height = this.getFormHeight()-10
					if(this.showButtonOnTop){
					   cfg.height = cfg.height - 17
					}
					module = new chis.application.def.script.limb.LimbTrainingEvaluateModule(cfg)
					module.on("saveEvaluate", this.onSaveEvaluate, this)
					this.midiModules["LimbTrainingEvaluateModule"] = module
					var p = module.initPanel()
					return p
				}
			},
			getLimbTrainingPlanModulePanel : function() {
				var module = this.midiModules["LimbTrainingPlanModule"]
				if (!module) {
					$import("chis.application.def.script.limb.LimbTrainingPlanModule")
					var cfg = {}
					cfg.isCombined = true
					cfg.autoLoadSchema = false
					cfg.autoLoadData = false
					cfg.showButtonOnTop = true
					cfg.mainApp = this.mainApp
					cfg.colCount = 3
					cfg.autoFieldWidth = false
					cfg.fldDefaultWidth = 150
					cfg.labelWidth = 120
					cfg.exContext = this.exContext
					cfg.op = this.planModuleOp
					module = new chis.application.def.script.limb.LimbTrainingPlanModule(cfg)
					module.on("save", this.onSaveTraining, this)
					this.midiModules["LimbTrainingPlanModule"] = module
					var p = module.initPanel()
					return p
				}
			},
			onCreate : function() {
				this.tab.items.itemAt(1).disable()
				this.tab.items.itemAt(2).disable()
			},
			onLoadData : function(store) {
				if (store.getCount() == 0) {
					this.midiModules["LimbDeformityRecordForm"].doInitialize()
					this.tab.items.item(1).disable()
					this.tab.items.item(2).disable()
					return
				}
				var index = this.grid.store.find("id",this.exContext.selectedId);
				if (index < 0) {
					index = 0
				}
				var r = store.getAt(index)
				this.process(r, index)
			},
			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index)
				this.process(r)
			},
			process : function(r, index) {
				this.clearAllActived()
				this.midiModules["LimbDeformityRecordList"].selectedIndex = index
				this.exContext.selectedId = r.get("id")
				this.tab.enable()
				this.exContext.control = r.data["_actions"]
				if (this.exContext.specialAvtiveTab) {
					this.exContext.control = {
						update : true,
						create : true
					}
				}
				this.exContext.r = r
				
				this.tab.items.itemAt(1).enable()
				if(r.get("trainingEvaluateCount")>0){
					this.tab.items.itemAt(2).enable()
				}else{
					this.tab.items.itemAt(2).disable()
				}
				this.activeModule(this.exContext.specialAvtiveTab||0)
			},
			loadData : function() {
				this.tab.setActiveTab(0)
				this.tab.enable()
				this.refreshExcontext()
				this.midiModules["LimbDeformityRecordList"].loadData()
			},
			onSaveEvaluate : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data)
				this.tab.items.itemAt(2).enable()
				if (data.btn == "yes") {
					this.planModuleOp = "create"
					if(this.midiModules["LimbTrainingPlanModule"]){
						this.midiModules["LimbTrainingPlanModule"].op = this.planModuleOp
					}
					this.tab.activate(2)
				} 
			},
			onClose : function(entryName, op, json, data) {
				this.fireEvent("refreshEhrView", "DEF_01");
				this.fireEvent("save", entryName, op, json, data);
				this.exContext.selectedId = data.defId;
			},
			onSave : function(entryName, op, json, data) {
				this.fireEvent("refreshEhrView", "DEF_01");
				this.exContext.selectedId = data.id
				if(op == "create"){
					Ext.Msg.show({
					title : '提示信息',
					msg : '是否需要进行训练评估?',
					modal : true,
					minWidth : 300,
					maxWidth : 600,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if(btn == "yes"){
							this.evaluateModuleOp = "create"
							if(this.midiModules["LimbTrainingEvaluateModule"]){
								this.midiModules["LimbTrainingEvaluateModule"].op = this.evaluateModuleOp
							}
							this.tab.activate(1)
						}
					},
					scope : this
				});
				this.fireEvent("save", entryName, op, json, data)
				}
				this.refreshEhrTopIcon();
			}
					
		});
