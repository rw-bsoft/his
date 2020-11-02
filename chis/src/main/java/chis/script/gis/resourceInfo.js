$package("chis.script.gis");
$import("org.ext.ux.portal.Portal", "app.modules.common", "app.desktop.Module",
		"util.dictionary.DictionaryBuilder");

chis.script.gis.resourceInfo = function(cfg) {
	this.codeRule = cfg.codeRule || "";
	this.dicAlias = cfg.dicAlias || "";
	this.dicId = cfg.dicId || "";
	this.portlets = {};
	this.portletModules = {};
	this.colCount = 3;
	this.rowCount = 2;
	this.title = "社区档案信息";
	Ext.apply(this, app.modules.common);
	chis.script.gis.resourceInfo.superclass.constructor.apply(this, [cfg]);
	// this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.script.gis.resourceInfo, app.desktop.Module, {
	init : function() {
		this.addEvents({
					"loadData" : true
				})
	},
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		// var tree = util.dictionary.TreeDicFactory.createTree({
		// id : this.dicId,
		// keyNotUniquely : true,
		// parentKey : this.navParentKey || {},
		// rootVisible : false
		// })

		var manageUnit = this.mainApp.deptId;
		var manageUnitText = this.mainApp.dept;
		var tree = util.dictionary.TreeDicFactory.createTree({
					id : "manageUnit",
					parentKey : manageUnit,
					parentText : manageUnitText,
					lengthLimit : "12",
					rootVisible : true
				})
		var myPage = this.getRefModule();
		this.modules = myPage.modules;

		if (this.modules) {
			this.colCount = Math.ceil(this.modules.length / 2);
		}
		var portlets = this.portlets;
		var cfg = {};
		cfg.items = [];
		cfg.title = this.title;
		this.initPortlets();
		mainItems = []
		var m = 0
		for (var i = 0; i < this.rowCount; i++) {
			var mPCfg = {
				xtype : 'panel',
				layout : 'column',
				border : true
				// ,anchor : '100% 50%'
				// ,height:200
				,
				items : []
			};
			mainItems.push(mPCfg);

			for (var j = 0; j < this.colCount; j++) {
				var index = i + "." + j;
				var p = portlets[index];
				if (p) {
					var cw = this.modules[m].cw;
					var column = {
						xtype : 'panel',
						title : this.modules[m].title,
						style : "padding:2px 2px 2px 2px;background-color:#ffffff;",
						items : [],
						frame : true,
						columnWidth : cw
					};
					mPCfg.items.push(column);
					column.items.push(p);
					m++
				}
			}
		}
		var mainPanel = new Ext.Panel({
					layout : 'anchor'
					// ,title : this.title
					,
					items : mainItems,
					autoScroll : true
				});
		this.tree = tree;
		this.mainPanel = mainPanel;
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
								width : 150,
								items : this.tree
							}, {
								layout : "fit",
								frame : false,
								split : true,
								title : '',
								region : 'center',
								bodyStyle : "background-color:#ffffff;",
								// width : 280,
								items : this.mainPanel
							}]
				});
		tree.on("click", this.onTreeClick, this);
		tree.on('beforeload', function(node) {
					if (node.attributes.key.length >= 9) {
						if (node.attributes.key == this.mainApp.deptId) {
							node.setText(this.mainApp.dept);
						}
						return false;
					}
				}, this);
		tree.expand();
		this.panel = panel // mainPanel;
		// this.panel.on("afterrender", this.onReady, this)
		return this.panel;
	},
	onTreeClick : function(node, e) {
//		if (node.attributes.key.length == 9) {
		if (node.attributes.key) {
			var ms = this.portletModules;
			for (var m in ms) {
				if (ms[m] && ms[m].query) {
					ms[m].query(node.attributes.key);

				}
			}
		}
		node.expand();

	},
	getRefModule : function() {
		body = {}
		body.modules = []
		if (this.refModule) {
			var refModule = this.refModule.split(",")
			for (var i = 0; i < refModule.length; i++) {
				body.modules.push(this.loadModuleCfg(refModule[i]));
			}
		}
		return body
	},
	loadModuleCfg : function(id) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "moduleConfigLocator",
					id : id
				})
		if (result.code != 200) {
			if (result.msg = "NotLogon") {
				this.mainApp.logon(this.loadModuleCfg, this, [id])
			}
			return null;
		}
		return result.json.body;
	},
	initPortlets : function() {
		var modules = this.modules;
		if (!modules) {
			return;
		}
		var portlets = this.portlets;
		var col = 0;
		var row = 0;
		for (var i = 0; i < modules.length; i++) {
			var mod = modules[i];
			if (mod.script) {
				$import(mod.script);
				if (row >= this.colCount) {
					col++;
					row = 0;
				}
				Ext.apply(mod, {
							modelId : col + "." + row,
							enableCnd : true,
							autoLoadSchema : false,
							autoLoadLoad : false,
							isCombined : true,
							disablePagingTbr : true,
							enableCnd : false,
							showCndsBar : false,
							mainApp : this.mainApp
						});
				var m = eval("new " + mod.script + "(mod)");
				m.on("openWin", this.onOpenWin, this);
				m.on("call", this.onCall, this);
				var p = this.getPortlet(m);
				portlets[col + "." + row] = p;
				this.portletModules[col + "." + row] = m;
				row++;
			}
		}
	},
	onReady : function() {
		this.loadData();
	},
	refresh : function() {
		this.loadData();
	},
	loadData : function() {
		var ms = this.portletModules;
		for (var m in ms) {
			if (ms[m] && ms[m].loadData) {
				ms[m].loadData();
			}
		}
	},
	getPortlet : function(module) {
		var p = module.initPanel();
		p.frame = false;
		return p;
		// return new Ext.ux.Portlet({title:module.title, height:200,
		// width:"100%", layout:"fit", items:p});
	},
	onOpenWin : function(id, initCnd, needRefresh) {
		this.fireEvent("openWin", id, initCnd, needRefresh);
	},
	onCall : function(id, xmlBody) {
		var ms = this.portletModules;
		idm = Number(id) + 0.1
		if (ms[idm] && ms[idm].setXML) {
			ms[idm].setXML(xmlBody);
		}
		idm2 = '1.0'
		if (ms[idm2] && ms[idm2].setXML) {
			ms[idm2].setXML(xmlBody);
		}
	},
	getWin : function() {
		var win = this.win
		var closeAction = "hide"
		if (!win) {
			win = new Ext.Window({
						title : this.title,
						width : this.width || 800,
						height : this.height || 450,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						constrain : true,
						modal : true,
						items : this.initPanel()
					})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
						this.win.doLayout()
						this.fireEvent("winShow", this)
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			this.win = win
		}
		win.instance = this;
		return win;
	}

});
