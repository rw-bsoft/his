$package("chis.script.gis")

$import("app.desktop.Module", "util.widgets.TabCloseMenu",
		"util.dictionary.TreeCheckDicFactory",
		"util.dictionary.DictionaryLoader", "app.modules.list.ChartListView")

$styleSheet("util.Gis")

chis.script.gis.integratedReportView = function(cfg) {
	var config = {
		width : 620,
		westWidth : 200,
		northHeight : 30,
		exQueryParam : {},
		currentIndex:0
	}
	Ext.apply(this, config)
	this.addEvents({
				"loaded" : true
			});
	chis.script.gis.integratedReportView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.gis.integratedReportView, app.desktop.Module, {
	initPanel : function() {
		if (this.panel) {
			return this.panel
		}
		var navDic = this.navDic
		var tr = util.dictionary.TreeCheckDicFactory.createDic({
					id : navDic,
					checkModel : "childCascade",
					tbar : ['搜索 : ', {
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
		tr.tree.on('contextmenu', this.onContextMenu, this);

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
        this.tree = tr.tree
        var root = this.tree.getRootNode()
        root.on("load", function() {
         this.tree.filter.filterBy(this.filterTree, this)
        }, this)
    
		this.initCnds()
		this.panel = panel
		return panel
	},
    filterTree : function( node ){
     if(this.mainApp.jobtitleId == "05" && ( node.id == "MHC" ||  node.id == "CDH" )){
      return false
     }
     return true
    },
	expandAll : function() {
		this.tree.root.expandChildNodes();
	},

	collapseAll : function() {
		this.tree.collapseAll();
	},

	onContextMenu : function(node, e) {
		if (!this.menu) { // create context menu on first right click
			this.menu = new Ext.menu.Menu([{
						text : '展开全部',
						iconCls : 'expandall',
						handler : this.expandAll,
						scope : this
					}, {
						text : '收缩全部',
						iconCls : 'app',
						handler : this.collapseAll,
						scope : this
					}]);
		}
		this.menu.showAt(e.getXY());
	},

	initCnds : function() {
		var ret = util.rmi.miniJsonRequestSync({
					serviceId : "reportSchemaLoader",
					schema : 'MHC/District_region'
				})
		if (ret.code == 200) {
			var schema = ret.json.body
			if (!schema || !schema.args) {
				return
			}
			var args = schema.args
			var queryParam = this.exQueryParam;

			var tbar = this.mainTab.getTopToolbar()

			for (var i = 0; i < args.length; i++) {
				var arg = args[i]
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
				if (!arg.hidden) {
					tbar.add(arg.alias + ":")
					tbar.add(this.createField(arg))
				}
			}
			tbar.add("-")
			tbar.add({
						text : "查询",
						iconCls : "query",
						handler : this.doQuery,
						scope : this
					})
			tbar.doLayout();
		} else {
			if (ret.msg == "NotLogon") {
				this.fireEvent("NotLogon");
			} else {
				alert(ret.msg)
			}
			return;
		}
	},

	createTabPanel : function() {
		if (this.mainTab) {
			return this.mainTab;
		}
		var combox = this.createDicField({
					id : "statisticalReportType",
					render : "Tree",
					defaultValue : {
						text : '按管理单元',
						key : 'manageUnit'
					}
				});
		
		combox.on("change",this.onReportTypeChange,this);

		Ext.apply(combox, {
					width : 150,
					queryArg : true
				});

		var tabpanel = new Ext.TabPanel({
					tbar : ['选择类别:', combox],
					border : true,
					bodyBorder : true,
					region : 'center',
					activeTab : 0,
					plugins : new util.widgets.TabCloseMenu(),
					enableTabScroll : true,
					animScroll : true,
					tabPosition : 'bottom',
					items : []
				});
		this.mainTab = tabpanel
		return tabpanel;
	},
	
	onReportTypeChange:function(){
		this.currentIndex++;
	},

	doFilter : function(field, e) {
		if (field.getValue().length == 0) {
			this.tree.tree.root.cascade(function(n) {
						n.ui.show();
					});
			return;
		}
		if (e.getKey() != Ext.EventObject.ENTER)
			return;
		this.tree.tree.root.expandChildNodes();
		this.tree.tree.root.cascade(function(n) {
					n.ui.show();
				});
		this.tree.tree.root.cascade(function(n) {
			if (n.text.length < field.getValue().length)
				return;
			var fl = true;
			for (var i = 0; i < n.text.length - field.getValue().length + 1; i++) {
				if (n.text.substr(i, field.getValue().length) == field
						.getValue()) {
					fl = false;
					break;
				}
			}
			if (fl && (n.id.indexOf("_") > -1 && n.id.split("_").length == 2)) {
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

	createField : function(it) {
		var defaultWidth = 100
		var cfg = {
			queryArg : true,
			name : it.id,
			fieldLabel : it.alias,
			xtype : it.xtype || "textfield",
			width : defaultWidth,
			value : it.defaultValue,
			hidden : it.hidden || false
		}
		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it['not-null']) {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
		}

		if (it.dic) {
			// cfg.width = it.width
			cfg.listWidth = defaultWidth
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
				cfg.width = 150
				break;
			case 'month' :
				cfg.xtype = 'monthfield'
				cfg.emptyText = "请选择日期"
				break;
		}
		return cfg;
	},

	createDicField : function(dic) {
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Simple";
		} else {
			cls += dic.render
		}
		cls += "DicFactory"

		$import(cls)
		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},

	onTreeItemClick : function(node, e) {
		// if (node.leaf) {
		// this.doHighLight(node);
		// }
	},

	onTreeItemChecked : function(node, checked) {
		if (node.leaf) {
			this._isUpdate=true;
			if (checked) {
				if (!node.hidden)
					this.addChild(node);
			} else {
				// if (!this.hasChildChecked(node)) {
				// node.parentNode.getUI().check(false);
				// }

				this.removeChild(node);
			}
		}
	},

	hasChildChecked : function(node) {
		var check = false;
		if (!node.parentNode)
			return;
		for (var i = 0; i < node.parentNode.childNodes.length; i++) {
			if (node.parentNode.childNodes[i].getUI().isChecked()) {
				check = true;
			} else {
				check = false;
			}
		}
		return check;
	},

	removeAll : function() {
		var node = this.list.getRootNode();
		while (node.hasChildNodes()) {
			var n = this.tree.getNodeById(node.firstChild.id);
			if (n) {
				n.getUI().toggleCheck(false);
			}
		}
	},

	removeSelect : function() {
		var sel = this.list.getSelectionModel().getSelectedNodes();
		if (sel.length == 0) {
			Ext.Msg.alert("提示", "请先选择专题");
			return;
		}
		var ids = [];
		for (var i = 0; i < sel.length; i++) {
			ids.push(sel[i].id);
		}
		for (var j = 0; j < ids.length; j++) {
			var node = this.tree.getNodeById(ids[j]);
			if (node) {
				node.getUI().toggleCheck(false);
			}
		}
	},

	addChild : function(child) {
		var c = this.list.getNodeById(child.id);
		if (c)
			return;
		var n = new Ext.tree.TreeNode({
					text : child.text,
					id : child.id
				});
		// n.attributes["mid"] = child.attributes["mid"];
		// n.attributes["kpicode"] = child.attributes["kpicode"];
		// if (this.list.getRootNode().childNodes.length > 5) {
		// var node = this.list.getRootNode().firstChild;
		// this.tree.getNodeById(node.id).getUI().toggleCheck(false);
		// }
		this.list.getRootNode().appendChild(n);
	},

	removeChild : function(child) {
		var n = this.list.getNodeById(child.id);
		if (n) {
			this.list.getRootNode().removeChild(n);
		};
	},

	doQuery : function() {
		var child = this.list.getRootNode().childNodes;
		if (child.length == 0) {
			Ext.Msg.alert("提示", "请先选择专题");
			return;
		}
		var subjects = [];
		var dics = {};
		for (var i = 0; i < child.length; i++) {
			subjects.push(child[i].id);
			dics[child[i].id] = child[i].text;
		}

		this.exQueryParam["subjects"] = subjects;
		this.exQueryParam["dics"] = dics;

		var tbar = this.mainTab.getTopToolbar()
		var fields = tbar.items

		var count = fields.getCount()
		for (var i = 0; i < count; i++) {
			var f = fields.item(i)
			if (f.queryArg) {
				if (!f.validate()) {
					return;
				}
				var v = f.getValue()
				if (v == null || v.length == 0) {
					continue;
				}

				if (f.getXType() == "treeField") {
					var sel = f.tree.getSelectionModel().getSelectedNode();
					if (sel && sel.attributes["schema"]) {
						this.exQueryParam["entryName"] = sel.attributes["schema"];
					}
				}

				if (f.getXType() == "datefield" || f.getXType() == "monthfield") {
					v = v.format("Y-m")
				}
				this.exQueryParam[f.getName()] = v
			}
		}

		this.loadJsonData()
		this.openTab()

	},

	openTab : function() {
		if(this._isUpdate){ 
		 	this.currentIndex++;
			this._isUpdate=false;
		}
		
		var m=this.midiModules[this.currentIndex];
		if (!m) {
			m= new app.modules.list.ChartListView({
									showButtonOnPT : true,
									schema : this.schema,
									data : this.data
								});
			var panel = m.initPanel();
			Ext.apply(panel,{
				  title:this.getTitle(),
				  currentIndex:this.currentIndex,
				  closable : true,
				  layout : "fit"
			})	
			panel.on("close",this.onTabClose,this);
			panel.on("destroy",this.onTabClose,this);
			this.mainTab.add(panel);
			this.mainTab.setActiveTab(panel);
			this.midiModules[this.currentIndex]=m;
		}else{
			m=this.midiModules[this.mainTab.getActiveTab()?this.mainTab.getActiveTab().currentIndex:this.currentIndex];
			m.data=this.data;
			m.refresh();
		}
	},
	
	onTabClose:function(p){
		this.midiModules[p.currentIndex]=undefined;
	    delete this.midiModules[p.currentIndex];
	},

	getTitle : function() {
		var child = this.list.getRootNode().childNodes;
		var title = (child.length == 1 ? child[0].text : child[0].text + "-"
				+ child[child.length - 1].text);
		title += "(" + this.mainTab.getTopToolbar().items.item(1).getRawValue()
				+ ")";
		return title;
	},

	loadJsonData : function() {
		var requestData = {
			serviceId : "chis.intergratedReportService"
		}
		for (var k in this.exQueryParam) {
			requestData[k] = this.exQueryParam[k];
		}

		var ret = util.rmi.miniJsonRequestSync(requestData)
		if (ret.code == 200) {
			this.schema = ret.json.body.schema || this.schema
			this.data = ret.json.body.data
			this.title = this.schema.title
		} else {
			if (ret.msg == "NotLogon" && this.mainApp) {
				this.fireEvent("NotLogon", this.loadJsonData, this);
			} else {
				alert(ret.msg)
			}
			return;
		}
	},

	initCnd : function(param, isRefresh) {
		var list = param.module || this.midiModules[param.moduleId];
		var cnd = this.getCnd(param);
		var queryCnd = list.queryCnd
		if (queryCnd) {
			cnd = ['and', cnd]
			cnd.push(queryCnd)
		}

		list.requestData.cnd = cnd;
		list.requestData.pageNo = 1
		if (isRefresh)
			list.refresh()
	},

	getCnd : function(param) {
		var initCnd = ["eq", ["$", "a.status"], ["s", "0"]]
		// var cnd = ['like', ['$', this.listField], ['s', param.regionCode +
		// '%']]
		var cnd = [
				'eq',
				['substring', ['$', this.listField], ['s', '0'],
						['s', param.regionCode.length]],
				['s', param.regionCode]]
		if (this.queryField) {
			cnd = ['and', cnd]
			cnd.push(['eq', ['$', this.queryField], ['s', param.value]]);
		}
		cnd = ['and', cnd]
		cnd.push(initCnd)
		return cnd;
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