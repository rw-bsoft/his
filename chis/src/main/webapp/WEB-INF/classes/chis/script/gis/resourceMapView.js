$package("chis.script.gis")

$import("app.desktop.Module", "util.dictionary.TreeDicFactory",
		"util.rmi.jsonRequest", "util.rmi.miniJsonRequestSync")

chis.script.gis.resourceMapView = function(cfg) {
	var def = {
		width : 620,
		westWidth : 200,
		serviceId : 'chis.integratedService',
		listField : "regionCode",
		mapField : "mapSign",
		exQueryParam : {}
	}
	Ext.apply(this, def)
	chis.script.gis.resourceMapView.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.script.gis.resourceMapView, app.desktop.Module, {
	initPanel : function() {
		if (this.panel) {
			return this.panel
		}
		var navDic = this.navDic
		var tr = util.dictionary.TreeCheckDicFactory.createDic({
					id : navDic,
					checkModel : "childCascade",
					tbar:['搜索 : ', {
								xtype : 'textfield',
								enableKeyEvents : true,
								tree : this,
								emptyText : '输入关键字后按回车键搜索',
								listeners : {
									specialkey : this.doFilter
								}
							}]
				})
		Ext.apply(tr.tree, {
					region : 'center',
					autoScroll : true
				});
		tr.tree.on('contextmenu', this.onContextMenu,this);

		var list = new Ext.tree.TreePanel({
					region : 'north',
					autoScroll : true,
					onlyLeafCheckable : false,
					animate : true,
					rootVisible : false,
					height : 160,
					hlColor : "red",
					frame : false,
					selModel : new Ext.tree.MultiSelectionModel(),
					border : false,
					// bodyStyle:'border:1px #99BBE8 solid;padding:4 4 4 0',
					split : false,
					root : new Ext.tree.TreeNode({
								text : 'root'
							}),
					bbar : [new Ext.SplitButton({
								text : '移除列表',
								iconCls : "gis-remove-icon",
								listeners : {
									click : {
										fn : this.removeAll,
										scope : this
									}
								},
								menu : new Ext.menu.Menu({
											id : 'mainMenu',
											items : [{
														text : '移除选中',
														listeners : {
															click : {
																fn : this.removeSelect,
																scope : this
															}
														}
													}, {
														text : '移除所有',
														listeners : {
															click : {
																fn : this.removeAll,
																scope : this
															}
														}
													}]
										})
							})],
					containerScroll : true
				});
		this.list = list;
		var items = [];
		items.push({
					layout : "border",
					split : true,
					collapsible : true,
					title : '专题栏',
					region : 'west',
					width : this.westWidth,
					items : [list, tr.tree]
				});
		var tabpanel = this.createTabPanel();
		items.push(tabpanel);
		var cfg = {
			layout : 'border',
			width : this.width,
			height : this.height,
			border : false,
			frame : false,
			items : items
		}

		cfg.buttons = this.createButtons()
		var panel = new Ext.Panel(cfg)
		tr.tree.on("click", this.onTreeItemClick, this)
		tr.tree.on("check", this.onTreeItemChecked, this)
		this.initCnds()
		this.panel = panel
		this.tree = tr.tree
		return panel},

	doFilter : function(field, e) {
		if (field.getValue().length == 0) {
			this.tree.tree.root.cascade(function(n) {
						n.ui.show();
					});
			return;
		}
		if (e.getKey()!=Ext.EventObject.ENTER) return;
		this.tree.tree.root.expandChildNodes();
		this.tree.tree.root.cascade(function(n) {
						n.ui.show();
					});
		this.tree.tree.root.cascade(function(n) {
					if (n.text.length < field.getValue().length)
						return;
					var fl = true;
					for (var i = 0; i < n.text.length-field.getValue().length+1; i++) {
						if (n.text.substr(i,field.getValue().length) == field.getValue()) {
							fl=false;
							break;
						}
					}
					if (fl && (n.id.indexOf("_")>-1 && n.id.split("_").length==2)) {
						n.ui.hide();
					}
				});
	},
	
	expandAll : function() {
		this.tree.root.expandChildNodes();
	},

	collapseAll : function() {
		this.tree.collapseAll();
	},

	createTabPanel : function() {
		if (this.mainTab) {
			return this.mainTab;
		}

		var tabitem = [{
		viewType : "gis",
		 layout : "fit",
		 title : "电子地图",
		 exCfg : {
		 script : "chis.script.gis.integratedMapView"
		 }}
		]

		var tabpanel = new Ext.TabPanel({
					border : true,
					bodyBorder : true,
					region : 'center',
					activeTab : 0,
					tabPosition : 'bottom',
					items : tabitem
				});
		tabpanel.on("tabchange", this.onTabChange, this);
		this.mainTab = tabpanel
		return tabpanel;
	},

	onTabChange : function(tabPanel, newTab, curTab) {
		var viewType = newTab.viewType
		if (newTab.__inited) {
			var m = this.midiModules[viewType]
			if (m && m.refresh) {
				m.refresh()
			}
			return;
		}
		var exCfg = newTab.exCfg
		var cfg = {
			showButtonOnTop : true,
			autoLoadSchema : false,
			isCombined : true
		}
		Ext.apply(cfg, exCfg);
		var ref = exCfg.ref
		if (ref) {
			var result = util.rmi.miniJsonRequestSync({
						serviceId : "moduleConfigLocator",
						id : ref
					})
			if (result.code == 200) {
				Ext.apply(cfg, result.json.body)
			}
		}
		var cls = cfg.script
		if (!cls) {
			return;
		}
		this.panel.el.mask("加载中...", "x-mask-loading");
		$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)")
							m.setMainApp(this.mainApp)
							// m.mainTab=this.mainTab;
							this.midiModules[viewType] = m;
							var p = m.initPanel();
							newTab.add(p);
							newTab.__inited = true
							if (viewType == "list")
								this.doCndQuery(viewType);
							this.mainTab.doLayout()
							this.panel.el.unmask();
						}, this])
	},

	onTreeItemClick : function(node, e) {
		var entryNames = [];
		var params = [];
		node.select();
		if (node.leaf) {
			entryNames.push({
						id : node.id,
						entryName : node.attributes.entryName
					});
			params.push({
						path : node.attributes.path
								|| node.parentNode.parentNode.attributes.path,
						KPICode : node.attributes.kpicode,
						KPICode2 : node.attributes.kpicode2,
						title : encodeURI(encodeURI(node.attributes.title)),
						subtitle : "(按" + node.text + ")"
					});
			this.midiModules["chart"].addPortlets(entryNames, params);
		} else {
			node.expand(true, false);
			if (node.id.indexOf("_") == -1)
				return;
			this.midiModules["chart"].addPortletByParent(node.id);
			// for(var i=0;i<node.childNodes.length;i++){
			// var n=node.childNodes[i].attributes.entryName;
			// if(!n) continue;
			// entryNames.push({
			// id:node.childNodes[i].id,
			// entryName:n
			// });
			// params.push({
			// path : node.childNodes[i].parentNode.parentNode.attributes.path,
			// KPICode : node.childNodes[i].attributes.kpicode,
			// title:encodeURI(encodeURI(node.childNodes[i].parentNode.text)),
			// subtitle:"(按"+node.childNodes[i].text+")"
			// });
			// }
		}

	},

	createTbarItem : function(id) {
		var ret = util.rmi.miniJsonRequestSync({
					serviceId : "reportSchemaLoader",
					schema : id
				})
		if (ret.code == 200) {
			this.schema = ret.json.body
			this.title = this.schema.title
		} else {
			if (ret.msg == "NotLogon" && this.mainApp) {
				this.fireEvent("NotLogon", this.loadJsonData, this);
			} else {
				alert(ret.msg)
			}
			return;
		}

		this.exQueryParam = {};
		var queryParam = this.exQueryParam;
		var tbar = this.mainTab.getTopToolbar()
		var field = tbar.items.items;
		for (var i = 0; i < tbar.items.getCount(); i++) {
			if (!tbar.items.item(i).queryArg) {
				// tbar.items.item(i).destroy();
				continue
			}
			// tbar.items.item(i).destroy()
		}
		// tbar.items.clear();

		for (var i = 0; i < this.schema.args.length; i++) {
			var arg = this.schema.args[i]
			var param = queryParam[arg.id]
			if (param) {
				if (arg.dic) {
					arg.defaultValue = {
						key : param,
						text : queryParam[arg.id + "_text"]
					};
				} else {
					arg.defaultValue = param
				}
			} else {
				if (arg.defaultValue) {
					queryParam[arg.id] = arg.defaultValue
				}
			}

			if (!this.tbarItemExists(arg.id)) {
				tbar.insert(tbar.items.getCount() - 2, arg.alias + ":")
				tbar.insert(tbar.items.getCount() - 2, this.createField(arg))
			}
		}
		tbar.doLayout();
	},

	tbarItemExists : function(id) {
		var tbar = this.mainTab.getTopToolbar()
		var field = tbar.items;
		for (var i = 0; i < field.getCount(); i++) {
			if (field.item(i).queryArg) {
				if (field.item(i).getName() == id)
					return true;
			}
		}
		return false;
	},

	createField : function(it) {
		var defaultWidth = 100
		var cfg = {
			queryArg : true,
			name : it.id,
			fieldLabel : it.alias,
			xtype : it.xtype || "textfield",
			width : defaultWidth,
			value : it.defaultValue
		}
		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it['not-null']) {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
		}
		if (it.dic) {
			cfg.width = it.width
			it.dic.defaultValue = it.defaultValue
			it.dic.width = it.width || defaultWidth
			var combox = this.createDicField(it.dic)
			Ext.apply(combox, cfg)
			return combox;
		}
		if (it.length) {
			cfg.maxLength = it.length;
		}
		switch (it.type) {
			case 'int' :
			case 'double' :
			case 'bigDecimal' :
				cfg.xtype = "numberfield"
				if (it.type == 'int') {
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				} else {
					cfg.decimalPrecision = it.precision || 2;
				}
				if (it.minValue) {
					cfg.minValue = it.minValue;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				break;
			case 'date' :
				cfg.xtype = 'datefield'
				cfg.emptyText = "请选择日期"
				break;
			case 'text' :
				cfg.xtype = "htmleditor"
				cfg.enableSourceEdit = false
				cfg.enableLinks = false
				cfg.width = 300
				break;
		}
		return cfg;
	},

	createDicField : function(dic) {
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Tree";
		} else {
			cls += dic.render;
		}
		cls += "DicFactory"

		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},

	doQuery : function() {
		var tbar = this.mainTab.getTopToolbar()
		var fields = tbar.items

		var count = fields.getCount()
		for (var i = 0; i < count; i++) {
			var f = fields.item(i)
			if (f.queryArg) {
				if (!f.validate()) {
					return;
				}

				var v = f.getValue();
				if (v == null || v.length == 0) {
					continue;
				}

				if (f.getXType() == "treeField"
						&& f.tree.getSelectionModel().getSelectedNode()) {
					v = f.tree.getSelectionModel().getSelectedNode().attributes;
				}

				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}

				this.exQueryParam[f.getName()] = v
			}
		}
		var viewType = this.mainTab.getActiveTab().viewType;
		switch (viewType) {
			case "chart" :
				this.doChartQuery(viewType);
				break;
			case "gis" :
				this.doMapQuery(viewType);
				break;
			case "list" :
				this.doCndQuery(viewType);
		}
	},

	doChartQuery : function(viewType) {
		var mchart = this.midiModules[viewType];
		mchart.entryName = this.entryName;
		mchart.exQueryParam = this.exQueryParam;
		mchart.reloadUrl();
	},

	doMapQuery : function(viewType) {
		var req = {
			serviceId : this.serviceId || "chis.simpleReport",
			schema : this.entryName,
			title : this.title,
			body : [],
			param : {}
		}
		if (this.exQueryParam) {
			for (var name in this.exQueryParam) {
				var v = this.exQueryParam[name]
				if (typeof v == "object") {
					req[this.mapField] = v[this.mapField];
					req[this.listField] = v.key;
				} else {
					if (name == this.listField) {
						req[this.listField] = v;
						req[this.mapField] = v;
					} else
						req.param[name] = v;
				}
			}
		}
		this.midiModules[viewType].mid = this.mid;
		this.midiModules[viewType].queryField = this.queryField;
		this.midiModules[viewType].doHighLight(req);
	},

	doCndQuery : function(viewType) {
		var list = this.midiModules[viewType]
		var initCnd = list.initCnd
		var queryCnd = list.queryCnd

		// var cnd = ['eq', ['$', this.listField], ['s', '33010201']];
		var code = this.exQueryParam[this.listField];
		if (typeof(code) == "object")
			code = code.key;
		var cnd = ['like', ['$', this.listField], ['s', code + '%']]

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

		list.requestData.cnd = cnd
		list.requestData.pageNo = 1
		list.refresh()
	},

	loadJsonData : function() {
		var req = {
			serviceId : this.serviceId || "chis.simpleReport",
			schema : this.entryName,
			title : this.title,
			pageSize : 500
		}
		if (this.exQueryParam) {
			for (var name in this.exQueryParam) {
				var v = this.exQueryParam[name]
				if (typeof v == "object") {
					v = v.key
				}
				req[name] = v;
			}
		}
		util.rmi.jsonRequest(req, function(code, msg, json) {
					if (code < 300) {
						if (json.totalCount > 0) {
							this.data = json.body
						} else {
							this.data = [];
						}

					} else {
						if (msg == "NotLogon" && this.mainApp) {
							this.fireEvent("NotLogon", this.loadJsonData, this);
						} else {
							alert(msg)
						}
					}
				}, this)
	},

	createButtons : function() {
		var actions = this.actions
		var buttons = []
		if (!actions) {
			return buttons
		}
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {
				text : action.name,
				ref : action.ref,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons
	},

	doAction : function(item, e) {
		var cmd = item.cmd
		var script = item.script
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			var cfg = {
				id : this.id,
				title : this.title,
				width : this.width,
				height : this.height,
				iconCls : 'bogus',
				shim : true,
				layout : "fit",
				items : this.initPanel(),
				animCollapse : true,
				constrainHeader : true,
				minimizable : true,
				maximizable : true,
				shadow : false
			}
			if (!this.mainApp) {
				cfg.closeAction = 'hide'
			}
			win = new Ext.Window(cfg)
			win.on("resize", this.onWinResize, this)
			win.on("show", function() {
						jsReady = true;
					});
			// win.on("show",this.initMap,this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win
	},

	onWinResize : function() {
	}

})
