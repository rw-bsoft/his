$package("com.bsoft.phis.pub")

/*
 * 科室员工维护界面module liyl 2012.3.29
 */

$import("app.modules.common", "com.bsoft.phis.SimpleList",
		"util.dictionary.TreeDicFactory")

com.bsoft.phis.pub.DepartmentManageModule = function(cfg) {
	// this.title = "机构用户管理"
	this.createForm = "com.bsoft.phis.pub.DepartmentManageForm"
	this.autoLoadSchema = true
	this.activeModules = {}
	this.midiMenus = {}
	this.pModules = {}
	this.westWidth = cfg.westWidth || 250
	this.gridDDGroup = 'gridDDGroup'
	this.saveServiceId = "simpleSave"
	this.showNav = true
	this.width = 950;
	this.height = 450
	this.cmd = "create";
	cfg.autoLoadData = false;
	cfg.winState = [300, 80]
	Ext.apply(this, app.modules.common)
	com.bsoft.phis.pub.DepartmentManageModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(com.bsoft.phis.pub.DepartmentManageModule,
		com.bsoft.phis.SimpleList, {
			warpPanel : function(grid) {
				if (!this.showNav) {
					return grid
				}
				var navDic = this.navDic
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							// sliceType : 5,
							parentKey : this.navParentKey || {},
							rootVisible : this.rootVisible || false
						})
				var tbar = [];
				var actions = [{
							id : "newKS",
							name : "新建科室",
							disabled : true,
							iconCls : "add"
						}, {
							id : "uptKS",
							name : "修改科室",
							disabled : true,
							iconCls : "update"
						}, {
							id : "delKS",
							name : "删除科室",
							disabled : true,
							iconCls : "remove"
						}];
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {}
					btn.id = action.id;
					btn.accessKey = "F1", btn.cmd = action.id
					btn.text = action.name, btn.iconCls = action.iconCls
							|| action.id
					btn.handler = this.doAction;
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
										items : grid
									}]
						});
				this.tree = tf.tree
				grid.__this = this
				tf.tree.on("click", this.onCatalogChanage, this)
				// this.warpPanel = panel
				// tf.tree.expandAll()
				this.tree.getLoader().on("load", this.onTreeLoad, this);
				this.tree.on("beforeexpandnode", this.onExpandNode, this);
				this.tree.on("beforecollapsenode", this.onCollapseNode, this);
				var actions = this.actions
				if (actions.length > 0) {
					this.tree.on("contextmenu", this.onContextMenu, this)
				}
				this.panel = panel;
				return panel
			},
			onReady : function() {
				com.bsoft.phis.pub.DepartmentManageModule.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			onTreeLoad : function(loader, node) {
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
				// 判断node是否有type
				if (node.hasChildNodes()) {
					node.eachChild(function(curNode) {
								if (!curNode.type) {
									curNode
											.setIcon(ClassLoader.appRootOffsetPath
													+ "images/close.png");
								}
							});
				}
			},
			onExpandNode : function(node) {
				if (node.getDepth() > 0 && !node.type) {
					node.setIcon(ClassLoader.appRootOffsetPath
							+ "images/open.png");
					// 判断node是否有type
				}
			},
			onCollapseNode : function(node) {
				if (node.getDepth() > 0 && !node.type) {
					node.setIcon(ClassLoader.appRootOffsetPath
							+ "images/close.png");
				}

			},
			onCatalogChanage : function(node, e) {
				// alert(Ext.encode(node.getUI().iconCls));
				var tbar = this.panel.getTopToolbar();
				var type = node.attributes.type
				var id = node.id;
				// alert(id)
				// if (node.attributes.type && node.attributes.type != "a"
				// && node.attributes.type != "b") {// 选中的是机构信息
				// tbar.getComponent('newKS').setDisabled(true);
				// tbar.getComponent('uptKS').setDisabled(true);
				// tbar.getComponent('delKS').setDisabled(true);
				// }
				// else
				if (type == "a" || type == "b") {// 选中科室
					tbar.getComponent('newKS').setDisabled(true);
					tbar.getComponent('uptKS').setDisabled(true);
					tbar.getComponent('delKS').setDisabled(true);
				} else if (type == "c" || type == "d") {
					tbar.getComponent('newKS').setDisabled(false);
					tbar.getComponent('uptKS').setDisabled(true);
					tbar.getComponent('delKS').setDisabled(true);
				}

				else {// 选中科室
					if (node.hasChildNodes()) {
						tbar.getComponent('newKS').setDisabled(false);
						tbar.getComponent('uptKS').setDisabled(false);
						tbar.getComponent('delKS').setDisabled(true);
					} else {
						tbar.getComponent('newKS').setDisabled(false);
						tbar.getComponent('uptKS').setDisabled(false);
						tbar.getComponent('delKS').setDisabled(false);
					}
				}

				var initCnd = this.initCnd
				var queryCnd = this.queryCnd
				if (!isNaN(id)) {
					// 根据节点类型 判断是机构还是科室 科室按科室代码查询 机构按机构代码LIKE查询
					if (type) {
						// 特殊处理: 如果是医院类型,只查本院数据不检索下级村站数据
						if (type == 'c' || type == 'd') {
							cnd = ['eq', ['$', 'a.JGID'], ['s', id]]
						} else if (type == 'a' || type == 'b') {
							cnd = ['like', ['$', 'a.JGID'], ['s', id + '%']]
						} else {
							return;
						}
					} else {
						cnd = ['eq', ['$', 'a.KSDM'], ['d', id]]
					}
					// cnd = ['eq', ['$', 'a.KSDM'], ['s', node.id]]
					this.initCnd = cnd;
					this.navCnd = cnd
					this.requestData.cnd = cnd
				}
				this.refresh()
			},
			onContextMenu : function(node, e) {
				if (node.isLeaf()) {
					return
				}
				node.select()
				// if (node.attributes.type) {// 选中的是机构信息
				//
				// } else {// 选中科室
				//
				// }
				var actions = [{
							id : "create",
							name : "新建",
							iconCls : "add"
						}, {
							id : "update",
							name : "修改"
						}, {
							id : "remove",
							name : "删除"
						}]
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
				if (node.type && node.attributes.type != "a"
						&& node.attributes.type != "b") {// 选中的是机构信息
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name == "create") {
							it.show()
						} else {
							it.hide()
						}
					}
				} else if (node.attributes.type == "a"
						|| node.attributes.type == "b") {// 选中省市级
					return;
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						it.hide()
					}
				} else {// 选中科室
					if (node.hasChildNodes()) {
						for (var i = 0; i < actions.length; i++) {
							var it = menu.items.itemAt(i)
							if (it.name == "create" || it.name == "update") {
								it.show()
							} else {
								it.hide()
							}
						}
					} else {
						for (var i = 0; i < actions.length; i++) {
							var it = menu.items.itemAt(i)
							it.show()
						}
					}
				}
				/*
				 * if (!node.attributes.JGID) {// 机构 for (var i = 0; i <
				 * actions.length; i++) { var it = menu.items.itemAt(i) if
				 * (it.name == "create") { it.show() } else { it.hide() } } }
				 * else { // 科室 for (var i = 0; i < actions.length; i++) { var
				 * it = menu.items.itemAt(i) it.show() } }
				 */

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
				// var title = node.text + '维护'
				// 判断选择节点是否是科室
				item.cmd = "update";
				var schema = "SYS_Office";
				if (item.name == "create") {
					this.cmd = "create";
					item.title = "新增科室";
					item.cmd = "create";
				} else if (item.name == "update") {
					this.cmd = "update";
					item.title = "修改科室";
					item.cmd = "update";
				}
				this.loadMenuModule(cls, schema, item, node)
				return
			},
			doMenuRemove : function(item) {
				var node = this.tree.getSelectionModel().getSelectedNode()
				Ext.Msg.show({
							title : '确认删除科室[' + node.text + ']',
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
							serviceId : "departmentManageService",
							serviceAction : "removeDepartment",
							schema : "SYS_Office",
							body : {
								pkey : node.id
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								node.remove();
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			loadMenuModule : function(cls, entryName, item, node) {
				if (this.loading) {
					return
				}
				var cmd = "menu"
				var cfg = {}
				cfg.title = item.title
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
				var m = this.midiModules[entryName]
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
										this.midiModules[entryName] = module
										this.openMenuModule(entryName, item,
												node)
									}, this])
				} else {
					Ext.apply(m, cfg)
					this.openMenuModule(entryName, item, node)
				}
			},
			openMenuModule : function(cmd, item, node) {
				var module = this.midiModules[cmd]
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
				/*
				 * if (!loadNode) { alert('请先选择员工科室信息！');
				 * module.getWin().hide(); return; }
				 */
				var nid = loadNode.id;
				var jgid = null;
				if (loadNode.type) {
					jgid = loadNode.id;
				} else {
					jgid = loadNode.attributes.JGID
				}
				module.data.KSDM = nid;
				module.data.JGID = jgid;
				// var KSDM = module.form.getForm().findField("KSDM");
				// var JGID = module.form.getForm().findField("JGID");
				// 判断是否是第一次加载
				// if (JGID.store.getCount() == 0) {
				// var _ctx = this;
				// setTimeout(function() {
				// _ctx.setFirstData(module.form.getForm(), jgid,
				// nid);
				// }, 300);// 第一次设置
				// } else {
				// KSDM.setValue(nid);
				// JGID.setValue(jgid);
				// }
				// KSDM.setDisabled(true);
			},
			// setFirstData : function(form, pjgid, nid) {
			// var JGID = form.findField("JGID")
			// var KSDM = form.findField("KSDM");
			// JGID.setValue(pjgid);
			// KSDM.setValue(nid);
			// },
			openModule : function(cmd, r, xy) {
				var node = this.tree.getSelectionModel().getSelectedNode();
				// /alert(node.attributes.type)
				if (node.attributes.type && cmd == "create") {
					MyMessageTip.msg("提示", "请选择机构科室后,再新增员工", true);
					return;
				}
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					win.setTitle(module.title)
					// win.setPagePosition(300, 50);
					win.show()
					var xy = win.getPosition();
					win.setPagePosition(xy[0], 50);
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doNew();
								this.afterOpenModule(module);
								break;
							case "read" :
							case "update" :
								module.loadData()
								// module.loadUserData();
						}
					}
				}
			},
			onDepartmentSave : function() {
				var loadNode = this.tree.getSelectionModel().getSelectedNode();
				if (this.cmd == "create") {
					if (loadNode.isExpanded()) {
						// if (loadNode.hasChildNodes()) {
						// for (var i = 0; i < loadNode.childNodes.length; i++)
						// {
						// loadNode.childNodes[i].remove();
						// }
						// }
						this.tree.getLoader().load(loadNode);
					}

					// this.onExpandNode(loadNode);
					loadNode.expand();

				} else {
					var id = loadNode.id;
					var parentNode = loadNode.parentNode;
					for (var i = 0; i < parentNode.childNodes.length; i++) {
						parentNode.childNodes[i].remove();
					}
					this.tree.getLoader().load(parentNode);
					parentNode.expand();
					this.afterLoad(parentNode, id);
					// setTimeout(function(){var node =
					// parentNode.findChild("id", id);node.select();},"1000");
					/*
					 * while (parentNode.childNodes.length==0) { } loadNode =
					 * parentNode.findChild("id", id); loadNode.select();
					 */

				}
			},
			afterLoad : function(parentNode, id) {
				var node = parentNode.findChild("id", id);
				var this1 = this;
				if (node != null) {
					node.select();
				} else {
					setTimeout(function() {
								this1.afterLoad(parentNode, id);
							}, "100");
				}
			},
			onUpdateTreeLoad : function(parentNode) {
				parentNode.expand();
				node = parentNode.findChild("id", id);
				node.select();
				this.tree.un("load", this.onUpdateTreeLoad, this);
			},
			// doCndQuery : function() {
			// var initCnd = this.initCnd
			// var navCnd = this.navCnd
			// var index = this.cndFldCombox.getValue()
			// var it = this.schema.items[index]
			//
			// if (!it) {
			// return;
			// }
			// var v = this.cndField.getValue()
			// this.requestData.pageNo = 1
			// var pt = this.grid.getBottomToolbar()
			// if (pt) {
			// pt.cursor = 0;
			// }
			// if (v == null || v == "") {
			// var cnd = [];
			//
			// this.queryCnd = null;
			// if (navCnd) {
			// if (initCnd) {
			// cnd.push("and")
			// cnd.push(navCnd)
			// cnd.push(initCnd)
			// } else {
			// cnd = navCnd
			// }
			// }
			// this.requestData.cnd = cnd
			// this.refresh()
			// return
			// }
			// var refAlias = it.refAlias || "a"
			// var cnd = ['eq', ['$', refAlias + "." + it.id]]
			// if (it.dic) {
			// if (it.dic.render == "Tree") {
			// var node = this.cndField.selectedNode
			// if (!node.isLeaf()) {
			// cnd[0] = 'like'
			// cnd.push(['s', v + '%'])
			// } else {
			// cnd.push(['s', v])
			// }
			// } else {
			// cnd.push(['s', v])
			// }
			// } else {
			// switch (it.type) {
			// case 'int' :
			// cnd.push(['i', v])
			// break;
			// case 'double' :
			// case 'bigDecimal' :
			// cnd.push(['d', v])
			// break;
			// case 'string' :
			// cnd[0] = 'like'
			// cnd.push(['s', v + '%'])
			// break;
			// case "date" :
			// v = v.format("Y-m-d")
			// cnd[1] = [
			// '$',
			// "str(" + refAlias + "." + it.id
			// + ",'yyyy-MM-dd')"]
			// cnd.push(['s', v])
			// break;
			// }
			// }
			// this.queryCnd = cnd
			// if (initCnd || navCnd) {
			// cnd = ['and', cnd]
			// if (initCnd) {
			// cnd.push(initCnd)
			// }
			// if (navCnd) {
			// cnd.push(navCnd)
			// }
			// }
			// this.requestData.cnd = cnd
			// this.refresh()
			// },
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
			},
			saveToServer : function(saveData, r) {
				var entryName = this.schema.id

				if (!this
						.fireEvent("beforeSave", entryName, "uptate", saveData)) {
					return;
				}
				this.tree.el.mask("在正保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : "update",
							schema : entryName,
							body : saveData
						}, function(code, msg, json) {
							this.tree.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							} else {
								this.grid.store.remove(r)
								this.fireEvent("save", entryName, json.body,
										this.op)
							}
						}, this)// jsonRequest
			},
			doNewJG : function() {
				alert("暂时无法维护机构信息！");
			},
			doNewKS : function() {
				var item = {
					title : "新增科室",
					cmd : "create",
					name : "create"
				}
				this.addNew(item);
			},
			doUptKS : function() {
				var item = {
					title : "修改科室",
					cmd : "update",
					name : "update"
				}
				this.addNew(item);
			},
			doDelKS : function() {
				this.doMenuRemove();
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("在正删除数据...");
				var compositeKey;
				if (this.isCompositeKey) {
					compositeKey = {};
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						compositeKey[this.schema.pkeys[i]] = r
								.get(this.schema.pkeys[i]);
					}
				}
				phis.script.rmi.jsonRequest({
					serviceId : "departmentManageService",
					pkey : r.id,
					body : compositeKey,
					schema : this.entryName,
					serviceAction : "removeDepartmentStaff", // 按钮标识
					module : this.grid._mId
						// 增加module的id
					}, function(code, msg, json) {
					this.unmask()
					if (code < 300) {
						this.store.remove(r)
						this.fireEvent("remove", this.entryName, 'remove',
								json, r.data)
					} else {
						this.processReturnMsg(code, msg, this.doRemove)
					}
				}, this)
			},
			doDisabled : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				var date = {};
				var msg;
				date["YGDM"] = r.get("YGDM");
				if ((r.get("ZFPB") == 0)) {
					date["ZFPB"] = 1 // modified by zhangxw
					msg = "确认禁用员工"
				} else {
					date["ZFPB"] = 0 // modified by zhangxw
					msg = "确认取消禁用员工"
				}
				Ext.Msg.confirm("请确认", msg + "【" + r.get("YGXM") + "】吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在修改数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.serviceAction,
											body : date
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												return;
											}
											var btns = this.grid
													.getTopToolbar();
											var btn = btns.find("cmd",
													"disabled");
											btn = btn[0];
											if (r.data.ZFPB == 0) {
												btn.setText(btn.getText()
														.replace("禁用", "取消禁用"));
											} else {
												btn.setText(btn.getText()
														.replace("取消禁用", "禁用"));
											}
											this.refresh();
										}, this)
							}
						}, this);
			},
			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "disabled");
				btn = btn[0];
				if (r.data.ZFPB == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("禁用", "取消禁用"));
				} else {
					btn.setText(btn.getText().replace("取消禁用", "禁用"));
				}

			},
			// 刚打开页面时候默认选中数据,这时候判断下作废按钮
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
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
			},
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
			onRendererZFPB : function(value, metaData, r, row) {
				var ZFPB = r.get("ZFPB");
				var src = (ZFPB == 1) ? "no" : "yes";
				if (ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
					+ "resources/phis/resources/images/" + src + ".png'/>";
				} else {
					return "";
				}
			},
			doChangeDepartment:function(){
				alert(777)
			}
		})