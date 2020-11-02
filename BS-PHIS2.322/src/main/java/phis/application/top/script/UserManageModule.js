$package("com.bsoft.phis.pub")

/*
 * 科室员工维护界面module liyl 2012.3.29
 */

$import("app.modules.common", "com.bsoft.phis.SimpleList",
		"util.dictionary.TreeDicFactory")

com.bsoft.phis.pub.UserManageModule = function(cfg) {
	// this.title = "机构用户管理"
	this.createForm = "com.bsoft.phis.pub.UserManageModule"
	this.autoLoadSchema = true
	this.activeModules = {}
	this.midiMenus = {}
	this.pModules = {}
	// this.mutiSelect = true
	this.westWidth = cfg.westWidth || 250
	this.showNav = true
	this.width = 950;
	this.height = 350
	cfg.autoLoadData = false;
	// cfg.winState = [300,80]
	this.serviceId = "userManageService";
	this.actionId = "userDisabled";
	this.listServiceId = "userList"
	Ext.apply(this, app.modules.common)
	com.bsoft.phis.pub.UserManageModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(com.bsoft.phis.pub.UserManageModule, com.bsoft.phis.SimpleList, {
			// active : function() {
			// if (this.node) {
			// try {
			// //关闭事件仍会触发tabchange事件
			// this.refresh()
			// } catch (e) {
			//
			// }
			// }
			// },
			warpPanel : function(grid) {
				if (!this.showNav) {
					return grid
				}
				var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.navDic,
							parentKey : this.navParentKey || {},
							rootVisible : this.rootVisible || false
						})
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							// tbar : tbar,
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
										items : grid
									}]
						});
				this.tree = tree
				grid.__this = this
				this.grid = grid;
				this.tree.on("click", this.onCatalogChanage, this)
				this.tree.on("load", this.onTreeLoad, this);
				this.panel = panel;
				return panel
			},
			onReady : function() {
				com.bsoft.phis.pub.UserManageModule.superclass.onReady
						.call(this);
				// this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
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

			},
			onCatalogChanage : function(node, e) {
				var type = node.attributes.type
				var initCnd = this.initCnd;
				var queryCnd = this.queryCnd
				if (type == "a" || type == "b") {
					this.initCnd = ['like', ['$', 'manaUnitId'],
							['s', node.id + "%"]]
				} else if (type == "c" || type == "d") {
					this.initCnd = ['eq', ['$', 'manaUnitId'], ['s', node.id]]
				} else {
					return;
				}
				// this.resetFirstPage()
				cnd = this.initCnd;
				this.navCnd = cnd
				this.requestData.cnd = cnd
				this.refresh()
				this.node = node;
			},
			openModule : function(cmd, r, xy) {
				var loadNode = this.tree.getSelectionModel().getSelectedNode();
				if (!loadNode) {
					alert('请先选择员工科室信息！');
					return;
				}
				var module = this.midiModules[cmd]
				if (module) {
					// var store = this.grid.getStore();
					// var n = store.getCount()
					// var ygbhs = []
					// for (var i = 0; i < n; i++) {
					// var r = store.getAt(i);
					// ygbhs.push(r.get("loginName"))
					// }
					// module.ygbhs = ygbhs;
					var win = module.getWin()
					win.setTitle(module.title);
					win.show()
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doNew();
								break;
							case "read" :
							case "update" :
								module.loadData();
						}
					}
				}
			},
			doDisabled : function() {
				var cm = this.grid.getSelectionModel();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var date = {};
				date["userId"] = r.get("userId");
				date["status"] = r.get("userId");
				date["status"] = "1";
				var msg = "确认禁用";
				if (r.get("status") == 1) {
					date["status"] = "0";
					msg = "确认启用";
				}
				Ext.Msg.confirm("请确认", msg + "【" + r.get("userName") + "】吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在修改数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.actionId,
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
											if (r.data.status == 0) {
												btn.setText(btn.getText()
														.replace("禁用", "启用"));
											} else {
												btn.setText(btn.getText()
														.replace("启用", "禁用"));
											}
											this.refresh();
										}, this)
							}
						}, this);
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
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
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
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "disabled");
				btn = btn[0];
				if (r.data.status == 1) {
					if (btn.getText().indexOf("启用") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("禁用", "启用"));
				} else {
					btn.setText(btn.getText().replace("启用", "禁用"));
				}

			},
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
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
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "userList";
				// this.requestData.body = body;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
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
					serviceId : "userManageService",
					pkey : r.id,
					body : compositeKey,
					schema : this.entryName,
					serviceAction : "removeUser", // 按钮标识
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

			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
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
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "pyCode") {
								v = v.toUpperCase();
							}
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
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			onRendererStatus : function(value, metaData, r, row) {
				var ZFPB = r.get("status");
				var src = (ZFPB == 1) ? "no" : "yes";
				if (ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
					+ "resources/phis/resources/images/" + src + ".png'/>";
				} else {
					return "";
				}
			}
		})