/**
 * 网格地址列表
 * 
 * @author : chenhb
 */
$package("chis.application.ag.script")
$import("app.modules.list.TreeNavListView",
		"chis.application.ag.script.AreaGridMaintenanceForm",
		"chis.application.ag.script.GenBuildingForm", "chis.application.ag.script.GenUnitForm")

chis.application.ag.script.AreaGridMaintenanceList = function(cfg) {
	cfg.westWidth = 300;
	cfg.rootVisible = true
	this.initCnd = ['like', ['$', 'regionCode'],
						['s', cfg.navParentKey + '%']]
	chis.application.ag.script.AreaGridMaintenanceList.superclass.constructor.apply(this,
			[cfg])
	
	this.on('firstRowSelected', this.onRowSelect)
	this.on('rowclick', this.onRowSelect)
}
Ext.extend(chis.application.ag.script.AreaGridMaintenanceList,
		app.modules.list.TreeNavListView, {
			init : function() {
				this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						})
				this.requestData = {
					serviceId : "chis.agService",
					serviceAction : "listAreaGrid",
					method:"execute",
					schema : this.entryName,
					cnd : this.initCnd,
					pageSize : this.pageSize || 25,
					pageNo : 1
				}
				if (this.serverParams) {
					Ext.apply(this.requestData, this.serverParams)
				}
				if (this.autoLoadSchema) {
					this.getSchema();
				}
			},
			loadData : function() {
				
				this.requestData.cnd = ['eq', ['$', 'parentCode'],
						['s', this.navParentKey]]
				chis.application.ag.script.AreaGridMaintenanceList.superclass.loadData
						.call(this)
			},
			refresh : function() {
				if (this.store)
					this.store.load()
			},
			initMenu : function() {
				var cmenu;
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
			},
			onReady : function() {
				
				this.on("remove", function(entryName, action, json, data) {
					var id = data["regionCode"];
					var node = this.findNodeByRegionCode(this.selectedNode, id);
					if (node) {
						node.remove();
					}

				}, this)
				chis.application.ag.script.AreaGridMaintenanceList.superclass.onReady
						.call(this)
				this.initMenu();
				if (!this.tree){
					return;
				}
				var root = this.tree.getRootNode()
				if(root && !root.attributes["key"]){
					Ext.Msg.alert("提示", "基础数据维护不完整，无法进行网格地址维护！");
					return;
				}
				var rightCllick = this.midiMenus["regionCodeMenu"]
				if (!rightCllick) {
					var common = {
						id : 'rightClickCont',
						items : [{
									text : '新建',
									iconCls : "create",
									// 增加菜单点击事件
									handler : this.doCreate,
									scope : this
								}, {
									handler : function() {
										this
												.openUpdate(
														this.selectedNode.attributes["key"],
														this.selectedNode.parentNode,
														this.selectedNode.childNodes);
									},
									scope : this,
									iconCls : "update",
									text : '查看'
								}, 
									{
									iconCls : "remove",
									handler : this.batchDelete,
									text : '删除',
									scope : this
								}, 
									{
									handler : this.batchResidenceCommunity,
									text : '快速生成网格地址',
									scope : this
								}, {
									handler : this.batchUnit,
									text : '快速生成户级网格地址',
									scope : this
								}]
					}

					// 定义右键菜单
					rightClick = new Ext.menu.Menu(common);
					this.midiMenus["regionCodeMenu"] = rightClick;
				}
				this.tree.on('contextmenu', function(node, event) {
							event.preventDefault();
							// 如果选中的是顶层节点,根据编码取查询父节点并拼接到这个节点上去.
							if (!node.parent
									&& node.attributes["key"] == this.navParentKey) {
								this.setParentForTopNode(node);
							}
							this.tree.selectPath(node.getPath())
							node.expand();
							var isFamily = node.attributes["isFamily"];
							this.selectedNode = node;
							if (isFamily == "1") {
								rightClick.items.itemAt(0).disable();
								rightClick.items.itemAt(1).enable();
								rightClick.items.itemAt(2).enable();
								rightClick.items.itemAt(3).disable();
								rightClick.items.itemAt(4).disable();
							} else if (isFamily >= 'd' && isFamily < "e") {// 区级
								// 只能新建
								rightClick.items.itemAt(0).enable();
								rightClick.items.itemAt(1).disable();
								rightClick.items.itemAt(2).disable();
								rightClick.items.itemAt(3).disable();
								rightClick.items.itemAt(4).disable();
							} else if (isFamily > 'e') {// 街道
								rightClick.items.itemAt(0).enable();
								if (node.parentNode == null) {
									rightClick.items.itemAt(1).disable();
									rightClick.items.itemAt(2).disable();
								} else {
									rightClick.items.itemAt(1).enable();
									rightClick.items.itemAt(2).enable();
								}
								if (isFamily >= 'f') {
									rightClick.items.itemAt(3).enable();
									rightClick.items.itemAt(4).enable();
								} else {
									rightClick.items.itemAt(3).disable();
									rightClick.items.itemAt(4).disable();
								}
							} else {
								rightClick.items.itemAt(0).disable();
								rightClick.items.itemAt(1).disable();
								rightClick.items.itemAt(2).disable();
								rightClick.items.itemAt(3).disable();
								rightClick.items.itemAt(4).disable()
							}
							this.onCatalogChanage(node, event)

							var viewHeight = Ext.lib.Dom.getViewHeight();
							var pageY = event.getPageY();
							var menuLen = 150
							if ((viewHeight - pageY) >= menuLen) {
								rightClick.showAt(event.getXY());
							} else {
								rightClick.showAt([event.getPageX(),
										viewHeight - menuLen]);
							}
						}, this);

				this.tree.on("click", function(node, event) {
					this.selectedNode = node;
					// 如果选中的是顶层节点,根据编码取查询父节点并拼接到这个节点上去.
					if (!node.parent
							&& node.attributes["key"] == this.navParentKey) {
						this.setParentForTopNode(node);
					}
					node.expand();
						// this.onCatalogChanage(node, event);
					}, this)
			},
			// 如果当前节点是顶层节点,为之设置父节点,从后台查询得来.
			setParentForTopNode : function(node) {
				if (!node)
					return;
				var regionCode = node.attributes["key"];
				if (!regionCode || regionCode.length <= 6) {
					return;
				}
				var parentCode = regionCode.substring(0, regionCode.length - 3);

				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.agService",
							serviceAction : "getNodeInfo",
							method:"execute",
							regionCode : parentCode
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var nodeInfo = result.json.nodeInfo;
				if (!nodeInfo)
					return;
				var parentNode = new Ext.tree.TreeNode({
							id : nodeInfo["regionCode"]
						})
				parentNode.attributes["key"] = nodeInfo["regionCode"];
				parentNode.attributes["text"] = nodeInfo["regionName"];
				parentNode.attributes["isFamily"] = nodeInfo["isFamily"];
				parentNode.ownerTree = this.tree;
				parentNode.appendChild(node);
				node.parent = parentNode;
			},

			batchResidenceCommunity : function() {
				if (!this.selectedNode) {
					return;
				}
				regionCode = this.selectedNode.attributes["key"]
				if (regionCode.length >= 24) {
					Ext.Msg.alert("提示", "已到达网格地址最大层数,无法新建！");
					return;
				}
				var using = this.checkRegionUsing(regionCode);
				if (using) {
					Ext.Msg.alert("提示", "该节点已经被使用，不能新建子节点！");
					return;
				}
				var genBuildingView = this.midiModules["genBuildingView"];
				if (!genBuildingView) {
					genBuildingView = new chis.application.ag.script.GenBuildingForm({
								selectedNode : this.selectedNode
							})
					genBuildingView.on("save", this.onSave, this);
					this.midiModules["genBuildingView"] = genBuildingView;
				}
				genBuildingView.selectedNode = this.selectedNode
				genBuildingView.initPanel();
				var win = genBuildingView.getWin();
				win.show();
			},
			batchUnit : function() {
				if (!this.selectedNode) {
					return;
				}
				regionCode = this.selectedNode.attributes["key"]
				if (regionCode.length >= 24) {
					Ext.Msg.alert("提示", "已到达网格地址最大层数,无法新建！");
					return;
				}
				var using = this.checkRegionUsing(regionCode);
				if (using) {
					Ext.Msg.alert("提示", "该节点已经被使用，不能新建子节点！");
					return;
				}
				var genUnitView = this.midiModules["genUnitView"];
				if (!genUnitView) {
					genUnitView = new chis.application.ag.script.GenUnitForm({})
					genUnitView.on("save", this.onSave, this);
					this.midiModules["genUnitView"] = genUnitView;
				}
				genUnitView.initPanel();
				genUnitView.setParentNode(this.selectedNode);
				var win = genUnitView.getWin();
				win.show();
			},
			batchDelete : function() {
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除该节点将同时删除子节点下所有数据，确认删除?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var selectedNode = this.selectedNode;
							var regionCode = this.selectedNode.attributes["key"];
							if (regionCode == null || regionCode.length == 0) {
								Ext.Msg.alert("提示", "网格地址编号为空！")
								return;
							}
							var parentCode = selectedNode.attributes["parentCode"];
							var parentNode = selectedNode.parentNode;
							var parentIsFamily = parentNode.attributes["isFamily"];
							this.mask("正在执行删除...")
							util.rmi.jsonRequest({
										serviceId : "chis.agService",
										serviceAction : "batchDelete",
										method:"execute",
										schema : this.entryName,
										body : {
											"regionCode" : regionCode,
											"parentCode" : parentCode,
											"parentIsFamily" : parentIsFamily
										}
									}, function(code, msg, json) {
										this.unmask();
										if (code > 300) {
											this.processReturnMsg(code, msg);
											return;
										}
										if (parentCode) {
											selectedNode.remove();
											if (!parentNode.hasChildNodes()) {
												parentNode.leaf = true
											}
											this.tree.selectPath(parentNode
													.getPath());
											this.onCatalogChanage(parentNode);
											this.selectedNode = parentNode;
										}
										if (json.body) {
											if (json.body.parentIsBottom == "true") {
												parentNode.leaf = true;
											}
										}

									}, this)// jsonRequest
						}
					},
					scope : this
				});
			},
			findNodeByRegionCode : function(parentNode, regionCode) {
				var childNodes = parentNode.childNodes
				for (var i = 0; i < childNodes.length; i++) {
					var child = childNodes[i];
					if (regionCode == child.attributes["key"])
						return child
				}
			},
			onRowSelect : function() {
//				if (!this.selectedNode) {
//					return;
//				}
				var row = this.getSelectedRecord();
				if (!row)
					return;
				var isFamily = row.get("isFamily");
				if (isFamily > 'e' ||isFamily=="1") {// 街道
					this.grid.getTopToolbar().items.item(5).enable();
				} else {
					this.grid.getTopToolbar().items.item(5).disable();
				}
			},
			doCndQuery : function(button, e, addNavCnd) {
				chis.application.ag.script.AreaGridMaintenanceList.superclass.doCndQuery.call(this,button,e,addNavCnd)
				this.selectedNode = null;
			},
			onCatalogChanage : function(node, e) {
				var regionCode = node.attributes["key"]
				var len = regionCode.length;
				var navField = this.navField
				var parentField = "parentCode";
				var initCnd = this.initCnd
				var queryCnd = this.queryCnd
				var cnd;
				if (!node.hasChildNodes() && node.leaf) {
					this.noChild = true;
					cnd = ['eq', ['$', navField], ['s', regionCode]]
				} else {
					this.noChild = false;
					cnd = ['eq', ['$', parentField], ['s', regionCode]]
				}
				// this.navCnd = cnd
				if (initCnd || queryCnd) {
					cnd = ['and', cnd]
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},
			changeTopMenuBtn : function(changeAll, menuNumArr, menuEnable) {
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				if (changeAll) {
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						if (menuEnable) {
							btn.enable()
						} else {
							btn.disable();
						}
					}
				} else {
					if (!menuNumArr)
						return;
					for (var i = 0; i < menuNumArr.length; i++) {
						var menuNum = menuNumArr[i];
						if (n <= menuNum) {
							continue;
						}
						var btn = btns.item(menuNum);
						if (menuEnable) {
							btn.enable()
						} else {
							btn.disable();
						}
					}
				}
			},
			onDblClick : function(grid, index, e) {
				var btns = this.grid.getTopToolbar().items;
				if (btns.item(5).disabled) {
					return;
				}
				chis.application.ag.script.AreaGridMaintenanceList.superclass.onDblClick
						.call(this, grid, index, e)
			},
			getTpf : function(regionCode, family) {
				var len = regionCode.length
				var i = 0;
				for (; i < this.codeRule.length; i++) {
					if (this.codeRule[i] == len)
						break;
				}

				var length = this.codeRule[i + 1] - this.codeRule[i];
				var tpf = "";
				for (var j = 0; j < length; j++) {
					tpf += "_";
				}
				if (family) {
					tpf += "_";
				}
				return tpf;
			},

			doAction : function(item, e) {
				var cmd = item.cmd
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				var action = this["do" + cmd]
				if (action) {
					action.apply(this, [item, e])
				}
			},
			doCreate : function() {
				if (!this.selectedNode) {
					return;
				}
				regionCode = this.selectedNode.attributes["key"]
				if (regionCode.length >= 24) {
					Ext.Msg.alert("提示", "已到达网格地址最大层数,无法新建！");
					return;
				}
				var isBottom = this.selectedNode.attributes["isBottom"];
				if (isBottom == "y") {
					var using = this.checkRegionUsing(regionCode);
					if (using) {
						Ext.Msg.alert("提示", "该节点已经被使用，不能新建子节点！");
						return;
					}
				}
				var createView = this.midiModules["create"]
				if (!createView) {
					createView = new chis.application.ag.script.AreaGridMaintenanceForm({
								entryName : this.entryName,
								title : "新增网格地址",
								autoLoadSchema : false
							})
					this.midiModules["create"] = createView
					createView.op = "create"
					createView.on("save", this.onSave, this)
				}
				createView.initPanel();
				var win = createView.getWin();
				win.show();
				createView.setNodeInfo(this.selectedNode, "create")

			},
			checkRegionUsing : function(regionCode) {
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.agService",
							serviceAction : "checkAreaGridUsing",
							method:"execute",
							body : {
								"regionCode" : regionCode
							}
						})
				if (res.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				} else {
					return res.json.body.result;
				}
			},
			doUpdate : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				if (this.noChild){
					this.openUpdate(r.id, this.selectedNode.parentNode,
							this.selectedNode.childNodes)
				}else {
					var selectedNode = this.selectedNode;
					if (!this.selectedNode) {
						var theNodePath = this.getParentPath(r)
						
						var thisObj = this ;
						this.tree.expandPath(theNodePath, null, function(s, n) {
									selectedNode = n
									var checkNode = thisObj.findNodeByRegionCode(
											selectedNode, r.id)
									thisObj.openUpdate(r.id, selectedNode,
											checkNode.childNodes)
								},this);
						this.tree.selectPath(theNodePath);
					} else {
						var checkNode = this.findNodeByRegionCode(selectedNode,
								r.id)
						this.openUpdate(r.id, selectedNode,
								checkNode.childNodes);
					}
				}
			},
			openUpdate : function(regionCode, parentNode, selectedNodeChilds) {
				this.parentPath = parentNode.getPath();
				var updateView = this.midiModules["update"]
				if (!updateView) {
					updateView = new chis.application.ag.script.AreaGridMaintenanceForm({
								entryName : this.entryName,
								title : "修改网格地址",
								autoLoadSchema : false
							})
					updateView.op = "update"
					updateView.initPanel();
					this.midiModules["update"] = updateView
					updateView.on("save", this.onSave, this)
				}
				if (!updateView.addCheckBox) {
					var check = new Ext.form.Checkbox({
								fieldLabel : "更新下级地址",
								id : "_checkOverwrite",
								hideLabel : false
							})
					updateView.form.add({
						layout : 'form',
						items : check,
						layoutConfig : {
							elementStyle : "MARGIN-RIGHT: auto; MARGIN-LEFT: auto;"
						}
					})
					updateView.addCheckBox = true;
				} else {

					var check = updateView.form.findById("_checkOverwrite");
					check.reset();
				}
				
				// 判断节点下是否有城市节点
				var hasCityChildNode = false;
				if (selectedNodeChilds) {
					var cityReg = /^[e-i]1$/ // 城市
					for (var i = 0; i < selectedNodeChilds.length; i++) {
						var childIsFamily = selectedNodeChilds[i].attributes["isFamily"];
						if (cityReg.test(childIsFamily)) {
							hasCityChildNode = true;
							break;
						}
						continue;
					}
				}
				updateView.selectedNodeChilds = selectedNodeChilds;
				updateView.hasCityChildNode = hasCityChildNode;
				var win = updateView.getWin();
				win.show();
				updateView.initDataId = regionCode
				updateView.loadData();
				updateView.op = "update"
				updateView.setNodeInfo(parentNode, "update")
				// updateView.selectedNodeIsFamily =
				// this.selectedNode.attributes["isFamily"];
			},
			doCancel : function() {
				this.getWin().hide();
			},
			expandAllParentNode : function(node) {
				node.expand();
				if (node.getDepth() != 0) {
					expandAllParentNode(node.parentNode);
				}
			},
			onSave : function(body, op) {
				if (!this.selectedNode) {
					return;
				}
				if (!body){
					this.selectedNode =  this.tree.getSelectionModel().getSelectedNode();
					this.onCatalogChanage(this.selectedNode);
					this.selectedNode.expand();
//					this.selectedNode.parentNode.reload(
//						function(_node){
////							this.selectedNode = this.tree.getNodeById(this.selectedNode.attributes["key"]);
////							this.tree.expandPath(this.selectedNode.getPath());
////							this.tree.selectPath(this.selectedNode.getPath());
//							this.selectedNode =  this.tree.getSelectionModel().getSelectedNode();
//							this.onCatalogChanage(this.selectedNode);
//							alert(456456456)
//						},this)
					return;
				}
				var regionCode = body["regionCode"];
				var parentCode = body["parentCode"]
				var oldCode = body["oldCode"];
				var isBottom = body["isBottom"];
				if (!regionCode || regionCode.length == 0) {
					return;
				}
				
				var nodeToBeReload = this.selectedNode.parentNode ;
				if(!nodeToBeReload||(this.selectedNode.leaf == false && !oldCode)) {
					nodeToBeReload = this.selectedNode ;
				}
				nodeToBeReload.reload(function(node){
					this.selectedNode = this.tree.getNodeById(this.selectedNode.attributes["key"]);
					if(!this.selectedNode){//网格地址编号改变的情况下 找不到原来节点。
						this.selectedNode = this.tree.getNodeById(regionCode);
					}
					
					var selectedPath = this.selectedNode.getPath();
					this.tree.expandPath(selectedPath);
					this.tree.selectPath(selectedPath);
					if (this.selectedNode) {
						this.onCatalogChanage(this.selectedNode);
					}
				},this);
				if (op == "create") {
					this.selectedNode.leaf = false;
				}
//				this.selectedNode.reload();	
//				this.selectedNode.reload();
				
				//this.refresh();
				this.getWin().hide();
			},

			doFileUpload : function() {
				if (!this.schema) {
					return
				}
				var fileUpWin = this.midiModules["fileUpWin"]
				if (!fileUpWin) {
					$import("chis.application.ag.script.AreaGridFileUpload")
					fileUpWin = new chis.application.ag.script.AreaGridFileUpload('xls');
					this.midiModules["fileUpWin"] = fileUpWin;
					fileUpWin.on("upFieldEnd", this.treeReload, this);
				}
				fileUpWin.show()
			},
			
			treeReload : function(){
				this.tree.root.reload();
			},

			doBatchupdate : function() {
				if (!this.schema) {
					return
				}
				var qWin = this.midiModules["qWin"]
				var cfg = {
					list : this
				}
				if (!qWin) {
					$import("chis.application.ag.script.AreaGridBatchModel")
					qWin = new chis.application.ag.script.AreaGridBatchModel(cfg)
					this.midiModules["qWin"] = qWin
				} else {
					Ext.apply(qWin, cfg)
					qWin.tabpanel.setActiveTab(0)
					qWin.form.getForm().reset()
					qWin.form.setDisabled(true)
				}
				qWin.getWin().show()
			},
			getParentPath : function(r) {
				var regionCode = r.get("regionCode");
				var isFamily = r.get("isFamily");
				var rootPath = this.tree.root.getPath();
				var parentPath = rootPath;
				var len = regionCode.length;
				for (var i = 6; i < len; i = i + 3) {
					var temp = regionCode.substring(0, i)
					parentPath = parentPath + "/" + temp;
				}
				if(isFamily == "1"){
					parentPath = parentPath.substring(0,parentPath.lastIndexOf("/"))
				}
				return parentPath
			}
		})