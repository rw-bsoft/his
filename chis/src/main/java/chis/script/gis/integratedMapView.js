$package("chis.script.gis")

$import("app.modules.gis.MapView", "util.gis.Map",
		"util.dictionary.TreeCheckDicFactory",
		"util.dictionary.TreeDicFactory", "util.dictionary.DictionaryLoader")

$styleSheet("util.Gis")

chis.script.gis.integratedMapView = function(cfg) {
	var defcfg = {
		mapid : 'Integrate-' + Ext.id(),
		width : 620,
		westWidth : 200,
		northHeight : 30,
		navDic : 'gissubject',
		listField : "regionCode",
		intergrated : false
	}
	Ext.apply(this, defcfg)
	chis.script.gis.integratedMapView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.gis.integratedMapView, app.modules.gis.MapView, {
	// override
	initPanel : function() {
		if (this.panel) {
			return this.panel
		}
		var navDic = this.navDic
		var tr = util.dictionary.TreeCheckDicFactory.createDic({
					id : navDic,
					checkModel : "childCascade"
				})
		Ext.apply(tr.tree, {
					region : 'center'
				});

		if (!this.date) {
			this.date = new Ext.form.DateField({
						format : 'Y-m-d',
						width : 125,
						value :new Date(),
						fieldLabel : "查询时间"
					});
		}

		if (!this.regionCode) {
			var con = "";
			this.regionCode = this.createAreaDic(con)
			var reg = this.regionCode;
			reg.fieldLabel = "网格地址"
		}
		
		if (!this.manaUnitId) {
			var con = "";
			this.manaUnitId = this.createManageUnitDic(con)
			var reg = this.manaUnitId;
			reg.fieldLabel = "管理单元"
		}

		var list = new Ext.tree.TreePanel({
			// title : '专题栏',
			region : 'north',
			autoScroll : true,
			onlyLeafCheckable : false,
			animate : true,
			rootVisible : false,
			height : 250,
			hlColor : "red",
			frame : false,
			selModel : new Ext.tree.MultiSelectionModel(),
			border : false,
			// bodyStyle:'border:1px #99BBE8 solid;padding:4 4 4 0',
			// collapsible : true,
			split : true,
			root : new Ext.tree.TreeNode({
						text : 'root'
					}),
			tbar : [new Ext.Panel({
						bodyBorder : false,
						width : 200,
						labelWidth : 50,
						items : [this.regionCode, this.date],
						frame : true,
						layout : "form"
					})],
			bbar : [{
						xtype : 'button',
						text : '专题分析',
						iconCls : 'gis-subject-icon',
						listeners : {
							click : {
								fn : this.highLightAll,
								scope : this
							}
						}
					}, "-", new Ext.SplitButton({
								text : '移除列表',
								iconCls : "gis-remove-icon",
								listeners : {
									click : {
										fn : this.removeSelect,
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
		if (!this.isMapModule) {
			items.push({
						layout : "border",
						split : true,
						collapsible : true,
						title : '',
						region : 'west',
						width : this.westWidth,
						items : [list, tr.tree]
					});
		}
		items.push({
					id : "mapContainer",
					layout : "fit",
					frame : false,
					split : true,
					title : '',
					region : 'center',
					html : this.initMap()
				});
		var cfg = {
			layout : 'border',
			width : this.width,
			height : this.height,
			border : false,
			frame : false,
			items : items
		}

		if (!this.isMapModule) {
			Ext.apply(cfg, {
						style : 'padding:0,0,5,0'
					})
		}
		cfg.buttons = this.createButtons()
		var panel = new Ext.Panel(cfg)
		panel.on("render", this.onMapLoad, this)
		tr.tree.on("check", this.onTreeItemChecked, this)
		this.panel = panel
		this.tree = tr.tree
		if (!this.isMapModule)
			this.tree.expandAll()
		return panel
	},

	createAreaDic : function(con) {
		var parentKey = "3301"
		var limit = con.lengthLimit || 100 // 字典KEY长度的限制
		if (this.mainApp && this.mainApp.regionCode) {
			parentKey = this.mainApp.regionCode
			if (parentKey.length > limit) {
				parentKey = parentKey.substring(0, limit)
			}
		}
		var dic = util.dictionary.TreeDicFactory.createDic({
					id : "areaGrid",
					width : 120,
					defaultValue:{key:3301,text:"杭州市",value:"3301"},
					parentKey : con.parentKey || parentKey,
					rootVisible : true
				})
		dic.emptyText = "网格地址"
		dic.name = "areaGrid"
		dic.allowBlank = false
		return dic
	},
	
	createManageUnitDic : function(con) {
		var parentKey = "3301"
		var limit = con.lengthLimit || 100 // 字典KEY长度的限制
		if (this.mainApp && this.mainApp.deptId) {
			parentKey = this.mainApp.deptId
			if (parentKey.length > limit) {
				parentKey = parentKey.substring(0, limit)
			}
		}
		var dic = util.dictionary.TreeDicFactory.createDic({
					id : "manageUnit",
					width : 120,
					parentKey : con.parentKey || parentKey,
					rootVisible : true
				})
		dic.emptyText = "管理单元"
		dic.name = "manageUnit"
		dic.allowBlank = false
		dic.tree.on("load", function(node) {
					node.getOwnerTree().filter.filterBy(function(n) {
								var flag = true
								if (n.id.length > limit) {
									flag = false
								}
								return flag
							}, this)
				}, this)
		return dic
	},

	highLightAll : function() {
		var sel = this.list.getSelectionModel().getSelectedNodes();
		
		var regionCode=this.regionCode.value
		if(!this.regionCode.tree.getSelectionModel().getSelectedNode()){
			var mapSign="15,0"
		}else{
			var mapSign = this.regionCode.tree.getSelectionModel().getSelectedNode().attributes.mapSign;
		}
		var child = this.list.getRootNode().childNodes;
		var date=this.date.value;
		if (child.length == 0) {
			Ext.Msg.alert("提示", "请先选择专题");
			return;
		}
		var subjects = {};
		var dics = {};
		if (sel.length == 0) {
			sel = child[0];
		} else {
			sel = sel[0];
		}
		for (var i = 0; i < child.length; i++) {
			subjects[child[i].id] = {
				mid : child[i].attributes["mid"],
				kpicode : child[i].attributes["kpicode"],
				kpicodeText : child[i].attributes["text"]
			}
			dics[child[i].id] = child[i].text;
		}
		try {
			this.doHighLight(sel, subjects, dics, mapSign,regionCode,date);
		} catch (e) {
			this.refresh();
		}
	},
	// override
	refresh : function() {
		// this.reload();
	},

	// override
	doHighLight : function(sel, subjects, dics, mapSign,regionCode,date) {
		var param={};
		var m = mapSign;
		if (m.indexOf(",") == -1)
			m = this.mainApp.mapSign;
		Ext.apply(param, {
					operationId : "LogicIntegrated",
					username : this.mainApp.uid,
					mapSign : m,
					serviceId:"chis.integratedService",
					current:sel.id,
					subjects:subjects,
					date:date,
					regionCode:regionCode
				});
			this.getObject().query(param);
	},

	// override
	onMapClick : function() {
		if (!Array.prototype.slice.call(arguments, 1))
			return;
		var m = arguments[0];
		m.moduleId = this.mid;
		this.openWin(m);
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
		n.attributes["mid"] = child.attributes["mid"];
		n.attributes["kpicode"] = child.attributes["kpicode"];
//		if (this.list.getRootNode().childNodes.length > 5) {
//			var node = this.list.getRootNode().firstChild;
//			this.tree.getNodeById(node.id).getUI().toggleCheck(false);
//		}
		this.list.getRootNode().appendChild(n);
	},

	removeChild : function(child) {
		var n = this.list.getNodeById(child.id);
		if (n) {
			this.list.getRootNode().removeChild(n);
		};
	},

	// override
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
