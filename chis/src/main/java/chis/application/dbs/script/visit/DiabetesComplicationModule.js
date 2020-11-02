$package("chis.application.dbs.script.visit")

$import("app.desktop.Module", "util.dictionary.TreeDicFactory",
		"util.dictionary.DictionaryLoader")

chis.application.dbs.script.visit.DiabetesComplicationModule = function(cfg) {
	this.width = 800
	this.height = 500
	Ext.apply(cfg, chis.script.BizCommon);
	chis.application.dbs.script.visit.DiabetesComplicationModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.dbs.script.visit.DiabetesComplicationModule,
		app.desktop.Module, {
			initPanel : function() {
				if (this.panel) {
					return this.panel
				}
				var treePanel = this.getTreePanel()
				var formPanel = this.getFormPanel()
				var listPanel = this.getListPanel()
				var panel = new Ext.Panel({
							border : false,
							hideBorders : true,
							frame : true,
							layout : "border",
							width : this.width,
							height : this.height,
							items : [{
										border : false,
										frame : false,
										width : 170,
										collapsiable : true,
										layout : "fit",
										region : "west",
										items : treePanel
									}, {
										border : false,
										frame : false,
										width : 620,
										collapsiable : true,
										layout : "fit",
										region : "center",
										items : new Ext.Panel({
													border : false,
													frame : false,
													hideBorders : true,
													layout : "border",
													items : [{
																border : false,
																frame : false,
																region : "north",
																layout : "fit",
																height : 120,
																items : formPanel
															}, {
																border : false,
																frame : false,
																region : "center",
																layout : "fit",
																height : 290,
																items : listPanel
															}]
												})
									}]
						})
				this.panel = panel
				return panel
			},
			getListPanel : function() {
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.isCombined = true
				cfg.autoLoadData = false
				cfg.showButtonOnTop = true
				cfg.mainApp = this.mainApp
				cfg.actions = [{
							id : "addRecord",
							name : "增加",
							iconCls : "add"
						}, {
							id : "removeRecord",
							name : "删除",
							iconCls : "remove"
						}, {
							id : "saveRecord",
							name : "保存",
							iconCls : "save"
						}, {
							id : "cancel",
							name : "取消",
							iconCls : "common_cancel"
						}]
				var module = this.midiModules["DiabetesRecordModule_complicationList"]
				if (!module) {
					$import("chis.application.dbs.script.visit.DiabetesComplicationListView")
					module = new chis.application.dbs.script.visit.DiabetesComplicationListView(cfg)
					module.on("addRecord", this.doAddRecord, this)
					module.on("saveRecord", this.onSaveRecord, this)
					module.on("cancel", this.onWinClose, this)
					this.midiModules["DiabetesRecordModule_complicationList"] = module
					var listPanel = module.initPanel()
					listPanel.border = false
					listPanel.frame = false
					this.listPanel = listPanel
					return listPanel
				}
			},
			keyManageFunc : function(keyCode, keyName) {
				var m = this.midiModules["DiabetesRecordModule_complicationList"];
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
			getFormPanel : function() {
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.isCombined = true
				cfg.autoLoadData = false
				cfg.showButtonOnTop = false
				cfg.actions = {}
				cfg.colCount = 2
				cfg.fldDefaultWidth = 200
				cfg.autoFieldWidth = false
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["DiabetesRecordModule_complicationForm"]
				if (!module) {
					$import("chis.application.dbs.script.visit.DiabetesComplicationFormView")
					module = new chis.application.dbs.script.visit.DiabetesComplicationFormView(cfg)
					module.on("formInvalid", this.onFormInvalid, this)
					this.midiModules["DiabetesRecordModule_complicationForm"] = module
					var formPanel = module.initPanel()
					formPanel.border = false
					formPanel.frame = false
					this.formPanel = formPanel
					return formPanel
				}
			},
			getTreePanel : function() {
				var complicationTree = util.dictionary.TreeDicFactory
						.createTree({
									id : "chis.dictionary.diabetesComplication"
								})
				complicationTree.on("click", this.onTreeClick, this)
				complicationTree.on("dblclick", this.onTreeDBLClick, this)
				this.complicationTree = complicationTree
				return complicationTree
			},
			onTreeClick : function(node, e) {
				//如果是一组二组则前三项不能添加
				var groupCode=this.exContext.args.r.data.groupCode;
				if((groupCode.indexOf('01')!=-1||groupCode.indexOf('02')!=-1)&&node.attributes.key<4)
				{
					Ext.MessageBox.alert("提示", "糖尿病一组二组不能添加前三项");
					return;
				}
				//
				var complication = {}
				complication.key = node.attributes.key
				complication.text = node.attributes.text
				this.midiModules["DiabetesRecordModule_complicationForm"]
						.setComplication(complication)
			},
			onTreeDBLClick : function(node, e) {
				//如果是一组二组则前三项不能添加
				var groupCode=this.exContext.args.r.data.groupCode;
				if((groupCode.indexOf('01')!=-1||groupCode.indexOf('02')!=-1)&&node.attributes.key<4)
				{
					Ext.MessageBox.alert("提示", "糖尿病一组二组不能添加前三项");
					return;
				}
				//
				this.midiModules["DiabetesRecordModule_complicationList"].modified = true
				this.onTreeClick(node, e)
				this.doAddRecord()
			},
			onFormInvalid : function(v) {
				this.formInvalid = v
			},
			doAddRecord : function() {
				var form = this.formPanel.getForm()
				var complication = form.findField("complicationCode")
				if (!complication.getValue()) {
					return
				}
				if (this.formInvalid == false) {
					return
				}
				var items = this.midiModules["DiabetesRecordModule_complicationList"].schema.items
				var data = {}
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = ""
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
						}
						if (it.type == "date" && v) {
							data[it.id] = v.format("Y-m-d")
							continue
						}
						if (it.dic && v) {
							data[it.id] = v
							data[it.id + "_text"] = f.getRawValue()
							continue
						}

						data[it.id] = v
					}
				}
				var record = new Ext.data.Record(data)
				this.midiModules["DiabetesRecordModule_complicationList"]
						.addRecord(record)
				this.midiModules["DiabetesRecordModule_complicationForm"].form
						.getForm().findField("diagnosisDate")
						.setValue(this.mainApp.serverDate)
			},

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width || 700,
								height : this.height || 450,
								iconCls : "icon-form",
								closeAction : "hide",
								shim : true,
								layout : "fit",
								plain : "true",
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : "center",
								items : this.initPanel(),
								modal : true
							})
					win.on("show", function() {
								this.onWinShow()
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
				return win
			},
			onSaveRecord : function() {
				this.win.hide()
				this.fireEvent("saveRecord")
			},
			onWinShow : function() {
				this.midiModules["DiabetesRecordModule_complicationForm"]
						.doNew()
				this.midiModules["DiabetesRecordModule_complicationForm"].exContext = this.exContext
				this.midiModules["DiabetesRecordModule_complicationForm"]
						.setPhrId(this.exContext.ids.phrId)
				this.midiModules["DiabetesRecordModule_complicationList"].exContext = this.exContext
				this.midiModules["DiabetesRecordModule_complicationList"]
						.loadData()
			},
			onWinClose : function() {
				this.win.hide()
			}
		})
