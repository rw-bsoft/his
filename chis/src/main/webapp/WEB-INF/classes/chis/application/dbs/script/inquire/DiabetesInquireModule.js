$package("chis.application.dbs.script.inquire")
$import("util.Accredit", "chis.script.BizTabModule")

chis.application.dbs.script.inquire.DiabetesInquireModule = function(cfg) {
	this.autoLoadData = false
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesInquire"
	chis.application.dbs.script.inquire.DiabetesInquireModule.superclass.constructor
			.apply(this, [cfg])
	this.width = 1000
}
Ext.extend(chis.application.dbs.script.inquire.DiabetesInquireModule,
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
				$import("chis.application.dbs.script.inquire.DiabetesInquireForm")
				var module = this.midiModules["DiabetesInquireForm"]
				var cfg = {}
				var moduleCfg = this.mainApp.taskManager
						.loadModuleCfg("chis.application.dbs.DBS/DBS/D11-5-1");
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
					module = new chis.application.dbs.script.inquire.DiabetesInquireForm(cfg)
					this.midiModules["DiabetesInquireForm"] = module
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
				$import("chis.application.dbs.script.inquire.DiabetesInquireList")
				var module = this.midiModules["DiabetesInquireList"]
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.showButtonOnTop = true
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.inquire.DiabetesInquireList(cfg)
					this.midiModules["DiabetesInquireList"] = module
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
				var m = this.midiModules["DiabetesInquireForm"];
				if (!m.btnAccessKeys || !m.btnAccessKeys[keyCode]) {
					m = this.midiModules["DiabetesInquireList"];
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
				this.midiModules["DiabetesInquireList"].selectedIndex = 0
				this.refreshExcontext()
				this.midiModules["DiabetesInquireList"].loadData()
			},
			onLoadData : function(store) {
				if (store.getCount() == 0) {
					this.midiModules["DiabetesInquireForm"].doNew()
					return
				}
				var r = store
						.getAt(this.midiModules["DiabetesInquireList"].selectedIndex)
				this.process(r,
						this.midiModules["DiabetesInquireList"].selectedIndex)
			},
			onSave : function(entryName, op, json, data) {
				if (op == "create") {
					var record = new Ext.data.Record(data)
					this.midiModules["DiabetesInquireList"].store.add(record);
				} else {
					var inquireId = data.inquireId
					var index = this.grid.store.find("inquireId", inquireId);
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
				this.midiModules["DiabetesInquireForm"].exContext = this.exContext
				this.midiModules["DiabetesInquireForm"].loadData()
			},
			checkCanCreate : function(inquireDate) {
				for (var i = 0; i < this.midiModules["DiabetesInquireList"].store
						.getCount(); i++) {
					var r = this.midiModules["DiabetesInquireList"].store
							.getAt(i);
					if (inquireDate.format("Y-m-d") == r.get("inquireDate")) {
						Ext.Msg.alert("提示", inquireDate.format("Y-m-d")
										+ "已有询问记录，不需再重新录入。");
						return false;
					}
				}
				return true;
			},
			onBeforeSave : function(entryName, op, saveData) {
				var inquireDate = this.midiModules["DiabetesInquireForm"].form
						.getForm().findField("inquireDate").getValue()
				if (op == "create") {
					return this.checkCanCreate(inquireDate);
				}
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