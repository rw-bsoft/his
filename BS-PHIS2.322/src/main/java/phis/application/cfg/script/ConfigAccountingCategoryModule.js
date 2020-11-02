$package("phis.application.cfg.script")

/**
 * 核算类别维护
 */
$import("phis.script.common", "phis.script.SimpleModule",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.ConfigAccountingCategoryModule = function(cfg) {
	this.westWidth = cfg.westWidth || 250
	this.showNav = true
	this.height = 450

	phis.application.cfg.script.ConfigAccountingCategoryModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigAccountingCategoryModule,
		phis.script.SimpleModule, {
			active : function() {
				if (this.node) {
					if (this.TRDList) {
						this.TRDList.loadZfbl(this.node.id);
					}
				}
			},
			initPanel : function(grid) {
				if (!this.mainApp['phis'].treasuryId
						&& (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId)) {
					Ext.MessageBox.alert("提示", "您还没有选择库房， 请先选择库房 !");
					return;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0
						&& this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				var navDic = this.navDic
				var filters="";
				if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
					filters = "['eq',['$','item.properties.JGID'],['s',"
							+ this.mainApp['phisApp'].deptId + "]]";
				} else {
					if (this.mainApp['phis'].treasuryId) {
						filters = "['in',['$','item.properties.ZBLB'],["
								+ this.mainApp['phis'].treasuryKfzb + "]]";
					} else {
						filters = "['or',['eq',['$','JGID'],['s',"
								+ this.mainApp['phisApp'].deptId
								+ "]],['eq',['$','item.properties.JGID'],['s',"
								+ this.mainApp.topUnitId + "]]]";
					}
				}
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							sliceType : 5,
							parentKey : this.navParentKey,
							filter : filters,
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
										region : 'west',
										width : this.westWidth,
										items : tf.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 250,
										items : this.getForm()
									}]
						});
				this.tree = tf.tree
				// grid.__this = this
				tf.tree.on("click", this.beforeCatalogChanage, this);
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
			getForm : function() {
				var module = this.createModule("getForm", this.refForm);
				var form = module.initPanel();
				this.form = form.getForm();
				this.hslbForm = form.getComponent("hslbxx");
				this.zblbForm = form;
				return form;

			},
			beforeCatalogChanage : function(node, e) {
				if (this.currentNode) {
					this.lastNode = this.currentNode;
				}
				this.currentNode = node;
				this.currentE = e;
				var tbar = this.panel.getTopToolbar();
				if (this.isDataChanged == true) {
					Ext.MessageBox.show({
								title : '提示',
								msg : '数据发生变更，是否保存数据？',
								buttons : Ext.MessageBox.YESNOCANCEL,
								fn : this.messageTeate,
								icon : Ext.MessageBox.QUESTION,
								scope : this
							});
				} else {
					this.onCatalogChanage(node, e);
				}
			},
			onCatalogChanage : function(node, e) {
				this.clickAction = "update";
				var tbar = this.panel.getTopToolbar();
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					var data = {
						"HSLB" : node.id
					};
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "getHslb",
								body : data
							});
					if (r.code == 613) {
						// MyMessageTip.msg("提示", "不能维护公共账簿核算类别", true);
						tbar.getComponent('create').setDisabled(true);
						tbar.getComponent('save').setDisabled(true);
						tbar.getComponent('remove').setDisabled(true);
						this.hslbForm.hide();
						return;
					} else if (r.code == 614) {
						MyMessageTip.msg("提示", "该账簿核算类别已有物资使用,不能维护", true);
						tbar.getComponent('create').setDisabled(true);
						tbar.getComponent('save').setDisabled(true);
						tbar.getComponent('remove').setDisabled(true);
						this.hslbForm.hide();
						return;
					}
				}
				if (node.getDepth() == 1) {
					this.hslbForm.hide();

				} else {
					this.hslbForm
							.setWidth(this.zblbForm.getComponent("zblbxx").width);
					this.hslbForm.show();
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "queryNodInfo",
							pk : node.id
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLoadReport);
				}
				var nodInfo = ret.json.ret;
				this.ZBLB = nodInfo.ZBLB;

				// tbar.getComponent('save').setDisabled(true);
				if (node.getDepth() == 1) {
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('save').setDisabled(true);
					tbar.getComponent('remove').setDisabled(true);
				} else if (node.hasChildNodes()) {// 有子节点
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('save').setDisabled(false);
					tbar.getComponent('remove').setDisabled(true);
				} else {// 没有子节点
					tbar.getComponent('create').setDisabled(false);
					tbar.getComponent('save').setDisabled(false);
					tbar.getComponent('remove').setDisabled(false);
				}

				this.form.findField("HSMC").on("change", function() {
							this.isDataChanged = true
						}, this);
				this.form.findField("PLSX").on("change", function() {
							this.isDataChanged = true
						}, this);

				this.form.findField("b.ZBMC").setValue(nodInfo.ZBMC == null
						? ""
						: nodInfo.ZBMC);
				this.form.findField("b.JGID")
						.setValue(nodInfo.JGID == this.mainApp.topUnitId
								? "1"
								: "0");
				this.form.findField("SJHS").setValue(node.getDepth() == 2
						? ""
						: nodInfo.SJHS);
				this.form.findField("HSMC").setValue(nodInfo.HSMC);
				this.form.findField("PLSX").setValue(nodInfo.PLSX == null
						? ""
						: nodInfo.PLSX);
				node.expand();
			},
			onContextMenu : function(node, e) {
				if (node.isLeaf()) {
					return
				}
				var tbar = this.panel.getTopToolbar();

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
				if (node.getDepth() == 1) {
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name == "create") {
							it.show()
							if (tbar.getComponent('create').disabled == true) {
								it.setDisabled(true)
							} else {
								it.setDisabled(false)
							}
						} else {
							it.hide()
						}
					}
				} else if (node.hasChildNodes()) {// 有子节点
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name == "create" || it.name == "remove") {
							it.show()
							if (it.name == "create") {
								if (tbar.getComponent('create').disabled == true) {
									it.setDisabled(true)
								} else {
									it.setDisabled(false)
								}
							}
							if (it.name == "remove") {
								if (tbar.getComponent('remove').disabled == true) {
									it.setDisabled(true)
								} else {
									it.setDisabled(false)
								}
							}
						} else {
							it.hide()
						}
					}
				} else { // 没有子节点
					for (var i = 0; i < actions.length; i++) {
						var it = menu.items.itemAt(i)
						if (it.name != "save") {
							it.show()
							if (tbar.getComponent('create').disabled == true
									|| tbar.getComponent('remove').disabled == true) {
								it.setDisabled(true)
							} else {
								it.setDisabled(false)
							}
						}
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
				var node = this.tree.getSelectionModel().getSelectedNode();
				var id = node.id
				this.hslbForm.show();
				// this.form.findField("JGID").setValue("");
				this.form.findField("SJHS").setValue(node.id);
				this.sjhs = node.id;
				this.form.findField("HSMC").setValue("");
				this.form.findField("PLSX").setValue(0);
				this.clickAction = "create";
				var tbar = this.panel.getTopToolbar();
				tbar.getComponent('save').setDisabled(false);

			},
			doCreate : function(item, e, newGroup) {
				this.addNew(item);
			},
			doRemove : function(item, e, newGroup) {
				this.doMenuRemove(item);
			},
			doMenuRemove : function(item) {
				var node = this.tree.getSelectionModel().getSelectedNode();
				Ext.Msg.show({
							title : '提示',
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
							icon : Ext.MessageBox.QUESTION,
							scope : this
						})
			},
			processMenuRemove : function(node) {
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "deleteNode",
							pk : node.id
						}, function(code, msg, json) {
							if (code < 300) {
								node.remove();
							} else {
								if (code == 616) {
									MyMessageTip.msg("提示",
											"该核算类别已有物资使用 ,不能删除!", true);
								} else {
									this.processReturnMsg(code, msg);
								}
							}
						}, this);
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
			afterNodeSave : function(addedNode) {
				MyMessageTip.msg("提示", "数据已保存!", true);
				this.isDataChanged = false;
				// var loadNode =
				// this.tree.getSelectionModel().getSelectedNode();
				// loadNode.remove();//先删除要加载的节点信息。
				this.tree.getLoader().load(addedNode);// 重新加载指定的节点
				this.tree.expandAll();
				this.form.findField("SJHS").store.reload();
			},
			doSave : function(item) {
				var HSLB_XX = {};
				HSLB_XX.JGID = this.mainApp['phisApp'].deptId;
				HSLB_XX.HSMC = this.form.findField("HSMC").getValue();
				if (HSLB_XX.HSMC == "" || HSLB_XX.HSMC == "null") {
					MyMessageTip.msg("提示", "核算名称不能为空!", true);
					return;
				}
				if (HSLB_XX.HSMC.length > 15) {
					MyMessageTip.msg("提示", "核算名称过长!", true);
					return;
				}
				HSLB_XX.ZBLB = this.ZBLB;
				var SJHS = this.form.findField("SJHS").getValue();
				if (SJHS != "") {
					HSLB_XX.SJHS = SJHS;
				} else {
					HSLB_XX.SJHS = this.sjhs
				}
				HSLB_XX.PLSX = this.form.findField("PLSX").getValue();
				if (HSLB_XX.PLSX) {
					var s = /^[0-9]+$/i;
					if (!s.test(HSLB_XX.PLSX)) {
						MyMessageTip.msg("提示", "排列顺序只能输入数字", true);
						return false;
					}
					if (HSLB_XX.PLSX.length > 4) {
						MyMessageTip.msg("提示", "排列顺序输入过长", true);
						return false;
					}
				}
				HSLB_XX.ZBMC = this.form.findField("b.ZBMC").getValue();
				if (this.clickAction == "create") {
					if (item) {
						this.addedNode = this.currentNode;
					} else {
						this.addedNode = this.lastNode;
					}
					this.action = "saveNodInfo";
				} else {
					if (item) {
						HSLB_XX.HSLB = this.currentNode.id;
						this.addedNode = this.currentNode;
					} else {
						HSLB_XX.HSLB = this.lastNode.id;
						this.addedNode = this.lastNode;
					}
					this.action = "updateNodInfo";
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.action,
							body : HSLB_XX
						});
				if (ret.code > 300) {
					if (ret.code == 615) {
						MyMessageTip.msg("提示", "同一账簿下核算名称不能重复!", true);
					} else {
						this.processReturnMsg(ret.code, ret.msg,
								this.doLoadReport);
					}
				} else {
					this.afterNodeSave(this.addedNode);
				}
				// alert(Ext.encode(HSLB_XX))

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
			messageTeate : function(btn, text) {
				if (btn == "yes") {
					this.doSave();
					this.onCatalogChanage(this.currentNode, this.currentE);
				}
				if (btn == "no") {
					this.isDataChanged = false;
					this.onCatalogChanage(this.currentNode, this.currentE);
				}
				if (btn == "cancel") {
					this.lastNode.select();
					this.currentNode = this.lastNode;
				}
			}

		})