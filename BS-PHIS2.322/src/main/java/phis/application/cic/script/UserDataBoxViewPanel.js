$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.UserDataBoxViewPanel = function(cfg) {
	phis.application.cic.script.UserDataBoxViewPanel.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.UserDataBoxViewPanel, phis.script.SimpleModule, {
			initPanel : function() {
				var item1 = new Ext.Panel({
							title : '住院病历',
							id : "case",
							iconCls : 'option',
							layout : 'fit',
							collapsible : true,
							titleCollapse : true,
							items : this.getCaseTreePanel()
						});
				var item2 = new Ext.Panel({
							title : '其他',
							id : "other",
							iconCls : 'option',
							layout : 'fit',
							collapsible : true,
							titleCollapse : true,
							items : this.getOtherTreePanel()
						});
				var viewPanel = new Ext.Panel({
							region : 'west',
							margins : '5 0 5 5',
//							split : true,
							width : 210,
							minSize : 200,
							maxSize : 230,
//							border : true,
							layoutConfig : {
								animate : true
							},
							layout : 'anchor',
							items : [item1, item2]
						});
				this.viewPanel = viewPanel;
				return viewPanel;
			},

			getOtherTreePanel : function() {
				var root = new Ext.tree.TreeNode({
							id : 'view2',
							text : '根目录'
						});
				var node1 = new Ext.tree.TreeNode({
							id : 'temporary',
							text : '特殊符号',
							iconCls : 'option',
							indexId : 21
						});
				var node2 = new Ext.tree.TreeNode({
							id : 'longTime',
							text : '常用字典',
							iconCls : 'option',
							indexId : 22
						});
				var node3 = new Ext.tree.TreeNode({
							id : 'jytestresult',
							text : '检验信息',
							iconCls : 'option',
							indexId : 23
						});
				root.appendChild(node1);
				root.appendChild(node2);
				root.appendChild(node3);
				var tree = new Ext.tree.TreePanel({
							id : 'tree2',
							animate : true,
							padding : '8 0 12 0',
							autoScroll : true,
							containerScroll : true,
							lines : false,
							rootVisible : false
						});
				tree.setRootNode(root);
				tree.on("click", this.onTreeClick, this);
				this.tree2 = tree;
				return tree;
			},
			getCaseTreePanel : function() {
				var root = new Ext.tree.TreeNode({
							id : 'view1',
							text : '根目录'
						});
				var node1 = new Ext.tree.TreeNode({
							id : 'history',
							text : '患者病史',
							iconCls : 'option',
							indexId : 11
						});
				var node2 = new Ext.tree.TreeNode({
							id : 'temporary',
							text : '临时医嘱',
							iconCls : 'option',
							indexId : 12
						});
				var node3 = new Ext.tree.TreeNode({
							id : 'longTime',
							text : '长期医嘱',
							iconCls : 'option',
							indexId : 13
						});
				var node4 = new Ext.tree.TreeNode({
							id : 'modify',
							text : '变更医嘱',
							iconCls : 'option',
							indexId : 14
						});
				
				root.appendChild(node1);
				root.appendChild(node2);
				root.appendChild(node3);
				root.appendChild(node4);
				var tree = new Ext.tree.TreePanel({
							id : 'tree1',
							animate : true,
							padding : '8 0 12 0',
							autoScroll : true,
							containerScroll : true,
							lines : false,
							rootVisible : false
						});
				tree.setRootNode(root);
				tree.on("click", this.onTreeClick, this);
				tree.on("afterrender", this.onTreeRender, this);
				this.tree1 = tree;
				return tree;
			},
			onTreeRender:function(tree){
				tree.selectPath("/view1/history");
			},
			onTreeClick : function(node, e) {
				var tree = node.getOwnerTree();
				var root = tree.getRootNode();
				if (root.id == "view1") {
					this.tree2.selectPath("/view2");
				}
				if (root.id == "view2") {
					this.tree1.selectPath("/view1");
				}
				var indexId = node.attributes.indexId;
				if (indexId == null) {
					return;
				}
				this.fireEvent("viewClick",indexId);
			}
		});