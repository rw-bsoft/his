$package("chis.application.ohr.script")

$import("util.Accredit", "chis.application.ohr.script.OldPeopleCheckupList")

chis.application.ohr.script.OldPeopleCheckupModule = function(cfg) {
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	cfg.width = 1000;
	cfg.height = 400;
	chis.application.ohr.script.OldPeopleCheckupModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.ohr.script.OldPeopleCheckupModule,
		app.desktop.Module, {
			initPanel : function() {
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : "chis.dictionary.oldPeopleCheckup",
							onlySelectLeaf : "true"
						});
				var tree = tf.tree;
				tree.expandAll();
				tree.title = " ";
				tree.split = true;
				tree.width = this.westWidth || 140;
				tree.height = 400;
				var grid = this.getGrid();
				grid.setWidth(600);
				grid.setHeight(405);
				grid.title = " ";
				grid.height = 400;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'hbox',
							height : 405,
							items : [tree, grid]
						});
				tf.tree.on("click", this.onTabActive, this);
				this.panel = panel;
				this.tree = tf.tree;
				return panel;
			},
			keyManageFunc : function(keyCode, keyName) {
				var m = this.list;
				if (m) {
					if (m.btnAccessKeys) {
						var btn = m.btnAccessKeys[keyCode];
						if (btn && btn.disabled) {
							return;
						}
					}
					if (!m.btnAccessKeys[keyCode]) {
						return;
					}
					this.grid.stopEditing()
					m.doAction(m.btnAccessKeys[keyCode]);
				}
			},

			getGrid : function() {
				var list = this.midiModules["listView"]
				if (!list) {
					var moduleCfg = this.mainApp.taskManager
							.loadModuleCfg(this.refModule);
					var cfg = {
						pageSize : 100,
						entryName : this.entryName,
						saveServiceId : this.saveServiceId,
						autoLoadSchema : false,
						serviceAction : this.serviceAction,
						autoLoadData : false,
						showButtonOnTop : true,
						disablePagingTbr : true,
						mutiSelect : true,
						enableCnd : false
					};
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
					var cls = cfg.script;
					$require(cls, [function() {
										list = eval("new " + cls + "(cfg)")
										list.on("save", this.onFormSave, this)
										list.setMainApp(this.mainApp)
									}, this])
					this.midiModules["listView"] = list;
				}
				this.list = list;
				this.grid = list.initPanel();
				this.grid.setAutoScroll(true);
				return this.grid;
			},

			onFormSave : function() {
				this.fireEvent("save");
			},

			onTabActive : function(node, e) {
				if (!node.leaf || node.parentNode == null) {
					return;
				}
				var id = node.id;
				var text = node.text;
				var sm = this.grid.getSelectionModel();
				var index = this.list.store.find('indicatorName', text);
				if (index > -1) {
					sm.select(index, 0);
				}
			},

			loadData : function() {
				this.list.exContext.control = this.exContext.control;
				this.list.readOnly = this.exContext.args.readOnly;
				this.list.empiId = this.exContext.args.empiId;
				this.list.recordId = this.exContext.args.initDataId;
				this.list.phrId = this.exContext.args.phrId;
				this.list.loadData();
			},

			getIndexData : function() {
				var record = [];
				for (var j = 0; j < this.list.store.getCount(); j++) {
					var storeItem = this.list.store.getAt(j);
					var items = this.list.schema.items;
					var r = {};
					for (var i = 0; i < items.length; i++) {
						var it = items[i];
						r[it.id] = storeItem.get(it.id);
					}
					record.push(r);
				}
				return record;
			},

			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								autoScroll : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							});
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					win.on("add", function() {
								this.win.doLayout();
								this.fireEvent("winShow", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					this.win = win;
				}
				win.instance = this;
				return win;
			}
		});