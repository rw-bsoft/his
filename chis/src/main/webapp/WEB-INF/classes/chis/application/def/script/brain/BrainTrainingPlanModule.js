$package("chis.application.def.script.brain");

$import("chis.script.BizTabModule");

chis.application.def.script.brain.BrainTrainingPlanModule = function(cfg) {
	this.autoLoadData = false;
	this.exContext = cfg.exContext;
	chis.application.def.script.brain.BrainTrainingPlanModule.superclass.constructor.apply(this,
			[cfg]);
};
Ext.extend(chis.application.def.script.brain.BrainTrainingPlanModule, chis.script.BizTabModule, {
	initPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : 830,
					height : 400,
					items : [{
								layout : "fit",
								collapsible : true,
								title : '',
								region : 'west',
								width : 120,
								height : 400,
								items : this.getBrainTrainingPlanList()
							}, {
								layout : "fit",
								title : '',
								region : 'center',
								width : 700,
								height : 480,
								items : this.getPanel()
							}]
				});
		return panel

	},
	getPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : 700,
					height : 550,
					items : [{
								layout : "fit",
								title : '',
								region : 'north',
								height : 180,
								items : this.getTraingPlanForm()
							}, {
								layout : "fit",
								title : '',
								region : 'center',
								height : 260,
								items : this.getBrainTrainingRecordList()
							}]
				});
		return panel
	},
	getBrainTrainingPlanList : function() {
		$import("chis.application.def.script.brain.BrainTrainingPlanList")
		var module = this.midiModules["BrainTrainingPlanList"]
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
			module = new chis.application.def.script.brain.BrainTrainingPlanList(cfg)
			this.midiModules["BrainTrainingPlanList"] = module
			module.on("loadData", this.onLoadData, this)
			module.on("rowclick", this.onRowClick, this)
			var list = module.initPanel()
			list.border = false
			list.frame = false
			this.list = list
			return list
		}
	},
	getBrainTrainingRecordList : function() {
		$import("chis.application.def.script.brain.BrainTrainingRecordList")
		var module = this.midiModules["BrainTrainingRecordList"]
		var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF02_1_1_5")
		cfg.autoLoadSchema = false
		cfg.showButtonOnTop = true
		cfg.autoLoadData = false
		cfg.isCombined = true
		cfg.disablePagingTbr = true
		cfg.mainApp = this.mainApp
		cfg.exContext = this.exContext
		if (!module) {
			module = new chis.application.def.script.brain.BrainTrainingRecordList(cfg)
			module.on("save", this.onListSave, this)
			this.midiModules["BrainTrainingRecordList"] = module
			var grid = module.initPanel()
			grid.border = false
			grid.frame = false
			this.grid = grid
			return grid
		}
	},
	getTraingPlanForm : function() {
		var module = this.midiModules["BrainTrainingPlanForm"]
		if (!module) {
			$import("chis.application.def.script.brain.BrainTrainingPlanForm")
			var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF02_1_1_4")
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
			module = new chis.application.def.script.brain.BrainTrainingPlanForm(cfg)
			module.on("create", this.onCreate, this)
			module.on("save", this.onSave, this)
			module.on("loadDataByLocal", this.onFormLoadData, this)
			this.midiModules["BrainTrainingPlanForm"] = module
			var form = module.initPanel()
			return form
		}
	},
	onSave : function(entryName, op, json, data) {
//		if (op == "create") {
//			var r = new Ext.data.Record(data)
//			this.exContext.r2 = r
//			this.midiModules["BrainTrainingPlanList"].store.add(r)
//			this.midiModules["BrainTrainingPlanList"].selectRow(this.midiModules["BrainTrainingPlanList"].store.getCount()
//							- 1)
//		} else {
//			var index = this.list.store.find("id", data.id);
//			var r = this.list.store.getAt(index);
//			if (!r) {
//				return
//			}
//			Ext.apply(r.data, data)
//			r.commit();
//		}
//		this.midiModules["BrainTrainingPlanList"].store.commitChanges()
		this.exContext.selectedId = data.id
		this.midiModules["BrainTrainingPlanList"].loadData()
		
		this.midiModules["BrainTrainingRecordList"].changeButtonState(false,0)
		this.midiModules["BrainTrainingRecordList"].changeButtonState(false,1)
		this.midiModules["BrainTrainingRecordList"].changeButtonState(false,2)
	},
	onCreate : function() {
		this.midiModules["BrainTrainingPlanForm"].doNew()
		this.selectedIndex = this.midiModules["BrainTrainingPlanList"].store
				.getCount()
		this.midiModules["BrainTrainingPlanList"].selectedIndex = this.selectedIndex
		this.midiModules["BrainTrainingPlanForm"].initDataId = null
		this.midiModules["BrainTrainingRecordList"].store.removeAll()
		if(this.exContext.control.update == false){
			this.midiModules["BrainTrainingPlanForm"].changeButtonState(true,0)
		}else{
			this.midiModules["BrainTrainingPlanForm"].changeButtonState(false,0)
		}
		this.midiModules["BrainTrainingRecordList"].changeButtonState(true,0)
		this.midiModules["BrainTrainingRecordList"].changeButtonState(true,1)
		this.midiModules["BrainTrainingRecordList"].changeButtonState(true,2)
	},
	onFormLoadData : function() {
		this.midiModules["BrainTrainingPlanForm"].validate()
	},
	loadData : function() {
		this.refreshExcontext()
		this.midiModules["BrainTrainingPlanList"].loadData()
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
		var index = store.find("id", this.exContext.selectedId);
		if (index < 0) {
			index = 0
		}
		this.midiModules["BrainTrainingPlanList"].selectedIndex = index
		var r = store.getAt(index)
		this.process(r, index)
	},
	onRowClick : function(grid, index, e) {
		var r = grid.store.getAt(index)
		this.process(r, index)
	},
	process : function(r, index) {
		this.exContext.r2 = r
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.defBrainService",
					serviceAction : "getTrainingPlanDataAndRecordList",
					method:"execute",
					body : {
						ids : this.exContext.ids,
						r : r.data
					}
				})
		this.midiModules["BrainTrainingPlanForm"].loadDataByLocal(result.json.body)
		this.midiModules["BrainTrainingRecordList"].loadDataByLocal(result.json.body)
		
		if(index != this.list.store.getCount()-1){
			this.midiModules["BrainTrainingPlanForm"].changeButtonState(true,0)
			this.midiModules["BrainTrainingRecordList"].changeButtonState(true,0)
			this.midiModules["BrainTrainingRecordList"].changeButtonState(true,1)
			this.midiModules["BrainTrainingRecordList"].changeButtonState(true,2)
		}else{
			if(this.exContext.control.update == true){
				this.midiModules["BrainTrainingPlanForm"].changeButtonState(false,0)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(false,0)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(false,1)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(false,2)
			}else{
				this.midiModules["BrainTrainingPlanForm"].changeButtonState(true,0)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(true,0)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(true,1)
				this.midiModules["BrainTrainingRecordList"].changeButtonState(true,2)
			}
		}
	}
});
