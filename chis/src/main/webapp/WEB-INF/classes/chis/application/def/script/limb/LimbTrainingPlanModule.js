$package("chis.application.def.script.limb");

$import("chis.script.BizTabModule");

chis.application.def.script.limb.LimbTrainingPlanModule = function(cfg) {
	this.autoLoadData = false;
	this.exContext = cfg.exContext;
	chis.application.def.script.limb.LimbTrainingPlanModule.superclass.constructor.apply(this,
			[cfg]);
};
Ext.extend(chis.application.def.script.limb.LimbTrainingPlanModule, chis.script.BizTabModule, {
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
								items : this.getLimbTrainingPlanList()
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
								items : this.getLimbTrainingRecordList()
							}]
				});
		return panel
	},
	getLimbTrainingPlanList : function() {
		$import("chis.application.def.script.limb.LimbTrainingPlanList")
		var module = this.midiModules["LimbTrainingPlanList"]
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
			module = new chis.application.def.script.limb.LimbTrainingPlanList(cfg)
			this.midiModules["LimbTrainingPlanList"] = module
			module.on("loadData", this.onLoadData, this)
			module.on("rowclick", this.onRowClick, this)
			var list = module.initPanel()
			list.border = false
			list.frame = false
			this.list = list
			return list
		}
	},
	getLimbTrainingRecordList : function() {
		$import("chis.application.def.script.limb.LimbTrainingRecordList")
		var module = this.midiModules["LimbTrainingRecordList"]
		var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF01_1_1_5")
		cfg.autoLoadSchema = false
		cfg.showButtonOnTop = true
		cfg.autoLoadData = false
		cfg.isCombined = true
		cfg.disablePagingTbr = true
		cfg.mainApp = this.mainApp
		cfg.exContext = this.exContext
		if (!module) {
			module = new chis.application.def.script.limb.LimbTrainingRecordList(cfg)
			module.on("save", this.onListSave, this)
			this.midiModules["LimbTrainingRecordList"] = module
			var grid = module.initPanel()
			grid.border = false
			grid.frame = false
			this.grid = grid
			return grid
		}
	},
	getTraingPlanForm : function() {
		var module = this.midiModules["LimbTrainingPlanForm"]
		if (!module) {
			$import("chis.application.def.script.limb.LimbTrainingPlanForm")
			var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF01_1_1_4")
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
			module = new chis.application.def.script.limb.LimbTrainingPlanForm(cfg)
			module.on("create", this.onCreate, this)
			module.on("save", this.onSave, this)
			module.on("loadDataByLocal", this.onFormLoadData, this)
			this.midiModules["LimbTrainingPlanForm"] = module
			var form = module.initPanel()
			return form
		}
	},
	onSave : function(entryName, op, json, data) {
//		if (op == "create") {
//			var r = new Ext.data.Record(data)
//			this.exContext.r2 = r
//			this.midiModules["LimbTrainingPlanList"].store.add(r)
//			this.midiModules["LimbTrainingPlanList"]
//					.selectRow(this.midiModules["LimbTrainingPlanList"].store
//							.getCount()
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
//		this.midiModules["LimbTrainingPlanList"].store.commitChanges()
		this.exContext.selectedId = data.id
		this.midiModules["LimbTrainingPlanList"].loadData()
		
		this.midiModules["LimbTrainingRecordList"].changeButtonState(false,0)
		this.midiModules["LimbTrainingRecordList"].changeButtonState(false,1)
		this.midiModules["LimbTrainingRecordList"].changeButtonState(false,2)
	},
	onCreate : function() {
		this.midiModules["LimbTrainingPlanForm"].doNew()
		this.selectedIndex = this.midiModules["LimbTrainingPlanList"].store
				.getCount()
		this.midiModules["LimbTrainingPlanList"].selectedIndex = this.selectedIndex
		this.midiModules["LimbTrainingPlanForm"].initDataId = null
		this.midiModules["LimbTrainingRecordList"].store.removeAll()
			if(this.exContext.control.update == false){
				this.midiModules["LimbTrainingPlanForm"].changeButtonState(true,0)
			}else{
				this.midiModules["LimbTrainingPlanForm"].changeButtonState(false,0)
			}
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,0)
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,1)
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,2)
	},
	onFormLoadData : function() {
		this.midiModules["LimbTrainingPlanForm"].validate()
	},
	loadData : function() {
		this.refreshExcontext()
		this.midiModules["LimbTrainingPlanList"].loadData()
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
		this.midiModules["LimbTrainingPlanList"].selectedIndex = index
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
					serviceId : "chis.defLimbService",
					serviceAction : "getTrainingPlanDataAndRecordList",
					method:"execute",
					body : {
						ids : this.exContext.ids,
						r : r.data
					}
				})
		this.midiModules["LimbTrainingPlanForm"].loadDataByLocal(result.json.body)
		this.midiModules["LimbTrainingRecordList"].loadDataByLocal(result.json.body)
		
		if(index != this.list.store.getCount()-1){
			this.midiModules["LimbTrainingPlanForm"].changeButtonState(true,0)
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,0)
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,1)
			this.midiModules["LimbTrainingRecordList"].changeButtonState(true,2)
		}else{
			if(this.exContext.control.update == true){
				this.midiModules["LimbTrainingPlanForm"].changeButtonState(false,0)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(false,0)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(false,1)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(false,2)
			}else{
				this.midiModules["LimbTrainingPlanForm"].changeButtonState(true,0)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(true,0)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(true,1)
				this.midiModules["LimbTrainingRecordList"].changeButtonState(true,2)
			}
		}
	}
});
