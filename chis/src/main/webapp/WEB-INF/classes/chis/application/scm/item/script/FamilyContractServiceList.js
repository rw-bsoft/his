/**
 *签约项目维护列表
 *
 * @author : chenhb
 */
$package("chis.application.scm.item.script")
$import("chis.script.BizTreeNavListForGP")

chis.application.scm.item.script.FamilyContractServiceList = function (cfg) {
    cfg.westWidth = 300;
    this.autoLoadData = true;
    cfg.rootVisible = true;
//	this.initCnd = ['like', ['$', 'regionCode'],
//						['s', cfg.navParentKey + '%']]
    chis.application.scm.item.script.FamilyContractServiceList.superclass.constructor.apply(this,
        [cfg])
    this.createCls = "chis.application.scm.item.script.UpdateContractServiceForm";
    this.updateCls = "chis.application.scm.item.script.UpdateContractServiceForm";
    // this.on('firstRowSelected', this.onRowSelect)
    // this.on('rowclick', this.onRowSelect)
    this.on("loadData", this.onListLoadData, this);

}
Ext.extend(chis.application.scm.item.script.FamilyContractServiceList,
    chis.script.BizTreeNavListForGP, {
        onListLoadData: function () {

            if (this.selectedNode) {
                var snKey = this.selectedNode.attributes["key"];
                this.selectedIndex = this.store.find("itemCode", snKey);
                this.selectRow(this.selectedIndex);
                this.changeCls(this.selectedNode)
            }
        },
        changeCls: function (nodeInfo) {
            var itemNature = nodeInfo.attributes["itemNature"];
            var isBottom = nodeInfo.attributes["isBottom"];
            debugger
            if (2 == itemNature && !nodeInfo.leaf) {
                this.createMod = "chis.application.scm.SCM/SCC/SCC03_01";
                this.updateMod = "chis.application.scm.SCM/SCC/SCC03_01";
                this.createCls = "chis.application.scm.item.script.HisServicePackageModule";
                this.updateCls = "chis.application.scm.item.script.HisServicePackageModule";
                this.midModName = "PayServiceItem";
                // this.midiModules["UpdateContractServiceForm"] = null;
                // this.midiModules["createContractServiceForm"] = null;
            } else if (2 == itemNature && 'y' == isBottom) {
                this.createMod = "chis.application.scm.SCM/SCC/SCC03_0101";
                this.updateMod = "chis.application.scm.SCM/SCC/SCC03_0101";
                this.createCls = "chis.application.scm.item.script.HisServicePackageForm";
                this.updateCls = "chis.application.scm.item.script.HisServicePackageForm";
                this.midModName = "PayServiceItemBottom";
            } else {
                this.createMod = "chis.application.scm.SCM/SCC/SCC03_02";
                this.updateMod = "chis.application.scm.SCM/SCC/SCC03_02";
                this.createCls = "chis.application.scm.item.script.UpdateContractServiceForm";
                this.updateCls = "chis.application.scm.item.script.UpdateContractServiceForm";
                this.midModName = "BaseServiceItem";
                // this.midiModules["UpdateContractServiceForm"] = null;
                // this.midiModules["createContractServiceForm"] = null;
            }
        },
        onReady: function () {
            this.on("remove", function (entryName, action, json, data) {
                var id = data["itemCode"];
                var node = this.findNodeByItemCode(this.selectedNode, id);
                if (node) {
                    node.remove();
                }

            }, this)
            chis.application.scm.item.script.FamilyContractServiceList.superclass.onReady
                .call(this)
//		if (!this.tree){
//			return;
//		}
            var root = this.tree.getRootNode()
            if (root && !root.attributes["key"]) {
                MyMessageTip.msg("提示", "需要基础数据，才能维护签约服务项！", true);
                return;
            }
            var rightCllick = this.midiMenus["itemCodeMenu"]
            if (!rightCllick) {
                var common = {
                    id: 'rightClick',
                    items: [{
                        text: '新建',
                        iconCls: "create",
                        // 增加菜单点击事件
                        handler: this.doCreateSI,
                        scope: this
                    }, {
                        handler: function () {
                            this
                                .openUpdate(
                                    this.selectedNode.attributes["key"],
                                    this.selectedNode.parentNode,
                                    this.selectedNode.childNodes);
                        },
                        scope: this,
                        iconCls: "update",
                        text: '查看'
                    },
                        {
                            iconCls: "remove",
                            handler: this.serviceDelete,
                            text: '删除',
                            scope: this
                        }]
                }

                // 定义右键菜单
                rightClick = new Ext.menu.Menu(common);
                this.midiMenus["itemCodeMenu"] = rightClick;
            }
            this.tree.on('contextmenu', function (node, event) {
                event.preventDefault();
                // 如果选中的是顶层节点,根据编码取查询父节点并拼接到这个节点上去.
                if (!node.parent
                    && node.attributes["key"] == this.navParentKey) {
                    this.setParentForTopNode(node);
                }
                this.tree.selectPath(node.getPath())
                node.expand();
                this.selectedNode = node;
                this.onCatalogChanage(node, event);
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

            this.tree.on("click", function (node, event) {
                this.selectedNode = node;
                if (!node.parent && node.attributes["key"] == this.navParentKey) {
                    // 如果选中的是顶层节点,根据编码取查询父节点并拼接到这个节点上去.
                    // this.onCatalogChanage(node, event);
                    this.setParentForTopNode(node);
                }
                node.expand();
                var parentKey = node.attributes["parentCode"];
                var cnd = ['eq', ['$', 'parentCode'], ['s', parentKey || '']];
                this.requestData.cnd = cnd;
                this.refresh();
            }, this)
            this.grid.on("rowdblclick", this.doUpdate, this);
        },
        // 如果当前节点是顶层节点,为之设置父节点,从后台查询得来.
        setParentForTopNode: function (node) {
            if (!node)
                return;
            var itemCode = node.attributes["key"];
            if (!itemCode || itemCode.length <= 2) {
                return;
            }
            var parentCode = regionCode.substring(0, regionCode.length - 3);

            var result = util.rmi.miniJsonRequestSync({
                serviceId: "chis.contractService",
                serviceAction: "getNodeInfo",
                method: "execute",
                itemCode: parentCode
            });
            if (result.code > 300) {
                this.processReturnMsg(result.code, result.msg);
                return
            }
            var nodeInfo = result.json.nodeInfo;
            if (!nodeInfo)
                return;
            var parentNode = new Ext.tree.TreeNode({
                id: nodeInfo["itemCode"]
            })
            parentNode.attributes["key"] = nodeInfo["itemCode"];
            parentNode.attributes["text"] = nodeInfo["itemName"];
            parentNode.ownerTree = this.tree;
            parentNode.appendChild(node);
            node.parent = parentNode;
        },
        onCatalogChanage: function (node, e) {
            if (!node.hasChildNodes() && node.leaf) {
                this.noChild = true;
            } else {
                this.noChild = false;
            }
        },
        doUpdate: function () {
            var r = this.getSelectedRecord();
            if (!r)
                return;
            if (this.noChild) {
                this.openUpdate(r.id, this.selectedNode.parentNode,
                    this.selectedNode.childNodes)
            } else {
                var selectedNode = this.selectedNode;
                if (!this.selectedNode) {
                    var theNodePath = this.getParentPath(r)

                    var thisObj = this;
                    this.tree.expandPath(theNodePath, null, function (s, n) {
                        selectedNode = n
                        var checkNode = thisObj.findNodeByItemCode(
                            selectedNode, r.id)
                        thisObj.openUpdate(r.id, selectedNode,
                            checkNode.childNodes)
                    }, this);
                    this.tree.selectPath(theNodePath);
                } else {
                    var checkNode = this.findNodeByItemCode(selectedNode,
                        r.id)
                    this.openUpdate(r.id, selectedNode,
                        checkNode.childNodes);
                }
            }
        },
        doModify: function () {
            var updateViewId = "update" + this.midModName;
            var updateView = this.midiModules[updateViewId]
            var r = this.grid.getSelectionModel().getSelected();
            if (!r) {
                return;
            }
            if (!updateView) {
                updateView = this.createModule(updateViewId, this.updateMod);
                updateView.op = "update"
                updateView.initPanel();
                this.midiModules[updateViewId] = updateView;
                updateView.on("save", this.onSave, this);
                updateView.on("itemSave", this.onSave, this);
            }
            updateView.loading = false;
            updateView.initDataId = r.itemCode;
            var win = updateView.getWin();
            win.add(updateView.initPanel());
            win.show();
            updateView.loadData();
            updateView.op = "update"
            updateView.setNodeInfo(parentNode, "update")

            // var itemCode = r.get("itemCode");
            // var parentCode = r.get("parentCode");
            // var isBottom = r.get("isBottom");
            // updateView.loading = false;
            // updateView.initDataId = itemCode;
            // var win = updateView.getWin();
            // win.show();
            // updateView.loadData();
            // updateView.op = "update"
            // updateView.parentCode = parentCode;
            // updateView.parentBottom = isBottom;
            // updateView.form.getForm().findField("parentCode")
            //     .setValue(parentCode);
            // updateView.form.getForm().findField("itemCode")
            //     .disable();
        },
        findNodeByItemCode: function (parentNode, itemCode) {
            var childNodes = parentNode.childNodes
            for (var i = 0; i < childNodes.length; i++) {
                var child = childNodes[i];
                if (itemCode == child.attributes["key"])
                    return child
            }
        },
        openUpdate: function (itemCode, parentNode, selectedNodeChilds) {
            var updateViewId = this.midModName;
            var updateView = this.midiModules[updateViewId]
            if (!updateView) {
                updateView = this.createModule(updateViewId, this.updateMod);
                updateView.op = "update"
                updateView.initPanel();
                this.midiModules[updateViewId] = updateView;
                updateView.on("save", this.onSave, this);
                updateView.on("itemSave", this.onSave, this);
            }
            updateView.loading = false;
            updateView.initDataId = itemCode;
            var win = updateView.getWin();
            win.add(updateView.initPanel());
            win.show();
            updateView.loadData();
            updateView.op = "update"
            updateView.setNodeInfo(parentNode, "update")
            // updateView.selectedNodeIsFamily =
        },
        doCreateSI: function () {

            if (!this.selectedNode) {
                MyMessageTip.msg("提示", "请选择健康管理服务项目！", true);
                return;
            }
            itemCode = this.selectedNode.attributes["key"]
            if (itemCode.length >= 9) {
                MyMessageTip.msg("提示", "已到达签约服务项维护最大层数,无法新建！", true);
                return;
            }
            var isBottom = this.selectedNode.attributes["isBottom"];
            if (isBottom && isBottom === "y") {
                return MyMessageTip.msg("提示", "已到达签约服务项维护子节点,无法新建！", true);

            }
            var createViewId = this.midModName;
            var createView = this.midiModules[createViewId]
            if (!createView) {
                createView = this.createModule(createViewId, this.createMod);
                this.midiModules[createViewId] = createView
                createView.parentCode = itemCode;
                createView.op = "create"
                createView.on("save", this.onSave, this)
                createView.on("itemSave", this.onSave, this)
            }
            var win = createView.getWin();
            win.add(createView.initPanel());
            win.show();
            createView.doNew();
            createView.setNodeInfo(this.selectedNode, "create")
        },
        onSave: function (body, op) {
            debugger
            if (!this.selectedNode) {
                return;
            }
            if (!body) {
                this.selectedNode = this.tree.getSelectionModel().getSelectedNode();
                this.onCatalogChanage(this.selectedNode);
                this.selectedNode.expand();
                return;
            }
            var itemCode = body["itemCode"];
            var parentCode = body["parentCode"]
            var oldCode = body["oldCode"];
            var isBottom = body["isBottom"];
            if (!itemCode || itemCode.length == 0) {
                return;
            }

            var nodeToBeReload = this.selectedNode.parentNode;
            if (!nodeToBeReload || (this.selectedNode.leaf == false && !oldCode)) {
                nodeToBeReload = this.selectedNode;
            }
            nodeToBeReload.reload(function (node) {
                this.selectedNode = this.tree.getNodeById(this.selectedNode.attributes["key"]);
                if (!this.selectedNode) {//项目编号改变的情况下 找不到原来节点。
                    this.selectedNode = this.tree.getNodeById(itemCode);
                }

                var selectedPath = this.selectedNode.getPath();
                this.tree.expandPath(selectedPath);
                this.tree.selectPath(selectedPath);
                if (this.selectedNode) {
                    this.onCatalogChanage(this.selectedNode);
                }
            }, this);
            if (op == "create") {
                this.selectedNode.leaf = false;
            }
//		this.selectedNode.reload();
//		this.selectedNode.reload();
            this.refresh();
            this.getWin().hide();
        },
        serviceDelete: function () {
            Ext.Msg.show({
                title: '删除确认',
                msg: '删除该节点将同时删除子节点下所有数据，确认删除?',
                modal: false,
                width: 300,
                buttons: Ext.MessageBox.OKCANCEL,
                multiline: false,
                fn: function (btn, text) {
                    if (btn == "ok") {
                        var selectedNode = this.selectedNode;
                        var itemCode = this.selectedNode.attributes["key"];
                        if (itemCode == null || itemCode.length == 0) {
                            MyMessageTip.msg("提示", "服务编号为空！", true)
                            return;
                        }
                        var parentCode = selectedNode.attributes["parentCode"];
                        var parentNode = selectedNode.parentNode;
                        this.mask("正在执行删除...")
                        util.rmi.jsonRequest({
                            serviceId: "chis.contractService",
                            serviceAction: "serviceItemDelete",
                            method: "execute",
                            schema: this.entryName,
                            body: {
                                "itemCode": itemCode
                            }
                        }, function (code, msg, json) {
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
                            this.refresh();
                        }, this)// jsonRequest
                    }
                },
                scope: this
            });
        }
    })
