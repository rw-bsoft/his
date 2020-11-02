$package("phis.application.fsb.script");

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"phis.script.widgets.TreeLoader");

phis.application.fsb.script.FsbNursePlanTree = function(cfg) {
	this.checkModel = 'cascade';// 'multiple':多选; 'single':单选; 'cascade':级联多选
	this.checkTree = false;// 是否构建可以选择的树
	phis.application.fsb.script.FsbNursePlanTree.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbNursePlanTree,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				this.zyh = sc.zyh;
				var rootNode = new Ext.tree.AsyncTreeNode({
							id : "root",
							text : "护理计划"
						});
				this.requestData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : "FsbNursePlan"
				}
				var _ctx = this;
				var url = ClassLoader.serverAppUrl || "";
				this.treeLoader = new phis.script.widgets.TreeLoader({
							dataUrl : url + '*.jsonRequest',
							requestMethod : "POST",
							jsonData : this.requestData
						});
				this.treeLoader.on("beforeload", function(treeLoader, node) {
							this.treeLoader.baseParams.zyh = this.zyh;
							return true;
						}, this);
				this.tree = new Ext.tree.TreePanel({
							root : rootNode,
							onlyLeafCheckable : false,
							rootVisible : true,// 根节点显示
							autoScroll : true,
							animate : false,// 是否动画
							enableDD : false,// 是否支持拖放
							containerScroll : true,
							lines : true,
							checkModel : this.checkModel,
							loader : this.treeLoader
						});

				// 根据当前节点判断节点下数据获取方式
				// tree.on('beforeload', this.beforeLoad, this);
				this.tree.on("click", this.onNodeClick, this);
				this.tree.on("load", this.onTreeLoad, this);
				rootNode.on("expand", function(node) {
							if (node.hasChildNodes()) {
								node.firstChild.select();
								// this.fireEvent("nodeSelect",
								// node.firstChild);
							}
						}, this);
				rootNode.expand();
				return this.tree;
			},
			/**
			 * 重写该方法以实现从多张表获取数据
			 * 
			 * @param {}
			 *            node
			 */
			beforeLoad : function(node) {
				// alert("beforeLoad");
			},
			onTreeLoad : function(node) {
				// alert("onTreeLoad");
			},
			/**
			 * 重写改方法实现点击事件
			 * 
			 * @param {}
			 *            node
			 * @param {}
			 *            e
			 */
			onNodeClick : function(node) {
				// alert(node.text)
				// attributes中自定义的属性需要用如下方式获取。不知道为什么，需要在第二个attributes中才能取到
				// var node = this.tree.getSelectionModel().getSelectedNode();
				if (!node)
					return;
				if (node.leaf) {
					this.fireEvent("nodeClick", node, this);
				}
			},
			refreshTree : function(data) {
				this.zyh = data.zyh;
				// this.treeLoader.dataUrl = "NurseRecord.search?zyh=" +
				// this.zyh;
				this.tree.getRootNode().reload();
			}
		});