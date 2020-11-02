$package("phis.application.cfg.script")

/**
 * 病人性质维护界面module zhangyq 2012.5.25
 */
$import("app.modules.common", "phis.script.SimpleList",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.ConfigPatientPropertiesModule = function(cfg) {
	this.createForm = "phis.application.cfg.script.ConfigPatientPropertiesForm"
	this.westWidth = cfg.westWidth || 250
	this.showNav = true
	this.height = 450
	this.serviceId = "configPatientPropertiesService";
	this.actionId = "removePatientNature";
	phis.application.cfg.script.ConfigPatientPropertiesModule.superclass.constructor
			.apply(this, [cfg])
	this.isCombined=false;
}

Ext.extend(phis.application.cfg.script.ConfigPatientPropertiesModule,
		phis.script.SimpleList, {
			active : function() {
				if (this.node) {
					if (this.TRDList) {
						this.TRDList.loadZfbl(this.node.id);
					}
				}
			},
			warpPanel : function(grid) {
				var navDic = this.navDic
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							sliceType : 5,
							parentKey : this.navParentKey,
							rootVisible : this.rootVisible || false
						})
				var tbar = [];
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {}
					btn.id = action.id;
					btn.accessKey = "F1", btn.cmd = action.id
					btn.text = action.name, btn.iconCls = action.iconCls
							|| action.id
					btn.handler = this.doAction;
					btn.name = action.id;
					// btn.scale = 'large';
					// ** add by yzh **
					btn.notReadOnly = action.notReadOnly
					if (action.notReadOnly)
						btn.disabled = false
					else
						btn.disabled = true;

					btn.scope = this;
					tbar.push(btn)
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : tbar,
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : this.westWidth,
										items : tf.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : this.getList()
									}]
						});
				this.tree = tf.tree
				// grid.__this = this
				tf.tree.on("click", this.onCatalogChanage, this)
				tf.tree.expandAll()// 展开树
				// tf.tree.on("load",this.onTreeLoad,this);
				// this.warpPanel = panel
				// var actions = this.actions
				if (actions.length > 0) {
					this.tree.on("contextmenu", this.onContextMenu, this)
				}
				this.panel = panel;
				return panel
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
			/*
			 * onTreeLoad : function(node) { if(node.getDepth()==0) {
			 * node.expandChildNodes(); } node.select();
			 * this.onCatalogChanage(node,this); },
			 */
			getList : function() {
				var module = this.createModule("getList", this.refList);
				var list = module.initPanel();
				return list;

			},
			onCatalogChanage : function(node, e) {
				var tbar = this.panel.getTopToolbar();
				if (node.id == -1) {
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('update').setDisabled(true);
					tbar.getComponent('remove').setDisabled(true);
				} else if (node.hasChildNodes()) {// 有子节点
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('update').setDisabled(false);
					tbar.getComponent('remove').setDisabled(true);
				} else {// 没有子节点
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('update').setDisabled(false);
					tbar.getComponent('remove').setDisabled(false);
				}
				var TRDList = this.midiModules['getList'];

				TRDList.disabled = true;
				if (TRDList) {
					if (!isNaN(node.id)) {

						if (node.id == -1) {
							TRDList.tab.setDisabled(true);
						} else {
							TRDList.setBrxz(node.id);
							TRDList.tab.setDisabled(false);
						}
					} else {
						TRDList.tab.setDisabled(true);
					}
				}
				this.TRDList = TRDList;
				this.node = node;
			},
			onContextMenu : function(node, e) {
				if (node.isLeaf()) {
					return
				}
				node.select()
				var actions = this.actions
				var menu = this.midiMenus["unittreemenu"]
				if (!menu) {
					var but = []
					var n = actions.length;
					for (var i = 0; i < n; i++) {
						var action = actions[i];
						var button = {
							text : action.name,
							name : action.id,
							iconCls : action.iconCls || action.id,
							handler : this.doMenuAction,
							hidden : true,
							scope : this
						}
						but.push(button)
					}
					menu = new Ext.menu.Menu({
								width : 100,
								items : but
							})
					this.midiMenus["unittreemenu"] = menu
				}
				if (node.id == -1) {
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name == "create") {
							it.show()
						} else {
							it.hide()
						}
					}
				} else if (node.hasChildNodes()) {// 有子节点
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name == "create" || it.name == "update") {
							it.show()
						} else {
							it.hide()
						}
					}
				} else { // 没有子节点
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						it.show()
					}
				}

				menu.showAt([e.getPageX(), e.getPageY()])
				this.fireEvent("contextmenu", node)
			},
			doMenuAction : function(item) {
				var cmd = item.name
				if (cmd == "create" || cmd == "update") {
					this.addNew(item)
				}
				if (cmd == "remove") {
					this.doMenuRemove(item)
				}
			},
			addNew : function(item) {
				var node = this.tree.getSelectionModel().getSelectedNode()
				var cls = this.createForm
				var id = node.id
				var title = node.text + '维护'
				item.cmd = "update";
				if (item.name == "create") {
					item.title = "新增病人性质";
					item.cmd = "create";
				}
				this.loadMenuModule(cls, "phis.application.cfg.schemas.GY_BRXZ", item, node)
				return
			},
			doCreate : function(item, e, newGroup) {
				this.addNew(item);
			},
			doUpdate : function(item, e, newGroup) {
				this.addNew(item);
			},
			doRemove : function(item, e, newGroup) {
				this.doMenuRemove(item);
			},
			doMenuRemove : function(item) {
				var node = this.tree.getSelectionModel().getSelectedNode()
				Ext.Msg.show({
							title : '确认删除病人性质[' + node.text + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processMenuRemove(node);
								}
							},
							scope : this
						})
			},
			processMenuRemove : function(node) {
				this.mask("正在删除数据...")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							method : "execute",
							schemaDetailsList : this.entryName,
							body : {
								pkey : node.id
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								node.remove();
							} else {
								if (code == 605) {
									MyMessageTip.msg("提示", msg, true);
								} else {
									this.processReturnMsg(code, msg);
								}
							}
						}, this);
			},
			loadMenuModule : function(cls, entryName, item, node) {
				if (this.loading) {
					return
				}
				var cmd = "menu"
				var cfg = {}
				cfg.title = this.title + '-' + item.text
				cfg.entryName = entryName
				cfg.op = item.cmd
				cfg.exContext = {}
				Ext.apply(cfg.exContext, this.exContext)
				
				if (item.cmd != 'create') {
					cfg.initDataId = node.id
					// cfg.exContext[entryName] = r
				} else {
					cfg.initDataId = null;
				}
				var m = this.midiModules[cmd]
				if (!m) {
					this.loading = true
					$require(cls, [function() {
										this.loading = false
										cfg.autoLoadData = false;
										var module = eval("new " + cls
												+ "(cfg)")
										module.on("save",
												this.onDepartmentSave, this)
										// module.on("close", this.active, this)
										module.opener = this
										module.setMainApp(this.mainApp)
										module.initPanel();
										this.midiModules[cmd] = module
										this.openMenuModule(cmd, item, node)
									}, this])
				} else {
					Ext.apply(m, cfg)
					this.openMenuModule(cmd, item, node)
				}
			},
			openMenuModule : function(cmd, item, node) {
				var module = this.midiModules[cmd]
				module.node = node;
				if (module) {
					var win = module.getWin()
					// win.center();
					win.setTitle(module.title)
					win.show()
					if (!win.hidden) {
						switch (item.cmd) {
							case "create" :
								module.doDepartmentNew(node)
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			afterOpenModule : function(module) {
				var loadNode = this.tree.getSelectionModel().getSelectedNode();
				if (!loadNode) {
					MyMessageTip.msg("提示", "请先选择病人性质信息!", true);
					module.getWin().hide();
					return;
				}
				var nid = loadNode.id;
				var BRXZ = module.form.getForm().findField("BRXZ");
				BRXZ.setValue(nid);
				BRXZ.setDisabled(true);
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition(this.xy[0] || xy[0], this.xy[1]
										|| xy[1])
					}
					win.setTitle(module.title)
					win.show()
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doCreate()
								this.afterOpenModule(module);
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			onDepartmentSave : function() {
				var loadNode = this.tree.getSelectionModel().getSelectedNode();
				// loadNode.remove();//先删除要加载的节点信息。
				this.tree.getLoader().load(loadNode);// 重新加载指定的节点
				this.tree.expandAll();
			},
			doCndQuery : function() {
				var initCnd = this.initCnd
				var navCnd = this.navCnd
				var index = this.cndFldCombox.getValue()
				var it = this.schema.items[index]
				if (!it) {
					return;
				}
				var v = this.cndField.getValue()
				this.requestData.pageNo = 1
				var pt = this.grid.getBottomToolbar()
				if (pt) {
					pt.cursor = 0;
				}
				if (v == null || v == "") {
					var cnd = [];

					this.queryCnd = null;
					if (navCnd) {
						if (initCnd) {
							cnd.push("and")
							cnd.push(navCnd)
							cnd.push(initCnd)
						} else {
							cnd = navCnd
						}
					}
					this.requestData.cnd = cnd
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode
						if (!node.isLeaf()) {
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
						} else {
							cnd.push(['s', v])
						}
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd || navCnd) {
					cnd = ['and', cnd]
					if (initCnd) {
						cnd.push(initCnd)
					}
					if (navCnd) {
						cnd.push(navCnd)
					}
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			onTreeNotifyDrop : function(dd, e, data) {
				var n = this.getTargetFromEvent(e);
				var r = dd.dragData.selections[0];
				var node = n.node
				var ctx = dd.grid.__this

				if (!node.leaf || node.id == r.data[ctx.navField]) {
					return false
				}
				var updateData = {}
				updateData[ctx.schema.pkey] = r.id
				updateData[ctx.navField] = node.attributes.key
				ctx.saveToServer(updateData, r)
				// node.expand()
			},
			addPanelToWin : function() {
				if (!this.fireEvent("panelInit", this.grid)) {
					return;
				};
				var win = this.getWin();
				win.add(this.warpPanel(this.grid))
				win.doLayout()
			}
		})