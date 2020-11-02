$package("phis.application.cfg.script")
$import("phis.script.common", "phis.script.SimpleList",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.SubstancesClassModule = function(cfg) {
	phis.application.cfg.script.SubstancesClassModule.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.cfg.script.SubstancesClassModule,
				phis.script.SimpleList,
				{
					warpPanel : function(grid) {
						if (this.mainApp['phis'].treasuryId == null
								|| this.mainApp['phis'].treasuryId == ""
								|| this.mainApp['phis'].treasuryId == undefined) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryEjkf != 0) {
							Ext.MessageBox.alert("提示", "该库房不是一级库房!");
							return;
						}

						var navDic = this.navDic;
						var lbxh = this.mainApp['phis'].treasuryLbxh;
						if (lbxh == 0) {
							navDic = "phis.dictionary.AccountingCategory_tree";
						}
						var filters = "";
						if (navDic == "phis.dictionary.materialInformation_tree") {
							filters = "['and',['eq',['$','item.properties.LBXH'],['i',"
									+ this.mainApp['phis'].treasuryLbxh
									+ "]],['eq',['$','item.properties.JGID'],['s',"
									+ this.mainApp['phisApp'].deptId + "]]]";
						} else {
							filters = "['in',['$','item.properties.ZBLB'],["
									+ this.mainApp['phis'].treasuryKfzb + "]]";
						}
						var tree = util.dictionary.TreeDicFactory.createTree({
							id : navDic,
							parentKey : this.navParentKey || {},
							filter : filters,
							rootVisible : this.rootVisible || false
						})

						var tbar = [];
						var actions = this.actions;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							var btn = {}
							btn.id = action.id;
							btn.accessKey = "F1", btn.cmd = action.id
							btn.text = action.name,
									btn.iconCls = action.iconCls || action.id
							btn.handler = this.doAction;
							btn.name = action.id;
							btn.scope = this;
							tbar.push(btn);
						}
						var tbarshow = [];
						if (lbxh != 0) {
							tbarshow = tbar;
						}
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : tbarshow,
							items : [ {
								layout : "fit",
								split : true,
								// collapsible : true,
								bodyStyle : 'padding:5px 0',
								title : '',
								width : 240,
								region : 'west',
								items : tree
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								items : this.getCategoriesGoodsModule()
							} ]
						});
						this.tree = tree
						tree.on("click", this.onCatalogChanage, this)
						tree.expandAll()// 展开树
						tree.on("load", this.onTreeLoad, this);
						this.panel = panel;
						return panel
					},
					getCategoriesGoodsModule : function() {
						var module = this.createModule(
								"refCategoriesGoodsModule",
								this.refCategoriesGoodsModule);
						this.module = module;
						module.opener = this;
						return module.initPanel();
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
						if (this.mainApp['phis'].treasuryLbxh != 0) {
							this.beforeclose();
						}
						var ZDXH = node.id;
						if (!ZDXH) {
							return;
						}
						if (this.mainApp['phis'].treasuryLbxh != 0) {
							var xh = this.searchTreeMX(ZDXH);
							if (!xh) {
								MyMessageTip.msg("提示", "没有明细！", true);
								return;
							}
							this.module.classList.FLBM = xh.FLBM;
							this.module.classList.ZDXH = xh.ZDXH;
							this.module.classList.tree = this.tree;

							this.module.classList.loadData(xh.ZDXH);
						} else {
							this.module.classList.HSLB = ZDXH;
							this.module.classList.setButtonsState(
									[ 'updateStage' ], false);
							this.module.classList.loadData(ZDXH);
						}
						this.module.noClassList.loadData();
						if (this.mainApp['phis'].treasuryLbxh == 0) {
							this.module.classList.setButtonsState(
									[ 'updateStage' ], false);
						}
						node.expand();

					},
					searchTreeMX : function(ZDXH) {
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configSubstancesClassService",
							serviceAction : "queryNodInfo",
							ZDXH : ZDXH
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doLoadReport);
						}
						return ret.json.ret;
					},
					searchLbgz : function(LBXH, GZCC) {
						if (!LBXH || !GZCC) {
							MyMessageTip.msg("提示", "类别序号和规则层次都不能为空！", true);
							return;
						}
						var body = {};
						body["LBXH"] = LBXH;
						body["GZCC"] = GZCC;
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configSubstancesClassService",
							serviceAction : "queryLBXH",
							body : body
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doLoadReport);
						}
						return ret.json.ret;
					},
					// 新增树节点
					doAdd : function(num) {
						var loadNode = this.tree.getSelectionModel()
								.getSelectedNode();
						var nodeId = loadNode.id;
						var sum = loadNode.getDepth();
						if (sum == 0) {
							return;
						}
						if (!nodeId) {
							MyMessageTip.msg("提示", "请选择节点！", true);
							return;
						}
						var treeList = this.searchTreeMX(nodeId);

						if ((num == 1) && (treeList.SJFL == -1)) {
							MyMessageTip.msg("提示", "根节点不能修改！", true);
							return;
						}
						if (num == 1) {
							sum = sum - 1;
						}
						if (sum == 0) {
							sum = 1;
						}
						var LbgzList = this.searchLbgz(treeList.LBXH, sum);
						if (!LbgzList || LbgzList == 0) {
							MyMessageTip.msg("提示", "已是规则的最后一层，不能再新增物资分类!", true);
							return;
						}
						this.substancesClassForm = this.createModule(
								"substancesClassForm", this.addRef);
						this.substancesClassForm.on("save", this.onSave, this);
						this.substancesClassForm.ZDXH = treeList.ZDXH;
						this.substancesClassForm.LBXH = treeList.LBXH;
						this.substancesClassForm.FLMC = treeList.FLMC;
						this.substancesClassForm.SJFL = treeList.SJFL;
						this.substancesClassForm.FLBM = treeList.FLBM;
						this.substancesClassForm.GZXH = treeList.GZXH;
						this.substancesClassForm.GZCD = LbgzList.GZCD;

						this.substancesClassForm.tree = this.tree;
						if (num && (num == 1)) {
							this.substancesClassForm.oper = 'update';
							this.substancesClassForm.num = 1;
						} else {
							this.substancesClassForm.oper = 'create';
							this.substancesClassForm.num = 0;
						}
						this.substancesClassForm.initPanel();
						var win = this.substancesClassForm.getWin();
						win.add(this.substancesClassForm.initPanel());
						win.show();
						win.center();
						this.substancesClassForm.doNew();
					},
					doXg : function() {
						this.doAdd(1);
					},
					doRefresh : function() {
						var loadNode = this.tree.getSelectionModel()
								.getSelectedNode();
						this.tree.getLoader().load(loadNode);
						loadNode.expandAll();
					},
					doRemove : function() {
						var node = this.tree.getSelectionModel()
								.getSelectedNode();
						var nodeId = node.id;
						var sum = node.getDepth();
						if (sum == 0) {
							return;
						}
						if (!nodeId) {
							MyMessageTip.msg("提示", "请选择节点！", true);
							return;
						}
						if (node.getDepth() == 1) {
							MyMessageTip.msg("提示", "根节点不能删除！", true);
							return;
						}
						if (node.hasChildNodes()) {
							MyMessageTip.msg("提示", "请先删除子节点！", true);
							return;
						}
						var treeList = this.searchTreeMX(node.id);
						var ZDXH = treeList.ZDXH;
						Ext.Msg.show({
							title : '提示',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.MenuRemove(ZDXH, node);
								}
							},
							icon : Ext.MessageBox.QUESTION,
							scope : this
						})
					},
					MenuRemove : function(ZDXH, node) {
						this.mask("正在删除数据...")
						phis.script.rmi.jsonRequest({
							serviceId : "configSubstancesClassService",
							serviceAction : "deleteNode",
							nodeId : ZDXH
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
					beforeclose : function(tabPanel, newTab, curTab) {
						// 判断grid中是否有修改的数据没有保存
						if (this.module.classList.store.getCount() > 0) {
							for ( var i = 0; i < this.module.classList.store
									.getCount(); i++) {
								if (!this.module.classList.store.getAt(i).get(
										"FLXH")) {
									if (confirm('数据已经修改，是否保存?')) {
										return this.module.classList
												.doUpdateStage();
									} else {
										break;
									}
								}
							}
							this.module.classList.store.rejectChanges();
						}
						if (this.module.noClassList.store.getCount() > 0) {
							for ( var i = 0; i < this.module.noClassList.store
									.getCount(); i++) {
								if (this.module.noClassList.store.getAt(i).get(
										"FLXH")) {
									if (confirm('数据已经修改，是否保存?')) {
										return this.module.classList
												.doUpdateStage();
									} else {
										break;
									}
								}
							}
							this.module.noClassList.store.rejectChanges();
						}

						return true;
					}

				});