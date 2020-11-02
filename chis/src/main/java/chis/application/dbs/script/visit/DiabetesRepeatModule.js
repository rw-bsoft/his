$package("chis.application.dbs.script.inquire")
$import("util.Accredit", "chis.script.BizTabModule")

chis.application.dbs.script.visit.DiabetesRepeatModule = function(cfg) {
	this.autoLoadData = false
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesRepeatVisit"
	chis.application.dbs.script.visit.DiabetesRepeatModule.superclass.constructor
			.apply(this, [cfg])
	this.width = 1000
}
Ext.extend(chis.application.dbs.script.visit.DiabetesRepeatModule,
		chis.script.BizTabModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : 960,
							height : 480,
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : 250,
										height : 400,
										items : this.getGrid()
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										height : 470,
										width : 700,
										items : this.getForm()
									}]
						});
				return panel

			},
			getForm : function() {
				$import("chis.application.dbs.script.visit.DiabetesRepeatForm")
				var module = this.midiModules["DiabetesRepeatForm"]
				var cfg = {}
				var moduleCfg = this.mainApp.taskManager
						.loadModuleCfg("chis.application.dbs.DBS/DBS/D11-3-7-2");
				Ext.apply(cfg, moduleCfg.json.body);
				Ext.apply(cfg, moduleCfg.json.body.properties);
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false
				cfg.showButtonOnTop = true
				cfg.isCombined = true
				cfg.colCount = 2
				cfg.fldDefaultWidth = 150
				cfg.autoFieldWidth = false
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.visit.DiabetesRepeatForm(cfg)
					this.midiModules["DiabetesRepeatForm"] = module
					var form = module.initPanel()
					form.border = false
					form.frame = false
					module.on("beforeSave", this.onBeforeSave, this)
					module.on("save", this.onSave, this)
					// module.on("create", this.onCreate, this)
					this.form = form
					return form;
				}
			},
			// ----------------------------------------------------------------------------form
			getGrid : function() {
				$import("chis.application.dbs.script.visit.DiabetesRepeatList")
				var module = this.midiModules["DiabetesRepeatList"]
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.showButtonOnTop = true
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.visit.DiabetesRepeatList(cfg)
					this.midiModules["DiabetesRepeatList"] = module
					module.on("loadData", this.onLoadData, this)
					var grid = module.initPanel()
					grid.border = false
					grid.frame = false
					grid.on("rowClick", this.onRowClick, this)
					// grid.on("create", this.onCreate, this)
					this.grid = grid
					return grid
				}
			},
			keyManageFunc : function(keyCode, keyName) {
				var m = this.midiModules["DiabetesRepeatForm"];
				if (!m.btnAccessKeys || !m.btnAccessKeys[keyCode]) {
					m = this.midiModules["DiabetesRepeatList"];
				}
				if (m) {
					if (m.btnAccessKeys) {
						var btn = m.btnAccessKeys[keyCode];
						if (btn && btn.disabled) {
							return;
						}
					}
					m.doAction(m.btnAccessKeys[keyCode]);
				}
			},
			loadData : function() {
				this.midiModules["DiabetesRepeatList"].selectedIndex = 0
				this.refreshExcontext()
				this.midiModules["DiabetesRepeatList"].loadData()
			},
			onLoadData : function(store) {
				if (store.getCount() == 0) {
					this.midiModules["DiabetesRepeatForm"].doNew()
					return
				}
				var r = store
						.getAt(this.midiModules["DiabetesRepeatList"].selectedIndex)
				this.process(r,
						this.midiModules["DiabetesRepeatList"].selectedIndex)
			},
			onSave : function(entryName, op, json, data) {
				if (op == "create") {
					var record = new Ext.data.Record(data)
					this.midiModules["DiabetesRepeatList"].store.add(record);
				} else {
					var recordId = data.recordId
					var index = this.grid.store.find("recordId", recordId);
					var r = this.grid.store.getAt(index);
					Ext.apply(r.data, data)
					r.commit()
				}
			},
			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index)
				this.process(r)
			},
			process : function(r, index) {
				if (!r) {
					return
				}
				this.exContext.r = r
				this.midiModules["DiabetesRepeatForm"].exContext = this.exContext
				this.midiModules["DiabetesRepeatForm"].loadData();
			},
			onBeforeSave : function(entryName, op, saveData) {
				saveData.visitId=this.exContext.args.r.data.visitId;
				if(op!='create')
					saveData.recordId=this.exContext.r.data.recordId;
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								constrain : true,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			},
			onWinShow : function() {
				this.loadData()
			}
		});