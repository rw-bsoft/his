$package("phis.application.cfg.script")

$import("app.modules.common", "phis.script.SimpleModule",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.ConfigChineseDiseaseModule = function(cfg) {
	this.westWidth = cfg.westWidth || 180
	this.showNav = true
	this.width = 950;
	this.height = 350
	cfg.autoLoadData = false;
	Ext.apply(this, app.modules.common)
	phis.application.cfg.script.ConfigChineseDiseaseModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigChineseDiseaseModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.navDic
						})
				this.tree = tree
				this.tree.on("click", this.onCatalogChanage, this)
				this.tree.on("load", this.onTreeLoad, this);
				this.tree.expandAll();

				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : this.westWidth,
										items : tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : this.getList()
									}]
						});

				this.panel = panel;
				return panel
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.oper = this;
				this.list.grid = this.list.initPanel();
				return this.list.grid;
			},
			onTreeLoad : function(node) {
				if (node) {
					if (!this.select) {
						node.select();
						this.select = true;
						if (!isNaN(node.id)) {
							this.onCatalogChanage(node, this);
						}
					}
					if (node.getDepth() == 0) {
						if (node.hasChildNodes()) {
							node.firstChild.expand();
						}
					}
				}
				this.list.node = node;

			},
			onCatalogChanage : function(node, e) {
				this.list.requestData.cnd = ['like', ['$', 'b.FLBM'],
						['s', node.id + "%"]];
				this.list.refresh()
				this.node = node;
				this.list.node = node;
			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex)
				var cmenu = this.midiMenus['gridContextMenu']
				if (!cmenu) {
					var items = [];
					var actions = this.actions
					if (!actions) {
						return;
					}
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];
						var it = {}
						it.cmd = action.id
						it.ref = action.ref
						it.iconCls = action.iconCls || action.id
						it.script = action.script
						it.text = action.name
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				}
				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (this.actions[i].id == "disabled") {
							var btnResult = btn[0].getText().substring(0, 2);
							cmenu.items.itemAt(i).setText(btnResult);
						}
						if (!btn || btn.length == 0) {
							continue;
						}
						if (btn[0].disabled) {
							cmenu.items.itemAt(i).disable();
						} else {
							cmenu.items.itemAt(i).enable();
						}

					}
				}
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) 
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			}
		})