$package("chis.application.pd.script")

$import("app.desktop.Module", "util.rmi.miniJsonRequestSync",
		"util.dictionary.TreeDicFactory", "util.dictionary.DictionaryLoader")

chis.application.pd.script.DBDataSetMaintenanceListView = function(cfg) {
	chis.application.pd.script.DBDataSetMaintenanceListView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(chis.application.pd.script.DBDataSetMaintenanceListView, app.desktop.Module, {
			initPanel : function() {
				var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.dicId,
							keyNotUniquely : true
						})
				tree.expandAll();
				var tab = this.createTabPanel()
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
										width : 200,
										items : tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : tab
									}]
						});
				tree.on("click", this.onNavTreeClick, this)
				tree.expand()
				this.tree = tree;
				this.panel = panel;
				return panel
			},
			createTabPanel : function() {
				var items = []
				var mainTab = new Ext.TabPanel({
							frame : false,
							border : false,
							activeTab : 0,
							items : items
						})
				mainTab.on("tabchange", this.onModuleActive, this)
				this.mainTab = mainTab;
				return mainTab;
			},
			onModuleActive : function(tabPanel, newTab, curTab) {

			},
			onModuleClose : function(p) {
				var m = this.midiModules[p.key]
				if (m) {
					m.destory()
					delete this.midiModules[p.key]
					delete this.midiComponents[p.key]
				}
			},
			onNavTreeClick : function(node, e) {
				if (!node.leaf) {
					return;
				}
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				var key = node.attributes.key
				if (this.midiModules[key]) {
					var finds = this.mainTab.find("key", key)
					if (finds.length == 1) {
						var p = finds[0]
						this.mainTab.activate(p)
					}
					return;
				}

				var exCfg = this.getModuleCfg(key)
				Ext.apply(cfg, exCfg)
				var ref = cfg.ref
				if (ref) {
					var moduleCfg = this.mainApp.taskManager.loadModuleCfg(ref);
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}
				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				this.mainTab.el.mask("正在加载...")
				var panel = {}
				var module = this.midiModules[key]
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)")
					this.midiModules[key] = module
					panel = this.midiComponents[key]
					if (!panel) {
						panel = module.initPanel()
						panel.on("destroy", this.onModuleClose, this)
						this.midiComponents[key] = panel
					}
					panel.title = node.attributes.text
					panel.closable = true
					panel.key = key
				}
				var p = this.mainTab.add(panel)
				this.mainTab.el.unmask()
				this.mainTab.doLayout()
				this.mainTab.activate(p)
				this.panel.doLayout()
			},
			getModuleCfg : function(key) {
				var dic = this.dataListNavDic
				if (!dic) {
					dic = util.dictionary.DictionaryLoader.load({
								id : 'chis.dictionary.dataListNav'
							})
					this.dataListNavDic = dic;
				}
				if (!dic) {
					return {};
				}
				var n = dic.wraper[key]
				var cfg = {
					closable : this.initTabClosable,
					frame : true,
					mKey : key,
					layout : "fit"
				}
				if (n) {
					Ext.apply(cfg, n)
					cfg.title = n.text;
				}
				return cfg;
			}
		})