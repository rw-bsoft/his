$package("chis.application.def.script.brain");

$import("chis.script.BizTabModule","chis.script.util.helper.Helper");

chis.application.def.script.brain.BrainTrainingEvaluateModule = function(cfg) {
	this.autoLoadData = false;
	this.exContext = cfg.exContext;
	chis.application.def.script.brain.BrainTrainingEvaluateModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 80;
	this.itemHeight = 400;
};
Ext.extend(chis.application.def.script.brain.BrainTrainingEvaluateModule, chis.script.BizTabModule, {
	initPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width || 830,
					height : this.height || 400,
					items : [{
								layout : "fit",
								split : true,
								collapsible : true,
								title : '',
								region : 'west',
								width : this.itemWidth || this.width,
                                height : this.itemHeight || this.height,
								items : this.getGrid()
							}, {
								layout : "fit",
								border : true,
								frame : true,
								split : true,
								title : '',
								region : 'center',
//								width : 670,
//								height : 400,
								items : this.getTabPanel()
							}]
				});
		this.panel = panel
		return panel;
	},
	getGrid : function() {
		$import("chis.application.def.script.brain.BrainTrainingEvaluateList")
		var module = this.midiModules["BrainTrainingEvaluateList"]
		var cfg = {}
		cfg.autoLoadSchema = false
		cfg.showButtonOnTop = true
		cfg.autoLoadData = false
		cfg.isCombined = true
		cfg.disablePagingTbr = true
		cfg.mainApp = this.mainApp
		cfg.height = 230
		cfg.exContext = this.exContext
		if (!module) {
			module = new chis.application.def.script.brain.BrainTrainingEvaluateList(cfg)
			this.midiModules["BrainTrainingEvaluateList"] = module
			module.on("loadData", this.onLoadData, this)
			module.on("rowclick", this.onRowClick, this)
			var grid = module.initPanel()
			grid.border = false
			grid.frame = false
			this.grid = grid
			return grid
		}
	},
	getTabPanel : function() {
		var tabItems = []
		tabItems.push({
					layout : "fit",
					title : "训练评估",
					name : "BrainTrainingEvaluateForm"
				});
		tabItems.push({
					layout : "fit",
					title : "总结",
					name : "BrainMiddleEvaluateForm"
				})
		var cfg = {
                    title : " ",
                    border : false,
                    width : this.width || 670,
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
        var tab = new Ext.TabPanel(cfg);
		tab.on("tabchange", this.onTabChange, this)
		this.tab = tab;
		return tab;

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
		if (newTab.name == "BrainTrainingEvaluateForm") {
			p = this.getBrainTrainingEvaluateForm()
		} else if (newTab.name == "BrainMiddleEvaluateForm") {
			p = this.geBrainMiddleEvaluateForm()
		}
		var m = this.midiModules[newTab.name]
		newTab.add(p)
		newTab.__inited = true
		this.tab.doLayout();
		newTab.__actived = true;
		if (newTab.name == "BrainTrainingEvaluateForm") {
			return
		}
		m.loadData()
	},
	getBrainTrainingEvaluateForm : function() {
		var module = this.midiModules["BrainTrainingEvaluateForm"]
		if (!module) {
			$import("chis.application.def.script.brain.BrainTrainingEvaluateForm")
			var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF02_1_1_2")
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
			module = new chis.application.def.script.brain.BrainTrainingEvaluateForm(cfg)
			module.on("create", this.onCreate, this)
			module.on("save",this.onSave,this)
			module.on("loadData",this.onFormLoadData,this)
			this.midiModules["BrainTrainingEvaluateForm"] = module
			var form = module.initPanel()
			return form
		}
	},
	geBrainMiddleEvaluateForm : function() {
		var module = this.midiModules["BrainMiddleEvaluateForm"]
		if (!module) {
			$import("chis.application.def.script.brain.BrainMiddleEvaluateForm")
			var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF02_1_1_3")
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
			module = new chis.application.def.script.brain.BrainMiddleEvaluateForm(cfg)
			module.on("create",this.onMECreate,this)
			this.midiModules["BrainMiddleEvaluateForm"] = module
			var form = module.initPanel()
			return form
		}
	},
	onSave:function(entryName,op,json,data){
		this.exContext.selectedId = data.id
		this.op = "update"
		this.midiModules["BrainTrainingEvaluateList"].loadData()
		
		if(op == "create" && data.evaluateStage != "1"){
			Ext.Msg.show({
					title : '提示信息',
					msg : '是否需要重新创建计划?',
					modal : true,
					minWidth : 300,
					maxWidth : 600,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						data.btn = btn
						this.fireEvent("saveEvaluate",entryName,op,json,data)
						return
					},
					scope : this
				})
		}else{
			this.fireEvent("saveEvaluate",entryName,op,json,data)
		}
	}
	,
	onBeforeSave:function(entryName, op, saveData){
		var store = this.midiModules["BrainTrainingEvaluateList"].store
		var lastVisitDate = ""
		if(store.getCount()>0){
			if(op=="create"){
				lastVisitDate = Date.parseDate(store.getAt(store.getCount()-1).get("visitDate"), "Y-m-d")
			}else{
				if(store.getCount()>1){
					lastVisitDate = Date.parseDate(store.getAt(store.getCount()-2).get("visitDate"), "Y-m-d")
				}else{
					return true
				}
			}
			var date = saveData.visitDate
			if(chis.script.util.helper.Helper.getAgeMonths(lastVisitDate, date) <2){
				Ext.Msg.alert("消息","距离上次评估未满2个月")
				return false
			}
		}
	}
	,
	onMECreate:function(){
		var firstScore = this.midiModules["BrainTrainingEvaluateList"].store.getAt(0).get("score")
		var secondScore = this.exContext.r1.get("score")
		this.midiModules["BrainMiddleEvaluateForm"].form.getForm().findField("evaluateDate").setValue(this.exContext.r1.get("visitDate"))	
		this.midiModules["BrainMiddleEvaluateForm"].form.getForm().findField("firstScore").setValue(firstScore)
		this.midiModules["BrainMiddleEvaluateForm"].form.getForm().findField("secondScore").setValue(secondScore)
		this.midiModules["BrainMiddleEvaluateForm"].onCalculateScore()
	}
	,
	onCreate : function() {
		var store = this.midiModules["BrainTrainingEvaluateList"].store
		if(store.getCount()>0){
			var date = new Date(store.getAt(store.getCount()-1).get("visitDate"))
			var lastVisitDate = new Date(date.getFullYear(),date.getMonth()+2,date.getDate() )
			this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("visitDate").setMinValue(lastVisitDate)
		}else{
			this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("visitDate").setMinValue(null)
		}
		this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("visitDate").validate()
		this.midiModules["BrainTrainingEvaluateForm"].changeButtonState(false,0)
		this.midiModules["BrainTrainingEvaluateForm"].doNew()
		this.selectedIndex = this.midiModules["BrainTrainingEvaluateList"].store.getCount()
		this.midiModules["BrainTrainingEvaluateList"].selectedIndex = this.selectedIndex
		this.midiModules["BrainTrainingEvaluateForm"].initDataId = null
		var evaluateStage = {}
		if (this.midiModules["BrainTrainingEvaluateList"].store.getCount() == 0) {
			evaluateStage.key = "1"
			evaluateStage.text = "初次评估"
		} else {
			evaluateStage.key = "2"
			evaluateStage.text = "不定期评估"
		}
		this.tab.items.itemAt(1).disable()
		this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("evaluateStage").setValue(evaluateStage)
		if(this.midiModules["BrainMiddleEvaluateForm"]){
			this.midiModules["BrainMiddleEvaluateForm"].doNew()
			this.midiModules["BrainMiddleEvaluateForm"].initDataId = null
		}
	},
	loadData : function() {
		this.refreshExcontext()
		this.midiModules["BrainTrainingEvaluateList"].loadData()
	},
	onLoadData : function(store) {
		if (store.getCount() == 0) {
			this.onCreate()
			return
		} 
		if(this.op == "create"){
			this.onCreate()
			this.op = null
			return
		}
		var index = this.grid.store.find("id", this.exContext.selectedId);
		if(index < 0){
			index = 0
		}
		var r = store.getAt(index)
		this.process(r,index)
	},
	onRowClick : function(grid, index, e) {
		var r = grid.store.getAt(index)
		this.process(r,index)
	},
	process : function(r, index) {
		this.midiModules["BrainTrainingEvaluateList"].selectedIndex = index
		this.tab.enable()
		this.clearAllActived()
		this.exContext.r1 = r
		this.selectedIndex = index
		
		var store = this.midiModules["BrainTrainingEvaluateList"].store
		var selectedIndex = this.midiModules["BrainTrainingEvaluateList"].selectedIndex
		if(store.getCount() == 0){
			this.onCreate()
			return
		}else{
			if(store.getCount()> 1 && selectedIndex == store.getCount()-1){
				var date = new Date(store.getAt(store.getCount()-2).get("visitDate"))
				var lastVisitDate = new Date(date.getFullYear(),date.getMonth()+1,date.getDate() )
				this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("visitDate").setMinValue(lastVisitDate)
			}else{
				this.midiModules["BrainTrainingEvaluateForm"].form.getForm().findField("visitDate").setMinValue(null)
			}
		}
		if(r.get("evaluateStage")== "1"){
			this.tab.items.itemAt(1).disable()
		}else{
			this.tab.items.itemAt(1).enable()
		}
		this.activeModule(0)
	}
	,
	onFormLoadData:function(){
		if(this.selectedIndex != this.grid.store.getCount() - 1){
			this.midiModules["BrainTrainingEvaluateForm"].changeButtonState(true,0)
			this.exContext.r1.data.middleEvaluate = false
		}else{
			if(this.exContext.control.update == true){
				this.midiModules["BrainTrainingEvaluateForm"].changeButtonState(false,0)
				this.exContext.r1.data.middleEvaluate = true
			}else{
				this.midiModules["BrainTrainingEvaluateForm"].changeButtonState(true,0)
				this.exContext.r1.data.middleEvaluate = false
			}
		}
	}
});
