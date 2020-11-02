$package("phis.application.war.script")
$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"phis.script.widgets.TreeLoader")
phis.application.war.script.EMRmodeBllbNotReviewTree = function(cfg) {
	this.checkModel = 'cascade';// 'multiple':多选; 'single':单选; 'cascade':级联多选
	this.checkTree = false;// 是否构建可以选择的树
	phis.application.war.script.EMRmodeBllbNotReviewTree.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.EMRmodeBllbNotReviewTree,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var rootNode = new Ext.tree.AsyncTreeNode({
							id : "root",
							text : "所有模版",
							attributes : {
								isRoot : true
							}
						});
				this.requestData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : "TreeLoad"
				}
				var url = ClassLoader.serverAppUrl || "";
				var tree = new Ext.tree.TreePanel({
							root : rootNode,
							border : true,
							onlyLeafCheckable : false,
							rootVisible : this.rootVisible != 'false',
							autoScroll : true,
							animate : false,// 是否动画
							enableDD : true,// 是否支持拖放
							containerScroll : true,
							lines : true,
							checkModel : this.checkModel,
							loader : new phis.script.widgets.TreeLoader({
										dataUrl : url + '*.jsonRequest',
										requestMethod : "POST",
										jsonData : this.requestData
//										,
//										baseAttrs : {
//											uiProvider : uiProvider
//										}
									})
						});
				tree.on('beforeload', this.beforeLoad, this);
				this.tree = tree
				tree.on("click", this.onNodeClick, this);
				rootNode.on("expand", function(node) {
							if (node.hasChildNodes()) {
								node.firstChild.select();
								this.fireEvent("treeClick", node.firstChild);
							} else {
								node.select();
								this.fireEvent("treeClick", node);
							}
						}, this);
				rootNode.expand();
				this.rootNode = rootNode;
				return tree
			},
			/**
			 * 重写该方法以实现从多张表获取数据
			 * 
			 * @param {}
			 *            node
			 */
			beforeLoad : function(node) {
				if (node.id != "root") {
					this.tree.loader.baseParams.queryFields = "EMRTYPECODE,EMRTYPENAME,FRAMEWORKCODE";
					this.tree.loader.baseParams.entryName = 'EMRTYPE';
					var cnd = "['and',['eq',['$','FRAMEWORKCODE'],['s','"
							+ node.id + "']],['ne',['$','EMRTYPECODE'],['s','"
							+ node.id + "']]]";
					this.tree.loader.baseParams.condition = cnd;
					this.tree.loader.baseParams.orderBy = 'EMRTYPECODE';
				} else {
					this.tree.loader.baseParams.queryFields = "FRAMEWORKCODE,FRAMEWORKNAME";
					this.tree.loader.baseParams.entryName = 'EMRFRAMEWORK';
					var cnd = "['eq',['$','FRAMEWORKCODE'],['s','" + this.BLLB
							+ "']]";
					this.tree.loader.baseParams.condition = cnd;
					this.tree.loader.baseParams.orderBy = 'FRAMEWORKCODE';
				}
			},
			/**
			 * 重写改方法实现点击事件
			 * 
			 * @param {}
			 *            node
			 * @param {}
			 *            e
			 */
			onNodeClick : function(node, e) {
				this.fireEvent("treeClick", node);
			}
		})