$package("chis.script")
$import("app.desktop.Module", "util.dictionary.TreeDicFactory",
		"chis.script.BizCommon", "util.rmi.miniJsonRequestSync")
chis.script.CombinedDocList = function(cfg) {
	this.westWidth = 180
	this.height = 500
	this.manageUnitField = cfg.manageUnitField || "manaUnitId"
	this.areaGridField = "regionCode"
	this.addEvents({
				moduleLoaded : true
			});
	Ext.apply(cfg, chis.script.BizCommon);
	this.noDefaultBtnKey = false;
	this.cnds = cfg.cnds;
	chis.script.CombinedDocList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.CombinedDocList, app.desktop.Module, {
			initPanel : function() {
				var navTab = this.createNavTab()
				var listTab = this.createListTab()
				var panel = new Ext.Panel({
							border : false,
							frame : this.frame == false ? false : true,
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
										items : navTab
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : listTab
									}]
						});
				this.superLeftPanel = panel.items.items[0]; 
				return panel
			},
			createNavTab : function() {
				var areaGridDicTree = util.dictionary.TreeDicFactory
						.createTree({
									id : "chis.dictionary.areaGrid",
									// parentKey:this.mainApp.regionCode,
									// parentText:this.mainApp.regionText,
									rootVisible : false
								})
				this.areaGridDicTree = areaGridDicTree
				areaGridDicTree.title = "网格地址"
				areaGridDicTree.on("click", this.onAreaGridTreeSelect, this)
				areaGridDicTree.on("contextmenu",
						this.onAreaGridTreeContextmenu, this)

				var manageUnit = this.mainApp.deptId;
				var manageUnitText = this.mainApp.dept;
				// if(manageUnit && manageUnit.length>=6){
				// manageUnit = manageUnit.substring(0,6)
				// }

				var manageUnitDicTree = util.dictionary.TreeDicFactory
						.createTree({
									id : "chis.@manageUnit",
									parentKey : manageUnit,
									parentText : manageUnitText,
									lengthLimit : "12",
									rootVisible : true
								})
				this.manageUnitDicTree = manageUnitDicTree
				manageUnitDicTree.title = "管辖机构"
				manageUnitDicTree
						.on("click", this.onManageUnitTreeSelect, this)
				manageUnitDicTree.on("contextmenu",
						this.onManageUnitTreeContextmenu, this)

				var navTab = new Ext.TabPanel({
							activeTab : 0,
							tabPosition : 'bottom',
							items : [areaGridDicTree, manageUnitDicTree]
						})
				this.navTab = navTab
				return navTab
			},
			keyManageFunc : function(keyCode, keyName) {
				var list = this.midiModules["list"];
				if (list) {
					if (list.btnAccessKeys) {
						var btn = list.btnAccessKeys[keyCode];
						if (btn && btn.disabled) {
							return true;
						}
					}
					list.doAction(list.btnAccessKeys[keyCode])
				}
			},
			onAreaGridTreeSelect : function(node, e) {
				var pk = node.attributes.key
				var curTab = this.listTab.getActiveTab()
				var viewType = curTab.viewType
				if (viewType == "gis" || viewType == "areaGrid") {
					var m = this.midiModules[viewType]
					if (!m.combinedView) {
						m.combinedView = this;
					}

					if (m && m.refresh) {
						m.parentKey = pk
						if (viewType == "gis") {
							m.mapSign = node.attributes.mapSign
							m.regionCode = node.attributes.key
							m.refresh()
							var m2 = this.midiModules["areaGrid"]
							if (m2) {
								m2.parentKey = pk
							}
						} else {
							m.refresh()
							var m2 = this.midiModules["gis"]
							if (m2) {
								m2.mapSign = node.attributes.mapSign
								m2.regionCode = node.attributes.key
							}
						}
					}
				} else {
					var m1 = this.midiModules["areaGrid"]
					var m2 = this.midiModules["gis"]
					if (m1) {
						m1.parentKey = pk;
					}
					if (m2) {
						m2.mapSign = node.attributes.mapSign
						m2.regionCode = node.attributes.key
					}
				}
				this.curState = {
					parentKey : pk,
					mapSign : node.attributes.mapSign
				}

				// this.resetList(this.areaGridField,node)
			},
			onManageUnitTreeSelect : function(node, e) {
				// this.resetList(this.manageUnitField,node)
			},
			onAreaGridTreeContextmenu : function(node, e) {
				e.preventDefault();
				this.areaGridDicTree.selectPath(node.getPath())
				this.areaGridNode = node
				var cmenu = this.midiMenus['areaGrid']
				if (!cmenu) {
					var items = [{
								text : "查询",
								iconCls : "query",
								handler : this.doAreaGridQuery,
								scope : this
							}];
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['areaGrid'] = cmenu
				}
				cmenu.showAt([e.getPageX(), e.getPageY()])
			},
			onManageUnitTreeContextmenu : function(node, e) {
				e.preventDefault();
				this.manageUnitDicTree.selectPath(node.getPath())
				this.manageUnitNode = node
				var cmenu = this.midiMenus['manageUnit']
				if (!cmenu) {
					var items = [{
								text : "查询",
								iconCls : "query",
								handler : this.doManageUnitQuery,
								scope : this
							}];
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['manageUnit'] = cmenu
				}
				cmenu.showAt([e.getPageX(), e.getPageY()])

			},
			doAreaGridQuery : function() {
				if (this.areaGridNode) {
					this.resetList(this.areaGridField, this.areaGridNode)
				}
			},
			doManageUnitQuery : function() {
				if (this.manageUnitNode) {
					this.resetList(this.manageUnitField, this.manageUnitNode)
				}
			},
			resetList : function(navField, node) {
				var list = this.midiModules["list"]
				var initCnd = list.initCnd
				var queryCnd = list.queryCnd

				var cnd;
				if (node.leaf) {
					cnd = ['eq', ['$', navField], ['s', node.id]]
				} else {
					cnd = ['like', ['$', navField], ['s', node.id + '%']]
				}
				list.navCnd = cnd
				if (initCnd || queryCnd) {
					cnd = ['and', cnd]
					if (initCnd) {
						cnd.push(initCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				// list.requestData.cnd = cnd
				// list.requestData.pageNo = 1
				var curTab = this.listTab.getActiveTab()
				var viewType = curTab.viewType
				if (viewType == "list") {
					list.doCndQuery(null, null, true)
				}
			},
			createListTab : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = [];
				var actions = this.actions
				var overriders = {
					"list" : false,
					"areaGrid" : false,
					"gis" : false
				}
				if (actions) {
					for (var i = 0; i < actions.length; i++) {
						var ac = actions[i];
						// @@ added by chinnsii,2010-09-07,使得可以从外面传递一个初始查询条件
						ac.cnds = this.cnds;
						var vt = ac.viewType
						overriders[vt] = true;
						tabItems.push({
									viewType : vt,
									layout : "fit",
									title : ac.name,
									exCfg : ac
								})
					}
				}

				var defaultCfg = [{
							viewType : "list",
							layout : "fit",
							title : "列表视图",
							exCfg : {
								script : "chis.script.BizSimpleListView",
								entryName : this.entryName,
								cnds : this.cnds
							}
						}, {
							viewType : "areaGrid",
							layout : "fit",
							title : "网格视图",
							exCfg : {
								script : "chis.script.AreaGridView",
								entryName : this.entryName,
								infoType : this.id
							}
						} // ,
				// {viewType:"gis",layout:"fit",title:"电子地图",exCfg:{script:"app.modules.gis.MapView",isMapModule:true}}
				]
				for (var i = 0; i < defaultCfg.length; i++) {
					var cfg = defaultCfg[i]
					var vt = cfg.viewType
					if (!overriders[vt]) {
						tabItems.push(cfg)
					}
				}

				var listTab = new Ext.TabPanel({
							activeTab : 0,
							deferredRender : false,
							tabPosition : 'bottom',
							items : tabItems
						})
				listTab.on("tabchange", this.onListTabChange, this);
				this.listTab = listTab
				return listTab;
			},
			onListTabChange : function(tabPanel, newTab, curTab) {
				var viewType = newTab.viewType

				if (viewType == "gis" || viewType == "areaGrid") {
					this.navTab.activate(0)
				}
				if (newTab.__inited) {
					var m = this.midiModules[viewType]
					if (m && m.refresh) {
						m.refresh()
					}
					if (viewType == "gis")
						this.fireEvent("moduleLoaded", this);
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true,
					mainApp : this.mainApp
				}
				Ext.apply(cfg, exCfg);
				Ext.apply(cfg, this.curState)
				if (this.param)
					Ext.apply(cfg, {
								param : this.param
							})
				var ref = exCfg.ref
				if (ref) {
					var result = util.rmi.miniJsonRequestSync({
								url : 'app/loadModule',
								id : ref
							})
					if (result.code == 200) {
						Ext.apply(cfg, result.json.body)
						var properties = result.json.body.properties
						Ext.apply(cfg, properties);

					}
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}
				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)")
									m.setMainApp(this.mainApp)
									m.parent = this;
									if (this.initCnd) {
										m.requestData.cnd = this.initCnd;
									}
									this.midiModules[viewType] = m;
									var p = m.initPanel();
									newTab.add(p);
									newTab.__inited = true
									this.listTab.doLayout()
									if (viewType == "gis")
										this.fireEvent("moduleLoaded", this);
								}, this])
			},

			refresh : function() {
				var list = this.midiModules["list"]
				list.refresh();
			},

			getWin : function() {
				var win = this.win
				var closeAction = "close"
				if (!this.mainApp) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrain : true,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})